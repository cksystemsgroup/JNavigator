/*
 * @(#) IPilot.java
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

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.util.IClock;

/**
 * This interface covers the functionality of a JAviator pilot. 
 * 
 * @author Clemens Krainer
 */
public interface IPilot
{
	/**
	 * This method receives data from the JAviator inertial sensors. The
	 * JControl main loop calls this method periodically at high frequency (e.g.
	 * 50Hz). It is this method that does the actual flying control. This method
	 * also calls the pilot interceptors.
	 * 
	 * @param sensorData the sensor data from the JAviator inertial sensors.
	 * @return the control data for flying
	 */
	public FlightControlData processSensorData (HardWareSensorData sensorData);
	
	/**
	 * Set the set course supplier for this pilot.
	 * 
	 * @param setCourseSupplier the new set course supplier object.
	 */
	public void setCourseSupplier (ISetCourseSupplier setCourseSupplier);
	
	/**
	 * Set the position provider for this pilot.
	 * 
	 * @param positionProvider the new position provider object.
	 */
	public void setPositionProvider (IPositionProvider positionProvider);
	
	/**
	 * Provide this pilot a clock.
	 * 
	 * @param clock a reference to the instantiated clock implementation.
	 */
	public void setClock (IClock clock);
	
	/**
	 * This method starts the flight along the course from the set course
	 * supplier.
	 *
	 * @throws ConfigurationException in case of configuration errors
	 */
	public void startFlyingSetCourse () throws ConfigurationException;
	
	/**
	 * This method stops the flight along the set course.
	 */
	public void stopFlyingSetCourse ();
	
	/**
	 * Return whether the pilot flies a set course.
	 * 
	 * @return true if the pilot flies a set course, false otherwise.
	 */
	public boolean isFlyingSetCourse ();
	
	/**
	 * Add a pilot interceptor. It is the <code>processSensorData</code>
	 * method that calls the pilot interceptors in the sequence they have been
	 * registered.
	 * 
	 * @param pilotInterceptor the new pilot interceptor.
	 */
//	public void addPilotInterceptor (IPilotInterceptor pilotInterceptor);
	
	/**
	 * Remove a pilot interceptor.
	 * 
	 * @param pilotInterceptor the pilot interceptor to be removed.
	 */
//	public void removePilotInterceptor (IPilotInterceptor pilotInterceptor);
}
