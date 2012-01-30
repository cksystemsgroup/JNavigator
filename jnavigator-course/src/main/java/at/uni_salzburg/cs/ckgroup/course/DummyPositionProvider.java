/*
 * @(#) DummyPositionProvider.java
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

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.NotImplementedException;

/**
 * This class implements a dummy Position Provider, i.e. it always returns the
 * same Position. If this class can find a reference Position in the properties
 * it will return this Position every time. If it can not find a reference
 * Position it will return the position zero, i.e. latitude=0, longitude=0,
 * altitude=0, as well as null values for courseOverGround and speedOverGround.
 * 
 * @author Clemens Krainer
 */
public class DummyPositionProvider implements IPositionProvider
{
	/**
	 * Constants for property keys.
	 */
	public static final String PROP_REFERENCE_LATITUDE = "reference.latitude";
	public static final String PROP_REFERENCE_LONGITUDE = "reference.longitude"; 
	public static final String PROP_REFERENCE_ALTITUDE = "reference.altitude";
	public static final String PROP_HAS_COURSE_OVER_GROUND = "has.course.over.ground";
	public static final String PROP_REFERENCE_COURSE_OVER_GROUND = "reference.course.over.ground";
	public static final String PROP_HAS_SPEED_OVER_GROUND = "has.speed.over.ground";
	public static final String PROP_REFERENCE_SPEED_OVER_GROUND = "reference.speed.over.ground";
	
	/**
	 * The current Position, i.e. the reference Position from the Properties or (0,0,0)
	 */
	private PolarCoordinate currentPosition;
	
	/**
	 * Fake a <code>NotImplementedException</code> in
	 * <code> getCourseOverGround()</code> if set to false.
	 */
	private boolean hasCourseOverGround;
	
	/**
	 * The current course over ground.
	 */
	private Double courseOverGround;
	
	/**
	 * Fake a <code>NotImplementedException</code> in
	 * <code> getSpeedOverGround()</code> if set to false.
	 */
	private boolean hasSpeedOverGround;
	
	/**
	 * The current speed over ground.
	 */
	private Double speedOverGround;
	
	/**
	 * The currently used geodetic system. 
	 */
	private IGeodeticSystem geodeticSystem = new WGS84();
	
	/**
	 * Construct a DummyPositionProvider.
	 * 
	 * @param props the Properties to be used to search for the reference Position.
	 */
	public DummyPositionProvider (Properties props) {
        double latitudeReference = Double.parseDouble (props.getProperty (PROP_REFERENCE_LATITUDE,"0"));
        double longitudeReference = Double.parseDouble (props.getProperty (PROP_REFERENCE_LONGITUDE,"0"));
        double altitudeReference = Double.parseDouble (props.getProperty (PROP_REFERENCE_ALTITUDE,"0"));
        hasCourseOverGround = "true".equalsIgnoreCase((props.getProperty(PROP_HAS_COURSE_OVER_GROUND,"true")));
        String cog = props.getProperty (PROP_REFERENCE_COURSE_OVER_GROUND);
        courseOverGround = cog == null ? null : Double.valueOf (cog);
        hasSpeedOverGround = "true".equalsIgnoreCase((props.getProperty(PROP_HAS_SPEED_OVER_GROUND,"true")));
        String sog = props.getProperty (PROP_REFERENCE_SPEED_OVER_GROUND);
        speedOverGround = sog == null ? null : Double.valueOf (sog);
        currentPosition = new PolarCoordinate (latitudeReference, longitudeReference, altitudeReference);
	}

	/**
	 * Return the current position as WGS84 coordinates, i.e. latitude,
	 * longitude and altitude.
	 * 
	 * @return the current position.
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
	 * @uml.property name="currentPosition"
	 */
	public PolarCoordinate getCurrentPosition () {
		return currentPosition;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCourseOverGround()
	 */
	public Double getCourseOverGround() {
		if (!hasCourseOverGround)
			throw new NotImplementedException();
		if (!hasSpeedOverGround)
			return null;
		return courseOverGround;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getSpeedOverGround()
	 */
	public Double getSpeedOverGround() {
		if (!hasSpeedOverGround)
			throw new NotImplementedException();
		if (!hasCourseOverGround)
			return null;
		return speedOverGround;
	}

	public IGeodeticSystem getGeodeticSystem() {
		return geodeticSystem;
	}

	public void close() {
		// TODO Auto-generated method stub
	}

}
