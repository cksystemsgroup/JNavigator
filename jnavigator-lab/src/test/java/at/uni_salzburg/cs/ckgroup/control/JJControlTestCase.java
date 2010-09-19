/*
 * @(#) JJControlTestCase.java
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
import java.util.Timer;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.CourseData;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.Matrix3x3;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.location.LocationDaemon;
import at.uni_salzburg.cs.ckgroup.location.PositionProvider;
import at.uni_salzburg.cs.ckgroup.simulation.APFWSimulationAdapter;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

public class JJControlTestCase extends TestCase {

	public void NOtestCase01 () {
		String jjPropsPath = "at/uni_salzburg/cs/ckgroup/control/JJControlTest/jjcontrol.properties";
		
		Properties props = null;
		try {
			props = PropertyUtils.loadProperties(jjPropsPath);
			
			RemoteControlMock remoteControl = new RemoteControlMock (props);
			assertNotNull (remoteControl);
			remoteControl.startRemoteControlThread();
			
			JJControl jjControl = new JJControl (props);
			assertNotNull (jjControl);
			
			Timer timer = new Timer ();
			timer.schedule(jjControl, 0, 50);
			
			Thread.sleep(10000);
			timer.cancel();
			
			remoteControl.stopRemoteControlThread();
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
//	private GpsReceiverSimulator gpsReceiverSimulator;
	
	public void NOtestCase02 () {
		String jjPropsPath = "at/uni_salzburg/cs/ckgroup/control/JJControlTest/jjcontrol.properties";
		
		Properties props = null;
		try {
			props = PropertyUtils.loadProperties(jjPropsPath);
			
			APFWSimulationAdapter sim = new APFWSimulationAdapter (props);
			sim.start ();
			
			LocationDaemon locationDaemon = new LocationDaemon (sim);
			assertNotNull (locationDaemon);
			locationDaemon.start();
			
			RemoteControlDaemon remoteControl = new RemoteControlDaemon (props);
			assertNotNull (remoteControl);
			remoteControl.setRemoteControl (sim);
			remoteControl.startRemoteControlThread();
			
			JJControl jjControl = new JJControl (props);
			assertNotNull (jjControl);
			
			IPositionProvider p = jjControl.getPositionProvider();
			PositionProvider positionProvider = p instanceof PositionProvider ? (PositionProvider)p : null;
			assertNotNull (positionProvider);
			locationDaemon.addLocationMessageListener (positionProvider);
			
//			gpsReceiverSimulator = new GpsReceiverSimulator (props);
//			gpsReceiverSimulator.setPositionProvider(p);
//			WGS84 geodeticSystem = new WGS84 ();
//			gpsReceiverSimulator.setGeodeticSystem (geodeticSystem);
//			
//	        Timer timer1 = new Timer ();
//	        timer1.schedule (gpsReceiverSimulator, 100, 100);
//	        
//	        int port = Integer.parseInt (props.getProperty("gps.simulator.port","3333"));
//	        SocketServerThread socketServer = new SocketServerThread (port);
//	        socketServer.start ();
			
			Timer timer2 = new Timer ();
			timer2.schedule (jjControl, 0, 50);
			
			jjControl.startFlyingSetCourse();
			
			Thread.sleep(1000000);
			timer2.cancel();
			
//			socketServer.terminate ();
			sim.terminate();
			Thread.sleep(500);
			remoteControl.stopRemoteControlThread();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void NOtestCase16 () {
//		PolarCoordinate e = new PolarCoordinate (47.821933, 13.040875, 440);
		PolarCoordinate oldPosition = new PolarCoordinate (47.822282, 13.040920, 440);
		PolarCoordinate newPosition = new PolarCoordinate (47.822039, 13.040208, 440);
		long timeSpan = 100000;
		
		WGS84 gs = new WGS84 ();
		
		CourseData res = gs.calculateSpeedAndCourse (oldPosition, newPosition, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
		assertEquals (59.77491367730676, res.distance, 1E-4);
		assertEquals (0.5977491367730676, res.speed, 1E-4);
		assertEquals (-2.741165263335737E-4, res.elevation, 1E-3);
		assertEquals (116.94432742662649, res.course, 1E-3);
	}
	
	public void testCase17 () {
		PolarCoordinate e = new PolarCoordinate (47.821933, 13.040875, 440);
		PolarCoordinate h = new PolarCoordinate (47.821955, 13.040903, 440);
		
		long timeSpan = 100000;
		
		WGS84 gs = new WGS84 ();
		
		CourseData res = gs.calculateSpeedAndCourse (e, h, timeSpan);
		
		assertNotNull (res);
		assertTrue (res.courseIsValid);
	}

	public void testCase20 () {
		PolarCoordinate referencePosition = new PolarCoordinate (47.821933, 13.040875, 440);
		WGS84 geodeticSystem = new WGS84 ();
		CartesianCoordinate d = new CartesianCoordinate (2.08443399082569,5.32753041284404,0.492776651376147);
		Matrix3x3 referenceRotator = new Matrix3x3 (0, 0, -63.05567257337351);
		CartesianCoordinate currPos = referenceRotator.multiply(d);
		PolarCoordinate currentPosition = geodeticSystem.walk(referencePosition, currPos.x, currPos.y, currPos.z);
		System.out.println ("testCase20: currentPosition=" + currentPosition);
	}

	public void NOtestCase03 () {
		String jjPropsPath = "at/uni_salzburg/cs/ckgroup/control/JJControlTest/jjcontrol-testCase03.properties";
		
		Properties props = null;
		try {
			props = PropertyUtils.loadProperties(jjPropsPath);
			
			IConnection locationReceiver = (IConnection) ObjectFactory.getInstance ().instantiateObject ("ubisense.location.receiver.", IConnection.class, props);
			
			LocationDaemon locationDaemon = new LocationDaemon (locationReceiver);
			assertNotNull (locationDaemon);
			locationDaemon.start();
			
			JJControl jjControl = new JJControl (props);
			assertNotNull (jjControl);
			
			IPositionProvider p = jjControl.getPositionProvider();
			PositionProvider positionProvider = p instanceof PositionProvider ? (PositionProvider)p : null;
			assertNotNull (positionProvider);
			locationDaemon.addLocationMessageListener (positionProvider);
			
			Timer timer = new Timer ();
			timer.schedule (jjControl, 0, 50);
			
			jjControl.startFlyingSetCourse();
			
			Thread.sleep(100000);
			timer.cancel();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
//    /**
//	 * This class implements the socket server thread. It opens a
//	 * <code>ServerSocket</code> at the specified port number and waits for
//	 * clients to connect. It is a <code>WorkingThread</code> that takes an
//	 * incoming connection and does the actual receiving and sending.
//	 */
//    private class SocketServerThread extends Thread
//    {
//		/**
//		 * If <code>active</code> equals to <code>true</code> the socket
//		 * server thread waits for incoming connections.
//		 */
//		private boolean active = true;
//		
//		/**
//		 * The TCP/IP port number of this server. 
//		 */
//		public int port;
//		
//		/**
//		 * The <code>ServerSocket</code> waiting for new connections.
//		 */
//		private ServerSocket serverSocket;
//		
//		/**
//		 * Construct a <code>SocketServerThread</code>
//		 * 
//		 * @param port the TCP/IP port number to listen to
//		 */
//		public SocketServerThread (int port)
//		{
//			this.port = port;
//		}
//		
//		/* (non-Javadoc)
//		 * @see java.lang.Thread#run()
//		 */
//		public void run ()
//		{
//	        System.out.println ("SocketServerThread: start. port=" + port);
//	
//	        try {
//	            serverSocket = new ServerSocket (port);
//	            System.out.println ("SocketServerThread: Server started");
//	
//	            while (active) {
//					Socket clientSocket = serverSocket.accept ();
//					
//					if (!active)
//						  break;
//					
//					WorkerThread worker = new WorkerThread (clientSocket);
//					worker.start ();
//	            }
//	        } catch (IOException e)
//	        {
//				e.printStackTrace();
//				try {
//					serverSocket.close ();
//	            } catch (IOException e1) {}
//	        }
//
//	        System.out.println ("SocketServerThread: end.");
//		}
//		
//		/**
//		 * Terminate the mail server thread.
//		 */
//		public void terminate () {
//			active = false;
//			try {
//            	serverSocket.close ();
//            } catch (IOException e) {}
//            yield ();
//		}
//    }
//
//    /**
//	 * This class implements a socket server worker thread that inherits an
//	 * established connection. It is this class that sends the GPS data to a
//	 * TCP/IP client.
//	 */
//    private class WorkerThread extends Thread {
//    	
//        /**
//         * The inherited socket with an established connection.
//         */
//        private Socket socket;
//
//        /**
//		 * Constructor.
//		 * 
//		 * @param socket
//		 *            a socket containing an already established connection to a
//		 *            client.
//		 */
//    	public WorkerThread (Socket socket) {
//    		this.socket = socket;
//    	}
//    	
//        /* (non-Javadoc)
//         * @see java.lang.Thread#run()
//         */
//        public void run ()
//        {
//			System.out.println ("GPS receiver: New client connected, id=" + this.getId());
//			
//        	try {
//            	int ch;
//            	InputStream gpsReceiver = gpsReceiverSimulator.getInputStream();
//            	OutputStream sockOut = socket.getOutputStream(); 
//
//        		while ((ch = gpsReceiver.read()) > 0)
//        			sockOut.write(ch);
//
//            } catch (IOException e) {
//            	if (e instanceof SocketException && e.getMessage().equals("Broken pipe"))
//            		System.out.println ("GPS receiver: Client disconnected, id=" + this.getId());
//            	else
//            		e.printStackTrace();            		
//            }
//        	
//            try {
//              socket.close ();
//            } catch (IOException e) {}
//        }
//    	
//    }
}
