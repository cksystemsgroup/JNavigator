/*
 * @(#) HardWareSensorData.java
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

public class HardWareSensorData
{
	/**
	 * The rotation about the vertical axis in degrees.
	 */
	public double	yaw;

	/**
	 * The rotation around the longitudinal axis in degrees.
	 */
	public double	roll;

	/**
	 * The rotation around the lateral or transverse axis in degrees.
	 */
	public double	pitch;

	/**
	 * The height above the ground in meters.
	 */
	public double	heightAboveGround;

	/**
	 * @param yaw The rotation about the vertical axis in degrees.
	 * @param roll The rotation around the longitudinal axis in degrees.
	 * @param pitch The rotation around the lateral or transverse axis in degrees.
	 * @param heightAboveGround The height above the ground in meters.
	 */
	public HardWareSensorData (double yaw, double roll, double pitch, double heightAboveGround)
	{
		this.yaw = yaw;
		this.roll = roll;
		this.pitch = pitch;
		this.heightAboveGround = heightAboveGround;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString ()
	{
		return "SensorData: yaw=" + yaw + "°, roll=" + roll + "°, pitch=" + pitch
				+ "°, heightAboveGround=" + heightAboveGround + "m";
	}
}
