/*
 * @(#) AposNtrip.java
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
import java.net.URL;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;

import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.io.TcpSocket;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;

/**
 * This class implements the APOS-NTRIP service for RTCM SC-104 correction
 * messages as an InputStream. It's an automaton that waits for a valid position
 * message (NMEA GGA) from the GPS receiver and then connects to the APOS-NTRIP
 * service. Until the APOS-NTRIP service sends valid RTCM messages, a read() call
 * is blocked. This class detects connection failures and reconnects to the
 * APOS-NTRIP service automatically.
 *
 * @author Clemens Krainer
 * @uml.dependency   supplier="at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message"
 */
public class AposNtrip extends InputStream implements Nmea0183MessageListener
{	
	/**
	 * The NTRIP user agent identification. 
	 */
	public static final String USER_AGENT_ID = "NTRIP JavaClient/1.0";
	
	/**
	 * Property key constants.
	 */
	public static final String PROP_CASTER = "caster";
	public static final String PROP_PORT = "port";
	public static final String PROP_USERNAME = "user";
	public static final String PROP_PASSWORD = "password";
	public static final String PROP_MOUNTPOINT = "mountpoint";
	
	/**
	 * The NMEA 0183 GGA message containing the rough current position. 
	 */
	private byte[] startPosition = null;
		
	/**
	 * The number of bytes read from the APOS RTCM SC-104 stream
	 * @uml.property  name="readCounter"
	 */
	private long readCounter = 0;
	
	/**
	 * The state constants for this automaton. 
	 */
	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_RECEIVING_RTCM_DATA = 1;
	
	/**
	 * The current state. 
	 */
	private int state = STATE_DISCONNECTED;
	
	/**
	 * The URL of the NTRIP caster
	 */
	private URL ntripCasterUrl;
	
	/**
	 * The basic authentication string for the APOS NTRIP service. 
	 */
	private byte[] basicAuthentication;
	
	/**
	 * The current connection to the NTRIP caster. 
	 */
	private IConnection ntripCasterConnection;
	
	/**
	 * The InputStream of the NTRIP connection.  
	 */
	private InputStream ntripInputStream;
	
	/**
	 * Construct an APOS NTRIP RTCM input stream.
	 * 
	 * @param ntripProperties the properties of the NTRIP connection to be used.
	 * @throws IOException thrown if host or port number is invalid.
	 */
	public AposNtrip (Properties ntripProperties) throws IOException {
		
		String caster = ntripProperties.getProperty (PROP_CASTER);
		int port = Integer.parseInt (ntripProperties.getProperty (PROP_PORT));
		String mountPoint = ntripProperties.getProperty (PROP_MOUNTPOINT);
		ntripCasterUrl = new URL ("http", caster, port, mountPoint == null ? "/" : "/" + mountPoint);
		
		String userName = ntripProperties.getProperty (PROP_USERNAME);
		String password = ntripProperties.getProperty (PROP_PASSWORD);
		
		byte[] encodedPassword = ( userName + ":" + password ).getBytes();
	    Base64 encoder = new Base64();
	    basicAuthentication = encoder.encode(encodedPassword);
	}
	
	/**
	 * Return the current connection state.
	 * 
	 * @return the current state
	 */
	public int getConnectionState () {
		return state;
	}
	
	/**
	 * Read one line from the network connection, including \r and \n.
	 * 
	 * @param b a byte array to put the read line in
	 * @return the number of read characters
	 * @throws IOException thrown in case of errors
	 */
	protected int readLine (byte[] b) throws IOException {
		
		int count = 0;
		int ch;
		
		while ( b.length >= count && (ch = ntripInputStream.read ()) >= 0) {
			b[count++] = (byte) ch;
			if (ch == '\n')
				break;
		}
		
		return count;
	}
	
	/**
	 * Compare two byte arrays up to a specific length for equality.
	 * 
	 * @param a the first array of bytes
	 * @param b the second array of bytes
	 * @param length the length of bytes to be checked
	 * @return true if the arrays are equal, false otherwise.
	 */
	static boolean areArraysEqual (byte[] a, byte[] b, int length) {
		
		if (a.length < length || b.length < length)
			return false;
		
		for (int k=0; k < length; k++)
			if (a[k] != b[k])
				return false;
		
		return true;
	}
	
	/**
	 * Connect to the APOS NTRIP service. Before connecting, this method waits
	 * for a valid start position in $GPGGA format. This is necessary, because
	 * APOS requires the rough current position before it sends RTCM SC-104
	 * correction data. If the remote station refuses to send correction data
	 * the connection is terminated and after a short wait period a reconnect is
	 * performed. *
	 * 
	 * @throws IOException thrown in case of IO errors.
	 */
	private void connect () throws IOException {
		state = STATE_DISCONNECTED;
		
		while (startPosition == null) {
			System.out.println ("AposNtrip.connect: waiting for a valid $GPGGA start position.");
			try { Thread.sleep (1000); } catch (InterruptedException e) {}
			if (startPosition != null)
				System.out.println ("AposNtrip.connect: $GPGGA start position is now available: " + startPosition);
		}
		
		if (ntripCasterConnection != null)
			ntripCasterConnection.close ();

		/*
		 * As convenient it would seem to be, we can not employ the
		 * HttpURLConnection implementation to perform this task. Although NTRIP
		 * is a HTTP like protocol HttpURLConnection does extra stuff that is
		 * not appreciated by the NTRIP servers.
		 */
		ntripCasterConnection = new TcpSocket (ntripCasterUrl.getHost (), ntripCasterUrl.getPort ());
		StringBuffer b = new StringBuffer ();
		b.append ("GET " + ntripCasterUrl.getPath () + " HTTP/1.0\r\n");
		b.append ("User-Agent: ").append (USER_AGENT_ID).append ("\r\n");
		b.append ("Authorization: Basic ").append (new String(basicAuthentication)).append ("\r\n\r\n");
		OutputStream out = ntripCasterConnection.getOutputStream ();
		out.write (b.toString ().getBytes ());
		out.write (startPosition);
		out.flush();
		
		ntripInputStream = ntripCasterConnection.getInputStream();
		
		final byte[] RESPONSE_OK = "ICY 200 OK\r\n".getBytes ();
		final byte[] EMPTY_LINE = "\r\n".getBytes ();
		
		byte[] buf = new byte[128];
		int len = readLine (buf);
		
		if (len < 10 || !areArraysEqual (RESPONSE_OK, buf, RESPONSE_OK.length)) {
			// the TCP/IP peer refuses to send NTRIP data, so we disconnect and retry after a short wait period.
			ntripCasterConnection.close ();
			try { Thread.sleep (1000); } catch (InterruptedException e) {}
			return;
		}

		// Eat the rest of the header.
		while (!areArraysEqual (EMPTY_LINE, buf, EMPTY_LINE.length))
			len = readLine (buf);
		
		state = STATE_RECEIVING_RTCM_DATA;
	}
	
	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read () throws IOException {
		
		while (state != STATE_RECEIVING_RTCM_DATA)
			connect ();
		
		int c = ntripInputStream.read ();

		++readCounter;
		return c;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener#receive(at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message)
	 */
	public void receive (Nmea0183Message message) {
		if (message.isAValidGgaMessage())
			startPosition = message.getBytes();
	}

	/**
	 * Return the current number of RTCM SC-104 message bytes read.
	 * @return  the current number of bytes read
	 * @uml.property  name="readCounter"
	 */
	public long getReadCounter () {
		return readCounter;
	}
	
}
