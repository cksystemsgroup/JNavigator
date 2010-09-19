/*
 * @(#) BufferedBufferedTransceiverTestCase.java
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
import at.uni_salzburg.cs.ckgroup.util.ByteArrayUtils;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This test cases verify the implementation of the
 * <code>BufferedTransceiver</code> class.
 * 
 * @author Clemens Krainer
 */
public class BufferedTransceiverTestCase extends TestCase {

	/**
	 * The properties used for the tests.
	 */
	private Properties props;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		props = new Properties ();
		props.setProperty(Transceiver.PROP_CONNECTION_TYPE, "stream");
		props.setProperty(Transceiver.PROP_RECONNECTION_TIMEOUT, "100");
		props.setProperty(Transceiver.PROP_MAXIMUM_RECONNECTS, "3");
		props.setProperty(Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.communication.FakeConnection");
		props.setProperty(BufferedTransceiver.PROP_SEND_BUFFER_LENGTH, "5");
		props.setProperty(Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"fake.io.exception", "false");
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
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNotNull (transceiver);
			Thread.sleep(100);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);
			
			transceiver.send(p);
			Thread.sleep(100);
			byte[] buf = connection.getWriteBuffer();
			assertNotNull (buf);
			arrayCompare (p.toByteArray(), buf);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Send one <code>Packet</code> to the transceiver and verify that it goes
	 * to the connection.
	 */
	public void testCase02 () {
		
		String sbLengthString = props.getProperty(BufferedTransceiver.PROP_SEND_BUFFER_LENGTH);
		assertNotNull (sbLengthString);
		int sendBufferLength = Integer.parseInt (sbLengthString);
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);

			for (int k=0; k < 2*sendBufferLength; k++) {
				transceiver.send(p);
				Thread.sleep(200);
				byte[] buf = connection.getWriteBuffer();
				assertNotNull (buf);
				arrayCompare (p.toByteArray(), buf);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Send one <code>Packet</code> to the transceiver and verify that it goes
	 * to the connection.
	 */
	public void testCase03 () {
		
		String sbLengthString = props.getProperty(BufferedTransceiver.PROP_SEND_BUFFER_LENGTH);
		assertNotNull (sbLengthString);
		int sendBufferLength = Integer.parseInt (sbLengthString);
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			Thread.sleep(200);
			for (int k=0; k < sendBufferLength-1; k++) {
				Packet p = new Packet ((byte)k,packet1);
				assertNotNull (p);
				transceiver.send(p);
			}

			Thread.sleep(200);
			byte[] buf = connection.getWriteBuffer();
			assertNotNull (buf);
			
			for (int k=0; k < sendBufferLength-1; k++) {
				System.out.println ("Check packet " + k);
				Packet p = new Packet ((byte)k,packet1);
				assertNotNull (p);
				byte[] pb = p.toByteArray();
				byte[] part = ByteArrayUtils.partition(buf, k*pb.length, pb.length);
				arrayCompare (pb, part);
			}
			
			for (int k=0; k < sendBufferLength-1; k++) {
				Packet p = new Packet ((byte)k,packet1);
				assertNotNull (p);
				transceiver.send(p);
			}

			Thread.sleep(200);
			buf = connection.getWriteBuffer();
			assertNotNull (buf);
			
			for (int k=0; k < sendBufferLength-1; k++) {
				System.out.println ("Check packet " + k);
				Packet p = new Packet ((byte)k,packet1);
				assertNotNull (p);
				byte[] pb = p.toByteArray();
				byte[] part = ByteArrayUtils.partition(buf, k*pb.length, pb.length);
				arrayCompare (pb, part);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Read one <code>Packet</code> from the transceiver and verify it.
	 */
	public void testCase04 () {
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);
			
			connection.setReadBuffer(p.toByteArray());
			
			Packet r = transceiver.receive ();
			assertNotNull (r);
			arrayCompare (p.toByteArray(), r.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that the transceiver tries to re-open a connection up to the
	 * maximum number of failed reconnects in succession.
	 */
	public void testCase05 () {
		
		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
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
	 * Verify that only "stream" connections are allowed.
	 */
	public void testCase06 () {
		props.setProperty(Transceiver.PROP_CONNECTION_TYPE, "datagram");
		
		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNull (transceiver);
		} catch (ConfigurationException e) {
			assertEquals ("Currently only stream connections are supported!", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Send one null <code>Packet</code> to the transceiver and verify that it throws
	 * an exception.
	 */
	public void testCase07 () {
		
		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			transceiver.send (null);
			fail ();
		} catch (NullPointerException e) {
			assertEquals ("Got null packet.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Send one <code>Packet</code> to the transceiver and verify that it throws
	 * an IOException on I/O errors and reopens the connection. Check if the current
	 * <code>Packet</code> is retransmitted.
	 */
	public void testCase08 () {
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNotNull (transceiver);
			
			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);
			
			connection.setFakeIOException (true);
			
			transceiver.send(p);
			
			Thread.yield();
			
			FakeConnection newConnection = (FakeConnection) transceiver.getConnection();
			
			int counter = 50;
			while (newConnection == connection && --counter > 0) {
				Thread.sleep(100);
				newConnection = (FakeConnection) transceiver.getConnection();
			}
			
			assertFalse ("Check reconnection of Transceiver", newConnection == connection);

			Thread.sleep(200);
			byte[] buf = newConnection.getWriteBuffer();
			assertNotNull (buf);
			arrayCompare (p.toByteArray(), buf);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Send one <code>Packet</code> to a transceiver that has a faulty
	 * connection. Verify that it re-connects and re-sends the
	 * <code>Packet</code> without throwing an <code>IOException</code> if the
	 * number of re-connects do not exceed the maximum allowed.
	 */
	public void testCase09 () {
		
		props.setProperty(Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"fake.io.exception", "true");
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};
		
		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props);
			assertNotNull (transceiver);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);
			
			transceiver.send (p);
			Thread.sleep (1500);
			
			props.setProperty(Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"fake.io.exception", "false");
			Thread.sleep(800);

			FakeConnection connection = (FakeConnection) transceiver.getConnection();
			assertNotNull (connection);
			
			byte[] buf = connection.getWriteBuffer();
			assertNotNull (buf);
			arrayCompare (p.toByteArray(), buf);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Construct a <code>BufferedTransceiver</code> by using an existing
	 * connection. Send one <code>Packet</code> to the transceiver and verify
	 * that it goes to the connection.
	 */
	public void testCase10 () {
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			BufferedTransceiver transceiver = new BufferedTransceiver (props, connection);
			assertNotNull (transceiver);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);
			
			transceiver.send(p);
			Thread.sleep(100);
			byte[] buf = connection.getWriteBuffer();
			assertNotNull (buf);
			arrayCompare (p.toByteArray(), buf);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Construct a <code>BufferedTransceiver</code> by using an existing
	 * connection. Send one <code>Packet</code> to the transceiver and verify
	 * that it goes to the connection.
	 */
	public void testCase11 () {
		
		String sbLengthString = props.getProperty(BufferedTransceiver.PROP_SEND_BUFFER_LENGTH);
		assertNotNull (sbLengthString);
		int sendBufferLength = Integer.parseInt (sbLengthString);
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			BufferedTransceiver transceiver = new BufferedTransceiver (props, connection);
			assertNotNull (transceiver);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);

			for (int k=0; k < 2*sendBufferLength; k++) {
				transceiver.send(p);
				Thread.sleep(200);
				byte[] buf = connection.getWriteBuffer();
				assertNotNull (buf);
				arrayCompare (p.toByteArray(), buf);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Construct a <code>BufferedTransceiver</code> by using an existing
	 * connection. Send one <code>Packet</code> to the transceiver and verify
	 * that it does not reconnect a failing connection. 
	 */
	public void testCase12 () {
		
		props.setProperty(Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"fake.io.exception", "true");
		
		byte [] packet1 = {10,11,12,13,14,15,16,17,18,19};

		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			BufferedTransceiver transceiver = new BufferedTransceiver (props, connection);
			assertNotNull (transceiver);
			
			Packet p = new Packet ((byte)1,packet1);
			assertNotNull (p);

			transceiver.send(p);
			Thread.sleep(500);
			byte[] buf = connection.getWriteBuffer();
			assertNotNull (buf);
			assertEquals (0, buf.length);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Construct a <code>BufferedTransceiver</code> by using a null connection.
	 * Verify that the constructor throws a <code>ConfigurationException</code>.
	 */
	public void testCase13 () {
		try {
			BufferedTransceiver transceiver = new BufferedTransceiver (props, (IConnection)null);
			assertNull (transceiver);
		} catch (ConfigurationException e) {
			assertEquals ("Connection must not be null.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
}
