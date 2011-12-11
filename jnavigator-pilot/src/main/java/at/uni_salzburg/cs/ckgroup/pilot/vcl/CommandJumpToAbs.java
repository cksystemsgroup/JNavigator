/*
 * @(#) CommandJumpToAbs.java
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

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;

public class CommandJumpToAbs implements ICommand {
	
	Logger LOG = Logger.getLogger(CommandJumpToAbs.class);
	
	public static final double MINIMUM_PRECISION = 0.1;
	
	private PolarCoordinate coordinate;
	private double precision;
	private boolean running = false;

	public CommandJumpToAbs (double latitude, double longitude, double altitude, double precision) {
		coordinate = new PolarCoordinate(latitude, longitude, altitude);
		this.precision = precision > MINIMUM_PRECISION ? precision : MINIMUM_PRECISION;
	}
	
	public PolarCoordinate getCoordinate() {
		return coordinate;
	}

	public double getPrecision() {
		return precision;
	}

	public void execute(IInterpreter interpreter) {
		long start = System.currentTimeMillis();
		IGeodeticSystem gs = interpreter.getGeodeticSystem();
		PolarCoordinate where = interpreter.getCurrentPosition();
		CartesianCoordinate whereCartesian = gs.polarToRectangularCoordinates(where);
		CartesianCoordinate destinationCartesian = gs.polarToRectangularCoordinates(coordinate);
		CartesianCoordinate motionVector = whereCartesian.subtract (destinationCartesian);
		double distance = motionVector.norm();
		
		LOG.info("Jumping from " + where + " to " + coordinate);
		running = true;
		interpreter.setSetCoursePosition(coordinate);

		try { Thread.sleep(500); } catch (InterruptedException e) { }
		
		if (running && distance > precision)
			LOG.info("Waiting for the vehicle to finally reach it's destination. Distance is " + distance + "m.");
		
		while (running && distance > precision) {
			PolarCoordinate currentPosition = interpreter.getCurrentPosition();
			CartesianCoordinate currentPositionCartesian = gs.polarToRectangularCoordinates(currentPosition);
			distance = destinationCartesian.subtract(currentPositionCartesian).norm();
			try { Thread.sleep(500); } catch (InterruptedException e) { }
			LOG.info("Waiting for the vehicle to finally reach it's destination. Distance is " + distance + "m.");
		}
		
		long now = System.currentTimeMillis();
		if (running)
			LOG.info("Destination " + coordinate + " reached in " + ((now-start)/1000) + "s");
		else
			LOG.info("Command terminated at position " + interpreter.getCurrentPosition() + ", distance is " + distance + "m.");
		
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
	
	
}
