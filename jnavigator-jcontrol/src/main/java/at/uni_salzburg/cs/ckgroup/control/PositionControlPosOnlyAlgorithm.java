/*
 * @(#) PositionControlAlgorithm.java
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

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class implements the position control algorithm for the JAviator
 * quadrotor helicopter.
 * 
 * @author Clemens Krainer
 */
public class PositionControlPosOnlyAlgorithm implements IPositionControlAlgorithm {
	
	/**
	 * The property key prefix for the debug mode.
	 */
	public static final String PROP_DEBUG_MODE = "debug";
	
	/**
	 * The property key prefix for the position controller.
	 */
	public static final String PROP_POSITION_CONTROLLER_PREFIX = "position.controller.";
	
	/**
	 * The property key for the initial motor rotation speed.
	 */
	public static final String PROP_MOTOR_LIFT_OFF_RPM = "motor.lift.off.rpm";
	
	/**
	 * The property key for the JAviator identification number.
	 */
	public static final String PROP_JAVIATOR_IDENTIFICATION = "javiator.identification";
	
	/**
	 * Is set to true when in debug mode.
	 */
	private boolean debugMode;
	
	/**
	 * PI divided by 180.
	 */
	public static final double PI180TH = Math.PI / 180.0;
	
	/**
	 * The gravitational acceleration in meters per seconds squared [m/s²]
	 */
	public static final double g = 9.81;
	
	/**
	 * The radius of earth in meters.
	 */
	public static final double earthRadius = 6400000.0;
	
	/**
	 * This variable holds the reference to the X controller.
	 */
	private IController xController;
	
	/**
	 * This variable holds the reference to the Y controller.
	 */
	private IController yController;
	
	/**
	 * Construct a <code>SimpleControlAlgorithm</code> object.
	 * 
	 * @param props the properties to be used for construction.
	 * @throws InstantiationException thrown in case of problems when creating the controller objects.
	 */
	public PositionControlPosOnlyAlgorithm (Properties props) throws InstantiationException {
		debugMode = "true".equalsIgnoreCase(props.getProperty(PROP_DEBUG_MODE, "false"));
		xController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_POSITION_CONTROLLER_PREFIX, IController.class, props);
		yController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_POSITION_CONTROLLER_PREFIX, IController.class, props);
	}
	
//	private long counter = 0;
//	private boolean first = true;
//	private long startTime = 0;
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IControlAlgorithm#apply(at.uni_salzburg.cs.ckgroup.communication.data.SensorData, at.uni_salzburg.cs.ckgroup.course.VehicleStatus, at.uni_salzburg.cs.ckgroup.course.PolarCoordinate, java.lang.Double, java.lang.Double)
	 */
	public CommandData apply(SensorData sensorData, VehicleStatus setCourseData,
			PolarCoordinate currentPosition, Double courseOverGround, Double speedOverGround)
	{
		if (setCourseData == null || currentPosition == null || courseOverGround == null || speedOverGround == null) {
			return new CommandData (0,0,0,0);
		}
		
		double desiredX = -earthRadius*(setCourseData.position.latitude * PI180TH);
		double currentX = -earthRadius*(currentPosition.latitude * PI180TH);
		double currentDXnr = -speedOverGround.doubleValue() * Math.cos (courseOverGround.doubleValue()*PI180TH);
		double deltaX = desiredX - currentX;

		double cosLatitude = Math.cos (setCourseData.position.latitude*PI180TH);
		double desiredY = earthRadius * cosLatitude * setCourseData.position.longitude * PI180TH;
		double currentY = earthRadius * cosLatitude * currentPosition.longitude * PI180TH;
		double currentDYnr = -speedOverGround.doubleValue() * Math.sin (courseOverGround.doubleValue()*PI180TH);
		double deltaY =  desiredY - currentY;
		
		// TODO This is wrong, but necessary because of the buggy JAviatorPlant. 
//		double sinYaw = Math.sin (navigationData.getYaw()*PI180TH);
//		double cosYaw = Math.cos (navigationData.getYaw()*PI180TH);
//
//		double currentDX = currentDXnr*cosYaw - currentDYnr*sinYaw;
//		double currentDY = currentDXnr*sinYaw + currentDYnr*cosYaw;
		double currentDX = currentDXnr;
		double currentDY = currentDYnr;
			
		double rollCtrl = yController.apply (deltaY, currentDY);
		double pitchCtrl = xController.apply (deltaX, currentDX);
		double desiredZ = setCourseData.position.altitude;
		
		double desiredYaw = setCourseData.orientation;
		if (desiredYaw >= 180)
			desiredYaw -= 360;
				
//		if (++counter > 0) {
//			System.out.println ("PC: " + courseOverGround.doubleValue() + " " + speedOverGround.doubleValue() + " " + deltaX + " " + currentDX + " " + deltaY + " " + currentDY + " " + rollCtrl + " " + pitchCtrl + " " + desiredZ);
//			counter = 0;
//		}
		
		return new CommandData (rollCtrl, pitchCtrl, desiredYaw, desiredZ);
	}

}
