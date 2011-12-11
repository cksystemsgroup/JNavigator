/*
 * @(#) SimplePilotTestCase.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.SimpleSetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;

/**
 * This class verifies the implementation of the <code>SimplePilot</code> class.
 * 
 * @author Clemens Krainer
 */
public class SimplePilotTestCase extends TestCase
{
	/**
	 * Load the test specific properties.
	 * 
	 * @param props the path to the property file relative to a CLASSPATH folder.
	 * @return the <code>Properties</code>.
	 */
	public Properties loadProperties (String props) {
		URL propsUrl = Thread.currentThread ().getContextClassLoader ().getResource (props);
		
		File inputDataFile = new File (propsUrl.getFile ());
		Properties properties = new Properties ();
		
		try {
			properties.load (new FileInputStream(inputDataFile));
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		
		return properties;
	}
	
	/**
	 * Create a test specific set course supplier.
	 * 
	 * @param setCourseDataFileName the path of the set course data relative to a CLASSPATH folder.
	 * @return the set course supplier.
	 * @throws IOException thrown in case of errors when reading the set course data file.
	 * @throws ConfigurationException thrown in case of errors in the set course data.
	 */
	public ISetCourseSupplier createSetCourseSupplier (String setCourseDataFileName) throws IOException, ConfigurationException	
	{
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (setCourseDataFileName);
		assertNotNull ("Can not find file " + setCourseDataFileName, url);
		
		File inputDataFile = new File (url.getFile ());
		assertTrue ("Can not read file " + setCourseDataFileName, inputDataFile.canRead ());
		
		InputStream courseDataStream = new FileInputStream (inputDataFile);
		assertNotNull (courseDataStream);
	
		IGeodeticSystem geodeticSystem = new WGS84 ();

		SimpleSetCourseSupplier scs = new SimpleSetCourseSupplier (courseDataStream, geodeticSystem);
		return scs;
	}
	
	/**
	 * This test verifies the behavior of the <code>SimplePilot</code> if set
	 * course supplier and position provider are not set.
	 */
	public void testCase01 () {
		final String propsPath = "at/uni_salzburg/cs/ckgroup/pilot/SimplePilotTest/pilot.properties";
		
		Properties props = loadProperties (propsPath);
		IClock clock = new MyTestClock ();
		
		try
		{
			SimplePilot pilot = new SimplePilot (props);
			assertNotNull (pilot);
			
			pilot.setClock (clock);
			pilot.setCourseSupplier (null);
			pilot.setPositionProvider (null);
			
			HardWareSensorData sensorData = new HardWareSensorData (100, 111, 123, 231);
			FlightControlData fc = pilot.processSensorData (sensorData);
			
			assertEquals (100, fc.yaw, 1E-9);
			assertEquals (111, fc.roll, 1E-9);
			assertEquals (123, fc.pitch, 1E-9);
			assertEquals (231, fc.heightAboveGround, 1E-9);
			
			pilot.startFlyingSetCourse ();
			assertTrue (pilot.isFlyingSetCourse());
			fc = pilot.processSensorData (sensorData);
			fc = pilot.processSensorData (sensorData);
			
			assertEquals (100, fc.yaw, 1E-9);
			assertEquals (111, fc.roll, 1E-9);
			assertEquals (123, fc.pitch, 1E-9);
			assertEquals (231, fc.heightAboveGround, 1E-9);
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the behavior of the
	 * <class>PassThroughPilotInterceptor</class> in conjunction with the simple
	 * pilot.
	 */
	public void testCase02 () {
		final String propsPath = "at/uni_salzburg/cs/ckgroup/pilot/SimplePilotTest/pilot.properties";
		final String setCourseDataFileName = "at/uni_salzburg/cs/ckgroup/pilot/SimplePilotTest/setcourse.dat";
		final int MAX_STATES = 10;
		
		Properties props = loadProperties (propsPath);
//		IPilotInterceptor pilotInterceptor1 = new PassThroughPilotInterceptor (null);
//		IPilotInterceptor pilotInterceptor2 = new PassThroughPilotInterceptor (null);
		IClock clock = new MyTestClock ();
		
		long [] oldFlightTime = new long [MAX_STATES];
		long[] currentFlightTime = new long [MAX_STATES];
		VehicleStatus[] vehicleStatus = new VehicleStatus [MAX_STATES];
		FlightControlData[] flightControlData = new FlightControlData [MAX_STATES];
		HardWareSensorData[] sensorData = new HardWareSensorData [MAX_STATES];
		sensorData[0] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[1] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[2] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[3] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[4] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[5] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[6] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[7] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[8] = new HardWareSensorData (100, 111, 123, 231);
		sensorData[9] = new HardWareSensorData (100, 111, 123, 231);


		try
		{
			// TODO complete this.
			SimplePilot pilot = new SimplePilot (props);
			assertNotNull (pilot);
			pilot.setClock (clock);
//			pilot.addPilotInterceptor (pilotInterceptor1);
//			pilot.addPilotInterceptor (pilotInterceptor2);
//			pilot.removePilotInterceptor (pilotInterceptor1);
			
			ISetCourseSupplier scs = createSetCourseSupplier (setCourseDataFileName);
			pilot.setCourseSupplier (scs);
			
			MyPositionProvider posp = new MyPositionProvider ();
			posp.add (new PolarCoordinate (48.0, 13.0, 440));
			pilot.setPositionProvider (posp);
			
			pilot.startFlyingSetCourse ();
			
			int k = 0;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);
//			System.out.println ("testCase02(): 1");

			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 2");
			
			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 3");
			
			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 4");
			
			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 5");
			
			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 6");
			
			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 7");
			
			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 8");
			
			++k;
			try { Thread.sleep (500); } catch (Exception e) {}
			flightControlData[k] = pilot.processSensorData (sensorData[k]);
			oldFlightTime[k] = pilot.getOldFlightTime ();
			currentFlightTime[k] = pilot.getCurrentFlightTime ();
			vehicleStatus[k] = scs.getSetCoursePosition (currentFlightTime[k]);			
//			System.out.println ("testCase02(): 9");
			
			pilot.stopFlyingSetCourse ();
			
//			assertEquals (100, fc.yaw, 1E-9);
//			assertEquals (111, fc.roll, 1E-9);
//			assertEquals (123, fc.pitch, 1E-9);
//			assertEquals (231, fc.heightAboveGround, 1E-9);
//			
//			
//			fc = pilot.processSensorData (sensorData);
//			fc = pilot.processSensorData (sensorData);
//			
//			assertEquals (100, fc.yaw, 1E-9);
//			assertEquals (111, fc.roll, 1E-9);
//			assertEquals (123, fc.pitch, 1E-9);
//			assertEquals (231, fc.heightAboveGround, 1E-9);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	
	/**
	 * This class implements a mock position provider.
	 * 
	 * @author Clemens Krainer
	 */
	private class MyPositionProvider implements IPositionProvider
	{
		/**
		 * The set course positions as a <code>Vector</code>
		 */
		private Vector	positions = new Vector ();

		/**
		 * The current index of the next position to deliver.
		 */
		private int index = 0;
		
		/**
		 * The currently used geodetic system. 
		 */
		private IGeodeticSystem geodeticSystem = new WGS84();
		
		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
		 */
		public PolarCoordinate getCurrentPosition ()
		{
			if (positions.size () <= 0)
				return null;
			
			if (index < positions.size ())
				return (PolarCoordinate) positions.get (index++);
			
			return (PolarCoordinate) positions.get (positions.size () - 1);
		}

		/**
		 * This method is responsible for adding new positions.
		 *  
		 * @param coordinate a new position.
		 */
		public void add (PolarCoordinate coordinate)
		{
			positions.add (coordinate);
		}

		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCourseOverGround()
		 */
		public Double getCourseOverGround() {
			return null;
		}

		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getSpeedOverGround()
		 */
		public Double getSpeedOverGround() {
			return null;
		}

		public IGeodeticSystem getGeodeticSystem() {
			return geodeticSystem;
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
