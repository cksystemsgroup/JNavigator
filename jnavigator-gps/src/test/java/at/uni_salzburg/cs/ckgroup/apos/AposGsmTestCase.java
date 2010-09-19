/*
 * @(#) AposGsmTestCase.java
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.gps.GpsDaemon;
import at.uni_salzburg.cs.ckgroup.gps.GpsReceiverMock;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.io.SerialLine;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import junit.framework.TestCase;

/**
 * This class verifies the implementation of the AposGsm class.
 * 
 * @author Clemens Krainer
 */
public class AposGsmTestCase extends TestCase
{
	private static final String position = "$GPGGA,223339.00,4759.43570,N,01256.20953,E,1,06,1.42,434.8,M,46.6,M,,*5F\r\n";
	private static final String phoneNumber = "013379502";
	
	private static final String testCase01_inputStream =
		"at\r\nOK\r\n" +
		"atd013379502\r\n" +
		"CONNECT 9600\r\n" +
		"Trying GpsNet10500 (10.254.98.11, 10500)... Open\r\n" +
		"???\r\n" +
		"???\r\n";

	private static final String testCase02_inputStream =
		"at\r\nOK\r\n" +
		"atd013379502\r\n" +
		"CONNECT 9600\r\n" +
		"Trying GpsNet10200 (10.254.98.11, 10200)...\r\n" +
		"% Connection refused by remote host\r\n" +
		"\r\n" +
		"NO CARRIER\r\n" + testCase01_inputStream;

	private static final String testCase03_inputStream =
		"at\r\nOK\r\n" +
		"atd013379502\r\n" +
		"CONNECT 9600\r\n" +
		"Trying GpsNet10500 (10.254.98.11, 10500)... Open\r\n" +
		"?\r\n" +
		"?\r\n" +
		"???\r\n" +
		"???\r\n" +
		"abcdeNO CARRIERfghijk" + testCase01_inputStream;
	
	private static final String[] testCase03_messages = {
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
		"$GPGGA,180846.00,4759.42827780,N,01256.20472936,E,2,06,2.6,443.10761,M,46.59871,M,2.3,0120*44\r\n"
	};

	/**
	 * This test case instantiates a mock modem and a AposGsm stream. The
	 * AposGsm stream must call the provided phone number and send the current
	 * position as a NMEA 0183 GGA message. The test verifies the output of the
	 * AposGsm stream against an expected string.
	 */
	public void testCase01 () {
		AposGsmModemMock modem = new AposGsmModemMock (testCase01_inputStream.getBytes ());
		
		Nmea0183Message gpgga = null;
		
		try
		{
			gpgga = new Nmea0183Message (position.getBytes ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
		
		assertNotNull (gpgga);
		
		try
		{
			AposGsm apos = new AposGsm (modem, phoneNumber);
			assertNotNull (apos);
			apos.receive (gpgga);

			apos.read ();
			assertEquals (AposGsm.STATE_RECEIVING_RTCM_DATA, apos.getConnectionState ());
			
			byte[] buf = modem.getOutputStreamBuffer ();
			System.out.print ("OutputStream: '");
			System.out.flush ();
			System.out.write (buf);
			System.out.flush ();
			System.out.println ("'");
			assertEquals("at\r\natd013379502\r\n$GPGGA,223339.00,4759.43570,N,01256.20953,E,1,06,1.42,434.8,M,46.6,M,,*5F\r\n",new String(buf));
		} catch (IOException e)
		{
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test case instantiates a mock modem and a AposGsm stream. The
	 * AposGsm stream must call the provided phone number and send the current
	 * position as a NMEA 0183 GGA message. The mock modem issues a NO CARRIER
	 * error messages and the AposGsm stream has to dial again. The test
	 * verifies the output of the AposGsm stream against an expected string.
	 */
	public void testCase02 () {
		AposGsmModemMock modem = new AposGsmModemMock (testCase02_inputStream.getBytes ());
		
		Nmea0183Message gpgga = null;
		
		try
		{
			gpgga = new Nmea0183Message (position.getBytes ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
		
		assertNotNull (gpgga);
		
		try
		{
			AposGsm apos = new AposGsm (modem, phoneNumber);
			assertNotNull (apos);
			apos.receive (gpgga);

			apos.read ();
			assertEquals (AposGsm.STATE_RECEIVING_RTCM_DATA, apos.getConnectionState ());
			
			byte[] buf = modem.getOutputStreamBuffer ();
			System.out.print ("OutputStream: '");
			System.out.flush ();
			System.out.write (buf);
			System.out.flush ();
			System.out.println ("'");
			assertEquals("at\r\natd013379502\r\nat\r\natd013379502\r\n$GPGGA,223339.00,4759.43570,N,01256.20953,E,1,06,1.42,434.8,M,46.6,M,,*5F\r\n",new String(buf));
			
		} catch (IOException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test case instantiates a mock modem and a AposGsm stream. The
	 * AposGsm stream must call the provided phone number and send the current
	 * position as a NMEA 0183 GGA message. The mock modem issues a NO CARRIER
	 * error messages and the AposGsm stream has to dial again. The test
	 * verifies the output of the AposGsm stream against an expected string.
	 */
	public void testCase03 () {
		AposGsmModemMock modem = new AposGsmModemMock (testCase03_inputStream.getBytes ());
		
		Nmea0183Message gpgga = null;
		
		try
		{
			gpgga = new Nmea0183Message (position.getBytes ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
		
		assertNotNull (gpgga);
		
		try
		{
			AposGsm apos = new AposGsm (modem, phoneNumber);
			assertNotNull (apos);
			
			IConnection gpsReceiver = new GpsReceiverMock (testCase03_messages, 5000); 
			GpsDaemon gpsDaemon = new GpsDaemon (gpsReceiver);
			gpsDaemon.start ();
			gpsDaemon.addNmea0183MessageListener (apos);
			
			apos.read ();
			apos.receive (gpgga);

			apos.read ();
			assertEquals (AposGsm.STATE_RECEIVING_RTCM_DATA, apos.getConnectionState ());
			while (apos.read () >= 0)
				continue;
			
			System.out.println ("Readcounter="+apos.getReadCounter ());
			
			byte[] buf = modem.getOutputStreamBuffer ();
			System.out.print ("OutputStream: '");
			System.out.flush ();
			System.out.write (buf);
			System.out.flush ();
			System.out.println ("'");
			assertEquals("at\r\natd013379502\r\n" +
               "$GPGGA,180836.00,4759.42761591,N,01256.20517953,E,1,06,2.6,443.12807,M,46.59871,M,0.0,,*6D\r\n" +
               "at\r\natd013379502\r\n" +
               "$GPGGA,223339.00,4759.43570,N,01256.20953,E,1,06,1.42,434.8,M,46.6,M,,*5F\r\n", new String(buf));
			
			gpsDaemon.terminate ();
		} catch (IOException e)
		{
			e.printStackTrace();
			fail ();
		}

	}
	
	/**
	 * This method works as testCase01() and testCase02() but uses a serial line
	 * to connect to a real GSM mobile phone.
	 * 
	 * @see at.uni_salzburg.cs.ckgroup.apos.AposGsmTestCase#testCase01()
	 * @see at.uni_salzburg.cs.ckgroup.apos.AposGsmTestCase#testCase02()
	 */
	public void notestCase04 () {
		
		File serialLine = new File ("/dev/ttyUSB0");
		SerialLine modem = null;
		
		try {
			modem = new SerialLine (serialLine, 115200, 8, 1, "n");
		}
		catch (Exception e) {
			fail ();
		}
		
		assertNotNull (modem); 
		Nmea0183Message gpgga = null;
		
		try
		{
			String position = "$GPGGA,231201.00,4759.43503,N,01256.20447,E,1,07,1.09,432.5,M,46.6,M,,*5F\r\n";
			gpgga = new Nmea0183Message (position.getBytes ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
		
		assertNotNull (gpgga);
		
		try
		{
			AposGsm apos = new AposGsm (modem, phoneNumber);
			assertNotNull (apos);
			apos.receive (gpgga);

			System.out.print ("APOS Data: ");
			for (int k=0; k < 100; k++) {
				int b = apos.read ();
				System.out.write (b);
			}
			System.out.flush ();
			System.out.println ();
			
		} catch (IOException e)
		{
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test case instantiates a mock modem and a AposGsm stream. The
	 * AposGsm stream must call the provided phone number and send the current
	 * position as a NMEA 0183 GGA message. The test verifies the output of the
	 * AposGsm stream against an expected string. This test is like
	 * testCase01(), but employs the ObjectFactory to to build an AposGsm
	 * object.
	 */
	public void testCase05 () {
		
		Properties props = new Properties ();
		props.setProperty ("className", "at.uni_salzburg.cs.ckgroup.apos.AposGsm");
		props.setProperty ("phoneNumber", "013379502");
		props.setProperty ("connectorClassName", "at.uni_salzburg.cs.ckgroup.apos.AposGsmModemMock");
		props.setProperty ("input", testCase01_inputStream);
		
		Nmea0183Message gpgga = null;
		
		try
		{
			gpgga = new Nmea0183Message (position.getBytes ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
		
		assertNotNull (gpgga);
		
		try
		{
			InputStream apos = ObjectFactory.getInstance ().instantiateInputStream ("", props);
			assertNotNull (apos);
			assertTrue (apos instanceof Nmea0183MessageListener);
			assertTrue (apos instanceof AposGsm);
			((Nmea0183MessageListener)apos).receive (gpgga);

			apos.read ();
			assertEquals (AposGsm.STATE_RECEIVING_RTCM_DATA, ((AposGsm)apos).getConnectionState ());
			
			byte[] buf = AposGsmModemMock.currentInstance.getOutputStreamBuffer ();
			System.out.print ("OutputStream: '");
			System.out.flush ();
			System.out.write (buf);
			System.out.flush ();
			System.out.println ("'");
			assertEquals(new String(buf),"at\r\natd013379502\r\n$GPGGA,223339.00,4759.43570,N,01256.20953,E,1,06,1.42,434.8,M,46.6,M,,*5F\r\n");
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}

}
