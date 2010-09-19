/*
 * @(#) SphericEarthTestCase.java
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
package at.uni_salzburg.cs.ckgroup.course;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the SphericEarth class.
 * 
 * @author Clemens Krainer
 */
public class SphericEarthTestCase extends TestCase
{
	private static double PI180TH = Math.PI / 180;
	
	/**
	 * This test case verifies the implementation of the
	 * rectangularToPolarCoordinates() and polarToRectangularCoordinates()
	 * methods of class SphericEarth.
	 */
	public void testCase01 () {
		CartesianCoordinate pos = new CartesianCoordinate (1000,10,100);
		SphericEarth gs = new SphericEarth ();
		
		PolarCoordinate wgs = gs.rectangularToPolarCoordinates (pos);
		CartesianCoordinate rec = gs.polarToRectangularCoordinates (wgs);
		
		System.out.println ();
		System.out.println ("Rectangular X=" + pos.x + " Y=" + pos.y + " Z=" + pos.z);
		System.out.println ("Polar       Latitude=" + wgs.latitude + " Longitude=" + wgs.longitude + " Altitude=" + wgs.altitude);
		System.out.println ("Rectangular X=" + rec.x + " Y=" + rec.y + " Z=" + rec.z);
		
		assertTrue (Math.abs (rec.x - pos.x) < 1E-4);
		assertTrue (Math.abs (rec.y - pos.y) < 1E-4);
		assertTrue (Math.abs (rec.z - pos.z) < 1E-4);
		
		assertTrue (Math.abs (5.710309516157825 - wgs.latitude) < 1E-4);
		assertTrue (Math.abs (0.5729386976834859 - wgs.longitude) < 1E-4);
		assertTrue (Math.abs (-6369995.74768726 - wgs.altitude) < 1E-4);
	}
	
	/**
	 * This test case verifies the implementation of the
	 * rectangularToPolarCoordinates() and polarToRectangularCoordinates()
	 * methods of class SphericEarth.
	 */
	public void testCase02 () {
		double latitude = 47.99043439493213;
		double longitude = 12.93670580800686;
		double altitude = 435.94417220;
		SphericEarth gs = new SphericEarth (null);
		
		PolarCoordinate wgs = new PolarCoordinate (latitude, longitude, altitude);
		CartesianCoordinate rec = gs.polarToRectangularCoordinates (wgs);
		PolarCoordinate pos = gs.rectangularToPolarCoordinates (rec);
		
		System.out.println ();
		System.out.println ("Polar       X=" + wgs.latitude + " Y=" + wgs.longitude + " Z=" + wgs.altitude);
		System.out.println ("Rectangular X=" + rec.x + " Y=" + rec.y + " Z=" + rec.z);
		System.out.println ("Polar       X=" + pos.latitude + " Y=" + pos.longitude + " Z=" + pos.altitude);
		
		assertTrue (Math.abs (wgs.latitude - pos.latitude) < 1E-4);
		assertTrue (Math.abs (wgs.longitude - pos.longitude) < 1E-4);
		assertTrue (Math.abs (wgs.altitude - pos.altitude) < 1E-4);
		
		assertTrue (Math.abs (4155881.9047818147 - rec.x) < 1E-4);
		assertTrue (Math.abs (954626.4999540795 - rec.y) < 1E-4);
		assertTrue (Math.abs (4734188.402771593 - rec.z) < 1E-4);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If old and new Position are the same, the speed must be zero and
	 * the course must be undefined.
	 */
	public void testCase03 () {		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (48,13,440);
		long timeSpan = 10;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertFalse (res.courseIsValid);
		assertEquals (0.0, res.distance, 1E-5);
		assertEquals (0.0, res.speed, 1E-5);
		assertEquals (0.0, res.elevation, 1E-5);
	}

	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If old and new Position are the same but the altitude varies, the
	 * speed must be not zero and the course must be undefined.
	 */
	public void testCase04 () {		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (48,13,480);
		long timeSpan = 10000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertFalse (res.courseIsValid);
		assertEquals (40.0, res.distance, 1E-5);
		assertEquals ( 4.0, res.speed, 1E-5);
		assertEquals (90.0, res.elevation, 1E-5);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If old and new Position are the same but the altitude varies, the
	 * speed over ground must be zero and the course must be undefined.
	 */
	public void testCase05 () {		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (48,13,400);
		long timeSpan = 10000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertFalse (res.courseIsValid);
		assertEquals ( 40.0, res.distance, 1E-5);
		assertEquals (  4.0, res.speed, 1E-5);
		assertEquals (-90.0, res.elevation, 1E-5);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If the new Position has a greater latitude than the old Position
	 * and the longitude remains the same, the speed over ground must not be
	 * zero and the course is North (0 degrees).
	 */
	public void testCase06 () {
		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (48.001,13,440);
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
		assertEquals (111.2026197925, res.distance, 1E-4);
		assertEquals (1.112026197925, res.speed, 1E-4);
		assertEquals (0.0, res.elevation, 1E-3);		
		assertEquals (0.0, res.course, 1E-4);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If the new Position has a smaller latitude than the old Position
	 * and the longitude remains the same, the speed over ground must not be
	 * zero and the course is South (180 degrees).
	 */
	public void testCase07 () {
		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (47.999,13,440);
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
		assertEquals (111.2026197925, res.distance, 1E-4);
		assertEquals (1.112026197925, res.speed, 1E-4);
		assertEquals (0.0, res.elevation, 1E-3);		
		assertEquals (180.0, res.course, 1E-4);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If the new Position has a greater longitude than the old Position
	 * and the latitude remains the same, the speed over ground must not be zero
	 * and the course is East (90 degrees).
	 */
	public void testCase08 () {
		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (48,13.001,440);
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
		assertEquals (74.40907641, res.distance, 1E-4);
		assertEquals (0.7440907641, res.speed, 1E-4);
		assertEquals (0.0, res.elevation, 1E-3);		
		assertEquals (270.0, res.course, 1E-4);
	}	

	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If the new Position has a smaller longitude than the old Position
	 * and the latitude remains the same, the speed over ground must not be zero
	 * and the course is West (270 degrees).
	 */
	public void testCase09 () {
		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (48,12.999,440);
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
		assertEquals (74.40907641, res.distance, 1E-4);
		assertEquals (0.7440907641, res.speed, 1E-4);
		assertEquals (0.0, res.elevation, 1E-3);		
		assertEquals (90.0, res.course, 1E-4);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If the oldPosition is null the result must also be null.
	 */
	public void testCase10 () {
		
		PolarCoordinate oldPosition = null;
		PolarCoordinate newPosition = new PolarCoordinate (48,12.999,440);
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNull (res);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If the newPosition is null the result must also be null.
	 */
	public void testCase11 () {
		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = null;
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNull (res);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If the timeSpan is zero the result must also be null.
	 */
	public void testCase12 () {
		
		PolarCoordinate oldPosition = new PolarCoordinate (48,13,440);
		PolarCoordinate newPosition = new PolarCoordinate (48,12.999,440);
		long timeSpan = 0;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNull (res);
	}
	
	/**
	 * This test calculates the elevation of a course.
	 */
	public void testCase13 () {
		PolarCoordinate A = new PolarCoordinate (48, 13, 1010);
		PolarCoordinate B = new PolarCoordinate (48.001, 13.002, 1000);
		IGeodeticSystem gs = new SphericEarth (); 
		CartesianCoordinate a = gs.polarToRectangularCoordinates (A);
		CartesianCoordinate b = gs.polarToRectangularCoordinates (B);
		
		CartesianCoordinate mv = b.subtract (a);
		double distance = mv.norm ();
		
		double x = a.multiply (mv) / (a.norm ()*distance);
		if (x > 1)
			x = 1;
		else if (x < -1)
			x = -1;
		double elevation = Math.asin (x) / PI180TH;
		
		assertEquals (-3.081734279042892, elevation, 1E-9);
	}
	
	/**
	 * This test verifies the walk() method.
	 */
	public void testCase14 () {
		PolarCoordinate A = new PolarCoordinate (48, 13, 1010);
		IGeodeticSystem gs = new SphericEarth ();
		
		PolarCoordinate U = gs.walk (A, 100, 0, 0);
		assertEquals (47.99910082105247, U.latitude, 1E-8);
		assertEquals (A.longitude, U.longitude, 1E-8);
		assertEquals (A.altitude, U.altitude, 1E-8);
		
		PolarCoordinate V = gs.walk (A, 0, 100, 0);
		assertEquals (A.latitude, V.latitude, 1E-8);
		assertEquals (13.001343801851217, V.longitude, 1E-8);
		assertEquals (A.altitude, V.altitude, 1E-8);
		
		PolarCoordinate W = gs.walk (A, 0, 0, 100);
		assertEquals (A.latitude, W.latitude, 1E-8);
		assertEquals (A.longitude, W.longitude, 1E-8);
		assertEquals (A.altitude+100, W.altitude, 1E-8);
	}
		
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If old and new Position are the same, the speed must be zero and
	 * the course must be undefined. Check with negative values for latitude.
	 */
	public void testCase15 () {
		PolarCoordinate oldPosition = new PolarCoordinate (-48.00001,13.00001,440);
		PolarCoordinate newPosition = new PolarCoordinate (-48,13,480);
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
		assertEquals (40.022372299542745, res.distance, 1E-4);
		assertEquals (0.40022372299542747, res.speed, 1E-4);
		assertEquals (88.0841428532707, res.elevation, 1E-3);	
//		assertEquals (315.00000001017776, res.course, 1E-3);
		assertEquals (33.78769179629967, res.course, 1E-3);
	}
	
	/**
	 * This test verifies the implementation of the calculateSpeedAndCourse()
	 * method. If old and new Position are the same, the speed must be zero and
	 * the course must be undefined. Check with negative values for latitude and
	 * longitude.
	 */
	public void testCase16 () {
		PolarCoordinate oldPosition = new PolarCoordinate (-48,-13,440);
		PolarCoordinate newPosition = new PolarCoordinate (-48.00001,-13.00001,400);
		long timeSpan = 100000;
		
		SphericEarth gs = new SphericEarth ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
		assertEquals (40.02237201864862, res.distance, 1E-4);
		assertEquals (0.40022372018648616, res.speed, 1E-4);
		assertEquals (-88.08416690453453, res.elevation, 1E-3);
//		assertEquals (224.99999998982221, res.course, 1E-3);
		assertEquals (146.21231333685338, res.course, 1E-3);
	}	

}
