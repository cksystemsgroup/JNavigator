/*
 * @(#) ProactivePilotTestCase.java
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
import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.Matrix3x3;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;

/**
 * This class verifies the implementation of the <code>ProactivePilot</code> class.
 * 
 * @author Clemens Krainer
 */
public class ProactivePilotTestCase extends TestCase {

	/**
	 * Verify the normal operation of the proactive pilot.
	 */
	public void NoTestCase01 () {
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
		props.setProperty("flight.plan.one.className", "at.uni_salzburg.cs.ckgroup.pilot.FlightPlanOne");
		props.setProperty("clock.className", "at.uni_salzburg.cs.ckgroup.pilot.FakeClock");
		
		ProactivePilot pilot = null;
		IClock clock = null;
		ISetCourseSupplier setCourseSupplier = null;
		IPositionProvider positionProvider = null;
		HardWareSensorData sensorData = null;
		
		try {
			pilot = new ProactivePilot (props);

			pilot.startFlyingSetCourse ();
			assertFalse (pilot.isFlyingSetCourse ());

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
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the throwing of an <code>InstantiationException</code>
	 * if the property <code>flight.plan.list</code> does not exist.
	 */
	public void testCase02 () {
		Properties props = new Properties ();
		
		try {
			ProactivePilot pilot = new ProactivePilot (props);
			assertNull (pilot);
		} catch (InstantiationException e) {
			assertEquals ("Property flight.plan.list is not set or empty.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the throwing of an <code>InstantiationException</code>
	 * if the class name of a flight plan in the list is not defined.
	 */
	public void testCase03 () {
		Properties props = new Properties ();
		props.setProperty("flight.plan.list", "one");
		
		try {
			ProactivePilot pilot = new ProactivePilot (props);
			assertNull (pilot);
		} catch (InstantiationException e) {
			assertEquals ("No property className defined for property set flight.plan.one.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the construction of the <code>ProactivePilot</code> if
	 * a set of flight plans is properly configurated in the properties.
	 */
	public void testCase04 () {
		Properties props = new Properties ();
		props.setProperty("flight.plan.list", "one");
		props.setProperty("flight.plan.one.className", "at.uni_salzburg.cs.ckgroup.pilot.FlightPlanOne");
		
		try {
			ProactivePilot pilot = new ProactivePilot (props);
			assertNotNull (pilot);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	
	public void NOtestCase01 () {
		double roll = 0;
		double pitch = 0;
		double yaw = 15;
		double time = 100;
		
		System.out.println ("roll=" + roll + ", pitch=" + pitch + ", yaw=" + yaw);
		
//		PolarCoordinate p1 = new PolarCoordinate (48, 13, 440);
		PolarCoordinate p1 = new PolarCoordinate (0, 0, 440);
//		PolarCoordinate p1 = new PolarCoordinate (90, 0, 440);
//		PolarCoordinate p1 = new PolarCoordinate (0, 45, 440);
		WGS84 gs = new WGS84 ();
		
		CartesianCoordinate c1 = gs.polarToRectangularCoordinates(p1);
		System.out.println ("p1=" + p1 + ", c1=" + c1);
		
		Matrix3x3 rot3 = new Matrix3x3 (0, 90-p1.latitude, -p1.longitude);
		CartesianCoordinate c3 = rot3.multiply(c1);
		System.out.println ("rot3=" + rot3 + ", det(rot3)=" + rot3.det());
		PolarCoordinate p3 = gs.rectangularToPolarCoordinates(c3);
		System.out.println ("p3=" + p3 + ", c3=" + c3);
		
		Matrix3x3 rot = new Matrix3x3 (roll, pitch, yaw);
		System.out.println ("rot=" + rot + ", det(rot)=" + rot.det());
		
		Matrix3x3 rot2 = new Matrix3x3 (0, 90-p1.latitude, -p1.longitude);
		System.out.println ("rot2=" + rot2 + ", det(rot2)=" + rot2.det());
		
		CartesianCoordinate g1 = c1.multiply (-9.81 / c1.norm());
		System.out.println ("g1=" + g1 + ", norm=" + g1.norm());
		
//		CartesianCoordinate a1 = ((new Matrix3x3 ()).subtract(rot2.multiply(rot))).multiply(g1);
		CartesianCoordinate a1 = rot.subtract(Matrix3x3.One).multiply(g1);
		System.out.println ("a1=" + a1);
		
		CartesianCoordinate c2 = c1.add(a1.multiply(time));
		PolarCoordinate p2 = gs.rectangularToPolarCoordinates(c2);
		System.out.println ("p2=" + p2 + ", c2=" + c2);
	}
	

	
	public void NOtestCase04 () {
		double roll = 15;
		double pitch = 15;
		double yaw = 0;
		double time = 10;
		double sinRoll = Math.sin(roll*Matrix3x3.PI180TH);
		double sinPitch = Math.sin(pitch*Matrix3x3.PI180TH);
		
		System.out.println ("roll=" + roll + ", pitch=" + pitch + ", yaw=" + yaw);

		PolarCoordinate p1 = new PolarCoordinate (48, 13, 440);
//		PolarCoordinate p1 = new PolarCoordinate (0, 0, 440);
//		PolarCoordinate p1 = new PolarCoordinate (0, 90, 440);
//		PolarCoordinate p1 = new PolarCoordinate (90, 0, 440);
//		PolarCoordinate p1 = new PolarCoordinate (0, 45, 440);
		WGS84 gs = new WGS84 ();
		CartesianCoordinate c1 = gs.polarToRectangularCoordinates(p1);
		System.out.println ("p1=" + p1 + ", c1=" + c1);

		CartesianCoordinate a = new CartesianCoordinate (sinPitch*9.81, sinRoll*9.81, 0);
		System.out.println ("a=" + a);

		Matrix3x3 rot2 = new Matrix3x3 (0, 90-p1.latitude, p1.longitude);
		System.out.println ("rot2=" + rot2 + ", det(rot2)=" + rot2.det());
		
		CartesianCoordinate c2 = c1.add(rot2.multiply(a).multiply(time));
		CartesianCoordinate c3 = c2.subtract(c1);
		PolarCoordinate p2 = gs.rectangularToPolarCoordinates(c2);
		System.out.println ("p2=" + p2 + ", c2=" + c2 + "\nc3=" + c3);
		
		CartesianCoordinate a2 = rot2.transpose().multiply(c2.subtract(c1)).multiply(1/time);
		System.out.println ("a2=" + a2);
		double roll2 = Math.asin (a2.x / 9.81) / Matrix3x3.PI180TH; 
		double pitch2 = Math.asin (a2.y / 9.81) / Matrix3x3.PI180TH;
		System.out.println ("roll2=" + roll2 + ", pitch2=" + pitch2);
	}
	
	/**
	 * This test covers the conversion of acceleration vectors to roll and pitch
	 * values for a yaw value of zero.
	 */
	public void NOtestCase05 () {

		double [][] ll = new double [][] { {0, 0}, {0, 90}, {0, -90}, {45, 0}, {48, 13}, {-48, 13}, {-48, -13}, {48, -13} };
		
		for (int k=0; k < ll.length; k++) {
			double latitude = ll[k][0];
			double longitude = ll[k][1];
			double sinLatitude = Math.sin(latitude * Matrix3x3.PI180TH);
			double cosLatitude = Math.cos(latitude * Matrix3x3.PI180TH);
			double sinLongitude = Math.sin(longitude * Matrix3x3.PI180TH);
			double cosLongitude = Math.cos(longitude * Matrix3x3.PI180TH);
			
			PolarCoordinate p1 = new PolarCoordinate (latitude, longitude, 0);
//			WGS84 gs = new WGS84 ();
//			CartesianCoordinate c1 = gs.polarToRectangularCoordinates(p1);
		
//			System.out.println ("p1=" + p1 + ", c1=" + c1);
			
			Matrix3x3 rot = new Matrix3x3 (0, 90-p1.latitude, p1.longitude);
			rot = rot.transpose();
	
//			System.out.println ("    rot=" + rot + ", det(rot)=" + rot.det());
//			CartesianCoordinate x1 = rot.multiply(c1);
//			PolarCoordinate px1 = gs.rectangularToPolarCoordinates(x1);
//			System.out.println ("    px1=" + px1 + ", x1=" + x1);
			
			// doing nothing
			CartesianCoordinate a = new CartesianCoordinate (0, 0, 0);
//			System.out.println ("nothing a=" + a);	
//			CartesianCoordinate c2 = c1.add(a.multiply(time));
			CartesianCoordinate c3 = rot.multiply(a);
//			PolarCoordinate p2 = gs.rectangularToPolarCoordinates(c2);
//			System.out.println ("    p2=" + p2 + ", c2=" + c2 + ", c3=" + c3);
			assertEquals ("doing nothing [" + k + "]", 0, c3.x, 1E-9);
			assertEquals ("doing nothing [" + k + "]", 0, c3.y, 1E-9);
			assertEquals ("doing nothing [" + k + "]", 0, c3.z, 1E-9);
			
			// going north
			a = (new CartesianCoordinate (-cosLongitude*sinLatitude, -sinLongitude*sinLatitude, cosLatitude)).multiply(10);
//			System.out.println ("North a=" + a + " (" + a.norm() + ")");
//			c2 = c1.add(a.multiply(time));
			c3 = rot.multiply(a);
//			p2 = gs.rectangularToPolarCoordinates(c2);
//			System.out.println ("    p2=" + p2 + ", c2=" + c2 + ", c3=" + c3);
			assertEquals ("going North [" + k + "]", -10, c3.x, 1E-9);
			assertEquals ("going North [" + k + "]", 0, c3.y, 1E-9);
			assertEquals ("going North [" + k + "]", 0, c3.z, 1E-9);
			
			// going south
			a = (new CartesianCoordinate (cosLongitude*sinLatitude, sinLongitude*sinLatitude, -cosLatitude)).multiply(10);
//			System.out.println ("South a=" + a);
//			c2 = c1.add(a.multiply(time));
			c3 = rot.multiply(a);
//			p2 = gs.rectangularToPolarCoordinates(c2);
//			System.out.println ("    p2=" + p2 + ", c2=" + c2 + ", c3=" + c3);
			assertEquals ("going South [" + k + "]", 10, c3.x, 1E-9);
			assertEquals ("going South [" + k + "]", 0, c3.y, 1E-9);
			assertEquals ("going South [" + k + "]", 0, c3.z, 1E-9);
			
			// going east
			a = (new CartesianCoordinate (-sinLongitude, cosLongitude, 0)).multiply (10);
//			System.out.println ("East a=" + a);
//			c2 = c1.add(a.multiply(time));
			c3 = rot.multiply(a);
//			p2 = gs.rectangularToPolarCoordinates(c2);
//			System.out.println ("    p2=" + p2 + ", c2=" + c2 + ", c3=" + c3);
			assertEquals ("going East [" + k + "]", 0, c3.x, 1E-9);
			assertEquals ("going East [" + k + "]", 10, c3.y, 1E-9);
			assertEquals ("going East [" + k + "]", 0, c3.z, 1E-9);
			
			// going west
			a = (new CartesianCoordinate (sinLongitude, -cosLongitude, 0)).multiply (10);
//			System.out.println ("West a=" + a);
//			c2 = c1.add(a.multiply(time));
			c3 = rot.multiply(a);
//			p2 = gs.rectangularToPolarCoordinates(c2);
//			System.out.println ("    p2=" + p2 + ", c2=" + c2 + ", c3=" + c3);
			assertEquals ("going West [" + k + "]", 0, c3.x, 1E-9);
			assertEquals ("going West [" + k + "]", -10, c3.y, 1E-9);
			assertEquals ("going West [" + k + "]", 0, c3.z, 1E-9);
		}
	}
	

	/*
	 * This test covers the conversion of acceleration vectors to roll and pitch
	 * values for non zero yaw values.
	 */
	public void NOtestCase06 () {

		double [][] ll = new double [][] { {0, 0, 15}, {0, 90, 30}, {0, -90, 45}, {45, 0, -15}, {48, 13, -30}, {-48, 13, -45}, {-48, -13, 75}, {48, -13, -75} };
		
		for (int k=0; k < ll.length; k++) {
			double latitude = ll[k][0];
			double longitude = ll[k][1];
			double orientation = ll[k][2];
			
			double sinLatitude = Math.sin(latitude * Matrix3x3.PI180TH);
			double cosLatitude = Math.cos(latitude * Matrix3x3.PI180TH);
			double sinLongitude = Math.sin(longitude * Matrix3x3.PI180TH);
			double cosLongitude = Math.cos(longitude * Matrix3x3.PI180TH);
			double sinOrientation = Math.sin(orientation * Matrix3x3.PI180TH);
			double cosOrientation = Math.cos(orientation * Matrix3x3.PI180TH);
			
			PolarCoordinate p1 = new PolarCoordinate (latitude, longitude, 0);
			
			Matrix3x3 rot = new Matrix3x3 (0, 90-p1.latitude, p1.longitude);
			Matrix3x3 ori = new Matrix3x3 (0, 0, -orientation);
			rot = ori.multiply(rot.transpose());
			
			// doing nothing
			CartesianCoordinate a = new CartesianCoordinate (0, 0, 0);
			CartesianCoordinate c3 = rot.multiply(a);
			assertEquals ("doing nothing [" + k + "]", 0, c3.x, 1E-9);
			assertEquals ("doing nothing [" + k + "]", 0, c3.y, 1E-9);
			assertEquals ("doing nothing [" + k + "]", 0, c3.z, 1E-9);
			
			// going north
			a = (new CartesianCoordinate (-cosLongitude*sinLatitude, -sinLongitude*sinLatitude, cosLatitude)).multiply(10);
			c3 = rot.multiply(a);
			assertEquals ("going North [" + k + "]", -10*cosOrientation, c3.x, 1E-9);
			assertEquals ("going North [" + k + "]",  10*sinOrientation, c3.y, 1E-9);
			assertEquals ("going North [" + k + "]", 0, c3.z, 1E-9);
			
			// going south
			a = (new CartesianCoordinate (cosLongitude*sinLatitude, sinLongitude*sinLatitude, -cosLatitude)).multiply(10);
			c3 = rot.multiply(a);
			assertEquals ("going South [" + k + "]",  10*cosOrientation, c3.x, 1E-9);
			assertEquals ("going South [" + k + "]", -10*sinOrientation, c3.y, 1E-9);
			assertEquals ("going South [" + k + "]", 0, c3.z, 1E-9);
			
			// going east
			a = (new CartesianCoordinate (-sinLongitude, cosLongitude, 0)).multiply (10);
			c3 = rot.multiply(a);
			assertEquals ("going East [" + k + "]",  10*sinOrientation, c3.x, 1E-9);
			assertEquals ("going East [" + k + "]",  10*cosOrientation, c3.y, 1E-9);
			assertEquals ("going East [" + k + "]", 0, c3.z, 1E-9);
			
			// going west
			a = (new CartesianCoordinate (sinLongitude, -cosLongitude, 0)).multiply (10);
			c3 = rot.multiply(a);
			assertEquals ("going West [" + k + "]", -10*sinOrientation, c3.x, 1E-9);
			assertEquals ("going West [" + k + "]", -10*cosOrientation, c3.y, 1E-9);
			assertEquals ("going West [" + k + "]", 0, c3.z, 1E-9);
		}
	}


}
