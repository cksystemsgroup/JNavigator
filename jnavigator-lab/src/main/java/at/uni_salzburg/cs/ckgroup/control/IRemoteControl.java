/*
 * @(#) IRemoteControl.java
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
package at.uni_salzburg.cs.ckgroup.control;

import java.io.IOException;

/**
 * This interface provides the functionality necessary for controlling air
 * vehicles.
 * 
 * @author Clemens Krainer
 */
public interface IRemoteControl
{
	/**
	 * Set the roll value in degrees.
	 * 
	 * @param roll the roll value in degrees.
	 * @throws IOException thrown in case of IO errors.
	 */
	public void setRoll (double roll) throws IOException;
	
	/**
	 * Set the pitch value in degrees.
	 * 
	 * @param pitch the pitch value in degrees.
	 * @throws IOException thrown in case of IO errors.
	 */
	public void setPitch (double pitch) throws IOException;
	
	/**
	 * Set the yaw value in degrees.
	 * 
	 * @param yaw the yaw value in degrees.
	 * @throws IOException thrown in case of IO errors.
	 */
	public void setYaw (double yaw) throws IOException;
	
	/**
	 * Set the thrust value in degrees.
	 * 
	 * @param thrust the thrust value in degrees.
	 * @throws IOException thrown in case of IO errors.
	 */
	public void setThrust (double thrust) throws IOException;
}
