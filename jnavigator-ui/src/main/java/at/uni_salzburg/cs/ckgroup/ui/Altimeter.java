/*
 * @(#) Altimeter.java
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JPanel;

/**
 * This class implements an <code>Altimeter</code> view. It extends the
 * <code>JPanel</code> container and receives new altitude values via the
 * <code>IAltitudeView</code> interface.
 * 
 * @author Clemens Krainer
 */
public class Altimeter extends JPanel implements IAltitudeView
{
	private static final long serialVersionUID = -4076648741571762140L;

	/**
	 * Some geometric constants.
	 */
	private static final int size = 180;
	private static final double f1 = 0.2777777;
	private static final double f2 = 0.3888888;
	private static final double f3 = 0.05;
	private static final double f4 = 0.0888888;
	private static final int r1 = (int)(size * f1);
	private static final int r2 = (int)(size * f2);
	private static final int l3 = (int)(size * f3);
	private static final int l4 = (int)(size * f4);
	private static final int x_origin = size/2;
	private static final int y_origin = size/2;
	private static final double startAngle = 90;
	private static final double endAngle = -270;
	private static final double main_interval = 36.0;
	private static final double sub_interval = 36.0/5.0;
	private static final int needleDiameter = (int)(size * 0.1);
	private static final int oneMeterNeedleLength = r1 + l3;
	private static final int tenMeterNeedleLength = r1 - l3;
	
	/**
	 * This variable contains the <code>AffineTransform</code> matrix for the
	 * rotation of the one meter needle.
	 */
	AffineTransform	atOneMeter = new AffineTransform ();

	/**
	 * This variable contains the <code>AffineTransform</code> matrix for the
	 * rotation of the ten meter needle.
	 */
	AffineTransform	atTenMeter = new AffineTransform ();
	
	/**
	 * The current altitude. 
	 */
	private double altitude = 0;
	
	/**
	 * The current altitude as a <code>String</code> 
	 */
	private String altitudeString = null;
	
	/**
	 * This variable contains all scale gradations of the altimeter.
	 */
	private Shape[] shapes;
	
	/**
	 * This circle is the center of both, one and ten meter needles.
	 */
	private static final Ellipse2D needleCenter = new Ellipse2D.Double (
			x_origin-needleDiameter/2, y_origin-needleDiameter/2, needleDiameter, needleDiameter);
	
	/**
	 * This polygon represents the one meter needle of the altimeter.
	 */
	private static final Polygon oneMeterPolygon = new Polygon (
			new int[] {x_origin+oneMeterNeedleLength,x_origin,x_origin,x_origin+oneMeterNeedleLength,x_origin+oneMeterNeedleLength},
			new int[] {y_origin-2, y_origin-needleDiameter/4, y_origin+needleDiameter/4, y_origin+1, y_origin-2},
			5);

	/**
	 * This polygon represents the ten meter needle of the altimeter.
	 */
	private static final Polygon tenMeterPolygon = new Polygon (
			new int[] {x_origin+tenMeterNeedleLength,x_origin,x_origin,x_origin+tenMeterNeedleLength,x_origin+tenMeterNeedleLength},
			new int[] {y_origin-2, y_origin-needleDiameter/6, y_origin+needleDiameter/6, y_origin+1, y_origin-2},
			5);

    /**
     * This variable contains a <b>en_US</b> schema. The simulator uses this
     * locale for converting numbers into Strings.
     */
    private Locale locale;
	
	/**
	 * Construct an altimeter.
	 */
	public Altimeter ()
	{
		setSize (size, size);
		shapes = new Shape[51];
		
		int i=0;
		for (double k=startAngle; k > endAngle; k -= main_interval)
		{
			if (k == startAngle)
				shapes[i++] = createLine (r1, l4, k);
			
			for (double j=sub_interval; j <= main_interval-sub_interval; j += sub_interval)
			{
				shapes[i++] = createLine (r1,l3,k-j);
			}
			shapes[i++] = createLine (r1, l4, k-main_interval);
		}
		
		locale = new Locale ("de","AT");
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
	 * @see at.uni_salzburg.cs.ckgroup.ui.IAltitudeView#setAltitude(java.util.Date, double)
	 */
	public void setAltitude (Date date, double altitude)
	{
		this.altitude = altitude;
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        String h = altitude < 10 ? " " : ""; 
        this.altitudeString = h + nf.format(altitude) + "m";
//		this.altitudeString = String.format ("%5.2fm", new Object[] {new Double (altitude)});
		repaint ();
	}

	/**
	 * Paint the <code>Altimeter</code> component.
	 * 
	 * @param g the <code>Graphics</code> context.
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
		ga.drawString ("Altitude", 5, 15);
		
		if (altitudeString != null)
			ga.drawString (altitudeString, 5, size-5);
		
		for (int k=0; k < shapes.length; k++)
			ga.draw (shapes[k]);
		
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(1);
        
		int n=0;
		for (double alpha=startAngle; alpha > endAngle; alpha -= main_interval)
		{
			double sinAlpha = Math.sin (Math.toRadians (alpha));
			double cosAlpha = Math.cos (Math.toRadians (alpha));
			String text = nf.format(n++);
//			String text = String.format ("%d", new Object[] {new Integer (n++)});
			ga.drawString (text, (int)(x_origin-3+(r2+7)*cosAlpha), (int)(y_origin+3-(r2+5)*sinAlpha));
		}

		
		double angleOne = (altitude*(startAngle - endAngle)/100.0) - startAngle;
		double angleTen = (altitude*(startAngle - endAngle)/1000.0) - startAngle;
		
		atOneMeter.setToIdentity ();
		atOneMeter.translate (x_origin, y_origin);
		atOneMeter.rotate (Math.toRadians (angleOne));
		AffineTransform saveXform = ga.getTransform ();
		AffineTransform toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (atOneMeter);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		if (altitudeString != null)
			ga.drawPolygon (oneMeterPolygon);
		ga.setTransform (saveXform);

		atTenMeter.setToIdentity ();
		atTenMeter.translate (x_origin, y_origin);
		atTenMeter.rotate (Math.toRadians (angleTen));
		saveXform = ga.getTransform ();
		toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (atTenMeter);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		ga.fill (needleCenter);
		if (altitudeString != null)
			ga.fillPolygon (tenMeterPolygon);
		ga.setTransform (saveXform);
	}
}
