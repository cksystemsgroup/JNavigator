/*
 * @(#) UdpSocketServerTestCase.java
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
package at.uni_salzburg.cs.ckgroup.io;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * This test case verifies the implementation of the
 * <code>UdpSocketServer</code> class.
 * 
 * @author Clemens Krainer
 */
public class UdpSocketServerTestCase extends TestCase {
	
	private Properties serverProps;
	private Properties clientProps;
	
//	private MyWorkerThread currentWorkerThread;
//	private boolean workerShouldRead;
//	private boolean workerShouldCrash;
	
	public void setUp () {
		serverProps = new Properties ();
		serverProps.setProperty("server.port", "7337");
		serverProps.setProperty("server.timeout", "1000");
		
		clientProps = new Properties ();
		clientProps.setProperty("host", "localhost");
		clientProps.setProperty("port", serverProps.getProperty("server.port"));
		
//		workerShouldRead = true;
//		workerShouldCrash = false;
	}
	
//	public void tearDown () {
//		try { Thread.sleep(500); } catch (InterruptedException e) {}
//	}
	
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
	 * Test for exception if property server.port is not set
	 */
	public void testCase01 () {
		
		serverProps.remove ("server.port");
		try {
			UdpSocketServer server = new UdpSocketServer(serverProps);
			assertNull (server);
		} catch (IOException e) {
			assertEquals ("Property server.port is not set.",e.getMessage());
		}
		
		serverProps.setProperty ("server.port", "");
		try {
			UdpSocketServer server = new UdpSocketServer(serverProps);
			assertNull (server);
		} catch (IOException e) {
			assertEquals ("Property server.port is not set.",e.getMessage());
		}
	}
	
	/**
	 * Test for exception if property server.timeout is not set
	 */
	public void testCase02 () {
		
		serverProps.remove("server.timeout");
		try {
			UdpSocketServer server = new UdpSocketServer(serverProps);
			assertNull (server);
		} catch (IOException e) {
			assertEquals ("Property server.timeout is not set.",e.getMessage());
		}
		
		serverProps.setProperty("server.timeout","");
		try {
			UdpSocketServer server = new UdpSocketServer(serverProps);
			assertNull (server);
		} catch (IOException e) {
			assertEquals ("Property server.timeout is not set.",e.getMessage());
		}
	}
	
	/**
	 * Send a message with no clients attached.
	 */
	public void testCase11 () {
		
		byte[] msg = new byte[] {0,1,2,3,4,5,6,7,8,9,64,65,66,67,68,69,70};

		try {
			UdpSocketServer server = new UdpSocketServer(serverProps);
			server.sendDatagram(msg, msg.length);
			server.close();
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Send messages with one client attached.
	 */
	public void testCase12 () {
		
		byte[] msg1 = new byte[] {0,1,2,3,4,5,6,7,8,9,64,65,66,67,68,69,70};
		byte[] msg2 = new byte[] {99,88,77,66,55,44,33,22,11,0,1,2,3,4,5,6};

		UdpSocketServer server = null;
		UdpClient client = null;
		try {
			server = new UdpSocketServer(serverProps);
			client = new UdpClient(clientProps);
			
			ServerReceiver serverReceiver = new ServerReceiver (server);
			serverReceiver.start();
			client.send(msg2);
			int counter = 0;
			while (serverReceiver.buffer == null && counter++ < 20) try { Thread.sleep(100); } catch (InterruptedException e) {}
			arrayCompare (msg2, serverReceiver.buffer);
			
			ClientReceiver clientReceiver = new ClientReceiver (client);
			clientReceiver.start();
			server.sendDatagram(msg1, msg1.length);
			counter = 0;
			while (clientReceiver.buffer == null && counter++ < 20) try { Thread.sleep(100); } catch (InterruptedException e) {}
			arrayCompare (msg1, clientReceiver.buffer);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		} finally {
			if (server != null)
				server.close();
			if (client != null)
				client.close();
		}
	}
	
	/**
	 * Send a message with one client attached and having a timeout.
	 */
	public void testCase13 () {
		serverProps.setProperty("server.timeout", "10");
		
		byte[] msg1 = new byte[] {0,1,2,3,4,5,6,7,8,9,64,65,66,67,68,69,70};
		byte[] msg2 = new byte[] {99,88,77,66,55,44,33,22,11,0,1,2,3,4,5,6};

		UdpSocketServer server = null;
		UdpClient client = null;
	
		try {
			server = new UdpSocketServer(serverProps);
			client = new UdpClient(clientProps);
			
			ServerReceiver serverReceiver = new ServerReceiver (server);
			serverReceiver.start();
			client.send(msg2);
			int counter = 0;
			while (serverReceiver.buffer == null && counter++ < 20) try { Thread.sleep(100); } catch (InterruptedException e) {}
			arrayCompare (msg2, serverReceiver.buffer);
			
			ClientReceiver clientReceiver = new ClientReceiver (client);
			clientReceiver.start();
			try { Thread.sleep(20); } catch (InterruptedException e) {}
			server.sendDatagram(msg1, msg1.length);
			counter = 0;
			while (clientReceiver.buffer == null && counter++ < 10) try { Thread.sleep(50); } catch (InterruptedException e) {}
			assertNull (clientReceiver.buffer);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		} finally {
			if (server != null)
				server.close();
			if (client != null)
				client.close();
		}
	}
	
	private class UdpClient {
		private InetSocketAddress serverAddress;
		private DatagramSocket clientSocket;

		public UdpClient (Properties props) throws SocketException {
			String host = props.getProperty ("host");
			int port = Integer.parseInt (props.getProperty ("port"));
			serverAddress = new InetSocketAddress(host, port);
			clientSocket = new DatagramSocket();
		}
		
		public void send (byte[] packet) throws IOException {
			DatagramPacket datagramPacket = new DatagramPacket(packet, packet.length);
			datagramPacket.setSocketAddress(serverAddress);
			clientSocket.send(datagramPacket);
		}
		
		public byte[] receive() throws IOException {
			byte[] buffer = new byte[64];
			DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
			clientSocket.receive(datagramPacket);
			return Arrays.copyOf (buffer, datagramPacket.getLength());
		}
		
		public void close () {
			clientSocket.close();
		}
	}
	
	private class ServerReceiver extends Thread {
		
		private UdpSocketServer server;
		public byte[] buffer;

		public ServerReceiver (UdpSocketServer server) {
			this.server = server;
		}
		
		public void run () {
			buffer = null;
			try {
				buffer = server.receiveDatagram();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ClientReceiver extends Thread {
		
		private UdpClient client;
		public byte[] buffer;

		public ClientReceiver (UdpClient client) {
			this.client = client;
		}
		
		public void run () {
			buffer = null;
			try {
				buffer = client.receive();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
