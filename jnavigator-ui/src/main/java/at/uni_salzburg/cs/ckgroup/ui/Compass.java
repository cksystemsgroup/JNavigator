/*
 * @(#) Compass.java
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
import java.awt.geom.AffineTransform;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JPanel;

/**
 * This class implements a <code>Compass</code> view.
 * 
 * @author Clemens Krainer
 */
public class Compass extends JPanel implements ICourseView
{
	private static final long serialVersionUID = -4076648741571762140L;

	/**
	 * Some geometric constants.
	 */
	private static final int size = 180;
	private static final double f1 = 0.3888888;
	private static final double f2 = 0.2111111;
	private static final double f3 = 0.0888888;
	private static final int l1 = (int)(size * f1);
	private static final int l2 = (int)(size * f2);
	private static final int l3 = (int)(size * f3);
	private static final int x_origin = size/2;
	private static final int y_origin = size/2;
	
	/**
	 * The first <code>Polygon</code> of the compass double cross.
	 */
	private static final Polygon poly1 = new Polygon (
			new int[] {x_origin,x_origin-l3,x_origin+l3,x_origin,x_origin},
			new int[] {y_origin-l1,y_origin,y_origin,y_origin+l1,y_origin},
			5);

	/**
	 * The second <code>Polygon</code> of the compass double cross.
	 */
	private static final Polygon poly2 = new Polygon (
			new int[] {x_origin,x_origin+l3,x_origin-l3,x_origin,x_origin},
			new int[] {y_origin-l1,y_origin,y_origin,y_origin+l1,y_origin},
			5);

	/**
	 * The third <code>Polygon</code> of the compass double cross.
	 */
	private static final Polygon poly3 = new Polygon (
			new int[] {x_origin-l1,x_origin,x_origin,x_origin+l1,x_origin},
			new int[] {y_origin,y_origin+l3,y_origin-l3,y_origin,y_origin},
			5);

	/**
	 * The fourth <code>Polygon</code> of the compass double cross.
	 */
	private static final Polygon poly4 = new Polygon (
			new int[] {x_origin-l1,x_origin,x_origin,x_origin+l1,x_origin},
			new int[] {y_origin,y_origin-l3,y_origin+l3,y_origin,y_origin},
			5);

	/**
	 * The fifth <code>Polygon</code> of the compass double cross.
	 */
	private static final Polygon poly5 = new Polygon (
			new int[] {x_origin, x_origin-l2, x_origin-l3, x_origin,
					x_origin+l2, x_origin, x_origin, x_origin+l2, x_origin+l3,
					x_origin, x_origin-l2, x_origin, x_origin },
			new int[] {y_origin, y_origin-l2, y_origin, y_origin, y_origin-l2,
					y_origin-l3, y_origin, y_origin+l2, y_origin,
					y_origin, y_origin+l2, y_origin+l3, y_origin},
			13);
	
	/**
	 * The sixth <code>Polygon</code> of the compass double cross.
	 */
	private static final Polygon poly6 = new Polygon (
			new int[] {x_origin, x_origin-l2, x_origin, x_origin,
					x_origin+l2, x_origin+l3, x_origin, x_origin+l2, x_origin,
					x_origin, x_origin-l2, x_origin-l3, x_origin },
			new int[] {y_origin, y_origin-l2, y_origin-l3, y_origin, y_origin-l2,
					y_origin, y_origin, y_origin+l2, y_origin+l3,
					y_origin, y_origin+l2, y_origin, y_origin},
			13);
	
	/**
	 * The <code>AffineTransform</code> that rotates the compass double cross.
	 */
	AffineTransform	at = new AffineTransform ();
	
	/**
	 * The current course as a double value. 
	 */
	private double course = 0;
	
	/**
	 * The current course as a string value.
	 */
	private String courseString = null;
	
    /**
     * This variable contains a <b>en_US</b> schema. The simulator uses this
     * locale for converting numbers into Strings.
     */
    private Locale locale;
	
	/**
	 * Construct a <code>Compass</code> view.
	 */
	public Compass ()
	{
		setSize (size, size);
		locale = new Locale ("de","AT");
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.ui.ICourseView#setCourse(java.util.Date, double)
	 */
	public void setCourse (Date date, double angle)
	{
		this.course = angle;
		
		NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        String h = angle < 10 ? " " : "";
        this.courseString = h + nf.format(angle) + "°";
//		this.courseString = String.format ("%5.2f°", new Object[] {new Double (angle)});
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
		ga.drawString ("Heading", 5, 15);
		
		if (courseString != null)
			ga.drawString (courseString, 5, size-5);

		at.setToIdentity ();
		at.translate (x_origin, y_origin);
		at.rotate (Math.toRadians (-course));
		AffineTransform saveXform = ga.getTransform ();
		AffineTransform toCenterAt = new AffineTransform ();
		toCenterAt.concatenate (at);
		toCenterAt.translate (-x_origin, -y_origin);
		ga.transform (toCenterAt);
		
		ga.fillPolygon (poly1);
		ga.drawPolygon (poly2);
		ga.fillPolygon (poly3);
		ga.drawPolygon (poly4);
		ga.fillPolygon (poly5);
		ga.setColor (Color.white);
		ga.fillPolygon (poly6);
		ga.setColor (Color.black);
		ga.drawPolygon (poly6);
		if (courseString != null) {
			ga.drawString ("N", x_origin-4, y_origin-l1-5);
			ga.drawString ("S", x_origin-2, y_origin+l1+15);
			ga.drawString ("E", x_origin+l1+5, y_origin+5);
			ga.drawString ("W", x_origin-l1-13, y_origin+5);
		}
		
		ga.setTransform (saveXform);
	}
}
