/*
 * @(#) LocationMessageSimulator.java
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
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Properties;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.data.SimulationData;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.io.TcpSocketServer;

public class LocationMessageSimulatorAdapter extends Thread implements IDataTransferObjectListener, IPositionProvider
{
    public final static Logger LOG = LoggerFactory.getLogger(LocationMessageSimulatorAdapter.class.getName());
	
	/**
	 * Name for the property that contains the file name for the GPS receiver
	 * simulator properties. If not set, the file name configured in
	 * <code>DEFAULT_PROPERTY_FILE_NAME</code> is used.
	 */
	public static final String PROP_LOCATION_MESSAGE_SIMULATOR_PROPERTIES = "location.message.simulator.properties";
	
	/**
	 * The file name of the location message simulator properties, if not configured otherwise.  
	 */
	public static final String DEFAULT_PROPERTY_FILE_NAME = "locsim.properties";
	
    /**
     * Property name for the X value of the reference point.
     */
    public static final String PROP_REFERENCE_X = "reference.X";
    
    /**
     * Property name for the Y value of the reference point.
     */
    public static final String PROP_REFERENCE_Y = "reference.Y";
    
    /**
     * Property name for the Z value of the reference point.
     */
    public static final String PROP_REFERENCE_Z = "reference.Z";
    
    /**
     * Property name for the orientation of the reference coordinate system.
     */
    public static final String PROP_REFERENCE_ORIENTATION = "reference.orientation";
    
    /**
	 * The server port number where the <code>LocationMessageSimulator</code>
	 * provides its messages.
	 */
    public static final String PROP_LOCATION_MESSAGE_SIMULATOR_PORT = "location.message.simulator.port";
    
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
	 * The location message simulator.
	 */
	private LocationMessageSimulator locationMessageSimulator;
	
	/**
	 * The <code>SocketServerThread</code> for TCP/IP handling.
	 */
	private SocketServerThread socketServer;
	
	/**
	 * The reference <code>Position</code> for the simulation.
	 */
	private CartesianCoordinate referencePosition;
	
//	/**
//	 * The orientation of the reference coordinate system, i.e. the orientation
//	 * of its X-axis in degrees. 0=North.
//	 */
//	private double referenceOrientation;
	
	/**
	 * The currently used geodetic system. 
	 */
	private IGeodeticSystem geodeticSystem = new WGS84();
	
	/**
	 * Construct a <code>LocationMessageSimulatorAdapter</code>.
	 * 
	 * @param properties
	 * @throws IOException
	 */
	public LocationMessageSimulatorAdapter (Properties props) throws IOException {
		
		if (props == null) {
			String propertyFileName = System.getProperty (PROP_LOCATION_MESSAGE_SIMULATOR_PROPERTIES);
			InputStream propsStream = null;
			
			if (propertyFileName == null) {
				System.out.println ("Property " + PROP_LOCATION_MESSAGE_SIMULATOR_PROPERTIES + " not set, trying file name " + DEFAULT_PROPERTY_FILE_NAME + " in CLASSPATH");
				propsStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream (DEFAULT_PROPERTY_FILE_NAME);
			} else {
				propsStream = new FileInputStream (propertyFileName);
			}
	
			if (propsStream == null)
				throw new NullPointerException ("Can not find default property file " + DEFAULT_PROPERTY_FILE_NAME);
			
			props = new Properties ();
			props.load(propsStream);
		}
        double referenceX = Double.parseDouble (props.getProperty (PROP_REFERENCE_X,"0"));
        double referenceY = Double.parseDouble (props.getProperty (PROP_REFERENCE_Y,"0"));
        double referenceZ = Double.parseDouble (props.getProperty (PROP_REFERENCE_Z,"0"));
        referencePosition = new CartesianCoordinate(referenceX, referenceY, referenceZ);
        
//        referenceOrientation = Double.parseDouble (props.getProperty (PROP_REFERENCE_ORIENTATION, "0"));
        
		locationMessageSimulator = new LocationMessageSimulator(props);
		locationMessageSimulator.setPositionProvider(this);
		
        Timer timer = new Timer ();
        timer.schedule (locationMessageSimulator, 100, 100);
        
        socketServer = new SocketServerThread (props);
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener#receive(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void receive(IDataTransferObject dto) throws IOException {
		
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
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCourseOverGround()
	 */
	public Double getCourseOverGround() {
		return new Double (courseOverGround);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
	 */
	public PolarCoordinate getCurrentPosition() {
		SimulationData s = simulationData;
		
		if (s == null) {
			if (LOG.isDebugEnabled())
				LOG.debug ("getCurrentPosition: SensorData: none available yet.");
			return null;
		}
		
		CartesianCoordinate r = referencePosition.add(new CartesianCoordinate(-s.getX(), s.getY(), s.getZ()));
		if (LOG.isDebugEnabled())
			LOG.debug ("getCurrentPosition: Position " + r.toString() + " SensorData " + s.toString());

		return new PolarCoordinate (r.x, r.y, r.z);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getSpeedOverGround()
	 */
	public Double getSpeedOverGround() {
		return new Double (speedOverGround);
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getGeodeticSystem()
	 */
	public IGeodeticSystem getGeodeticSystem() {
		return geodeticSystem;
	}


	private class SocketServerThread extends TcpSocketServer {

		public SocketServerThread (Properties props) throws IOException {
			super(props);
		}
		
		public void startWorkerThread(IConnection connection) {
			WorkerThread worker = new WorkerThread (connection);
			worker.start();
		}
		
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

	public void close() {
		// TODO Auto-generated method stub
	}
	
    /**
	 * This class implements a socket server worker thread that inherits an
	 * established connection. It is this class that sends the position data to a
	 * TCP/IP client.
	 */
    private class WorkerThread extends Thread {
    	
        /**
         * The inherited <code>IConnection</code> with an established connection.
         */
        private IConnection connection;

        /**
		 * Constructor.
		 * 
		 * @param socket
		 *            a socket containing an already established connection to a
		 *            client.
		 */
    	public WorkerThread (IConnection connection) {
    		this.connection = connection;
    	}
    	
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run ()
        {
			System.out.println ("Location message simulator: New client connected, name=" + this.getName());
			
        	try {
            	int ch;
            	InputStream messageReceiver = locationMessageSimulator.getInputStream();
            	OutputStream sockOut = connection.getOutputStream(); 

        		while ((ch = messageReceiver.read()) > 0)
        			sockOut.write(ch);

            } catch (IOException e) {
            	if (e instanceof SocketException && e.getMessage().equals("Broken pipe"))
            		System.out.println ("Location message simulator: Client disconnected, name=" + this.getName());
            	else
            		e.printStackTrace();            		
            }
        	
            try {
            	connection.close ();
            } catch (IOException e) {}
        }
    }

}
