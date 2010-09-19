/*
 * @(#) AposNtripCasterMock.java
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
package at.uni_salzburg.cs.ckgroup.apos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

public class AposNtripCasterMock
{
	public static final String PROP_INPUT_DATA_FILE = "input.data.file";
	public static final String PROP_EXPECTED_LOCATION = "expected.location";
	
    /**
     * This flag indicates the state of the mail server
     */
    private boolean ntripCasterRunning;
    
    /**
     * The port number for the NTRIP caster to listen.
     */
    private int port;
	
	/**
	 * The basic authentication string for the APOS NTRIP service. 
	 */
	private byte[] basicAuthentication;
    
    /**
     * This variable contains the reference to the NTRIP caster service.
     */
    private NtripCasterThread ntripCaster;
    
	/**
	 * 
	 */
	private String expectedRequest;
	private String expectedUserAgent;
	private String expectedAuthorisation;
	private String expectedLocation;
	
	private File inputDataFile;

	/**
	 * Construct a NTRIP mock caster.
	 * 
	 * @param props the properties for the NTRIP mock caster.
	 * @throws FileNotFoundException 
	 */
	public AposNtripCasterMock (Properties props) throws FileNotFoundException {
		init (props);
	}
		
	/**
	 * Initialize the NTRIP mock caster.
	 * @throws FileNotFoundException 
	 */
	private void init (Properties props) throws FileNotFoundException {
		
		port = Integer.parseInt (props.getProperty (AposNtrip.PROP_PORT));
		String userName = props.getProperty (AposNtrip.PROP_USERNAME);
		String password = props.getProperty (AposNtrip.PROP_PASSWORD);
		String mountPoint = props.getProperty (AposNtrip.PROP_MOUNTPOINT);
		
		byte[] encodedPassword = ( userName + ":" + password ).getBytes();
	    Base64 encoder = new Base64 ();
	    basicAuthentication = encoder.encode( encodedPassword );
	
	    expectedRequest = "GET /" + mountPoint + " HTTP/1.0";
	    expectedUserAgent = "User-Agent: .*";
	    expectedAuthorisation = "Authorization: Basic " + (new String (basicAuthentication));
	    expectedLocation = props.getProperty (PROP_EXPECTED_LOCATION);
	    
	    System.out.println ("AposNtripCasterMock: expectedRequest=" + expectedRequest);
	    System.out.println ("AposNtripCasterMock: expectedUserAgent=" + expectedUserAgent);
	    System.out.println ("AposNtripCasterMock: expectedAuthorisation=" + expectedAuthorisation);
	    
	    String fileName = props.getProperty (PROP_INPUT_DATA_FILE);
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (fileName);
		
		if (url == null)
			throw new FileNotFoundException (fileName);
		
		inputDataFile = new File (url.getFile ());
	}
	
    /**
     * This method creates the NTRIP caster thread and starts it.
     *
     * @throws IllegalStateException thrown if the mail server already runs
     */
    public void startCasterThread () throws IllegalStateException
    {
        System.out.println ("Start Mailserver on port " + port);
        if (ntripCaster != null)
            throw new IllegalStateException ("NTRIP caster is already running.");

        ntripCasterRunning = false;

        ntripCaster = new NtripCasterThread ();
        ntripCaster.start ();

        // Now we wait for the NTRIP caster to open its socket.
        int cycles = 100;
        while (!ntripCasterRunning && --cycles > 0) {
            try { Thread.sleep (50); } catch (Exception e) {}
            Thread.yield ();
        }

        if (cycles == 0)
            throw new IllegalStateException ("Can not launch NTRIP caster.");
    }

    /**
     * This method stops the running NTRIP caster thread
     *
     * @throws IllegalStateException thrown if the mail server does not run
     */
    public void stopCasterThread () throws IllegalStateException
    {
            System.out.println ("\nStop NTRIP Caster on port " + port);
            if (ntripCaster == null)
                    throw new IllegalStateException ("NTRIP caster thread does not run.");

            ntripCaster.terminate ();

            // Now we wait for the NTRIP caster to diminish.
            int cycles = 100;
            while (ntripCasterRunning && --cycles > 0) {
                try { Thread.sleep (50); } catch (Exception e) {}
                Thread.yield ();
            }
            
            ntripCaster = null;
    }
    
    /**
	 * This class implements the NTRIP caster thread. It opens a
	 * <code>ServerSocket</code> at the specified port number and waits for
	 * clients to connect. It is a <code>NtripCasterWorkerThread</code> that
	 * takes an incoming connection and does the actual receiving and sending.
	 */
    private class NtripCasterThread extends Thread
    {
    	ServerSocket server_socket;
    	
        /**
		 * If <code>active</code> equals to <code>true</code> the NTRIP
		 * caster thread waits for incoming connections.
		 */
        private boolean active = true;

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run ()
        {
            System.out.println ("NtripCasterThread: start. port=" + port);

            try {
                server_socket = new ServerSocket (port);
                System.out.println ("NtripCasterThread: Server started");

                ntripCasterRunning = true;

                while (active) {
                  Socket client_socket = server_socket.accept ();

                  if (!active)
                      break;

                  NtripCasterWorkerThread worker = new NtripCasterWorkerThread (client_socket);

                  worker.start ();

                  System.out.println ("NtripCasterThread: New client connected");
                }
            } catch (IOException e)
            {
            	if (!(e instanceof SocketException) && !e.getMessage ().equals ("Socket closed"))
                    e.printStackTrace();
	            terminate ();
	            return;
            }

            System.out.println ("NtripCasterThread: end.("+(active?"voluntarily":"forced")+")");
        }

        /**
         * Terminate the mail server thread.
         */
        public void terminate () {
                active = false;
                try { server_socket.close (); } catch (IOException e) {}
                yield ();
        }
    }
    
    /**
     * This class implements a mail server worker thread that inherits an
     * established connection and receives the email from the client. It is this
     * class that implements the EHLO protocol.
     */
    private class NtripCasterWorkerThread extends Thread
    {
        /**
         * The inherited socket with an established connection.
         */
        private Socket socket;

        /**
         * The automaton state constants. 
         */
        private static final int STATE_CONNECTION_OPEN = 0;
        private static final int STATE_REQUEST_RECEIVED = 1;
        private static final int STATE_AGENT_RECEIVED = 2;
        private static final int STATE_AUTORISATION_RECEIVED = 3;
        private static final int STATE_HEADER_COMPLETE = 4;
        private static final int STATE_LOCATION_RECEIVED = 5;
        private static final int STATE_SENDING_DATA = 6;
        private static final int STATE_INVALID_HEADER = 7;
        
        /**
         * The current state of the automaton. See above at "Automaton
         * Constants".
         */
        private int state;

        /**
         * Set to <code>true</code> if a NTRIP caster worker thread
         * communicates to a client.
         */
        private boolean running;

        /**
         * Constructor.
         *
         * @param socket a socket containing an already established connection
         *        to a client.
         */
        public NtripCasterWorkerThread (Socket socket)
        {
            this.socket = socket;
            state = 0;
        }

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run ()
        {
        	running = true;

            try {
                InputStream socket_in = socket.getInputStream ();
                InputStreamReader isr = new InputStreamReader (socket_in, "8859_1");
                BufferedReader client_in = new BufferedReader (isr);
                OutputStream client_out = socket.getOutputStream ();

                while (running) {
                	
                	while (state != STATE_LOCATION_RECEIVED && running) {
	
	                    String client_str = client_in.readLine ();
	
	                    System.out.println ("Client message: '" + client_str + "'");
	
	                    state = handleMessage (client_str);
	                    System.out.println ("New State: '" + state + "'");
	
	                    if (state == STATE_INVALID_HEADER) {
	                    	running = false;
	                    	socket.close ();
	                    }
                	}
                	
                	if (!running)
                		break;

                	state = STATE_SENDING_DATA;

                	FileInputStream data = new FileInputStream (inputDataFile);
                	
                	int ch = 0;
                	int charCounter = 0;
                	while (state == STATE_SENDING_DATA && running && ch >= 0) {
                		ch = data.read ();
                		if (ch >= 0) {
	                		client_out.write (ch);
	                		++charCounter;
                		}
                	}
                	
                	client_out.flush ();
                	client_out.close ();
                	
                	System.out.println ("\nNtripCasterWorkerThread: characters written: " + charCounter);
                }

            } catch (IOException e) {
            	if (!(e instanceof SocketException) || !e.getMessage ().equals ("Socket closed")) {
            		System.out.println ( "NtripCasterWorkerThread: I/O error 1 " + e );
            	}
            		
            }

            try {
              socket.close ();
            } catch (IOException e){
              System.out.println ("NtripCasterWorkerThread: I/O error 2 " + e );
            }
        }

        /**
         * Handle a client message.
         *
         * @param msg the client message.
         * @return the new state.
         */
        private int  handleMessage (String msg)
        {
        	if (state == STATE_CONNECTION_OPEN && msg.equals (expectedRequest))
       			return STATE_REQUEST_RECEIVED;
        	
        	if (state == STATE_REQUEST_RECEIVED && msg.matches (expectedUserAgent))
        		return STATE_AGENT_RECEIVED;
        	
        	if (state == STATE_AGENT_RECEIVED && msg.equals (expectedAuthorisation))
        		return STATE_AUTORISATION_RECEIVED;
        	
        	if (state == STATE_AUTORISATION_RECEIVED && msg.equals (""))
        		return STATE_HEADER_COMPLETE;
        	
        	if (state == STATE_HEADER_COMPLETE && msg.equals (expectedLocation))
        		return STATE_LOCATION_RECEIVED;
        	
        	return STATE_INVALID_HEADER;
        }

    }

}
