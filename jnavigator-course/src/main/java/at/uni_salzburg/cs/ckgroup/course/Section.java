/*
 * @(#) Section.java
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
 * A <code>Section</code> of a set course is the linear motion path for the
 * vehicle from one set course way point to the next within a certain time. At
 * the beginning and the end a section also defines the desired orientation of
 * the vehicle.
 * 
 * @author Clemens Krainer
 * @see SectionFlightPlan
 */
public class Section {
	
	/**
	 * The first position of the vehicle in this section.
	 */
	private PolarCoordinate startPosition;
	
	/**
	 * The orientation of the vehicle at the start position.
	 */
	private double startOrientation;
	
	/**
	 * The last position of the vehicle in this section.
	 */
	private PolarCoordinate endPosition;
	
	/**
	 * The orientation of the vehicle at the end position.
	 */
	private double endOrientation;
	
	/**
	 * The travel time for the vehicle to pass through this section.
	 */
	private long travelTime;
	
	/**
	 * Construct a set course section.
	 * 
	 * @param startPosition the first position of the vehicle in this section.
	 * @param startOrientation the orientation of the vehicle at the start position.
	 * @param endPosition the last position of the vehicle in this section.
	 * @param endOrientation the orientation of the vehicle at the end position.
	 * @param travelTime the travel time for the vehicle to pass through this section.
	 */
	public Section (PolarCoordinate startPosition, double startOrientation, PolarCoordinate endPosition, double endOrientation,long travelTime) {
		this.startPosition = startPosition;
		this.startOrientation = startOrientation;
		this.endPosition = endPosition;
		this.endOrientation = endOrientation;
		this.travelTime = travelTime;
	}
	
	/**
	 * @return the first position of the vehicle in this section.
	 */
	public PolarCoordinate getStartPosition () {
		return startPosition;
	}
	
	/**
	 * @return the orientation of the vehicle at the start position.
	 */
	public double getStartOrientation () {
		return startOrientation;
	}
	
	/**
	 * @return the last position of the vehicle in this section.
	 */
	public PolarCoordinate getEndPosition () {
		return endPosition;
	}
	
	/**
	 * @return the orientation of the vehicle at the end position.
	 */
	public double getEndOrientation () {
		return endOrientation;
	}
	
	/**
	 * @return the travel time for the vehicle to pass through this section.
	 */
	public long getTravelTime () {
		return travelTime;
	}
}
