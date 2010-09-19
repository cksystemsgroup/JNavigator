/*
 * @(#) ISetCourseSupplier.java
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

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This interface specifies the functionality of a set course supplier.
 * 
 * @author Clemens Krainer
 */
public interface ISetCourseSupplier
{
	/**
	 * Return the set course VehicleStatus to a given point in time.
	 * 
	 * @param time the time in milliseconds since start.
	 * @return the set course position as WGS84 coordinates, i.e. latitude and
	 *         longitude in degrees, altitude in meters.
	 */
	public VehicleStatus getSetCoursePosition (long time);
	
	/**
	 * Return the current geodetic system in charge.
	 * 
	 * @return the current geodetic system.
	 */
	public IGeodeticSystem getGeodeticSystem ();
	
	/**
	 * Return the complete set course data.
	 * 
	 * @return the set course data.
	 */
	public VehicleStatus[] getSetCourseData ();
	
	/**
	 * Return the time table for the set course data.
	 *  
	 * @return the time table.
	 */
	public long[] getTimeTable ();
	
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
	public void loadSetCourse (InputStream courseData) throws ConfigurationException, IOException;
}
