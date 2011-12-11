/*
 * @(#) IPositionProvider.java
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
 * This interface covers the functionality of a geodetic position provider. An
 * implementation of this Interface must support the current position as WGS84
 * coordinates.
 * 
 * @author Clemens Krainer
 */
public interface IPositionProvider
{
	/**
	 * Return the current position as WGS84 coordinates, i.e. latitude,
	 * longitude and altitude.
	 * 
	 * @return the current position.
	 */
	public PolarCoordinate getCurrentPosition ();
	
	/**
	 * Return the current speed over ground in meters per second. 
	 *
	 * @return The current speed over ground in meters per second or null if not present.
	 */
	public Double getSpeedOverGround ();
	
	/**
	 * Return the current course over ground in degrees. 
	 *
	 * @return The current course over ground in degrees or null if not present.
	 */
	public Double getCourseOverGround ();
	
	/**
	 * Return the currently used geodetic system.
	 * 
	 * @return the currently used geodetic system.
	 */
	public IGeodeticSystem getGeodeticSystem ();
}
