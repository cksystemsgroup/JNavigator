/*
 * @(#) GpsReceiverSimulatorAdapter.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2007  Clemens Krainer
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
package javiator.simulation;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.data.SimulationData;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.simulation.GpsReceiverSimulator;

/**
 * The <code>GpsReceiverSimulator</code> runs as a separate <code>Thread</code>
 * and provides simulated NMEA 0183 messages cyclically to registered
 * <code>Nmea0183MessageListener</code> like the
 * <code>GpsReceiverSimulatorAdapter</code>. It is the
 * <code>GpsReceiverSimulatorAdapter</code> that integrates the
 * <code>GpsReceiverSimulator</code> into the <code>MockJAviator</code>.
 * <p>
 * The <code>MockJAviator</code> main method registers the
 * <code>GpsReceiverSimulatorAdapter</code> as an
 * <code>ISensorDataListener</code> to provide the current position to the
 * <code>GpsReceiverSimulator</code>. The main method also registers the adapter
 * as an <code>IPacketContainer</code> to forward the simulated GPS data
 * messages.
 * <p>
 * The <code>GpsReceiverSimulatorAdapter</code> acts as an
 * <code>IPositionProvider</code> to the <code>GpsReceiverSimulator</code>. The
 * latter calls the <code>getCurrentPosition()</code> method to retrieve the
 * current position of the <code>MockJAviator</code> from the last available
 * <code>SensorData</code> object.
 * <p>
 * Since the <code>MockJAviator</code> and the <code>JControlMain</code>
 * exchange data frequently and the <code>GpsReceiverSimulator</code> runs
 * independently the <code>GpsReceiverSimulatorAdapter</code> employs ring
 * buffers for data interchange to avoid locks. If the
 * <code>GpsReceiverSimulator</code> sends simulated data with a higher
 * frequency than the <code>MockJAviator</code> can forward, the eldest GPS data
 * will be lost. This behavior is intended, because only the most current GPS
 * data is interest.
 * 
 * @author Clemens Krainer
 */
public class GpsReceiverSimulatorAdapter extends Thread implements IDataTransferObjectListener, IPositionProvider
{	
	private static final Logger LOG = Logger.getLogger(GpsReceiverSimulatorAdapter.class.getName());
	
	/**
	 * Name for the property that contains the file name for the GPS receiver
	 * simulator properties. If not set, the file name configured in
	 * <code>DEFAULT_PROPERTY_FILE_NAME</code> is used.
	 */
	public static final String PROP_GPS_RECEIVER_SIMULATOR_PROPERTIES = "gps.receiver.simulator.properties";

	/**
	 * The file name of the GPS simulator properties, if not configured otherwise.  
	 */
	public static final String DEFAULT_PROPERTY_FILE_NAME = "gpssim.properties";
	
    /**
     * Property name for the latitude of the reference point.
     */
    public static final String PROP_REFERENCE_LATITUDE = "reference.latitude";
    
    /**
     * Property name for the longitude of the reference point.
     */
    public static final String PROP_REFERENCE_LONGITUDE = "reference.longitude";
    
    /**
     * Property name for the altitude of the reference point.
     */
    public static final String PROP_REFERENCE_ALTITUDE = "reference.altitude";
    
    /**
	 * The server port number where the <code>GpsReceiverSimulator</code>
	 * provides its messages.
	 */
    public static final String PROP_GPS_RECEIVER_SIMULATOR_PORT = "gps.receiver.simulator.port";
	
	/**
	 * This buffer stores the <code>SensorData</code> object that drops in via the <code>ISensorDataListener</code> interface.
	 */
//    private SensorData sensorData;
    
	/**
	 * This buffer stores the <code>SimulationData</code> object that drops in via the <code>ISensorDataListener</code> interface.
	 */
    private SimulationData simulationData;
    
    /**
     * The current speed over ground in meters per second.
     */
    private double speedOverGround = 0;
    
    /**
     * The current course over ground in degrees.
     */
    private double courseOverGround = 0;

	/**
	 * The GPS receiver simulator.
	 */
	private GpsReceiverSimulator gpsReceiverSimulator;
	
	/**
	 * The <code>SocketServerThread</code> for TCP/IP handling.
	 */
	private SocketServerThread socketServer;
	
    /**
	 * The currently used geodetic system.
	 */
	private IGeodeticSystem geodeticSystem;
	
	/**
	 * The reference <code>Position</code> for the simulation.
	 */
	private PolarCoordinate referencePosition;
	
	/**
	 * This is the <code>OutputStream</code> for logging.
	 */
//	private PrintStream log = null;

	/**
	 * Construct a <code>GpsReceiverSimulatorAdapter</code>.
	 * 
	 * @throws IOException
	 */
	public GpsReceiverSimulatorAdapter (Properties props) throws IOException {
		
		if (props == null) {
			System.out.println (System.getProperty("java.class.path"));
			
			String propertyFileName = System.getProperty (PROP_GPS_RECEIVER_SIMULATOR_PROPERTIES);
			InputStream propsStream = null;
			
			if (propertyFileName == null) {
				System.out.println ("Property " + PROP_GPS_RECEIVER_SIMULATOR_PROPERTIES + " not set, trying file name " + DEFAULT_PROPERTY_FILE_NAME + " in CLASSPATH");
				propsStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (DEFAULT_PROPERTY_FILE_NAME);
			} else {
				propsStream = new FileInputStream (propertyFileName);
			}
	
			if (propsStream == null)
				throw new NullPointerException ("Can not find default property file " + DEFAULT_PROPERTY_FILE_NAME);
			
			props = new Properties ();
			props.load(propsStream);
		}
        double latitudeReference = Double.parseDouble (props.getProperty (PROP_REFERENCE_LATITUDE,"0"));
        double longitudeReference = Double.parseDouble (props.getProperty (PROP_REFERENCE_LONGITUDE,"0"));
        double altitudeReference = Double.parseDouble (props.getProperty (PROP_REFERENCE_ALTITUDE,"0"));
        referencePosition = new PolarCoordinate (latitudeReference, longitudeReference, altitudeReference);

		gpsReceiverSimulator = new GpsReceiverSimulator (props);
		gpsReceiverSimulator.setPositionProvider(this);
		geodeticSystem = new WGS84 ();
		gpsReceiverSimulator.setGeodeticSystem(geodeticSystem);
		
        Timer timer = new Timer ();
        timer.schedule (gpsReceiverSimulator, 100, 100);
        
        int port = Integer.parseInt (props.getProperty(PROP_GPS_RECEIVER_SIMULATOR_PORT,"3333"));
        socketServer = new SocketServerThread (gpsReceiverSimulator, port);
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener#receive(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void receive(IDataTransferObject dto) throws IOException {
//		if (dto instanceof SensorData) {
//			sensorData =  (SensorData) dto;
//			double dX = sensorData.getDdRoll();
//			double dY = sensorData.getDdPitch();
//			
//			speedOverGround = Math.sqrt(dX*dX + dY*dY);
//			
//			courseOverGround = Math.toDegrees (Math.atan2 (dY, -dX));
//			if (courseOverGround < 0)
//				courseOverGround += 360;
//			
//			return;
//		}
		
		if (dto instanceof SimulationData) {
			simulationData = (SimulationData) dto;
			double dX = simulationData.getDx();
			double dY = simulationData.getDy();
			
			speedOverGround = Math.sqrt(dX*dX + dY*dY);
			
			courseOverGround = Math.toDegrees (Math.atan2 (dY, -dX));
			if (courseOverGround < 0)
				courseOverGround += 360;
			
			return;
		}

		LOG.warn("Can not handle dto: " + dto);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
	 */
	public PolarCoordinate getCurrentPosition () {
//		SensorData s = sensorData;
		SimulationData s = simulationData;
		
		if (s == null) {
			if (LOG.isDebugEnabled())
				LOG.debug ("getCurrentPosition: SimulationData: none available yet.");
			return null;
		}
		
		PolarCoordinate r = geodeticSystem.walk (referencePosition, s.getX(), -s.getY(), s.getZ());
		if (LOG.isDebugEnabled())
			LOG.debug ("getCurrentPosition: Position " + r.toString() + " SimulationData " + s.toString());

		return r;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCourseOverGround()
	 */
	public Double getCourseOverGround() {
		return new Double (courseOverGround);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getSpeedOverGround()
	 */
	public Double getSpeedOverGround() {
		return new Double (speedOverGround);
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		socketServer.run();
	}
	
	/**
	 * Terminate the currently running server thread.
	 */
	public void terminate() {
		socketServer.terminate();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getGeodeticSystem()
	 */
	public IGeodeticSystem getGeodeticSystem() {
		return geodeticSystem;
	}
	
}
