/*
 * @(#) Transceiver.java
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
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class implements a transceiver. A transceiver abstracts a streaming
 * connection oriented link to a resource. It allows sending and receiving
 * <code>Packet</code> objects without bothering about the low level details of
 * the accessed resource.
 * 
 * @author Clemens Krainer
 */
public class Transceiver implements ITransceiver {
	
	// TODO refactor for STREAM ONLY usage as <code>StreamTransceiver</code>
	
	/**
	 * When reconnecting this variable is used for synchronizing. 
	 */
	public Object[] lock = new Object[1];
	
	/**
	 * Constant for a connection oriented resource type. 
	 * @see See also at PROP_CONNECTION_TYPE.
	 */
	public static final String CONNECTION_STREAM = "stream";
	
	/**
	 * Constant for a packet oriented resource type.
	 * @see See also at PROP_CONNECTION_TYPE.
	 */
//	public static final String CONNECTION_DATAGRAM = "datagram";
	
	/**
	 * This property key defines the reconnection timeout in milliseconds.   
	 */
	public static final String PROP_RECONNECTION_TIMEOUT = "reconnection.timeout";
	
	/**
	 * This property key defines the maximum number of reconnects.
	 */
	public static final String PROP_MAXIMUM_RECONNECTS = "maximum.reconnects";
	
	/**
	 * This property key defines the type of the connection. See also at
	 * <code>CONNECTION_STREAM</code> and <code>CONNECTION_DATAGRAM</code>.
	 * 
	 * @see CONNECTION_STREAM
	 * @see CONNECTION_DATAGRAM
	 */
	public static final String PROP_CONNECTION_TYPE = "connection.type";
	
	/**
	 * This property key prefix prepends the connection parameter properties. 
	 */
	public static final String PROP_CONNECTION_PARAMS_PREFIX = "connection.params.";
	
	/**
	 * The <code>Properties</code> used by this transceiver and its subcomponents.
	 */
	private Properties props;
	
	/**
	 * The connection in case a connection oriented link is used.
	 */
	private IConnection connection = null;
	
	/**
	 * The <code>InputStream</code> of the connection.
	 */
	private InputStream inputStream = null;
	
	/**
	 * The <code>OutputStream</code> of the connection.
	 */
	private OutputStream outputStream = null;
	
	/**
	 * The connection type. See also at <code>CONNECTION_STREAM</code> and
	 * <code>CONNECTION_DATAGRAM</code>.
	 * 
	 * @see CONNECTION_STREAM
	 * @see CONNECTION_DATAGRAM
	 */
	private String connectionType;
	
	/**
	 * The reconnection timeout in milliseconds.
	 */
	private long reconnectionTimeOut;
	
	/**
	 * The maximum number of reconnects.
	 */
	private int maximumReconnects;
	
	/**
	 * This variable indicates that the transceiver is currently reestablishing a connection.
	 */
	private boolean reconnecting = false;
	
	/**
	 * This variable contains the number of failed reconnects. 
	 */
	private int reconnectCounter = 0;

	/**
	 * Construct a <code>Transceiver</code> by using <code>Properties</code>.
	 * This constructor allows the <code>Transceiver</code> to reconnect a link.
	 * 
	 * @param props
	 *            the <code>Properties</code> to be used for construction.
	 * @throws ConfigurationException
	 *             thrown in case of configuration errors.
	 * @throws IOException
	 *             thrown in case of I/O errors.
	 */
	public Transceiver (Properties props) throws ConfigurationException, IOException {
		this.props = props;
		reconnectionTimeOut = Long.parseLong (props.getProperty(PROP_RECONNECTION_TIMEOUT, "1000"));
		connectionType = props.getProperty(PROP_CONNECTION_TYPE);
		maximumReconnects = Integer.parseInt (props.getProperty(PROP_MAXIMUM_RECONNECTS,"100"));
		init ();
	}
	
	/**
	 * Construct a <code>Transceiver</code> by using an existing
	 * <code>IConnection</code>.
	 * 
	 * @param connection
	 *            the <code>IConnection</code> to be used for construction.
	 * @throws ConfigurationException
	 *             thrown in case of configuration errors.
	 * @throws IOException
	 *             thrown in case of I/O errors.
	 */
	public Transceiver (IConnection connection) throws ConfigurationException, IOException {
		if (connection == null)
			throw new ConfigurationException ("Connection must not be null.");
		
		maximumReconnects = 0;
		this.connection = connection;
		
		inputStream = connection.getInputStream();
		outputStream = connection.getOutputStream();
	}
	
	/**
	 * Initialize the <code>Transceiver</code>.
	 * 
	 * @throws ConfigurationException thrown in case of configuration errors.
	 * @throws IOException thrown in case of I/O errors.
	 */
	private void init () throws ConfigurationException, IOException {
		if (CONNECTION_STREAM.equals(connectionType)) {
			connection = ObjectFactory.getInstance ().instantiateIConnection (PROP_CONNECTION_PARAMS_PREFIX, props);
		} else
			throw new ConfigurationException ("Currently only stream connections are supported!");
		
		inputStream = connection.getInputStream();
		outputStream = connection.getOutputStream();
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#send(at.uni_salzburg.cs.ckgroup.communication.Packet)
	 */
	public void send (Packet packet) throws IOException {
//		System.out.println ("Transceiver.send length=" + packet.toByteArray().length);
		
		boolean ok = false;
		
		while (!ok) {
			try {
				outputStream.write (packet.toByteArray());
				ok = true;
				reconnectCounter = 0;
			} catch (IOException e) {
				reconnect (e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#receive()
	 */
	public Packet receive () throws IOException 	{
		Packet p = null;
		boolean ok = false;
		
		while (!ok) {
			try {
				p = new Packet (inputStream);
				ok = true;
				reconnectCounter = 0;
			} catch (IOException e) {
				reconnect (e);
			}
		}
		
		return p;
	}
	
	/**
	 * Reconnect the link to the resource. The reconnecting sequence employs the
	 * <code>lock</code> object to synchronize this method to avoid multiple
	 * parallel connection initializations.
	 * 
	 * @param exception
	 *            the I/O exception that caused the link break.
	 * @throws IOException
	 *             thrown in case that the number of reconnects exceeds the
	 *             maximum allowed number.
	 */
	private void reconnect (IOException exception) throws IOException {
		boolean isCurrentlyReconnecting;

		if (maximumReconnects <= 0)
			throw exception;
		
		synchronized (lock) {
			isCurrentlyReconnecting = reconnecting;
			if (!reconnecting)
				reconnecting = true;
		}
		
		if (!isCurrentlyReconnecting) {
			System.out.println ("Connection lost, reconnecting (" + reconnectCounter + ")");
			exception.printStackTrace();
			try { Thread.sleep (reconnectionTimeOut); } catch (InterruptedException e1) { }
			try { init (); } catch (ConfigurationException e1) { }
			if (++reconnectCounter > maximumReconnects)
				throw new IOException ("Exceeded the maximum number of allowed reconnects. Cause was: " + exception.getMessage());
			reconnecting = false;
		} else {
			try { Thread.sleep (reconnectionTimeOut); } catch (InterruptedException e1) { }
			while (reconnecting)
				try { Thread.sleep (50); } catch (InterruptedException e1) { }
		}
	}
	
	/**
	 * Return the <code>IConnection</code> of this <code>Transceiver</code>.
	 * This method is intended for unit testing only.
	 * 
	 * @return the connection.
	 */
	IConnection getConnection () {
		return connection;
	}
}
