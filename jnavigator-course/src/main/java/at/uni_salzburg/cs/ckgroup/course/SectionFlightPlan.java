/*
 * @(#) SectionFlightPlan.java
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
 * This class implements a flight plan to an existing set course section. Once
 * initialized with planning data, a <code>SectionFlightPlan</code> can
 * interpolate the desired vehicle position inside a section.
 * 
 * A <code>SectionFlightPlan</code> splits a <code>Section</code> into several
 * subsections. Each subsection describes a fragment of constant acceleration
 * defined by travel time, start velocity and end velocity.
 * 
 * @author Clemens Krainer
 * @see Section
 */
public class SectionFlightPlan {

	/**
	 * The current index in the durations, schedule and velocities arrays.
	 */
	private int index = 0;
	
	/**
	 * The duration in milliseconds for each subsection.
	 */
	private long[] durations;
	
	/**
	 * The start time points in milliseconds for each subsection calculated from
	 * the subsection durations.
	 */
	private long[] schedule;
	
	/**
	 * The start velocities for each section in meters per seconds.
	 */
	private double[] velocities;
	
	/**
	 * The distances of each subsection.
	 */
	private double[] distances;
	
	/**
	 * The associated <code>Section</code> object.
	 */
	private Section section;
	
	/**
	 * Construct a <code>SectionFlightPlan</code>.
	 * 
	 * @param section the section this <code>SectionFlightPlan</code> is associated with.
	 */
	public SectionFlightPlan (Section section) {
		if (section == null)
			throw new NullPointerException ("The section parameter must not be null!");
		
		this.section = section;
	}
	
	/**
	 * Set the planned values of this section flight plan.
	 * 
	 * @param durations the planned duration of each subsection.
	 * @param velocities the planned velocity in each subsection. 
	 */
	public void setValues (long[] durations, double[] velocities) {
		
		if (durations.length+1 != velocities.length)
			throw new ArrayIndexOutOfBoundsException ("The number of elements in the velocities array must be one more than in the durations array.");
		
		index = 0;
		
		this.durations = durations;
		this.velocities = velocities;
		
		schedule = new long[durations.length+1];
		schedule[0] = 0;
		for (int k=0; k < durations.length; k++)
			schedule[k+1] = schedule[k] + durations[k];
		
		distances = new double[durations.length];
		distances[0] = 0;
		for (int k=0; k < durations.length-1; k++)
			distances[k+1] = distances[k] + 0.0005 * durations[k] * (velocities[k+1] + velocities[k]);
	}
	
	/**
	 * Calculate the scheduled position of the vehicle.
	 * 
	 * @param time the time since the section start.
	 * @param geodeticSystem the geodetic system to be used for calculations.
	 * @return the scheduled position of the vehicle.
	 */
	public PolarCoordinate getScheduledPosition (long time, IGeodeticSystem geodeticSystem) {
		
		if (section.getEndPosition() == null)
			return section.getStartPosition();
			
		if (time < schedule[index])
			index = 0;
		
		while (index+1 < schedule.length && time > schedule[index+1])
			++index;

		if (index+1 >= schedule.length)
			return section.getEndPosition();

		double dT = 0.001 * (time - schedule[index]);
		double r = distances[index] + velocities[index]*dT + 0.5*(velocities[index+1] - velocities[index])*dT*dT/(0.001*durations[index]);
		
		CartesianCoordinate startPosition = geodeticSystem.polarToRectangularCoordinates (section.getStartPosition());
		CartesianCoordinate endPosition = geodeticSystem.polarToRectangularCoordinates (section.getEndPosition());
		CartesianCoordinate newPosition = endPosition.subtract (startPosition).normalize ().multiply (r).add (startPosition);
		return geodeticSystem.rectangularToPolarCoordinates (newPosition);
	}
}
