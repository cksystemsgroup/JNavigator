/*
 * @(#) TransceiverAdapterTestCase.java
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
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

public class TransceiverAdapterTestCase extends TestCase {

	private IDataTransferObjectProvider dtoProvider;
	private Properties props;
	
	private Packet packetOne;
//	private Packet packetTwo;
//	private Packet packetThree;
	private Packet packetFour;
	
	private MockListenerOne listenerOne;
	private MockListenerTwo listenerTwo;
	
	void arrayCompare (byte[] a, byte[] b) {
		assertEquals ("array length", a.length, b.length);
		
		for (int k=0; k < a.length; k++)
			assertEquals ("a["+k+"] != b["+k+"]", a[k], b[k]);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		
		props = new Properties ();
		
		props.setProperty (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.communication.BufferedTransceiver");
		props.setProperty (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+Transceiver.PROP_CONNECTION_TYPE, "stream");
		props.setProperty (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+Transceiver.PROP_RECONNECTION_TIMEOUT,"100000");
		props.setProperty (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+Transceiver.PROP_MAXIMUM_RECONNECTS, "1");
		props.setProperty (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"className",
				"at.uni_salzburg.cs.ckgroup.communication.FakeConnection");
		
		props.setProperty (TransceiverAdapter.PROP_MAPPING_LIST, "one, two, three");
		
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"one"+TransceiverAdapter.PROP_MAPPING_TYPE_SUFFIX, "1");
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"one"+TransceiverAdapter.PROP_MAPPING_CLASS_NAME_SUFFIX,
				"at.uni_salzburg.cs.ckgroup.communication.MockDataTransferObjectOne");
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"one"+TransceiverAdapter.PROP_MAPPING_REGISTER_SUFFIX, "true");
		
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"two"+TransceiverAdapter.PROP_MAPPING_TYPE_SUFFIX, "2");
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"two"+TransceiverAdapter.PROP_MAPPING_CLASS_NAME_SUFFIX,
				"at.uni_salzburg.cs.ckgroup.communication.MockDataTransferObjectTwo");
		
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"three"+TransceiverAdapter.PROP_MAPPING_TYPE_SUFFIX, "3");
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"three"+TransceiverAdapter.PROP_MAPPING_CLASS_NAME_SUFFIX,
				"at.uni_salzburg.cs.ckgroup.communication.MockDataTransferObjectThree");
		
		packetOne =   new Packet ((byte)1, new byte[] {1,2,3,4,5,6,7,8,9,0});
//		packetTwo =   new Packet ((byte)2, new byte[] {2,3,4,5,6,7,8,9,0,1});
//		packetThree = new Packet ((byte)3, new byte[] {3,4,5,6,7,8,9,0,1,2});
		packetFour =  new Packet ((byte)4, new byte[] {4,5,6,7,8,9,0,1,2,3});
		
		listenerOne = new MockListenerOne ();
		listenerTwo = new MockListenerTwo ();
		
		dtoProvider = new Dispatcher ();		
		dtoProvider.addDataTransferObjectListener(listenerOne, IDataTransferObject.class);
		dtoProvider.addDataTransferObjectListener(listenerTwo, MockDataTransferObjectTwo.class);
	}

	
	/**
	 * Verify the message forwarding from the
	 * <code>IConnection>/code> to the associated
	 * listeners.
	 */
	public void testCase01 () {
		try {
			TransceiverAdapter ta = new TransceiverAdapter (props);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);
			
			BufferedTransceiver transceiver = (BufferedTransceiver) ta.getTransceiver();
			FakeConnection connection = (FakeConnection) transceiver.getConnection();

			connection.setReadBuffer (packetOne.toByteArray());
			ta.start ();
			try { Thread.sleep (200); } catch (InterruptedException e) { }
			transceiver.terminate();
			
			assertEquals (1, listenerOne.counter);
			arrayCompare (packetOne.getPayload(), listenerOne.dto.toByteArray());
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Verify the message forwarding from the dispatcher to the associated
	 * <code>IConnection>/code>.
	 */
	public void testCase02 () {
		try {
			TransceiverAdapter ta = new TransceiverAdapter (props);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);
			ta.start ();

			BufferedTransceiver transceiver = (BufferedTransceiver) ta.getTransceiver();
			FakeConnection connection = (FakeConnection) transceiver.getConnection();

			MockDataTransferObjectOne one = new MockDataTransferObjectOne (new byte[] {1,9,8,7,6,5,4,3,2,1,0});
			Packet packetOne = new Packet ((byte)1, one.toByteArray());
			dtoProvider.dispatch (listenerOne, one);
			
			try { Thread.sleep (500); } catch (InterruptedException e) { }
			transceiver.terminate();

			
			byte[] received = connection.getWriteBuffer();
			
			System.out.print ("received: ");
			for (int k=0; k < received.length; k++) {
				System.out.print (k==0?"[":", ");
				System.out.print ((received[k]+256)&0xFF);
			}
			System.out.println ("]");
			
			arrayCompare (packetOne.toByteArray(), received);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that a missing "mapping.list" property causes a
	 * <code>ConfigurationException</code>
	 */
	public void testCase03 () {
		props.remove (TransceiverAdapter.PROP_MAPPING_LIST);
		TransceiverAdapter ta;
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			assertEquals ("Property mapping.list not configured.", e.getMessage());
		}

		props.setProperty (TransceiverAdapter.PROP_MAPPING_LIST, "");
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			assertEquals ("Property mapping.list not configured.", e.getMessage());
		}
	}
	
	/**
	 * Verify that a missing "mapping.*.type" property causes a
	 * <code>ConfigurationException</code>
	 */
	public void testCase04 () {
		props.remove (TransceiverAdapter.PROP_MAPPING_PREFIX+"two"+TransceiverAdapter.PROP_MAPPING_TYPE_SUFFIX);
		TransceiverAdapter ta;
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			assertEquals ("Property mapping.two.type not configured.", e.getMessage());
		}
		
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"two"+TransceiverAdapter.PROP_MAPPING_TYPE_SUFFIX, "");
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			assertEquals ("Property mapping.two.type not configured.", e.getMessage());
		}
	}
	
	/**
	 * Verify that a missing "mapping.*.className" property causes a
	 * <code>ConfigurationException</code>
	 */
	public void testCase05 () {
		props.remove (TransceiverAdapter.PROP_MAPPING_PREFIX+"three"+TransceiverAdapter.PROP_MAPPING_CLASS_NAME_SUFFIX);
		TransceiverAdapter ta;
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			assertEquals ("Property mapping.three.className not configured.", e.getMessage());
		}
		
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"three"+TransceiverAdapter.PROP_MAPPING_CLASS_NAME_SUFFIX, "");
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			assertEquals ("Property mapping.three.className not configured.", e.getMessage());
		}
	}
	
	/**
	 * Verify that a mapped class must implement the <class>IDataTransferObject</code>
	 * interface.
	 */
	public void testCase06 () {
		props.setProperty(TransceiverAdapter.PROP_MAPPING_PREFIX+"three"+TransceiverAdapter.PROP_MAPPING_CLASS_NAME_SUFFIX,
				"at.uni_salzburg.cs.ckgroup.communication.MockListenerThree");
		TransceiverAdapter ta;
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			assertEquals ("Class at.uni_salzburg.cs.ckgroup.communication.MockListenerThree at mapping.three.className is no derivative of at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject", e.getMessage());
		}
	}
	
	/**
	 * Verify that a "mapping.*.type" must be an integer greater zero. 
	 */
	public void testCase07 () {
		props.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"two"+TransceiverAdapter.PROP_MAPPING_TYPE_SUFFIX, "0");
		TransceiverAdapter ta;
		try {
			ta = new TransceiverAdapter (props);
			assertNull (ta);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			assertEquals ("Property mapping.two.type has not been assigned a number > 0.", e.getMessage());
		}
	}
	
	/**
	 * Verify that a packet of an unregistered type is not delivered. 
	 */
	public void testCase08 () {
		try {
			TransceiverAdapter ta = new TransceiverAdapter (props);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);
			
			BufferedTransceiver transceiver = (BufferedTransceiver) ta.getTransceiver();
			FakeConnection connection = (FakeConnection) transceiver.getConnection();

			connection.setReadBuffer (packetFour.toByteArray());
			ta.start ();
			try { Thread.sleep (200); } catch (InterruptedException e) { }
			transceiver.terminate();
			
			assertEquals (0, listenerOne.counter);
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that an unregistered <code>IDataTransferObject</code> is not delivered.
	 */
	public void testCase09 () {
		try {
			TransceiverAdapter ta = new TransceiverAdapter (props);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);
			ta.start ();

			BufferedTransceiver transceiver = (BufferedTransceiver) ta.getTransceiver();
			FakeConnection connection = (FakeConnection) transceiver.getConnection();

			MockDataTransferObjectFour four = new MockDataTransferObjectFour (new byte[] {1,9,8,7,6,5,4,3,2,1,0});
			dtoProvider.dispatch (listenerOne, four);
			
			try { Thread.sleep (200); } catch (InterruptedException e) { }
			ta.terminate();
			try { Thread.sleep (200); } catch (InterruptedException e) { }
			
			byte[] received = connection.getWriteBuffer();
			assertEquals (0, received.length);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that an unregistered <code>IDataTransferObject</code> causes an <code>IOException</code>.
	 */
	public void testCase10 () {
		try {
			TransceiverAdapter ta = new TransceiverAdapter (props);
			assertNotNull (ta);
			MockDataTransferObjectFour four = new MockDataTransferObjectFour (new byte[] {1,9,8,7,6,5,4,3,2,1,0});
			ta.receive (four);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		} catch (IOException e) {
			assertEquals ("No mapping found for IDataTransferObject: class at.uni_salzburg.cs.ckgroup.communication.TransceiverAdapterTestCase$MockDataTransferObjectFour", e.getMessage());
		}
	}
	
	/**
	 * Try to provoke a parallel reconnection in the underlying transceiver.
	 */
	public void testCase11 () {
		props.setProperty (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+Transceiver.PROP_RECONNECTION_TIMEOUT,"100");
		try {
			TransceiverAdapter ta = new TransceiverAdapter (props);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);
			ta.start ();

			BufferedTransceiver transceiver = (BufferedTransceiver) ta.getTransceiver();
			FakeConnection connection = (FakeConnection) transceiver.getConnection();

			MockDataTransferObjectFour four = new MockDataTransferObjectFour (new byte[] {1,9,8,7,6,5,4,3,2,1,0});
			dtoProvider.dispatch (listenerOne, four);
			
			try { Thread.sleep (200); } catch (InterruptedException e) { }
			ta.terminate();
			try { Thread.sleep (200); } catch (InterruptedException e) { }
			
			byte[] received = connection.getWriteBuffer();
			assertEquals (0, received.length);
			
			connection.setReadBuffer (packetOne.toByteArray());
			try { Thread.sleep (200); } catch (InterruptedException e) { }
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the message forwarding from the
	 * <code>IConnection>/code> to the associated
	 * listeners.
	 */
	public void testCase12 () {
		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			BufferedTransceiver transceiver = new BufferedTransceiver (props, connection); 
			
			TransceiverAdapter ta = new TransceiverAdapter (props, transceiver);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);
			
			connection.setReadBuffer (packetOne.toByteArray());
			ta.start ();
			try { Thread.sleep (500); } catch (InterruptedException e) { }
			transceiver.terminate();
			
			assertEquals (1, listenerOne.counter);
			arrayCompare (packetOne.getPayload(), listenerOne.dto.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Verify the message forwarding from the dispatcher to the associated
	 * <code>IConnection>/code>.
	 */
	public void testCase13 () {
		try {
			FakeConnection connection = (FakeConnection) ObjectFactory.getInstance ().instantiateIConnection (TransceiverAdapter.PROP_TRANSCEIVER_PREFIX+Transceiver.PROP_CONNECTION_PARAMS_PREFIX, props);
			assertNotNull (connection);
			
			BufferedTransceiver transceiver = new BufferedTransceiver (props, connection); 
			
			TransceiverAdapter ta = new TransceiverAdapter (props, transceiver);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);		
	
			MockDataTransferObjectOne one = new MockDataTransferObjectOne (new byte[] {1,9,8,7,6,5,4,3,2,1,0});
			Packet packetOne = new Packet ((byte)1, one.toByteArray());
			dtoProvider.dispatch (listenerOne, one);
			
			try { Thread.sleep (500); } catch (InterruptedException e) { }
			transceiver.terminate();

			
			byte[] received = connection.getWriteBuffer();
			
			System.out.print ("received: ");
			for (int k=0; k < received.length; k++) {
				System.out.print (k==0?"[":", ");
				System.out.print ((received[k]+256)&0xFF);
			}
			System.out.println ("]");
			
			arrayCompare (packetOne.toByteArray(), received);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Create a transceiver adapter using a null transceiver. Verify that the
	 * constructor throws a <code>ConfigurationException</code>.
	 */
	public void testCase14 () {
		try {
			TransceiverAdapter transceiver = new TransceiverAdapter (props, (ITransceiver)null);
			assertNull (transceiver);
		} catch (ConfigurationException e) {
			assertEquals ("The transceiver must not be null.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Send a message to a listener that throws a
	 * <code>CommunicationException</code>. Verify that the
	 * <code>TransceiverAdapter</code> continues to run.
	 */
	public void testCase15 () {
		listenerOne.throwCommunicationException = true;
		try {
			TransceiverAdapter ta = new TransceiverAdapter (props);
			assertNotNull (ta);
			ta.setDtoProvider (dtoProvider);
			
			BufferedTransceiver transceiver = (BufferedTransceiver) ta.getTransceiver();
			FakeConnection connection = (FakeConnection) transceiver.getConnection();

			connection.setReadBuffer (packetOne.toByteArray());
			ta.start ();
			try { Thread.sleep (300); } catch (InterruptedException e) { }
//			assertFalse (ta.getState() == Thread.State.TERMINATED);
			transceiver.terminate();
			
			assertEquals (0, listenerOne.counter);
			arrayCompare (packetOne.getPayload(), listenerOne.dto.toByteArray());
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This class implements a <code>IDataTransferObject</code> derivative to be used in the
	 * unit tests.
	 * 
	 * @author Clemens Krainer
	 */
	private class MockDataTransferObjectFour implements IDataTransferObject {
		protected byte[] content;
		public MockDataTransferObjectFour (byte[] content) { this.content = content; }
		public byte[] toByteArray() { return content; }
	}
}
