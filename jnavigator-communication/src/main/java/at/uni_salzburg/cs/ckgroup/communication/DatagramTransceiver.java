/*
 * @(#) DatagramTransceiver.java
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
package at.uni_salzburg.cs.ckgroup.communication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This class implements a transceiver that abstracts a packet oriented link to
 * a resource. It allows sending and receiving <code>Packet</code> objects
 * without bothering about the low level details of the accessed resource.
 * 
 * @author Clemens Krainer
 */
public class DatagramTransceiver implements ITransceiver {
	
	/**
	 * Constants for property keys. 
	 */
	public static final String PROP_HOST = "host";
	public static final String PROP_PORT = "port";
	
	/**
	 * This transceiver's socket for communication to the datagram service. 
	 */
	private DatagramSocket clientSocket;
	
	/**
	 * The address and port number of the datagram service.
	 */
	private InetSocketAddress serverAddress;
	
	/**
	 * Construct a <code>DatagramTransceiver</code> by using <code>Properties</code>.
	 * 
	 * @param props the <code>Properties</code> to be used for construction.
	 * @throws SocketException thrown in case of I/O errors.
	 */
	public DatagramTransceiver (Properties props) throws ConfigurationException, SocketException {
		String host = props.getProperty (PROP_HOST);
		if (host == null || "".equals(host))
			throw new ConfigurationException ("Property " + PROP_HOST + " is not set.");
		
		String portString = props.getProperty (PROP_PORT);
		if (portString == null || "".equals(portString))
			throw new ConfigurationException ("Property " + PROP_PORT + " is not set.");
		
		int port = Integer.parseInt (portString);
		serverAddress = new InetSocketAddress(host, port);
		clientSocket = new DatagramSocket();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#send(at.uni_salzburg.cs.ckgroup.communication.Packet)
	 */
	public void send(Packet packet) throws IOException {
		byte[] ba = packet.toByteArray();
		DatagramPacket datagramPacket = new DatagramPacket(ba, ba.length, serverAddress);
		synchronized (this) {
			clientSocket.send(datagramPacket);
		}
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#receive()
	 */
	public Packet receive() throws IOException {
		byte[] buffer = new byte[256];
		DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
		clientSocket.receive(datagramPacket);
		return new Packet (new ByteArrayInputStream (Arrays.copyOf (buffer, datagramPacket.getLength())));
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#close()
	 */
	public void close() {
		clientSocket.close();
	}

}
