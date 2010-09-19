/*
 * @(#) RemoteControlDaemon.java
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
 * This class implements a TCP/IP daemon that receives control commands and
 * forwards them to an instance of <code>IRemotecontrol</code>.
 * 
 * @author Clemens Krainer
 */
public class RemoteControlDaemon {
		
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
    private RemoteControlThread remoteControlThread;
    
    /**
     * This variable refers to the remote control instance.
     */
    private IRemoteControl remoteControl;
    
	/**
	 * Construct a remote control mock.
	 * 
	 * @param props the properties for the remote control mock.
	 * @throws ConfigurationException thrown if the port number is < 1024
	 */
	public RemoteControlDaemon (Properties props) throws ConfigurationException {
		port = Integer.parseInt (props.getProperty (PROP_PORT));
		if (port < 1024)
			throw new ConfigurationException ("Property " + PROP_PORT + " must be greater than 1023");
	}
	
	/**
	 * Set the remote control instance.
	 * 
	 * @param remoteControl the new remote control instance.
	 */
	public void setRemoteControl (IRemoteControl remoteControl) {
		this.remoteControl = remoteControl;
	}

	/**
	 * Send the new values for roll, pitch, yaw and thrust to the remote control.
	 * 
	 * @param roll the new value for roll
	 * @param pitch the new value for pitch
	 * @param yaw the new value for yaw
	 * @param thrust the new value for thrust
	 * @throws IOException thrown in case of communication problems of the remote control
	 */
	protected void sendToRemoteControl (int roll, int pitch, int yaw, int thrust) throws IOException {
		if (remoteControl == null)
			return;
		
		double _roll = (roll - 0x80) / 31.75;
		double _pitch = (pitch - 0x80) / 31.75;
		double _yaw = (yaw - 0x80) / 0.706;
		double _thrust = (thrust - 0x80) / 620.0;
		
		remoteControl.setRoll (_roll);
		remoteControl.setPitch (_pitch);
		remoteControl.setYaw (_yaw);
		remoteControl.setThrust (_thrust);
//		System.out.println ("RemoteControlDaemon1: thrust=" + thrust + ", roll="+ roll + ", pitch=" + pitch + ", yaw=" + yaw);
//		System.out.println ("RemoteControlDaemon2: thrust=" + _thrust + ", roll="+ _roll + ", pitch=" + _pitch + ", yaw=" + _yaw);
	}
	
    /**
     * This method creates the remote control service thread and starts it.
     *
     * @throws IllegalStateException thrown if the remote control service already runs
     */
    public void startRemoteControlThread () throws IllegalStateException
    {
        System.out.println ("Start Remote Control on port " + port);
        if (remoteControlThread != null)
            throw new IllegalStateException ("Remote control service is already running.");

        remoteControlRunning = false;

        remoteControlThread = new RemoteControlThread ();
        remoteControlThread.start ();

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
            if (remoteControlThread == null)
                    throw new IllegalStateException ("Remote control service thread does not run.");

            remoteControlThread.terminate ();

            // Now we wait for the remote control service to diminish.
            int cycles = 100;
            while (remoteControlRunning && --cycles > 0) {
                try { Thread.sleep (50); } catch (Exception e) {}
                Thread.yield ();
            }
            
            remoteControlThread = null;
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
            }

            System.out.println ("RemoteControlThread: end.");
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
//				OutputStream socket_out = socket.getOutputStream ();

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
                	
//                	if (thrust != 128)
//                		System.out.println ("RemoteControlDaemon: Message received: yaw=" + yaw + ", thrust=" + thrust + ", pitch=" + pitch + ", roll=" + roll + " ");
//                	
                	int cs = head ^ yaw ^ thrust ^ roll ^ pitch;
                	if (checkSum == cs) {
//                		System.out.println ("Checksum OK (" + checkSum + ")");
                		sendToRemoteControl (roll, pitch, yaw, thrust);
                	}
//                	else
//                		System.out.println ("Checksum error: received=" + checkSum + ", calculated=" + cs);
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
