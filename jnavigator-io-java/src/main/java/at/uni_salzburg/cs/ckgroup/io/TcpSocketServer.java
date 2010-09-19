/*
 * @(#) TcpSocketServer.java
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
package at.uni_salzburg.cs.ckgroup.io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

/**
 * This class implements a TCP socket server. It opens a
 * <code>ServerSocket</code> at the specified port number and waits for clients
 * to connect. It is a <code>WorkingThread</code> that takes an incoming
 * connection and does the actual receiving and sending.
 * 
 * @author Clemens Krainer
 */
public abstract class TcpSocketServer extends Thread {
	
	public static final String PROP_SERVER_PORT = "server.port";
	
	/**
	 * If <code>active</code> equals to <code>true</code> the socket
	 * server thread waits for incoming connections.
	 */
	private boolean active;
	
	/**
	 * The TCP/IP port number of this server. 
	 */
	public int port;
	
	/**
	 * The <code>ServerSocket</code> waiting for new connections.
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Construct a <code>TcpSocketServer</code>
	 * 
	 * @param port the TCP/IP port number to listen to
	 * @throws IOException 
	 */
	public TcpSocketServer (Properties props) throws IOException {
		String portString = props.getProperty(PROP_SERVER_PORT);
		
		if (portString == null || "".equals(portString))
			throw new IOException ("Property " + PROP_SERVER_PORT + " is not set.");
		
		this.port = Integer.parseInt(portString);
		serverSocket = new ServerSocket (port);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run () {
        try {
//        	System.out.println ("TcpSocketServer: Server started, port=" + port);
			
        	active = true;
        	while (active) {
        		Socket clientSocket = serverSocket.accept ();
				
				if (!active)
					break;
				
				IConnection connection = new SocketWrapper (clientSocket);
				startWorkerThread (connection);
			}
        } catch (IOException e)
        {
        	if (e instanceof SocketException && e.getMessage().equals("Socket closed"))
        		System.out.println ("TcpSocketServer: Client disconnected");
        	else
        		e.printStackTrace();
        	try {
        		serverSocket.close ();
        	} catch (IOException e1) {}
        }

//		System.out.println ("TcpSocketServer: end.");
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
	
	/**
	 * Start a working thread that handles the incoming <code>IConnection</code>
	 * from a newly connected client.
	 * 
	 * @param connection
	 *            the new incoming connection.
	 */
	public abstract void startWorkerThread (IConnection connection);
}
