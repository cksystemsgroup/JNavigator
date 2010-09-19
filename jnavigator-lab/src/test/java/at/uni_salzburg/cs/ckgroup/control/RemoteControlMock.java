/*
 * @(#) RemoteControlMock.java
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

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This class implements a TCP server that receives and displays the remote
 * control commands for testing.
 * 
 * @author Clemens Krainer
 */
public class RemoteControlMock {
	
	public static final String PROP_PORT = "remote.control.port";
	
    /**
     * This flag indicates the state of the mail server
     */
    private boolean remoteControlRunning;
    
    /**
     * The port number for the remote control to listen.
     */
    private int port;
	
    /**
     * This variable contains the reference to the remote control service.
     */
    private RemoteControlThread remoteControl;
    
	/**
	 * Construct a remote control mock.
	 * 
	 * @param props the properties for the remote control mock.
	 * @throws ConfigurationException thrown if the port number is < 1024
	 */
	public RemoteControlMock (Properties props) throws ConfigurationException {
		port = Integer.parseInt (props.getProperty (PROP_PORT));
		if (port < 1024)
			throw new ConfigurationException ("Property " + PROP_PORT + " must be greater than 1023");
	}

    /**
     * This method creates the remote control service thread and starts it.
     *
     * @throws IllegalStateException thrown if the remote control service already runs
     */
    public void startRemoteControlThread () throws IllegalStateException
    {
        System.out.println ("Start Remote Control on port " + port);
        if (remoteControl != null)
            throw new IllegalStateException ("Remote control service is already running.");

        remoteControlRunning = false;

        remoteControl = new RemoteControlThread ();
        remoteControl.start ();

        // Now we wait for the remote control service to open its socket.
        int cycles = 100;
        while (!remoteControlRunning && --cycles > 0) {
            try { Thread.sleep (50); } catch (Exception e) {}
            Thread.yield ();
        }

        if (cycles == 0)
            throw new IllegalStateException ("Can not launch remote control.");
    }

    /**
     * This method stops the running remote control service thread
     *
     * @throws IllegalStateException thrown if the remote control service does not run
     */
    public void stopRemoteControlThread () throws IllegalStateException
    {
            System.out.println ("\nStop remote control on port " + port);
            if (remoteControl == null)
                    throw new IllegalStateException ("Remote control service thread does not run.");

            remoteControl.terminate ();

            // Now we wait for the remote control service to diminish.
            int cycles = 100;
            while (remoteControlRunning && --cycles > 0) {
                try { Thread.sleep (50); } catch (Exception e) {}
                Thread.yield ();
            }
            
            remoteControl = null;
    }
    
    /**
	 * This class implements the remote control thread. It opens a
	 * <code>ServerSocket</code> at the specified port number and waits for
	 * clients to connect. It is a <code>RemoteControlWorkerThread</code> that
	 * takes an incoming connection and does the actual receiving and sending.
	 */
    private class RemoteControlThread extends Thread {
    	ServerSocket server_socket;
    	
        /**
		 * If <code>active</code> equals to <code>true</code> the remote
		 * control service thread waits for incoming connections.
		 */
        private boolean active = true;

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run ()
        {
            System.out.println ("RemoteControlThread: start. port=" + port);

            try {
                server_socket = new ServerSocket (port);
                System.out.println ("RemoteControlThread: Server started");

                remoteControlRunning = true;

                while (active) {
					Socket client_socket = server_socket.accept ();
					
					if (!active)
						break;
					
					RemoteControlWorkerThread worker = new RemoteControlWorkerThread (client_socket);
					
					worker.start ();
                }
            } catch (IOException e)
            {
            	if (!(e instanceof SocketException) && !e.getMessage ().equals ("Socket closed"))
                    e.printStackTrace();
	            terminate ();
	            return;
            }

            System.out.println ("RemoteControlThread: end.("+(active?"voluntarily":"forced")+")");
        }

        /**
         * Terminate the remote control service thread.
         */
        public void terminate () {
        	active = false;
        	try { server_socket.close (); } catch (IOException e) {}
        	yield ();
        }
    }
    
    /**
	 * This class implements a server worker thread that inherits an established
	 * connection and receives the remote control commands from the client.
	 */
    private class RemoteControlWorkerThread extends Thread
    {
        /**
         * The inherited socket with an established connection.
         */
        private Socket socket;

        /**
		 * Set to <code>true</code> if a remote control worker thread
		 * communicates to a client.
		 */
        private boolean running;
        
        /**
         * Constructor.
         *
         * @param socket a socket containing an already established connection
         *        to a client.
         */
        public RemoteControlWorkerThread (Socket socket)
        {
            this.socket = socket;
        }

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run ()
        {
        	System.out.println ("RemoteControlWorkerThread: New client connected");
        	
        	running = true;

            try {
                InputStream socket_in = socket.getInputStream ();
//                InputStreamReader isr = new InputStreamReader (socket_in, "8859_1");
//                BufferedReader client_in = new BufferedReader (isr);
//                OutputStream client_out = socket.getOutputStream ();

                while (running) {
                	int head;
                	
                	while ( (head = socket_in.read()) != 0x02) {
                		if (head < 0) {
                			running = false;
                			break;
                		}
                		System.out.println ("Trying to synchronize, received byte " + head);
                	}
                	
                	if (!running)
                		break;
                	
                	int yaw = socket_in.read();
                	int thrust = socket_in.read();
                	int pitch = socket_in.read();
                	int roll = socket_in.read();
                	int checkSum = socket_in.read();
                	
                	System.out.println ("Message received: yaw=" + yaw + ", thrust=" + thrust + ", pitch=" + pitch + ", roll=" + roll + " ");
                	
                	int cs = head ^ yaw ^ thrust ^ roll ^ pitch;
                	if (checkSum != cs)
                		System.out.println ("Checksum error: received=" + checkSum + ", calculated=" + cs);
                	else
                		System.out.println ("Checksum OK (" + checkSum + ")");
                }

            } catch (IOException e) {
            	if (!(e instanceof SocketException) || !e.getMessage ().equals ("Socket closed")) {
            		System.out.println ( "RemoteControlWorkerThread: I/O error 1 " + e );
            	}
            }

            try {
              socket.close ();
            } catch (IOException e){
              System.out.println ("RemoteControlWorkerThread: I/O error 2 " + e );
            }
            
            System.out.println ("RemoteControlWorkerThread: Client disconnected");
        }

    }

}
