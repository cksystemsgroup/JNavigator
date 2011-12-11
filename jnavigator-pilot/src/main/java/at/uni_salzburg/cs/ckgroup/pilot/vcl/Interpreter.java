/*
 * @(#) Interpreter.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2011  Clemens Krainer
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA. 
 */
package at.uni_salzburg.cs.ckgroup.pilot.vcl;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.pilot.IAutoPilot;
import at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration;

public class Interpreter extends Thread implements IInterpreter {
	
	Logger LOG = Logger.getLogger(Interpreter.class);
	
	private int programCounter;
	
	private Object lock1 = new Object();
	
	private ICommand[] cmds;

	private ICommand activeCmd = new CommandNoop();
	
	private boolean executing = false;
	
	private boolean idle = true;
	
	private IPositionProvider positionProvider;
	
	private IConfiguration configuration;
	
	boolean clearanceForTakeOffGranted = false;

	private PolarCoordinate setCoursePosition;
	
//	private IGeodeticSystem geodeticSystem;
	
	private IAutoPilot autoPilot;
	
	/**
	 * @param cmds the new sequence of commands.
	 * @param disrupt use true to disrupt the current running command.
	 */
	public void loadCommandSequence(List<ICommand> cmds, boolean disrupt) {
		LOG.info("loading new command sequence.");
		synchronized (lock1) {
			if (disrupt && activeCmd != null)
				activeCmd.terminate();
			if (disrupt)
				LOG.info("Cancelling current command in line " + programCounter);
			if (!disrupt && activeCmd != null)
				activeCmd.waitForTermination();
			this.cmds = cmds.toArray(new ICommand[0]);
			programCounter = 0;
		}
		this.interrupt();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run () {
		idle = false;
		executing = true;
		programCounter = 0;
		int maxErrors = 10;
		
		while (executing) {
			if (positionProvider == null) {
				if (positionProvider == null) LOG.error("No position provider available!");
				try { Thread.sleep(500); } catch (InterruptedException e) { }
				if (maxErrors-- < 0) {
					executing = false;
					LOG.info("Terminating script.");
				}
				continue;
			}
			
			idle = programCounter >= cmds.length;
			if (!idle) {
				synchronized (lock1) {
					if (programCounter < cmds.length)
						activeCmd = cmds[programCounter];
				}
				LOG.info("Executing: pc=" + programCounter + ", cmd=" + activeCmd.toString());
				++programCounter;
				try {
					activeCmd.execute(this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				LOG.info("Current VCL script executed, waiting for new VCL commands.");
				try { Thread.sleep(10000); } catch (InterruptedException e) { LOG.info("Buggerit, I have to work again!"); }
			}
		}
		executing = false;
	}

	/**
	 * @return true if the interpreter executes a VCL script.
	 */
	public boolean isExecuting() {
		return executing;
	}

	/**
	 * @return true if the interpreter waits for new VCL commands to execute.
	 */
	public boolean isIdle() {
		return idle;
	}

	/**
	 * Terminate the execution of the current script.
	 */
	public void terminate() {
		executing = false;
		if (activeCmd != null)
			activeCmd.terminate();
	}
	
	public int getProgramCounter() {
		return programCounter;
	}

	public IPositionProvider getPositionProvider() {
		return positionProvider;
	}

	public void setPositionProvider(IPositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}

	public IConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(IConfiguration configuration) {
		this.configuration = configuration;
	}

	public boolean isClearanceForTakeOffGranted() {
		return clearanceForTakeOffGranted;
	}

	public void setClearanceForTakeOffGranted(boolean clearanceForTakeOffGranted) {
		this.clearanceForTakeOffGranted = clearanceForTakeOffGranted;
	}

	public void switchToManualMode() {
		executing = false;
		this.interrupt();
	}

	public PolarCoordinate getSetCoursePosition() {
		return setCoursePosition;
	}

	public void setSetCoursePosition(PolarCoordinate setCoursePosition) {
		this.setCoursePosition = setCoursePosition;
	}

	public PolarCoordinate getCurrentPosition() {
		PolarCoordinate p = positionProvider.getCurrentPosition();
		return new PolarCoordinate(p.latitude, p.longitude, autoPilot.getAltitudeOverGround());
	}

	public IGeodeticSystem getGeodeticSystem() {
		return positionProvider.getGeodeticSystem();
	}

	public IAutoPilot getAutoPilot() {
		return autoPilot;
	}

	public void setAutoPilot(IAutoPilot autoPilot) {
		this.autoPilot = autoPilot;
	}
	
}
