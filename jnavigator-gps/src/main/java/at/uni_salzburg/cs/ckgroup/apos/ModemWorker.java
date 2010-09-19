/*
 * @(#) ModemWorker.java
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
package at.uni_salzburg.cs.ckgroup.apos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;

/**
 * This class implements the functionality needed to handle modems. For
 * connecting to the modem and for executing commands this implementation
 * employs an additional thread, implemented as inner class
 * <code>ModemReader</code> for reading bytes from the modem.
 * 
 * @author Clemens Krainer
 */
public class ModemWorker
{
	/**
	 * The properties of the underlying modem connection.
	 */
	private Properties connectionProperties = null;
	
	/**
	 * The connection to the modem.
	 * 
	 * @uml.property name="connection"
	 */
	private IConnection connection = null;
	
	/**
	 * Construct a ModemWorker by properties.
	 * 
	 * @param connectionProperties the properties specifying the modem connection.
	 */
	public ModemWorker (Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}
	
	/**
	 * Construct a ModemWorker using an already established connection.
	 * 
	 * @param connection the already established connection.
	 */
	public ModemWorker (IConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Return the currently used modem connection.
	 * 
	 * @return the currently used modem connection.
	 * @uml.property name="connection"
	 */
	public IConnection getConnection () { 
		return connection;
	}
	
	/**
	 * Connect to the modem and perform a basic function check by applying the
	 * <b>AT</b> modem command
	 * 
	 * @param retries the number of retries for connecting to the modem
	 * @throws IOException thrown in case of errors
	 */
	public void connect (int retries) throws IOException {
		
		final byte[] MSG_AT_QUERY = "at\r\n".getBytes ();
				
		InputStream in;
		OutputStream out;
		ModemReader reader = null;
		
		if (retries <= 0)
			retries = 1;
		
		while (retries-- > 0) {

			if (connectionProperties != null) {
				try {
					connection = ObjectFactory.getInstance ().instantiateIConnection (null, connectionProperties);
				} catch (InstantiationException e)
				{
					e.printStackTrace ();
					throw new IOException (e.getMessage () + "\n" + e.toString ());
				}
			}
			
			in = connection.getInputStream ();
			out = connection.getOutputStream ();
			reader = new ModemReader (in);
			reader.start ();
			try { Thread.sleep (100); } catch (InterruptedException e1) { }
			System.out.println ("Check if GSM Modem is ready.");
			out.write (MSG_AT_QUERY);
			out.flush();

			int waitCounter = 20;
			while (reader.isRunning ()) {
				try { Thread.sleep (100); } catch (InterruptedException e1) { }
				if (waitCounter-- <= 0) {
					reader.terminate ();
					connection.close ();
					connection = null;
				}
				try { Thread.sleep (200); } catch (InterruptedException e) { }
			}
								
			if (connection != null && reader.okReveiced ())
				break;

			System.out.println ("GSM Modem is not ready yet. Reconnecting.");
		}
		
		if (connection == null)
			throw new IOException ("Can not connect to GSM modem.");
		
		System.out.println ("GSM Modem is ready.");
	}
	
	/**
	 * Execute a modem command.
	 * 
	 * @param command the command to be executed.
	 * @param resultStrings expected result messages. This method already
	 *        recognizes the messages <code>OK</code>, <code>ERROR</code>
	 *        and <code>NO CARRIER</code>.
	 * @param retries the number of retries for reconnecting to the modem.
	 * @return the first element of this array contains the bytes received from
	 *         the modem and the second element contains the recognized result
	 *         message.
	 * @throws IOException thrown on errors
	 */
	public byte[][] executeCommand (byte[] command, byte[][] resultStrings, int retries) throws IOException {
		
		byte[][] result = null;

		if (retries <= 0)
			retries = 1;
		
		while (retries-- > 0) {
			if (connection == null)
				connect (1);
			
			if (connection == null)
				continue;
			
			InputStream in = connection.getInputStream ();
			OutputStream out = connection.getOutputStream ();
			ModemReader reader = new ModemReader (in);
			reader.setResultStrings (resultStrings);
			reader.start ();
			try { Thread.sleep (100); } catch (InterruptedException e1) { }
			System.out.println ("Send command to GSM Modem.");
			out.write (command);
			out.flush();

			int waitCounter = 100;
			while (reader.isRunning ()) {
				try { Thread.sleep (100); } catch (InterruptedException e1) { }
				if (waitCounter-- <= 0) {
					reader.terminate ();
					connection.close ();
					connection = null;
					waitCounter = -1;
				}
				try { Thread.sleep (1000); } catch (InterruptedException e) { }
				System.out.println ("GSM Modem has not finished yet.");
			}
			
			if (connection != null)
				System.out.println ("GSM Modem is ready.");
			else
				System.out.println ("GSM Modem is not ready!");
	
			
			result = new byte[2][];
			result[0] = reader.getBuffer ();
			result[1] = reader.getResultString ();
			
			if (reader.busyReveiced ())
				try { Thread.sleep (10000); } catch (InterruptedException e) { }
				
			if (reader.delayedReveiced ()) {
				++retries;
				try { Thread.sleep (10000); } catch (InterruptedException e) { }
			}
		}
		
		return  result;
	}

	
	/**
	 * The subclass ModemReader implements a separate Thread for reading from
	 * the modem connection.
	 * 
	 * @author Clemens Krainer
	 */
	private class ModemReader extends Thread {
		
		private byte[][] defaultResultStrings = {
				"OK\r\n".getBytes (), "ERROR\r\n".getBytes (), "NO CARRIER".getBytes (),
				"BUSY\r\n".getBytes (), "DELAYED\r\n".getBytes () };
		
		/**
		 * Indices of specific result strings.
		 */
		private static final int RESULT_INDEX_OK = 0;
		private static final int RESULT_INDEX_BUSY = 3;
		private static final int RESULT_INDEX_DELAYED = 4;
		
		/**
		 * The complete array of result strings. It contains the default result
		 * strings and user defined result strings.
		 */
		private byte[][] resultStrings = null;
		
		/**
		 * The current result string indices. This array indicates for each
		 * result string the currently received length of fitting bytes. Once a
		 * index reaches the length of its corresponding result string this
		 * string has been recognized as an input and the detection flag in
		 * array resultDetected is set.
		 */
		private int[] resultIndexes;

		/**
		 * The current result string recognized flags. See <code>resultIndexes</code>.
		 */
		private boolean[] resultDetected;
		
		/**
		 * The modem InputStream
		 */
		private InputStream inputStream;
		
		/**
		 * Byte buffer for received bytes. 
		 */
		private byte[] buf = new byte[1024];
		
		/**
		 * 
		 */
		private int index;
		
		/**
		 * This variable indicates that the ModemReader thread is running.
		 * @uml.property  name="running"
		 */
		private boolean running;
		
		/**
		 * Lock object for variable <code>running</code>. 
		 */
		private Object runningLock = new Object();
		
		/**
		 * Construct a ModemReader Thread.
		 * 
		 * @param inputStream the InputStream of the modem connection. 
		 */
		public ModemReader (InputStream inputStream) {
			this.inputStream = inputStream;
			setResultStrings (null);
		}
		
		/**
		 * Join the default result strings with the additional result strings
		 * for usage in the <code>checkForResultStrings()</code> method.
		 * 
		 * @param extraResultStrings the additional result strings.
		 * @uml.property name="resultStrings"
		 */
		public void setResultStrings (byte[][] extraResultStrings) {
			
			int extraLength = 0;
			
			if (extraResultStrings != null)
				extraLength = extraResultStrings.length;
			
			resultStrings = new byte[defaultResultStrings.length + extraLength][];
			
			int k;
			for (k=0; k < defaultResultStrings.length; k++)
				resultStrings[k] = defaultResultStrings[k];
			
			for (k=0; k < extraLength; k++)
				resultStrings[defaultResultStrings.length+k] = extraResultStrings[k];
			
			resultIndexes = new int[resultStrings.length];
			resultDetected = new boolean[resultStrings.length];
			
			for (k=0; k < resultStrings.length; k++) {
				resultIndexes[k] = -1;
				resultDetected[k] = false;
			}
			
			index = -1;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run () {
			
			if (resultStrings == null)
				setResultStrings (null);

			try
			{
				int ch;
				running = true;
				while (running && (ch = inputStream.read ()) >= 0) {
					buf[++index] = (byte)ch;
					System.out.print ((char)ch);
					running = checkForResultStrings ((byte)ch);
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			running = false;
			System.out.println ("ModemReader.run: terminated.");
		}
		
		/**
		 * Terminate the ModemReader thread.
		 */
		public void terminate () {
			
			synchronized (runningLock) {
				if (running == false)
					return;
				
				running = false;
			}
			try {
				inputStream.close ();
			} catch (IOException e) {
				System.out.println ("ModemWorker.teminate(): IOException on close().");
			}
		}
				
		/**
		 * Determine if a <code>OK</code> response message has arrived.
		 * 
		 * @return true if the <code>OK</code> response message has arrived,
		 *         false otherwise.
		 */
		public boolean okReveiced () {
			return resultDetected[RESULT_INDEX_OK];
		}
		
		/**
		 * Determine if a <code>BUSY</code> response message has arrived.
		 * 
		 * @return true if the <code>BUSY</code> response message has arrived,
		 *         false otherwise.
		 */
		public boolean busyReveiced () {
			return resultDetected[RESULT_INDEX_BUSY];
		}
		
		/**
		 * Determine if a <code>BUSY</code> response message has arrived.
		 * 
		 * @return true if the <code>BUSY</code> response message has arrived,
		 *         false otherwise.
		 */
		public boolean delayedReveiced () {
			return resultDetected[RESULT_INDEX_DELAYED];
		}
	
		/**
		 * Get the result string that terminated the last command executed.
		 * 
		 * @return the result string.
		 */
		public byte[] getResultString () {
			
			for (int k=0; k < resultStrings.length; k++)
				if (resultDetected[k])
					return resultStrings[k];

			return null;
		}
		
		/**
		 * Determine if the ModemReader Thread is currently running.
		 * 
		 * @return true if the ModemReader Thread is currently running.
		 * @uml.property name="running"
		 */
		public boolean isRunning () {
			synchronized (runningLock) {
				return running;
			}
		}
		
		/**
		 * This method implements an automaton that recognises the receipt of a
		 * response message.
		 * 
		 * @param ch the currently read character from the modem connection.
		 * @return true if no response message has arrived yet.
		 */
		private boolean checkForResultStrings (byte ch) {
			boolean noneFound = true;
			
			for (int k=0; k < resultStrings.length; k++) {
				if (!resultDetected[k]) {
					++resultIndexes[k];
					if (resultStrings[k][resultIndexes[k]] == ch) {
						if (resultIndexes[k]+1 == resultStrings[k].length) {
							resultDetected[k] = true;
							noneFound = false;
						}
					} else
						resultIndexes[k] = -1;
				}
			}
			
			return noneFound;
		}

		/**
		 * Return the buffer of received characters from the modem connection.
		 * 
		 * @return the buffer of received characters from the modem connection.
		 */
		public byte[] getBuffer () {
			
			if (index < 0)
				return null;

			int len = index;
			byte[] b = new byte[len+1];
			for (int k=0; k <= len; k++)
				b[k] = buf[k];

			return b;
		}

	}
}
