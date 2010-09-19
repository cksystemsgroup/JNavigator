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

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.io.TcpSocketServer;

/**
 * This test verifies the implementation of the <code>TcpServer</code> class.
 * 
 * @author Clemens Krainer
 */
public class TcpServerTestCase extends TestCase {
	
	private IDataTransferObjectProvider dtoProvider;
	private Properties serverProps;
	private Properties clientProps;
	private Packet packetOne;
	private MockListenerOne listenerOne;
	
	public void setUp () throws Exception {
		super.setUp();
		
		serverProps = new Properties ();
		serverProps.setProperty (TcpSocketServer.PROP_SERVER_PORT, "5469");
		
		serverProps.setProperty (TransceiverAdapter.PROP_MAPPING_LIST, "one");
		
		serverProps.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"one"+TransceiverAdapter.PROP_MAPPING_TYPE_SUFFIX, "1");
		serverProps.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"one"+TransceiverAdapter.PROP_MAPPING_CLASS_NAME_SUFFIX,
				"at.uni_salzburg.cs.ckgroup.communication.MockDataTransferObjectOne");
		serverProps.setProperty (TransceiverAdapter.PROP_MAPPING_PREFIX+"one"+TransceiverAdapter.PROP_MAPPING_REGISTER_SUFFIX, "true");
		
		packetOne =   new Packet ((byte)1, new byte[] {1,2,3,4,5,6,7,8,9,0});
		
		listenerOne = new MockListenerOne ();
		
		dtoProvider = new Dispatcher ();		
		dtoProvider.addDataTransferObjectListener(listenerOne, IDataTransferObject.class);
		
		clientProps = new Properties ();
		clientProps.setProperty (Transceiver.PROP_CONNECTION_TYPE, "stream");
		clientProps.setProperty (Transceiver.PROP_RECONNECTION_TIMEOUT,"100000");
		clientProps.setProperty (Transceiver.PROP_MAXIMUM_RECONNECTS, "0");
		clientProps.setProperty (Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.io.TcpSocket");
		clientProps.setProperty (Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"host", "127.0.0.1");
		clientProps.setProperty (Transceiver.PROP_CONNECTION_PARAMS_PREFIX+"port", "5469");
	}

	void arrayCompare (byte[] a, byte[] b) {
		assertEquals ("array length", a.length, b.length);
		
		for (int k=0; k < a.length; k++)
			assertEquals ("a["+k+"] != b["+k+"]", a[k], b[k]);
	}
	
	/**
	 * Create a <code>TcpServer</code> and use a <code>Transceiver</code> as TCP
	 * client. Send one packet via TCP and verify that it arrives on the other
	 * side.
	 */
	public void testCase01 () {
		
		try {
			TcpServer server = new TcpServer (serverProps);
			server.setDtoProvider (dtoProvider);
			server.start();
			
			Transceiver client = new Transceiver (clientProps);
			
			client.send(packetOne);
			
			try { Thread.sleep(500); } catch (InterruptedException e) {;}
			
			assertEquals (1, listenerOne.counter);
			arrayCompare (packetOne.getPayload(), listenerOne.dto.toByteArray());
			
			server.terminate();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Create a <code>TcpServer</code> and use a <code>Transceiver</code> as TCP
	 * client. Ensure that the transceiver configuration is erroneous so, that a
	 * exception rises on construction time. Verify that a packet sent to the
	 * server will not arrive.
	 */
	public void testCase02 () {
		
		serverProps.setProperty (Transceiver.PROP_RECONNECTION_TIMEOUT,"10.34");
		
		try {
			TcpServer server = new TcpServer (serverProps);
			server.setDtoProvider (dtoProvider);
			server.start();
			
			Transceiver client = new Transceiver (clientProps);
			
			client.send(packetOne);
			
			try { Thread.sleep(500); } catch (InterruptedException e) {;}
			
			assertEquals (0, listenerOne.counter);
			server.terminate();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
}
