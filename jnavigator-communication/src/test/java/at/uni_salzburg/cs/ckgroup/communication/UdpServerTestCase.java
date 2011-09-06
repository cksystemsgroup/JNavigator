package at.uni_salzburg.cs.ckgroup.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.io.UdpSocketServer;

public class UdpServerTestCase extends TestCase {
	
	private IDataTransferObjectProvider dtoProvider;
	private Properties serverProps;
	private Properties clientProps;
	private Packet packetOne;
	private MockListenerOne listenerOne;
	
	
	public void setUp () throws Exception {
		super.setUp();
		
		serverProps = new Properties ();
		serverProps.setProperty (UdpSocketServer.PROP_SERVER_PORT, "5469");
		serverProps.setProperty (UdpSocketServer.PROP_TIMEOUT, "1000");
		
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
		clientProps.setProperty ("host", "127.0.0.1");
		clientProps.setProperty ("port", "5469");
	}

	void arrayCompare (byte[] a, byte[] b) {
		assertEquals ("array length", a.length, b.length);
		
		for (int k=0; k < a.length; k++)
			assertEquals ("a["+k+"] != b["+k+"]", a[k], b[k]);
	}
	
	
	/**
	 * Create a <code>UdpServer</code> and use a <code>Transceiver</code> as UDP
	 * client. Send one packet via UDP and verify that it arrives on the other
	 * side.
	 */
	public void testCase01 () {
		
		try {
			UdpServer server = new UdpServer (serverProps);
			server.setDtoProvider (dtoProvider);
			ServerReceiver serverReceiver = new ServerReceiver (server);
			serverReceiver.start();
			try { Thread.sleep(100); } catch (InterruptedException e) {;}
			
			DatagramTransceiver client = new DatagramTransceiver (clientProps);
			client.send(packetOne);
			
			try { Thread.sleep(500); } catch (InterruptedException e) {;}
			
			assertEquals (1, listenerOne.counter);
			arrayCompare (packetOne.getPayload(), listenerOne.dto.toByteArray());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
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
