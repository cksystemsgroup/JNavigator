/*
 * @(#) CourseData.java
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

/**
 * This class contains course data for exchange with the
 * <code>IGeodeticSystem</code> instances.
 * 
 * @author Clemens Krainer
 */
public class CourseData
{
	/**
	 * The distance between the old and the current Position in meters. 
	 */	
	public double distance;
	 
	/**
	 * The average speed necessary to arrive at the current Position in time in meters per second.
	 */
	public double speed;
	 
	/**
	 * The elevation angle between the old and the current Position in degrees.
	 */
	public double elevation;
	 
	/**
	 * The course over ground in degrees. This element is optional, i.e. the result array may have three or four elements.
	 */
	public double course;

	/**
	 * This variable indicates that the <code>course</code> value is valid.
	 */
	public boolean courseIsValid;
	 
	/**
	 * @param distance the distance between the old and the current Position in meters.
	 * @param speed the average speed necessary to arrive at the current Position in time in meters per second.
	 * @param elevation the elevation angle between the old and the current Position in degrees.
	 * @param course the course over ground in degrees. This element is optional, i.e. the result array may have three or four elements.
	 * @param courseIsValid this variable indicates that the <code>course</code> value is valid.
	 */
	public CourseData (double distance, double speed, double elevation, double course,
			boolean courseIsValid)
	{
		this.distance = distance;
		this.speed = speed;
		this.elevation = elevation;
		this.course = course;
		this.courseIsValid = courseIsValid;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return	"distance=" + distance + ", speed=" + speed + ", elevation=" + elevation +
				", course=" + course + ", courseIsValid=" + courseIsValid;
	}
	 
}
