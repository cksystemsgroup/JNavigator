/*
 * @(#) BirdsEyeView.java
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
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JPanel;

import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;

/**
 * This class implements a <code>BirdsEyeView</code> by implementing the
 * <code>ICoordinateView</code> interface. It receives latitude and longitude
 * values via the <code>ICoordinateView</code> interface and displays the
 * <code>MAX_POSITIONS_IN_VIEW</code> last coordinates.
 * 
 * @author Clemens Krainer
 */
public class BirdsEyeView extends JPanel implements ICoordinateView
{
	private static final long serialVersionUID = -4076648741571762140L;

	/**
	 * Some geometric constants.
	 */
	private static final int width = 400;
	private static final int height = 400;
	private static final int topMargin = 20;
	private static final int bottomMargin = 20;
	private static final int leftMargin = 20;
	private static final int rightMargin = 20;
	private static final int MAX_POSITIONS_IN_VIEW = 100;
	
	/**
	 * Display Zoom in pixel per meter.
	 */
	private static final double displayZoom = 5;
	
	/**
	 * The current latitude value as a <code>String</code>.
	 */
	private String latitudeString;
	
	/**
	 * The current longitude value as a <code>String</code>.
	 */
	private String longitudeString;

	/**
	 * The current altitude value as a <code>String</code>.
	 */
	private String altitudeString;
	
	/**
	 * The complete position containing latitude, longitude and altitude as a
	 * <code>String</code>.
	 */
	private String positionString;
	
	/**
	 * The minimum latitude value received.
	 */
	private double minLatitude;
	
	/**
	 * The maximum latitude value received. 
	 */
	private double maxLatitude;
	
	/**
	 * The minimum longitude value received. 
	 */
	private double minLongitude;
	
	/**
	 * The maximum longitude value received. 
	 */
	private double maxLongitude;
	
	/**
	 * The current position received.
	 */
	private PolarCoordinate currentPosition;
	
	/**
	 * The geodetic system to be used.
	 */
	private IGeodeticSystem geodeticSystem; 
	
	/**
	 * The <code>Vector</code> of the last <code>MAX_POSITIONS_IN_VIEW</code>
	 * received coordinates.
	 */
	private Vector<PolarCoordinate> coordinates = new Vector<PolarCoordinate>();
	
    /**
     * This variable contains a <b>en_US</b> schema. The simulator uses this
     * locale for converting numbers into Strings.
     */
    private Locale locale;
	
	/**
	 * construct a <code>BirdsEyeView</code>.
	 */
	public BirdsEyeView ()
	{
		setSize (width, height);
		geodeticSystem = new WGS84 ();
		locale = new Locale ("en","US");
	}
	
	/**
	 * Set the geodetic System to ge used for coordinate transformations.
	 * 
	 * @param g the new geodetic System to be used.
	 */
	public void setGeodeticSystem (IGeodeticSystem g)
	{
		geodeticSystem = g;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.ui.ICoordinateView#setCoordinate(java.util.Date, at.uni_salzburg.cs.ckgroup.course.PolarCoordinate)
	 */
	public void setCoordinate (Date date, PolarCoordinate coordinate)
	{
		currentPosition = coordinate;
		while (coordinates.size () > MAX_POSITIONS_IN_VIEW)
			coordinates.remove (0);
		coordinates.add (coordinate);
		
		if (latitudeString == null)
		{
			minLatitude = maxLatitude = coordinate.latitude;
			minLongitude = maxLongitude = coordinate.longitude;
		}
		else
		{
			if (coordinate.latitude < minLatitude)  minLatitude = coordinate.latitude;
			if (coordinate.latitude > maxLatitude)  maxLatitude = coordinate.latitude;
			if (coordinate.longitude < minLongitude)  minLongitude = coordinate.longitude;
			if (coordinate.longitude > maxLongitude)  maxLongitude = coordinate.longitude;
		}
		
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMaximumFractionDigits(10);
        nf.setMinimumFractionDigits(10);
        nf.setMinimumIntegerDigits(1);
        
        latitudeString = nf.format(coordinate.latitude) + "°";
        longitudeString = nf.format(coordinate.longitude) + "°";
        altitudeString = nf.format(coordinate.altitude) + "m";
//		latitudeString = String.format ("%.10f°", new Object[] {new Double (coordinate.latitude)});
//		longitudeString = String.format ("%.10f°", new Object[] {new Double (coordinate.longitude)});
//		altitudeString = String.format ("%.10fm", new Object[] {new Double (coordinate.altitude)});
		positionString = latitudeString + " " + longitudeString + " " + altitudeString;
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
		ga.fillRect (0, 0, width-1, height-1);
		ga.setColor (Color.black);
		ga.fillRect (leftMargin, topMargin, width-leftMargin-rightMargin-1, height-topMargin-bottomMargin-1);

		ga.drawString ("Bird's Eye View", 5, 15);

		double centerLatitude = (maxLatitude + minLatitude) / 2.0;
		double centerLongitude = (maxLongitude + minLongitude) / 2.0;
		double displayWidth = (width - leftMargin - rightMargin - 1) / displayZoom;
		double displayHeight = (height - topMargin - bottomMargin - 1) / displayZoom;

		PolarCoordinate center = new PolarCoordinate (centerLatitude, centerLongitude, 0);
		PolarCoordinate topLeft = geodeticSystem.walk (center, -displayHeight/2, -displayWidth/2,  0); 
		PolarCoordinate bottomRight = geodeticSystem.walk (center, displayHeight/2, displayWidth/2, 0);

		for (int k=0; k < coordinates.size (); k++)
		{
			PolarCoordinate pos = (PolarCoordinate)coordinates.get (k);
			double dy = (height-topMargin-bottomMargin-1) * (pos.latitude - topLeft.latitude) / (bottomRight.latitude - topLeft.latitude); 
			double dx = (width-leftMargin-rightMargin-1) * (pos.longitude - topLeft.longitude) / (bottomRight.longitude - topLeft.longitude);
			ga.setColor (pos == currentPosition ? Color.yellow : Color.green);
			ga.fillRect ((int)(leftMargin+dx), (int)(topMargin+dy), 2, 2);
		}

		ga.setColor (Color.black);
		if (currentPosition != null)
			ga.drawString (positionString, 5, height-5);
	}

}
