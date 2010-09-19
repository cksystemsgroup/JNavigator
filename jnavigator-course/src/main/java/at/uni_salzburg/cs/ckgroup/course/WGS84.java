/*
 * @(#) WGS84.java
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
 * This geodetic system implements the Department of Defense World Geodetic
 * System 1984 (WGS84)
 *
 * @see http://earth-info.nga.mil/GandG/publications/tr8350.2/tr8350_2.html NGA:
 *      DoD World Geodetic System 1984, Its Definition and Relationships with
 *      Local Geodetic Systems
 * @see at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem
 * @author   Clemens Krainer
 * @uml.dependency   supplier="at.uni_salzburg.cs.ckgroup.course.Position"
 * @uml.dependency   supplier="at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem"
 */
public class WGS84 implements IGeodeticSystem
{
	/**
	 * WGS84 specific constants.
	 */
	public static final double EQUATORIAL_AXIS = 6378137;
	public static final double POLAR_AXIS = 6356752.3142;	
	public static final double ANGULAR_ECCENTRICITY = Math.acos (POLAR_AXIS/EQUATORIAL_AXIS);
	public static final double FIRST_ECCENTRICITY = 8.1819190842622E-2;
	
	/**
	 * PI divided by 180. 
	 */
	public static final double PI180TH = Math.PI / 180;
	
	/**
	 * Construct a WGS84 geodetic system.
	 */
	public WGS84 () {
		// intentionally empty
	}
	
	/**
	 * Construct a WGS84 geodetic system. This method supports construction by
	 * the <code>ObjectFactory</code> and it does not use the provided
	 * properties.
	 * 
	 * @param props the properties to be used to construct this geodetic system.
	 */
	public WGS84 (Properties props) {
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

		double u = Math.sin (latitude*PI180TH) * FIRST_ECCENTRICITY;
		double N = EQUATORIAL_AXIS / Math.sqrt (1 - u*u);
		
		double x = (N + altitude) * Math.cos (latitude*PI180TH) * Math.cos (longitude*PI180TH);
		double y = (N + altitude) * Math.cos (latitude*PI180TH) * Math.sin (longitude*PI180TH);
		double v = POLAR_AXIS/EQUATORIAL_AXIS;
		double z = (v*v*N + altitude) * Math.sin (latitude*PI180TH);
		
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

		double newLatitude = 90;
		double latitude = 0;
		double u, v, w, N=0;
		double sin2AE = Math.sin (2*ANGULAR_ECCENTRICITY);
		double sinAE = Math.sin (ANGULAR_ECCENTRICITY);
		
		while (Math.abs (latitude - newLatitude) > 1E-13) {
			latitude = newLatitude;
			
			u = Math.sin (latitude) * Math.sin (ANGULAR_ECCENTRICITY);
			N = EQUATORIAL_AXIS / Math.sqrt (1 - u*u);
			
			v = N * Math.sin (latitude);
			w = N * Math.cos (latitude);
			
			double numerator = EQUATORIAL_AXIS*EQUATORIAL_AXIS*z + v*v*v*sin2AE*sin2AE/4;
			double denominator = EQUATORIAL_AXIS*EQUATORIAL_AXIS*Math.sqrt (x*x + y*y) - w*w*w*sinAE*sinAE;
			newLatitude = Math.atan (numerator/denominator);
		}
		
		double cosNLat = Math.cos (newLatitude);
		double sinNLat = Math.sin (newLatitude);
		
		double altitude = cosNLat*Math.sqrt (x*x + y*y) + sinNLat*(z + sinAE*sinAE*N*sinNLat) - N;
		
		double longitude = Math.asin (y / ((N + altitude)*cosNLat));
		
		return new PolarCoordinate (newLatitude/PI180TH, longitude/PI180TH, altitude); 
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
		 * position rectOldPosition.
		 */
		CartesianCoordinate normalVector = new CartesianCoordinate (rectOldPosition.x, rectOldPosition.y, rectOldPosition.z*EQUATORIAL_AXIS*EQUATORIAL_AXIS/(POLAR_AXIS*POLAR_AXIS));
		
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
		
//		double u = Math.sin (latitude) * FIRST_ECCENTRICITY;
//		double N = EQUATORIAL_AXIS / Math.sqrt (1 - u*u);
		double N = EQUATORIAL_AXIS;
		
		// TODO This is only an approximation using a sphere with radius N
		double dLatitude = x /  (N+startPosition.altitude);
		double dLongitude = y / ((N+startPosition.altitude)*Math.cos (latitude));
		latitude -= dLatitude;
		longitude += dLongitude;
		
		latitude /= PI180TH;
		longitude /= PI180TH;
		
		return new PolarCoordinate (latitude, longitude, altitude);
	}
}
