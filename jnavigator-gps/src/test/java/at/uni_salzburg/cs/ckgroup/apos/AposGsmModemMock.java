/*
 * @(#) AposGsmModemMock.java
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

/**
 * The mock implementation of the APOS-GSM service.
 * 
 * @author   Clemens Krainer
 */
public class AposGsmModemMock implements IConnection
{
	/**
	 * The last instantiation of this class. The AposGsmTestCase class needs
	 * this for verifying the AposGsm class.
	 */
	public static AposGsmModemMock currentInstance = null;
	
	/**
	 * The InputStream of this APOS-GSM mock service.
	 */
	private ModemInputStream modemInputStream;
	
	/**
	 * The modemInputStream.read() method will block until a command has arrived
	 * on the modemOutputStream.
	 */
	private boolean waitForNextCommand = false;
	
	/**
	 * The OutputStream of this APOS-GSM mock service. 
	 */
	private ModemOutputStream modemOutputStream;
	
	/**
	 * Construct an APOS-GSM mock service.
	 * 
	 * @param input the bytes to be returned by the InputStream
	 */
	public AposGsmModemMock (byte[] input) {
		modemInputStream = new ModemInputStream (input);
		modemOutputStream = new ModemOutputStream ();
		currentInstance = this;
	}
	
	/**
	 * Construct an APOS-GSM mock service.
	 * 
	 * @param props the property set containing a property <b>input</b> that
	 *        provides the bytes to be returned by the InputStream
	 */
	public AposGsmModemMock (Properties props) {
		modemInputStream = new ModemInputStream (props.getProperty ("input").getBytes ());
		modemOutputStream = new ModemOutputStream ();
		currentInstance = this;
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
		return modemInputStream;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream () throws IOException {
		return modemOutputStream;
	}
	
	/**
	 * Return the collected bytes from the OutputStream
	 * 
	 * @return the bytes from the OutputStream
	 */
	public byte[] getOutputStreamBuffer () {
		return modemOutputStream.getBytes ();
	}

	/**
	 * This class implements the InputStream for the APOS-GSM mock service.
	 * Every byte this InputStream returns by means of the read() method has to
	 * be provided at construction time.
	 */
	protected class ModemInputStream extends InputStream {
		
		private int readPtr;
		private byte[] bytes;
		
		/**
		 * The modem response strings to watch at.
		 */
		private byte[][] responseStrings = { "OK\r\n".getBytes (), "ERROR\r\n".getBytes (), "NO CARRIER\r\n".getBytes (), "RING\r\n".getBytes () };
		private int[] responseIndexes = { -1, -1, -1, -1 };

		/**
		 * Construct the InputStream for the APOS-GSM mock service.
		 * 
		 * @param input the bytes the method read() will return.
		 */
		public ModemInputStream (byte[] input) {
			readPtr = 0;
			bytes = input;
		}
		
		/* (non-Javadoc)
		 * @see java.io.InputStream#read()
		 */
		public int read () throws IOException {
			if (readPtr >= bytes.length)
				return -1;
			
			while (waitForNextCommand) {
				try { Thread.sleep(100); } catch (InterruptedException e) { }
//				Thread.yield ();
			}
			
			int ch = bytes[readPtr++];
			waitForNextCommand = checkForModemResponseStrings (ch); 
			return ch;
		}
		
		/**
		 * Check for any of the modem response code defined in <code>responseStrings</code>.
		 * 
		 * @param ch the currently read character from the modem connection.
		 * @return true if one of the modem response codes has been detected.
		 */
		private boolean checkForModemResponseStrings (int ch) {
			
			boolean found = false;
			
			for (int k=0; k < responseStrings.length; k++) {
				++responseIndexes[k];
				if (responseStrings[k][responseIndexes[k]] == ch) {
					if (responseIndexes[k]+1 == responseStrings[k].length) {
						found = true;
						responseIndexes[k] = -1;
						System.out.print ("Response code detected: ");
						try { System.out.write (responseStrings[k]); System.out.flush (); } catch (IOException e) {}
					}
				} else
					responseIndexes[k] = -1;
			}
			
			return found;
		}
		
	}
	
	/**
	 * This class implements the OutputStream for the APOS-GSM mock service. It
	 * collects the bytes written to it via the write() method.
	 */
	protected class ModemOutputStream extends OutputStream {
		
		private int writePtr;
		private byte[] buf;
		
		/**
		 * Construct the OutputStream for the APOS-GSM mock service.
		 */
		public ModemOutputStream () {
			writePtr = 0;
			buf = new byte[10240];
		}
		
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write (int b) throws IOException {
			buf[writePtr++] = (byte)b;
			waitForNextCommand = false;
		}
		
		/**
		 * Return the bytes collected by this OutputStream so far.
		 * 
		 * @return the collected byte 
		 */
		public byte[] getBytes () {
			byte[] b = new byte[writePtr];
			for (int k=0; k < writePtr; k++)
				b[k] = buf[k];
			return b;
		}
	}
}
