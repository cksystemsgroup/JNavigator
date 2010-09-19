/*
 * @(#) Nmea0183MessageTestCase.java
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
package at.uni_salzburg.cs.ckgroup.nmea;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the Nmea0183Message class.
 * 
 * @author Clemens Krainer
 */
public class Nmea0183MessageTestCase extends TestCase
{
	/**
	 * Test if empty message leads to a "Message too short." exception message
	 */
	public void testCase01 () {
		final byte[] msg = "".getBytes ();
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg);
			fail();
			m.equals (m);	/* just for making the compiler happy. */
		} catch (Nmea0183MalformedMessageException e)
		{
			assertEquals("Message too short.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test if a malformed message leads to a "Checksum error." exception message
	 */
	public void testCase02 () {
		final byte[] msg = "$GPGGA*18\r\n".getBytes ();
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg);
			fail();
			m.equals (m);	/* just for making the compiler happy. */
		} catch (Nmea0183MalformedMessageException e)
		{
			assertEquals("Checksum error.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}

	/**
	 * Test if invalid prefix leads to a "Wrong NMEA 0183 message prefix." exception message
	 */
	public void testCase03 () {
		final byte[] msg = "$AAVTG,304.33,T,,M,0.635,N,1.176,K,A*3B\r\n".getBytes ();
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg);
			fail();
			m.equals (m);	/* just for making the compiler happy. */
		} catch (Nmea0183MalformedMessageException e)
		{
			assertEquals("Wrong NMEA 0183 message prefix.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}

	/**
	 * Test if invalid prefix leads to a "Wrong NMEA 0183 message prefix." exception message
	 */
	public void testCase04 () {
		final byte[] msg = "$GPVTG,304.33,T,,M,0.635,N,1.176,K,A*3B".getBytes ();
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg);
			fail();
			m.equals (m);	/* just for making the compiler happy. */
		} catch (Nmea0183MalformedMessageException e)
		{
			assertEquals("Wrong NMEA 0183 message postfix.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test if missing '*' before checksum leads to a "Checksum error." exception message
	 */
	public void testCase05 () {
		final byte[] msg = "$GPVTG,304.33,T,,M,0.635,N,1.176,K,A 3B\r\n".getBytes ();
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg);
			assertNull (m);
			fail();
		} catch (Nmea0183MalformedMessageException e)
		{
			assertEquals("Checksum error.", e.getMessage ());
		} catch (Throwable t) {
			t.printStackTrace ();
			fail();
		}
	}

	/**
	 * Test a correct NMEA 0183 message
	 */
	public void testCase06 () {

		final byte[] msg = "$GPGGA,223339.00,4759.43570,N,01256.20953,E,1,06,1.42,434.8,M,46.6,M,,*5F\r\n".getBytes ();
		
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg);
			assertTrue (m.isAValidGgaMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test a correct NMEA 0183 GGA message with invalid content 
	 */
	public void testCase07 () {

		final byte[] msg = "$GPGGA,193912.00,,,,,0,00,99.99,,,,,,*67\r\n".getBytes ();
		
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg);
			assertTrue (m.isAGgaMessage());
			assertFalse (m.isAValidGgaMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test a NMEA 0183 GPGSV message
	 */
	public void testCase08 () {
		final byte[] msg = "$GPGSV,2,1,07,01,08,151,42,03,66,168,47,14,24,124,39,16,04,185,41*7C\r\n".getBytes ();
		
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertFalse (m.isAValidGgaMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
	
	/**
	 * Test an invalid NMEA 0183 GPGS message
	 */
	public void testCase09 () {
		final byte[] msg = "$GPGS,2,1,07,01,08,151,42,03,66,168,47,14,24,124,39,16,04,185,41*2A\r\n".getBytes ();
		
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertFalse (m.isAValidGgaMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
	
	/**
	 * Test an invalid but formally valid NMEA 0183 message
	 */
	public void testCase10 () {
		final byte[] msg = "$GPGS,2,1,07,01,08,151,42,03,66,168,47,14,24,124,39,16,04,185,41*2A\r\n".getBytes ();
		
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertFalse (m.isAValidGgaMessage());
			assertFalse (m.startsWith ("$GPGSV,2,1,07,01,08,151,42,03,66,168,47,14,24,124,39,16,04,185,41*7C\r\n".getBytes ()));
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
	
	/**
	 * Test an empty $GPGGA NMEA 0183 message
	 */
	public void testCase11 () {
		final byte[] msg = "$GPGGA,193912.00,,,,,0,00,99.99,,,,,,*67\r\n".getBytes();
		
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertTrue (m.isAGgaMessage());
			assertFalse (m.isAValidGgaMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
	
	/**
	 * Test an empty $GPGGA NMEA 0183 message
	 */
	public void testCase12 () {
		final byte[] msg = "$GPGGA,,,,,,0,00,,,M,,M,,*66\r\n".getBytes();

		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertTrue (m.isAGgaMessage());
			assertFalse (m.isAValidGgaMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
	
	/**
	 * Test an empty $GPRMC NMEA 0183 message
	 */
	public void testCase13 () {
		final byte[] msg = "$GPRMC,174646.00,V,,,,,,,061007,,,N*7B\r\n".getBytes();

		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertTrue (m.isARmcMessage());
			assertFalse (m.isAValidRmcMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
		
	/**
	 * Test an empty $GPRMC NMEA 0183 message
	 */
	public void testCase14 () {
		final byte[] msg = "$GPRMC,,V,,,,,,,,,,N*53\r\n".getBytes();

		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertTrue (m.isARmcMessage());
			assertFalse (m.isAValidRmcMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
	
	/**
	 * Test an empty $GPVTG NMEA 0183 message
	 */
	public void testCase15 () {
		final byte[] msg = "$GPVTG,,,,,,,,,N*30\r\n".getBytes();

		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertTrue (m.isAVtgMessage());
			assertFalse (m.isAValidVtgMessage());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}
	}
	
	/**
	 * Verify the <code>Nmea0183Message.toString()</code< method.
	 */
	public void testCase16 () {
		final byte[] msg = "$GPGSV,2,1,07,01,08,151,42,03,66,168,47,14,24,124,39,16,04,185,41*7C\r\n".getBytes ();
		
		try
		{
			Nmea0183Message m = new Nmea0183Message (msg,0,msg.length);
			assertFalse (m.isAValidGgaMessage());
			assertEquals (new String (msg), m.toString());
		} catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}		
	}
}
