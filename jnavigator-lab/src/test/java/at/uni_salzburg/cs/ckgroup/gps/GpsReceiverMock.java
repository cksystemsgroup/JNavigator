/*
 * @(#) GpsReceiverMock.java
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
package at.uni_salzburg.cs.ckgroup.gps;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

/**
 * This class implements a simple GPS receiver simulator.
 * 
 * @author   Clemens Krainer
 */
public class GpsReceiverMock implements IConnection
{
	/**
	 * The InputStream of this APOS-GSM mock service.
	 */
	private GpsReceiverMockInputStream receiverInputStream;
	
	/**
	 * The OutputStream of this APOS-GSM mock service. 
	 */
	private GpsReceiverMockOutputStream receiverOutputStream;
	
	/**
	 * Construct an APOS-GSM mock service.
	 * 
	 * @param messages the messages to be returned by the InputStream
	 * @param cycleTime the cycle in milliseconds the messages should be sent
	 */
	public GpsReceiverMock (String[] messages, long cycleTime) {
		receiverInputStream = new GpsReceiverMockInputStream (messages, cycleTime);
		receiverOutputStream = new GpsReceiverMockOutputStream ();
	}
	
	/**
	 * Construct an APOS-GSM mock service.
	 * 
	 * @param props the properties containing the messages and the cycle time in
	 *            milliseconds the messages should be sent.
	 */
	public GpsReceiverMock (Properties props) {
		int amount = Integer.parseInt (props.getProperty ("amount","0"));
		long cycleTime = Integer.parseInt (props.getProperty ("cycleTime","1"));
		
		String[] messages = new String[amount];
		for (int k=0; k < amount; k++) {
			String key = "message." + k;
			String val = props.getProperty (key);
			if (val != null)
				val = val.replaceAll("\\r", "\r").replaceAll("\\n", "\n");
			messages[k] = val;
		}
		
		receiverInputStream = new GpsReceiverMockInputStream (messages, cycleTime);
		receiverOutputStream = new GpsReceiverMockOutputStream ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close () throws IOException {
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream () throws IOException {
		return receiverInputStream;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream () throws IOException {
		return receiverOutputStream;
	}

	/**
	 * Pretend that there will be an IOException while reading the InputStream.
	 */
	public void pretendInputStreamIOException () {
		receiverInputStream.pretendIOException ();
	}
	
	/**
	 * Pretend that there will be an IOException while writing the OutputStream.
	 */
	public void pretendOutputStreamIOException () {
		receiverOutputStream.pretendIOException ();
	}

	/**
	 * This class implements the InputStream for the APOS-GSM mock service.
	 * Every byte this InputStream returns by means of the read() method has to
	 * be provided at construction time.
	 */
	protected class GpsReceiverMockInputStream extends InputStream {
		
		private int next = 0;
		private int readPtr = 0;
		private int msgPtr = 0;
		
		private String[] messages;
		private long cycleTime;
		private boolean throwIOException = false;
		
		/**
		 * Construct an GpsReceiverMockInputStream.
		 * 
		 * @param messages the messages to be returned by the
		 *        GpsReceiverMockInputStream
		 * @param cycleTime the cycle in milli seconds the messages should be
		 *        sent
		 */
		public GpsReceiverMockInputStream (String[] messages, long cycleTime) {
			this.messages = messages;
			this.cycleTime = cycleTime;
		}
				
		/**
		 * A cheap 16 bit random number generator.
		 * 
		 * @return a random number
		 */
		private int rand() {
	        next = next * 1103515245 + 12345;
	        return (int)(next / 65536) % 32768;
		}

		/* (non-Javadoc)
		 * @see java.io.InputStream#read()
		 */
		public int read () throws IOException {
			
			if (throwIOException) {
				throwIOException = false;
				throw new IOException ("fake IOException.");
			}

			if (readPtr >= messages[msgPtr].getBytes ().length) {
				readPtr = 0;
				msgPtr = rand() % messages.length;
				while (msgPtr < 0) msgPtr += messages.length;
				try { Thread.sleep (cycleTime); } catch (InterruptedException e) { }
			}

//			System.out.println ("GpsReceiverMockInputStream.read: msgPtr="+msgPtr+", readPtr="+readPtr);
			return messages[msgPtr].getBytes ()[readPtr++];
		}
		
		/**
		 * Pretend that there will be an IOException while reading the
		 * InputStream.
		 */
		public void pretendIOException () {
			throwIOException = true;
		}
	}
	
	/**
	 * This class implements the OutputStream for the GPS mock receiver.
	 */
	protected class GpsReceiverMockOutputStream extends OutputStream {

		private boolean throwIOException = false;
		
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write (int b) throws IOException {

			if (throwIOException) {
				throwIOException = false;
				throw new IOException ("fake IOException.");
			}
		}

		/**
		 * Pretend that there will be an IOException while reading the
		 * OutputStream.
		 */
		public void pretendIOException () {
			throwIOException = true;
		}
	}
}
