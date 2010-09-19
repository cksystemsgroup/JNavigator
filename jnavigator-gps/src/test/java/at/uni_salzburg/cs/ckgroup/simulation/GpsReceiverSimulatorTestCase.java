/*
 * @(#) GpsReceiverSimulatorTestCase.java
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
package at.uni_salzburg.cs.ckgroup.simulation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Timer;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.course.DummyPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;

public class GpsReceiverSimulatorTestCase extends TestCase
{
	private static final String props = "at/uni_salzburg/cs/ckgroup/simulation/GpsReceiverSimulatorTest/gpssim.properties";
	private static final String data = "at/uni_salzburg/cs/ckgroup/simulation/GpsReceiverSimulatorTest/gpssim.dat";
	private Properties properties;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		URL propsUrl = Thread.currentThread ().getContextClassLoader ().getResource (props);
		
		File inputDataFile = new File (propsUrl.getFile ());
		properties = new Properties ();
		
		try {
			properties.load (new FileInputStream(inputDataFile));
			properties.setProperty (GpsReceiverSimulator.PROP_DATA_FILE_NAME, data);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
    /**
     * Read one line from an InputStream, including \r and \n.
     *
     * @param is the InputStream to be read
     * @param b a byte array to put the read line in
     * @return the number of read characters
     * @throws IOException thrown in case of errors
     */
    protected static int readLine (InputStream is, byte[] b) throws IOException {
        int count = 0;
        int ch;

        while ( b.length >= count && (ch = is.read ()) >= 0) {
            b[count++] = (byte) ch;
            if (ch == '\n')
                    break;
        }

        return count;
    }
	
	/**
	 * Test the <code>GpsReceiverSimulator</code> by employing a
	 * <code>Timer</code> that triggers the <code>run()</code> method of a
	 * simulator instance every 10ms. By instantiating
	 * <code>Nmea0183Message</code> objects, this test verifies that the
	 * messages are well-defined.
	 */
	public void testCase01 () {

		final int NUMBER_OF_MESSAGES = 2800;
		
		File dat = new File ("simulator.ubx");
		
		try {
			FileOutputStream datOut = new FileOutputStream (dat);
			
			GpsReceiverSimulator sim = new GpsReceiverSimulator (properties);
			DummyPositionProvider positionProvider = new DummyPositionProvider (properties);
			assertNotNull (sim);
			sim.setPositionProvider (null);
			sim.setGeodeticSystem (null);
			
			Timer timer = new Timer ();
			timer.schedule (sim, 1000, 10);
			
			OutputStream os = sim.getOutputStream ();
			os.write ("lala".getBytes ());
			InputStream is = sim.getInputStream ();
			int counter = NUMBER_OF_MESSAGES;			
			byte[] buf = new byte[1024];
			int len;
			
			while ( (len = readLine (is, buf)) > 0 && counter-- > 0) {
				Nmea0183Message m = new Nmea0183Message (buf, 0, len);
//				System.out.write (m.getBytes ());
				datOut.write (m.getBytes ());
				assertNotNull (m);
				if (counter == NUMBER_OF_MESSAGES-10)
					sim.setPositionProvider (positionProvider);
			}
			datOut.close ();
			sim.close ();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test if the constructor of <code>GpsReceiverSimulator</code> throws a
	 * <code>FileNotFoundException</code> if it can not find the set course
	 * data file.
	 */
	public void testCase02 () {
		
		final String missingFileName = data+".missing";
		
		try {
			properties.setProperty (GpsReceiverSimulator.PROP_DATA_FILE_NAME, missingFileName);
			GpsReceiverSimulator sim = new GpsReceiverSimulator (properties);
			assertNull (sim);
		} catch (FileNotFoundException e){
			assertEquals (missingFileName,e.getMessage ());
		} catch (Exception e) {			
			e.printStackTrace();			
			fail ();
		}
	}
	
	/**
	 * Test the implementation of the <code>convertAngleToString()</code>
	 * method of the <code>GpsReceiverSimulator</code> class. This method has
	 * to convert several positive and negative numbers to the correct Strings.
	 */
	public void testCase03 () {
		
		try {
			GpsReceiverSimulator sim = new GpsReceiverSimulator (properties);
			
			String a = sim.convertAngleToString ( 48.0);
			String b = sim.convertAngleToString (-48.0);
			String c = sim.convertAngleToString ( 13.0000000001);
			String d = sim.convertAngleToString (-13.0000000001);
			String e = sim.convertAngleToString ( 12.9999999999);
			String f = sim.convertAngleToString (-12.9999999999);
			String g = sim.convertAngleToString ( 12.99999999999);
			String h = sim.convertAngleToString (-12.99999999999);

			assertEquals ( "4800.00000000", a);
			assertEquals ("-4800.00000000", b);
			assertEquals ( "1300.00000001", c);
			assertEquals ("-1300.00000001", d);
			assertEquals ( "1259.99999999", e);
			assertEquals ("-1259.99999999", f);
			assertEquals ( "1300.00000000", g);
			assertEquals ("-1300.00000000", h);
			
		} catch (Exception e) {			
			e.printStackTrace();			
			fail ();
		}
	}

	/**
	 * Verify the implementation of the
	 * <code>GpsReceiverSimulator.calculateChecksum</code> static method.
	 */
	public void testCase04 () {
		char[] c;
		
		c = GpsReceiverSimulator.calculateChecksum ("$GPGGA,134307.00,4759.42598497,N,01256.20347770,E,2,06,1.3,435.47315,M,46.59872,M,1.2,0240".getBytes ());
		assertNotNull (c);
		assertEquals (3, c.length);
		assertEquals ('*', c[0]);
		assertEquals ('4', c[1]);
		assertEquals ('2', c[2]);

		c = GpsReceiverSimulator.calculateChecksum ("$GPGGA,134310.00,4759.42596032,N,01256.20363782,E,2,06,1.3,435.41377,M,46.59872,M,2.4,0240".getBytes ());
		assertNotNull (c);
		assertEquals (3, c.length);
		assertEquals ('*', c[0]);
		assertEquals ('4', c[1]);
		assertEquals ('D', c[2]);

		c = GpsReceiverSimulator.calculateChecksum ("$GPRMC,134311.00,A,4759.42598383,N,01256.20365708,E,0.1,15.1,161207,,,D".getBytes ());
		assertNotNull (c);
		assertEquals (3, c.length);
		assertEquals ('*', c[0]);
		assertEquals ('6', c[1]);
		assertEquals ('1', c[2]);
		
		c = GpsReceiverSimulator.calculateChecksum ("$GPGGA,134257.00,4759.42607660,N,01256.20334234,E,2,06,1.3,435.43073,M,46.59873,M,2.0,0240".getBytes ());
		assertNotNull (c);
		assertEquals (3, c.length);
		assertEquals ('*', c[0]);
		assertEquals ('4', c[1]);
		assertEquals ('F', c[2]);
	}
	
	/**
	 * Test the <code>GpsReceiverSimulator</code> by employing a
	 * <code>Timer</code> that triggers the <code>run()</code> method of a
	 * simulator instance every 10ms. By instantiating
	 * <code>Nmea0183Message</code> objects, this test verifies that the
	 * messages are well-defined. Disable the <code>getCourseOverGround()</code>
	 * Method in the <code>DummyPositionProvider</code>.
	 */
	public void testCase05 () {

		final int NUMBER_OF_MESSAGES = 2800;
		
		File dat = new File ("simulator.ubx");
		
		properties.setProperty(DummyPositionProvider.PROP_HAS_COURSE_OVER_GROUND, "false");
		
		try {
			FileOutputStream datOut = new FileOutputStream (dat);
			
			GpsReceiverSimulator sim = new GpsReceiverSimulator (properties);
			DummyPositionProvider positionProvider = new DummyPositionProvider (properties);
			assertNotNull (sim);
			sim.setPositionProvider (positionProvider);
			sim.setGeodeticSystem (new WGS84 ());
			
			Timer timer = new Timer ();
			timer.schedule (sim, 1000, 10);
			
			OutputStream os = sim.getOutputStream ();
			os.write ("lala".getBytes ());
			InputStream is = sim.getInputStream ();
			int counter = NUMBER_OF_MESSAGES;			
			byte[] buf = new byte[1024];
			int len;
			
			while ( (len = readLine (is, buf)) > 0 && counter-- > 0) {
				Nmea0183Message m = new Nmea0183Message (buf, 0, len);
//				System.out.write (m.getBytes ());
				datOut.write (m.getBytes ());
				assertNotNull (m);
//				if (counter == NUMBER_OF_MESSAGES-10)
//					sim.setPositionProvider (positionProvider);
			}
			timer.cancel();
			datOut.close ();
			sim.close ();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test the <code>GpsReceiverSimulator</code> by employing a
	 * <code>Timer</code> that triggers the <code>run()</code> method of a
	 * simulator instance every 10ms. By instantiating
	 * <code>Nmea0183Message</code> objects, this test verifies that the
	 * messages are well-defined. Disable the <code>getSpeedOverGround()</code>
	 * Method in the <code>DummyPositionProvider</code>.
	 */
	public void testCase06 () {

		final int NUMBER_OF_MESSAGES = 2800;
		
		File dat = new File ("simulator.ubx");
		
		properties.setProperty(DummyPositionProvider.PROP_HAS_SPEED_OVER_GROUND, "false");
		
		try {
			FileOutputStream datOut = new FileOutputStream (dat);
			
			GpsReceiverSimulator sim = new GpsReceiverSimulator (properties);
			DummyPositionProvider positionProvider = new DummyPositionProvider (properties);
			assertNotNull (sim);
			sim.setPositionProvider (positionProvider);
			sim.setGeodeticSystem (new WGS84 ());
			
			Timer timer = new Timer ();
			timer.schedule (sim, 1000, 10);
			
			OutputStream os = sim.getOutputStream ();
			os.write ("lala".getBytes ());
			InputStream is = sim.getInputStream ();
			int counter = NUMBER_OF_MESSAGES;			
			byte[] buf = new byte[1024];
			int len;
			
			while ( (len = readLine (is, buf)) > 0 && counter-- > 0) {
				Nmea0183Message m = new Nmea0183Message (buf, 0, len);
//				System.out.write (m.getBytes ());
				datOut.write (m.getBytes ());
				assertNotNull (m);
//				if (counter == NUMBER_OF_MESSAGES-10)
//					sim.setPositionProvider (positionProvider);
			}
			timer.cancel();
			datOut.close ();
			sim.close ();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
}
