/*
 * @(#) JControlPosOnly.java
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

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider;
import at.uni_salzburg.cs.ckgroup.communication.ISender;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.PilotData;
import at.uni_salzburg.cs.ckgroup.communication.data.PositionControllerParameters;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.ShutdownEvent;
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
public class JControlPosOnly extends TimerTask implements ISender, IDataTransferObjectListener {
	
	public static final String PROP_ALGORITHM_PREFIX = "algorithm.";
	
	public static final String PROP_SET_COURSE_FOLDER = "set.course.folder";
	
	public static final String PROP_CHECK_BOUNDARIES = "limiter.check.boundaries";
	public static final String PROP_NORTH_BOUNDARY = "limiter.north.boundary";
	public static final String PROP_SOUTH_BOUNDARY = "limiter.south.boundary";
	public static final String PROP_EAST_BOUNDARY = "limiter.east.boundary";
	public static final String PROP_WEST_BOUNDARY = "limiter.west.boundary";
	
	/**
	 * The associated <code>Dispatcher</code>
	 */
	private IDataTransferObjectProvider dtoProvider;

	/**
	 * The <code>Properties</code> used for construction of
	 * <code>JControlPosOnly</code>.
	 */
	private Properties props;
	
	/**
	 * The employed <code>IControlAlgorithm</code> derivative.
	 */
	private IPositionControlAlgorithm algorithm;
	
	/**
	 * The reference to the WGS84 position provider.
	 */
	private IPositionProvider positionProvider;
	
	/**
	 * The reference to the set course supplier
	 */
	private ISetCourseSupplier setCourseSupplier;

	/**
	 * The current <code>CommandData</code> for the helicopter.
	 */
	private CommandData commandData = new CommandData(0, 0, 0, 0);
	
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
	 * The name of the folder containing the set course data files.  
	 */
	private File setCourseFolder;
	
	/**
	 * This variable indicates if the boundaries for autonomous flights should
	 * be checked.
	 */
	private boolean checkBoundaries;
	
	/**
	 * Maximum allowed latitude North in degrees.
	 */
	private double northBoundary;
	
	/**
	 * Maximum allowed latitude South in degrees. 
	 */
	private double southBoundary;
	
	/**
	 * Maximum allowed latitude East in degrees.
	 */
	private double eastBoundary;
	
	/**
	 * Maximum allowed latitude West in degrees.
	 */
	private double westBoundary;
	
	/**
	 * Construct a <code>JControlPosOnly</code> object.
	 * @param props the <code>Properties</code> to be used for construction.
	 * @throws ConfigurationException thrown in case of configuration errors.
	 */
	public JControlPosOnly (Properties props) throws ConfigurationException {
		this.props = props;
		algorithm = (IPositionControlAlgorithm) ObjectFactory.getInstance ().instantiateObject (PROP_ALGORITHM_PREFIX, IPositionControlAlgorithm.class, this.props);
		setCourseFolder = new File (props.getProperty(PROP_SET_COURSE_FOLDER,"classes"));
		if (!setCourseFolder.isDirectory())
			throw new ConfigurationException("Can not chdir() set course folder " + setCourseFolder.getName());
		
		checkBoundaries = Boolean.parseBoolean (props.getProperty (PROP_CHECK_BOUNDARIES, "false"));
		
		northBoundary = Double.parseDouble (props.getProperty (PROP_NORTH_BOUNDARY, "0"));
		southBoundary = Double.parseDouble (props.getProperty (PROP_SOUTH_BOUNDARY, "0"));
		eastBoundary = Double.parseDouble (props.getProperty (PROP_EAST_BOUNDARY, "0"));
		westBoundary = Double.parseDouble (props.getProperty (PROP_WEST_BOUNDARY, "0"));
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
		dtoProvider.addDataTransferObjectListener(this, CommandData.class);
		dtoProvider.addDataTransferObjectListener(this, ShutdownEvent.class);
		dtoProvider.addDataTransferObjectListener(this, PilotData.class);
		dtoProvider.addDataTransferObjectListener(this, PositionControllerParameters.class);
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

		if (dto instanceof SensorData) {
			if (dtoProvider == null || positionProvider == null)
				return;
			SensorData sensorData = (SensorData) dto;

			if (autoPilotFlight) {
				PolarCoordinate position = positionProvider.getCurrentPosition();
				if (checkBoundaries) {
					if (boundaryBreached (position)) {
						autoPilotFlight = false;
						String responseString = "" + PilotData.CMD_RESPONSE + "ERROR Boundaries breached: position=" + position;
						System.out.println (responseString);
						// TODO fixme: CT does not know about breaches.
						PilotData response = new PilotData (responseString.getBytes());
						dtoProvider.dispatch (this, response);
						commandData = new CommandData (0,0,0,0);
						System.exit(0);
						return;
					}
				}
				Double courseOverGround = positionProvider.getCourseOverGround();
				Double speedOverGround = positionProvider.getSpeedOverGround();
				if (Double.isNaN(position.latitude) || Double.isNaN(position.latitude) || Double.isNaN(position.latitude) || courseOverGround.isNaN() || speedOverGround.isNaN()) {
					System.out.println ("SensorData: " + position + ", " + courseOverGround + ", " + speedOverGround);
				}
				long flyingTime = clock.currentTimeMillis() - autoPilotStartTime;
				VehicleStatus vehicleStatus = setCourseSupplier.getSetCoursePosition (flyingTime);
				commandData = algorithm.apply (sensorData, vehicleStatus, position, courseOverGround, speedOverGround);
			} else {
				// intentionally empty
			}
		} else if (dto instanceof CommandData) {
			commandData = (CommandData) dto;
		} else if (dto instanceof ShutdownEvent) {
			// TODO handle DTO ShutdownEvent
			throw new CommunicationException ("Can not handle ShutdownEvent yet.");
		} else if (dto instanceof PilotData) {
			handlePilotData ((PilotData)dto);
		} else if (dto instanceof PositionControllerParameters) {
			System.out.println ("PositionControllerParameters: setting parameters not implemented.");
		} else
			throw new IOException ("Can not handle IDataTransferObject object of class " + dto.getClass().getName()); 
	}

	/**
	 * @param position
	 * @return
	 */
	private boolean boundaryBreached(PolarCoordinate position) {
		
		if (position.getLatitude() > northBoundary)
			return true;

		if (position.getLatitude() < southBoundary)
			return true;

		if (position.getLongitude() > eastBoundary)
			return true;

		if (position.getLongitude() < westBoundary)
			return true;
		
		return false;
	}

	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	public void run() {
		try {
			dtoProvider.dispatch (this, commandData);
		} catch (IOException e) {
			e.printStackTrace();
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
				System.out.println ("JControlPosOnly.receive: loading set course '" + parameters + "'");
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
				System.out.println ("handlePilotData: " + msg.toString());
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
					System.out.println ("SetCourseFileSender: " + msg.toString());
					dtoProvider.dispatch (this, response);
					try { Thread.sleep(200); } catch (InterruptedException e) {;}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
