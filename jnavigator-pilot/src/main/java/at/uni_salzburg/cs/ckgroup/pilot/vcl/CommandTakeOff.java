/*
 * @(#) CommandTakeOff.java
 *
 * This code is part of the JNavigator project.
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
import java.util.Locale;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;


public class CommandTakeOff implements ICommand {
	
	private static final Logger LOG = Logger.getLogger(CommandTakeOff.class);
	
	public static final double MAXIMUM_TAKEOFF_VELOCITY = 0.5;
	
	public static final long CYCLE_TIME = 200;

	private double takeOffAltitude;
	
	private double altitude = 0;
	
	private long time;
	
	private boolean running = false;
	
	/**
	 * @param altitude required altitude in meters.
	 * @param time the time to take-off in milliseconds.
	 */
	public CommandTakeOff (double takeOffAltitude, long time) {
		this.takeOffAltitude = Math.abs(takeOffAltitude);
		this.time = Math.abs(time * 1000);
		long minimumTime = (long)(1000 * this.takeOffAltitude / MAXIMUM_TAKEOFF_VELOCITY);
		if (minimumTime < 2) {
			minimumTime = 2;
		}
		this.time = this.time < minimumTime ? minimumTime : this.time;
	}

	public void execute(IInterpreter interpreter) throws IOException {
		LOG.info("Starting engines.");
		interpreter.getAutoPilot().startUpEngines();
		
		long start = System.currentTimeMillis();
		long now = start;
		PolarCoordinate where = interpreter.getCurrentPosition();
		LOG.info("Take off started at " + where);
		running = true;
		while (running && now < start + time + CYCLE_TIME) {
			now = System.currentTimeMillis();
			altitude = takeOffAltitude * (now - start) / time;
			where.setAltitude(altitude);
			interpreter.setSetCoursePosition(where);
			try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
//			LOG.info("Take off in progress. Position is " + interpreter.getCurrentPosition() + ", Set course is " + where);
		}
		where.setAltitude(takeOffAltitude);
		interpreter.setSetCoursePosition(where);
		try { Thread.sleep(1000); } catch (InterruptedException e) { }
		running = false;
		LOG.info("Take off completed. Position is " + interpreter.getCurrentPosition() + ", Set course is " + where);
	}

	public void terminate() {
		LOG.info("Forced termination");
		running = false;
	}

	public void waitForTermination() {
		LOG.info("Waiting for termination.");
		while (running) {
			try { Thread.sleep(500); } catch (InterruptedException e) { }
		}
		LOG.info("Termination completed.");
	}
	
	@Override
	public String toString() {
		// takeoff 1m for 5s
		return String.format(Locale.US, "takeoff %.1fm for %ds", takeOffAltitude, time/1000);
	}
	
}
