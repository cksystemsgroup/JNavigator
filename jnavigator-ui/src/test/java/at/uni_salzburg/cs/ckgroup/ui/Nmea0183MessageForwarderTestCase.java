/*
 * @(#) Nmea0183MessageForwarderTestCase.java
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
package at.uni_salzburg.cs.ckgroup.ui;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MalformedMessageException;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;

/**
 * This tests verify the correct implementation of the
 * <code>Nmea0183MessageForwarder</code> class.
 * 
 * @author Clemens Krainer
 */
public class Nmea0183MessageForwarderTestCase extends TestCase {

	Nmea0183MessageForwarder forwarder;
	AltitudeView altitudeView;
	TimeView timeView;
	CourseView courseView;
	SpeedView speedView;
	CoordinateView coordinateView;
	SatelliteView satelliteView;
		
	public void setUp () {
		forwarder = new Nmea0183MessageForwarder ();
		forwarder.addView (altitudeView = new AltitudeView ());
		forwarder.addView (timeView = new TimeView ());
		forwarder.addView (courseView = new CourseView ());
		forwarder.addView (speedView = new SpeedView ());
		forwarder.addView (coordinateView = new CoordinateView ());
		forwarder.addView (satelliteView = new SatelliteView ());
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPGGA message.
	 */
	public void testCase010 () {
		byte[] messageString = "$GPGGA,173648.6,4800.00024840,N,1300.00033156,E,1,07,1.0,439.45593,M,46.5987,M,1.9,0120*71\r\n".getBytes();

		Calendar cal = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		cal.setTime (new Date());
		cal.set (Calendar.HOUR_OF_DAY, 17);
		cal.set (Calendar.MINUTE, 36);
		cal.set (Calendar.SECOND, 48);
		cal.set (Calendar.MILLISECOND, 600);
		long expected = cal.getTimeInMillis ();

		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// AltitudeView
			assertEquals (439.45593, altitudeView.altitude, 1E-9);

			cal.setTime (altitudeView.date);
			long result = cal.getTimeInMillis ();
			assertEquals ("Date in altitude view", expected, result);

			// CoordinateView
			assertEquals ("latitude",  48.00000414,  coordinateView.coordinate.latitude,  1E-9);
			assertEquals ("longitude", 13.000005526, coordinateView.coordinate.longitude, 1E-9);
			assertEquals ("altitude",  439.45593,    coordinateView.coordinate.altitude,  1E-9);
			
			cal.setTime (coordinateView.date);
			result = cal.getTimeInMillis ();
			assertEquals ("Date in coordinate view", expected, result);
			
			// TimeView
			cal.setTime (timeView.dateTime);
			result = cal.getTimeInMillis ();
			assertEquals ("Date in time view", expected, result);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPGGA message.
	 */
	public void testCase011 () {
		byte[] messageString = "$GPGGA,173648.640,4800.00024840,S,1300.00033156,W,1,07,1.0,439.45593,M,46.5987,M,1.9,0120*7A\r\n".getBytes();

		Calendar cal = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		cal.setTime (new Date());
		cal.set (Calendar.HOUR_OF_DAY, 17);
		cal.set (Calendar.MINUTE, 36);
		cal.set (Calendar.SECOND, 48);
		cal.set (Calendar.MILLISECOND, 640);
		long expected = cal.getTimeInMillis ();

		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// AltitudeView
			assertEquals (439.45593, altitudeView.altitude, 1E-9);

			cal.setTime (altitudeView.date);
			long result = cal.getTimeInMillis ();
			assertEquals ("Date in altitude view", expected, result);

			// CoordinateView
			assertEquals ("latitude",  -48.00000414,  coordinateView.coordinate.latitude,  1E-9);
			assertEquals ("longitude", -13.000005526, coordinateView.coordinate.longitude, 1E-9);
			assertEquals ("altitude",   439.45593,    coordinateView.coordinate.altitude,  1E-9);
			
			cal.setTime (coordinateView.date);
			result = cal.getTimeInMillis ();
			assertEquals ("Date in coordinate view", expected, result);
			
			// TimeView
			cal.setTime (timeView.dateTime);
			result = cal.getTimeInMillis ();
			assertEquals ("Date in time view", expected, result);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it
	 * receives a malformed $GPGGA message. The time field is corrupt, so
	 * <code>receive()</code> can not update the time information.
	 */
	public void testCase012 () {
		byte[] messageString = "$GPGGA,17364.640,4800.00024840,S,1300.00033156,W,1,07,1.0,439.45593,M,46.5987,M,1.9,0120*42\r\n".getBytes();

		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// AltitudeView
			assertEquals (439.45593, altitudeView.altitude, 1E-9);
			assertNull (altitudeView.date);

			// CoordinateView
			assertEquals ("latitude",  -48.00000414,  coordinateView.coordinate.latitude,  1E-9);
			assertEquals ("longitude", -13.000005526, coordinateView.coordinate.longitude, 1E-9);
			assertEquals ("altitude",   439.45593,    coordinateView.coordinate.altitude,  1E-9);
			
			assertNull (coordinateView.date);
			
			// TimeView
			assertNull (timeView.dateTime);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Verify the implementation of the <code>receive()</code> method when it
	 * receives a $GPGGA message. The values for latitude and longitude are
	 * missing, so <code>receive()</code> can not update the position and date
	 * values. Only the time views are updated, because the time field is OK.
	 */
	public void testCase013 () {
		byte[] messageString = "$GPGGA,173648.64,,,,,1,07,1.0,439.45593,M,46.5987,M,1.9,0120*48\r\n".getBytes();

		Calendar cal = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		cal.setTime (new Date());
		cal.set (Calendar.HOUR_OF_DAY, 17);
		cal.set (Calendar.MINUTE, 36);
		cal.set (Calendar.SECOND, 48);
		cal.set (Calendar.MILLISECOND, 640);
		long expected = cal.getTimeInMillis ();

		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// AltitudeView
			assertEquals (0, altitudeView.altitude, 1E-9);
			assertNull (altitudeView.date);

			// CoordinateView
			assertNull (coordinateView.coordinate);
			assertNull (coordinateView.date);
			
			// TimeView
			cal.setTime (timeView.dateTime);
			long result = cal.getTimeInMillis ();
			assertEquals ("Date in time view", expected, result);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Verify the implementation of the <code>receive()</code> method when it
	 * receives a malformed $GPGGA message. The latitude field is corrupt, so
	 * <code>receive()</code> can only update the time information and no
	 * position values.
	 */
	public void testCase014 () {
		byte[] messageString = "$GPGGA,173648.6,480a.00024840,N,1300.00033156,E,1,07,1.0,439.45593,M,46.5987,M,1.9,0120*20\r\n".getBytes();

		Calendar cal = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		cal.setTime (new Date());
		cal.set (Calendar.HOUR_OF_DAY, 17);
		cal.set (Calendar.MINUTE, 36);
		cal.set (Calendar.SECOND, 48);
		cal.set (Calendar.MILLISECOND, 600);
		long expected = cal.getTimeInMillis ();

		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// AltitudeView
			assertEquals (0, altitudeView.altitude, 1E-9);

			assertNull (altitudeView.date);

			// CoordinateView
			assertNull (coordinateView.coordinate);
			assertNull (coordinateView.date);
			
			// TimeView
			cal.setTime (timeView.dateTime);
			long result = cal.getTimeInMillis ();
			assertEquals ("Date in time view", expected, result);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPVTG message.
	 */
	public void testCase020 () {
		byte[] messageString = "$GPVTG,258.75,T,,M,0.93,N,1.73,K,A*3F\r\n".getBytes();
		
		INavigatorView[] views = { altitudeView, timeView, courseView, speedView, coordinateView, satelliteView };
		forwarder = new Nmea0183MessageForwarder ();
		forwarder.addViews (views);
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// CourseView
			assertEquals (258.75, courseView.course, 1E-9);
			
			// SpeedView
			assertEquals (1.73, speedView.speed, 1E-9);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it
	 * receives a $GPVTG message. The course field is empty, so no course view
	 * and no speed view gets an update.
	 */
	public void testCase021 () {
		byte[] messageString = "$GPVTG,,T,,M,0.93,N,1.73,K,A*2C\r\n".getBytes();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// CourseView
			assertEquals (0, courseView.course, 1E-9);
			
			// SpeedView
			assertEquals (0, speedView.speed, 1E-9);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it
	 * receives a $GPVTG message. The course field has a malformed value, so no
	 * course view and no speed view gets an update.
	 */
	public void testCase022 () {
		byte[] messageString = "$GPVTG,258a75,T,,M,0.93,N,1.73,K,A*70\r\n".getBytes();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// CourseView
			assertEquals (0, courseView.course, 1E-9);
			
			// SpeedView
			assertEquals (0, speedView.speed, 1E-9);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPZDA message.
	 */
	public void testCase030 () {
		byte[] messageString1 = "$GPZDA,163908.068,15,02,2008,00,00*51\r\n".getBytes();
		byte[] messageString2 = "$GPZDA,163909.068,15,02,2008,00,00*50\r\n".getBytes();

		Calendar cal = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		cal.set (Calendar.YEAR, 2008);
		cal.set (Calendar.MONTH, 1);
		cal.set (Calendar.DAY_OF_MONTH, 15);
		cal.set (Calendar.HOUR_OF_DAY, 16);
		cal.set (Calendar.MINUTE, 39);
		cal.set (Calendar.SECOND,  8);
		cal.set (Calendar.MILLISECOND, 68);
		long expected = cal.getTimeInMillis ();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString1);
			forwarder.receive (message);
			
			// TimeView
			cal.setTime (timeView.dateTime);
			long result = cal.getTimeInMillis ();
			assertEquals ("Date in time view", expected, result);
			
			message = new Nmea0183Message (messageString2);
			forwarder.receive (message);
			// TimeView
			cal.setTime (timeView.dateTime);
			result = cal.getTimeInMillis ();
			assertEquals ("Date in time view", expected+1000, result);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it
	 * receives a $GPZDA message. The year field has a malformed value, so no
	 * time view gets an update.
	 */
	public void testCase031 () {
		byte[] messageString = "$GPZDA,163908.068,15,02,20a8,00,00*00\r\n".getBytes();

		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
			// TimeView
			assertNull (timeView.dateTime);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPGLL message.
	 */
	public void testCase04 () {
		byte[] messageString = "$GPGLL,4759.43165,N,01256.21184,E,145202.00,A,A*6D\r\n".getBytes();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPGSA message.
	 */
	public void testCase05 () {
		byte[] messageString = "$GPGSA,A,2,13,27,04,,,,,,,,,,4.08,3.95,1.00*02\r\n".getBytes();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPGSV message.
	 */
	public void testCase06 () {
		byte[] messageString = "$GPGSV,3,1,11,16,13,039,,20,04,128,,06,00,338,,13,66,052,46*70\r\n".getBytes();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a $GPRMC message.
	 */
	public void testCase07 () {
		byte[] messageString = "$GPRMC,145202.00,A,4759.43165,N,01256.21184,E,0.007,,220407,,,A*70\r\n".getBytes();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the implementation of the <code>receive()</code> method when it receives a (non existing) $GPRMD message.
	 */
	public void testCase08 () {
		byte[] messageString = "$GPRMD,145202.00,A,4759.43165,N,01256.21184,E,0.007,,220407,,,A*77\r\n".getBytes();
		
		try {
			Nmea0183Message message = new Nmea0183Message (messageString);
			forwarder.receive (message);
			
		} catch (Nmea0183MalformedMessageException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	
	
	
	
	private class AltitudeView implements IAltitudeView {
		public Date date = null;
		public double altitude = 0;
		
		public void setAltitude(Date date, double altitude) {
			this.date = date;
			this.altitude = altitude;
		}

		public void invalidate() {
			date = null;
			altitude = 0;
		}
	}
	
	private class TimeView implements ITimeView {
		public Date dateTime;
		
		public void setDateTime(Date dateTime) {
			this.dateTime = dateTime;
		}

		public void invalidate() {
			dateTime = null;
		}
	}
	
	private class CourseView implements ICourseView {
		// public Date date = null;
		public double course = 0;
		
		public void setCourse(Date date, double course) {
		     // this.date = date;
			this.course = course;
		}

		public void invalidate() {
		     // date = null;
			course = 0;
		}
	}
	
	private class SpeedView implements ISpeedView {
		// public Date date = null;
		public double speed = 0;
		
		public void setSpeed(Date date, double speed) {
			// this.date = date;
			this.speed = speed;
		}

		public void invalidate() {
			// date = null;
			speed = 0;
		}
	}
	
	private class CoordinateView implements ICoordinateView {
		public Date date = null;
		public PolarCoordinate coordinate = null;
		
		public void setCoordinate(Date date, PolarCoordinate coordinate) {
			this.date = date;
			this.coordinate = coordinate;
		}

		public void invalidate() {
			date = null;
			coordinate = null;
		}
	}
	
	private class SatelliteView implements ISatelliteView {
//		public Date date = null;
//		public int satellitesInView = 0;
//		public int satelliteId = 0;
//		public int elevation = 0;
//		public int azimut = 0;
//		public int snr = 0;
		
		public void setSatellite(Date date, int satellitesInView, 	int satelliteId, int elevation, int azimut, int snr) {
//			this.date = date;
//			this.satellitesInView = satellitesInView;
//			this.satelliteId = satelliteId;
//			this.elevation = elevation;
//			this.azimut = azimut;
//			this.snr = snr;
		}

		public void invalidate() {
//			date = null;
//			satellitesInView = 0;
//			satelliteId = 0;
//			elevation = 0;
//			azimut = 0;
//			snr = 0;
		}
	}
}
