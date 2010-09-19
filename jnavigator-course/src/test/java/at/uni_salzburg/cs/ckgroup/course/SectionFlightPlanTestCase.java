/*
 * @(#) SectionFlightPlanTestCase.java
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

public class SectionFlightPlanTestCase extends TestCase {

	Section section;
	PolarCoordinate startPosition;
	double startOrientation;
	PolarCoordinate endPosition;
	double endOrientation;
	long travelTime;
	long[] durations;
	double[] velocities;
	IGeodeticSystem wgs84;
	
	public void setUp () {
		startPosition = new PolarCoordinate (48, 13, 400);
		assertNotNull (startPosition);
		startOrientation = 0;
		endPosition = new PolarCoordinate (48.02, 13.02, 500);
		assertNotNull (endPosition);
		endOrientation = 270;
		travelTime = 20000;
		section = new Section (startPosition, startOrientation, endPosition, endOrientation, travelTime);
		assertNotNull (section);
		wgs84 = new WGS84 ();
		
		CartesianCoordinate l = wgs84.polarToRectangularCoordinates(startPosition).subtract(wgs84.polarToRectangularCoordinates(endPosition));
		double len = l.norm();
		durations = new long[] { 5000, 10000, 20000 };
		velocities = new double[] { 10, 100, 100, 10 };
		
		double distance = 0;
		for (int k=0; k < durations.length-1; k++)
			distance += 0.0005 * durations[k] * (velocities[k+1] + velocities[k]);
		
		velocities[velocities.length-1] = 2000 * (len - distance) / durations[durations.length-1] - velocities[velocities.length-2];
	}
	
	public void testCase01 () {
		try {
			SectionFlightPlan sfp = new SectionFlightPlan (section);
			assertNotNull (sfp);
			sfp.setValues (durations, velocities);
			PolarCoordinate p = sfp.getScheduledPosition (0, wgs84);
			System.out.println ("1:" + p);
			assertEquals (startPosition.latitude, p.latitude, 1E-9);
			assertEquals (startPosition.longitude, p.longitude, 1E-9);
			assertEquals (startPosition.altitude, p.altitude, 1E-4);
			
			p = sfp.getScheduledPosition ( 5000, wgs84);
			System.out.println ("2:" + p);
			assertEquals (48.00205233675232, p.latitude, 1E-9);
			assertEquals (13.002051461241297, p.longitude, 1E-9);
			assertEquals (410.2089111888781, p.altitude, 1E-6);
			
			p = sfp.getScheduledPosition (15000, wgs84);
			System.out.println ("3:" + p);
			assertEquals (48.00951500885856, p.latitude, 1E-9);
			assertEquals (13.00951263703431, p.longitude, 1E-9);
			assertEquals (447.4321438455954, p.altitude, 1E-4);
			
			p = sfp.getScheduledPosition (0, wgs84);
			System.out.println ("1:" + p);
			assertEquals (startPosition.latitude, p.latitude, 1E-9);
			assertEquals (startPosition.longitude, p.longitude, 1E-9);
			assertEquals (startPosition.altitude, p.altitude, 1E-4);
			
			// 1ms before the end of the section -> endposition not fully reached.
			p = sfp.getScheduledPosition (34999, wgs84);
			System.out.println ("4:" + p);
			assertEquals (endPosition.latitude, p.latitude, 1E-6);
			assertEquals (endPosition.longitude, p.longitude, 1E-6);
			assertEquals (endPosition.altitude, p.altitude, 1E-2);
			
			p = sfp.getScheduledPosition (40000, wgs84);
			System.out.println ("5:" + p);
			assertEquals (endPosition.latitude, p.latitude, 1E-9);
			assertEquals (endPosition.longitude, p.longitude, 1E-9);
			assertEquals (endPosition.altitude, p.altitude, 1E-4);			
			
			p = sfp.getScheduledPosition (35000, wgs84);
			System.out.println ("6:" + p);
			assertEquals (endPosition.latitude, p.latitude, 1E-9);
			assertEquals (endPosition.longitude, p.longitude, 1E-9);
			assertEquals (endPosition.altitude, p.altitude, 1E-4);
		} catch (Throwable t) {
			t.printStackTrace();
			fail ();
		}
	}
	
	public void testCase02 () {
		try {
			Section section = new Section (startPosition, startOrientation, null, endOrientation, travelTime);
			SectionFlightPlan sfp = new SectionFlightPlan (section);
			assertNotNull (sfp);
			sfp.setValues (durations, velocities);
			
			PolarCoordinate p = sfp.getScheduledPosition (40000, wgs84);
			System.out.println ("7:" + p);
			assertEquals (startPosition.latitude, p.latitude, 1E-9);
			assertEquals (startPosition.longitude, p.longitude, 1E-9);
			assertEquals (startPosition.altitude, p.altitude, 1E-4);			
			
		} catch (Throwable t) {
			t.printStackTrace();
			fail ();
		}
	}
	
	public void testCase03 () {
		
		try {
			SectionFlightPlan sfp = new SectionFlightPlan (null);
			assertNull (sfp);
		} catch (NullPointerException e) {
			assertEquals ("The section parameter must not be null!", e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
			fail ();
		}
	}

	public void testCase04 () {
		
		SectionFlightPlan sfp = new SectionFlightPlan (section);
		assertNotNull (sfp);
		long[] durations = new long[] { 1 };
		double[] velocities = new double[] { };
		try {
			sfp.setValues(durations, velocities);
		} catch (ArrayIndexOutOfBoundsException e) {
			assertEquals ("The number of elements in the velocities array must be one more than in the durations array.", e.getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
			fail ();
		}
	}


}
