/*
 * @(#) PositionProvider.java
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
package at.uni_salzburg.cs.ckgroup.location;

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.Matrix3x3;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.StringUtils;

/**
 * @author  Clemens Krainer
 */
public class SimplePositionProvider implements IPositionProvider, ILocationMessageListener
{
	/**
	 * Constants for property keys.
	 */
	public static final String PROP_REFERENCE_POSITION = "reference.position";
	public static final String PROP_REFERENCE_ORIENTATION = "reference.orientation";
	public static final String PROP_GEODETIC_SYSTEM_PREFIX = "geodetic.system.";
	public static final String PROP_TAG_ID = "tag.id";
	public static final String PROP_TAG_TYPE = "tag.type";
	public static final String PROP_CYCLE_TIME = "cycle.time";
	
	/**
	 * The reference Position from the properties as WGS84 coordinates.
	 */
	private PolarCoordinate referencePosition;
	
	/**
	 * The reference orientation of the reporting positioning system in degrees.
	 * North=0°, West=90°, South=180°, East=270°.
	 */
	private double referenceOrientation;
	
	/**
	 * Rotation matrix for adjustments according to the <code>referenceOrientation</code>
	 */
	private Matrix3x3 referenceRotator;
	
	/**
	 * The geodetic system to be used for calculations.
	 */
	private IGeodeticSystem geodeticSystem;
	
	/**
	 * The type of tag one.
	 */
	private String tagType;
	
	/**
	 * The identification string of tag one. 
	 */
	private String tagId;
	
	/**
	 * Simulation cycle time in ms.
	 */
	private double cycleTime;
	
	/**
	 * The current Position of both tags.
	 * @uml.property  name="currentPosition"
	 * @uml.associationEnd  
	 */
	private PolarCoordinate currentPosition;
	
	/**
	 * The last estimated position.
	 */
	private CartesianCoordinate lastPosition;
	
	/**
	 * The current course over ground in degrees.
	 * North=0°, West=90°, South=180°, East=270°.
	 */
	private Double currentCourseOverGround = null;
	
	/**
	 * The current speed over ground in m/s.
	 */
	private Double currentSpeedOverGround = null;
	
	/**
	 * Construct a <code>PositionProvider</code> from properties.
	 * 
	 * @param props
	 * @throws ConfigurationException 
	 */
	public SimplePositionProvider (Properties props) throws ConfigurationException
	{
		String referencePositionString = props.getProperty (PROP_REFERENCE_POSITION);
		if (referencePositionString == null || referencePositionString.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_REFERENCE_POSITION);
		
//		String[] r = referencePositionString.trim ().split ("\\s*,\\s*");
		String[] r = StringUtils.splitOnCharAndTrim(',',referencePositionString);
		if (r.length != 3)
			throw new ConfigurationException ("Property " + PROP_REFERENCE_POSITION + " should have comma separated values for latitude, longitude and altitude.");
		
		double latitude = Double.parseDouble (r[0]);
		double longitude = Double.parseDouble (r[1]);
		double altitude = Double.parseDouble (r[2]);
		referencePosition = new PolarCoordinate (latitude, longitude, altitude);
		
		String referenceOrientationString = props.getProperty (PROP_REFERENCE_ORIENTATION);
		if (referenceOrientationString == null || referenceOrientationString.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_REFERENCE_ORIENTATION);
		
		referenceOrientation = Double.parseDouble (referenceOrientationString);
		referenceRotator = new Matrix3x3 (0, 0, referenceOrientation);
		
		geodeticSystem = (IGeodeticSystem) ObjectFactory.getInstance ().instantiateObject (
				PROP_GEODETIC_SYSTEM_PREFIX, IGeodeticSystem.class, props);
		
		tagId = props.getProperty (PROP_TAG_ID);
		if (tagId == null || tagId.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_ID);

		tagType = props.getProperty (PROP_TAG_TYPE);
		if (tagType == null || tagType.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_TYPE);
		
		cycleTime = Double.parseDouble (props.getProperty (PROP_CYCLE_TIME, "100"));
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.location.LocationMessageListener#receive(at.uni_salzburg.cs.ckgroup.location.LocationMessage)
	 */
	public void receive (LocationMessage message) {
		// $LOCPNQ,Person,0e0gC17N_2M8GUQO0000em0001m,2008-06-24 00:08:52.192244708,0.025674,1,0.283855,2.89264,0.771619,0.659928,0,0,-0.751329*5E
		
		String msg = new String (message.getBytes());
		
// TODO check this.
//		String[] fields = msg.split ("[,*]");
		String[] fields = StringUtils.splitOnCharAndTrim(',',msg);
		

		if (!fields[0].equals("$LOCPNQ"))
			return;
		
		if (!tagType.equals (fields[1]) || !tagId.equals (fields[2]))
			return;

		double x;
		double y;
		double z;
//		double a;
//		double b;
//		double c;
//		double d;

		try {
			x = Double.parseDouble (fields[ 6]);
			y = Double.parseDouble (fields[ 7]);
			z = Double.parseDouble (fields[ 8]);
//			a = Double.parseDouble (fields[ 9]);
//			b = Double.parseDouble (fields[10]);
//			c = Double.parseDouble (fields[11]);
//			d = Double.parseDouble (fields[12]);
		} catch (NumberFormatException e) {
//			e.printStackTrace();
			return;
		}
		
		CartesianCoordinate currPos = new CartesianCoordinate (x,y,z);
		
		currPos = referenceRotator.multiply(currPos);
		
		CartesianCoordinate diff =
			lastPosition == null ? new CartesianCoordinate (0,0,0) : currPos.subtract(lastPosition);
		double dX = diff.getX();
		double dY = diff.getY();
		
		currentCourseOverGround = new Double (180 * Math.atan2(dY, dX) / Math.PI);
		currentSpeedOverGround = new Double (1000*Math.sqrt(dX*dX+dY*dY)/cycleTime);
		
		lastPosition = currPos;
		
		currentPosition = geodeticSystem.walk(referencePosition, -currPos.x, -currPos.y, currPos.z);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
	 */
	public PolarCoordinate getCurrentPosition () {
		return currentPosition;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCourseOverGround()
	 */
	public Double getCourseOverGround () {
		return currentCourseOverGround;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getSpeedOverGround()
	 */
	public Double getSpeedOverGround () {
		return currentSpeedOverGround;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getGeodeticSystem()
	 */
	public IGeodeticSystem getGeodeticSystem() {
		return geodeticSystem;
	}

	public void close() {
		// TODO Auto-generated method stub
	}

}
