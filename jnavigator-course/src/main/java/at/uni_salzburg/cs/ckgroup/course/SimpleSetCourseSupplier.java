/*
 * @(#) SimpleSetCourseSupplier.java
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.Properties;
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.StringUtils;

/**
 * This class implements a simple set course supplier. It load set course data,
 * containing latitude, longitude, altitude, running time and vehicle
 * orientation from an InputStream. Additionally, it estimates distance, speed,
 * course and elevation angle values consistent with the course data. A user of
 * this class may query a required VehicleStatus to a given point in time.
 * 
 * @author Clemens Krainer
 */
public class SimpleSetCourseSupplier implements ISetCourseSupplier
{
	/**
	 * Constant for the set course data file name property key. 
	 */
	public static final String PROP_DATA_FILE_NAME = "data.fileName";
	
	/**
	 * Constant for the geodetic system's class name.
	 */
	public static final String PROP_GEODETIC_SYSTEM_PREFIX = "geodetic.system.";
	
	/**
	 * The required states containing WGS84 position total speed, course over
	 * ground, elevation and orientation of the vehicle.
	 * 
	 * @uml.property name="vehicleStates"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	private VehicleStatus[] vehicleStates;
	
	/**
	 * The duration in milliseconds when the next state must be reached.
	 */
	private long[] durations;
	
	/**
	 * The geodetic system to be used for calculations. 
	 */
	private IGeodeticSystem geodeticSystem;
	
	/**
	 * The last set course index.
	 */
	private int lastIndex = -1;
	
	/**
	 * Construct a simple set course supplier from provided properties.
	 * 
	 * @param props the properties to be used.
	 * @throws ConfigurationException thrown in case of an configuration error.
	 * @throws IOException thrown in case of I/O errors when loading the set
	 *             course from file.
	 */
	public SimpleSetCourseSupplier (Properties props) throws ConfigurationException, IOException {
		init (props);
	}
	
	/**
	 * Initialise the set course and construct the geodetic system according to
	 * the provided properties.
	 * 
	 * @param props the properties to be used.
	 * @throws ConfigurationException thrown in case of configuration errors.
	 * @throws IOException thrown in case of I/O errors when loading the set
	 *             course from file.
	 */
	private void init (Properties props) throws ConfigurationException, IOException
	{
		geodeticSystem = (IGeodeticSystem) ObjectFactory.getInstance ().instantiateObject (
				PROP_GEODETIC_SYSTEM_PREFIX, IGeodeticSystem.class, props);

		String dataFileName = props.getProperty (PROP_DATA_FILE_NAME);
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (dataFileName);
		if (url == null)
			throw new ConfigurationException ("Can not find course data file " + dataFileName);
		
		File inputDataFile = new File (url.getFile ());
		
		loadSetCourse (new FileInputStream (inputDataFile));
	}
	
	/**
	 * Construct a simple set course supplier.
	 * 
	 * @param courseData the InputStream containing the course data.
	 * @param geodeticSystem the geodetic system to be used for calculations.
	 * @throws ConfigurationException thrown if no course data is available or
	 *         if negative duration values have been used.
	 * @throws IOException thrown in case of IO errors.
	 */
	public SimpleSetCourseSupplier (InputStream courseData, IGeodeticSystem geodeticSystem)
			throws ConfigurationException, IOException
	{
		this.geodeticSystem = geodeticSystem;
		loadSetCourse (courseData);
	}
	
	/**
	 * Load the course data from a given InputStream. This load procedure
	 * interprets the course data line by line. A line starting with '#' is
	 * considered as a comment and therefore skipped. Empty lines are ignored.
	 * Every other line must contain the values for latitude, longitude,
	 * altitude, duration and orientation. Semicolons (';') separate the fields
	 * from each other.
	 * 
	 * @param courseData the InputStream containing the course data.
	 * @throws ConfigurationException thrown if no course data is available or
	 *         the available data is invalid.
	 * @throws IOException thrown in case of IO errors.
	 */
	public void loadSetCourse (InputStream courseData)
			throws ConfigurationException, IOException
	{		
		LineNumberReader reader = new LineNumberReader (new InputStreamReader (courseData));
		String line;
		Vector<VehicleStatus> states = new Vector<>();
		Vector<Long> timeSpans = new Vector<>();
		
		while ((line = reader.readLine()) != null) {
			
			if (line.matches ("\\s*#.*") || line.matches ("\\s*"))
				continue;
			
//			String[] x = line.split (";");
			String[] x = StringUtils.splitOnCharAndTrim(';',line);
			if (x.length < 5)
				throw new ConfigurationException ("Invalid course data in line " + reader.getLineNumber ());
			
			PolarCoordinate position = new PolarCoordinate (Double.parseDouble (x[0]), Double.parseDouble (x[1]), Double.parseDouble (x[2]) );
			
			long duration = Long.parseLong (x[3]);
			if (duration <= 0)
				throw new ConfigurationException ("Negative or zero duration values are not allowed. Error in line " + reader.getLineNumber ());
			
			double orientation = Double.parseDouble (x[4]);
			if (orientation < 0 || orientation > 360)
				throw new ConfigurationException ("Invalid orientation in line " + reader.getLineNumber () + " only values between 0 and 360 degrees are allowed!");

			states.add (new VehicleStatus (position, 0, 0, 0, orientation));
			timeSpans.add (Long.valueOf(duration));
		} 
		
		if (states.size () == 0)
			throw new ConfigurationException ("The number of vehicle states is zero.");
		
		vehicleStates = new VehicleStatus [states.size ()];
		durations = new long [states.size ()];
		for (int k=0; k < states.size (); k++) {
			vehicleStates[k] = (VehicleStatus)states.get(k);
			durations[k] = ((Long)timeSpans.get (k)).longValue ();
		}
		
		calculateCourseDetails ();
	}

	/**
	 * Calculate the speed, elevation angle and course between the nodes of the
	 * set course data.
	 */
	private void calculateCourseDetails () {
		
		double oldCourse = 0;
	
		for (int k=0; k < vehicleStates.length-1; k++) {
			VehicleStatus currentState = vehicleStates [k];
			VehicleStatus nextState = vehicleStates [k+1];
			
			CourseData courseData = geodeticSystem.calculateSpeedAndCourse (currentState.position, nextState.position, durations[k]);
	
			vehicleStates [k].totalSpeed = courseData.speed;
			vehicleStates [k].elevation = courseData.elevation;
			if (courseData.courseIsValid) {
				vehicleStates [k].courseOverGround = courseData.course;
				oldCourse = courseData.course;
			} else
				vehicleStates [k].courseOverGround = oldCourse;
		}
		
		vehicleStates [vehicleStates.length-1].courseOverGround = oldCourse;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier#getSetCoursePosition(long)
	 */
	public VehicleStatus getSetCoursePosition (long time) {

		VehicleStatus oldStatus = vehicleStates[0];
		double oldTimeStamp = 0;
		VehicleStatus newStatus = vehicleStates[1];
		double newTimeStamp = durations [0];
				
		int k = 1;
		while (newTimeStamp < time && k+1 < vehicleStates.length) {
			oldStatus = newStatus;
			oldTimeStamp = newTimeStamp;
			newTimeStamp += durations [k++];
			newStatus = vehicleStates[k];
		}
		
		if (k != lastIndex) {
			lastIndex = k;
			System.out.println ("Setcourse: " + newStatus);
		}
		
		CartesianCoordinate newPosition = geodeticSystem.polarToRectangularCoordinates (newStatus.position);

		if (k+1 >= vehicleStates.length)
			return new VehicleStatus (newStatus.position, 0, 0, 0, 0);
			
		double timeScale = (time - oldTimeStamp) / (newTimeStamp - oldTimeStamp);
		
		CartesianCoordinate oldPosition = geodeticSystem.polarToRectangularCoordinates (oldStatus.position);
		newPosition = newPosition.subtract (oldPosition);
		newPosition = newPosition.multiply (timeScale);
		newPosition = newPosition.add (oldPosition);
		PolarCoordinate newPos = geodeticSystem.rectangularToPolarCoordinates (newPosition);
				
		return new VehicleStatus (newPos, oldStatus.totalSpeed, oldStatus.courseOverGround,oldStatus.elevation,
				CourseUtils.interpolateAngle (oldStatus.orientation, newStatus.orientation, timeScale));
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier#getGeodeticSystem()
	 */
	public IGeodeticSystem getGeodeticSystem ()
	{
		return geodeticSystem;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier#getSetCourseData()
	 */
	public VehicleStatus[] getSetCourseData ()
	{
		return vehicleStates;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier#getTimeTable()
	 */
	public long[] getTimeTable ()
	{
		long[] timeTable = new long[durations.length];
		
		timeTable[0] = durations[0];

		for (int k=1; k < timeTable.length; k++)
			timeTable[k] = timeTable[k-1] + durations[k];

		return timeTable;
	}

}
