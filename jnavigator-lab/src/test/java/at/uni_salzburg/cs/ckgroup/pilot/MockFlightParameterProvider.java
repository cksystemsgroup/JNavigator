/*
 * @(#) MockFlightParameterProvider.java
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
 * This class implements the <code>IFlightParameterProvider</code> for the flight plan test cases.
 * 
 * @author Clemens Krainer
 */
public class MockFlightParameterProvider implements IFlightParameterProvider
{
	/**
	 * The dead time after issuing a control command
	 */
	long deadTime;
	
	/**
	 * The response time after a control command has been ended
	 */
	long responseTime;
	
	/**
	 * The maximum acceleration to be used to control the vehicle
	 */
	double maxAcceleration;
	
	/**
	 * The minimum time a command should last
	 */
	long minCommandTime;
	
	/**
	 * The delay from the vehicle being in a certain position to the arrival of the according  position message
	 */
	long messageDelay;
	
	/**
	 * Construct a <code>MockFlightParameterProvider</code> object.
	 * 
	 * @param deadTime the dead time after issuing a control command
	 * @param responseTime the response time after a control command has been ended
	 * @param maxAcceleration the maximum acceleration to be used to control the vehicle
	 * @param minCommandTime the minimum time a command should last
	 * @param messageDelay the delay from the vehicle being in a certain position to the arrival of the according  position message  
	 */
	public MockFlightParameterProvider (long deadTime, long responseTime, double maxAcceleration, long minCommandTime, long messageDelay){
		this.deadTime = deadTime;
		this.responseTime =responseTime;
		this.maxAcceleration = maxAcceleration;
		this.minCommandTime = minCommandTime;
		this.messageDelay = messageDelay;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getCommandDeadTime()
	 */
	public long getCommandDeadTime() {
		return deadTime;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getCommandResponseTime()
	 */
	public long getCommandResponseTime() {
		return responseTime;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getMaximumAcceleration()
	 */
	public double getMaximumAcceleration() {
		return maxAcceleration;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getMinimumCommandTime()
	 */
	public long getMinimumCommandTime() {
		return minCommandTime;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getPositionMessageDelay()
	 */
	public long getPositionMessageDelay() {
		return messageDelay;
	}
	
}