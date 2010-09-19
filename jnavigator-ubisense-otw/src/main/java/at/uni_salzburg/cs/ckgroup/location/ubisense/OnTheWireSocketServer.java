/*
 * @(#) OnTheWireSocketServer.java
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
package at.uni_salzburg.cs.ckgroup.location.ubisense;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import at.uni_salzburg.cs.ckgroup.io.IConnection;


/**
 * This class implements the socket server thread. It opens a
 * <code>ServerSocket</code> at the specified port number and waits for
 * clients to connect. It is a <code>WorkingThread</code> that takes an
 * incoming connection and does the actual receiving and sending.
 */
public class OnTheWireSocketServer extends Thread
{
	/**
	 * If <code>active</code> equals to <code>true</code> the socket
	 * server thread waits for incoming connections.
	 */
	private boolean active = true;
	
	/**
	 * The TCP/IP port number of this server. 
	 */
	private int port;
	
	/**
	 * The service to be forwarded.
	 */
	private IConnection service;
	
	/**
	 * The <code>ServerSocket</code> waiting for new connections.
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Construct a <code>SocketServerThread</code>
	 * 
	 * @param port the TCP/IP port number to listen to
	 */
	public OnTheWireSocketServer (int port, IConnection service)
	{
		this.port = port;
		this.service = service;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run ()
	{
        System.out.println ("OnTheWireSocketServer: start. port=" + port);

        try {
            serverSocket = new ServerSocket (port);
            System.out.println ("OnTheWireSocketServer: Server started");

            while (active) {
				Socket clientSocket = serverSocket.accept ();
				
				if (!active)
					  break;
				
				OnTheWireWorkerThread worker = new OnTheWireWorkerThread (service, clientSocket);
				worker.start ();
            }
        } catch (IOException e)
        {
			e.printStackTrace();
			try {
				serverSocket.close ();
            } catch (IOException e1) {}
        }

        System.out.println ("OnTheWireSocketServer: end.");
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