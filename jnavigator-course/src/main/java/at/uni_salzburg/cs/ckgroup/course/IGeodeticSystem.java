/*
 * @(#) IGeodeticSystem.java
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
 * This interface provides functionality necessary to convert polar in
 * rectangular coordinates and vice versa. Additionally it provides
 * functionality to estimate distance, speed, elevation and course between two
 * given polar coordinates.
 * 
 * @author Clemens Krainer
 */
public interface IGeodeticSystem
{
	/**
	 * Convert rectangular coordinates to polar coordinates.
	 * 
	 * @param coordinates the rectangular coordinates x, y, and z in meters as a
	 *        Position object.
	 * @return a new Position object containing the polar coordinates, i.e. the
	 *         altitude in meter, longitude and latitude in degrees.
	 */
	public PolarCoordinate rectangularToPolarCoordinates (CartesianCoordinate coordinates);

	/**
	 * Convert rectangular coordinates to polar coordinates.
	 * 
	 * @param x the value for X in meters.
	 * @param y the value for Y in meters.
	 * @param z the value for Z in meters.
	 * @return a new Position object containing the polar coordinates, i.e.
	 *         latitude, longitude in degrees and altitude in meters
	 */
	public PolarCoordinate rectangularToPolarCoordinates (double x, double y, double z);		

	/**
	 * Convert polar coordinates in rectangular coordinates.
	 * 
	 * @param coordinates i.e. latitude, longitude in degrees and altitude in
	 *        meters.
	 * @return a new Position object containing the rectangular coordinates.
	 */
	public CartesianCoordinate polarToRectangularCoordinates (PolarCoordinate coordinates);
	
	/**
	 * Convert polar coordinates in rectangular coordinates.
	 * 
	 * @param latitude the latitude value in degrees.
	 * @param longitude the longitude value in degrees.
	 * @param altitude the altitude value in meters.
	 * @return a new Position object containing the rectangular coordinates x,
	 *         ,y and z in meters.
	 */
	public CartesianCoordinate polarToRectangularCoordinates (double latitude, double longitude, double altitude);

	/**
	 * Calculate the distance, speed, elevation angle and course over ground.
	 * 
	 * @param oldPosition the old Position in polar coordinates.
	 * @param currentPosition the current Position in polar coordinates.
	 * @param timeSpan the time in milliseconds from old to current Position.
	 * @return the estimated <code>CourseData</code>
	 */
	public CourseData calculateSpeedAndCourse (PolarCoordinate oldPosition, PolarCoordinate currentPosition, long timeSpan);
	
	/**
	 * Walk on the geodetic system. Negative <i>walking</i> values indicate the
	 * according opposite direction.
	 * 
	 * @param startPosition the start position in polar coordinates.
	 * @param x the distance in meters to go south.
	 * @param y the distance in meters to go east.
	 * @param z the distance in meters to ascent.
	 * @return the Position reached after walking in polar coordinates.
	 */
	public PolarCoordinate walk (PolarCoordinate startPosition, double x, double y, double z);
}
