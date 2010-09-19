/*
 * @(#) GpsPositionProviderTestCase.java
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

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.gps.GpsPositionProvider;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MalformedMessageException;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import junit.framework.TestCase;

public class GpsPositionProviderTestCase extends TestCase
{
	/**
	 * This test case verifies the <code>receive()</code> and the
	 * <code>getCurrentPosition()</code> methods with $GPGGA messages only.
	 */
	public void testCase01 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		PolarCoordinate c;
		String msg;
		
		try
		{
			msg = "$GPGGA,134320.00,4759.42607290,N,01256.20326407,E,2,06,1.3,435.49942,M,46.59873,M,2.2,0240*43\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99043454833333,   c.latitude,  1E-9);
			assertEquals (12.936721067833334,  c.longitude, 1E-9);
			assertEquals (435.49942,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134321.00,4759.42606525,S,01256.20321732,E,2,06,1.3,435.42723,M,46.59873,M,2.0,0240*55\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (-47.99043442083333,  c.latitude,  1E-9);
			assertEquals (12.936720288666667,  c.longitude, 1E-9);
			assertEquals (435.42723,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134322.00,4759.42604600,N,01256.20312684,W,2,06,1.3,435.56408,M,46.59873,M,1.2,0240*5D\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99043409999999,   c.latitude,  1E-9);
			assertEquals (-12.936718780666666, c.longitude, 1E-9);
			assertEquals (435.56408,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134323.00,4759.42549549,S,01256.20334815,W,2,06,1.3,436.20764,M,46.59872,M,1.0,0240*4F\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (-47.99042492483333,  c.latitude,  1E-9);
			assertEquals (-12.936722469166668, c.longitude, 1E-9);
			assertEquals (436.20764,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134324.00,4759.42490876,N,01256.20276330,E,2,06,1.3,437.28062,M,46.59873,M,2.0,0240*42\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.990415146,        c.latitude,  1E-9);
			assertEquals (12.936712721666666,  c.longitude, 1E-9);
			assertEquals (437.28062,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134325.00,4759.42420023,S,01256.20256304,E,2,06,1.3,437.78413,M,46.59873,M,2.4,0240*5B\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (-47.99040333716667,  c.latitude,  1E-9);
			assertEquals (12.936709384000002,  c.longitude, 1E-9);
			assertEquals (437.78413,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134326.00,4759.42368899,N,01256.20254332,W,2,06,1.3,437.26227,M,46.59874,M,2.2,0240*59\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99039481650001,   c.latitude,  1E-9);
			assertEquals (-12.936709055333331, c.longitude, 1E-9);
			assertEquals (437.26227,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134327.00,4759.42309682,S,01256.20269880,W,2,06,1.3,436.73688,M,46.59873,M,1.4,0240*48\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (-47.990384947,       c.latitude,  1E-9);
			assertEquals (-12.936711646666666, c.longitude, 1E-9);
			assertEquals (436.73688,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134328.00,4759.42259026,N,01256.20299702,E,2,06,1.3,437.21651,M,46.59872,M,2.4,0240*4E\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.990376504333334,  c.latitude,  1E-9);
			assertEquals (12.936716617000002,  c.longitude, 1E-9);
			assertEquals (437.21651,           c.altitude,  1E-9);
			
			msg = "$GPGGA,134323.00,4759.42549549,S,01256.20334815,E,2,06,1.3,436.20764,M,46.59872,M,1.0,0240*5D\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (-47.99042492483333,  c.latitude,  1E-9);
			assertEquals (12.936722469166668,  c.longitude, 1E-9);
			assertEquals (436.20764,           c.altitude,  1E-9);
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	/**
	 * This test case verifies the <code>receive()</code> and the
	 * <code>getCurrentPosition()</code> methods with several different NMEA
	 * 0183 message types.
	 */
	public void testCase02 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		PolarCoordinate c;
		String msg;
		
		try
		{
			msg = "$GPGGA,134320.00,4759.42607290,N,01256.20326407,E,2,06,1.3,435.49942,M,46.59873,M,2.2,0240*43\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99043454833333,   c.latitude,  1E-9);
			assertEquals (12.936721067833334,  c.longitude, 1E-9);
			assertEquals (435.49942,           c.altitude,  1E-9);
			
			
			msg = "$GPRMC,134321.00,A,4759.42606525,N,01256.20321732,E,0.1,15.1,161207,,,D*65\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99043442083333,   c.latitude,  1E-9);
			assertEquals (12.936720288666667,  c.longitude, 1E-9);
			assertEquals (435.49942,           c.altitude,  1E-9);

			msg = "$GPGSV,2,1,07,03,70,294,43,16,42,197,39,18,50,077,44,19,38,297,42*70\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99043442083333,   c.latitude,  1E-9);
			assertEquals (12.936720288666667,  c.longitude, 1E-9);
			assertEquals (435.49942,           c.altitude,  1E-9);

			msg = "$GPGSV,2,2,07,21,29,066,35,22,53,148,45,32,33,175,38*43\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99043442083333,   c.latitude,  1E-9);
			assertEquals (12.936720288666667,  c.longitude, 1E-9);
			assertEquals (435.49942,           c.altitude,  1E-9);

			msg = "$GPRMC,134326.00,A,4759.42368899,N,01256.20254332,E,1.7,15.1,161207,,,D*65\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertEquals (47.99039481650001,   c.latitude,  1E-9);
			assertEquals (12.936709055333331,  c.longitude, 1E-9);
			assertEquals (435.49942,           c.altitude,  1E-9);			
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	/**
	 * This test case verifies the <code>receive()</code> and the
	 * <code>getCurrentPosition()</code> methods a null NMEA 0183 message.
	 */
	public void testCase03 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		PolarCoordinate c;
		String msg;
		
		try
		{
			msg = "$GPGGA,134320.00,4759.42607290,N,01256.20326407,E,2,06,1.3,435.49942,M,46.59873,M,2.2,0240*43\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertNotNull (c);
			assertEquals (47.99043454833333,   c.latitude,  1E-9);
			assertEquals (12.936721067833334,  c.longitude, 1E-9);
			assertEquals (435.49942,           c.altitude,  1E-9);

			p.receive (null);
			c = p.getCurrentPosition ();
			assertNull (c);
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	/**
	 * This test case verifies the angleStringToValue() method for positive angles.
	 */
	public void testCase04 ()
	{		
		double a = GpsPositionProvider.angleStringToValue ("4759.42607290");
		double b = GpsPositionProvider.angleStringToValue ("4759.42606525");
		double c = GpsPositionProvider.angleStringToValue ("4759.42368899");
		double d = GpsPositionProvider.angleStringToValue ("4759.99999999");
		double e = GpsPositionProvider.angleStringToValue ("0000.00000000");
		double f = GpsPositionProvider.angleStringToValue ("1111.11111111");
		double g = GpsPositionProvider.angleStringToValue ("1160.00000000");
		double h = GpsPositionProvider.angleStringToValue ("9999.99999999");
		
		assertEquals (47.99043454833333, a, 1E-9);
		assertEquals (47.99043442083333, b, 1E-9);
		assertEquals (47.99039481650001, c, 1E-9);
		assertEquals (47.999999999833335,d, 1E-9);
		assertEquals (0.0,               e, 1E-9);
		assertEquals (11.185185185166667,f, 1E-9);
		assertEquals (12.0,              g, 1E-9);
		assertEquals (100.66666666649999,h, 1E-9);		
	}
	
	/**
	 * This test case verifies the angleStringToValue() method for negative angles.
	 */
	public void testCase05 ()
	{		
		double a = GpsPositionProvider.angleStringToValue ("-4759.42607290");
		double b = GpsPositionProvider.angleStringToValue ("-4759.42606525");
		double c = GpsPositionProvider.angleStringToValue ("-4759.42368899");
		double d = GpsPositionProvider.angleStringToValue ("-4759.99999999");
		double e = GpsPositionProvider.angleStringToValue ("-0000.00000000");
		double f = GpsPositionProvider.angleStringToValue ("-1111.11111111");
		double g = GpsPositionProvider.angleStringToValue ("-1160.00000000");
		double h = GpsPositionProvider.angleStringToValue ("-9999.99999999");
		
		assertEquals (-47.99043454833333, a, 1E-9);
		assertEquals (-47.99043442083333, b, 1E-9);
		assertEquals (-47.99039481650001, c, 1E-9);
		assertEquals (-47.999999999833335,d, 1E-9);
		assertEquals (-0.0,               e, 1E-9);
		assertEquals (-11.185185185166667,f, 1E-9);
		assertEquals (-12.0,              g, 1E-9);
		assertEquals (-100.66666666649999,h, 1E-9);		
	}
	
	/**
	 * Test the behavior of the <code>GpsPositionProvider</code>
	 * implementation when receiving "$GPRMC" NMEA 0183 messages.
	 */
	public void testCase06 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		Double speed;
		Double course;
		String msg;
		int k;
		
		try
		{
			assertNull (p.getCourseOverGround());
			assertNull (p.getSpeedOverGround());
			
			msg = "$GPRMC,215136.00,A,4759.43142,N,01256.20407,E,0.586,345.60,150407,,,A*6A\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));

			speed = p.getSpeedOverGround();
			assertNotNull (speed);
			assertEquals (0.586*1.852, speed.doubleValue(), 1E-9);

			course = p.getCourseOverGround();
			assertNotNull (course);
			assertEquals (345.60, course.doubleValue(), 1E-9);
			
			// Test nullification of the course value
			msg = "$GPRMC,215136.00,A,4759.43142,N,01256.20407,E,0.586,,150407,,,A*70\r\n";
			for (k=0; k < GpsPositionProvider.MAX_MISSING_DATA_COUNTER; k++)
			{
				p.receive (new Nmea0183Message (msg.getBytes ()));
				speed = p.getSpeedOverGround();
				assertNotNull (speed);
				assertEquals (0.586*1.852, speed.doubleValue(), 1E-9);
	
				course = p.getCourseOverGround();
				assertNotNull (course);
				assertEquals (345.60, course.doubleValue(), 1E-9);
			}

			p.receive (new Nmea0183Message (msg.getBytes ()));
			speed = p.getSpeedOverGround();
			assertNotNull (speed);
			assertEquals (0.586*1.852, speed.doubleValue(), 1E-9);

			course = p.getCourseOverGround();
			assertNull (course);
			
			// Test complete course data again.
			msg = "$GPRMC,215136.00,A,4759.43142,N,01256.20407,E,0.586,345.60,150407,,,A*6A\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));

			speed = p.getSpeedOverGround();
			assertNotNull (speed);
			assertEquals (0.586*1.852, speed.doubleValue(), 1E-9);

			course = p.getCourseOverGround();
			assertNotNull (course);
			assertEquals (345.60, course.doubleValue(), 1E-9);
			
			// Test nullification of the speed value
			msg = "$GPRMC,215136.00,A,4759.43142,N,01256.20407,E,,345.60,150407,,,A*4F\r\n";
			for (k=0; k < GpsPositionProvider.MAX_MISSING_DATA_COUNTER; k++)
			{
				p.receive (new Nmea0183Message (msg.getBytes ()));
				speed = p.getSpeedOverGround();
				assertNotNull (speed);
				assertEquals (0.586*1.852, speed.doubleValue(), 1E-9);
	
				course = p.getCourseOverGround();
				assertNotNull (course);
				assertEquals (345.60, course.doubleValue(), 1E-9);
			}
			
			p.receive (new Nmea0183Message (msg.getBytes ()));
			speed = p.getSpeedOverGround();
			assertNull (speed);

			course = p.getCourseOverGround();
			assertNotNull (course);
			assertEquals (345.60, course.doubleValue(), 1E-9);
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * Test the behavior of the <code>GpsPositionProvider</code>
	 * implementation when receiving "$GPVTG" NMEA 0183 messages.
	 */
	public void testCase07 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		Double speed;
		Double course;
		String msg;
		int k;
		
		try
		{
			assertNull (p.getCourseOverGround());
			assertNull (p.getSpeedOverGround());
			
			msg = "$GPVTG,316.66,T,,M,0.797,N,1.478,K,A*3A\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));

			speed = p.getSpeedOverGround();
			assertNotNull (speed);
			assertEquals (0.797*1.852, speed.doubleValue(), 1E-9);

			course = p.getCourseOverGround();
			assertNotNull (course);
			assertEquals (316.66, course.doubleValue(), 1E-9);
			
			// Test nullification of the course value
			msg = "$GPVTG,,T,,M,0.797,N,1.478,K,A*20\r\n";
			for (k=0; k < GpsPositionProvider.MAX_MISSING_DATA_COUNTER; k++)
			{
				p.receive (new Nmea0183Message (msg.getBytes ()));
				speed = p.getSpeedOverGround();
				assertNotNull (speed);
				assertEquals (0.797*1.852, speed.doubleValue(), 1E-9);
	
				course = p.getCourseOverGround();
				assertNotNull (course);
				assertEquals (316.66, course.doubleValue(), 1E-9);
			}

			p.receive (new Nmea0183Message (msg.getBytes ()));
			speed = p.getSpeedOverGround();
			assertNotNull (speed);
			assertEquals (0.797*1.852, speed.doubleValue(), 1E-9);

			course = p.getCourseOverGround();
			assertNull (course);
			
			// Test complete course data again.
			msg = "$GPVTG,316.66,T,,M,0.797,N,1.478,K,A*3A\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));

			speed = p.getSpeedOverGround();
			assertNotNull (speed);
			assertEquals (0.797*1.852, speed.doubleValue(), 1E-9);

			course = p.getCourseOverGround();
			assertNotNull (course);
			assertEquals (316.66, course.doubleValue(), 1E-9);
			
			// Test nullification of the speed value
			msg = "$GPVTG,316.66,T,,M,,N,1.478,K,A*1D\r\n";
			for (k=0; k < GpsPositionProvider.MAX_MISSING_DATA_COUNTER; k++)
			{
				p.receive (new Nmea0183Message (msg.getBytes ()));
				speed = p.getSpeedOverGround();
				assertNotNull (speed);
				assertEquals (0.797*1.852, speed.doubleValue(), 1E-9);
	
				course = p.getCourseOverGround();
				assertNotNull (course);
				assertEquals (316.66, course.doubleValue(), 1E-9);
			}
			
			p.receive (new Nmea0183Message (msg.getBytes ()));
			speed = p.getSpeedOverGround();
			assertNull (speed);

			course = p.getCourseOverGround();
			assertNotNull (course);
			assertEquals (316.66, course.doubleValue(), 1E-9);
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test case verifies the <code>receive()</code>, the
	 * <code>getCurrentPosition()</code>, the
	 * <code>getSpeedOverGround()</code> and the
	 * <code>getCourseOverGround</code> methods employing "$GPRMC" messages.
	 */
	public void testCase08 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		PolarCoordinate c;
		String msg;
		
		try
		{
			msg = "$GPRMC,133736.00,A,4759.42641081,N,01256.20282520,E,0.1,15.1,161207,,,D*61\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertNotNull (c);
			assertEquals (47.990440180166665, c.latitude,  1E-9);
			assertEquals (12.936713753333333, c.longitude, 1E-9);
			
			Double speedOverGround = p.getSpeedOverGround();
			assertNotNull (speedOverGround);
			assertEquals (0.1852, speedOverGround.doubleValue(), 1E-9);
			
			Double courseOverGround = p.getCourseOverGround();
			assertNotNull (courseOverGround);
			assertEquals (15.1, courseOverGround.doubleValue(), 1E-9);
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test case verifies the <code>receive()</code>, the
	 * <code>getCurrentPosition()</code>, the
	 * <code>getSpeedOverGround()</code> and the
	 * <code>getCourseOverGround</code> methods employing "$GPRMC" messages.
	 */
	public void testCase09 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		PolarCoordinate c;
		String msg;
		
		try
		{
			msg = "$GPRMC,133736.00,A,4759.42641081,N,01256.20282520,E,,15.1,161207,,,D*4E\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertNotNull (c);
			assertEquals (47.990440180166665, c.latitude,  1E-9);
			assertEquals (12.936713753333333, c.longitude, 1E-9);
			
			Double speedOverGround = p.getSpeedOverGround();
			assertNull (speedOverGround);
			
			Double courseOverGround = p.getCourseOverGround();
			assertNotNull (courseOverGround);
			assertEquals (15.1, courseOverGround.doubleValue(), 1E-9);
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test case verifies the <code>receive()</code>, the
	 * <code>getCurrentPosition()</code>, the
	 * <code>getSpeedOverGround()</code> and the
	 * <code>getCourseOverGround</code> methods employing "$GPRMC" messages.
	 */
	public void testCase10 ()
	{
		GpsPositionProvider p = new GpsPositionProvider ();
		assertNotNull (p);
		PolarCoordinate c;
		String msg;
		
		try
		{
			msg = "$GPRMC,133736.00,A,4759.42641081,N,01256.20282520,E,0.1,,161207,,,D*7A\r\n";
			p.receive (new Nmea0183Message (msg.getBytes ()));
			c = p.getCurrentPosition ();
			assertNotNull (c);
			assertEquals (47.990440180166665, c.latitude,  1E-9);
			assertEquals (12.936713753333333, c.longitude, 1E-9);
			
			Double speedOverGround = p.getSpeedOverGround();
			assertNotNull (speedOverGround);
			assertEquals (0.1852, speedOverGround.doubleValue(), 1E-9);
			
			Double courseOverGround = p.getCourseOverGround();
			assertNull (courseOverGround);
		}
		catch (Nmea0183MalformedMessageException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
}
