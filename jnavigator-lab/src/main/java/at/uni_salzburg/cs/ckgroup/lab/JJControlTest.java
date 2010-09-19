/*
 * @(#) JJControlTest.java
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
package at.uni_salzburg.cs.ckgroup.lab;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.Timer;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.control.JJControl;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.location.LocationDaemon;
import at.uni_salzburg.cs.ckgroup.location.PositionProvider;
import at.uni_salzburg.cs.ckgroup.simulation.GpsReceiverSimulator;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

public class JJControlTest extends TestCase {

	private GpsReceiverSimulator gpsReceiverSimulator;
	
	public static void main (String[] args) {
		JJControlTest testCase = new JJControlTest ();
		testCase.testCase01 ();
	}

	public void testCase01 () {
		String jjPropsPath = "at/uni_salzburg/cs/ckgroup/lab/JJControl/jjcontrol.properties";
		
		Properties props = null;
		try {
			System.out.println ("testCase01 started.");
			System.out.println ("testCase01 started.");
			System.out.println ("testCase01 started.");
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
			
			gpsReceiverSimulator = new GpsReceiverSimulator (props);
			gpsReceiverSimulator.setPositionProvider(p);
			WGS84 geodeticSystem = new WGS84 ();
			gpsReceiverSimulator.setGeodeticSystem (geodeticSystem);
			
	        Timer timer1 = new Timer ();
	        timer1.schedule (gpsReceiverSimulator, 100, 100);
	        
	        int port = Integer.parseInt (props.getProperty("gps.simulator.port","3333"));
	        SocketServerThread socketServer = new SocketServerThread (port);
	        socketServer.start ();
			
			Timer timer = new Timer ();
			timer.schedule (jjControl, 0, 50);
			
			jjControl.startFlyingSetCourse();
			
			Thread.sleep(1000000);
			socketServer.terminate ();
			timer.cancel();
			System.out.println ("testCase01 terminated.");
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
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
		public void terminate () {
			active = false;
			try {
            	serverSocket.close ();
            } catch (IOException e) {}
            yield ();
		}
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
			System.out.println ("GPS receiver: New client connected, id=" + this.getName());
			
        	try {
            	int ch;
            	InputStream gpsReceiver = gpsReceiverSimulator.getInputStream();
            	OutputStream sockOut = socket.getOutputStream(); 

        		while ((ch = gpsReceiver.read()) > 0)
        			sockOut.write(ch);

            } catch (IOException e) {
            	if (e instanceof SocketException && e.getMessage().equals("Broken pipe"))
            		System.out.println ("GPS receiver: Client disconnected, id=" + this.getName());
            	else
            		e.printStackTrace();            		
            }
        	
            try {
              socket.close ();
            } catch (IOException e) {}
        }
    	
    }
}
