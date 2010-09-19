/*
 * @(#) GpsDaemonTestCase.java
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

import at.uni_salzburg.cs.ckgroup.gps.GpsDaemon;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;
import at.uni_salzburg.cs.ckgroup.rtcm.RtcmSc104ReferenceStationMock;
import at.uni_salzburg.cs.ckgroup.rtcm.RtcmSc104Scanner;
import junit.framework.TestCase;

public class GpsDaemonTestCase extends TestCase
{
	private static final String[] testCase01_messages = {
			"$GPGSV,2,1,07,01,08,151,42,03,66,168,47,14,24,124,39,16,04,185,41*7C\r\n",
			"$GPGSV,2,2,07,19,73,289,48,22,60,073,47,32,71,153,48*40\r\n",
			"$GPGGA,180827.00,4759.42796281,N,01256.20523945,E,1,06,2.6,443.17562,M,46.59871,M,0.0,,*68\r\n",
			"$GPGGA,180828.00,4759.42794361,N,01256.20522751,E,1,06,2.6,443.16461,M,46.59871,M,0.0,,*63\r\n",
			"$GPGGA,180829.00,4759.42787504,N,01256.20521871,E,1,06,2.6,443.18172,M,46.59871,M,0.0,,*62\r\n",
			"$GPGGA,180830.00,4759.42779600,N,01256.20518606,E,1,06,2.6,443.15816,M,46.59871,M,0.0,,*6E\r\n",
			"$GPGGA,180831.00,4759.42775535,N,01256.20518081,E,1,06,2.6,443.14765,M,46.59871,M,0.0,,*65\r\n",
			"$GPGGA,180832.00,4759.42772714,N,01256.20515094,E,1,06,2.6,443.13651,M,46.59871,M,0.0,,*68\r\n",
			"$GPGGA,180833.00,4759.42771710,N,01256.20509677,E,1,06,2.6,443.13186,M,46.59871,M,0.0,,*65\r\n",
			"$GPGGA,180834.00,4759.42771863,N,01256.20518034,E,1,06,2.6,443.09232,M,46.59871,M,0.0,,*6F\r\n",
			"$GPGGA,180835.00,4759.42768645,N,01256.20513956,E,1,06,2.6,443.08296,M,46.59870,M,0.0,,*64\r\n",
			"$GPGGA,180836.00,4759.42761591,N,01256.20517953,E,1,06,2.6,443.12807,M,46.59871,M,0.0,,*6D\r\n",
			"$GPGGA,180846.00,4759.42827780,N,01256.20472936,E,2,06,2.6,443.10761,M,46.59871,M,2.3,0120*44\r\n",
			"\r\n" };

	private int receivedMessages = 0;

	public void testCase01 () {
		GpsReceiverMock rcv = new GpsReceiverMock (testCase01_messages, 200);
		RtcmSc104ReferenceStationMock refStation;
		InputStream inputStream;
		RtcmSc104Scanner sc;
		
		try
		{
			receivedMessages = 0;
						
			Nmea0183MessageListener listener = new MyMsgListener ();
			GpsDaemon gpsd = new GpsDaemon (rcv);
			gpsd.addNmea0183MessageListener (listener);
			gpsd.start ();
			
			refStation = new RtcmSc104ReferenceStationMock ("at/uni_salzburg/cs/ckgroup/rtcm/RtcmSc104ScannerTest/reference.dat");
			inputStream = refStation.getInputStream ();
			sc = new RtcmSc104Scanner (inputStream);
			sc.addMessageListener (gpsd);
			sc.run ();

			while (receivedMessages < 6)
				try { Thread.sleep (100); } catch (Exception e) {}

			assertEquals (0, gpsd.getNumberOfNotSentRtcmMessages ());
			assertEquals (179, gpsd.getNumberOfSentRtcmMessages ());
			rcv.pretendOutputStreamIOException ();

			refStation = new RtcmSc104ReferenceStationMock ("at/uni_salzburg/cs/ckgroup/rtcm/RtcmSc104ScannerTest/reference.dat");
			inputStream = refStation.getInputStream ();
			sc = new RtcmSc104Scanner (inputStream);
			sc.addMessageListener (gpsd);
			sc.run ();
				
			while (receivedMessages < 12)
				try { Thread.sleep (100); } catch (Exception e) {}

			gpsd.terminate ();

			assertEquals (2, gpsd.getNumberOfMalformedMessages ());
			assertEquals (1, gpsd.getNumberOfNotSentRtcmMessages ());
			assertEquals (357, gpsd.getNumberOfSentRtcmMessages ());
			assertEquals (12, gpsd.getNumberOfWellFormedMessages ());

		} catch (IOException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	public void testCase02 () {
		IConnection rcv = new GpsReceiverMock (testCase01_messages, 100);

		try
		{
			receivedMessages = 0;
			Nmea0183MessageListener listener = new MyMsgListener ();
			GpsDaemon gpsd = new GpsDaemon (rcv);
			gpsd.addNmea0183MessageListener (listener);
			gpsd.removeNmea0183MessageListener (listener);
			gpsd.start ();
			try
			{
				Thread.sleep (1000);
			} catch (Exception e)
			{
			}
			gpsd.terminate ();

			assertEquals (0, receivedMessages);
		} catch (IOException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	public void testCase03 () {
		GpsReceiverMock rcv = new GpsReceiverMock (testCase01_messages, 100);

		try
		{
			receivedMessages = 0;
			Nmea0183MessageListener listener = new MyMsgListener ();
			GpsDaemon gpsd = new GpsDaemon (rcv);
			gpsd.addNmea0183MessageListener (listener);
			gpsd.start ();

			try { Thread.sleep (1000); } catch (Exception e) {}
			
			rcv.pretendInputStreamIOException ();
			
			int counter = 20;
			while (gpsd.isRunning () && counter-- > 0) {
				try { Thread.sleep (100); } catch (Exception e) {}
			}

			assertFalse (gpsd.isRunning ());

		} catch (IOException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	public void testCase04 () {
		byte[] msg = new byte[1024];
		for (int k=0; k < msg.length; k++)
			msg[k] = (byte)(65+k%26);
		msg[msg.length-1] = '\n';
		String[] testCase04_messages = new String [2];
		testCase04_messages[0] = new String (msg);
		testCase04_messages[1] = new String (msg);
		GpsReceiverMock rcv = new GpsReceiverMock (testCase04_messages, 100);

		try
		{
			receivedMessages = 0;
			Nmea0183MessageListener listener = new MyMsgListener ();
			GpsDaemon gpsd = new GpsDaemon (rcv);
			gpsd.addNmea0183MessageListener (listener);
			gpsd.start ();

			try { Thread.sleep (1000); } catch (Exception e) {}
			assertTrue (gpsd.isRunning ());
			
			gpsd.terminate ();
			
			int counter = 30;
			while (gpsd.isRunning () && counter-- > 0) {
				try { Thread.sleep (1000); } catch (Exception e) {}
			}

			assertFalse (gpsd.isRunning ());

		} catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This class implements a simple receiver for NMEA 0183 messages.
	 */
	private class MyMsgListener implements Nmea0183MessageListener
	{

		public void receive (Nmea0183Message message) {
			++receivedMessages;
			// printGpsdStatus ();
			System.out.print ("GpsDaemonTestCase.receive: ");
			try
			{
				System.out.write (message.getBytes ());
			} catch (IOException e)
			{
			}
			System.out.flush ();
		}

		// public void printGpsdStatus () {
		// System.out.print ("State: well formed messages=" +
		// gpsd.getNumberOfWellFormedMessages ());
		// System.out.print (", malformed messages=" +
		// gpsd.getNumberOfMalformedMessages ());
		// System.out.print (", sent RTCM messages=" +
		// gpsd.getNumberOfSentRtcmMessages ());
		// System.out.print (", not sent RTCM messages=" +
		// gpsd.getNumberOfNotSentRtcmMessages ());
		// System.out.println ();
		// }
	}
}
