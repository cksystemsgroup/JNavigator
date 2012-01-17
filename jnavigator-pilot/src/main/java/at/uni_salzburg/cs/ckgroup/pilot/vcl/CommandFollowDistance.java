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

import java.net.URI;
import java.util.Map;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.pilot.PositionProxy;

public class CommandFollowDistance implements ICommand {
	
	private static final Logger LOG = Logger.getLogger(CommandFollowDistance.class);
	
	public static final String SENSOR_NAME_POSITION = "position";

	public static final long CYCLE_TIME = 1000;
	
	private String pilotName;
	private boolean running = false;
	private CartesianCoordinate differenceVector;
	
	public CommandFollowDistance (String pilotName, double distance, double course, double altitudeDifference) {
		this.pilotName = pilotName.trim();
		double x = -distance * Math.cos(Math.toRadians(course));		
		double y = distance * Math.sin(Math.toRadians(course));
		differenceVector = new CartesianCoordinate(x, y, altitudeDifference);
	}

	public void execute(IInterpreter interpreter) {
		Map<String, URI> pilotUriMap = interpreter.getConfiguration().getPilotUriMap();
		if (pilotUriMap == null || pilotUriMap.size() == 0) {
			LOG.error("No remote pilots configured.");
			return;
		}
		
		URI uri = interpreter.getConfiguration().getPilotUriMap().get(pilotName);
		if (uri == null) {
			LOG.error("Pilot unknown: '" + pilotName + "'");
			return;
		}
		
		PositionProxy proxy = new PositionProxy(uri.toString());
		proxy.fetchCurrentPosition();
		
		running = true;
		while (running && (!proxy.isAutoPilotFlight() || proxy.getCurrentPosition() == null)) {
			mySleep();
			proxy.fetchCurrentPosition();
		}
		
		if (!running) {
			return;
		}
		
		PolarCoordinate masterPosition = proxy.getCurrentPosition();
		IGeodeticSystem gs = interpreter.getGeodeticSystem();

		while (running && proxy.isAutoPilotFlight()) {
//			masterPosition.setAltitude(proxy.getAltitudeOverGround());
			CartesianCoordinate masterCartesian = gs.polarToRectangularCoordinates(masterPosition);
			CartesianCoordinate destinationCartesian = masterCartesian.add(differenceVector);
			PolarCoordinate setCoursePosition = gs.rectangularToPolarCoordinates(destinationCartesian);
			if (setCoursePosition.getAltitude() < 1.0) {
				setCoursePosition.setAltitude(1.0);
			}
			interpreter.setSetCoursePosition(setCoursePosition);
			
			mySleep();
			proxy.fetchCurrentPosition();
			masterPosition = proxy.getCurrentPosition();
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


	private void mySleep() {
		try { Thread.sleep(CYCLE_TIME); } catch (InterruptedException e) { }
	}
}
