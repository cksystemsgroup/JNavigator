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
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Timer;

import javiator.util.ISensorDataListener;
import javiator.util.SensorData;
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
public class GpsReceiverSimulatorAdapter implements ISensorDataListener, IPositionProvider, Runnable
{	
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
    private SensorData sensorData;
    
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
	private PrintStream log = null;
	
	/**
	 * Construct a <code>GpsReceiverSimulatorAdapter</code>.
	 * 
	 * @throws IOException
	 */
	public GpsReceiverSimulatorAdapter () throws IOException {
		
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
		
		Properties props = new Properties ();
		props.load(propsStream);
		
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
        socketServer = new SocketServerThread (port);
	}
	
	/**
	 * Set the <code>OutputStream</code> for logging.
	 * 
	 * @param log the new <code>OutputStream</code>
	 */
	public void setLogOutputStream (OutputStream log) {
		this.log = log == null ? null : new PrintStream (log);
	}
	
	/* (non-Javadoc)
	 * @see javiator.simulation.SensorDataListener#receive(javiator.util.SensorData)
	 */
	public void receive (SensorData sensorData) {
		this.sensorData = sensorData;
		
		double dX = sensorData.ddroll / 1000.0;
		double dY = sensorData.ddpitch / 1000.0;
		
		speedOverGround = Math.sqrt(dX*dX + dY*dY);
		
		courseOverGround = Math.toDegrees (Math.atan2 (dY, -dX));
		if (courseOverGround < 0)
			courseOverGround += 360;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
	 */
	public PolarCoordinate getCurrentPosition () {
		SensorData s = sensorData;
		
		if (s == null) {
			if (log != null)
				log.println ("getCurrentPosition: SensorData: none available yet.");
			return null;
		}
		
		PolarCoordinate r = geodeticSystem.walk (referencePosition, s.x/1000.0, -s.y/1000.0, s.z/1000.0);
		if (log != null)
			log.println ("getCurrentPosition: Position " + r.toString() + " SensorData " + s.toString());

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
	 * This class implements the socket server thread. It opens a
	 * <code>ServerSocket</code> at the specified port number and waits for
	 * clients to connect. It is a <code>WorkingThread</code> that takes an
	 * incoming connection and does the actual receiving and sending.
	 */
    private class SocketServerThread extends Thread
    {
		/**
		 * If <code>active</code> equals to <code>true</code> the socket
		 * server thread waits for incoming connections.
		 */
		private boolean active = true;
		
		/**
		 * The TCP/IP port number of this server. 
		 */
		public int port;
		
		/**
		 * The <code>ServerSocket</code> waiting for new connections.
		 */
		private ServerSocket serverSocket;
		
		/**
		 * Construct a <code>SocketServerThread</code>
		 * 
		 * @param port the TCP/IP port number to listen to
		 */
		public SocketServerThread (int port)
		{
			this.port = port;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run ()
		{
	        System.out.println ("SocketServerThread: start. port=" + port);
	
	        try {
	            serverSocket = new ServerSocket (port);
	            System.out.println ("SocketServerThread: Server started");
	
	            while (active) {
					Socket clientSocket = serverSocket.accept ();
					
					if (!active)
						  break;
					
					WorkerThread worker = new WorkerThread (clientSocket);
					worker.start ();
	            }
	        } catch (IOException e)
	        {
				e.printStackTrace();
				try {
					serverSocket.close ();
	            } catch (IOException e1) {}
	        }

	        System.out.println ("SocketServerThread: end.");
		}
		
		/**
		 * Terminate the mail server thread.
		 */
//		public void terminate () {
//			active = false;
//			try {
//            	serverSocket.close ();
//            } catch (IOException e) {}
//            yield ();
//		}
    }

    /**
	 * This class implements a socket server worker thread that inherits an
	 * established connection. It is this class that sends the GPS data to a
	 * TCP/IP client.
	 */
    private class WorkerThread extends Thread {
    	
        /**
         * The inherited socket with an established connection.
         */
        private Socket socket;

        /**
		 * Constructor.
		 * 
		 * @param socket
		 *            a socket containing an already established connection to a
		 *            client.
		 */
    	public WorkerThread (Socket socket) {
    		this.socket = socket;
    	}
    	
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run ()
        {
			System.out.println ("GPS receiver: New client connected, name=" + this.getName());
			
        	try {
            	int ch;
            	InputStream gpsReceiver = gpsReceiverSimulator.getInputStream();
            	OutputStream sockOut = socket.getOutputStream(); 

        		while ((ch = gpsReceiver.read()) > 0)
        			sockOut.write(ch);

            } catch (IOException e) {
            	if (e instanceof SocketException && e.getMessage().equals("Broken pipe"))
            		System.out.println ("GPS receiver: Client disconnected, name=" + this.getName());
            	else
            		e.printStackTrace();            		
            }
        	
            try {
              socket.close ();
            } catch (IOException e) {}
        }
    	
    }
	
}
