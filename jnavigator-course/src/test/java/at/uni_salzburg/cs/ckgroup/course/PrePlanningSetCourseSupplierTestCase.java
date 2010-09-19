/*
 * @(#) PrePlanningSetCourseSupplierTestCase.java
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

import java.io.InputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.NotImplementedException;

import junit.framework.TestCase;

/**
 * This tests verify the correct implementation of the
 * <code>PrePlanningSetCourseSupplier</code> class.
 * 
 * @author Clemens Krainer
 */
public class PrePlanningSetCourseSupplierTestCase extends TestCase {
	
	final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/PrePlanningSetCourseSupplierTest/setcourse.dat";

	Properties props;
	
	public void setUp () {
		props = new Properties ();
		props.setProperty (PrePlanningSetCourseSupplier.PROP_GEODETIC_SYSTEM_PREFIX + "className", "at.uni_salzburg.cs.ckgroup.course.WGS84");
		props.setProperty (PrePlanningSetCourseSupplier.PROP_COURSE_PLANNER_PREFIX + "className", "at.uni_salzburg.cs.ckgroup.course.TestCoursePlanner");
		props.setProperty (PrePlanningSetCourseSupplier.PROP_DATA_FILE_NAME + "className", "at.uni_salzburg.cs.ckgroup.course.WGS84");
	}
	
	/**
	 * Verify the <code>Properties</code> constructor and the
	 * <code>getSetCoursePosition()</code> method.
	 */
	public void testCase01 () {
		
		try {
			PrePlanningSetCourseSupplier ppscs = new PrePlanningSetCourseSupplier (props);
			assertNotNull (ppscs);
			
			InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
			assertNotNull (courseDataStream);
			
			ppscs.loadSetCourse (courseDataStream);
			
			VehicleStatus vst = ppscs.getSetCoursePosition (80001);
			System.out.println ("testCase02, 2: " + vst);
			assertEquals (48.00000, vst.position.latitude, 1E-9);
			assertEquals (13.00004, vst.position.longitude, 1E-9);
			assertEquals (     447, vst.position.altitude, 1E-4);
			assertEquals (      90, vst.orientation, 1E-9);
		} catch (Throwable t) {
			t.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * Verify the <code>IGeodeticSystem</code>, <code>ICoursePlanner</code>
	 * constructor and the <code>getSetCoursePosition()</code> method.
	 */
	public void testCase02 () {
		Properties props = new Properties ();
		IGeodeticSystem geodeticSystem = new WGS84 ();
		
		try {
			ICoursePlanner coursePlanner = new TestCoursePlanner (props);
			PrePlanningSetCourseSupplier ppscs = new PrePlanningSetCourseSupplier (geodeticSystem, coursePlanner);
			assertNotNull (ppscs);
			assertEquals (geodeticSystem, ppscs.getGeodeticSystem());
			
			InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
			assertNotNull (courseDataStream);
			
			ppscs.loadSetCourse (courseDataStream);
			VehicleStatus vst = ppscs.getSetCoursePosition (0);
			System.out.println ("testCase02, 1: " + vst);
			assertEquals (48.00000, vst.position.latitude, 1E-9);
			assertEquals (13.00000, vst.position.longitude, 1E-9);
			assertEquals (     440, vst.position.altitude, 1E-4);
			assertEquals (       0, vst.orientation, 1E-9);
			
			vst = ppscs.getSetCoursePosition (80001);
			System.out.println ("testCase02, 2: " + vst);
			assertEquals (48.00000, vst.position.latitude, 1E-9);
			assertEquals (13.00004, vst.position.longitude, 1E-9);
			assertEquals (     447, vst.position.altitude, 1E-4);
			assertEquals (      90, vst.orientation, 1E-9);
			
			vst = ppscs.getSetCoursePosition (80000);
			System.out.println ("testCase02, 3: " + vst);
			assertEquals (48.00000, vst.position.latitude, 1E-9);
			assertEquals (13.00004, vst.position.longitude, 1E-9);
			assertEquals (     447, vst.position.altitude, 1E-4);
			assertEquals (      90, vst.orientation, 1E-9);
			
			vst = ppscs.getSetCoursePosition (60001);
			System.out.println ("testCase02, 4: " + vst);
			assertEquals (48.00000, vst.position.latitude, 1E-9);
			assertEquals (13.00003, vst.position.longitude, 1E-9);
			assertEquals (     446, vst.position.altitude, 1E-4);
			assertEquals (       0, vst.orientation, 1E-9);

			vst = ppscs.getSetCoursePosition (10000000);
			System.out.println ("testCase02, 5: " + vst);
			assertEquals (48.00000, vst.position.latitude, 1E-9);
			assertEquals (13.00004, vst.position.longitude, 1E-9);
			assertEquals (     447, vst.position.altitude, 1E-4);
			assertEquals (     270, vst.orientation, 1E-9);
			
		} catch (Throwable t) {
			t.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * Verify that the <code>getSetCourseData()</code> throws a <code>NotImplementedException</code>.
	 */
	public void testCase03 () {
		
		try {
			PrePlanningSetCourseSupplier ppscs = new PrePlanningSetCourseSupplier (props);
			assertNotNull (ppscs);
			
			InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
			assertNotNull (courseDataStream);
			
			ppscs.loadSetCourse (courseDataStream);
			
			try {
				VehicleStatus[] vst = ppscs.getSetCourseData ();
				assertNull (vst);
			} catch (NotImplementedException e) {
				// Intentionally empty.
			} 
		} catch (Throwable t) {
			t.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * Verify that the <code>getTimeTable()</code> throws a <code>NotImplementedException</code>.
	 */
	public void testCase04 () {
		
		try {
			PrePlanningSetCourseSupplier ppscs = new PrePlanningSetCourseSupplier (props);
			assertNotNull (ppscs);
			
			InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
			assertNotNull (courseDataStream);
			
			ppscs.loadSetCourse (courseDataStream);
			
			try {
				long[] tt = ppscs.getTimeTable ();
				assertNull (tt);
			} catch (NotImplementedException e) {
				// Intentionally empty.
			} 
		} catch (Throwable t) {
			t.printStackTrace ();
			fail ();
		}
	}
}
