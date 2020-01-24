/*
 * @(#) CommandLand.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;


public class CommandLand implements ICommand {
	
    public final static Logger LOG = LoggerFactory.getLogger(CommandLand.class);
	
	public static final double MAXIMUM_LAND_ACCELERATION = 0.3;
	
	public static final double MAXIMUM_LAND_VELOCITY = 1;
	
	public static final long CYCLE_TIME = 200;
	
	private double altitude = 0;
	
	private boolean running = false;

	public void execute(IInterpreter interpreter) {
		LOG.info("Landing vehicle.");
		long start = System.currentTimeMillis();
		long now = start;
		double landAltitude = interpreter.getAutoPilot().getAltitudeOverGround();
		double landTime = landAltitude / MAXIMUM_LAND_VELOCITY + MAXIMUM_LAND_VELOCITY / (2 * MAXIMUM_LAND_ACCELERATION);
		double tOne = landTime - MAXIMUM_LAND_VELOCITY / MAXIMUM_LAND_ACCELERATION;
		PolarCoordinate where = interpreter.getCurrentPosition();
		running = true;
		altitude = landAltitude;
		LOG.info("landing: landAltitude=" + landAltitude + ", landTime=" + landTime + ", tOne=" + tOne);
		while (running && now < start + 1000*landTime + CYCLE_TIME && interpreter.getAutoPilot().getAltitudeOverGround() > 0.05) {
			now = System.currentTimeMillis();
			double tFlight = (now - start) / 1000.0;
			altitude = landAltitude - MAXIMUM_LAND_VELOCITY *  tFlight;
			if (tFlight >= tOne) {
				altitude += 0.5 * MAXIMUM_LAND_ACCELERATION * (tFlight - tOne) * (tFlight - tOne);
			}
			where.setAltitude(altitude);
			interpreter.setSetCoursePosition(where);
//			LOG.info("t=" + tFlight + ", landing from altitude " + interpreter.getAutoPilot().getAltitudeOverGround());
			try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
		}
		
		LOG.info("Vehicle landed at " + interpreter.getCurrentPosition());
		where.setAltitude(0);
		interpreter.setSetCoursePosition(where);
		try { Thread.sleep(1000); } catch (InterruptedException e) { }
		try {
			interpreter.getAutoPilot().shutDownEngines();
		} catch (IOException e) {
			e.printStackTrace();
		}
		running = false;
	}

	public void terminate() {
		running = false;
		LOG.info("Forced termination");
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
		return "land";
	}
}
