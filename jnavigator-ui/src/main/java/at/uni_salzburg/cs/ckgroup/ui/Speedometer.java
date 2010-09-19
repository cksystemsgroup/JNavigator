/*
 * @(#) Speedometer.java
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
 * This class implements a <code>Speedometer</code> view by implementing the
 * <code>ISpeedView</code> interface.
 * 
 * @author Clemens Krainer
 */
public class Speedometer extends JPanel implements ISpeedView
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
	private static final double startAngle = 225;
	private static final double endAngle = -45;
	private static final double max_speed = 100;
	private static final double main_interval = (startAngle-endAngle) / 5;
	private static final double sub_interval = (startAngle-endAngle) / 25;
	private static final int needleDiameter = (int)(size * 0.1);
	private static final int needleLength = r1 + l3;
	
	/**
	 * The <code>AffineTransform</code> matrix that rotates the speedometer needle.
	 */
	AffineTransform	at = new AffineTransform ();
	
	/**
	 * The current speed as a double value.
	 */
	private double speed = 0;
	
	/**
	 * The current speed as a string. 
	 */
	private String speedString = null;
	
	/**
	 * This variable contains all scale gradations of the speedometer.
	 */
	private Shape[] shapes;
	
	/**
	 * This <code>Ellipse2D</code> represents the center of the speedometer needle.
	 */
	private static final Ellipse2D needleCenter = new Ellipse2D.Double (
			x_origin-needleDiameter/2, y_origin-needleDiameter/2, needleDiameter, needleDiameter);
	
	/**
	 * This <code>Polygon</code> represents the speedometer needle.
	 */
	private static final Polygon needlePolygon = new Polygon (
			new int[] {x_origin+needleLength,x_origin,x_origin,x_origin+needleLength,x_origin+needleLength},
			new int[] {y_origin-2, y_origin-needleDiameter/4, y_origin+needleDiameter/4, y_origin+1, y_origin-2},
			5);
	
    /**
     * This variable contains a <b>en_US</b> schema. The simulator uses this
     * locale for converting numbers into Strings.
     */
    private Locale locale;

	/**
	 * Construct a <code>Speedometer</code>.
	 */
	public Speedometer ()
	{
		setSize (size, size);
		shapes = new Shape[26];
		
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
//		System.out.println ("I="+i);
		
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
	 * @see at.uni_salzburg.cs.ckgroup.ui.ISpeedView#setSpeed(java.util.Date, double)
	 */
	public void setSpeed (Date date, double currentSpeed)
	{
		this.speed = currentSpeed/3.6;
		
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        String h1 = this.speed < 10 ? " " : "";
        String h2 = currentSpeed < 10 ? " " : "";
        this.speedString = h1 + nf.format(this.speed) + "m/s = "+ h2 + nf.format(currentSpeed) +"km/h";
//		this.speedString = String.format ("%5.2fm/s = %5.2fkm/h", new Object[] {new Double (speed),new Double (currentSpeed)});
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
		ga.drawString ("Speed", 5, 15);
		
		if (speedString != null)
			ga.drawString (speedString, 5, size-5);
		
		for (int k=0; k < shapes.length; k++)
			ga.draw (shapes[k]);
		
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(1);
		for (double alpha=startAngle; alpha >= endAngle; alpha -= main_interval)
		{
			double sinAlpha = Math.sin (Math.toRadians (alpha));
			double cosAlpha = Math.cos (Math.toRadians (alpha));
			int speed = (int)(max_speed*(startAngle - alpha)/(startAngle-endAngle));
			String text = nf.format (speed);
//			String text = String.format ("%d", new Object[] {new Integer (speed)});
			ga.drawString (text, (int)(x_origin-7+(r2+7)*cosAlpha), (int)(y_origin+3-(r2+5)*sinAlpha));
		}
		
		at.setToIdentity ();
		at.translate (x_origin, y_origin);
		
		double angle = (3.6*speed*(startAngle - endAngle)/max_speed) - startAngle;
		at.rotate (Math.toRadians (angle));
		AffineTransform saveXform = ga.getTransform ();
		AffineTransform toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (at);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		
		ga.fill (needleCenter);
		
		if (speedString != null)
			ga.fillPolygon (needlePolygon);
		
		ga.setTransform (saveXform);
	}
}
