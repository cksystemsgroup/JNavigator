/*
 * @(#) GpsPositionProvider.java
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

import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;
import at.uni_salzburg.cs.ckgroup.util.StringUtils;

/**
 * This class receives NMEA 0183 GGA messages from a
 * <code>Nmea0183MessageProvider</code> and converts the received positions to
 * <code>PolarCoordinate</code> objects. Via the <code>IPositionProvider</code>
 * interface this class offers the position to others.
 * 
 * @author Clemens Krainer
 */
public class GpsPositionProvider implements IPositionProvider, Nmea0183MessageListener
{
	/**
	 * This constant converts Knots (kn) to Kilometers per Hour (km/h). 
	 */
	private static final double KMH_PER_KNOTS = 1.852;
	
	/**
	 * This constant is the limit of invalid values the <code>receive()</code>
	 * method receives before setting the internal variable to <code>null</code>.
	 */
	static final int MAX_MISSING_DATA_COUNTER = 8;
	
	/**
	 * This variable contains the coordinates of the last NMEA 0138 message from above.
	 */
	private PolarCoordinate coordinate = null;
	
	/**
	 * The current course over ground.
	 */
	private Double courseOverGround = null;
	
	/**
	 * This variable counts invalid course data. Every time the
	 * <code>receive()</code> method gains a message containing an invalid
	 * course it increases this counter. If the counter is greater than the
	 * <code>MAX_MISSING_DATA_COUNTER</code> constant, the
	 * <code>receive()</code> method resets the <code>courseOverGround</code>
	 * to <code>null>/code>.
	 */
	private int courseOverGroundCounter = 0;
	
	/**
	 * The current speed over ground.
	 */
	private Double speedOverGround = null;
	
	/**
	 * This variable counts invalid speed data. Every time the
	 * <code>receive()</code> method gains a message containing an invalid
	 * velocity it increases this counter. If the counter is greater than the
	 * <code>MAX_MISSING_DATA_COUNTER</code> constant, the
	 * <code>receive()</code> method resets the <code>speedOverGround</code>
	 * to <code>null>/code>.
	 */
	private int speedOverGroundCounter = 0;
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
	 */
	public PolarCoordinate getCurrentPosition ()
	{
		return coordinate;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCourseOverGround()
	 */
	public Double getCourseOverGround ()
	{
		return courseOverGround;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getSpeedOverGround()
	 */
	public Double getSpeedOverGround ()
	{
		return speedOverGround;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener#receive(at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message)
	 */
	public void receive (Nmea0183Message newMessage)
	{
		if (newMessage == null)
		{
			coordinate = null;
			return;
		}

		if (newMessage.isAValidGgaMessage ())
		{
			// $GPGGA,134320.00,4759.42607290,N,01256.20326407,E,2,06,1.3,435.49942,M,46.59873,M,2.2,0240*43
			String m = new String (newMessage.getBytes ());
//			String[] x = m.split (",");
			String[] x = StringUtils.splitOnCharAndTrim(',',m);
			double latitude  = x[3].equals ("N") ? angleStringToValue (x[2]) : -angleStringToValue (x[2]);
			double longitude = x[5].equals ("E") ? angleStringToValue (x[4]) : -angleStringToValue (x[4]);
			double altitude  = Double.parseDouble (x[9]);
			
			if (Double.isNaN(latitude) || Double.isNaN(longitude) || Double.isNaN(altitude)) {
				System.out.print ('n');
			}
			
			coordinate = new PolarCoordinate (latitude, longitude, altitude);
			return;
		}
		
		if (newMessage.isAValidRmcMessage ())
		{
			// $GPRMC,215136.00,A,4759.43142,N,01256.20407,E,0.586,345.60,150407,,,A*6A
			String m = new String (newMessage.getBytes ());
//			String[] x = m.split (",");
			String[] x = StringUtils.splitOnCharAndTrim(',',m);
			handleCourseAndSpeed (x[8], x[7]);
			
			double latitude  = x[4].equals ("N") ? angleStringToValue (x[3]) : -angleStringToValue (x[3]);
			double longitude = x[6].equals ("E") ? angleStringToValue (x[5]) : -angleStringToValue (x[5]);
			double altitude  = coordinate != null ? coordinate.altitude : 0;
			
			if (Double.isNaN(latitude) || Double.isNaN(longitude) || Double.isNaN(altitude)) {
				System.out.print ('n');
			}
			
			coordinate = new PolarCoordinate (latitude, longitude, altitude);
			return;
		}
		
		if (newMessage.isAValidVtgMessage ())
		{
			// $GPVTG,316.66,T,,M,0.797,N,1.478,K,A*3A
			String m = new String (newMessage.getBytes ());
//			String[] x = m.split (",");
			String[] x = StringUtils.splitOnCharAndTrim(',',m);
			handleCourseAndSpeed (x[1], x[5]);			
			return;
		}
	}
	
	/**
	 * Convert the strings of the course and the speed to <code>Double</code>
	 * values and take care of invalid values.
	 * 
	 * @param courseString
	 *            the course in degrees as a <code>String</code>
	 * @param speedString
	 *            the speed in knots as a <code>String</code>
	 */
	private void handleCourseAndSpeed (String courseString, String speedString)
	{
		if (speedString.equals(""))
		{
			if (++speedOverGroundCounter > MAX_MISSING_DATA_COUNTER)
				speedOverGround = null;
		}
		else
		{
			speedOverGround = new Double (Double.parseDouble(speedString) * KMH_PER_KNOTS);
			speedOverGroundCounter = 0;
			if (speedOverGround.isNaN()) {
				System.out.print ('s');
			}
		}
			
		if (courseString.equals(""))
		{
			if (++courseOverGroundCounter > MAX_MISSING_DATA_COUNTER)
				courseOverGround = null;
		}
		else
		{
			courseOverGround = Double.valueOf(courseString);
			courseOverGroundCounter = 0;
			if (courseOverGround.isNaN()) {
				System.out.print ('c');
			}
		}
	}
	
	/**
	 * Convert a NMEA 0183 angle from <code>String</code> to a double value. A
	 * value of e.g. 4712.1387 is an angle of 47 degrees and 12.1387 minutes and
	 * will be converted to 47.20231167 degrees. This method is only visible
	 * within this package.
	 * 
	 * @param angleString the angle as a <code>String</code>
	 * @return the converted angle as a double value.
	 */
	static double angleStringToValue (String angleString)
	{
		double value = Double.parseDouble (angleString);
		double degrees = (int)(value/100);
		double seconds = value/100 - degrees;
		double angle = degrees + seconds / 0.6;
		return angle;
	}


}
