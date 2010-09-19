/*
 * @(#) SimplePilot.java
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

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.course.CourseData;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.filter.IFilter;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class implements a simple pilot that is able to navigate the JAviator
 * along a given set course.
 * 
 * @author Clemens Krainer
 */
public class SimplePilot implements IPilot
{
	/**
	 * PI divided by 180. 
	 */
	public static final double PI180TH = Math.PI / 180;
	
	/**
	 * The property key prefix of the course filter. 
	 */
	public static final String PROP_COURSE_FILTER_PREFIX = "course.filter.";
	
	/**
	 * The property key for the maximum allowed slant angle of the JAviator plant.
	 */
	public static final String PROP_SLANT_ANGLE = "maximum.slant.angle";
	
	/**
	 * The list of pilot interceptors to be invoked.
	 */
//	private Vector interceptors = new Vector ();
	
	/**
	 * The current set course supplier.
	 */
	private ISetCourseSupplier setCourseSupplier;
	
	/**
	 * The current position provider.
	 */
	private IPositionProvider positionProvider;
	
	/**
	 * This variable indicates the the pilot navigates the JAviator along the
	 * set course.
	 */
	private boolean isFlyingSetCourse = false;
	
	/**
	 * This variable contains the start time of a flight along the set course.
	 */
	private long startTime;
	
	/**
	 * The time of the current invocation in milliseconds.
	 */
	private long currentFlightTime;
	
	/**
	 * The time of the last invocation in milliseconds.
	 */
	private long oldFlightTime;
	
	/**
	 * The maximum allowed slant angle of the JAviator plant.
	 */
	private double maxSlantAngle;
	
	/**
	 * The filter for following the set course. 
	 */
	private IFilter courseFilter;
	
	/**
	 * This variable holds the current position at each invocation of the
	 * <code>processSensorData</code> method.
	 */
	private PolarCoordinate currentPosition = null;
	
	/**
	 * This variable holds the curent position of the last invocation of the
	 * <code>processSensorData</code> method.
	 */
	private PolarCoordinate oldPosition = null;

	/**
	 * The reference to the clock implementation.
	 */
	private IClock clock;

	/**
	 * Construct a <code>SimplePilot</code>.
	 * 
	 * @param props the <code>Properties</code> for this pilot.
	 * @throws InstantiationException 
	 */
	public SimplePilot (Properties props) throws InstantiationException
	{
		courseFilter = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_COURSE_FILTER_PREFIX, IFilter.class, props);
		maxSlantAngle = Double.parseDouble(props.getProperty(PROP_SLANT_ANGLE,"35"));
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#addPilotInterceptor(at.uni_salzburg.cs.ckgroup.pilot.IPilotInterceptor)
	 */
//	public void addPilotInterceptor (IPilotInterceptor pilotInterceptor)
//	{
//		if (pilotInterceptor != null)
//			interceptors.add (pilotInterceptor);
//	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#removePilotInterceptor(at.uni_salzburg.cs.ckgroup.pilot.IPilotInterceptor)
	 */
//	public void removePilotInterceptor (IPilotInterceptor pilotInterceptor)
//	{
//		while (interceptors.remove (pilotInterceptor))
//			continue;
//	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#setCourseSupplier(at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier)
	 */
	public void setCourseSupplier (ISetCourseSupplier setCourseSupplier)
	{
		this.setCourseSupplier = setCourseSupplier;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#setPositionProvider(at.uni_salzburg.cs.ckgroup.course.IPositionProvider)
	 */
	public void setPositionProvider (IPositionProvider positionProvider)
	{
		this.positionProvider = positionProvider;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#setClock(at.uni_salzburg.cs.ckgroup.util.IClock)
	 */
	public void setClock (IClock clock)
	{
		this.clock = clock;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#startFlyingSetCourse()
	 */
	public void startFlyingSetCourse ()
	{
		isFlyingSetCourse = true;
		startTime = clock.currentTimeMillis ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#stopFlyingSetCourse()
	 */
	public void stopFlyingSetCourse ()
	{
		isFlyingSetCourse = false;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#isFlyingSetCourse()
	 */
	public boolean isFlyingSetCourse()
	{
		return isFlyingSetCourse;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#processSensorData(at.uni_salzburg.cs.ckgroup.pilot.HardWareSensorData)
	 */
	public FlightControlData processSensorData (HardWareSensorData sensorData)
	{
		if (!isFlyingSetCourse || setCourseSupplier == null || positionProvider == null)
			return new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);		
		
		oldFlightTime = currentFlightTime;
		currentFlightTime = clock.currentTimeMillis () - startTime;
		
		oldPosition = currentPosition;
		currentPosition = positionProvider.getCurrentPosition ();
//		System.out.println ("FlightControlData.processSensorData: currentPosition:   " + currentPosition);

		if (oldPosition == null)
			return new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);		

		VehicleStatus setCoursePosition = setCourseSupplier.getSetCoursePosition (currentFlightTime);
		CourseData currentCourse = setCourseSupplier.getGeodeticSystem ().calculateSpeedAndCourse (oldPosition, currentPosition, currentFlightTime - oldFlightTime);
		CourseData setCourse = setCourseSupplier.getGeodeticSystem ().calculateSpeedAndCourse (currentPosition, setCoursePosition.position, 1);
		
		if (currentCourse == null || setCourse == null)
			return new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);
		
//		VehicleStatus navigationData = new VehicleStatus (currentPosition, currentCourse.speed, currentCourse.course, currentCourse.elevation, sensorData.yaw);

		double yaw = setCoursePosition.orientation;
		double offCourseAngle = setCoursePosition.orientation - setCourse.course;

//		double propulsion = setCourse.distance;
		double propulsion = courseFilter.apply (setCourse.distance);
		double gamma = Math.atan(propulsion*PI180TH)/PI180TH;
		double alpha = 0;
		double beta = 0;
		if (Math.abs(gamma) > 1E-1) {
			if (gamma > maxSlantAngle)  gamma = maxSlantAngle; else if (gamma < -maxSlantAngle) gamma = -maxSlantAngle;
			beta = Math.atan(Math.tan(gamma*PI180TH)*Math.cos(offCourseAngle*PI180TH)) / PI180TH;
			alpha = Math.asin(Math.sin(beta*PI180TH)*Math.cos(gamma*PI180TH)*Math.tan(offCourseAngle*PI180TH)) / PI180TH;
		}
		
		double roll = alpha;
		double pitch = -beta;
		
		double heightAboveGround = sensorData.heightAboveGround + setCoursePosition.position.altitude - currentPosition.altitude;

//		System.out.println ("FlightControlData.processSensorData: yaw="+yaw+", offCourseAngle="+offCourseAngle+
//				", distance="+setCourse.distance+
//				", propulsion="+propulsion+
//				", roll="+roll+", pitch="+pitch+", gamma="+gamma+", alpha="+alpha+", beta="+beta);

		FlightControlData flightControlData = new FlightControlData (yaw, roll, pitch, heightAboveGround);
//		System.out.println ("FlightControlData.processSensorData: flightControlData: " + flightControlData);
		
//		for (int k=0; k < interceptors.size (); k++) {
//			IPilotInterceptor interceptor = (IPilotInterceptor) interceptors.get (k);
//			flightControlData = interceptor.adjust (navigationData, flightControlData);
//		}
		
		
//		double dx = (setCoursePosition.position.latitude - currentPosition.latitude)*PI180TH*6400000.0;
//		double dy = (setCoursePosition.position.longitude - currentPosition.longitude)*PI180TH*6400000.0*Math.cos(setCoursePosition.position.latitude*PI180TH);
//		double dz = setCoursePosition.position.altitude - currentPosition.altitude;
//		System.out.println ("SimplePilot: flightControlData:"
//			+ "\t" + currentFlightTime
//			+ "\t" + setCoursePosition.position.latitude
//			+ "\t" + setCoursePosition.position.longitude
//			+ "\t" + setCoursePosition.position.altitude		
//			+ "\t" + currentPosition.latitude
//			+ "\t" + currentPosition.longitude
//			+ "\t" + currentPosition.altitude
//			+ "\t" + dx
//			+ "\t" + dy
//			+ "\t" + dz
//			+ "\t" + navigationData.courseOverGround
//			+ "\t" + navigationData.elevation
//			+ "\t" + navigationData.orientation
//			+ "\t" + navigationData.totalSpeed
//			+ "\t" + offCourseAngle
//			+ "\t" + yaw
//			+ "\t" + roll
//			+ "\t" + pitch
//			+ "\t" + heightAboveGround
//		);
		
		return flightControlData;
	}
	
	/**
	 * Return the old flight time in milliseconds since the start of the set
	 * course flight. This method is needed for testing the
	 * <code>processSensorData()</code> method.
	 * 
	 * @return the old flight time.
	 */
	long getOldFlightTime () {
		return oldFlightTime;
	}
	
	/**
	 * Return the current flight time in milliseconds since the start of the set
	 * course flight. This method is needed for testing the
	 * <code>processSensorData()</code> method.
	 * 
	 * @return the current flight time.
	 */
	long getCurrentFlightTime () {
		return currentFlightTime;
	}
	
}
