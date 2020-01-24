/*
 * @(#) ProactivePilot.java
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
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.Matrix3x3;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class implements a pro-active pilot that is able to navigate the
 * JAviator along a given set course.
 *
 * The implementation employs objects that implement the
 * <code>IFlightPlan</code> interface to schedule command sequences to control
 * the JAviator.
 *
 * @author Clemens Krainer
 * @uml.dependency   supplier="at.uni_salzburg.cs.ckgroup.pilot.IFlightPlan"
 */
public class ProactivePilot implements IPilot, IFlightParameterProvider
{
	/**
	 * PI divided by 180. 
	 */
	public static final double PI180TH = Math.PI / 180;
	
	/**
	 * The property key for the maximum allowed tilt angle of the JAviator plant.
	 */
//	public static final String PROP_TILT_ANGLE = "maximum.tilt.angle";
	
	/**
	 * The property key for the average dead time between the start of a command
	 * and the reaction of the JAviator.
	 */
	public static final String PROP_AVERAGE_DEAD_TIME = "average.dead.time";
	
	/**
	 * The property key for the average response time between the end of a
	 * command and the end of the reaction of the JAviator.
	 */
	public static final String PROP_AVERAGE_RESPONSE_TIME = "average.response.time";
	
	/**
	 * The property key for the minimum deviation in hover mode.
	 */
//	public static final String PROP_MINIMUM_DEVIATION = "minimum.deviation";
	
	/**
	 * The property key for the material constant for the acceleration of the
	 * tilted JAviator.
	 */
	public static final String PROP_ACCELERATION_FACTOR = "acceleration.factor";
	
	/**
	 * The property key prefix of the speed filter.
	 */
//	public static final String PROP_SPEED_FILTER_PREFIX = "speed.filter.";
	
	/**
	 * The property key of average delay time to deliver the current position.
	 */
	public static final String PROP_AVERAGE_MESSAGE_DELAY = "message.delay";

	/**
	 * The property key of the minimum time span a command must last.
	 */
	public static final String PROP_MINIMUM_COMMAND_TIME = "minimum.command.time";
	
	/**
	 * The property key of the maximum allowed acceleration of the vehicle.
	 */
	public static final String PROP_MAXIMUM_ALLOWED_ACCELERATION = "maximum.allowed.acceleration";
	
	/**
	 * The property key of the flight plan list.
	 */
	public static final String PROP_FLIGHT_PLAN_LIST = "flight.plan.list";
	
	/**
	 * The property key prefix of a flight plan entry.
	 */
	public static final String PROP_FLIGHT_PLAN_PREFIX = "flight.plan.";
	
	/**
	 * The current set course supplier.
	 */
	private ISetCourseSupplier setCourseSupplier;

	/**
	 * The complete set course taken from the set course supplier.
	 */
	private VehicleStatus[] setCourse;
	
	/**
	 * The set course time table. 
	 */
	private long[] timeTable;
	
	/**
	 * The index in the <code>setCourse</code> and <code>timeTable</code>
	 * arrays of the last invocation of the <code>processSensorData</code>
	 * method.
	 */
	private int oldCourseIndex;
	
	/**
	 * The pilot executes this acceleration commands until it reaches the end
	 * time of the last command. This variable contains the main acceleration
	 * commands.
	 */
	private Vector<AccelerationCommand> accelerationCommands1 = new Vector<>();

	/**
	 * The pilot executes this acceleration commands until it reaches the end
	 * time of the last command. This variable contains additional acceleration
	 * commands.
	 */
	private Vector<AccelerationCommand> accelerationCommands2 = new Vector<>();

	/**
	 * This variable contains the end time of the currently executed control
	 * command. It's in milliseconds relative to the <code>startTime</code>.
	 */
//	private long commandTimeEnd;
	
	/**
	 * This variable holds the end time of the response time after the last
	 * executed command. After this point in time the pilot may execute a new
	 * command. It's in milliseconds relative to the <code>startTime</code>.
	 */
//	private long responseTimeEnd;
	
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
//	private double maxTiltAngle;
	
	/**
	 * The average dead time between the start of a command and the reaction of
	 * the JAviator in milli seconds.
	 */
	private long averageDeadTime;
	
	/**
	 * The average response time between the end of a command and the end of the
	 * reaction of the JAviator in milliseconds.
	 */
	private long averageResponseTime;
	
	/**
	 * The minimum deviation in hover mode in meters. Any deviation from set
	 * course below this value will not cause a correction maneuver.
	 */
//	private double minimumDeviation;
	
	/**
	 * The constant for the acceleration of the tilted JAviator.
	 */
	private double accelerationFactor;
	
	/**
	 * The filter for damping the speed changes. 
	 */
//	private IFilter speedFilter;
	
	/**
	 * This variable holds the current position at each invocation of the
	 * <code>processSensorData</code> method.
	 */
	private PolarCoordinate currentPosition = null;
	
	/**
	 * This variable holds the current position of the last invocation of the
	 * <code>processSensorData</code> method.
	 */
	private PolarCoordinate oldPosition = null;

	/**
	 * The time span from now to the next set course point.
	 */
	private long planTimeToNextPoint;
	
	/**
	 * The delay time to deliver the current position.
	 */
	private long positionMessageDelay;
	
	/**
	 * The minimum time span a command must last.
	 */
	private long minimumCommandTime;
	
	/**
	 * The maximum allowed acceleration of the vehicle. 
	 */
	private double maximumAllowedAcceleration;
	
	/**
	 * The flight plan estimators. The <code>ProactivePilot</code>
	 * sequentially tries each of the flight plan estimators if one of them can
	 * schedule a sequence of commands to control the vehicle.
	 */
	private IFlightPlan flightPlans[];
	
	/**
	 * The current set course position, total speed, course over ground,
	 * elevation and orientation.
	 */
	private VehicleStatus setCoursePosition;

	/**
	 * The current height above ground.
	 */
	private double heightAboveGround;
	
	/**
	 * The reference to the clock implementation.
	 */
	private IClock clock;
	
	/**
	 * Construct a <code>ProactivePilot</code>.
	 * 
	 * @param props the <code>Properties</code> for this pilot.
	 * @throws InstantiationException 
	 */
	public ProactivePilot (Properties props) throws InstantiationException
	{
//		maxTiltAngle = Double.parseDouble(props.getProperty(PROP_TILT_ANGLE,"20"));
		averageDeadTime = Long.parseLong(props.getProperty(PROP_AVERAGE_DEAD_TIME,"1000"));
		averageResponseTime = Long.parseLong(props.getProperty(PROP_AVERAGE_RESPONSE_TIME,"1000"));
//		minimumDeviation = Double.parseDouble(props.getProperty(PROP_MINIMUM_DEVIATION,"0.1"));
		accelerationFactor = Double.parseDouble(props.getProperty(PROP_ACCELERATION_FACTOR,"9.81"));
		positionMessageDelay = Long.parseLong(props.getProperty(PROP_AVERAGE_MESSAGE_DELAY,"1000"));
		minimumCommandTime = Long.parseLong(props.getProperty(PROP_MINIMUM_COMMAND_TIME,"1000"));
		maximumAllowedAcceleration = Double.parseDouble(props.getProperty(PROP_MAXIMUM_ALLOWED_ACCELERATION,"0.1"));
		
//		speedFilter = (IFilter) ObjectFactory.getInstance ().instantiateObject (PROP_SPEED_FILTER_PREFIX, IFilter.class, props);

		String flightPlanList = props.getProperty(PROP_FLIGHT_PLAN_LIST);
		if (flightPlanList == null || flightPlanList.equals(""))
			throw new InstantiationException ("Property " + PROP_FLIGHT_PLAN_LIST + " is not set or empty.");
		
		String[] flightPlanNames = flightPlanList.trim().split("\\s*,\\s*");
		
		flightPlans = new IFlightPlan [flightPlanNames.length];
		
		for (int k=0; k < flightPlanNames.length; k++) {
			String prefix = PROP_FLIGHT_PLAN_PREFIX + flightPlanNames[k] + ".";
			flightPlans[k] = (IFlightPlan) ObjectFactory.getInstance ().instantiateObject(prefix, IFlightPlan.class, props);
			flightPlans[k].setFlightParameterProvider(this);
			System.out.println ("ProactivePilot: Adding flight plan " + k + ": "+ prefix);
		}
		
//		flightPlans = new IFlightPlan[1];
//		flightPlans[0] = new FlightPlanThree (this);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#addPilotInterceptor(at.uni_salzburg.cs.ckgroup.pilot.IPilotInterceptor)
	 */
//	public void addPilotInterceptor (IPilotInterceptor pilotInterceptor)
//	{
//		throw new NotImplementedException ();
//	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#removePilotInterceptor(at.uni_salzburg.cs.ckgroup.pilot.IPilotInterceptor)
	 */
//	public void removePilotInterceptor (IPilotInterceptor pilotInterceptor)
//	{
//		throw new NotImplementedException ();
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
	
	/**
	 * Provide this pilot a clock.
	 * 
	 * @param clock a reference to the clock.
	 */
	public void setClock (IClock clock)
	{
		this.clock = clock;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#startFlyingSetCourse()
	 */
	public void startFlyingSetCourse () throws ConfigurationException
	{
		if (clock == null)
			throw new ConfigurationException ("Clock not configured.");
			
		if (setCourseSupplier == null)
			throw new ConfigurationException ("Set course not configured.");
		
		if (positionProvider == null)
			throw new ConfigurationException ("Position provider not configured.");
		
		setCourse = setCourseSupplier.getSetCourseData ();
		timeTable = setCourseSupplier.getTimeTable();
		oldCourseIndex = 0;
//		commandTimeEnd = -1;
//		responseTimeEnd = -1;
		isFlyingSetCourse = true;
		startTime = clock.currentTimeMillis ();
		accelerationCommands1.clear();
		accelerationCommands2.clear();
		
		System.out.println ("Set course:");
		for (int k=0; k < setCourse.length; k++)
			System.out.println ("[" + k + "]: " + timeTable[k] + "  " + setCourse[k]);
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
		if (!isFlyingSetCourse || clock == null || setCourseSupplier == null || positionProvider == null)
			return new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);		
		
		oldFlightTime = currentFlightTime;
		currentFlightTime = clock.currentTimeMillis () - startTime;
		
		oldPosition = currentPosition;
		currentPosition = approximateCurrentPosition ();
		
		if (oldPosition == null)
			return new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);		

		setCoursePosition = setCourseSupplier.getSetCoursePosition (currentFlightTime);
		// TODO: check! heightAboveGround might be wrong.
		heightAboveGround = sensorData.heightAboveGround + setCoursePosition.position.altitude - currentPosition.altitude;
		
//		System.out.println ("ProactivePilot: position data: oldFlightTime=" +oldFlightTime +
//				", currentFlightTime=" + currentFlightTime +
//				", oldPosition=" + oldPosition +
//				", currentPosition=" + currentPosition +
//				", setCoursePosition=" + setCoursePosition
//				);
		
		FlightControlData controlCommand = retrieveCommand (currentFlightTime);

		// We have a planned command to execute
		if (controlCommand != null)
			return controlCommand;

//		System.out.println ("ProactivePilot: type ?  ##########################################################");
				
		int courseIndex = oldCourseIndex;
		while (courseIndex+1 < timeTable.length && timeTable[courseIndex] < currentFlightTime)
			++courseIndex;

		boolean endOfSetCourse = courseIndex+1 >= timeTable.length;

		if (courseIndex != oldCourseIndex) {
			System.out.println ("ProactivePilot: type ?  courseIndex=" + courseIndex + ", endOfSetCourse=" + endOfSetCourse);
			oldCourseIndex = courseIndex;
		}
		
		long nextPointInTime = timeTable[courseIndex];
		VehicleStatus nextPosition = setCourse[courseIndex];

		int courseIndexPlusOne = courseIndex+1 < timeTable.length ? courseIndex + 1 : courseIndex;
		long nextPlusOnePointInTime = timeTable[courseIndexPlusOne];
		VehicleStatus nextPlusOnePosition = setCourse[courseIndexPlusOne];
		
		CartesianCoordinate currentPositionRect = setCourseSupplier.getGeodeticSystem ().polarToRectangularCoordinates (currentPosition);
		CartesianCoordinate oldPositionRect = setCourseSupplier.getGeodeticSystem ().polarToRectangularCoordinates (oldPosition);
		CartesianCoordinate currentVelocity = currentPositionRect.subtract (oldPositionRect).multiply (1000.0/(currentFlightTime - oldFlightTime));
		
		CartesianCoordinate nextPositionRect = setCourseSupplier.getGeodeticSystem ().polarToRectangularCoordinates (nextPosition.position);
		CartesianCoordinate nextPositionPlusOneRect = setCourseSupplier.getGeodeticSystem ().polarToRectangularCoordinates (nextPlusOnePosition.position);
		
		CartesianCoordinate nextVelocity = courseIndexPlusOne == courseIndex ? new CartesianCoordinate () : nextPositionPlusOneRect.subtract (nextPositionRect);
		long nextPlusOneDuration = nextPlusOnePointInTime - nextPointInTime;
		if (nextPlusOneDuration > 0)
			nextVelocity.multiply (1000.0/nextPlusOneDuration);
		
		CartesianCoordinate distance = nextPositionRect.subtract (currentPositionRect);
		CartesianCoordinate direction = distance.normalize ();
		
		CartesianCoordinate currentVelocityDist = direction.multiply (currentVelocity.multiply (direction));
		CartesianCoordinate nextVelocityDist = direction.multiply (nextVelocity.multiply (direction));
		
//		CartesianCoordinate currentVelocityNor = currentVelocity.subtract (currentVelocityDist);
//		CartesianCoordinate nextVelocityNor = nextVelocityDist.subtract (nextVelocityDist);
		// TODO Geschwindigkeiten quer zur zu überbrückenden Wegstrecke berücksichtigen. 
		
		for (int k=0; k < flightPlans.length; k++) {
			boolean ok = flightPlans[k].estimateFlightPlan (distance, currentVelocityDist, nextVelocityDist, nextPointInTime - currentFlightTime);
			if (ok)
				return queueAccelerationCommands  (flightPlans[k]);
		}

		return new FlightControlData (setCoursePosition.orientation, 0, 0, heightAboveGround);		
	}

	/**
	 * Approximate the real current position from the position from the position
	 * provider, the measurement delay, the speed and the course of the vehicle.
	 * 
	 * @return the approximated current position.
	 */
	private PolarCoordinate approximateCurrentPosition() {

		PolarCoordinate pos = positionProvider.getCurrentPosition();
		return pos;

//		Double course = positionProvider.getCourseOverGround();
//		Double speed = positionProvider.getSpeedOverGround();
//		
//		if (course == null || speed == null)
//			return pos;
//		
//		double distance = speed.doubleValue() * positionMessageDelay / 1000.0;
//		double x = -distance * Math.cos (course.doubleValue() * PI180TH);
//		double y = -distance * Math.sin (course.doubleValue() * PI180TH);
//		
//		PolarCoordinate result = setCourseSupplier.getGeodeticSystem ().walk(pos, x, y, 0.0);
//		
//		System.out.println ("approximateCurrentPosition: distance=" + distance + ", speed=" + speed + ", course=" + course);
//		System.out.println ("approximateCurrentPosition: provider Position " + pos);
//		System.out.println ("approximateCurrentPosition:      new position " + result);
//		return result;
	}

	/**
	 * Log the flight control data as well as some other information.
	 * 
	 * @param flightControlData the current flight control data.
	 * @param currentCourse the current course of the JAviator.
	 * @param setCoursePosition the current set course position.
	 * @param nextPositionCourse the course data to the next set course position.
	 */
//	private void logFlightControlData (FlightControlData flightControlData) {
//
//		double dx = (setCoursePosition.position.latitude - currentPosition.latitude)*PI180TH*6400000.0;
//		double dy = (setCoursePosition.position.longitude - currentPosition.longitude)*PI180TH*6400000.0*Math.cos(setCoursePosition.position.latitude*PI180TH);
//		double dz = setCoursePosition.position.altitude - currentPosition.altitude;
//		System.out.println ("ProactivePilot: flightControlData:"
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
//			+ "\t" + flightControlData.yaw
//			+ "\t" + flightControlData.roll
//			+ "\t" + flightControlData.pitch
//			+ "\t" + flightControlData.heightAboveGround
//		);
//	}
	
	/**
	 * From a given acceleration vector estimate the flight control data.
	 *  
	 * @param acceleration the acceleration vector.
	 * @return the flight control data.
	 */
	private FlightControlData createFlightControlData (CartesianCoordinate acceleration) {
		
		Matrix3x3 rot = new Matrix3x3 (0, 90-currentPosition.latitude, currentPosition.longitude);
		Matrix3x3 ori = new Matrix3x3 (0, 0, -setCoursePosition.orientation);
		rot = ori.multiply(rot.transpose());

		CartesianCoordinate command = rot.multiply(acceleration);
		
		double roll = Math.asin (command.y / accelerationFactor) / PI180TH; 
		double pitch = Math.asin (command.x / accelerationFactor) / PI180TH;
//		System.out.println ("ProactivePilot.createFlightControlData(): roll=" + roll + ", pitch=" + pitch);
		
		FlightControlData flightControlData = new FlightControlData (setCoursePosition.orientation, roll, pitch, heightAboveGround);
		
//		logFlightControlData (flightControlData);
		return flightControlData;
	}
	
	/**
	 * Retrieve the current executed command from the set of prepared control commands.
	 * 
	 * @param currentFlightTime the time starting at the beginning of the automatic flight.
	 * @return the data for flight control.
	 */
	private FlightControlData retrieveCommand (long currentFlightTime)
	{	
		AccelerationCommand a1 = null;
		while (accelerationCommands1.size() > 0) {
			a1 = (AccelerationCommand) accelerationCommands1.get(0);
			if (currentFlightTime < a1.endTime)
				break;
			accelerationCommands1.remove(0);
		}

		AccelerationCommand a2 = null;
		while (accelerationCommands1.size() > 0) {
			a2 = (AccelerationCommand) accelerationCommands1.get(0);
			if (currentFlightTime < a2.endTime)
				break;
			accelerationCommands1.remove(0);
		}

		CartesianCoordinate a = null;
		if (a1 != null) {
			a = a1.acceleration;
			if (a2 != null)  a = a.add(a2.acceleration);
		} else {
			if (a2 != null)  a = a2.acceleration;
		}
			
		if (a == null)
			return null;
		
		return createFlightControlData (a);
	}

	/**
	 * Read the estimated acceleration schedule from a flight plan and store them in the acceleration queue.
	 * 
	 * @param flightPlan the flight plan containing the newly estimated acceleration schedule.
	 * @return the data for flight control.
	 */
	private FlightControlData queueAccelerationCommands (IFlightPlan flightPlan) {

		long commandTimes[] = flightPlan.getCommandTimes();
		CartesianCoordinate accelerations[] = flightPlan.getAccelerations();
		
		for (int i=1; i < commandTimes.length; i++) 
			commandTimes[i] += commandTimes[i-1]; 
		             
		for (int k=0; k < commandTimes.length; k++) {
			AccelerationCommand cmd = new AccelerationCommand (currentFlightTime + commandTimes[k], accelerations[k]);
			accelerationCommands1.add(cmd);
		}

		return createFlightControlData (accelerations[0]);
	}

	/**
	 * Return the old flight time in milliseconds since the start of the set
	 * course flight. This method is needed for testing the
	 * <code>processSensorData()</code> method.
	 * 
	 * @return the old flight time.
	 */
	long getOldFlightTime ()
	{
		return oldFlightTime;
	}
	
	/**
	 * Return the current flight time in milliseconds since the start of the set
	 * course flight. This method is needed for testing the
	 * <code>processSensorData()</code> method.
	 * 
	 * @return the current flight time.
	 */
	long getCurrentFlightTime ()
	{
		return currentFlightTime;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getCommandDeadTime()
	 */
	public long getCommandDeadTime()
	{
		return averageDeadTime;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getCommandResponseTime()
	 */
	public long getCommandResponseTime()
	{
		return averageResponseTime;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getPlanTimeToNextSetCoursePoint()
	 */
	public long getPlanTimeToNextSetCoursePoint()
	{
		return planTimeToNextPoint;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getPositionMessageDelay()
	 */
	public long getPositionMessageDelay()
	{
		return positionMessageDelay;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getMinimumCommandTime()
	 */
	public long getMinimumCommandTime()
	{
		return minimumCommandTime;
	}
	
	/**
	 * This is a helper class that contains an acceleration vector and the end
	 * time of the acceleration relative to the start time of an automatic
	 * flight.
	 * 
	 * @author Clemens Krainer
	 */
	private class AccelerationCommand
	{
		/**
		 * The end time of the acceleration relative to the start time of an automatic flight.
		 */
		public long endTime;
		
		/**
		 * The acceleration vector.
		 */
		public CartesianCoordinate acceleration;
		
		/**
		 * Construct an <code>AccelerationCommand</code> object.
		 * 
		 * @param endTime the end time of the acceleration relative to the start time of an automatic flight.
		 * @param acceleration the acceleration vector.
		 */
		public AccelerationCommand (long endTime, CartesianCoordinate acceleration)
		{
			this.endTime = endTime;
			this.acceleration = acceleration;
		}
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IFlightParameterProvider#getMaximumAcceleration()
	 */
	public double getMaximumAcceleration()
	{
		return maximumAllowedAcceleration;
	}

}
