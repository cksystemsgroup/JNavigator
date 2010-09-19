/*
 * @(#) BufferedTransceiver.java
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

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.io.IConnection;

/**
 * This class implements a buffered transceiver. A transceiver abstracts either
 * a connection or packet oriented link to a resource. It allows sending and
 * receiving <code>Packet</code> objects without bothering about the low level
 * details of the accessed resource.
 * 
 * This transceiver uses a ring buffer of configurable size to cache messages.
 * In a separate <class>Thread</class> it forwards the messages to an underlying
 * <code>Transceiver>/code>.
 * 
 * @author Clemens Krainer
 */
public class BufferedTransceiver implements ITransceiver {
	
	/**
	 * The property key of the send buffer length.
	 */
	public static final String PROP_SEND_BUFFER_LENGTH = "send.buffer.length";
	
	/**
	 * The send ring buffer.
	 * @see PROP_SEND_BUFFER_LENGTH
	 */
	private Packet[] sendBuffer;
	
	/**
	 * The send buffer write index.
	 * @see sendBuffer
	 */
	private int sendBufferWriteIndex = 0;

	/**
	 * The send buffer read index.
	 * @see sendBuffer
	 */
	private int sendBufferReadIndex = 0;
	
	/**
	 * The <code>Writer</code> thread forwards the messages from the
	 * <code>sendBuffer</code> to the underlying <code>Transceiver</code>.
	 */
	private Writer writer;

	/**
	 * The underlying <code>Transceiver</code>.
	 */
	private Transceiver transceiver;
	
	/**
	 * The reconnection timeout in milliseconds.
	 * @see Transceiver.PROP_RECONNECTION_TIMEOUT
	 */
	private long reconnectionTimeOut;
	
	/**
	 * The <code>Properties</code> to be used for the construction of the
	 * <code>BufferedTransceiver</code> and all subcomponents.
	 */
	private Properties props;
	
	/**
	 * This variable indicates a running <code>Writer</code> <code>Thread</code>. 
	 */
	private boolean running = false;
	
	/**
	 * This variable indicates that the <code>BufferedTransceiver</code> is
	 * allowed to reestablish a broken connection.
	 */
	private boolean reConnectingAllowed;
	
	/**
	 * Construct a <code>BufferedTransceiver</code> and create the underlying transceiver.
	 * 
	 * @param props the <code>Properties</code> to be used for construction.
	 * @throws IOException thrown in case of I/O errors.
	 * @throws ConfigurationException thrown in case of configuration errors.
	 */
	public BufferedTransceiver (Properties props) throws IOException, ConfigurationException {
		this.props = props;
		transceiver = new Transceiver (props);
		init (props);
		reConnectingAllowed = true;
	}

	/**
	 * Construct a <code>BufferedTransceiver</code> and create the underlying transceiver.
	 * 
	 * @param props the <code>Properties</code> to be used for construction.
	 * @param connection the <
	 */
	public BufferedTransceiver (Properties props, IConnection connection) throws IOException, ConfigurationException {
		this.props = props;
		this.transceiver = new Transceiver (connection); 
		init (props);
		reConnectingAllowed = false;
	}
	
	/**
	 * Initialize the <code>BufferedTransceiver</code> and start the <code>Writer</code> thread.
	 * 
	 * @param props the <code>Properties</code> to be used for construction.
	 */
	public void init (Properties props) {
		reconnectionTimeOut = Long.parseLong (props.getProperty (Transceiver.PROP_RECONNECTION_TIMEOUT, "1000"));
		int sendBufferLength = Integer.parseInt (props.getProperty ("send.buffer.length","10"));
		sendBuffer = new Packet[sendBufferLength];
		writer = new Writer ();
		writer.start ();
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#send(at.uni_salzburg.cs.ckgroup.communication.Packet)
	 */
	public void send (Packet packet) throws IOException {
		if (packet == null)
			throw new NullPointerException ("Got null packet.");
		sendBuffer[sendBufferWriteIndex] = packet;
		if (sendBufferWriteIndex < sendBuffer.length-1) {
			++sendBufferWriteIndex;
		} else {
			sendBufferWriteIndex = 0;
		}
		if (writer.notActive)
			writer.interrupt();
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.ITransceiver#receive()
	 */
	public Packet receive () throws IOException 	{
		return transceiver.receive();
	}
	
	/**
	 * Return the <code>IConnection</code> of this <code>Transceiver</code>.
	 * This method is intended for unit testing only.
	 * 
	 * @return the connection.
	 */
	IConnection getConnection () {
		return transceiver.getConnection();
	}
	
	
	/**
	 * The <code>Writer</code> thread forwards the messages from the
	 * <code>sendBuffer</code> to the underlying <code>Transceiver</code>. 
	 * 
	 * @author Clemens Krainer
	 */
	class Writer extends Thread {
		/**
		 * This variable indicates that the <code>Writer</code> thread is
		 * waiting for messages to send to the underlying
		 * <code>Transceiver</code>.
		 */
		public boolean notActive = false;
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run () {
			running = true;
			while (running) {
				notActive = false;
				while (sendBufferWriteIndex != sendBufferReadIndex) {
					Packet p = sendBuffer[sendBufferReadIndex];
					try {
						transceiver.send (p);
						if (sendBufferReadIndex < sendBuffer.length-1)
							++sendBufferReadIndex;
						else
							sendBufferReadIndex = 0;
					} catch (IOException e) {
						e.printStackTrace();
						if (!reConnectingAllowed) {
							running = false;
							break;
						}
						try {
							Thread.sleep (reconnectionTimeOut);
							transceiver = new Transceiver (props);
						} catch (Exception e1) { }
					}
				}
				notActive = true;
				try {
					Thread.sleep (500);
				} catch (InterruptedException e) { }
			}
		}
	}
	
	/**
	 * Terminate the <code>Writer</code> thread.
	 */
	public void terminate () {
		running = false;
	}
}
