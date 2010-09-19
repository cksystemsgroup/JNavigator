/*
 * @(#) IFlightPlan.java
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

import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;

/**
 * This interface provides the functionality of a flight plan.
 * 
 * @author Clemens Krainer
 */
public interface IFlightPlan {
	/**
	 * Return the last estimated command duration.
	 * 
	 * @return the last estimated command duration.
	 */
	public long[] getCommandTimes();

	/**
	 * Return the last estimated acceleration vector.
	 * 
	 * @return the last estimated acceleration vector.
	 */
	public CartesianCoordinate[] getAccelerations();

	/**
	 * Estimate the flight plan. It is crucial that the vectors distance,
	 * currentVelocity and nextVelocity are collinear. This method relies on
	 * that and performs no checks whatsoever.
	 * 
	 * @param distance
	 *            the distance vector from the current position to the desired
	 *            next position.
	 * @param currentVelocity
	 *            the current velocity of the vehicle.
	 * @param nextVelocity
	 *            the desired velocity at the end of the distance vector.
	 * @param planTime
	 *            the planned time to traverse the distance.
	 * @return true if the estimated flight plan is applicable, false otherwise.
	 */
	public boolean estimateFlightPlan(CartesianCoordinate distance,
			CartesianCoordinate currentVelocity,
			CartesianCoordinate nextVelocity, long planTime);

	/**
	 * Return if the schedule needs reworking.
	 * 
	 * @return true it the schedule needs reworking, false otherwise.
	 */
	public boolean scheduleNeedsReworking();
	
	/**
	 * Set the provider for the current flight parameters.
	 * 
	 * @param flightParameterProvider the provider for the current flight parameters.
	 */
	public void setFlightParameterProvider (IFlightParameterProvider flightParameterProvider);
}
