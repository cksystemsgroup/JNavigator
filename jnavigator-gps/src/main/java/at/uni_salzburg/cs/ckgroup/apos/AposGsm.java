/*
 * @(#) AposGsm.java
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
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class implements the APOS-GSM service for RTCM SC-104 correction
 * messages as an InputStream. It's an automaton that waits for a valid position
 * message (NMEA GGA) from the GPS receiver and then connects to the APOS-GSM
 * service. Until the APOS-GSM service sends valid RTCM messages, a read() call
 * is blocked. This class detects connection failures and reconnects to the
 * APOS-GSM service automatically.
 *
 * @author   Clemens Krainer
 * @uml.dependency   supplier="at.uni_salzburg.cs.ckgroup.io.IConnection"
 */
public class AposGsm extends InputStream implements Nmea0183MessageListener
{	
	/**
	 * Property constants. 
	 */
	public static final String PROP_CONNECTOR_CLASS_NAME = "connectorClassName";
	public static final String PROP_PHONE_NUMBER = "phoneNumber";
	
	/**
	 * The NMEA 0183 GGA message containing the rough current position. 
	 */
	private byte[] startPosition = null;
	
	/**
	 * The GSM modem sends a "NO CARRIER" message if the connection fails or
	 * breaks.
	 */
	private static final byte[] noCarrier = "NO CARRIER".getBytes ();
	private int noCarrierIndex = 0;
	
	/**
	 * The number of bytes read from the APOS RTCM SC-104 stream
	 * @uml.property  name="readCounter"
	 */
	private long readCounter = 0;
	
	/**
	 * The state constants for this automaton. 
	 */
	public static final int STATE_DISCONNECTED = 0;
	public static final int STATE_DIALING = 1;
	public static final int STATE_CONNECTED = 2;
	public static final int STATE_RECEIVING_RTCM_DATA = 3;
	
	/**
	 * The current state. 
	 */
	private int state = STATE_DISCONNECTED;
	
	/**
	 * The connection to the GSM modem
	 */
	private IConnection modemConnection;
	
	/**
	 * The InputStream of the GSM modem connection.  
	 */
	private InputStream modemInputStream;
	
	/**
	 * The OutputStream of the GSM modem connection.  
	 */
	private OutputStream modemOutputStream;
	
	/**
	 * The GSM phone number of the APOS service. 
	 */
	private String phoneNumber;
	
	/**
	 * The Properties of the attached connection (optional). If this Properties
	 * are not null the connection to the GSM modem will be established before
	 * dialing.
	 */
	private Properties connectionProperties = null;

	/**
	 * Construct an APOS GSM RTCM input stream.
	 * 
	 * @param modemConnection the already opened connection to the GSM modem
	 * @param phoneNumber the phone number to be called for the APOS service
	 * @throws IOException thrown in case of errors
	 */
	public AposGsm (IConnection modemConnection, String phoneNumber) throws IOException {
		this.modemConnection = modemConnection;
		this.modemInputStream = this.modemConnection.getInputStream ();
		this.modemOutputStream = this.modemConnection.getOutputStream ();
		this.phoneNumber = phoneNumber;
	}
	
	/**
	 * Construct an APOS GSM RTCM input stream.
	 * 
	 * @param props the connection properties
	 * @throws IOException thrown in case of errors
	 * @throws InstantiationException thrown in case of errors in the ObjectFactory.
	 */
	public AposGsm (Properties props) throws IOException {
		
		phoneNumber = props.getProperty (PROP_PHONE_NUMBER);
		
		connectionProperties = new Properties ();
		connectionProperties.putAll (props);
		connectionProperties.setProperty (ObjectFactory.PROP_CLASS_NAME, props.getProperty (PROP_CONNECTOR_CLASS_NAME));
		connectionProperties.remove (PROP_CONNECTOR_CLASS_NAME);
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
	 * Check if the newly received character completes a "NO CARRIER" message
	 * from the modem. If the connection broke, reset the state to disconnected.
	 * 
	 * @param c the newly received character from the modem.
	 */
	private void checkIfCarrierIsLost (int c) {
		if (noCarrier[noCarrierIndex] == c)
			++noCarrierIndex;
		else
			noCarrierIndex = 0;
		
		if (noCarrierIndex >= noCarrier.length) {
			state = STATE_DISCONNECTED;
		}
	}
	
	/**
	 * Dial the phone number of the APOS service. Before dialing, this method
	 * waits for a valid start position in $GPGGA format. This is necessary,
	 * because APOS requires the rough current position before it sends RTCM
	 * SC-104 correction data.
	 * 
	 * @throws IOException thrown in case of errors when sending the call
	 *         command to the modem.
	 */
	private void dial () throws IOException {
		
		while (startPosition == null) {
			System.out.println ("AposGsm.dial: waiting for a valid $GPGGA start position.");
			try { Thread.sleep (1000); } catch (InterruptedException e) {}
			if (startPosition != null)
				System.out.println ("AposGsm.dial: $GPGGA start position is now available: " + startPosition);
		}
		
		ModemWorker worker;
		String dialCommand = "atd" +  phoneNumber + "\r\n";
		String aposGsmWaitForGga = "???\r\n";
		byte[][] resultStrings = new byte[1][];
		resultStrings[0] = aposGsmWaitForGga.getBytes ();
		byte[][] result = null;
		
		if (connectionProperties != null) {
			
			if (modemInputStream != null)
				modemInputStream.close ();
			
			if (modemOutputStream != null)
				modemOutputStream.close ();
			
			if (modemConnection != null)
				modemConnection.close ();
			
			worker = new ModemWorker (connectionProperties);
			worker.connect (1);
			result = worker.executeCommand (dialCommand.getBytes (), resultStrings, 1);

			modemConnection = worker.getConnection ();
			modemInputStream = modemConnection.getInputStream ();
			modemOutputStream = modemConnection.getOutputStream ();
			
		} else {
			
			worker = new ModemWorker (modemConnection);
			worker.connect (1);
			result = worker.executeCommand (dialCommand.getBytes (), resultStrings, 1);
		}
		
//		if (result != null && result[0] != null) {
//			System.out.print ("dial: Result='");
//			System.out.flush ();
//			System.out.write (result[0]);
//			System.out.flush ();
//			System.out.println ("'");
//		}
		
		if (result != null && result[1] != null && aposGsmWaitForGga.equals(new String(result[1]))) {
			
			System.out.println ("AposGsm.read: sending $GPGGA start position.");
			modemOutputStream.write (startPosition);
			modemOutputStream.flush ();
			
			state = STATE_RECEIVING_RTCM_DATA;
		}
		
		noCarrierIndex = 0;
	}
	

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read () throws IOException {

		while (state == STATE_DISCONNECTED)
			dial ();
		
		int c = modemInputStream.read ();
		checkIfCarrierIsLost (c);
		++readCounter;
//		if (readCounter % 100 == 0)
//			System.out.println ("AposGsm.read: " + readCounter + " bytes read.");
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
