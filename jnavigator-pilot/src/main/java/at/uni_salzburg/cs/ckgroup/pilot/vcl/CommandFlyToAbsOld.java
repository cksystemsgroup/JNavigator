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

public class CommandFlyToAbsOld implements ICommand {
	
	private static final Logger LOG = Logger.getLogger(CommandFlyToAbsOld.class);
	
	public static final double MINIMUM_PRECISION = 0.1;
	
	public static final double MAXIMUM_ACCELERATION = 1.0;
	
	public static final long CYCLE_TIME = 500;
	
	private PolarCoordinate coordinate;
	private double velocity;
	private double precision;
	private boolean running = false;

	public CommandFlyToAbsOld (double latitude, double longitude, double altitude, double precision, double velocity) {
		coordinate = new PolarCoordinate(latitude, longitude, altitude);
		this.velocity = velocity > 0 ? velocity : 0.2;
		this.precision = precision > MINIMUM_PRECISION ? precision : MINIMUM_PRECISION;
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
		
		double time = distance / velocity;
		double p = time / 2.0;
		double q = velocity * time / MAXIMUM_ACCELERATION;
		double tOne = p;
		if (p*p > q)
			tOne -= Math.sqrt(p*p -q);
		double vMax = tOne * MAXIMUM_ACCELERATION;
		double tTwo = time - tOne;
		double s1 = 0, s2 = 0, s3 = 0;
		
		LOG.info("Parameters: time=" + time + ", tOne=" + tOne + ", tTwo=" + tTwo + ", vMax=" + vMax + ", dist=" + distance + ", velocity=" + velocity);
		LOG.info("Flying from " + where + " to " + coordinate + " in " + time + "s.");
		running = true;
		boolean sect1 = false, sect2 = false, sect3 = false, sect4 = false;
		while (running && distance > precision && now < start + 1000.0 * time + CYCLE_TIME) {
			now = System.currentTimeMillis();
			
			double tFlight = (now - start) / 1000.0;

			if (tFlight < tOne) {
				s1 = tFlight * tFlight * MAXIMUM_ACCELERATION / 2.0;
				if (!sect1) { LOG.info("Section 1 reached."); sect1 = true; }				
			} else if (tFlight > tOne && tFlight < tTwo) {
				s1 = tOne * tOne * MAXIMUM_ACCELERATION / 2.0;
				s2 = vMax * (tFlight - tOne);
				if (!sect2) { LOG.info("Section 2 reached."); sect2 = true; }
			} else if (tFlight > tTwo && tFlight < time) {
				s1 = tOne * tOne * MAXIMUM_ACCELERATION / 2.0;
				s2 = (tFlight - 2*tOne) * vMax;
				s3 = (tFlight - tTwo) * vMax - (tFlight - tTwo) * (tFlight - tTwo) * MAXIMUM_ACCELERATION / 2.0;
				if (!sect3) { LOG.info("Section 3 reached."); sect3 = true; }
			} else {
				s1 = s3 = tOne * tOne * MAXIMUM_ACCELERATION / 2;
				s2 = (time - 2*tOne) * vMax;
				if (!sect4) { LOG.info("Section 4 reached."); sect4 = true; }
			}
				
			CartesianCoordinate differenceVector = motionVector.multiply((s1+s2+s3)/distance);
//			PolarCoordinate setCoursePosition = gs.walk(where, differenceVector.x, differenceVector.y, differenceVector.z);
			CartesianCoordinate setCourseCartesian = whereCartesian.add(differenceVector);
			PolarCoordinate setCoursePosition = gs.rectangularToPolarCoordinates(setCourseCartesian);
			interpreter.setSetCoursePosition(setCoursePosition);
			try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
		}
		
		if (running && distance > precision)
			LOG.info("Flying time is over. Waiting for the vehicle to finally reach it's destination. Distance is " + distance + "m.");
		
		interpreter.setSetCoursePosition(coordinate);
		while (running && distance > precision) {
			PolarCoordinate currentPosition = interpreter.getCurrentPosition();
			CartesianCoordinate currentPositionCartesian = gs.polarToRectangularCoordinates(currentPosition);
			distance = destinationCartesian.subtract(currentPositionCartesian).norm();
			try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
		}
		
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
