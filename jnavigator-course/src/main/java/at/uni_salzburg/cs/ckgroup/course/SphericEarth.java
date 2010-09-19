/*
 * @(#) SphericEarth.java
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

/**
 * This geodetic system implements the earth as a sphere.
 * 
 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem
 * @author Clemens Krainer
 * @uml.dependency supplier="at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate"
 */
public class SphericEarth implements IGeodeticSystem
{
	/**
	 * Spheric Earth average radius
	 */
	public static final double AVG_EARTH_RADIUS = 6371000.785;
	
	/**
	 * PI divided by 180 
	 */
	private static final double PI180TH = Math.PI / 180;
	
	/**
	 * Construct a spheric earth geodetic system.
	 */
	public SphericEarth () {
		// intentionally empty
	}
	
	/**
	 * Construct a spheric earth geodetic system. This method supports construction by
	 * the <code>ObjectFactory</code> and it does not use the provided
	 * properties.
	 * 
	 * @param props the properties to be used to construct this geodetic system.
	 */
	public SphericEarth (Properties props) {
		// intentionally empty
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem#polarToRectangularCoordinates(at.uni_salzburg.cs.ckgroup.course.Position)
	 */
	public CartesianCoordinate polarToRectangularCoordinates (PolarCoordinate coordinates) {
		return polarToRectangularCoordinates (coordinates.latitude, coordinates.longitude, coordinates.altitude);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem#polarToRectangularCoordinates(double, double, double)
	 */
	public CartesianCoordinate polarToRectangularCoordinates (double latitude,
			double longitude, double altitude) {
		
		double x = (AVG_EARTH_RADIUS + altitude) * Math.cos (latitude*PI180TH) * Math.cos (longitude*PI180TH);
		double y = (AVG_EARTH_RADIUS + altitude) * Math.cos (latitude*PI180TH) * Math.sin (longitude*PI180TH);
		double z = (AVG_EARTH_RADIUS + altitude) * Math.sin (latitude*PI180TH);
		
		return new CartesianCoordinate (x, y, z);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem#rectangularToPolarCoordinates(at.uni_salzburg.cs.ckgroup.course.Position)
	 */
	public PolarCoordinate rectangularToPolarCoordinates (CartesianCoordinate coordinates) {
		return rectangularToPolarCoordinates (coordinates.x, coordinates.y, coordinates.z);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem#rectangularToPolarCoordinates(double, double, double)
	 */
	public PolarCoordinate rectangularToPolarCoordinates (double x, double y, double z) {

		double N = Math.sqrt (x*x + y*y + z*z);
		double altitude = N - AVG_EARTH_RADIUS;
		double latitude = Math.asin (z/N);
		double longitude = Math.asin (y/(N*Math.cos (latitude)));
			
		return new PolarCoordinate (latitude/PI180TH, longitude/PI180TH, altitude);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem#calculateSpeedAndCourse(at.uni_salzburg.cs.ckgroup.course.Position, at.uni_salzburg.cs.ckgroup.course.Position, long)
	 */
	public CourseData calculateSpeedAndCourse (PolarCoordinate oldPosition,
			PolarCoordinate currentPosition, long timeSpan) {

		if (currentPosition == null || oldPosition == null || timeSpan == 0)
			return null;

		CartesianCoordinate rectOldPosition = polarToRectangularCoordinates (oldPosition);
		CartesianCoordinate rectCurrentPosition = polarToRectangularCoordinates (currentPosition);
		CartesianCoordinate motionVector = rectCurrentPosition.subtract (rectOldPosition);

		double distance = motionVector.norm ();
		double speed = distance * 1000 / timeSpan;
		double elevation = 0;
		
		/*
		 * normalVector is the normal vector to the tangential plane to earth in
		 * position rectOldPosition. Since SphericEarth is an ideal sphere the
		 * position vector rectOldPosition is a normal vector to the tangential
		 * plane in position rectOldPosition.
		 */
		CartesianCoordinate normalVector = rectOldPosition;
		
		if (distance > 1E-3) {
			double x = normalVector.multiply (motionVector) / (normalVector.norm ()*distance);
			if (x >= 1)
				x = 1;
			else if (x <= -1)
				x = -1;
			elevation = Math.asin (x) / PI180TH;
		}
		
		if (speed < 1E-2 || (Math.abs (currentPosition.latitude - oldPosition.latitude) < 1E-6 && Math.abs (currentPosition.longitude - oldPosition.longitude) < 1E-6))
			return new CourseData (distance, speed, elevation, 0, false);
		
		double course = Math.atan2 ((oldPosition.longitude - currentPosition.longitude)*Math.cos(currentPosition.latitude*PI180TH),
									currentPosition.latitude - oldPosition.latitude);

		if (course < 0)
			course += 2*Math.PI;

		course /= PI180TH;
   
		return new CourseData (distance, speed, elevation, course, true);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem#walk(at.uni_salzburg.cs.ckgroup.course.Position, double, double, double)
	 */
	public PolarCoordinate walk (PolarCoordinate startPosition, double x, double y, double z) {
		
		double latitude = startPosition.latitude * PI180TH;
		double longitude =  startPosition.longitude * PI180TH;
		double altitude = startPosition.altitude + z;
		
		double dLatitude = x /  (AVG_EARTH_RADIUS + startPosition.altitude);
		double dLongitude = y / ((AVG_EARTH_RADIUS + startPosition.altitude)*Math.cos (latitude));
		latitude -= dLatitude;
		longitude += dLongitude;
		
		latitude /= PI180TH;
		longitude /= PI180TH;
		
		return new PolarCoordinate (latitude, longitude, altitude);
	}

}
