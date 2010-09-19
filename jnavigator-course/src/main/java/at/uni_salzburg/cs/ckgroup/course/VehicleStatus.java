/*
 * @(#) VehicleStatus.java
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
 * This class implements a 5-tuple to describe the vehicle status. It contains
 * Position, total speed, course over ground, elevation and orientation of the
 * vehicle. Ground is considered as a plain, normal to the zenith-nadir axis.
 * The elevation is the angle between ground and motion vector of the vehicle.
 * 
 * @author Clemens Krainer
 */
public class VehicleStatus
{
	/**
	 * The current position of the vehicle: latitude and longitude in degrees,
	 * altitude in meters.
	 */
	public PolarCoordinate position;
	
	/**
	 * The current total speed in meters per second. 
	 */
	public double totalSpeed;
	
	/**
	 * The current course over ground in degrees. 
	 */
	public double courseOverGround;
	
	/**
	 * The current elevation in degrees, i.e. the angle between ground and
	 * motion vector of the vehicle. Ground is considered as a plain, normal to
	 * the zenith-nadir axis.
	 */
	public double elevation;
	
	/**
	 * The current orientation of the vehicle over ground in degrees. Zero
	 * indicates north, 90 indicates east, 180 indicates south and 270 indicates
	 * west.
	 */
	public double orientation;
	
	/**
	 * Construct a VehicleStatus.
	 * 
	 * @param position the position.
	 * @param totalSpeed the total speed of the vehicle.
	 * @param courseOverGround the course over ground.
	 * @param elevation the angle between ground and motion vector of the vehicle.
	 * @param orientation the orientation of the vehicle over ground.
	 */
	public VehicleStatus (PolarCoordinate position, double totalSpeed, double courseOverGround,
			double elevation, double orientation)
	{
		this.position = position;
		this.totalSpeed = totalSpeed;
		this.courseOverGround = courseOverGround;
		this.elevation = elevation;
		this.orientation = orientation;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "position=(" + position.latitude + "°, " + position.longitude + "°, "
				+ position.altitude + "m), totalSpeed=" + totalSpeed + "m/s, courseOverGround="
				+ courseOverGround + "°, elevation=" + elevation + "°, orientation=" + orientation
				+ "°";
	}
}
