/*
 * @(#) GpsDaemon.java
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
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MalformedMessageException;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageProvider;
import at.uni_salzburg.cs.ckgroup.rtcm.RtcmSc104Message;
import at.uni_salzburg.cs.ckgroup.rtcm.RtcmSc104MessageListener;

/**
 * TODO describe!
 *
 * @author  Clemens Krainer
 */
public class GpsDaemon extends Thread implements RtcmSc104MessageListener, Nmea0183MessageProvider
{
	/**
	 * This variable contains all NMEA message listeners. 
	 */
	private Vector<Nmea0183MessageListener> nmeaMessageListeners = new Vector<>();
	
	/**
	 * The running indicator of this thread. The set/reset by the methods run()
	 * and terminate(), respectively.
	 * 
	 * @uml.property name="running"
	 */
	private boolean running = false;

	/**
	 * The number of well formed messages received. 
	 */
	private int wellFormedMessages = 0;
	
	/**
	 * The number of malformed messages received. 
	 */
	private int malformedMessages = 0;
	
	/**
	 * The number of RTCM messages sent to the GPS receiver.
	 */
	private int sentRtcmMessages = 0;
	
	/**
	 * The number of RTCM messages not sent to the GPS receiver.
	 */
	private int notSentRtcmMessages = 0;
	
	/**
	 * The last word of the last RTCM message sent to the GPS receiver.  
	 */
	private int previouslySentWord = 0;
	
	/**
	 * The current GPS receiver abstracted as an IConnection. 
	 */
	private IConnection gpsReceiver;
	
	/**
	 * The current GPS receiver input stream.
	 */
	private InputStream gpsInputStream;
	
	/**
	 * The current GPS receiver output stream. 
	 */
	private OutputStream gpsOutputStream;
	
	/**
	 * Construct a GPS daemon thread
	 * 
	 * @param gpsReceiver the already open GPS receiver connection
	 * @throws IOException thrown in case of errors
	 */
	public GpsDaemon (IConnection gpsReceiver) throws IOException {
		this.gpsReceiver = gpsReceiver;
		gpsInputStream = this.gpsReceiver.getInputStream ();
		gpsOutputStream = this.gpsReceiver.getOutputStream ();		
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageProvider#addNmea0183MessageListener(at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener)
	 */
	public void addNmea0183MessageListener (Nmea0183MessageListener listener) {
		nmeaMessageListeners.add (listener);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageProvider#removeNmea0183MessageListener(at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener)
	 */
	public void removeNmea0183MessageListener (Nmea0183MessageListener listener) {
		while (nmeaMessageListeners.remove (listener))
			continue;
	}
	
	/**
	 * @return the number of malformed messages read from the GPS receiver
	 */
	public int getNumberOfMalformedMessages () {
		return malformedMessages;
	}
	
	/**
	 * @return the number of well formed messages
	 */
	public int getNumberOfWellFormedMessages () {
		return wellFormedMessages;
	}

	/**
	 * @return the number of not sent RTCM SC-104 messages
	 */
	public int getNumberOfNotSentRtcmMessages () {
		return notSentRtcmMessages;
	}
	
	/**
	 * @return the number of sent RTCM SC-104 messages
	 */
	public int getNumberOfSentRtcmMessages () {
		return sentRtcmMessages;
	}
	
	/**
	 * Read one line from the GPS receiver, including \r and \n.
	 * 
	 * @param b a byte array to put the read line in
	 * @return the number of read characters
	 * @throws IOException thrown in case of errors
	 */
	protected int readLine (byte[] b) throws IOException {
		int count = 0;
		int ch;
		
//		try {
			while ( b.length > count && (ch = gpsInputStream.read ()) >= 0) {
				b[count++] = (byte) ch;
				if (ch == '\n')
					break;
			}
//		} catch (ArrayIndexOutOfBoundsException e) {
//			e.printStackTrace();
//			System.err.println ("readLine Exception");
//		}
		
//		System.err.print ("GpsDaemon.readLine: '");
//		System.err.write (b, 0, count);
//		System.err.println ("'");
		
		return count;
	}

	/**
	 * Deliver a received message to all listeners
	 * 
	 * @param message the message to be delivered
	 */
	protected void fireNewMessage (Nmea0183Message message) {
//		System.out.println ("fireNewMessage: " + message.toString());

		for (int i = 0; i < nmeaMessageListeners.size (); i++) {
			Object l = nmeaMessageListeners.get (i);
			if (l instanceof Nmea0183MessageListener)
				((Nmea0183MessageListener)l).receive (message);
		}
	}
	
	/**
	 * Run the GPS daemon thread. It reads messages from the GPS receiver and
	 * delivers them to registered listeners.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run () {
		running = true;
		byte[] line = new byte[512];
		int len;
		
		try {
			
			while (running) {
				len = readLine (line); 
				if (len > 0) {				
					Nmea0183Message m;
					try
					{
						m = new Nmea0183Message (line,0,len);
//						System.out.print ("Message received (ok): " + new String(line,0,len));
						++wellFormedMessages;
						fireNewMessage (m);
					} catch (Nmea0183MalformedMessageException e)
					{
						++malformedMessages;
						System.err.print ("Message received (err): '" + new String(line,0,len) + "'");
						// TODO handle these errors
					}
				}
			}

			gpsReceiver.close ();
			
		} catch (IOException e) {
			System.out.println ("end.");
			running = false;
		}
	}
	
	/**
	 * Terminate this thread
	 */
	public void terminate () {
		running = false;
	}
	
	/**
	 * Return whether this Thread is running or not.
	 * @return  true if this Thread is running, false otherwise.
	 * @uml.property  name="running"
	 */
	public boolean isRunning () {
		return running;
	}
	
	/**
	 * Receive a RtcmSc104Message and forward it to the GPS receiver
	 * 
	 * @param message the message to be forwarded
	 * @see at.uni_salzburg.cs.ckgroup.rtcm.RtcmSc104MessageListener#receive(at.uni_salzburg.cs.ckgroup.rtcm.RtcmSc104Message)
	 */
	public void receive (RtcmSc104Message message) {
		byte[] m = message.getBytes (previouslySentWord);
		try
		{
			gpsOutputStream.write (m);
			previouslySentWord = message.getLastWord ();
			++sentRtcmMessages;
		} catch (IOException e)
		{
			++notSentRtcmMessages;
		}
	}

}
