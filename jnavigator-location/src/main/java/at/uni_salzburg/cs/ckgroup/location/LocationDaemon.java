/*
 * @(#) LocationDaemon.java
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
package at.uni_salzburg.cs.ckgroup.location;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

public class LocationDaemon extends Thread implements ILocationMessageProvider
{
	/**
	 * This variable contains all location message listeners. 
	 */
	private Vector<ILocationMessageListener> locationMessageListeners = new Vector<>();
	
	/**
	 * The running indicator of this thread. The set/reset by the methods run() and terminate(), respectively.
	 * @uml.property  name="running"
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
	 * The current location receiver abstracted as an IConnection. 
	 */
	private IConnection locationReceiver;
	
	/**
	 * The current location receiver input stream.
	 */
	private InputStream locationInputStream;
	
	/**
	 * The current location receiver output stream. 
	 */
//	private OutputStream locationOutputStream;
	
	/**
	 * Construct a location daemon thread
	 * 
	 * @param gpsReceiver the already open GPS receiver connection
	 * @throws IOException thrown in case of errors
	 */
	public LocationDaemon (IConnection locationReceiver) throws IOException {
		this.locationReceiver = locationReceiver;
		locationInputStream = this.locationReceiver.getInputStream ();
//		locationOutputStream = this.locationReceiver.getOutputStream ();		
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.location.LocationMessageProvider#addLocationMessageListener(at.uni_salzburg.cs.ckgroup.location.LocationMessageListener)
	 */
	public void addLocationMessageListener(ILocationMessageListener listener) {
		locationMessageListeners.add (listener);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.location.LocationMessageProvider#removeLocationMessageListener(at.uni_salzburg.cs.ckgroup.location.LocationMessageListener)
	 */
	public void removeLocationMessageListener(ILocationMessageListener listener) {
		while (locationMessageListeners.remove (listener))
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
	 * Read one line from the location receiver, including \r and \n.
	 * 
	 * @param b a byte array to put the read line in
	 * @return the number of read characters
	 * @throws IOException thrown in case of errors
	 */
	protected int readLine (byte[] b) throws IOException {
		int count = 0;
		int ch;
		
		while ( b.length >= count && (ch = locationInputStream.read ()) >= 0) {
			b[count++] = (byte) ch;
			if (ch == '\n')
				break;
		}
		
//		System.out.print ("LocationDaemon.readLine: '");
//		System.out.write (b, 0, count);
//		System.out.println ("'");
		
		return count;
	}
	
	/**
	 * Deliver a received message to all listeners
	 * 
	 * @param message the message to be delivered
	 */
	protected void fireNewMessage (LocationMessage message) {
//		System.out.println ("fireNewMessage: " + message.toString());

		for (int i = 0; i < locationMessageListeners.size (); i++) {
			Object l = locationMessageListeners.get (i);
			((ILocationMessageListener)l).receive (message);
		}
	}
	
	/**
	 * Run the location daemon thread. It reads messages from the location
	 * receiver and delivers them to registered listeners.
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
					LocationMessage m;
					try
					{
						m = new LocationMessage (line,0,len);
//						System.out.print ("Message received (ok): " + new String(line,0,len));
						++wellFormedMessages;
						fireNewMessage (m);
					} catch (MalformedLocationMessageException e)
					{
						++malformedMessages;
						System.out.print ("Message received (err): " + new String(line,0,len));
						// TODO handle these errors
					}
				}
			}

			locationReceiver.close ();
			
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
	 * 
	 * @return  true if this Thread is running, false otherwise.
	 * @uml.property  name="running"
	 */
	public boolean isRunning () {
		return running;
	}
}
