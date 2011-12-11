/*
 * @(#) UdpSocketServer.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2010  Clemens Krainer
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
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * This class implements a UDP socket server that opens a <code>DatagramSocket</code> at the specified port number.
 * The user of this class receives datagrams sent by clients via method <code>receive()</code>. 
 * After receiving a datagram, this class memorizes the sending client in a hash table before handing the datagram to the caller.
 * The same hash table provides the client's addresses as recipients for datagrams to be sent. 
 * If a client refuses to send datagrams for <code>timeout</code> milliseconds, it is deleted from the hash table. 
 * 
 * @author Clemens Krainer
 */
public class UdpSocketServer {
	
	/**
	 * The porperty key for the server port number.
	 */
	public static final String PROP_SERVER_PORT = "server.port";
	
	/**
	 * The property key for the server timeout value in milliseconds as a
	 * <code>String</code>
	 */
	public static final String PROP_TIMEOUT = "server.timeout";
	
	/**
	 * The UDP/IP port number of this server. 
	 */
	public int port;
	
	/**
	 * The maximum time in milliseconds between having received a packet from a
	 * client and sending a packet to the client.
	 */
	public long timeout;
	
	/**
	 * The <code>DatagramSocket</code> waiting for new connections.
	 */
	private DatagramSocket serverSocket;
	
	/**
	 * The <code>Map</code> containing all active clients of this UDP/IP server.
	 */
	private Map<SocketAddress,Long> clients = new HashMap<SocketAddress,Long>();
	
	/**
	 * Construct a <code>UdpSocketServer</code>
	 * 
	 * @param port the UDP/IP port number to listen to
	 * @throws IOException 
	 */
	public UdpSocketServer (Properties props) throws IOException {

		String portString = props.getProperty(PROP_SERVER_PORT);
		if (portString == null || "".equals(portString))
			throw new IOException ("Property " + PROP_SERVER_PORT + " is not set.");
		
		String timeoutString = props.getProperty(PROP_TIMEOUT);
		if (timeoutString == null || "".equals(timeoutString))
			throw new IOException ("Property " + PROP_TIMEOUT + " is not set.");
		
		this.port = Integer.parseInt(portString);
		this.timeout = Long.parseLong(timeoutString);
		
		serverSocket = new DatagramSocket(port);
	}
	
	/**
	 * Close the server's socket.
	 */
	public void close () {
       	serverSocket.close ();
	}
	
	/**
	 * Receive a packet from a client.
	 * 
	 * @return the received packet as an array of bytes.
	 * @throws IOException thrown in case of errors.
	 */
	public byte[] receiveDatagram () throws IOException {
		byte[] buffer = new byte[256];
		DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
		serverSocket.receive(datagram);
		SocketAddress address = datagram.getSocketAddress();
		if (!clients.containsKey(address))
			System.out.println("UdpSocketServer.receiveDatagram() new Client " + address);
		clients.put (address, new Long (System.currentTimeMillis()));	// TODO use the IClock interface!
		return Arrays.copyOf (buffer, datagram.getLength());
	}

	/**
	 * Send a packet to all clients
	 * 
	 * @param buffer
	 * @param length
	 * @throws IOException 
	 */
	public void sendDatagram (byte[] buffer, int length) throws IOException {

		DatagramPacket packet = new DatagramPacket(buffer, length);
		long now = System.currentTimeMillis();	// TODO use the IClock interface!
		
		Set<Entry<SocketAddress,Long>> clientSet = clients.entrySet();
		Iterator<Entry<SocketAddress,Long>> iterator = clientSet.iterator();
		
		while (iterator.hasNext()) {
			Entry<SocketAddress,Long> e = iterator.next();
			SocketAddress address = e.getKey();
			Long lastAccess = e.getValue();
			if (now - lastAccess.longValue() > timeout) {
				System.out.println("UdpSocketServer.sendDatagram () inactive client removed " + address);
				iterator.remove();
			} else {
				packet.setSocketAddress(address);
				serverSocket.send(packet);
//				System.out.println("UdpSocketServer.sendDatagram () sending to " + address);
			}
		}
	}
}
