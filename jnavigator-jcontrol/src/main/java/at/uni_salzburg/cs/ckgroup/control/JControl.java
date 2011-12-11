/*
 * @(#) JControl.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Properties;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider;
import at.uni_salzburg.cs.ckgroup.communication.ISender;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.FlyingMode;
import at.uni_salzburg.cs.ckgroup.communication.data.FlyingState;
import at.uni_salzburg.cs.ckgroup.communication.data.GroundReport;
import at.uni_salzburg.cs.ckgroup.communication.data.JaviatorData;
import at.uni_salzburg.cs.ckgroup.communication.data.KeepAlive;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorOffsets;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.PilotData;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.ShutdownEvent;
import at.uni_salzburg.cs.ckgroup.communication.data.SwitchMode;
import at.uni_salzburg.cs.ckgroup.communication.data.SwitchState;
import at.uni_salzburg.cs.ckgroup.communication.data.TrimValues;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class implements the java controller for the JAviator. 
 * 
 * @author Clemens Krainer
 */
/**
 * @author clem
 *
 */
public class JControl extends TimerTask implements ISender, IDataTransferObjectListener {
	
	Logger LOG = Logger.getLogger(JControl.class);
	
	public static final String PROP_ALGORITHM_PREFIX = "algorithm.";
	
	public static final String PROP_FORCED_GC_CYCLE = "forced.gc.cycle";
	
	public static final String PROP_SET_COURSE_FOLDER = "set.course.folder";
	
	private MotorSignals actuatorData = new MotorSignals ((short)0,(short)0,(short)0,(short)0,(short)0);
	
	private MotorOffsets motorOffsets = new MotorOffsets((short)0,(short)0,(short)0,(short)0);
	
	private double oldX = 0;
	private double oldY = 0;
	private double oldZ = 0;
	
	/**
	 * The associated <code>Dispatcher</code>
	 */
	private IDataTransferObjectProvider dtoProvider;

	/**
	 * The <code>Properties</code> used for construction of
	 * <code>JControl</code>.
	 */
	private Properties props;
	
	/**
	 * The employed <code>IControlAlgorithm</code> derivative.
	 */
	private IControlAlgorithm algorithm;
	
	/**
	 * The reference to the WGS84 position provider.
	 */
	private IPositionProvider positionProvider;
	
	/**
	 * The reference to the set course supplier
	 */
	private ISetCourseSupplier setCourseSupplier;
	
	/**
	 * This variable indicates the flying mode of the helicopter.
	 * 
	 * @see FlyingMode
	 */
	private FlyingMode mode = FlyingMode.HELI_MODE_POS_CTRL;
	
	/**
	 * This variable indicates the flying mode of the helicopter.
	 * 
	 * @see FlyingState
	 */
	private FlyingState state = FlyingState.HELI_STATE_SHUTDOWN;
	
	/**
	 * The current <code>NavigationData</code> from the ground station.
	 */
	private CommandData navigationData;
		
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
	 * The number of cycles to run before a GC is forced. Deactivated by -1.
	 */
	private int forcedGcCycle;
	
	/**
	 * The name of the folder containing the set course data files.  
	 */
	private File setCourseFolder;
	
//	/**
//	 * True if the sensor data, set-course data, etc. should be logged. 
//	 */
//	private boolean logTheData = false;
//	
//	/**
//	 * The file where the data schould be logged into.
//	 */
//	private PrintWriter logWriter = null;
	
	/**
	 * Construct a <code>JControl</code> object.
	 * @param props the <code>Properties</code> to be used for construction.
	 * @throws ConfigurationException thrown in case of configuration errors.
	 */
	public JControl (Properties props) throws ConfigurationException {
		this.props = props;
		algorithm = (IControlAlgorithm) ObjectFactory.getInstance ().instantiateObject (PROP_ALGORITHM_PREFIX, IControlAlgorithm.class, this.props);
		forcedGcCycle = Integer.parseInt(props.getProperty(PROP_FORCED_GC_CYCLE,"-1"));
		setCourseFolder = new File (props.getProperty(PROP_SET_COURSE_FOLDER,"classes"));
		if (!setCourseFolder.isDirectory())
			throw new ConfigurationException("Can not chdir() set course folder " + setCourseFolder.getName());
//		if (logTheData) {
//			try {
//				logWriter = new PrintWriter("jcontrol.out");
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//		}
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
		dtoProvider.addDataTransferObjectListener(this, SensorData.class);
//		dtoProvider.addDataTransferObjectListener(this, JaviatorData.class);
		dtoProvider.addDataTransferObjectListener(this, CommandData.class);
		dtoProvider.addDataTransferObjectListener(this, ShutdownEvent.class);
		dtoProvider.addDataTransferObjectListener(this, PilotData.class);
		dtoProvider.addDataTransferObjectListener(this, TrimValues.class);
		dtoProvider.addDataTransferObjectListener(this, SwitchState.class);
		dtoProvider.addDataTransferObjectListener(this, SwitchMode.class);
	}
	
	/**
	 * Set the associated position provider for WGS84 positions.
	 * 
	 * @param positionProvider the current position provider.
	 */
	public void setPositionProvider (IPositionProvider positionProvider) {
		this.positionProvider = positionProvider;
	}
	
	public void setSetCourseSupplier (ISetCourseSupplier setCourseSupplier) {
		this.setCourseSupplier = setCourseSupplier;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener#receive(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void receive (IDataTransferObject dto) throws IOException {
		
//		if (dto instanceof GroundReport) {
//			GroundReport gr = (GroundReport) dto;
//			handleDto (gr.getSensorData());
//		} else {
			handleDto (dto);
//		}
	}
	
	private void handleDto (IDataTransferObject dto) throws IOException {

		if (dto instanceof SensorData) {
			if (state != FlyingState.HELI_STATE_FLYING) {
				actuatorData = new MotorSignals ((short)0,(short)0,(short)0,(short)0,(short)0);
				return;
			}
			
			SensorData sensorData = (SensorData) dto;

			if (dtoProvider != null && positionProvider != null && autoPilotFlight) {
				PolarCoordinate position = positionProvider.getCurrentPosition();
				Double courseOverGround = positionProvider.getCourseOverGround();
				Double speedOverGround = positionProvider.getSpeedOverGround();
				long now = clock.currentTimeMillis();
				long flyingTime = now - autoPilotStartTime;
				VehicleStatus vehicleStatus = setCourseSupplier.getSetCoursePosition (flyingTime);
				actuatorData = algorithm.apply (sensorData, vehicleStatus, position, courseOverGround, speedOverGround);
//				trace (now, flyingTime, sensorData, vehicleStatus, position, courseOverGround, speedOverGround);
			} else {
				actuatorData = algorithm.apply (sensorData, navigationData);
			}
			
			GroundReport gr = new GroundReport();
			gr.setSensorData(sensorData);
			gr.setMotorOffsets(motorOffsets);
			gr.setMotorSignals(actuatorData);
			gr.setMode(mode);
			gr.setState(state);
			dtoProvider.dispatch (this, gr);
			
			
		} else if (dto instanceof JaviatorData) {
			if (state != FlyingState.HELI_STATE_FLYING) {
				actuatorData = new MotorSignals ((short)0,(short)0,(short)0,(short)0,(short)0);
				return;
			}
			
			JaviatorData jd = (JaviatorData)dto;
			SensorData sensorData = new SensorData(jd);
			sensorData.setDx(sensorData.getX() - oldX);
			oldX = sensorData.getX();
			sensorData.setDy(sensorData.getY() - oldY);
			oldY = sensorData.getY();
			sensorData.setDz(sensorData.getZ() - oldZ);
			oldZ = sensorData.getZ();
			actuatorData = algorithm.apply (sensorData, navigationData);
			GroundReport gr = new GroundReport();
			gr.setSensorData(sensorData);
			gr.setMotorOffsets(motorOffsets);
			gr.setMotorSignals(actuatorData);
			gr.setMode(mode);
			gr.setState(state);
			dtoProvider.dispatch (this, gr);
			
			
		} else if (dto instanceof CommandData) {
			navigationData = (CommandData) dto;
			
		} else if (dto instanceof ShutdownEvent) {
			LOG.info("ShutdownEvent received.");
			state = FlyingState.HELI_STATE_SHUTDOWN;
			actuatorData = new MotorSignals ((short)0,(short)0,(short)0,(short)0,(short)0);
			// TODO handle DTO ShutdownEvent
//			throw new CommunicationException ("Can not handle ShutdownEvent yet.");
			
		} else if (dto instanceof SwitchState) {
			LOG.info("SwitchState received.");
			switch (state) {
			case HELI_STATE_GROUND:
				state = FlyingState.HELI_STATE_FLYING;
				break;
			case HELI_STATE_FLYING:
				state = FlyingState.HELI_STATE_GROUND;
				break;
			case HELI_STATE_SHUTDOWN:
				state = FlyingState.HELI_STATE_GROUND;
				break;
			default:
				state = FlyingState.HELI_STATE_SHUTDOWN;
			}
			dtoProvider.dispatch (this, dto);
			
		} else if (dto instanceof SwitchMode) {
			LOG.info("SwitchMode received.");
			switch (mode) {
			case HELI_MODE_MAN_CTRL:
				mode = FlyingMode.HELI_MODE_POS_CTRL;
				break;
			case HELI_MODE_POS_CTRL:
				mode = FlyingMode.HELI_MODE_MAN_CTRL;
				break;
			default:
				mode = FlyingMode.HELI_MODE_MAN_CTRL;
			}
			dtoProvider.dispatch (this, dto);
			
		} else if (dto instanceof PilotData) {
			LOG.info("PilotData received.");
			handlePilotData ((PilotData)dto);
			
		} else if (dto instanceof TrimValues) {
			TrimValues trimValues = (TrimValues) dto;
			LOG.info ("JControl.receive: new trim values roll=" + trimValues.getRoll() +
					", pitch=" + trimValues.getPitch() + ", yaw=" + trimValues.getYaw());
			algorithm.setTrimValues(trimValues);
		
		} else if (dto instanceof KeepAlive) {
			System.out.print(".");
			
		} else
			throw new IOException ("Can not handle IDataTransferObject object of class " + dto.getClass().getName()); 
	}

//	private void trace(long now, long flyingTime, SensorData sensorData, VehicleStatus vehicleStatus,
//			PolarCoordinate position, Double courseOverGround,
//			Double speedOverGround) {
//		
//		if (!logTheData)
//			return;
//		
//		CartesianCoordinate whereCartesian = positionProvider.getGeodeticSystem().polarToRectangularCoordinates(position);
//		CartesianCoordinate destinationCartesian = positionProvider.getGeodeticSystem().polarToRectangularCoordinates(vehicleStatus.position);
//		CartesianCoordinate motionVector = destinationCartesian.subtract (whereCartesian);
//		double distance = motionVector.norm();
//		
//		logWriter.println (
//			now + "\t" + flyingTime + "\t" +
//			distance + "\t" +
//			vehicleStatus.position.getLongitude() + "\t" +
//			vehicleStatus.position.getLatitude() + "\t" +
//			vehicleStatus.position.getAltitude() + "\t" +
//			position.getLongitude() + "\t" +
//			position.getLatitude() + "\t" +
//			position.getAltitude()
//		);
//		
//	}

	private long gcCounter = 0;
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		try {
			dtoProvider.dispatch (this, actuatorData);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (forcedGcCycle > 0 && ++gcCounter > forcedGcCycle) {
			gcCounter = 0;
			LOG.debug ("Call GC: " + System.currentTimeMillis());
			System.gc();
		}
	}

	/**
	 * Provide a clock to work with.
	 *  
	 * @param clock the provided clock.
	 */
	public void setClock (IClock clock) {
		this.clock = clock;
	}
	
	private void handlePilotData (PilotData dto) throws IOException {
		
		switch (dto.getCommand()) {
		case PilotData.CMD_START:
			autoPilotFlight = false;
			boolean apf = setCourseSupplier != null;
			String parameters = dto.getParameters ();
			if (apf) {
				LOG.info ("JControl.receive: loading set course '" + parameters + "'");
				StringBuffer msg = new StringBuffer ();
				msg.append(PilotData.CMD_RESPONSE);
				try {
					InputStream courseData = new FileInputStream (new File(setCourseFolder, parameters));
					setCourseSupplier.loadSetCourse (courseData);
					msg.append("OK LOADED ").append(parameters);
				} catch (ConfigurationException e) {
					e.printStackTrace();
					apf = false;
					StringWriter w = new StringWriter ();
					e.printStackTrace(new PrintWriter (w));
					msg.append("ERROR LOADING ").append(parameters).append("\r\n").append(w.toString());
				} catch (IOException e) {
					e.printStackTrace();
					apf = false;
					StringWriter w = new StringWriter ();
					e.printStackTrace(new PrintWriter (w));
					msg.append("ERROR LOADING ").append(parameters).append("\r\n").append(w.toString());
				}
				LOG.info ("handlePilotData: " + msg.toString());
				String msgString = msg.toString();
				String responseString = msgString.length() > 126 ? msgString.substring(0, 126) : msgString;
				PilotData response = new PilotData (responseString.getBytes());
				dtoProvider.dispatch (this, response);
			}
			autoPilotStartTime = clock.currentTimeMillis ();
			autoPilotFlight = apf;
			break;
			
		case PilotData.CMD_STOP:
			autoPilotFlight = false;
			break;
			
		case PilotData.CMD_SEND_SET_COURSE_FILE_NAMES:
			SetCourseFileSender senderThread = new SetCourseFileSender (setCourseFolder);
			senderThread.start();
			break;
		
		}
		
	}
	
	/**
	 * @return true if an autopilot flight is carried out.
	 */
	protected boolean getAutoPilotFlight () {
		return autoPilotFlight;
	}
	
	/**
	 * @return the start time in milliseconds of the last autopilot start.
	 */
	protected long getAutoPilotStartTime () {
		return autoPilotStartTime;
	}
	
	
	/**
	 * This class sends the names of the currently available set course data
	 * files to the GCS. To avoid a transceiver buffer overflow, this class
	 * sends only every 200ms one file name.
	 * 
	 * @author Clemens Krainer
	 */
	private class SetCourseFileSender extends Thread implements ISender {
		
		/**
		 * The folder containing all the set course data files.
		 */
		private File folder;
		
		/**
		 * Construct a <code>SetCourseFileSender</code>
		 * 
		 * @param folder the folder containing all the set course data files.
		 */
		public SetCourseFileSender (File folder) {
			this.folder = folder;
		}
		
		/**
		 * Read all file names of the configured set course folder and publish
		 * them via the dispatcher.
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run () {
			String [] fileNames = folder.list (new FilenameFilter() {
				public boolean accept(File dir, String name) {
					File x = new File (dir, name);
					return x.isFile() && name.endsWith(".dat");
				}
			});
			
			Arrays.sort(fileNames);
			
			try {
				for (int i=0; i < fileNames.length; ++i) {
					StringBuffer msg = new StringBuffer ();
					msg.append(PilotData.CMD_STRING_FILE_NAME).append(' ').append(i==0?'r':'k').append(' ').append(fileNames[i]);
					PilotData response = new PilotData (msg.toString().getBytes());
					LOG.info ("SetCourseFileSender: " + msg.toString());
					dtoProvider.dispatch (this, response);
					try { Thread.sleep(200); } catch (InterruptedException e) {;}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
