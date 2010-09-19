/*
 * @(#) MeasurementPilot.java
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
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;

/**
 * This class implements a simple pilot that performs simple flight maneuvers
 * for measurement purposes.
 * 
 * @author Clemens Krainer
 */
public class MeasurementPilot implements IPilot
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
	
	public static final String PROP_IMPULSE_DURATION = "impulse.duration";
	public static final String PROP_STABILISATION_DURATION = "stabilisation.duration";
	
	public static final String PROP_IMPULSE_PITCH = "impulse.pitch";
	public static final String PROP_IMPULSE_ROLL = "impulse.roll";
	public static final String PROP_IMPULSE_YAW = "impulse.yaw";
	public static final String PROP_IMPULSE_ALTITUDE = "impulse.altitude";
	public static final String PROP_BREAK_DURATION = "break.duration";
	
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
//	private double maxSlantAngle;
	
	/**
	 * The filter for following the set course. 
	 */
//	private IFilter courseFilter;
	
	
	private PolarCoordinate currentPosition = null;
	
	private PolarCoordinate oldPosition = null;
	
//	private double dxold = 0;
	private boolean headerPrinted = false;
	
	private int old_index = -1;
	private long[] impulseStartTimes;	
	private long[] impulseTime;
	private double[] impulsePitch;
	private double[] impulseRoll;
	private double[] impulseYaw;
	private double[] impulseAltitude;
	
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
	public MeasurementPilot (Properties props) throws InstantiationException
	{
//		courseFilter = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_COURSE_FILTER_PREFIX, IFilter.class, props);
//		maxSlantAngle = Double.parseDouble(props.getProperty(PROP_SLANT_ANGLE,"35"));

		String[] impulses = props.getProperty(PROP_IMPULSE_DURATION,"1000 0").trim().split("\\s+");
		String[] pitches = props.getProperty(PROP_IMPULSE_PITCH,"0").trim().split("\\s+");
		String[] roll = props.getProperty(PROP_IMPULSE_ROLL,"0").trim().split("\\s+");
		String[] yaw = props.getProperty(PROP_IMPULSE_YAW,"0").trim().split("\\s+");
		String[] altitude = props.getProperty(PROP_IMPULSE_ALTITUDE,"440.4").trim().split("\\s+");
		
		impulseTime = new long[impulses.length];
		impulsePitch = new double[impulses.length];
		impulseRoll = new double[impulses.length];
		impulseYaw = new double[impulses.length];
		impulseAltitude = new double[impulses.length];
		
		for (int k=0; k < impulses.length; k++) {
			impulseTime[k] = Integer.parseInt(impulses[k]);
			if (k > 0)
				impulseTime[k] += impulseTime[k-1];
			impulsePitch[k] = k < pitches.length ? Double.parseDouble(pitches[k]) : 0;
			impulseRoll[k] = k < roll.length ? Double.parseDouble(roll[k]) : 0;
			impulseYaw[k] = k < yaw.length ? Double.parseDouble(yaw[0]) : 0;
			impulseAltitude[k] = k < altitude.length ? Double.parseDouble(altitude[0]) : impulseAltitude[0];
		}
		
		impulseStartTimes = new long[impulses.length];
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

		if (oldPosition == null)
			return new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);		

		final PolarCoordinate p1 = new PolarCoordinate (48.0, 13.0, 440.4);
//		final PolarCoordinate p2 = new PolarCoordinate (48.00000896511478, 13.0, 440.4);
		final VehicleStatus setCoursePosition1 = new VehicleStatus (p1, 0, 0, 0, 0);
//		final VehicleStatus setCoursePosition2 = new VehicleStatus (p2, 0, 0, 0, 0);
//		VehicleStatus setCoursePosition = currentFlightTime > 1000 ? setCoursePosition1 : setCoursePosition2;
		VehicleStatus setCoursePosition = setCoursePosition1;
				
		CourseData currentCourse = setCourseSupplier.getGeodeticSystem ().calculateSpeedAndCourse (oldPosition, currentPosition, currentFlightTime - oldFlightTime);
		CourseData setCourse = setCourseSupplier.getGeodeticSystem ().calculateSpeedAndCourse (currentPosition, setCoursePosition.position, 1);
		
		if (currentCourse == null || setCourse == null)
			return new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);
		
//		VehicleStatus navigationData = new VehicleStatus (currentPosition, currentCourse.speed, currentCourse.course, currentCourse.elevation, sensorData.yaw);

		double yaw = setCoursePosition.orientation;
//		double offCourseAngle = setCoursePosition.orientation - setCourse.course;
		
		setCoursePosition.position.altitude = 440.4;

		int i = 0;
		while (i < impulseTime.length-1 && impulseTime[i] < currentFlightTime)
			i++;
		
		if (old_index != i) {
			impulseStartTimes[i] = currentFlightTime;
			old_index = i;
		}
		
		double pitch = impulsePitch[i];
		double roll = impulseRoll[i];
		yaw = impulseYaw[i];
		setCoursePosition.position.altitude = impulseAltitude[i];
		
		
		
		double heightAboveGround = sensorData.heightAboveGround + setCoursePosition.position.altitude - currentPosition.altitude;

		FlightControlData flightControlData = new FlightControlData (yaw, roll, pitch, heightAboveGround);
		
		double dx = (setCoursePosition.position.latitude - currentPosition.latitude)*PI180TH*6400000.0;
//		double dy = (setCoursePosition.position.longitude - currentPosition.longitude)*PI180TH*6400000.0*Math.cos(setCoursePosition.position.latitude*PI180TH);
//		double dz = setCoursePosition.position.altitude - currentPosition.altitude;
//		double ddx = (dx - dxold)/(currentFlightTime - oldFlightTime);
		
		if (!headerPrinted) {
			headerPrinted = true;
			
			for (int j=0; j < impulseTime.length; j++)
				System.out.println ("ImpulseTime["+j+"]="+impulseTime[j]);
			
			System.out.println ("SimplePilot: flightControlData:\ttime [s]\ttime[ms]" +
					"\tdx [m]\tdx [mm]\tpitch [°]\tduration" +
					"\timpulse\tsd.yaw\tsd.roll\tsd.pitch\theight above ground");
		}
		
		System.out.println ("SimplePilot: flightControlData:"
				+ "\t" + ((currentFlightTime - (i > 0 ? impulseStartTimes[1] : 0))/1000.0)
				+ "\t" +  (currentFlightTime - (i > 0 ? impulseStartTimes[1] : 0))
				+ "\t" + (dx*100.0)
				+ "\t" + dx
				+ "\t" + pitch
				+ "\t" + (i > 0 ? impulseStartTimes[i] - impulseStartTimes[i-1] : 0)
				+ "\t" + i
				+ "\t" + sensorData.yaw
				+ "\t" + sensorData.roll
				+ "\t" + sensorData.pitch
				+ "\t" + sensorData.heightAboveGround
			);
					
		
//		dxold = dx;
		
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
