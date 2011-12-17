/*
 * @(#) CommandFlyToAbs.java
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

public class CommandFlyToAbs implements ICommand {
	
	Logger LOG = Logger.getLogger(CommandFlyToAbs.class);
	
	public static final double MINIMUM_PRECISION = 0.1;
	
	public static final double MINIIMUM_VELOCITY = 0.2;
	
	public static final double MAXIMUM_VELOCITY = 4.0;
	
	public static final long CYCLE_TIME = 500;
	
	private PolarCoordinate coordinate;
	private double velocity;
	private double precision;
	private boolean running = false;

	public CommandFlyToAbs (double latitude, double longitude, double altitude, double precision, double velocity) {
		coordinate = new PolarCoordinate(latitude, longitude, altitude);
		this.velocity = velocity > 0 ? velocity : MINIIMUM_VELOCITY;
		this.precision = precision > MINIMUM_PRECISION ? precision : MINIMUM_PRECISION;
		LOG.info("Constructor " + coordinate + ", velocity=" + this.velocity + "m/s, precision=" + this.precision + "m.");
	}
	
	public PolarCoordinate getCoordinate() {
		return coordinate;
	}

	public double getVelocity() {
		return velocity;
	}

	public double getPrecision() {
		return precision;
	}

	public void execute(IInterpreter interpreter) {
		long start = System.currentTimeMillis();
		long now = start;
		IGeodeticSystem gs = interpreter.getGeodeticSystem();

		PolarCoordinate where = interpreter.getCurrentPosition();
		CartesianCoordinate whereCartesian = gs.polarToRectangularCoordinates(where);
		CartesianCoordinate destinationCartesian = gs.polarToRectangularCoordinates(coordinate);
		CartesianCoordinate motionVector = destinationCartesian.subtract (whereCartesian);
		double distance = motionVector.norm();
		
		double totalTime = distance / velocity;
		double vMax = 1.5 * velocity;
		if (vMax > MAXIMUM_VELOCITY) {
			LOG.info("Reducing maximum velocity from " + vMax + "m/s to " + MAXIMUM_VELOCITY + "m/s.");
			vMax = MAXIMUM_VELOCITY;
			totalTime = 1.5 * distance / vMax;
		}
		
		LOG.info("Parameters: time=" + totalTime + ", vMax=" + vMax + ", dist=" + distance + ", velocity=" + velocity);
		LOG.info("Flying from " + where + " to " + coordinate + " in " + totalTime + "s, distance=" + distance);
		running = true;
		while (running && distance > precision && now < start + 1000.0 * totalTime + CYCLE_TIME) {
			now = System.currentTimeMillis();
			double tFlight = (now - start) / 1000.0;
			double s = 2.0 * vMax * tFlight * tFlight * (3.0 - 2.0 * tFlight / totalTime) / (3.0 * totalTime);
			CartesianCoordinate differenceVector = motionVector.multiply(s/distance);
			CartesianCoordinate setCourseCartesian = whereCartesian.add(differenceVector);
			PolarCoordinate setCoursePosition = gs.rectangularToPolarCoordinates(setCourseCartesian);
			interpreter.setSetCoursePosition(setCoursePosition);
			try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
		}

		PolarCoordinate currentPosition = interpreter.getCurrentPosition();
		CartesianCoordinate currentPositionCartesian = gs.polarToRectangularCoordinates(currentPosition);
		distance = destinationCartesian.subtract(currentPositionCartesian).norm();

		if (running && distance > precision)
			LOG.info("Flying time is over. Waiting for the vehicle to finally reach it's destination. Distance is " + distance + "m.");
		
		interpreter.setSetCoursePosition(coordinate);
//		long overtimeStart = now = System.currentTimeMillis();
		while (running && distance > precision /* && (now - overtimeStart < 20000 || distance > 10) */) {
//			now = System.currentTimeMillis();
			currentPosition = interpreter.getCurrentPosition();
			currentPositionCartesian = gs.polarToRectangularCoordinates(currentPosition);
			distance = destinationCartesian.subtract(currentPositionCartesian).norm();
			try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
		}
		
		try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
		if (running)
			LOG.info("Destination " + coordinate + " reached.");
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
