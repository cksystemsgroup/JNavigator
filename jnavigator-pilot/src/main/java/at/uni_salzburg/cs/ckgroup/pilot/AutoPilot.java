/*
 * @(#) AutoPilot.java
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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider;
import at.uni_salzburg.cs.ckgroup.communication.ISender;
import at.uni_salzburg.cs.ckgroup.communication.data.AltitudeControllerParameters;
import at.uni_salzburg.cs.ckgroup.communication.data.AttitudeControllerParameters;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.FlyingMode;
import at.uni_salzburg.cs.ckgroup.communication.data.FlyingState;
import at.uni_salzburg.cs.ckgroup.communication.data.GroundReport;
import at.uni_salzburg.cs.ckgroup.communication.data.IdleLimit;
import at.uni_salzburg.cs.ckgroup.communication.data.PositionControllerParameters;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.ShutdownEvent;
import at.uni_salzburg.cs.ckgroup.communication.data.SwitchMode;
import at.uni_salzburg.cs.ckgroup.communication.data.SwitchState;
import at.uni_salzburg.cs.ckgroup.communication.data.YawControllerParameters;
import at.uni_salzburg.cs.ckgroup.control.IController;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

public class AutoPilot implements IDataTransferObjectListener, ISender, IAutoPilot {
	
    public final static Logger LOG = LoggerFactory.getLogger(AutoPilot.class);
	
	/**
	 * The property key prefix for the position controller.
	 */
	public static final String PROP_POSITION_CONTROLLER_PREFIX = "position.controller.";
	
	public static final String PROP_ALT_CTRL_PARAMS = "jcontrol.altitude.controller.";
	public static final String PROP_ATT_CTRL_PARAMS = "jcontrol.attitude.controller.";
	public static final String PROP_POS_CTRL_PARAMS = "jcontrol.x-y.controller.";
	public static final String PROP_YAW_CTRL_PARAMS = "jcontrol.yaw.controller.";
	public static final String PROP_IDLE_LIMIT = "jcontrol.idle.limit";
	
	/**
	 * PI divided by 180.
	 */
	public static final double PI180TH = Math.PI / 180.0;
	
	/**
	 * The radius of earth in meters.
	 */
	public static final double earthRadius = 6400000.0;
	
	/**
	 * The configured idle limit.
	 */
	private IdleLimit idleLimit;
	
	/**
	 * The configured parameters for the JControl altitude controller.
	 */
	private AltitudeControllerParameters altitudeControllerParameters;
	
	/**
	 * The configured parameters for the JControl attitude controller.
	 */
	private AttitudeControllerParameters attitudeControllerParameters;
	
	/**
	 * The configured parameters for the JControl position controller.
	 */
	private PositionControllerParameters positionControllerParameters;
	
	/**
	 * The configured parameters for the JControl yaw controller.
	 */
	private YawControllerParameters yawControllerParameters;
	
	/**
	 * This variable holds the reference to the X controller.
	 */
	private IController xController;
	
	/**
	 * This variable holds the reference to the Y controller.
	 */
	private IController yController;
	
	/**
	 * The associated <code>Dispatcher</code>
	 */
	private IDataTransferObjectProvider dtoProvider;
	
	/**
	 * The reference to the WGS84 position provider.
	 */
	private IPositionProvider positionProvider;
	
	/**
	 * The reference to the set course supplier
	 */
	private ISetCourseSupplier setCourseSupplier;
	
	/**
	 * This variable is true for an autopilot flight and false for manual flight.
	 */
	private boolean autoPilotFlight = false;
	
	/**
	 * The time the autopilot guided flight has started in milliseconds since the epoch. 
	 */
	private long autoPilotStartTime;
	
	/**
	 * The clock for the autopilot to work with.
	 */
	private IClock clock;

	/**
	 * The current flying mode.
	 */
	private FlyingMode mode = FlyingMode.NONE;

	/**
	 * The current flying state.
	 */
	private FlyingState state = FlyingState.NONE;
	
	/**
	 * The vehicle's current altitude over ground.
	 */
	private double altitudeOverGround = 0;
	
	/**
	 * True if the sensor data, set-course data, etc. should be logged. 
	 */
	private boolean logTheData = false;
	
	/**
	 * The file where the data should be logged into.
	 */
	private PrintWriter logWriter = null;
	
	/**
	 * Construct a <code>AutoPilot</code> object.
	 * 
	 * @param props the properties to be used for construction.
	 * @throws InstantiationException thrown in case of problems when creating the controller objects.
	 */
	public AutoPilot (Properties props) throws InstantiationException {
		idleLimit = new IdleLimit(Double.parseDouble(props.getProperty(PROP_IDLE_LIMIT,"0")));
		altitudeControllerParameters = new AltitudeControllerParameters(props, PROP_ALT_CTRL_PARAMS);
		attitudeControllerParameters = new AttitudeControllerParameters(props, PROP_ATT_CTRL_PARAMS);
		positionControllerParameters = new PositionControllerParameters(props, PROP_POS_CTRL_PARAMS);
		yawControllerParameters = new YawControllerParameters(props, PROP_YAW_CTRL_PARAMS);
		xController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_POSITION_CONTROLLER_PREFIX, IController.class, props);
		yController = (IController) ObjectFactory.getInstance().instantiateObject(PROP_POSITION_CONTROLLER_PREFIX, IController.class, props);
	}
	
	/**
	 * Apply the currently available <code>SensorData</code> from the JAviator,
	 * the set course data and the current vehicle status to the control
	 * algorithm for autopilot flight.
	 *
	 * @param sensorData the currently available <code>SensorData</code>.
	 * @param setCourseData the position and orientation from the set course.
	 * @param currentPosition the current position as a WGS84 coordinate
	 * @param courseOverGround the course over ground in degrees: 0° North, 
	 * @param speedOverGround the current speed over ground
	 * @return the new <code>CommandData</code>.
	 */
	private CommandData apply(SensorData sensorData, VehicleStatus setCourseData,
			PolarCoordinate currentPosition, Double courseOverGround, Double speedOverGround) {
		
		if (setCourseData == null || setCourseData.position == null || currentPosition == null || courseOverGround == null || speedOverGround == null)
			return new CommandData (0,0,0,0);
		
		double desiredX = -earthRadius*(setCourseData.position.latitude * PI180TH);
		double currentX = -earthRadius*(currentPosition.latitude * PI180TH);
		double currentDXnr = -speedOverGround.doubleValue() * Math.cos (courseOverGround.doubleValue()*PI180TH);
		double deltaX = desiredX - currentX;

		double cosLatitude = Math.cos (setCourseData.position.latitude*PI180TH);
		double desiredY = earthRadius * cosLatitude * setCourseData.position.longitude * PI180TH;
		double currentY = earthRadius * cosLatitude * currentPosition.longitude * PI180TH;
		double currentDYnr = -speedOverGround.doubleValue() * Math.sin (courseOverGround.doubleValue()*PI180TH);
		double deltaY =  desiredY - currentY;
		
		double currentDX = currentDXnr;
		double currentDY = currentDYnr;
		
		double desiredZ = setCourseData.position.altitude;
		
		double rollCtrl = yController.apply (deltaY, currentDY);
		double pitchCtrl = xController.apply (deltaX, currentDX);
		
		double desiredYaw = setCourseData.orientation;
		if (desiredYaw >= 180)
			desiredYaw -= 360;
		
		return new CommandData(rollCtrl, pitchCtrl, desiredYaw, desiredZ);
	}
	
	/**
	 * Set the associated dispatcher and register the <class>SensorData</code>
	 * derivative with the dispatcher.
	 * 
	 * @param dtoProvider
	 *            the associated dispatcher.
	 */
	public void setDtoProvider (IDataTransferObjectProvider dtoProvider) {
		this.dtoProvider = dtoProvider;
		dtoProvider.addDataTransferObjectListener(this, GroundReport.class);
		dtoProvider.addDataTransferObjectListener(this, SensorData.class);
//		dtoProvider.addDataTransferObjectListener(this, MotorSignals.class);
//		dtoProvider.addDataTransferObjectListener(this, MotorOffsets.class);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener#receive(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void receive(IDataTransferObject dto) throws IOException {
		// TODO Auto-generated method stub
		
		if (dto instanceof GroundReport) {
			GroundReport gr = (GroundReport) dto;
//			gr.getMotorOffsets();
//			gr.getMotorSignals();
			handleDto (gr.getSensorData());
			FlyingMode newMode = gr.getMode();
			if (mode != newMode) {
				LOG.info("Flying mode changed from " + mode + " to " + newMode);
				mode = newMode;
			}
			FlyingState newState = gr.getState();
			if (state != newState) {
				LOG.info("Flying state changed from " + state + " to " + newState);
				state = newState;
			}
			
		} else if (dto instanceof SensorData) {
			
		} else {
			handleDto (dto);
		}
		
	}
	
	/**
	 * Receive a <code>IDataTransferObject</code>.
	 * 
	 * @param dto the <code>IDataTransferObject</code> to be received.
	 * @throws IOException if an DTO can not be processed.
	 */
	int counter = 0;
	private void handleDto (IDataTransferObject dto) throws IOException {

		if (dto instanceof SensorData) {
			SensorData sensorData = (SensorData) dto;
			altitudeOverGround = sensorData.getZ();
			
			if (dtoProvider != null && positionProvider != null && autoPilotFlight) {
				PolarCoordinate position = positionProvider.getCurrentPosition();
				Double courseOverGround = positionProvider.getCourseOverGround();
				Double speedOverGround = positionProvider.getSpeedOverGround();
				long now = clock.currentTimeMillis();
				long flyingTime = now - autoPilotStartTime;
				VehicleStatus vehicleStatus = setCourseSupplier.getSetCoursePosition (flyingTime);
				CommandData cmd = apply (sensorData, vehicleStatus, position, courseOverGround, speedOverGround);
//				cmd = new CommandData (cmd.getRoll(), cmd.getPitch(), cmd.getYaw(), 0.6);
				dtoProvider.dispatch (this, cmd);
//				if (counter++ > 1000) {
//					LOG.info("SensorData is " + sensorData);
//					if (vehicleStatus.position != null)
//						LOG.info("SetCourse is " + vehicleStatus);
//					if (cmd != null)
//						LOG.info("CommandData is " + cmd);
//					LOG.info("Current position is " + position + ", course is " + courseOverGround + ", speed is " + speedOverGround);
//					counter=0;
//				}
//				int l = ((Aviator)setCourseSupplier).getCurrentVclCommandLine();
//				long t = System.currentTimeMillis();
//				pw.println(t + " line " + l + " set course " + (vehicleStatus.position != null ? vehicleStatus : "")  + ", command " + cmd);
				
				
				trace (now, flyingTime, sensorData, vehicleStatus, position, courseOverGround, speedOverGround);
				
				
			}
		}
//		else
//			throw new IOException ("Can not handle IDataTransferObject object of class " + dto.getClass().getName()); 
	}

	private void trace(long now, long flyingTime, SensorData sensorData,
			VehicleStatus setCourse, PolarCoordinate currentPosition,
			Double currentCourseOverGround, Double currentSpeedOverGround) {
		
		if (!logTheData || setCourse == null || setCourse.position == null)
			return;
		
		PolarCoordinate cp = new PolarCoordinate(currentPosition.getLatitude(), currentPosition.getLongitude(), sensorData.getZ());
		CartesianCoordinate currentCartesian = positionProvider.getGeodeticSystem().polarToRectangularCoordinates(cp);
		CartesianCoordinate setCourseCartesian = positionProvider.getGeodeticSystem().polarToRectangularCoordinates(setCourse.position);
		CartesianCoordinate motionVector = setCourseCartesian.subtract (currentCartesian);
		double distance = motionVector.norm();
		
		logWriter.println (
			now + "\t" + flyingTime + "\t" +
			distance + "\t" +
			setCourse.position.getLongitude() + "\t" +
			setCourse.position.getLatitude() + "\t" +
			setCourse.position.getAltitude() + "\t" +
			setCourseCartesian.getX() + "\t" +
			setCourseCartesian.getY() + "\t" +
			setCourseCartesian.getZ() + "\t" +
			cp.getLongitude() + "\t" +
			cp.getLatitude() + "\t" +
			cp.getAltitude() + "\t" +
			currentCartesian.getX() + "\t" +
			currentCartesian.getY() + "\t" +
			currentCartesian.getZ()
		);
		
	}

	public boolean isAutoPilotFlight() {
		return autoPilotFlight;
	}

	public void setAutoPilotFlight(boolean autoPilotFlight) {
		this.autoPilotFlight = autoPilotFlight;
	}

	public IPositionProvider getPositionProvider() {
		return positionProvider;
	}

	public void setPositionProvider(IPositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}

	public ISetCourseSupplier getSetCourseSupplier() {
		return setCourseSupplier;
	}

	public void setSetCourseSupplier(ISetCourseSupplier setCourseSupplier) {
		this.setCourseSupplier = setCourseSupplier;
	}

	public double getAltitudeOverGround() {
		return altitudeOverGround;
	}

	public void setClock(IClock clock) {
		this.clock = clock;
	}

	public void startUpEngines() throws IOException {
		autoPilotFlight = true;
		autoPilotStartTime = clock.currentTimeMillis();
		
		dtoProvider.dispatch(this, idleLimit);
		try { Thread.sleep(500); } catch (InterruptedException e) { }
		dtoProvider.dispatch(this, attitudeControllerParameters);
		try { Thread.sleep(500); } catch (InterruptedException e) { }
		dtoProvider.dispatch(this, yawControllerParameters);
		try { Thread.sleep(500); } catch (InterruptedException e) { }
		dtoProvider.dispatch(this, positionControllerParameters);
		try { Thread.sleep(500); } catch (InterruptedException e) { }
		dtoProvider.dispatch(this, altitudeControllerParameters);
		try { Thread.sleep(500); } catch (InterruptedException e) { }
		
		while (autoPilotFlight && state != FlyingState.HELI_STATE_FLYING) {
			dtoProvider.dispatch(this, new SwitchState(null));
			LOG.info("Waiting for vehicle to switch to state " + FlyingState.HELI_STATE_FLYING + ". Current state is " + state);
			waitForStateChange (state);
		}

		while (autoPilotFlight && mode != FlyingMode.HELI_MODE_MAN_CTRL) {
			dtoProvider.dispatch(this, new SwitchMode(null));
			LOG.info("Waiting for vehicle to switch to mode " + FlyingMode.HELI_MODE_MAN_CTRL + ". Current mode is " + mode);
			waitForModeChange (mode);
		}
		
		if (logTheData) {
			logWriter = new PrintWriter("auto-pilot-" + autoPilotStartTime + ".out");
		}
	}

	private void waitForModeChange (FlyingMode oldMode) {
		int maxWaits = 10;
		while (autoPilotFlight && oldMode == mode && maxWaits-- > 0) {
			try { Thread.sleep(1000); } catch (InterruptedException e) { }
		}
	}

	private void waitForStateChange(FlyingState oldState) {
		int maxWaits = 10;
		while (autoPilotFlight && oldState == state && maxWaits-- > 0) {
			try { Thread.sleep(1000); } catch (InterruptedException e) { }
		}		
	}

	public void shutDownEngines() throws IOException {
		dtoProvider.dispatch(this, new ShutdownEvent(null));
		autoPilotFlight = false;
		state = FlyingState.NONE;
		mode = FlyingMode.NONE;
		if (logTheData) {
			logWriter.close();
		}
	}
	
	
}
