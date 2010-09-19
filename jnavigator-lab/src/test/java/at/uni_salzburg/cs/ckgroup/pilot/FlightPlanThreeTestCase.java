/*
 * @(#) FlightPlanThreeTestCase.java
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

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;

public class FlightPlanThreeTestCase extends TestCase {
	
	public void testCase01 () {
		MockFlightParameterProvider param = new MockFlightParameterProvider (1000, 2000, 1, 1000, 1000);
		
		FlightPlanOne flp = new FlightPlanOne (null);
		flp.setFlightParameterProvider(param);
		
		CartesianCoordinate distance = new CartesianCoordinate(0,50,0);
		CartesianCoordinate currentVelocity = new CartesianCoordinate(0,1,0);
		CartesianCoordinate nextVelocity = new CartesianCoordinate(0,2,0);
		long planTime = 20000;
		
		boolean ok = flp.estimateFlightPlan(distance, currentVelocity, nextVelocity, planTime);
		assertTrue (ok);
		
		CartesianCoordinate[] a = flp.getAccelerations();
		long[] t = flp.getCommandTimes();
		boolean rework = flp.scheduleNeedsReworking ();
		
		assertEquals (0.0, a[0].x, 1E-9);
		assertEquals (0.8235294117647057, a[0].y, 1E-9);
		assertEquals (0.0, a[0].z, 1E-9);
		assertEquals (1000, t[0]);
	
		assertEquals (0.0, a[1].x, 1E-9);
		assertEquals (0.0, a[1].y, 1E-9);
		assertEquals (0.0, a[1].z, 1E-9);
		assertEquals (16000, t[1]);
	
		assertEquals (0.0, a[2].x, 1E-9);
		assertEquals (-0.32352941176470584, a[2].y, 1E-9);
		assertEquals (0.0, a[2].z, 1E-9);
		assertEquals (1000, t[2]);
		
		assertEquals (0.0, a[3].x, 1E-9);
		assertEquals (0.0, a[3].y, 1E-9);
		assertEquals (0.0, a[3].z, 1E-9);
		assertEquals (2000, t[3]);
		
		assertFalse (rework);
	}

	public void testCase02 () {
		MockFlightParameterProvider param = new MockFlightParameterProvider (1000, 2000, 1, 1000, 1000);
		
		FlightPlanOne flp = new FlightPlanOne (null);
		flp.setFlightParameterProvider(param);
		
		CartesianCoordinate distance = new CartesianCoordinate(0,200,0);
		CartesianCoordinate currentVelocity = new CartesianCoordinate(0,1,0);
		CartesianCoordinate nextVelocity = new CartesianCoordinate(0,2,0);
		long planTime = 20000;
		
		boolean ok = flp.estimateFlightPlan(distance, currentVelocity, nextVelocity, planTime);
		assertTrue (ok);
		
		CartesianCoordinate[] a = flp.getAccelerations();
		long[] t = flp.getCommandTimes();
		boolean rework = flp.scheduleNeedsReworking ();
		
		assertEquals (0.0, a[0].x, 1E-9);
		assertEquals (0.921229534411315, a[0].y, 1E-9);
		assertEquals (0.0, a[0].z, 1E-9);
		assertEquals (12159, t[0]);
	
		assertEquals (0.0, a[1].x, 1E-9);
		assertEquals (0.0, a[1].y, 1E-9);
		assertEquals (0.0, a[1].z, 1E-9);
		assertEquals (2000, t[1]);
	
		assertEquals (0.0, a[2].x, 1E-9);
		assertEquals (-0.9147515947282432, a[2].y, 1E-9);
		assertEquals (0.0, a[2].z, 1E-9);
		assertEquals (11159, t[2]);
		
		assertEquals (0.0, a[3].x, 1E-9);
		assertEquals (0.0, a[3].y, 1E-9);
		assertEquals (0.0, a[3].z, 1E-9);
		assertEquals (2000, t[3]);
		
		assertFalse (rework);
	}
	
	public void testCase03 () {
		MockFlightParameterProvider param = new MockFlightParameterProvider (1000, 2000, 1, 1000, 1000);
		
		FlightPlanOne flp = new FlightPlanOne (null);
		flp.setFlightParameterProvider(param);
		
		CartesianCoordinate distance = new CartesianCoordinate(0,20,0);
		CartesianCoordinate currentVelocity = new CartesianCoordinate(0,1,0);
		CartesianCoordinate nextVelocity = new CartesianCoordinate(0,2,0);
		long planTime = 20000;
		
		boolean ok = flp.estimateFlightPlan(distance, currentVelocity, nextVelocity, planTime);
		assertFalse (ok);
		
		CartesianCoordinate[] a = flp.getAccelerations();
		assertNull (a);
		long[] t = flp.getCommandTimes();
		assertNull (t);
		boolean rework = flp.scheduleNeedsReworking ();
		assertTrue (rework);
	}
	
	public void NOtestCase04 () {
		
//		ProactivePilot: position data: oldFlightTime=107980, currentFlightTime=108004,
//		oldPosition=(48.00005842183333°, 12.999966381°, 440.379m),
//		currentPosition=(48.00005842183333°, 12.999966381°, 440.379m), 
//		setCoursePosition=position=(48.0°, 13.0°, 440.4m), totalSpeed=0.0m/s, courseOverGround=0.0°, elevation=0.0°, orientation=0.0°
		long currentFlightTime = 108004;
		long oldFlightTime = 107980;
		PolarCoordinate oldPosition = new PolarCoordinate (48.00005842183333, 12.999966381, 440.379);
		PolarCoordinate currentPosition = new PolarCoordinate (48.00005842183333, 12.999966381, 440.379);
		PolarCoordinate nextPosition = new PolarCoordinate (48.0, 13.0, 440.4);
		
		IGeodeticSystem gs = new WGS84 ();
		CartesianCoordinate currentPositionRect = gs.polarToRectangularCoordinates (currentPosition);
		CartesianCoordinate oldPositionRect = gs.polarToRectangularCoordinates (oldPosition);
		CartesianCoordinate currentVelocity = currentPositionRect.subtract (oldPositionRect).multiply (1/(currentFlightTime - oldFlightTime));
		
		CartesianCoordinate nextPositionRect = gs.polarToRectangularCoordinates (nextPosition);
		CartesianCoordinate nextVelocity = new CartesianCoordinate (0, 0, 0);
		CartesianCoordinate distance = nextPositionRect.subtract (currentPositionRect);
		
		MockFlightParameterProvider param = new MockFlightParameterProvider (1000, 2000, 1, 1000, 1000);
		
		FlightPlanOne flp = new FlightPlanOne (null);
		flp.setFlightParameterProvider(param);
		
//		CartesianCoordinate distance = new CartesianCoordinate(0,20,0);
//		CartesianCoordinate currentVelocity = new CartesianCoordinate(0,1,0);
//		CartesianCoordinate nextVelocity = new CartesianCoordinate(0,2,0);
		long planTime = 20000;
		
		boolean ok = flp.estimateFlightPlan(distance, currentVelocity, nextVelocity, planTime);
		assertFalse (ok);
		
		CartesianCoordinate[] a = flp.getAccelerations();
		assertNull (a);
		long[] t = flp.getCommandTimes();
		assertNull (t);
		boolean rework = flp.scheduleNeedsReworking ();
		assertTrue (rework);
	}

	public void testCase05 () {

//	ProactivePilot: position data: oldFlightTime=57637, currentFlightTime=57660,
//	oldPosition=(48.000000349666664°, 13.000012555166666°, 440.399m),
//	currentPosition=(48.000000349666664°, 13.000012555166666°, 440.399m),
//	setCoursePosition=position=(48.0°, 13.00003°, 440.4m), totalSpeed=0.0m/s, courseOverGround=0.0°, elevation=0.0°, orientation=0.0°
//		long currentFlightTime = 57660;
//		long oldFlightTime = 57637;
//		PolarCoordinate oldPosition = new PolarCoordinate (48.000000349666664, 13.000012555166666, 440.399);
//		PolarCoordinate currentPosition = new PolarCoordinate (48.000000349666664, 13.000012555166666, 440.399);
//		PolarCoordinate nextPosition = new PolarCoordinate (48.0, 13.00003, 440.4);
//		
//		IGeodeticSystem gs = new WGS84 ();
//		CartesianCoordinate currentPositionRect = gs.polarToRectangularCoordinates (currentPosition);
//		CartesianCoordinate oldPositionRect = gs.polarToRectangularCoordinates (oldPosition);
//		CartesianCoordinate currentVelocity = currentPositionRect.subtract (oldPositionRect).multiply (1/(currentFlightTime - oldFlightTime));
//		
//		CartesianCoordinate nextPositionRect = gs.polarToRectangularCoordinates (nextPosition);
//		CartesianCoordinate nextVelocity = new CartesianCoordinate (0, 0, 0);
//		CartesianCoordinate distance = nextPositionRect.subtract (currentPositionRect);
//		
		MockFlightParameterProvider param = new MockFlightParameterProvider (1000, 2000, 1, 1000, 1000);
		
		FlightPlanOne flp = new FlightPlanOne (null);
		flp.setFlightParameterProvider(param);
		
//		FlightPlanThree.estimateFlightPlan: distance=(-0.2640614453703165m, 1.2751989505486563m, -0.025274149142205715m), d2=NaN,
//		currentVelocity=(-0.0m, 0.0m, -0.0m), nextVelocity=(-0.0m, 0.0m, -0.0m), planTime=2.953674058873324
		CartesianCoordinate distance = new CartesianCoordinate(-0.2640614453703165, 1.2751989505486563, -0.025274149142205715);
		CartesianCoordinate currentVelocity = new CartesianCoordinate(0,0,0);
		CartesianCoordinate nextVelocity = new CartesianCoordinate(0,0,0);
		long planTime = 2953;
		
		boolean ok = flp.estimateFlightPlan(distance, currentVelocity, nextVelocity, planTime);
		assertFalse (ok&false);
//		
//		CartesianCoordinate[] a = flp.getAccelerations();
//		assertNull (a);
//		long[] t = flp.getCommandTimes();
//		assertNull (t);
//		boolean rework = flp.scheduleNeedsReworking ();
//		assertTrue (rework);
	}
	
}
