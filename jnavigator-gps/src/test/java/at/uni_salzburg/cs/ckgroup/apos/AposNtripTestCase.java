/*
 * @(#) AposNtripTestCase.java
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

import java.io.FileNotFoundException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the <code>AposNtrip</code> RTCM
 * SC-104 correction messages service connector.
 * 
 * @author Clemens Krainer
 */
public class AposNtripTestCase extends TestCase
{
	private static final String CFG_CASTER = "localhost";
	private static final String CFG_PORT = "21011";
	private static final String CFG_USERNAME = "vrs_tester";
	private static final String CFG_PASSWORT = "noPassword";
	private static final String CFG_MOUNTPOINT = "APOS_DGPS";
	
	Properties props = new Properties ();
	private AposNtripCasterMock caster;
	
	public void setUp () {

		props = new Properties ();
		props.setProperty (AposNtrip.PROP_CASTER, CFG_CASTER);
		props.setProperty (AposNtrip.PROP_PORT, CFG_PORT);
		props.setProperty (AposNtrip.PROP_USERNAME, CFG_USERNAME);
		props.setProperty (AposNtrip.PROP_PASSWORD, CFG_PASSWORT);
		props.setProperty (AposNtrip.PROP_MOUNTPOINT, CFG_MOUNTPOINT);
		props.setProperty (AposNtripCasterMock.PROP_INPUT_DATA_FILE, "at/uni_salzburg/cs/ckgroup/apos/AposNtripTest/reference.dat");
		props.setProperty (AposNtripCasterMock.PROP_EXPECTED_LOCATION, "$GPGGA,202810.00,4759.41831,N,01256.21095,E,1,07,1.95,434.2,M,46.6,M,,*55");
		
		try
		{
			caster = new AposNtripCasterMock (props);
			caster.startCasterThread ();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	public void tearDown () {
		caster.stopCasterThread ();
	}
	
	/**
	 * This test verifies the correctness of the <code>AposNtrip</code> class when everything works.
	 */
	public void testCase01 () {

		final int[] response = {
			0x66, 0x69, 0x53, 0x7e, 0x53, 0x65, 0x7f, 0x41, 0x44, 0x5e, 0x42, 0x40, 0x50, 0x55, 0x62, 0x59,
			0x66, 0x6f, 0x41, 0x78, 0x5a, 0x40, 0x76, 0x7c, 0x69, 0x5b, 0x52, 0x47, 0x64, 0x6e, 0x43, 0x79,
			0x7c, 0x7b, 0x5b, 0x57, 0x6d, 0x4c, 0x55, 0x5e, 0x4e, 0x50, 0x7d, 0x6d, 0x49, 0x50, 0x65, 0x51,
			0x6e, 0x73, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x59, 0x62, 0x6c, 0x41, 0x55, 0x65, 0x5f, 0x55, 0x43,
			0x4f, 0x4a, 0x6b, 0x40, 0x70, 0x52, 0x58, 0x54, 0x64, 0x7c, 0x55, 0x71, 0x40, 0x5a, 0x45, 0x50,
			0x78, 0x6f, 0x41, 0x4e, 0x55, 0x72, 0x69, 0x6a, 0x5e, 0x52, 0x78, 0x7f, 0x5d, 0x47, 0x5f, 0x41,
			0x64, 0x44, 0x45, 0x4d, 0x48, 0x7c, 0x77, 0x42, 0x6a, 0x67, 0x41, 0x50, 0x7c, 0x42, 0x40, 0x40,
			0x70, 0x7c, 0x7f, 0x40, 0x43, 0x60, 0x7e, 0x5e, 0x60, 0x41, 0x68, 0x42, 0x4a, 0x60, 0x7f, 0x5f,
			0x41, 0x41, 0x40, 0x41, 0x60, 0x43, 0x6e, 0x67, 0x43, 0x70, 0x7c, 0x7d, 0x5f, 0x40, 0x40, 0x41,
			0x73, 0x40, 0x7c, 0x7f, 0x41, 0x5c, 0x7f, 0x43, 0x40, 0x40, 0x7e, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f,
			0x7f, 0x43, 0x74, 0x7e, 0x58, 0x70, 0x7d, 0x4f, 0x7e, 0x72, 0x40, 0x7d, 0x7f, 0x7f, 0x72, 0x59,
			0x7e, 0x6d, 0x41, 0x50, 0x65, 0x7f, 0x6d, 0x41, 0x53, 0x60, 0x7c, 0x7b, 0x76, 0x59, 0x7f, 0x79,
			0x41, 0x74, 0x69, 0x40, 0x71, 0x7e, 0x5f, 0x43, 0x78, 0x42, 0x7b, 0x5f, 0x6e, 0x55, 0x7d, 0x77,
			0x49, 0x58, 0x48, 0x7e, 0x7b, 0x4c, 0x62, 0x40, 0x6e, 0x7f, 0x75, 0x77, 0x40, 0x7a, 0x4c, 0x40,
			0x54, 0x44, 0x60, 0x7e, 0x5f, 0x4e, 0x59, 0x40, 0x60, 0x4c, 0x5b, 0x78, 0x7f, 0x7b, 0x5e, 0x56,
			0x40, 0x60, 0x5d, 0x55, 0x63, 0x59, 0x7e, 0x6d, 0x41, 0x75, 0x6a, 0x7f, 0x5d, 0x7e, 0x4a, 0x60
		};
		
		try
		{
			AposNtrip apos = new AposNtrip (props);
			int x = apos.getConnectionState ();
			assertEquals (AposNtrip.STATE_DISCONNECTED, x);
			
			String locationString = props.getProperty (AposNtripCasterMock.PROP_EXPECTED_LOCATION) + "\r\n";
			
			Nmea0183Message location = new Nmea0183Message (locationString.getBytes ());
			
			DelayedHomeLocationProvider loc = new DelayedHomeLocationProvider (apos, location);
			loc.start ();

			for (int k=0; k < response.length; k++) {
				int ch = apos.read ();
				assertEquals ("Response " + k + ": ", ch, response[k]);
//				System.out.print ((char)ch);
			}
			
			assertEquals (256, apos.getReadCounter ());
		} catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase02 () {
		byte[] a = new byte[] {0,1,2};
		byte[] b = new byte[] {1,2,3};
		byte[] c = new byte[] {0};
		
		assertTrue (AposNtrip.areArraysEqual (a,a,3));
		assertTrue (AposNtrip.areArraysEqual (a,c,1));
		
		assertFalse (AposNtrip.areArraysEqual (a,b,3));
		assertFalse (AposNtrip.areArraysEqual (b,a,3));
		assertFalse (AposNtrip.areArraysEqual (a,c,3));
		assertFalse (AposNtrip.areArraysEqual (b,c,3));
		assertFalse (AposNtrip.areArraysEqual (b,c,1));
	}

	
	private class DelayedHomeLocationProvider extends Thread {
		
		private AposNtrip apos;
		private Nmea0183Message location;
		
		public DelayedHomeLocationProvider (AposNtrip apos, Nmea0183Message location) {
			this.apos = apos;
			this.location = location;
		}
		
		public void run () {
			System.out.println ("DelayedHomeLocationProvider: wait 2s.");
			try { Thread.sleep (2000); } catch (InterruptedException e) {}
			System.out.println ("DelayedHomeLocationProvider: send the location.");
			apos.receive (location);			
		}
	}
}
