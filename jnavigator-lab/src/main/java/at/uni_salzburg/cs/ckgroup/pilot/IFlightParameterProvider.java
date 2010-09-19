/*
 * @(#) FlightParameterProvider.java
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

/**
 * This interface provides the access to flight control specific variables.
 * 
 * @author Clemens Krainer
 */
public interface IFlightParameterProvider {

	/**
	 * Return the delay time to deliver the current position.
	 * 
	 * @return the delay time of the position message.
	 */
	public long getPositionMessageDelay ();
	
	/**
	 * Return the time between the issue of a command to the first response of the JAviator.
	 * 
	 * @return the dead time.
	 */
	public long getCommandDeadTime ();
	
	/**
	 * Return the time between the end of a command and the response of the JAviator.
	 *  
	 * @return the response time.
	 */
	public long getCommandResponseTime ();
	
	/**
	 * Return the time span from now to the next set course point.
	 * 
	 * @return the time span from now to the next set course point.
	 */
//	public long getPlanTimeToNextSetCoursePoint ();
	
	/**
	 * Return the minimum time span a command must last.
	 * 
	 * @return
	 */
	public long getMinimumCommandTime ();
	
	/**
	 * Return the maximum allowed acceleration of the vehicle.
	 * 
	 * @return the maximum allowed acceleration of the vehicle.
	 */
	public double getMaximumAcceleration ();
}
