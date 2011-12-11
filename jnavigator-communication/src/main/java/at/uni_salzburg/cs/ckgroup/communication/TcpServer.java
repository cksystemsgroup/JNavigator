/*
 * @(#) TcpServer.java
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

import java.io.IOException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.io.TcpSocketServer;

/**
 * This class extends a <code>TcpSocketServer</code> for sending and receiving
 * data transfer objects.
 * 
 * @author Clemens Krainer
 */
public class TcpServer extends TcpSocketServer implements IDataTransferObjectForwarder {

	/**
	 * The properties of this <code>TcpServer</code> and it's attached objects.
	 */
	private Properties props;
	
	/**
	 * Forward received objects to this provider.
	 */
	private IDataTransferObjectProvider dtoProvider;
	
	/**
	 * Construct a <code>TcpServer</code>.
	 * 
	 * @param props the properties to be used for construction. 
	 * @throws IOException thrown in case of I/O errors.
	 */
	public TcpServer(Properties props) throws IOException {
		super(props);
		this.props = props;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.TcpSocketServer#startWorkerThread(at.uni_salzburg.cs.ckgroup.io.IConnection)
	 */
	public void startWorkerThread (IConnection connection) {
		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props, connection);
			TransceiverAdapter adapter = new TransceiverAdapter (props, transceiver);
			adapter.setDtoProvider (dtoProvider);
			adapter.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param dispatcher
	 */
	public void setDtoProvider (IDataTransferObjectProvider dispatcher) {
		this.dtoProvider = dispatcher;
	}
}
