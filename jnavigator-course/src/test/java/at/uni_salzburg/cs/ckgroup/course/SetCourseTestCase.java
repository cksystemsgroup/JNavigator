/*
 * @(#) SetCourseTestCase.java
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

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This test verifies the correct implementation of the <code>SetCourse</code> class.
 * 
 * @author Clemens Krainer
 */
public class SetCourseTestCase extends TestCase {

	/**
	 * Test loading a correct set course.
	 */
	public void testCase01 () {
		
		double[] expectedLatitude = { 48.00000, 48.00001, 48.00002, 48.00003, 48.00002, 48.00001, 48.00000, 48.00000, 48.00000, 48.00000, 48.00000, 48.00000, 48.00000 };
		double[] expectedLongitude = { 13.00000, 13.00000, 13.00000, 13.00000, 13.00001, 13.00002, 13.00003, 13.00004, 13.00004, 13.00004, 13.00004, 13.00004, 13.00004 };
		double[] expectedAltitude = { 440, 441, 442, 443, 444, 445, 446, 447, 447, 447, 447, 447, 447 };
		long[]   expectedTravelTime = { 10000, 10000, 10000, 10000, 10002, 10004, 10009, 10000, 10001, 10080, 10000, 10000, 10000 };
		double[] expectedOrientation = { 0, 0, 0, 0, 0, 0, 0, 0, 90, 180, 270, 0, 270 };
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SetCourseTest/setcourse.dat";
		
		InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
		assertNotNull (courseDataStream);
		
		try {
			Section[] sections = SetCourse.loadSetCourse (courseDataStream);
			assertEquals ("Number of sections", expectedLatitude.length, sections.length);
			
			for (int k=0; k < expectedLatitude.length; k++) {
				
				assertEquals ("start point Latitude    "+k, expectedLatitude[k],    sections[k].getStartPosition ().latitude, 1E-9);
				assertEquals ("start point Longitude   "+k, expectedLongitude[k],   sections[k].getStartPosition ().longitude, 1E-9);
				assertEquals ("start point Altitude    "+k, expectedAltitude[k],    sections[k].getStartPosition ().altitude, 1E-9);
				assertEquals ("start point Orientation "+k, expectedOrientation[k], sections[k].getStartOrientation(), 1E-9);
				
				if (k+1 < expectedLatitude.length) {
					assertEquals ("end point Latitude    "+k, expectedLatitude[k+1],    sections[k].getEndPosition ().latitude, 1E-9);
					assertEquals ("end point Longitude   "+k, expectedLongitude[k+1],   sections[k].getEndPosition ().longitude, 1E-9);
					assertEquals ("end point Altitude    "+k, expectedAltitude[k+1],    sections[k].getEndPosition ().altitude, 1E-9);
					assertEquals ("end point Orientation "+k, expectedOrientation[k+1], sections[k].getEndOrientation(), 1E-9);
				} else {
					assertNull   ("end point Position    "+k, sections[k].getEndPosition ());
					assertEquals ("end point Orientation "+k, expectedOrientation[k], sections[k].getEndOrientation(), 1E-9);
				}
				
				assertEquals ("Travel time "+k, expectedTravelTime[k], sections[k].getTravelTime());
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SetCourse</code> implementation recognizes malformed
	 * course data lines.
	 */
	public void testCase02 () {

		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SetCourseTest/broken-setcourse1.dat";
		InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
		assertNotNull (courseDataStream);
		
		try {
			Section[] sections = SetCourse.loadSetCourse (courseDataStream);
			assertNull (sections);
		} catch (ConfigurationException e) {
			assertEquals ("Invalid course data in line 4",e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SetCourser</code> implementation recognizes negative
	 * or zero duration values in the course data lines.
	 */
	public void testCase03 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SetCourseTest/broken-setcourse2.dat";
		InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
		assertNotNull (courseDataStream);
		
		try {
			Section[] sections = SetCourse.loadSetCourse (courseDataStream);
			assertNull (sections);
		} catch (ConfigurationException e) {
			assertEquals ("Negative or zero duration values are not allowed. Error in line 7",e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SetCourse</code> implementation recognizes invalid
	 * orientation values in the course data lines.
	 */
	public void testCase04 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SetCourseTest/broken-setcourse3.dat";
		InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
		assertNotNull (courseDataStream);
		
		try {
			Section[] sections = SetCourse.loadSetCourse (courseDataStream);
			assertNull (sections);
		} catch (ConfigurationException e) {
			assertEquals ("Invalid orientation in line 7 only values between 0 and 360 degrees are allowed!",e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SetCourse</code> implementation recognizes invalid
	 * orientation values in the course data lines.
	 */
	public void testCase05 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SetCourseTest/broken-setcourse4.dat";
		InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
		assertNotNull (courseDataStream);
		
		try {
			Section[] sections = SetCourse.loadSetCourse (courseDataStream);
			assertNull (sections);
		} catch (ConfigurationException e) {
			assertEquals ("Invalid orientation in line 7 only values between 0 and 360 degrees are allowed!",e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SetCourse</code> implementation recognizes if no
	 * course data is available at all.
	 */
	public void testCase06 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SetCourseTest/broken-setcourse5.dat";
		InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
		assertNotNull (courseDataStream);
		
		try {
			SetCourse sc = new SetCourse ();
			assertNotNull (sc);
			Section[] sections = SetCourse.loadSetCourse (courseDataStream);
			assertNull (sections);
		} catch (ConfigurationException e) {
			assertEquals ("The number of vehicle states is zero.",e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}	
	}
	
	/**
	 * Verify if the <code>SetCourse</code> implementation recognizes if no
	 * course data is available at all.
	 */
	public void testCase07 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SetCourseTest/broken-setcourse6.dat";
		InputStream courseDataStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (SET_COURSE_DATA_FILE_NAME);
		assertNotNull (courseDataStream);
		
		try {
			SetCourse sc = new SetCourse ();
			assertNotNull (sc);
			Section[] sections = SetCourse.loadSetCourse (courseDataStream);
			assertNull (sections);
		} catch (ConfigurationException e) {
			assertEquals ("Negative or zero duration values are not allowed. Error in line 10",e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}	
	}
}
