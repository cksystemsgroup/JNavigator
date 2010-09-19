/*
 * @(#) AdvancedCoursePlannerTestCase.java
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

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

import junit.framework.TestCase;

/**
 * This tests verify the implementation of the
 * <code>AdvancedCoursePlanner</code> class.
 * 
 * @author Clemens Krainer
 */
public class AdvancedCoursePlannerTestCase extends TestCase {

	private Properties props;
	private Section[] sections;
	private IGeodeticSystem geodeticSystem;
	
	public void setUp () {
		props = new Properties ();
		props.setProperty (AdvancedCoursePlanner.PROP_MAXIMUM_ALLOWED_ACCELERATION, "2");
		
		PolarCoordinate p1 = new PolarCoordinate (48.00000, 13.00000, 440);
		PolarCoordinate p2 = new PolarCoordinate (48.00000, 13.00001, 440);
		PolarCoordinate p3 = new PolarCoordinate (48.00001, 13.00002, 440);
		PolarCoordinate p4 = new PolarCoordinate (48.00001, 13.00003, 440);
		
		sections = new Section[4];
		sections[0] = new Section (p1, 0, p2, 0, 10000);
		sections[1] = new Section (p2, 0, p3, 0,  5000);
		sections[2] = new Section (p3, 0, p4, 0, 15000);
		sections[3] = new Section (p4, 0, null, 0, 0);
		
		geodeticSystem = new WGS84 ();
	}
	
	/**
	 * Verify the correct function of the <code>planCourse</code> method.
	 */
	public void testCase01 () {
		try {
			AdvancedCoursePlanner planner = new AdvancedCoursePlanner (props);
			assertNotNull (planner);
			
			SectionFlightPlan[] plans = planner.planCourse (sections, geodeticSystem);
			assertNotNull (plans);
			assertEquals (sections.length, plans.length);
			
			for (int k=0; k < plans.length; k++) {
				System.out.println ("AdvancedCoursePlannerTestCase.testCase01: k="+k);
				PolarCoordinate p = plans[k].getScheduledPosition(0, geodeticSystem);
				assertEquals ("Start position section "+k+" latitude:", sections[k].getStartPosition().latitude, p.latitude, 1E-8);
				assertEquals ("Start position section "+k+" longitude:", sections[k].getStartPosition().longitude, p.longitude, 1E-8);
				assertEquals ("Start position section "+k+" altitude:", sections[k].getStartPosition().altitude, p.altitude, 1E-4);
				
				p = plans[k].getScheduledPosition(sections[k].getTravelTime(), geodeticSystem);
				if (sections[k].getEndPosition() == null) {
					assertEquals ("End position section "+k+" latitude:", sections[k].getStartPosition().latitude, p.latitude, 1E-8);
					assertEquals ("End position section "+k+" longitude:", sections[k].getStartPosition().longitude, p.longitude, 1E-8);
					assertEquals ("End position section "+k+" altitude:", sections[k].getStartPosition().altitude, p.altitude, 1E-4);
				} else {
					assertEquals ("End position section "+k+" latitude:", sections[k].getEndPosition().latitude, p.latitude, 1E-8);
					assertEquals ("End position section "+k+" longitude:", sections[k].getEndPosition().longitude, p.longitude, 1E-8);
					assertEquals ("End position section "+k+" altitude:", sections[k].getEndPosition().altitude, p.altitude, 1E-4);
				}
			}
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that the <code>AdvancedCoursePlanner</code> throws a
	 * <code>ConfigurationException</code> if the
	 * <code>maximum.allowed.acceleration</code> is empty.
	 */
	public void testCase02 () {
		props.setProperty (AdvancedCoursePlanner.PROP_MAXIMUM_ALLOWED_ACCELERATION, "");
		try {
			AdvancedCoursePlanner planner = new AdvancedCoursePlanner (props);
			assertNull (planner);
		} catch (ConfigurationException e) {
			assertEquals ("Please set the property maximum.allowed.acceleration", e.getMessage());
		}
	}

	/**
	 * Verify that the <code>AdvancedCoursePlanner</code> throws a
	 * <code>ConfigurationException</code> if the
	 * <code>maximum.allowed.acceleration</code> is not set.
	 */
	public void testCase03 () {
		props.remove (AdvancedCoursePlanner.PROP_MAXIMUM_ALLOWED_ACCELERATION);
		try {
			AdvancedCoursePlanner planner = new AdvancedCoursePlanner (props);
			assertNull (planner);
		} catch (ConfigurationException e) {
			assertEquals ("Please set the property maximum.allowed.acceleration", e.getMessage());
		}
	}

	/**
	 * Verify that the <code>AdvancedCoursePlanner</code> throws a
	 * <code>ConfigurationException</code> if the set course can not be flown in
	 * time.
	 */
	public void testCase04 () {
		try {
			AdvancedCoursePlanner planner = new AdvancedCoursePlanner (props);
			assertNotNull (planner);
			
			PolarCoordinate p1 = new PolarCoordinate (48.00000, 13.00000, 440);
			PolarCoordinate p2 = new PolarCoordinate (48.00000, 13.00001, 440);
			
			Section[] sections = new Section[1];
			sections[0] = new Section (p1, 0, p2, 0, 1000);
			
			SectionFlightPlan[] plans = planner.planCourse (sections, geodeticSystem);
			assertNull (plans);
			
		} catch (ConfigurationException e) {
			assertEquals ("Can not estimate flight plan! Section=0, v1=0.0, v3=0.0, x=1.0, y=-1.0, v2=-1.0", e.getMessage());
		}
	}
	
	/**
	 * Verify the correct implementation of the <code>estimateV2()</code> method.
	 */
	public void testCase06 () {
		Properties props = new Properties ();
		props.setProperty (AdvancedCoursePlanner.PROP_MAXIMUM_ALLOWED_ACCELERATION, "1");
		try {
			AdvancedCoursePlanner planner = new AdvancedCoursePlanner (props);
			assertNotNull (planner);
			double v1 = 0.5;
			double v3 = 0.5;
			double x = -1;
			double y = 1;
			double time = 1;
			double distance = 1;
			double v2 = planner.estimateV2 (v1, v3, x, y, time, distance);
			System.out.println ("testCase06 v2=" + v2);
			assertTrue (v2 >= 0);
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
}
