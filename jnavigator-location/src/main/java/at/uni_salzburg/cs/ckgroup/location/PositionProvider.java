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
import at.uni_salzburg.cs.ckgroup.NotImplementedException;
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
public class PositionProvider implements IPositionProvider, ILocationMessageListener
{
	/**
	 * Constants for property keys.
	 */
	public static final String PROP_REFERENCE_POSITION = "reference.position";
	public static final String PROP_REFERENCE_ORIENTATION = "reference.orientation";
	public static final String PROP_GEODETIC_SYSTEM_PREFIX = "geodetic.system.";
	public static final String PROP_TAG_ONE_ID = "tag.one.id";
	public static final String PROP_TAG_ONE_TYPE = "tag.one.type";
	public static final String PROP_TAG_TWO_ID = "tag.two.id";
	public static final String PROP_TAG_TWO_TYPE = "tag.two.type";
	public static final String PROP_TAG_DISTANCE = "tag.distance";
	public static final String PROP_TAG_ZERO_POSITION = "tag.zero.position";
	
	/**
	 * The reference Position from the properties as WGS84 coordinates.
	 * @uml.property  name="referencePosition"
	 * @uml.associationEnd  
	 */
	private PolarCoordinate referencePosition;
	
	/**
	 * The reference orientation of the reporting positioning system in degrees.
	 * North=0°, West=90°, South=180°, East=270°.
	 */
	private double referenceOrientation;
	
	/**
	 * Rotation matrix for adjustments according to the <code>referenceOrientation</code>
	 * @uml.property  name="referenceRotator"
	 * @uml.associationEnd  
	 */
	private Matrix3x3 referenceRotator;
	
	/**
	 * The geodetic system to be used for calculations.
	 * @uml.property  name="geodeticSystem"
	 * @uml.associationEnd  
	 */
	private IGeodeticSystem geodeticSystem;
	
	/**
	 * The type of tag one.
	 */
	private String tagOneType;
	
	/**
	 * The identification string of tag one. 
	 */
	private String tagOneId;
	
	/**
	 * The type of tag two.
	 */
	private String tagTwoType;
	
	/**
	 * The identification string of tag two. 
	 */
	private String tagTwoId;
	
	/**
	 * The distance vector between tag one and tag two in meters. It starts at tag one and points to tag two. Defining the distance between the tags as a vector allows the <code>PositionProvider</code> deriving the vehicle orientation from it. The x coordinate corresponds to South, the y coordinate corresponds to East and the z coordinates corresponds to the nadir-zenit axis.
	 * @uml.property  name="tagDistance"
	 * @uml.associationEnd  
	 */
	private CartesianCoordinate tagDistance;
	
	/**
	 * The zero position vector in meters. It starts at tag one and points to the zero position, i.e. the position the <code>PositionProvider</code> will report to the <code>LocationMessageListener</code> objects. See the <code>tagDistance</code> variable for an explanation of the x, y and z values.
	 * @uml.property  name="tagZeroPosition"
	 * @uml.associationEnd  
	 */
	private CartesianCoordinate tagZeroPosition;

	/**
	 * The position of tag one in the coordinates of the reporting positioning system.
	 * @uml.property  name="tagOnePosition"
	 * @uml.associationEnd  
	 */
	private CartesianCoordinate tagOnePosition;
	
	/**
	 * The position of tag two in the coordinates of the reporting positioning system.
	 * @uml.property  name="tagTwoPosition"
	 * @uml.associationEnd  
	 */
	private CartesianCoordinate tagTwoPosition;
	
	/**
	 * The current Position of both tags.
	 * @uml.property  name="currentPosition"
	 * @uml.associationEnd  
	 */
	private PolarCoordinate currentPosition;
	
	/**
	 * The current course over ground.
	 * @uml.property  name="courseOverGround"
	 */
	private Double courseOverGround = null;
	
	/**
	 * The current speed over ground.
	 */
//	private Double speedOverGround;
	
	
	/**
	 * Construct a <code>PositionProvider</code> from properties.
	 * 
	 * @param props
	 * @throws ConfigurationException 
	 */
	public PositionProvider (Properties props) throws ConfigurationException
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
		
		tagOneId = props.getProperty (PROP_TAG_ONE_ID);
		if (tagOneId == null || tagOneId.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_ONE_ID);

		tagOneType = props.getProperty (PROP_TAG_ONE_TYPE);
		if (tagOneType == null || tagOneType.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_ONE_TYPE);

		
		tagTwoId = props.getProperty (PROP_TAG_TWO_ID);
		if (tagTwoId == null || tagTwoId.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_TWO_ID);
			
		tagTwoType = props.getProperty (PROP_TAG_TWO_TYPE);
		if (tagTwoType == null || tagTwoType.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_TWO_TYPE);

		if (tagOneId.equals(tagTwoId))
			throw new ConfigurationException ("Properties " + PROP_TAG_ONE_ID + " and " + PROP_TAG_TWO_ID + " are equal!");
		
		
		String tagDistanceString = props.getProperty (PROP_TAG_DISTANCE);
		if (tagDistanceString == null || tagDistanceString.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_DISTANCE);
		
//		String[] dist = tagDistanceString.trim ().split ("\\s*,\\s*");
		String[] dist = StringUtils.splitOnCharAndTrim(',',tagDistanceString);
		if (dist.length != 3)
			throw new ConfigurationException ("Property " + PROP_TAG_DISTANCE + " should have comma separated values for x, y and z.");
		
		double x1 = Double.parseDouble (dist[0]);
		double y1 = Double.parseDouble (dist[1]);
		double z1 = Double.parseDouble (dist[2]);
		
		tagDistance = new CartesianCoordinate (x1, y1, z1);
		
		if (tagDistance.norm() < 1E-3)
			throw new ConfigurationException ("The distance between the two tags has to be more than 1mm.");
		
		String tagZeroPositionString = props.getProperty (PROP_TAG_ZERO_POSITION);
		if (tagZeroPositionString == null || tagZeroPositionString.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_ZERO_POSITION);
		
//		String[] zero = tagZeroPositionString.trim ().split ("\\s*,\\s*");
		String[] zero = StringUtils.splitOnCharAndTrim(',',tagZeroPositionString);
		if (zero.length != 3)
			throw new ConfigurationException ("Property " + PROP_TAG_ZERO_POSITION + " should have comma separated values for x, y and z.");
		
		double x2 = Double.parseDouble (zero[0]);
		double y2 = Double.parseDouble (zero[1]);
		double z2 = Double.parseDouble (zero[2]);
		
		tagZeroPosition = new CartesianCoordinate (x2, y2, z2);
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
		
		int tag = 0;
		if (tagOneType.equals (fields[1]) && tagOneId.equals (fields[2]))
			tag = 1;
		else if (tagTwoType.equals (fields[1]) && tagTwoId.equals (fields[2]))
			tag = 2;
		
		if (tag == 0)
			return;
		
//		String timeString = fields[3];
		
		double x = Double.parseDouble (fields[ 6]);
		double y = Double.parseDouble (fields[ 7]);
		double z = Double.parseDouble (fields[ 8]);
//		double a = Double.parseDouble (fields[ 9]);
//		double b = Double.parseDouble (fields[10]);
//		double c = Double.parseDouble (fields[11]);
//		double d = Double.parseDouble (fields[12]);
		
		CartesianCoordinate pos = new CartesianCoordinate (x,y,z);
		
		if (tag == 1)
			tagOnePosition = pos;
		else
			tagTwoPosition = pos;
		
		if (tagOnePosition == null || tagTwoPosition == null)
			return;
		
		CartesianCoordinate distance = tagTwoPosition.subtract(tagOnePosition);
//		double q = -distance.x / Math.sqrt(distance.x*distance.x + distance.y*distance.y);
//		
//		if (distance.y > 0)
//			courseOverGround = new Double (-180*Math.acos(q)/Math.PI + referenceOrientation);
//		else
//			courseOverGround = new Double (+180*Math.acos(q)/Math.PI + referenceOrientation);
		double cog = Math.atan2(-distance.y, -distance.x)*180/Math.PI + referenceOrientation;
		courseOverGround = new Double (cog >= 0 ? cog : 360+cog);
		
		CartesianCoordinate currPos = calculateCurrentPosition (distance);

		currPos = referenceRotator.multiply(currPos);
		
		currentPosition = geodeticSystem.walk(referencePosition, currPos.x, currPos.y, currPos.z);
//		System.out.print("PositionProvider.receive: msg=" + msg);
//		System.out.println ("PositionProvider.receive: pos=" + currPos + ", currentPosition=" + currentPosition + ", ref=" + referencePosition);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCurrentPosition()
	 */
	/**
	 * @return
	 * @uml.property  name="currentPosition"
	 */
	public PolarCoordinate getCurrentPosition () {
		return currentPosition;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getCourseOverGround()
	 */
	/**
	 * @return
	 * @uml.property  name="courseOverGround"
	 */
	public Double getCourseOverGround () {
		return courseOverGround;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.course.IPositionProvider#getSpeedOverGround()
	 */
	public Double getSpeedOverGround () {
		throw new NotImplementedException ();
	}

	/**
	 * Calculate the current position in rectangular coordinates of the location system.
	 * 
	 * @param currentDistance the current distance vector from tag one to tag two.
	 * @return the current position.
	 */
	CartesianCoordinate calculateCurrentPosition (CartesianCoordinate currentDistance) {
		
		if (tagZeroPosition.norm() < 1E-2)
			return tagOnePosition;
		
		CartesianCoordinate rotationAxis = tagDistance.crossProduct(currentDistance);

		double cosPhi = 1;
		double sinPhi = 0;
		CartesianCoordinate pos;
		
		if (rotationAxis.norm() > 1E-2) {
			rotationAxis = rotationAxis.normalize();
			cosPhi = tagDistance.multiply(currentDistance) / (tagDistance.norm()*currentDistance.norm());
			sinPhi = Math.sqrt(1 - cosPhi*cosPhi);
		
			CartesianCoordinate a = tagZeroPosition.multiply(cosPhi);
			CartesianCoordinate b = rotationAxis.multiply(rotationAxis.multiply(tagZeroPosition)).multiply(1-cosPhi);
			CartesianCoordinate c = tagZeroPosition.crossProduct(rotationAxis).multiply(sinPhi);
			pos = a.add(b).add(c);
		} else
			pos = tagZeroPosition;

		double x = currentDistance.multiply(pos); 
		if (x < 0)
			pos = pos.multiply(-1);
		else
			if (x == 0) {
				// This works only for roll and pitch values below 90 degrees!
				CartesianCoordinate z = new CartesianCoordinate (0,0,1);
				if (z.multiply(rotationAxis) > 0)
					pos = pos.multiply(-1);
			}

		pos = tagOnePosition.add(pos);
		return pos;
	}
}
