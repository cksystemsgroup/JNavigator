/*
 * @(#) JJControl.java
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
package at.uni_salzburg.cs.ckgroup.control;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.TimerTask;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.filter.IFilter;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.pilot.FlightControlData;
import at.uni_salzburg.cs.ckgroup.pilot.HardWareSensorData;
import at.uni_salzburg.cs.ckgroup.pilot.IPilot;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * @uml.dependency   supplier="at.uni_salzburg.cs.ckgroup.control.RemoteControlDaemon"
 */
public class JJControl extends TimerTask
{
	/**
	 * The key prefix of the roll filter properties.
	 */
	public static final String PROP_ROLL_FILTER_PREFIX = "roll.filter.";
	
	/**
	 * The key prefix of the roll output curve properties
	 */
	public static final String PROP_ROLL_OUTPUT_CURVE_PREFIX = "roll.output.curve.";
	
	/**
	 * The key prefix of the pitch filter properties.
	 */
	public static final String PROP_PITCH_FILTER_PREFIX = "pitch.filter.";
	
	/**
	 * The key prefix of the pitch output curve properties.
	 */
	public static final String PROP_PITCH_OUTPUT_CURVE_PREFIX = "pitch.output.curve.";
	
	/**
	 * The key prefix of the yaw filter properties.
	 */
	public static final String PROP_YAW_FILTER_PREFIX = "yaw.filter.";
	
	/**
	 * The key prefix of the yaw output curve properties.
	 */
	public static final String PROP_YAW_OUTPUT_CURVE_PREFIX = "yaw.output.curve.";
	
	/**
	 * The key prefix of the altitude filter properties.
	 */
	public static final String PROP_ALTITUDE_FILTER_PREFIX = "altitude.filter.";
	
	/**
	 * The key prefix of the altitude output curve properties.
	 */
	public static final String PROP_ALTITUDE_OUTPUT_CURVE_PREFIX = "altitude.output.curve.";
	
	/**
	 * The key prefix for the pilot properties. 
	 */
	public static final String PROP_PILOT_PREFIX = "pilot.";

	/**
	 * The key prefix of the position provider.
	 */
	public static final String PROP_POSITION_PROVIDER_PREFIX = "position.provider.";

	/**
	 * The key prefix of the set course supplier properties.
	 */
	public static final String PROP_SET_COURSE_SUPPLIER_PREFIX = "set.course.supplier.";
	
	/**
	 * The key prefix of the remote control properties
	 */
	public static final String PROP_REMOTE_CONTROL_PREFIX = "remote.control.";
	
	/**
	 * This variable refers to the roll filter.
	 */
//	private IFilter rollFilter;
	
	/**
	 * This variable refers to the roll output curve.
	 */
	private IFilter rollOutputCurve;
	
	/**
	 * This variable refers to the pitch filter.
	 */
//	private IFilter pitchFilter;

	/**
	 * This variable refers to the pitch output curve.
	 */
	private IFilter pitchOutputCurve;
	
	/**
	 * This variable refers to the yaw filter.
	 */
	private IFilter yawFilter;
	
	/**
	 * This variable refers to the yaw output curve.
	 */
	private IFilter yawOutputCurve;
	
	/**
	 * This variable refers to the altitude filter.
	 */
	private IFilter altitudeFilter;
	
	/**
	 * This variable refers to the altitude output curve.
	 */
	private IFilter altitudeOutputCurve;
	
	/**
	 * This variable refers to the position provider implementation.
	 */
	private IPositionProvider positionProvider;
	
	/**
	 * This variable refers to the pilot implementation.
	 */
	private IPilot pilot;
	
	/**
	 * This variable refers to the set course supplier implementation.
	 */
	private ISetCourseSupplier setCourseSupplier;
	
	/**
	 * This variable refers to the remote control connection.
	 */
	private IConnection remoteControl;
	
	/**
	 * This variable refers to the remote control output stream.
	 */
	private OutputStream remoteControlOutputStream;
	
	/**
	 * This variable holds the current sensor data.
	 */
	private HardWareSensorData sensorData;
	
	/**
	 * This variable holds the current flight control data.
	 */
	private FlightControlData flightControlData;
	
	/**
	 * Construct a <code>JJControl</code> object.
	 * 
	 * @param props the properties to be used 
	 * @throws InstantiationException thrown if the <code>ObjectFactory</code> cannot create an object.
	 * @throws IOException thrown if the connection to the remote control fails.
	 */
	public JJControl (Properties props) throws InstantiationException, IOException
	{
//		rollFilter = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_ROLL_FILTER_PREFIX, IFilter.class, props);
		rollOutputCurve = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_ROLL_OUTPUT_CURVE_PREFIX, IFilter.class, props);
//		pitchFilter = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_PITCH_FILTER_PREFIX, IFilter.class, props);
		pitchOutputCurve = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_PITCH_OUTPUT_CURVE_PREFIX, IFilter.class, props);
		yawFilter = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_YAW_FILTER_PREFIX, IFilter.class, props);
		yawOutputCurve = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_YAW_OUTPUT_CURVE_PREFIX, IFilter.class, props);
		altitudeFilter = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_ALTITUDE_FILTER_PREFIX, IFilter.class, props);
		altitudeOutputCurve = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_ALTITUDE_OUTPUT_CURVE_PREFIX, IFilter.class, props);
		
		pilot = (IPilot) ObjectFactory.getInstance ().instantiateObject (PROP_PILOT_PREFIX, IPilot.class, props);
		positionProvider = (IPositionProvider) ObjectFactory.getInstance ().instantiateObject (PROP_POSITION_PROVIDER_PREFIX, IPositionProvider.class, props);
		setCourseSupplier = (ISetCourseSupplier) ObjectFactory.getInstance ().instantiateObject (PROP_SET_COURSE_SUPPLIER_PREFIX, ISetCourseSupplier.class, props);
		
		pilot.setPositionProvider (positionProvider);
		pilot.setCourseSupplier (setCourseSupplier);
		
		remoteControl = (IConnection) ObjectFactory.getInstance ().instantiateObject (PROP_REMOTE_CONTROL_PREFIX, IConnection.class, props);
		remoteControlOutputStream = remoteControl.getOutputStream();
		sensorData = new HardWareSensorData (0,0,0,0);
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run ()
	{
		PolarCoordinate pos = positionProvider.getCurrentPosition();
		if (pos == null)
			return;
		
		sensorData.heightAboveGround = pos.altitude;

		flightControlData = pilot.processSensorData(sensorData);
		
		sensorData.roll = 0;
		sensorData.pitch = 0;
		sensorData.yaw = positionProvider.getCourseOverGround().doubleValue();
		
		double roll = flightControlData.roll;
		double rollRaw = rollOutputCurve.apply (roll);
		double pitch = flightControlData.pitch;
		double pitchRaw = pitchOutputCurve.apply (pitch);
		
		double yawDifference = flightControlData.yaw - sensorData.yaw;
		while (yawDifference < 0)
			yawDifference += 360;
		double yaw = yawFilter.apply (yawDifference);
//		double yaw = 1.417322835*5;
		double yawRaw = yawOutputCurve.apply(yaw);
		
		double altitudeDifference = flightControlData.heightAboveGround - sensorData.heightAboveGround;
		double coll = altitudeFilter.apply (altitudeDifference);
		double collRaw = altitudeOutputCurve.apply(coll);
		
//		System.out.println ("JJControl: coll=" + coll + ", diff=" + altitudeDifference + ", flightControlData.heightAboveGround=" + flightControlData.heightAboveGround +
//				", sensorData.heightAboveGround=" + sensorData.heightAboveGround);
//		System.out.println ("JJControl: coll=" + coll + ", roll=" + roll + ", pitch=" + pitch + ", yaw=" + yaw + ", yd=" + yawDifference + ", sdYaw=" + sensorData.yaw);
		
		byte[] msg = new byte[6];
		msg[0] = (byte) 0x02;
		msg[1] = (byte) yawRaw;
		msg[2] = (byte) collRaw;
		msg[3] = (byte) pitchRaw;
		msg[4] = (byte) rollRaw;
		msg[5] = (byte) (msg[0] ^ msg[1] ^ msg[2] ^ msg[3] ^ msg[4]);
		
		try {
//			System.out.println ("JJControl: Message received: yaw=" + (0xFF & msg[1]) + ", thrust=" + (0xFF & msg[2]) +
//					", pitch=" + (0xFF & msg[3]) + ", roll=" + (0xFF & msg[4]) + " ");
			remoteControlOutputStream.write(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return the current position provider.
	 * 
	 * @return the current position provider.
	 */
	public IPositionProvider getPositionProvider () {
		return positionProvider;
	}
	
	public void startFlyingSetCourse () {
		try {
			pilot.startFlyingSetCourse ();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void stopFlyingSetCourse () {
		pilot.stopFlyingSetCourse ();
	}

}
