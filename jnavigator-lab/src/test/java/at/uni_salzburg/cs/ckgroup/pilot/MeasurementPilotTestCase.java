/*
 * @(#) MeasurementPilotTestCase.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2009  Clemens Krainer
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

import java.util.Properties;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;


/**
 * This class verifies the implementation of the <code>MeasurementPilot</code>.
 * 
 * @author Clemens Krainer
 */
public class MeasurementPilotTestCase extends TestCase {

	/**
	 * Verify the normal operation of the measurement pilot.
	 */
	public void testCase01 () {
		Properties props = new Properties ();
		props.setProperty("name", "Procative Pilot");
		props.setProperty("className", "at.uni_salzburg.cs.ckgroup.pilot.ProactivePilot");
		props.setProperty("maximum.tilt.angle", "8");
		props.setProperty("average.dead.time", "1000");
		props.setProperty("average.response.time", "2960");
		props.setProperty("minimum.deviation", "0.8");
		props.setProperty("acceleration.factor", "2.85");
		props.setProperty("message.delay", "1000");
		props.setProperty("minimum.command.time", "500");
		props.setProperty("maximum.allowed.acceleration", "0.5");
		props.setProperty("flight.plan.list", "one");
		props.setProperty("flight.plan.one.className", "at.uni_salzburg.cs.ckgroup.pilot.FlightPlanThree");
		
		MeasurementPilot pilot = null;
		IClock clock = new MyTestClock ();
		ISetCourseSupplier setCourseSupplier = null;
		IPositionProvider positionProvider = null;
		HardWareSensorData sensorData = new HardWareSensorData (1, 2, 3, 4);
		
		try {
			pilot = new MeasurementPilot (props);
			pilot.setClock (clock);

			pilot.setCourseSupplier (setCourseSupplier);
			pilot.setPositionProvider (positionProvider);
			
			pilot.processSensorData (sensorData);
			
			pilot.startFlyingSetCourse ();
			assertTrue (pilot.isFlyingSetCourse ());

			pilot.processSensorData (sensorData);
			
			pilot.stopFlyingSetCourse ();
			assertFalse (pilot.isFlyingSetCourse ());
			
			pilot.processSensorData (sensorData);

		} catch (InstantiationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	
	/**
	 * This class implements a simple test clock.
	 * 
	 * @author Clemens Krainer
	 */
	public class MyTestClock implements IClock
	{
		public long currentTime = 0;
		
		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.util.IClock#currentTimeMillis()
		 */
		public long currentTimeMillis() {
			return currentTime;
		}
		
	}
}
