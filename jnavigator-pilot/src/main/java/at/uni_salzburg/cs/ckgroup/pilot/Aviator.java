/*
 * @(#) Aviator.java
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
package at.uni_salzburg.cs.ckgroup.pilot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.CommandGoManual;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.CommandLand;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.ICommand;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.Interpreter;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.Parser;

/**
 * This class is responsible for navigating the vehicle.
 */
public class Aviator implements IAviator, ISetCourseSupplier {
	
    public final static Logger LOG = LoggerFactory.getLogger(Aviator.class);
	
	/**
	 * The current VCL parser.
	 */
	private Parser parser = new Parser();
	
	/**
	 * The current VCL interpreter.
	 */
	private Interpreter interpreter = new Interpreter();

	/**
	 * The current vehicle builder.
	 */
	private IVehicleBuilder vehicleBuilder;
	
	/**
	 * The current configuration.
	 */
	private IConfiguration config;
	
	public Aviator () {
		LOG.info("Constructor.");
	}
	
	/**
	 * Load a VCL script from an <code>InputStream</code> and parse it.
	 * 
	 * @param inStream the configuration's <code>InputStream</code>
	 * @throws IOException thrown in case of errors.
	 */
	public void loadVclScript (InputStream inStream) throws IOException {
		LOG.info("Loading new VCL script.");
		parser.parse(inStream);
		if (interpreter.isExecuting() && parser.isScriptOk())
			interpreter.loadCommandSequence(parser.getScript(), true);
	}

	/**
	 * Destroy all dependent objects and unload the configuration.
	 */
	public void destroy() {
		interpreter.terminate();
	}
	
	/**
	 * @return whether the currently loaded VCL script has no syntax errors.
	 */
	public boolean isScriptOk() {
		return parser.isScriptOk();
	}
	
	/**
	 * @return the current VCL script as a list of strings.
	 */
	public List<String> getScript() {
		return parser.getSource();
	}
	
	/**
	 * @return the current VCL script as a list of <code>ICommand</code> objects.
	 */
	public List<ICommand> getVclSctipt () {
		return parser.getScript();
	}
	
	/**
	 * @return the current VCL script errors as a list.
	 */
	public List<Boolean> getScriptErrors() {
		return parser.getErrors();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#start()
	 */
	public void start() {
		LOG.error("Starting autonomous flight.");
		interpreter = new Interpreter();
		interpreter.loadCommandSequence(parser.getScript(), false);
		interpreter.setPositionProvider(vehicleBuilder.getPositionProvider());
		interpreter.setAutoPilot(vehicleBuilder.getAutoPilot());
		interpreter.setConfiguration(config);
		interpreter.start();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#stop()
	 */
	public void stop() {
		LOG.error("Stopping autonomous flight.");
		List<ICommand> land = new ArrayList<ICommand>();
		land.add(new CommandLand());
		land.add(new CommandGoManual());
		interpreter.loadCommandSequence(land, true);
		while (interpreter.isExecuting() && !interpreter.isIdle()) {
			LOG.error("Waiting for the vehicle to land.");
			try { Thread.sleep(2000); } catch (InterruptedException e) { }
		}
		vehicleBuilder.getAutoPilot().setAutoPilotFlight(false);
		LOG.error("Autonomous flight stopped.");
		interpreter.terminate();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#isVclExecuted()
	 */
	public boolean isVclExecutionActive() {
		return interpreter.isExecuting();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#getCurrentVclCommandLine()
	 */
	public int getCurrentVclCommandLine() {
		return isVclExecutionActive() ? interpreter.getProgramCounter() : -1;
	}

	public VehicleStatus getSetCoursePosition(long time) {
		PolarCoordinate position = interpreter.getSetCoursePosition();
		VehicleStatus s = new VehicleStatus(position, 0, 0, 0, 0);
		return s;
	}

	public IGeodeticSystem getGeodeticSystem() {
		return interpreter.getGeodeticSystem();
	}

	public VehicleStatus[] getSetCourseData() {
		// TODO Auto-generated method stub
		LOG.error("Aviator.getSetCourseData() not yet implemented.");
		return null;
	}

	public long[] getTimeTable() {
		// TODO Auto-generated method stub
		LOG.error("Aviator.getTimeTable() not yet implemented.");
		return null;
	}

	public void loadSetCourse(InputStream courseData) throws ConfigurationException, IOException {
		// TODO Auto-generated method stub
		LOG.error("Aviator.loadSetCourse() not yet implemented.");
	}

	public void setVehicleBuilder(IVehicleBuilder vehicleBuilder) {
		this.vehicleBuilder = vehicleBuilder;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#getStatusData()
	 */
	public String getStatusData() {
		return interpreter.getStatusData();
	}

	public void setConfig(IConfiguration config) {
		this.config = config;
	}
	
	
}
