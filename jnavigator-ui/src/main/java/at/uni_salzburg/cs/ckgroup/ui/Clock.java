/*
 * @(#) Clock.java
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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;

import javax.swing.JPanel;

/**
 * This class implements a clock view by means of the <code>ITimeView</code> interface.
 * 
 * @author Clemens Krainer
 */
public class Clock extends JPanel implements ITimeView
{
	private static final long serialVersionUID = -4076648741571762140L;

	/**
	 * Some geometric constants.
	 */
	private static final int size = 180;
	private static final double f1 = 0.2777777;
	private static final double f3 = 0.17;
	private static final double f4 = 0.05;
	private static final double f5 = 0.1;
	private static final double f6 = 0.08;
	private static final int r1 = (int)(size * f1);
	private static final int r3 = (int)(size * f3);
	private static final int l4 = (int)(size * f4);
	private static final int l5 = (int)(size * f5);
	private static final int l6 = (int)(size * f6);
	private static final int x_origin = size/2;
	private static final int y_origin = size/2;
	private static final double startAngle = 90;
	private static final double sub_interval = 6.0;
	private static final int hoursNeedleLength = r3;
	private static final int minutesNeedleLength = r1 - l4;
	private static final int secondsNeedleLength = r1 - 2;
	
	/**
	 * A UTC calendar. It is this calendar that performs the time estimations.
	 */
	private Calendar calendar;
	
	/**
	 * This <code>AffineTransform</code> matrix rotates the hours needle of the
	 * clock.
	 */
	AffineTransform	atHours = new AffineTransform ();
	
	/**
	 * This <code>AffineTransform</code> matrix rotates the minutes needle of
	 * the clock.
	 */
	AffineTransform	atMinutes = new AffineTransform ();

	/**
	 * This <code>AffineTransform</code> matrix rotates the seconds needle of
	 * the clock.
	 */
	AffineTransform	atSeconds = new AffineTransform ();
	
	/**
	 * The current date and time.
	 */
	private Date dateAndTime = null;
	
	/**
	 * The current date as a <code>String</code>.
	 */
	private String dateString = "";
	
	/**
	 * The current time as a <code>String>/code>. 
	 */
	private String timeString = "";
	
	/**
	 * This variable contains all scale gradations of the clock. 
	 */
	private Shape[] shapes;
	
	/**
	 * The current hour as an integer.
	 */
	private int hour = 0;
	
	/**
	 * The current minute as an integer. 
	 */
	private int minute = 0;
	
	/**
	 * The current second as an integer. 
	 */
	private int second = 0;
	
	/**
	 * The hours needle as a <code>Polygon</code>.
	 */
	private static final Polygon hoursNeedlePolygon = new Polygon (
			new int[] {x_origin+hoursNeedleLength,x_origin-l4,x_origin-l4,x_origin+hoursNeedleLength,x_origin+hoursNeedleLength},
			new int[] {y_origin-2, y_origin-2, y_origin+2, y_origin+2, y_origin-2},
			5);

	/**
	 * The minutes needle as a <code>Polygon</code>.
	 */
	 private static final Polygon minutesNeedlePolygon = new Polygon (
			new int[] {x_origin+minutesNeedleLength,x_origin-l4,x_origin-l4,x_origin+minutesNeedleLength,x_origin+minutesNeedleLength},
			new int[] {y_origin-2, y_origin-2, y_origin+2, y_origin+2, y_origin-2},
			5);

	/**
	 * The seconde needle as a <code>Polygon</code>.
	 */
	private static final Polygon secondsNeedlePolygon = new Polygon (
			new int[] {x_origin+secondsNeedleLength,x_origin-l5,x_origin-l5,x_origin+secondsNeedleLength,x_origin+secondsNeedleLength},
			new int[] {y_origin-1, y_origin-1, y_origin+1, y_origin+1, y_origin-1},
			5);

	/**
	 * The en_US locale to format numbers.
	 */
	private Locale locale;
	
	/**
	 * Construct a <code>Clock</code> view.
	 */
	public Clock ()
	{
		setSize (size, size);
		shapes = new Shape[60];
		
		for (int i=0; i < 60; i++) {
			double angle = startAngle - i*sub_interval;
			double length = i % 15 == 0 ? l5 : i % 5 == 0 ? l6 : l4;
			shapes[i] = createLine (r1, length, angle);
		}

		calendar = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		locale = new Locale ("en","US");
	}
	
	/**
	 * Create a line of the scale gradations.
	 * 
	 * @param radius the inner radius of the scale
	 * @param length the length of the line
	 * @param alpha the angle of the line
	 * @return the corresponding line as a <class>Shape</class> instance.
	 */
	private Shape createLine (double radius, double length, double alpha)
	{		
		double sinAlpha = Math.sin (Math.toRadians (alpha));
		double cosAlpha = Math.cos (Math.toRadians (alpha));
		return new Line2D.Double (
				x_origin+radius*cosAlpha, y_origin-radius*sinAlpha,
				x_origin+(radius+length)*cosAlpha, y_origin-(radius+length)*sinAlpha
			);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.ui.ITimeView#setDateTime(java.util.Date)
	 */
	public void setDateTime (Date date)
	{
		this.dateAndTime = date;
		
		timeString = "";
		dateString = "";
		
		if (date != null) {
			calendar.setTime (dateAndTime);
			hour = calendar.get (Calendar.HOUR_OF_DAY);
			minute = calendar.get (Calendar.MINUTE);
			second = calendar.get (Calendar.SECOND);
//			Object[] args = new Object [4];
//			args[0] = new Integer (hour);
//			args[1] = new Integer (minute);
//			args[2] = new Integer (second);
//			args[3] = new Integer (calendar.get (Calendar.MILLISECOND));		
//			timeString = String.format ("%02d:%02d:%02d.%03d", args);
			
			NumberFormat nf = NumberFormat.getInstance(locale);
			nf.setMaximumFractionDigits(0);
			nf.setMinimumFractionDigits(0);
			nf.setMinimumIntegerDigits(2);
			nf.setGroupingUsed(false);
			
			timeString = nf.format(hour) + ':';
			timeString += nf.format(minute) + ':';
			timeString += nf.format(second) + '.';
			nf.setMinimumIntegerDigits(3);
			timeString += nf.format(calendar.get (Calendar.MILLISECOND));
			
//			args = new Object [3];
//			args[0] = new Integer (calendar.get (Calendar.YEAR));
//			args[1] = new Integer (calendar.get (Calendar.MONTH)+1);
//			args[2] = new Integer (calendar.get (Calendar.DAY_OF_MONTH));
//			dateString = String.format ("%04d-%02d-%02d", args);
			
			nf.setMinimumIntegerDigits(4);
			dateString = nf.format(calendar.get (Calendar.YEAR)) + '-';
			nf.setMinimumIntegerDigits(2);
			dateString += nf.format(calendar.get (Calendar.MONTH)+1) + '-';
			dateString += nf.format(calendar.get (Calendar.DAY_OF_MONTH));
		}
		
		repaint ();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	public void paintComponent (Graphics g)
	{
		super.paintComponent (g);
		Graphics2D ga = (Graphics2D)g;

		ga.setColor (Color.white);
		ga.fillRect (0, 0, size-1, size-1);
		ga.setColor (Color.black);
		ga.drawRect (0, 0, size-1, size-1); // draw border 
		ga.drawString ("Time", 5, 15);
		ga.drawString ("UTC", size-30, 15);
		ga.drawString (timeString, 5, size-5);
		ga.drawString (dateString, size-82, size-5);
		for (int k=0; k < shapes.length; k++)
			ga.draw (shapes[k]);

		if (dateAndTime == null)
			return;
		
		double angleHour = hour * 30 - startAngle;
		double angleMinute = minute * 6 - startAngle;
		double angleSecond = second * 6 - startAngle;
		
		atHours.setToIdentity ();
		atHours.translate (x_origin, y_origin);
		atHours.rotate (Math.toRadians (angleHour));
		AffineTransform saveXform = ga.getTransform ();
		AffineTransform toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (atHours);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		ga.fillPolygon (hoursNeedlePolygon);
		ga.setTransform (saveXform);
		
		atMinutes.setToIdentity ();
		atMinutes.translate (x_origin, y_origin);
		atMinutes.rotate (Math.toRadians (angleMinute));
		saveXform = ga.getTransform ();
		toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (atMinutes);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		ga.fillPolygon (minutesNeedlePolygon);
		ga.setTransform (saveXform);
		
		atSeconds.setToIdentity ();
		atSeconds.translate (x_origin, y_origin);
		atSeconds.rotate (Math.toRadians (angleSecond));
		saveXform = ga.getTransform ();
		toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (atSeconds);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		ga.fillPolygon (secondsNeedlePolygon);
		ga.setTransform (saveXform);		
	}

}
