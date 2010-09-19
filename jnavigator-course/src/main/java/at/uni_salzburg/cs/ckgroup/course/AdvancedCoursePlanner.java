/*
 * @(#) AdvancedCoursePlanner.java
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

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This course planner estimates accelerations to a given set course to smoothen
 * the transition between course sections as much as possible.
 * 
 * @author Clemens Krainer
 */
public class AdvancedCoursePlanner implements ICoursePlanner {
	
	/**
	 * The property key constant for the maximum acceleration [m/s].
	 */
	public static final String PROP_MAXIMUM_ALLOWED_ACCELERATION = "maximum.allowed.acceleration";
	
	/**
	 * The maximum allowed acceleration [m/s] of the vehicle.
	 */
	private double maximumAcceleration;
	
	/**
	 * Construct an <code>AdvancedCoursePlanner</code>.
	 * 
	 * @param props the <code>Properties</code> to be used for construction. 
	 */
	public AdvancedCoursePlanner (Properties props) throws ConfigurationException {
		String maximumAccelerationString = props.getProperty (PROP_MAXIMUM_ALLOWED_ACCELERATION);
		if (maximumAccelerationString == null || "".equals (maximumAccelerationString))
			throw new ConfigurationException ("Please set the property " + PROP_MAXIMUM_ALLOWED_ACCELERATION);
		maximumAcceleration = Double.parseDouble (maximumAccelerationString);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.ICoursePlanner#planCourse(at.uni_salzburg.cs.ckgroup.course.Section[], at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem)
	 */
	public SectionFlightPlan[] planCourse(Section[] sections, IGeodeticSystem geodeticSystem) throws ConfigurationException {
		
		double averageSpeeds[] = new double [sections.length];
		double distances[] = new double [sections.length];
		CartesianCoordinate distanceVectors[] = new CartesianCoordinate [sections.length];
		double speedFactor[] = new double [sections.length];
		
		
		SectionFlightPlan[] flightPlans = new SectionFlightPlan[sections.length];
		for (int k=0; k < sections.length; k++) {
			flightPlans[k] = new SectionFlightPlan (sections[k]);
			
			if (sections[k].getEndPosition() != null) {
				CartesianCoordinate startPosition = geodeticSystem.polarToRectangularCoordinates (sections[k].getStartPosition());
				CartesianCoordinate endPosition = geodeticSystem.polarToRectangularCoordinates (sections[k].getEndPosition());
				distanceVectors[k] = endPosition.subtract(startPosition);
				distances[k] = distanceVectors[k].norm();
				averageSpeeds[k] = 1000 * distances[k] / sections[k].getTravelTime();
				
				double xx = k == 0 ? 1 : distanceVectors[k].norm() * distanceVectors[k-1].norm();
				speedFactor[k] = k == 0 || xx == 0 ? 1 : distanceVectors[k].multiply(distanceVectors[k-1]) / xx;
			} else {
				distanceVectors[k] = new CartesianCoordinate (0,0,0);
				distances[k] = 0;
				averageSpeeds[k] = 0;
				speedFactor[k] = 1;
			}
		}
		
		for (int k=0; k < sections.length; k++) {
			double v1 = 0;
			double v3 = 0;
			
			if (sections[k].getEndPosition() != null) {
				v1 = k == 0 ? 0 : averageSpeeds[k-1] * speedFactor[k];
				v3 = k == sections.length-1 ? 0 : averageSpeeds[k+1] * speedFactor[k];
			}
			
			double x =  averageSpeeds[k] >= v1 ? 1 : -1;
			double y =  averageSpeeds[k] <= v3 ? 1 : -1;

			double v2 = estimateV2 (v1, v3, x, y, 0.001*sections[k].getTravelTime(), distances[k]);
			if (v2 < 0)
				throw new ConfigurationException ("Can not estimate flight plan! Section=" + k + ", v1=" + v1 + ", v3=" + v3 +", x=" + x + ", y=" + y + ", v2=" + v2);
			
			double t1 = x * (v2 - v1) / maximumAcceleration;
			double t2 = y * (v3 - v2) / maximumAcceleration;
//			if (t1 < 0 || t2 < 0 || t1+t2 > 0.001*sections[k].getTravelTime())
//				throw new ConfigurationException ("Can not estimate flight plan! Section=" + k + ", v1=" + v1 + ", v3=" + v3 +", x=" + x + ", y=" + y + ", v2=" + v2);

			long[] durations = new long[3];
			double[] velocities = new double[4];
			durations[0] = (long) (1000.0 * t1);
			durations[2] = (long) (1000.0 * t2);
			durations[1] = sections[k].getTravelTime() - durations[0] - durations[2];
			velocities[0] = v1;
			velocities[1] = v2;
			velocities[2] = v2;
			velocities[3] = v3;
			flightPlans[k].setValues(durations, velocities);
		}

		return flightPlans;
	}
	
	/**
	 * Estimate the middle speed of a set course section.
	 * 
	 * @param v1 the speed of the vehicle when entering the set course section.
	 * @param v3 the speed of the vehicle when leaving the set course section.
	 * @param x acceleration indicator when entering a set course section: acceleration = 1, break = -1
	 * @param y acceleration indicator when leaving a set course section: acceleration = 1, break = -1
	 * @param time the time the vehicle needs to travel through the set course section in seconds
	 * @param distance the distance to travel in the set course section.
	 * @return the middle speed of the set course section.
	 */
	double estimateV2 (double v1, double v3, double x, double y, double time, double distance) {
		
		if (x == y)
			return 0.5 * (2*distance*maximumAcceleration + x*v1*v1 -y*v3*v3) / (x*v1 - y*v3 + maximumAcceleration*time);
		
		double p = 2 * (x*v1 - y*v3 + maximumAcceleration*time) / (y - x);
		double q = (y*v3*v3 - x*v1*v1 - 2*distance*maximumAcceleration) / (y - x);
		double root = p*p/4 - q;
		if (root < 0)
			return -1;
		root = Math.sqrt(root);
		double v21 = -p/2 + root;
		double v22 = -p/2 - root;
		return v22 < 0 ? v21 : v22;
	}

}
