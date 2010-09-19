/*
 * @(#) GpsDaemonBuilderTestCase.java
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

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.gps.GpsDaemon;
import at.uni_salzburg.cs.ckgroup.gps.GpsDaemonBuilder;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;
import junit.framework.TestCase;

/**
 * This class verifies the implementation of the <code>GpsDaemonBuilder</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class GpsDaemonBuilderTestCase extends TestCase
{
	private static final String goodProps1 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-ok1.properties";
	private static final String goodProps2 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-ok2.properties";
	private static final String propsMock = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-mock.properties";
	private static final String notAvailableProps = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/i-do-not-exists.properties";
	private static final String deficientProps1 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-fail1.properties";
	private static final String deficientProps2 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-fail2.properties";
	private static final String deficientProps3 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-fail3.properties";
	private static final String deficientProps4 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-fail4.properties";
	private static final String deficientProps5 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-fail5.properties";
	private static final String deficientProps6 = "at/uni_salzburg/cs/ckgroup/gps/GpsDaemonBuilderTest/jnavigator-fail6.properties";
	private GpsDaemon gpsd = null;
	
	/**
	 * This method constructs a <code>GpsDaemonBuilder</code> and a
	 * Nmea0183MessageListener and runs the <code>GpsDaemonBuilder</code> to
	 * receive messages.
	 */
	public void NOtestCase01 () {
		GpsDaemonBuilder p;
		Nmea0183MessageListener listener;
		try
		{
			listener = new MyMsgListener ();
			p = new GpsDaemonBuilder (goodProps1);
			gpsd = p.getGpsDaemon ();
			assertNotNull (gpsd);
			gpsd.addNmea0183MessageListener (listener);
			gpsd.run ();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}	
	}
	
	/**
	 * This method constructs a <code>GpsDaemonBuilder</code> and a
	 * Nmea0183MessageListener and runs the <code>GpsDaemonBuilder</code> to
	 * receive messages.
	 */
	public void testCase02 () {
		GpsDaemonBuilder p;
		Nmea0183MessageListener listener;
		try
		{
			listener = new MyMsgListener ();
			p = new GpsDaemonBuilder (propsMock);
			gpsd = p.getGpsDaemon ();
			assertNotNull (gpsd);
			gpsd.addNmea0183MessageListener (listener);
//			gpsd.run ();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}	
	}
	
	/**
	 * This method constructs a <code>GpsDaemonBuilder</code> and a
	 * Nmea0183MessageListener and runs the <code>GpsDaemonBuilder</code> to
	 * receive messages.
	 */
	public void testCase021 () {
		GpsDaemonBuilder p;
		Nmea0183MessageListener listener;
		try
		{
			Properties props = PropertyUtils.loadProperties (propsMock);
			listener = new MyMsgListener ();
			p = new GpsDaemonBuilder (props);
			gpsd = p.getGpsDaemon ();
			assertNotNull (gpsd);
			gpsd.addNmea0183MessageListener (listener);
//			gpsd.run ();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> which
	 * fails, because the provided property file does not exist.
	 */
	public void testCase03 () {

		// First ensure, that the property file really does not exist.
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (notAvailableProps);
		
		if (url != null)
		{
			File inputDataFile = new File (url.getFile ());
			assertFalse (inputDataFile.exists ());
		}
		
		// Now run the test.
		try
		{
			GpsDaemonBuilder p = new GpsDaemonBuilder (notAvailableProps);
			assertNull (p);
		}
		catch (FileNotFoundException e)
		{
			if (!e.getMessage ().equals (notAvailableProps))
			{
				fail ();
				e.printStackTrace ();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> which
	 * fails, because the provided property file is not properly edited.
	 */
	public void testCase04 () {

		try
		{
			GpsDaemonBuilder p = new GpsDaemonBuilder (deficientProps1);
			assertNull (p);
		}
		catch (ConfigurationException e)
		{
//			if (!e.getMessage ().equals ("Missing property rtcm.message.provider"))
			if (!e.getMessage ().equals ("Missing property gps.receiver"))
			{
				e.printStackTrace ();
				fail ();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> which
	 * fails, because the provided property file is not properly edited.
	 */
	public void testCase05 () {

		try
		{
			GpsDaemonBuilder p = new GpsDaemonBuilder (deficientProps2);
			assertNull (p);
		}
		catch (ConfigurationException e)
		{
//			if (!e.getMessage ().equals ("Missing property apos.mock.dgps2.className"))
			if (!e.getMessage ().equals ("Missing property gps.receiver"))
			{
				e.printStackTrace ();
				fail ();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> which
	 * fails, because the provided property file is not properly edited.
	 */
	public void testCase06 () {

		try
		{
			GpsDaemonBuilder p = new GpsDaemonBuilder (deficientProps3);
			assertNull (p);
		}
		catch (ConfigurationException e)
		{
//			if (!e.getCause ().getCause ().getMessage ().equals ("Property rtcm.data.file not (properly) set"))
			if (!e.getMessage ().equals ("Missing property gps.mock3.className"))
			{
				e.printStackTrace ();
				fail ();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> which
	 * fails, because the provided property file is not properly edited.
	 */
	public void testCase07 () {

		try
		{
			GpsDaemonBuilder p = new GpsDaemonBuilder (deficientProps4);
			assertNull (p);
		}
		catch (ConfigurationException e)
		{
			if (!e.getMessage ().equals ("Missing property gps.mock4.className"))
			{
				e.printStackTrace ();
				fail ();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> which
	 * fails, because the provided property file is not properly edited.
	 */
	public void testCase08 () {

		try
		{
			System.getProperties ().remove ("gps.receiver");
			GpsDaemonBuilder p = new GpsDaemonBuilder (deficientProps5);
			assertNull (p);
		}
		catch (ConfigurationException e)
		{
			if (!e.getMessage ().equals ("Missing property gps.receiver"))
			{
				e.printStackTrace ();
				fail ();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> without
	 * an RTCM provider.
	 */
	public void testCase09 () {

		try
		{
			System.getProperties ().remove ("rtcm.message.provider");
			GpsDaemonBuilder p = new GpsDaemonBuilder (goodProps2);
			assertNotNull (p);
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail();
		}	
	}
	
	/**
	 * This method tries to construct a <code>GpsDaemonBuilder</code> with
	 * an RTCM provider that is not configured properly.
	 */
	public void testCase10 () {

		try
		{
			System.getProperties ().remove ("apos.mock.dgps.className");
			GpsDaemonBuilder p = new GpsDaemonBuilder (deficientProps6);
			assertNull (p);
		}
		catch (ConfigurationException e)
		{
			if (!e.getMessage ().equals ("Missing property apos.mock.dgps.className"))
			{
				e.printStackTrace ();
				fail ();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail();
		}	
	}

	
	
	
	/**
	 * This class implements a simple receiver for NMEA 0183 messages.
	 */
	private class MyMsgListener implements Nmea0183MessageListener {

		public void receive (Nmea0183Message message) {
//			printGpsdStatus ();
//			System.out.print ("GpsDaemonBuilderTestCase.receive: ");
//			try { System.out.write (message.getBytes ()); } catch (IOException e) {}
//			System.out.flush ();
		}
	
//		public void printGpsdStatus () {
//			System.out.println (
//				"State: well formed messages=" + gpsd.getNumberOfWellFormedMessages () +
//				", malformed messages=" + gpsd.getNumberOfMalformedMessages () +
//				", sent RTCM messages=" + gpsd.getNumberOfSentRtcmMessages () +
//				", not sent RTCM messages=" + gpsd.getNumberOfNotSentRtcmMessages ()
//			);
//		}
	}

}
