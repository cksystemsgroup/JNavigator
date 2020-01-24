/*
 * @(#) Nmea0183MessageForwarder.java
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
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;

/**
 * This class implements a <code>Nmea0183MessageForwarder</code> to forward
 * NMEA 0183 messages from the GPS receiver to arbitrary views providing the
 * interface <code>INavigatorView</code>.
 * 
 * @author Clemens Krainer
 */
public class Nmea0183MessageForwarder implements Nmea0183MessageListener
{
	private Vector<IAltitudeView> altitudeViews = new Vector<>();
	private Vector<ITimeView> timeViews = new Vector<>();
	private Vector<ICourseView> courseViews = new Vector<>();
	private Vector<ISpeedView> speedViews = new Vector<>();
	private Vector<ICoordinateView> coordinateViews = new Vector<>();
	private Vector<ISatelliteView> satelliteViews = new Vector<>();

	private Calendar calendar;
	private Date currentDate;
	
	/**
	 * Add some new views to be refreshed.
	 * 
	 * @param views the views to be refreshed.
	 */
	public void addViews (INavigatorView[] views)
	{
		for (int k=0; k < views.length; k++)
			addView (views[k]);
	}
	
	/**
	 * Add a single view to be refreshed.
	 * 
	 * @param view the view to be refreshed.
	 */
	public void addView (INavigatorView view)
	{
		if (view instanceof IAltitudeView)
			altitudeViews.add ((IAltitudeView)view);
		
		if (view instanceof ITimeView)
			timeViews.add ((ITimeView)view);
		
		if (view instanceof ICourseView)
			courseViews.add ((ICourseView)view);
		
		if (view instanceof ISpeedView)
			speedViews.add ((ISpeedView)view);
		
		if (view instanceof ICoordinateView)
			coordinateViews.add ((ICoordinateView)view);
		
		if (view instanceof ISatelliteView)
			satelliteViews.add ((ISatelliteView)view);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener#receive(at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message)
	 */
	public void receive (Nmea0183Message message)
	{
		String x = new String (message.getBytes ());
		String[] parts = x.split ("[,*]");
		
		if (parts[0].equals ("$GPGGA"))
			parseNmea0183MessageGPGGA (parts);
		
		else if (parts[0].equals ("$GPGLL"))
			parseNmea0183MessageGPGLL (parts);
		
		else if (parts[0].equals ("$GPGSA"))
			parseNmea0183MessageGPGSA (parts);
		
		else if (parts[0].equals ("$GPGSV"))
			parseNmea0183MessageGPGSV (parts);
		
		else if (parts[0].equals ("$GPRMC"))
			parseNmea0183MessageGPRMC (parts);
		
		else if (parts[0].equals ("$GPVTG"))
			parseNmea0183MessageGPVTG (parts);
		
		else if (parts[0].equals ("$GPZDA"))
			parseNmea0183MessageGPZDA (parts);
		
		else 
			System.err.println ("Unknown message. Input String was: '" + message + "'");
	}

	
	/**
	 * Parse a $GPGGA NMEA 0183 message.
	 * 
	 * @param message the message in pieces.
	 */
	public void parseNmea0183MessageGPGGA (String[] message)
	{
		parseTime (message[1]);
		
		for (int k=0; k < timeViews.size (); k++)
			((ITimeView)timeViews.get (k)).setDateTime (currentDate);
		
		if (message[2].equals (""))
			return;
		
		PolarCoordinate coordinate = null;
		double altitude = 0;
		try {
			// $GPGGA,173648.640,4800.00024840,N,1300.00033156,E,1,07,1.0,439.45593,M,46.5987,M,1.9,0120*75
			double latitude = message[3].equals ("N") ? parseAngle (message[2]) : -parseAngle (message[2]);
			double longitude = message[5].equals ("E") ? parseAngle (message[4]) : -parseAngle (message[4]);
			altitude = Double.parseDouble (message[9]);
			coordinate = new PolarCoordinate (latitude, longitude, altitude);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println ("Input String was: '" + message + "'");
			return;
		} catch (ArrayIndexOutOfBoundsException e2) {
			e2.printStackTrace();
			System.err.println ("Input String was: '" + message + "'");
			return;
		} catch (StringIndexOutOfBoundsException e3) {
			e3.printStackTrace();
			System.err.println ("Input String was: '" + message + "'");
			return;
		}
		
		for (int k=0; k < coordinateViews.size (); k++)
			((ICoordinateView)coordinateViews.get (k)).setCoordinate (currentDate, coordinate);
		
		for (int k=0; k < altitudeViews.size (); k++)
			((IAltitudeView)altitudeViews.get (k)).setAltitude (currentDate, altitude);
	}

	/**
	 * Parse a $GPGLL NMEA 0183 message.
	 * 
	 * @param message the message in pieces.
	 */
	public void parseNmea0183MessageGPGLL (String[] message)
	{
//		System.out.println ("Not implemented: Nmea0183MessageForwarder.parseNmea0183MessageGPGLL");
	}

	/**
	 * Parse a $GPGSA NMEA 0183 message.
	 * 
	 * @param message the message in pieces.
	 */
	public void parseNmea0183MessageGPGSA (String[] message)
	{
//		System.out.println ("Not implemented: Nmea0183MessageForwarder.parseNmea0183MessageGPGSA");
	}

	/**
	 * Parse a $GPGSV NMEA 0183 message.
	 * 
	 * @param message the message in pieces.
	 */
	public void parseNmea0183MessageGPGSV (String[] message)
	{
//		System.out.println ("Not implemented: Nmea0183MessageForwarder.parseNmea0183MessageGPGSV");
	}

	/**
	 * Parse a $GPRMC NMEA 0183 message.
	 * 
	 * @param message the message in pieces.
	 */
	public void parseNmea0183MessageGPRMC (String[] message)
	{
		parseTime (message[1]);
		
		for (int k=0; k < timeViews.size (); k++)
			((ITimeView)timeViews.get (k)).setDateTime (currentDate);
	}
	
	/**
	 * Parse a $GPVTG NMEA 0183 message.
	 * 
	 * @param message the message in pieces.
	 */
	public void parseNmea0183MessageGPVTG (String[] message)
	{
		if (message[1].equals (""))
			return;
		
		double course = 0;
		double speed = 0;
		try {
			// $GPVTG,258.75,T,,M,0.93,N,1.73,K,A*3F
			course = Double.parseDouble (message[1]);
			speed = Double.parseDouble (message[7]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println ("Input String was: '" + message + "'");
			return;
		} catch (ArrayIndexOutOfBoundsException e2) {
			e2.printStackTrace();
			System.err.println ("Input String was: '" + message + "'");
			return;
		}
		
		for (int k=0; k < courseViews.size (); k++)
			((ICourseView)courseViews.get (k)).setCourse (currentDate, course);

		for (int k=0; k < speedViews.size (); k++)
			((ISpeedView)speedViews.get (k)).setSpeed (currentDate, speed);
	}

	/**
	 * Parse a $GPZDA NMEA 0183 message.
	 * 
	 * @param message the message in pieces.
	 */
	public void parseNmea0183MessageGPZDA (String[] message)
	{
		if (calendar == null)
			calendar = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));

		try {
			// $GPZDA,163908.068,15,02,2008,00,00*51
			int year = Integer.parseInt (message[4]);
			int month = Integer.parseInt (message[3]);
			int date = Integer.parseInt (message[2]);
			
			calendar.set (Calendar.YEAR, year);
			calendar.set (Calendar.MONTH, month-1);
			calendar.set (Calendar.DAY_OF_MONTH, date);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println ("Input String was: '" + message + "'");
			return;
		} catch (ArrayIndexOutOfBoundsException e2) {
			e2.printStackTrace();
			System.err.println ("Input String was: '" + message + "'");
			return;
		}
		
		parseTime (message[1]);
		
		for (int k=0; k < timeViews.size (); k++)
			((ITimeView)timeViews.get (k)).setDateTime (currentDate);
	}
	
	/**
	 * Parse an GPS NMEA 0183 angle.
	 * 
	 * @param a the angle to be parsed.
	 * @return the angle as a double value.
	 */
	public static double parseAngle (String a)
	{
		double degrees = Double.parseDouble (a.substring (0, 2));
		double minutes = Double.parseDouble (a.substring (2));
		return degrees + minutes/60.0;
	}
	
	/**
	 * Parse the time in a NMEA 0183 message.
	 * 
	 * @param time the time as a <code>String</code> to be parsed.
	 */
	public void parseTime (String time)
	{
		int hourOfDay = 0;
		int minute = 0;
		int second = 0;
		String fraction = "";
		int millisecond = 0;
		try {
			fraction = time.substring (7);
			hourOfDay = Integer.parseInt (time.substring (0,2));
			minute = Integer.parseInt (time.substring (2,4));
			second = Integer.parseInt (time.substring (4,6));
			millisecond = Integer.parseInt (fraction);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.err.println ("Input String was: '" + time + "'");
			return;
		} catch (ArrayIndexOutOfBoundsException e2) {
			e2.printStackTrace();
			System.err.println ("Input String was: '" + time + "'");
			return;
		}
		switch (fraction.length ()) {
			case 1: millisecond *= 100; break;
			case 2: millisecond *= 10; break;
		}
		
		Calendar cal;
		if (calendar != null)
		{	// A $GPZDA message already arrived, so we have a calendar to work with
			cal = calendar;
		}
		else
		{	// A $GPZDA message has not arrived yet.
			cal = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
			cal.setTime (new Date());
		}
		
		cal.set (Calendar.HOUR_OF_DAY, hourOfDay);
		cal.set (Calendar.MINUTE, minute);
		cal.set (Calendar.SECOND, second);
		cal.set (Calendar.MILLISECOND, millisecond);
		currentDate = cal.getTime ();
	}

}
