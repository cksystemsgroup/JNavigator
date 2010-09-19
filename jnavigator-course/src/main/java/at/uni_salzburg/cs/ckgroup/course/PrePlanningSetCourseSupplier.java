/*
 * @(#) PrePlanningSetCourseSupplier.java
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.NotImplementedException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class implements a pre-planning set course supplier. It load set course data,
 * containing latitude, longitude, altitude, running time and vehicle
 * orientation from an InputStream. 
 * 
 * Additionally, it estimates distance, speed,
 * course and elevation angle values consistent with the course data. A user of
 * this class may query a required VehicleStatus to a given point in time.
 * 
 * @author Clemens Krainer
 */
public class PrePlanningSetCourseSupplier implements ISetCourseSupplier
{
	/**
	 * Constant for the set course data file name property key. 
	 */
	public static final String PROP_DATA_FILE_NAME = "data.fileName";
	
	/**
	 * Property key prefix for the geodetic system.
	 */
	public static final String PROP_GEODETIC_SYSTEM_PREFIX = "geodetic.system.";
	
	/**
	 * Property key prefix for the set course planner.
	 */
	public static final String PROP_COURSE_PLANNER_PREFIX = "course.planner.";
	
	/**
	 * The geodetic system to be used for calculations. 
	 */
	private IGeodeticSystem geodeticSystem;
	
	/**
	 * The set course planner to be used.
	 */
	private ICoursePlanner coursePlanner;
	
	/**
	 * The last set course index.
	 */
	private int index = 0;

	/**
	 * The absolute start time of each section in milliseconds.
	 */
	private long[] schedule;
	
	/**
	 * The set course sections. 
	 */
	private Section[] sections;
	
	/**
	 * The flight plans to each section.
	 */
	private SectionFlightPlan[] sectionFlightPlans;
	
	/**
	 * Construct a simple set course supplier from provided properties.
	 * Construct the geodetic system and the course planner according to the
	 * provided properties.
	 * 
	 * @param props the properties to be used.
	 * @throws ConfigurationException thrown in case of an configuration error.
	 * @throws IOException thrown in case of I/O errors when loading the set course from file.
	 */
	public PrePlanningSetCourseSupplier (Properties props)
			throws ConfigurationException, IOException
	{
		geodeticSystem = (IGeodeticSystem) ObjectFactory.getInstance ().instantiateObject (
				PROP_GEODETIC_SYSTEM_PREFIX, IGeodeticSystem.class, props);
		
		coursePlanner = (ICoursePlanner) ObjectFactory.getInstance ().instantiateObject (
				PROP_COURSE_PLANNER_PREFIX, ICoursePlanner.class, props);

//		String dataFileName = props.getProperty (PROP_DATA_FILE_NAME);
//		InputStream courseData = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (dataFileName);
	}
	
	/**
	 * Construct a simple set course supplier.
	 * 
	 * @param geodeticSystem the geodetic system to be used for calculations.
	 * @param coursePlanner the course planner to be used for set course planning.
	 * @throws ConfigurationException thrown if no course data is available or
	 *         if negative duration values have been used.
	 * @throws IOException thrown in case of IO errors.
	 */
	public PrePlanningSetCourseSupplier (IGeodeticSystem geodeticSystem, ICoursePlanner coursePlanner)
			throws ConfigurationException, IOException
	{
		this.geodeticSystem = geodeticSystem;
		this.coursePlanner = coursePlanner;
	}
	
	/**
	 * Load the course data from a given InputStream. This load procedure
	 * interprets the course data line by line. A line starting with '#' is
	 * considered as a comment and therefore skipped. Empty lines are ignored.
	 * Every other line must contain the values for latitude, longitude,
	 * altitude, duration and orientation. Semicolons (';') separate the fields
	 * from each other.
	 * @see SetCourse
	 * 
	 * @param courseData the InputStream containing the course data.
	 * @throws ConfigurationException thrown if no course data is available or
	 *         the available data is invalid.
	 * @throws IOException thrown in case of IO errors.
	 */
	public void loadSetCourse (InputStream courseData)
			throws ConfigurationException, IOException
	{		
		index = 0;
		sections = SetCourse.loadSetCourse (courseData);
		sectionFlightPlans = coursePlanner.planCourse (sections, geodeticSystem);
		
		schedule = new long[sections.length+1];
		schedule[0] = 0;
		for (int k=0; k < sections.length; k++)
			schedule[k+1] = schedule[k] + sections[k].getTravelTime();
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier#getSetCoursePosition(long)
	 */
	public VehicleStatus getSetCoursePosition (long time) {

		if (time < schedule[index])
			index = 0;
			
		while (index+1 < schedule.length && time > schedule[index+1])
			++index;

		if (index+1 >= schedule.length)
			return new VehicleStatus (sections[sections.length-1].getStartPosition(), 0, 0, 0, sections[sections.length-1].getStartOrientation());
		
		PolarCoordinate scheduledPosition = sectionFlightPlans[index].getScheduledPosition(time - schedule[index], geodeticSystem);
		
		double timeScale = (time - schedule[index]) / sections[index].getTravelTime();
		double orientation = CourseUtils.interpolateAngle (sections[index].getStartOrientation(), sections[index].getEndOrientation(), timeScale);
		return new VehicleStatus (scheduledPosition, 0, 0, 0, orientation);
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
		throw new NotImplementedException ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier#getTimeTable()
	 */
	public long[] getTimeTable ()
	{
		throw new NotImplementedException ();
	}

}
