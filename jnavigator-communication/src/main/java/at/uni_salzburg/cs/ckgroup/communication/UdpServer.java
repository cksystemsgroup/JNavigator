/*
 * @(#) UdpServer.java
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
package at.uni_salzburg.cs.ckgroup.communication;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.io.UdpSocketServer;

/**
 * This class extends a <code>UdpSocketServer</code> for sending and receiving
 * data transfer objects.
 * 
 * @author Clemens Krainer
 */
public class UdpServer extends UdpSocketServer implements ITransceiver {

	/**
	 * Forward received objects to this provider.
	 */
	private IDataTransferObjectProvider dtoProvider;
	
	/**
	 * The attached <code>TransceiverAdapter</code>.
	 */
	private TransceiverAdapter adapter;
	
	/**
	 * Construct a <code>TcpServer</code>.
	 * 
	 * @param props the properties to be used for construction. 
	 * @throws IOException thrown in case of I/O errors.
	 * @throws ConfigurationException 
	 */
	public UdpServer(Properties props) throws IOException, ConfigurationException {
		super(props);
		adapter = new TransceiverAdapter (props, this);
		adapter.start();
	}
	
	/**
	 * @param dispatcher
	 */
	public void setDtoProvider (IDataTransferObjectProvider dispatcher) {
		this.dtoProvider = dispatcher;
		adapter.setDtoProvider (dtoProvider);
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#send(at.uni_salzburg.cs.ckgroup.communication.Packet)
	 */
	public void send(Packet packet) throws IOException {
		byte[] bytes = packet.toByteArray();
		sendDatagram(bytes, bytes.length);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#receive()
	 */
	public Packet receive () throws IOException {
		byte[] buffer = receiveDatagram ();
		Packet packet = new Packet (new ByteArrayInputStream(buffer));
		return packet;
	}
	
}
