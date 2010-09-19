/*
 * @(#) TcpSocketServerTestCase.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * This test case verifies the implementation of the
 * <code>TcpSocketServer</code> class.
 * 
 * @author Clemens Krainer
 */
public class TcpSocketServerTestCase extends TestCase {

	private Properties serverProps;
	private Properties clientProps;
	
	private MyWorkerThread currentWorkerThread;
	
	private boolean workerShouldRead;
	private boolean workerShouldCrash;
	
	public void setUp () {
		serverProps = new Properties ();
		serverProps.setProperty("server.port", "31337");
		
		clientProps = new Properties ();
		clientProps.setProperty("host", "localhost");
		clientProps.setProperty("port", serverProps.getProperty("server.port"));
		
		workerShouldRead = true;
		workerShouldCrash = false;
	}
	
	public void tearDown () {
		try { Thread.sleep(500); } catch (InterruptedException e) {}
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
	 * Connect to a socket send some bytes and verify that the client receives
	 * the bytes.
	 */
	public void testCase01 () {
		
		byte[] msg = new byte[] {0,1,2,3,4,5,6,7,8,9,64,65,66,67,68,69,70};
		
		try {
			MyServer server = new MyServer (serverProps);
			server.start();
			Thread.yield();
			
			String host = clientProps.getProperty("host");
			int port = Integer.parseInt (clientProps.getProperty ("port"));
			TcpSocket socket = new TcpSocket (host, port);
			OutputStream out = socket.getOutputStream();
			out.write (msg);
			out.flush();
			try { Thread.sleep(200); } catch (InterruptedException e) {}
			socket.close();
			server.terminate();
			
			arrayCompare (msg, currentWorkerThread.outputStream.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Again, connect to a socket send some bytes and verify that the client
	 * receives the bytes.
	 */
	public void testCase02 () {
		
		byte[] msg = new byte[] {0,1,2,3,4,5,6,7,8,9,64,65,66,67,68,69,70};
		
		try {
			MyServer server = new MyServer (serverProps);
			server.start();
			Thread.yield();
			
			TcpSocket socket = new TcpSocket (clientProps);
			OutputStream out = socket.getOutputStream();
			out.write (msg);
			out.flush();
			try { Thread.sleep(200); } catch (InterruptedException e) {}
			socket.close();
			server.terminate();
			
			arrayCompare (msg, currentWorkerThread.outputStream.toByteArray());
			
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Connect to a socket send some bytes from the client and verify that the
	 * server receives the bytes.
	 */
	public void testCase03 () {
		
		byte[] recvMsg = new byte[] {5,6,7,8,9,64,65,66,67,0,1,2,3,4,68,69,70};
		
		try {
			workerShouldRead = false;
			workerShouldCrash = true;
			
//			ServerSocket.setSocketFactory(new MySocketImplFactory());
			
			MyServer server = new MyServer (serverProps);
			server.start();
			try { Thread.sleep (200); } catch (InterruptedException e) {}
			
			TcpSocket socket = new TcpSocket (clientProps);
			try { Thread.sleep (200); } catch (InterruptedException e) {}
			InputStream in = socket.getInputStream();
			
			currentWorkerThread.inputStream = new ByteArrayInputStream (recvMsg);
			int ch;
			ByteArrayOutputStream outB = new ByteArrayOutputStream ();
			currentWorkerThread.interrupt();
			socket.close();
			
			while ( (ch = in.read ()) >= 0)
				outB.write(ch);
			
			server.terminate();
			try { Thread.sleep (200); } catch (InterruptedException e) {}
			socket.close();
			
			arrayCompare (recvMsg, outB.toByteArray());
			
		} catch (IOException e) {
			if (e instanceof SocketException && "Socket closed".equals (e.getMessage ()))
				;
			else {
				e.printStackTrace();
				fail ();
			}
		}
	}
	
	/**
	 * Set the server port in the properties to an empty string. Verify that the
	 * constructor throws an <code>IOException</code>.
	 */
	public void testCase04 () {
		serverProps.setProperty("server.port", "");
		try {
			MyServer server = new MyServer (serverProps);
			assertNull (server);
		} catch (IOException e) {
			assertEquals ("Property server.port is not set.", e.getMessage());
		}
	}

	/**
	 * No setting of the server port in the properties at all. Verify that the
	 * constructor throws an <code>IOException</code>.
	 */
	public void testCase05 () {
		serverProps.remove ("server.port");
		try {
			MyServer server = new MyServer (serverProps);
			assertNull (server);
		} catch (IOException e) {
			assertEquals ("Property server.port is not set.", e.getMessage());
		}
	}
	
	/**
	 * This class implements the <code>startWorkerThread()</code> method of the
	 * <code>TcpSocketServer</code> for this unit tests.
	 * 
	 * @author Clemens Krainer
	 */
	private class MyServer extends TcpSocketServer {

		/**
		 * Construct the implementation for this unit tests.
		 * 
		 * @param props the <code>Properties</code> to be used.
		 * @throws IOException thrown in case of I/O errors, e.g. an already used port.
		 */
		public MyServer(Properties props) throws IOException {
			super (props);
		}

		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.io.TcpSocketServer#startWorkerThread(at.uni_salzburg.cs.ckgroup.io.IConnection)
		 */
		public void startWorkerThread (IConnection connection) {
			MyWorkerThread thread = new MyWorkerThread (connection);
			thread.start();
		}
	}
	
	/**
	 * This class implements a socket server worker thread that inherits an
	 * established connection. It is this class that sends the GPS data to a
	 * TCP/IP client.
	 */
	private class MyWorkerThread extends Thread {
	   	
        /**
         * The inherited socket with an established connection.
         */
        private IConnection connection;
        
        /**
         * The buffer for incoming data.
         */
        public ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
        
        /**
         * The buffer for outgoing data.
         */
        public ByteArrayInputStream inputStream = null;

        /**
		 * Constructor.
		 * 
		 * @param connection a socket containing an already established connection to a client.
		 */
    	public MyWorkerThread (IConnection connection) {
    		this.connection = connection;
    		currentWorkerThread = this;
    	}
    	
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run ()
        {
        	try {
            	int ch;
            	
    			if (workerShouldRead) {
    				
    				InputStream socketIn = connection.getInputStream ();
    				
	        		while ((ch = socketIn.read()) >= 0) {
		        		if (workerShouldCrash)
		        			return;
	        			outputStream.write(ch);
	        		}
	        		
    			} else {
    				
    				OutputStream socketOut = connection.getOutputStream();
    				
    				int counter = 20;
    				while (inputStream == null && ++counter > 0)
    					try { Thread.sleep (100); } catch (InterruptedException e) {}
    					
    				while ((ch = inputStream.read ()) >= 0) {
    					if (workerShouldCrash)
    	        			return;
    					socketOut.write(ch);
    				}
				}
    			
            } catch (IOException e) {
            	if (e instanceof SocketException && e.getMessage().equals("Broken pipe"))
            		System.out.println ("MyWorkerThread: Client disconnected, name=" + this.getName());
            	else
            		e.printStackTrace();            		
            }
        	
            try {
              connection.close ();
            } catch (IOException e) {}
        }
	}
	
//	private class MySocketImplFactory implements SocketImplFactory {
//
//		public SocketImpl createSocketImpl() {
//			System.out.println ("MySocketImplFactory.createSocketImpl");
//			return new MySocketImpl ();
//		}
//	}
//	
//	private class MySocketImpl extends SocketImpl {
//
//		protected void accept(SocketImpl s) throws IOException {
//			System.out.println ("MySocketImpl.accept");
//		}
//
//		protected int available() throws IOException {
//			System.out.println ("MySocketImpl.available");
//			return 0;
//		}
//
//		protected void bind(InetAddress host, int port) throws IOException {
//			System.out.println ("MySocketImpl.bind");
//		}
//
//		protected void close() throws IOException {
//			System.out.println ("MySocketImpl.close");
//		}
//
//		protected void connect(String host, int port) throws IOException {
//			System.out.println ("MySocketImpl.connect");
//		}
//
//		protected void connect(InetAddress address, int port)
//				throws IOException {
//			System.out.println ("MySocketImpl.connect");
//		}
//
//		protected void connect(SocketAddress address, int timeout)
//				throws IOException {
//			System.out.println ("MySocketImpl.connect");
//		}
//
//		protected void create(boolean stream) throws IOException {
//			System.out.println ("MySocketImpl.create");
//		}
//
//		protected InputStream getInputStream() throws IOException {
//			System.out.println ("MySocketImpl.getInputStream");
//			return null;
//		}
//
//		protected OutputStream getOutputStream() throws IOException {
//			System.out.println ("MySocketImpl.getOutputStream");
//			return null;
//		}
//
//		protected void listen(int backlog) throws IOException {
//			System.out.println ("MySocketImpl.listen");
//		}
//
//		protected void sendUrgentData(int data) throws IOException {
//			System.out.println ("MySocketImpl.sendUrgentData");
//		}
//
//		public Object getOption(int optID) throws SocketException {
//			System.out.println ("MySocketImpl.getOption");
//			return null;
//		}
//
//		public void setOption(int optID, Object value) throws SocketException {
//			System.out.println ("MySocketImpl.setOption");
//		}
//	}
}
