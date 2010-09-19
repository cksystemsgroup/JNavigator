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

import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.TrimValues;
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
public class PositionControlAlgorithm implements IControlAlgorithm {
	
	/**
	 * The property key prefix for the debug mode.
	 */
	public static final String PROP_DEBUG_MODE = "debug";
	
	/**
	 * The property key prefix for the roll controller.
	 */
	public static final String PROP_ROLL_CONTROLLER_PREFIX = "roll.controller.";
	
	/**
	 * The property key prefix for the pitch controller.
	 */
	public static final String PROP_PITCH_CONTROLLER_PREFIX = "pitch.controller.";
	
	/**
	 * The property key prefix for the yaw controller.
	 */
	public static final String PROP_YAW_CONTROLLER_PREFIX = "yaw.controller.";
	
	/**
	 * The property key prefix for the altitude controller.
	 */
	public static final String PROP_ALTITUDE_CONTROLLER_PREFIX = "altitude.controller.";
	
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
	 * This variable holds the reference to the roll controller.
	 */
	private IController rollController;
	
	/**
	 * This variable holds the reference to the pitch controller.
	 */
	private IController pitchController;
	
	/**
	 * This variable holds the reference to the yaw controller.
	 */
	private IController yawController;
	
	/**
	 * This variable holds the reference to the altitude controller.
	 */
	private IController altitudeController;
	
	/**
	 * This variable holds the reference to the X controller.
	 */
	private IController xController;
	
	/**
	 * This variable holds the reference to the Y controller.
	 */
	private IController yController;
	
	/**
	 * This variable contains the initial motor rotation speed.
	 */
	private double motorLiftOffRpm;
	
	/**
	 * The trim value for roll.
	 */
	private double rollTrimValue = 0;
	
	/**
	 * The trim value for pitch.
	 */
	private double pitchTrimValue = 0;
	
	/**
	 * The trim value for yaw.
	 */
	private double yawTrimValue = 0;
	
	private int id = 0;
	
	/**
	 * Construct a <code>SimpleControlAlgorithm</code> object.
	 * 
	 * @param props the properties to be used for construction.
	 * @throws InstantiationException thrown in case of problems when creating the controller objects.
	 */
	public PositionControlAlgorithm (Properties props) throws InstantiationException {
		debugMode = "true".equalsIgnoreCase(props.getProperty(PROP_DEBUG_MODE, "false"));
		rollController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_ROLL_CONTROLLER_PREFIX, IController.class, props);
		pitchController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_PITCH_CONTROLLER_PREFIX, IController.class, props);
		yawController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_YAW_CONTROLLER_PREFIX, IController.class, props);
		altitudeController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_ALTITUDE_CONTROLLER_PREFIX, IController.class, props);
		xController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_POSITION_CONTROLLER_PREFIX, IController.class, props);
		yController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_POSITION_CONTROLLER_PREFIX, IController.class, props);
		motorLiftOffRpm = Double.parseDouble (props.getProperty (PROP_MOTOR_LIFT_OFF_RPM, "0"));
		id = Integer.parseInt(props.getProperty(PROP_JAVIATOR_IDENTIFICATION,"0"));
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IControlAlgorithm#apply(at.uni_salzburg.cs.ckgroup.communication.data.SensorData, at.uni_salzburg.cs.ckgroup.communication.data.NavigationData)
	 */
	public MotorSignals apply(SensorData sensorData, CommandData navigationData)
	{
		if (navigationData == null)
			return new MotorSignals (0,0,0,0,id);
		
		double rollCtrl = navigationData.getRoll();
		double pitchCtrl = navigationData.getPitch();
			
		double desiredZ = navigationData.getHeightOverGround();
		double currentZ = sensorData.getZ();
		double currentDZ = sensorData.getDz();
		double deltaZ = desiredZ - currentZ;

		double b = yawController.apply (yawTrimValue + navigationData.getYaw() - sensorData.getYaw(), sensorData.getDYaw());
		double p = pitchController.apply (pitchTrimValue + pitchCtrl - sensorData.getPitch(), sensorData.getDPitch());
		double r = rollController.apply (rollTrimValue + rollCtrl - sensorData.getRoll(), sensorData.getDRoll());
		double m = altitudeController.apply (deltaZ, currentDZ);
		m += motorLiftOffRpm;
		
//		if (++counter % 25 == 0)
//			System.out.println (
//				System.currentTimeMillis() + "\t" +
//				navigationData.getRoll() + "\t" +
//				navigationData.getPitch() + "\t" +
//				navigationData.getYaw() + "\t" +
//				navigationData.getHeightOverGround() + "\t" +
//				sensorData.getRoll() + "\t" +
//				sensorData.getPitch() + "\t" +
//				sensorData.getYaw() + "\t" +
//				sensorData.getZ() + "\t" +
//				sensorData
//				);
		first = true;
		
		return new MotorSignals (m+b+p, m-b-r, m+b-p, m-b+r,id);
	}
	
	private long counter = 0;
	private boolean first = true;
	private long startTime = 0;
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IControlAlgorithm#apply(at.uni_salzburg.cs.ckgroup.communication.data.SensorData, at.uni_salzburg.cs.ckgroup.course.VehicleStatus, at.uni_salzburg.cs.ckgroup.course.PolarCoordinate, java.lang.Double, java.lang.Double)
	 */
	public MotorSignals apply(SensorData sensorData, VehicleStatus setCourseData,
			PolarCoordinate currentPosition, Double courseOverGround, Double speedOverGround)
	{
		if (setCourseData == null || currentPosition == null || courseOverGround == null || speedOverGround == null)
			return new MotorSignals (0,0,0,0,id);
		
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
//		double rollCtrl = yController.apply (deltaY);
//		double pitchCtrl = xController.apply (deltaX);
			
		double desiredZ = setCourseData.position.altitude;
		double currentZ = sensorData.getZ();
		double currentDZ = sensorData.getDz();
		double deltaZ = desiredZ - currentZ;
		
		double desiredYaw = setCourseData.orientation;
		if (desiredYaw >= 180)
			desiredYaw -= 360;
				
		double b = yawController.apply (yawTrimValue + desiredYaw - sensorData.getYaw(), sensorData.getDYaw());
		double p = pitchController.apply (pitchTrimValue + pitchCtrl - sensorData.getPitch(), sensorData.getDPitch());
		double r = rollController.apply (rollTrimValue + rollCtrl - sensorData.getRoll(), sensorData.getDRoll());
		double m = altitudeController.apply (deltaZ, currentDZ);

		m += motorLiftOffRpm;
		
		Runtime s_runtime = Runtime.getRuntime();
		long totalMemory = s_runtime.totalMemory();
		long freeMemory = s_runtime.freeMemory();
		long usedMemory = totalMemory - freeMemory;
		
		if (debugMode && first) {
			first = false;
			System.out.println (
					"time\t" +
					"flight time\t" + 
					"reference longitude\t" +
					"reference latitude\t" +
					"current longitude\t" +
					"current latitude\t" +
					"current altitude\t" +
					"Set course longitude\t" +
					"Set course latitude\t" +
					"Set course altitude\t" +
					"current roll\t" +
					"desired roll\t" +
					"roll error\t" +
					"current pitch\t" +
					"desired pitch\t" +
					"pitch error\t" +
					"current yaw\t" +
					"desired yaw\t" +
					"yaw error\t" +
					"x reference\t" +
					"currentX\t" +
					"desiredX\t" +
					"deltaX\t" +
					"currentDX\t" +
					"y reference\t" + 
					"currentY\t" +
					"desiredY\t" +
					"deltaY\t" +
					"currentDY\t" +
					"z reference\t" + 
					"currentZ\t" +
					"desiredZ\t" +
					"deltaZ\t" +
					"currentDZ\t" +
//					"setCourseData.orientation\t" +
//					"desiredYaw\t" +
//					"sensorData.getYaw()\t" +
					"totalMemory\t" +
					"freeMemory\t" +
					"usedMemory"
					);
			startTime = System.currentTimeMillis();
		}
//		counter = 0;
		long time = System.currentTimeMillis();
		if (debugMode && counter++ % 5 == 0) {
			System.out.println (
				time + "\t" +
				(time - startTime) + "\t" +
				"13\t" +
				"48\t" +
				currentPosition.longitude + "\t" +
				currentPosition.latitude + "\t" +
				currentPosition.altitude + "\t" +
				setCourseData.position.longitude + "\t" +
				setCourseData.position.latitude + "\t" +
				setCourseData.position.altitude + "\t" +
				sensorData.getRoll() + "\t" +
				rollCtrl + "\t" +
				(sensorData.getRoll() - rollCtrl) + "\t" +
				sensorData.getPitch() + "\t" +
				pitchCtrl + "\t" +
				(sensorData.getPitch() - pitchCtrl) + "\t" +
				sensorData.getYaw() + "\t" +
				desiredYaw + "\t" +
				(sensorData.getYaw() - desiredYaw) + "\t" +
				0 + "\t" +	// x reference
				currentX + "\t" +
				desiredX + "\t" +
				deltaX + "\t" +
				currentDX + "\t" +
				0 + "\t" +	// y reference
				currentY + "\t" +
				desiredY + "\t" +
				deltaY + "\t" +
				currentDY + "\t" +
				0 + "\t" +	// z reference
				currentZ + "\t" +
				desiredZ + "\t" +
				deltaZ + "\t" +
				currentDZ + "\t" +
//				setCourseData.orientation + "\t" +
//				desiredYaw + "\t" +
//				sensorData.getYaw() + "\t" +
				totalMemory + "\t" +
				freeMemory + "\t" +
				usedMemory
			);
		}
		
//		if (time-startTime >= 10000 && time-startTime <= 14000) { 
//			rollCtrl = 0;
//			pitchCtrl = 0;
//			desiredYaw= 20;
//		}
//		double b = yawController.apply (yawTrimValue + desiredYaw - sensorData.getYaw(), sensorData.getDYaw());
//		double p = pitchController.apply (pitchTrimValue + pitchCtrl - sensorData.getPitch(), sensorData.getDPitch());
//		double r = rollController.apply (rollTrimValue + rollCtrl - sensorData.getRoll(), sensorData.getDRoll());
//		double m = altitudeController.apply (deltaZ, currentDZ);
//
//		m += motorLiftOffRpm;
		
		return new MotorSignals (m+b+p, m-b-r, m+b-p, m-b+r, id);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IControlAlgorithm#setTrimValues(at.uni_salzburg.cs.ckgroup.communication.data.TrimValues)
	 */
	public void setTrimValues(TrimValues trimValues) {
		rollTrimValue = trimValues.getRoll();
		pitchTrimValue = trimValues.getPitch();
		yawTrimValue = trimValues.getYaw();
	}

}
