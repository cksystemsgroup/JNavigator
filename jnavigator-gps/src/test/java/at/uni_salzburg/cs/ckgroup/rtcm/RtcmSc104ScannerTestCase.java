/*
 * @(#) RtcmSc104ScannerTestCase.java
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
package at.uni_salzburg.cs.ckgroup.rtcm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the RtcmSc104Scanner class.
 * 
 * @author Clemens Krainer
 */
public class RtcmSc104ScannerTestCase extends TestCase
{
	/**
	 * The number of received messages.
	 */
	private int messageCounter;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		messageCounter = 0;
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown () {

	}
	
	/**
	 * Test if the RtcmSc104Scanner implementation can read well formed RTCM
	 * SC-104 messages.
	 */
	public void testCase01 ()
	{
		try {
			RtcmSc104ReferenceStationMock refStation =
				new RtcmSc104ReferenceStationMock ("at/uni_salzburg/cs/ckgroup/rtcm/RtcmSc104ScannerTest/reference.dat");
			
			InputStream inputStream = refStation.getInputStream ();
			RtcmSc104Scanner sc = new RtcmSc104Scanner (inputStream);
	
			RtcmSc104MessageListener listener = new MessageListener ();
			sc.addMessageListener (listener);
			sc.removeMessageListener (listener);
			sc.addMessageListener (listener);
			
			sc.run ();
			
			System.out.println ("Scanner has terminated.");
		} catch (FileNotFoundException e) {
			fail ();
		}
		
		assertEquals ("Number of messages received.", 179, messageCounter);
	}
	
	
	/**
	 * This class implements a RTCM SC-104 message listener. testCase01() uses
	 * this class to receive the messages from the RtcmSc104Scanner instance.
	 */
	private class MessageListener implements RtcmSc104MessageListener {
		
		private int previouslySentWord = 2;

		public void receive (RtcmSc104Message message) {

			try
			{
				System.out.println ("\nMessage received: " + message.toString ());
				System.out.flush ();
				System.out.write ("Byte stream: ".getBytes ());
				System.out.write (message.getBytes (previouslySentWord));
				System.out.flush ();				
				previouslySentWord = message.getLastWord ();
				System.out.println ();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			
			++messageCounter;
			assertEquals ("Station ID", 411, message.stationId);
			assertEquals ("Station Health",   0, message.stationHealth);
			assertEquals ("Sequence Number",  (messageCounter+6) % 8, message.sequenceNumber);
			switch (message.messageType) {
//			case 1: assertEquals ("Message 1 length", 15, message.messageLength); break;
//			case 2: assertEquals ("Message 2 length", 15, message.messageLength); break;
			case 23: assertEquals ("Message 23 length", 1, message.messageLength); break;
			case 24: assertEquals ("Message 24 length", 6, message.messageLength); break;
			case 59: assertEquals ("Message 59 length", 22, message.messageLength); break;
			}
		}
	}
	
	/**
	 * Test if the RtcmSc104Scanner implementation can run as a separate Thread.
	 */
	public void testCase02 ()
	{
		try {
			RtcmSc104ReferenceStationMock refStation =
				new RtcmSc104ReferenceStationMock ("at/uni_salzburg/cs/ckgroup/rtcm/RtcmSc104ScannerTest/reference.dat");
			
			InputStream inputStream = refStation.getInputStream ();
			RtcmSc104Scanner sc = new RtcmSc104Scanner (inputStream);
			
			RtcmSc104MessageListener listener = new RtcmSc104MessageListener () {
				public void receive (RtcmSc104Message message) {
					try { Thread.sleep(1000); } catch (InterruptedException e) { }
				}
			};
			sc.addMessageListener (listener);
			
			sc.start ();
			sc.terminate ();
			
			System.out.println ("Scanner has terminated.");
		} catch (Exception e) {
			fail ();
		}
	}
	
	/**
	 * Test if the RtcmSc104Scanner implementation throws an IOException on EOF.
	 */
	public void notestCase03 ()
	{
		try {
			RtcmSc104ReferenceStationMock refStation =
				new RtcmSc104ReferenceStationMock ("at/uni_salzburg/cs/ckgroup/rtcm/RtcmSc104ScannerTest/defective.dat");
			
			InputStream inputStream = refStation.getInputStream ();
			RtcmSc104Scanner sc = new RtcmSc104Scanner (inputStream);
			
//			RtcmSc104MessageListener listener = new RtcmSc104MessageListener () {
//				public void receive (RtcmSc104Message message) { }
//			};
//			sc.addMessageListener (listener);
			
			sc.run ();
			fail ();
		} catch (IOException e) {
			assertEquals ("End of data.",e.getMessage ());
		} catch (Exception e) {
			fail ();
		}		
	}
	
}
