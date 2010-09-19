/*
 * @(#) SimpleSetCourseSupplierTestCase.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

import junit.framework.TestCase;

/**
 * This test verifies the correct implementation of the SimpleSetCourseSupplier class.
 * 
 * @author Clemens Krainer
 */
public class SimpleSetCourseSupplierTestCase extends TestCase
{
	/**
	 * Test the proper implementation of the <code>getSetCoursePosition()</code> method. See also <code>testCase08()</code>.
	 */
	public void testCase01 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/setcourse.dat";
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (SET_COURSE_DATA_FILE_NAME);
		assertNotNull ("Can not find file " + SET_COURSE_DATA_FILE_NAME, url);
		
		File inputDataFile = new File (url.getFile ());
		assertTrue ("Can not read file " + SET_COURSE_DATA_FILE_NAME, inputDataFile.canRead ());
		
		try
		{
			InputStream courseDataStream = new FileInputStream (inputDataFile);
			assertNotNull (courseDataStream);
		
			IGeodeticSystem geodeticSystem = new WGS84 ();

			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (courseDataStream, geodeticSystem);
			assertEquals (geodeticSystem, scs.getGeodeticSystem());
			
			VehicleStatus st = scs.getSetCoursePosition (0);
			System.out.println ("     0: " + st.toString ());
			
			st = scs.getSetCoursePosition (5000);
			System.out.println ("  5000: " + st.toString ());
			
			st = scs.getSetCoursePosition (10000);
			System.out.println (" 10000: " + st.toString ());
			
			st = scs.getSetCoursePosition (20000);
			System.out.println (" 20000: " + st.toString ());
			
			st = scs.getSetCoursePosition (30000);
			System.out.println (" 30000: " + st.toString ());

			st = scs.getSetCoursePosition (40000);
			System.out.println (" 40000: " + st.toString ());
			
			st = scs.getSetCoursePosition (50000);
			System.out.println (" 50000: " + st.toString ());
			
			st = scs.getSetCoursePosition (60000);
			System.out.println (" 60000: " + st.toString ());
			
			st = scs.getSetCoursePosition (70000);
			System.out.println (" 70000: " + st.toString ());

			st = scs.getSetCoursePosition (80000);
			System.out.println (" 80000: " + st.toString ());

			st = scs.getSetCoursePosition (90000);
			System.out.println (" 90000: " + st.toString ());

			st = scs.getSetCoursePosition (100000);
			System.out.println ("100000: " + st.toString ());

			st = scs.getSetCoursePosition (110000);
			System.out.println ("110000: " + st.toString ());

			st = scs.getSetCoursePosition (120000);
			System.out.println ("120000: " + st.toString ());
			
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SimpleSetCourseSupplier</code> implementation
	 * recognizes malformed course data lines.
	 */
	public void testCase02 () {

		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/broken-setcourse1.dat";
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (SET_COURSE_DATA_FILE_NAME);
		assertNotNull ("Can not find file " + SET_COURSE_DATA_FILE_NAME, url);
		
		File inputDataFile = new File (url.getFile ());
		assertTrue ("Can not read file " + SET_COURSE_DATA_FILE_NAME, inputDataFile.canRead ());
		
		try
		{
			InputStream courseDataStream = new FileInputStream (inputDataFile);
			assertNotNull (courseDataStream);
		
			IGeodeticSystem geodeticSystem = new WGS84 ();

			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (courseDataStream, geodeticSystem);
			assertNull (scs);
		} catch (ConfigurationException e) {
			assertEquals ("Invalid course data in line 5",e.getMessage ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SimpleSetCourseSupplier</code> implementation
	 * recognizes negative or zero duration values in the course data lines.
	 */
	public void testCase03 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/broken-setcourse2.dat";
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (SET_COURSE_DATA_FILE_NAME);
		assertNotNull ("Can not find file " + SET_COURSE_DATA_FILE_NAME, url);
		
		File inputDataFile = new File (url.getFile ());
		assertTrue ("Can not read file " + SET_COURSE_DATA_FILE_NAME, inputDataFile.canRead ());
		
		try
		{
			InputStream courseDataStream = new FileInputStream (inputDataFile);
			assertNotNull (courseDataStream);
		
			IGeodeticSystem geodeticSystem = new WGS84 ();

			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (courseDataStream, geodeticSystem);
			assertNull (scs);
		} catch (ConfigurationException e) {
			assertEquals ("Negative or zero duration values are not allowed. Error in line 7",e.getMessage ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SimpleSetCourseSupplier</code> implementation
	 * recognizes invalid orientation values in the course data lines.
	 */
	public void testCase04 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/broken-setcourse3.dat";
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (SET_COURSE_DATA_FILE_NAME);
		assertNotNull ("Can not find file " + SET_COURSE_DATA_FILE_NAME, url);
		
		File inputDataFile = new File (url.getFile ());
		assertTrue ("Can not read file " + SET_COURSE_DATA_FILE_NAME, inputDataFile.canRead ());
		
		try
		{
			InputStream courseDataStream = new FileInputStream (inputDataFile);
			assertNotNull (courseDataStream);
		
			IGeodeticSystem geodeticSystem = new WGS84 ();

			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (courseDataStream, geodeticSystem);
			assertNull (scs);
		} catch (ConfigurationException e) {
			assertEquals ("Invalid orientation in line 8 only values between 0 and 360 degrees are allowed!",e.getMessage ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SimpleSetCourseSupplier</code> implementation
	 * recognizes invalid orientation values in the course data lines.
	 */
	public void testCase05 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/broken-setcourse4.dat";
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (SET_COURSE_DATA_FILE_NAME);
		assertNotNull ("Can not find file " + SET_COURSE_DATA_FILE_NAME, url);
		
		File inputDataFile = new File (url.getFile ());
		assertTrue ("Can not read file " + SET_COURSE_DATA_FILE_NAME, inputDataFile.canRead ());
		
		try
		{
			InputStream courseDataStream = new FileInputStream (inputDataFile);
			assertNotNull (courseDataStream);
		
			IGeodeticSystem geodeticSystem = new WGS84 ();

			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (courseDataStream, geodeticSystem);
			assertNull (scs);
		} catch (ConfigurationException e) {
			assertEquals ("Invalid orientation in line 8 only values between 0 and 360 degrees are allowed!",e.getMessage ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the <code>SimpleSetCourseSupplier</code> implementation
	 * recognizes if no course data is available at all.
	 */
	public void testCase06 () {
		
		final String SET_COURSE_DATA_FILE_NAME = "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/broken-setcourse5.dat";
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (SET_COURSE_DATA_FILE_NAME);
		assertNotNull ("Can not find file " + SET_COURSE_DATA_FILE_NAME, url);
		
		File inputDataFile = new File (url.getFile ());
		assertTrue ("Can not read file " + SET_COURSE_DATA_FILE_NAME, inputDataFile.canRead ());
		
		try
		{
			InputStream courseDataStream = new FileInputStream (inputDataFile);
			assertNotNull (courseDataStream);
		
			IGeodeticSystem geodeticSystem = new WGS84 ();

			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (courseDataStream, geodeticSystem);
			assertNull (scs);
		} catch (ConfigurationException e) {
			assertEquals ("The number of vehicle states is zero.",e.getMessage ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}	
	}
	
	/**
	 * Test the proper implementation of the <code>getSetCoursePosition()</code>
	 * method. This test performs the same checks as <code>testCase01()</code>
	 * but uses the <code>Properties</code> constructor.
	 */
	public void testCase08 () {
		
		Properties props = new Properties ();
		props.setProperty (SimpleSetCourseSupplier.PROP_DATA_FILE_NAME, "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/setcourse.dat");
		props.setProperty (SimpleSetCourseSupplier.PROP_GEODETIC_SYSTEM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.course.WGS84");
		
		try
		{
			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (props);
			VehicleStatus st = scs.getSetCoursePosition (0);
			System.out.println ("     0: " + st.toString ());
			
			st = scs.getSetCoursePosition (5000);
			System.out.println ("  5000: " + st.toString ());
			
			st = scs.getSetCoursePosition (10000);
			System.out.println (" 10000: " + st.toString ());
			
			st = scs.getSetCoursePosition (20000);
			System.out.println (" 20000: " + st.toString ());
			
			st = scs.getSetCoursePosition (30000);
			System.out.println (" 30000: " + st.toString ());

			st = scs.getSetCoursePosition (40000);
			System.out.println (" 40000: " + st.toString ());
			
			st = scs.getSetCoursePosition (50000);
			System.out.println (" 50000: " + st.toString ());
			
			st = scs.getSetCoursePosition (60000);
			System.out.println (" 60000: " + st.toString ());
			
			st = scs.getSetCoursePosition (70000);
			System.out.println (" 70000: " + st.toString ());

			st = scs.getSetCoursePosition (80000);
			System.out.println (" 80000: " + st.toString ());

			st = scs.getSetCoursePosition (90000);
			System.out.println (" 90000: " + st.toString ());

			st = scs.getSetCoursePosition (100000);
			System.out.println ("100000: " + st.toString ());

			st = scs.getSetCoursePosition (110000);
			System.out.println ("110000: " + st.toString ());

			st = scs.getSetCoursePosition (120000);
			System.out.println ("120000: " + st.toString ());
			
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that the <code>Properties</code> constructor recognises a non existent property file.
	 */
	public void testCase09 () {
		String fileName = "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/nonexistent-setcourse.dat";
		
		Properties props = new Properties ();
		props.setProperty (SimpleSetCourseSupplier.PROP_DATA_FILE_NAME, fileName);
		props.setProperty (SimpleSetCourseSupplier.PROP_GEODETIC_SYSTEM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.course.WGS84");
		
		try
		{
			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (props);
			assertNull (scs);
		}
		catch (ConfigurationException e) {
			assertEquals ("Can not find course data file " + fileName, e.getMessage ());
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	public void testCase10 () {
		
		Properties props = new Properties ();
		props.setProperty (SimpleSetCourseSupplier.PROP_DATA_FILE_NAME, "at/uni_salzburg/cs/ckgroup/course/SimpleSetCourseSupplierTest/setcourse.dat");
		props.setProperty (SimpleSetCourseSupplier.PROP_GEODETIC_SYSTEM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.course.WGS84");
		
		try
		{
			SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (props);
			
			VehicleStatus[] vst = scs.getSetCourseData ();
			
			assertEquals ( 48.0,                 vst[0].position.latitude,  1E-9);
			assertEquals ( 13.0,                 vst[0].position.longitude, 1E-9);
			assertEquals (440.0,                 vst[0].position.altitude,  1E-9);
			assertEquals (  0.0,                 vst[0].orientation,  1E-9);
			assertEquals ( 41.96494866989823,    vst[0].elevation,  1E-9);
			assertEquals (  0.1495493151051871,  vst[0].totalSpeed,  1E-9);
			assertEquals (  0.0,                 vst[0].courseOverGround,  1E-9);

			assertEquals ( 48.00001,             vst[1].position.latitude,  1E-9);
			assertEquals ( 13.0,                 vst[1].position.longitude, 1E-9);
			assertEquals (441.0,                 vst[1].position.altitude,  1E-9);
			assertEquals (  0.0,                 vst[1].orientation,  1E-9);
			assertEquals ( 41.964944201615644,   vst[1].elevation,  1E-9);
			assertEquals (  0.14954932809653934, vst[1].totalSpeed,  1E-9);
			assertEquals (  0.0,                 vst[1].courseOverGround,  1E-9);

			assertEquals ( 48.00002,             vst[2].position.latitude,  1E-9);
			assertEquals ( 13.0,                 vst[2].position.longitude, 1E-9);
			assertEquals (442.0,                 vst[2].position.altitude,  1E-9);
			assertEquals (  0.0,                 vst[2].orientation,  1E-9);
			assertEquals ( 41.96493969951983,    vst[2].elevation,  1E-9);
			assertEquals (  0.14954934119088398, vst[2].totalSpeed,  1E-9);
			assertEquals (  0.0,                 vst[2].courseOverGround,  1E-9);

			assertEquals ( 48.00003,             vst[3].position.latitude,  1E-9);
			assertEquals ( 13.0,                 vst[3].position.longitude, 1E-9);
			assertEquals (443.0,                 vst[3].position.altitude,  1E-9);
			assertEquals (  0.0,                 vst[3].orientation,  1E-9);
			assertEquals ( 36.74909392864542,    vst[3].elevation,  1E-9);
			assertEquals (  0.16713683139052518, vst[3].totalSpeed,  1E-9);
			assertEquals (213.78768152999214,    vst[3].courseOverGround,  1E-9);

			assertEquals ( 48.00002,             vst[4].position.latitude,  1E-9);
			assertEquals ( 13.00001,             vst[4].position.longitude, 1E-9);
			assertEquals (444.0,                 vst[4].position.altitude,  1E-9);
			assertEquals (  0.0,                 vst[4].orientation,  1E-9);
//			assertEquals ( 36.74908799525913,    vst[4].elevation,  1E-9);
			assertEquals ( 36.74908799261158,    vst[4].elevation,  1E-8);
			assertEquals (  0.16713685458809158, vst[4].totalSpeed,  1E-9);
			assertEquals (213.78768668196292,    vst[4].courseOverGround,  1E-9);

			assertEquals ( 48.00001,             vst[5].position.latitude,  1E-9);
			assertEquals ( 13.00002,             vst[5].position.longitude, 1E-9);
			assertEquals (445.0,                 vst[5].position.altitude,  1E-9);
			assertEquals (  0.0,                 vst[5].orientation,  1E-9);
//			assertEquals ( 36.749082022371795,   vst[5].elevation,  1E-9);
			assertEquals ( 36.74908202501934,    vst[5].elevation,  1E-8);
			assertEquals (  0.1671368777287054,  vst[5].totalSpeed,  1E-9);
			assertEquals (213.78769180100375,    vst[5].courseOverGround,  1E-9);

			assertEquals ( 48.0,                 vst[6].position.latitude,  1E-9);
			assertEquals ( 13.00003,             vst[6].position.longitude, 1E-9);
			assertEquals (446.0,                 vst[6].position.altitude,  1E-9);
			assertEquals (  0.0,                 vst[6].orientation,  1E-9);
//			assertEquals ( 53.26580752528333,    vst[6].elevation,  1E-9);
			assertEquals ( 53.26580751309053,    vst[6].elevation,  1E-7);
			assertEquals (  0.12477869082206262, vst[6].totalSpeed,  1E-9);
			assertEquals (270.0,                 vst[6].courseOverGround,  1E-9);

			
//			48.00000;13.00000;440;10000;0
//			48.00001;13.00000;441;10000;0
//			48.00002;13.00000;442;10000;0
//			48.00003;13.00000;443;10000;0
//			48.00002;13.00001;444;10000;0
//			48.00001;13.00002;445;10000;0
//			48.00000;13.00003;446;10000;0
//			48.00000;13.00004;447;10000;0
//			48.00000;13.00004;447;10000;90
//			48.00000;13.00004;447;10000;180
//			48.00000;13.00004;447;10000;270
//			48.00000;13.00004;447;10000;0
//			48.00000;13.00004;447;10000;270
//			
			long[] timeTable = scs.getTimeTable ();
			assertEquals (13, timeTable.length);
			
			assertEquals (10000, timeTable[0]);
			assertEquals (20000, timeTable[1]);
			assertEquals (30000, timeTable[2]);
			assertEquals (40000, timeTable[3]);
			assertEquals (50000, timeTable[4]);
			assertEquals (60000, timeTable[5]);
			assertEquals (70000, timeTable[6]);
			assertEquals (80000, timeTable[7]);
			assertEquals (90000, timeTable[8]);
			assertEquals (100000, timeTable[9]);
			assertEquals (110000, timeTable[10]);
			assertEquals (120000, timeTable[11]);
			assertEquals (130000, timeTable[12]);
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
}
