/*
 * @(#) TransceiverTestCase.java
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

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This test case verifies the implementation of the <code>Transceiver</code> class.
 * 
 * @author Clemens Krainer
 */
public class TransceiverTestCase extends TestCase {

	/**
	 * The properties used for the tests.
	 */
	private Properties props;
	
	/**
	 * The global transceiver used for the tests.
	 */
	private Transceiver transceiver;
	
	/**
	 * A global packet used for the tests.
	 */
	private Packet packet;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		props = new Properties ();
		props.setProperty(Transceiver.PROP_CONNECTION_TYPE, "stream");
		props.setProperty(Transceiver.PROP_RECONNECTION_TIMEOUT, "100");
		props.setProperty(Transceiver.PROP_MAXIMUM_RECONNECTS, "3");
		props.setProperty(Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.communication.FakeConnection");
	}
	
	/**
	 * Compare two byte arrays.
	 * 
	 * @param a the first byte array.
	 * @param b the second byte array.
	 */
	void arrayCompare (byte[] a, byte[] b) {
		assertEquals ("array length", a.length, b.length);
		
		for (int k=0; k < a.length; k++)
			assertEquals ("a["+k+"] != b["+k+"]", a[k], b[k]);
	}
	
	/**
	 * Send one <code>Packet</code> to the transceiver and verify that it goes
	 * to the connection.
	 */
	public void testCase01 () {
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			transceiver = new Transceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			Packet packet = new Packet ((byte)1,packet1);
			assertNotNull (packet);
			
			transceiver.send(packet);
			byte[] buf = connection.getWriteBuffer();
			assertNotNull (buf);
			arrayCompare (packet.toByteArray(), buf);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Read one <code>Packet</code> from the transceiver and verify that it.
	 */
	public void testCase02 () {
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			transceiver = new Transceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			Packet packet = new Packet ((byte)1,packet1);
			assertNotNull (packet);
			
			connection.setReadBuffer(packet.toByteArray());
			
			Packet r = transceiver.receive ();
			assertNotNull (r);
			arrayCompare (packet.toByteArray(), r.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that an <code>IOException</code> is thrown if the reconnects
	 * exceed the maximum allowed number.
	 */
	public void testCase03 () {
		try {
			transceiver = new Transceiver (props);
			assertNotNull (transceiver);
			
			Packet r = transceiver.receive ();
			assertNull (r);
			
		} catch (IOException e) {
			assertEquals ("Exceeded the maximum number of allowed reconnects. Cause was: FakeConnection is closed.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that only the connection type "stream" is allowed.
	 */
	public void testCase04 () {
		props.setProperty(Transceiver.PROP_CONNECTION_TYPE, "datagram");
		
		try {
			transceiver = new Transceiver (props);
			assertNull (transceiver);
		} catch (ConfigurationException e) {
			assertEquals ("Currently only stream connections are supported!", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Try to provoke a parallel reconnect.
	 */
	public void testCase05 () {
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};
		
		props.setProperty(Transceiver.PROP_RECONNECTION_TIMEOUT, "800");
		
		try {
			packet = new Packet ((byte)1,packet1);
			assertNotNull (packet);
			
			transceiver = new Transceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			connection.setFakeIOException (true);
			
			Thread t = new Thread () {
				public void run () {
					try {
						transceiver.send (packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			t.start();
			try { Thread.sleep(100); } catch (Exception e) { }
			Packet r = transceiver.receive ();
			assertNotNull (r);
			arrayCompare(packet.toByteArray(), r.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			assertEquals ("Exceeded the maximum number of allowed reconnects. Cause was: FakeConnection is closed.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Create a connection and pass it to the transceiver. Send one
	 * <code>Packet</code> to the transceiver and verify that it goes to the
	 * connection.
	 */
	public void testCase06 () {
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};
		
		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			transceiver = new Transceiver (connection);
			assertNotNull (transceiver);
			
			Packet packet = new Packet ((byte)1,packet1);
			assertNotNull (packet);
			
			transceiver.send(packet);
			byte[] buf = connection.getWriteBuffer();
			assertNotNull (buf);
			arrayCompare (packet.toByteArray(), buf);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Create a connection and pass it to the transceiver. Read one
	 * <code>Packet</code> from the transceiver and verify that it.
	 */
	public void testCase07 () {
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};
		
		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			transceiver = new Transceiver (connection);
			assertNotNull (transceiver);
			
			Packet packet = new Packet ((byte)1,packet1);
			assertNotNull (packet);
			
			connection.setReadBuffer(packet.toByteArray());
			
			Packet r = transceiver.receive ();
			assertNotNull (r);
			arrayCompare (packet.toByteArray(), r.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Create a connection and pass it to the transceiver. Verify that
	 * <code>send()</code> throws an <code>IOException</code> on I/O errors.
	 */
	public void testCase08 () {
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};
		
		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			transceiver = new Transceiver (connection);
			assertNotNull (transceiver);
			
			Packet packet = new Packet ((byte)1,packet1);
			assertNotNull (packet);
			
			connection.setFakeIOException (true);
			transceiver.send(packet);
			fail ();
		} catch (IOException e) {
			assertEquals ("write() faked an IOException", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Create a connection and pass it to the transceiver. Verify that
	 * <code>receive()</code> throws an <code>IOException</code> on I/O errors.
	 */
	public void testCase09 () {
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};
		
		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			transceiver = new Transceiver (connection);
			assertNotNull (transceiver);
			
			Packet packet = new Packet ((byte)1,packet1);
			assertNotNull (packet);
			
			connection.setReadBuffer(packet.toByteArray());
			connection.setFakeIOException (true);
			Packet r = transceiver.receive ();
			assertNull (r);
		} catch (IOException e) {
			assertEquals ("read() faked an IOException", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Create a transceiver using a null connection Verify that the constructor
	 * throws a <code>ConfigurationException</code>.
	 */
	public void testCase10 () {
		try {
			transceiver = new Transceiver ( (IConnection)null);
			assertNull (transceiver);
		} catch (ConfigurationException e) {
			assertEquals ("Connection must not be null.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
}
