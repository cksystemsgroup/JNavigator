/*
 * @(#) PositionProviderTestCase.java
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

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.NotImplementedException;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;

/**
 * This class verifies the implementation of the <code>PositionProvider</code> class.
 * 
 * @author Clemens Krainer
 */
public class PositionProviderTestCase extends TestCase {

	Properties props;
	
	public void setUp () {
		props = new Properties ();
		props.setProperty (PositionProvider.PROP_REFERENCE_POSITION, "48, 13, 440");
		props.setProperty (PositionProvider.PROP_REFERENCE_ORIENTATION, "0");
		props.setProperty (PositionProvider.PROP_TAG_ONE_TYPE, "ULocationIntegration::Tag");
		props.setProperty (PositionProvider.PROP_TAG_ONE_ID, "000000000000000020000021098");
		props.setProperty (PositionProvider.PROP_TAG_TWO_TYPE, "ULocationIntegration::Tag");
		props.setProperty (PositionProvider.PROP_TAG_TWO_ID, "000000000000000020000021099");
		props.setProperty (PositionProvider.PROP_TAG_DISTANCE, "-0.3, 0, 0");
		props.setProperty (PositionProvider.PROP_TAG_ZERO_POSITION, "-0.15, 0, 0");
		props.setProperty (PositionProvider.PROP_GEODETIC_SYSTEM_PREFIX + "className", "at.uni_salzburg.cs.ckgroup.course.WGS84");
	}
	
	public void tearDown () {
		props = null;
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a correct set of properties.
	 */
	public void testCase01 () {		
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNotNull (pp);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an empty reference position.
	 */
	public void testCase02 () {		
		props.setProperty (PositionProvider.PROP_REFERENCE_POSITION, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: reference.position", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a missing reference position.
	 */
	public void testCase021 () {		
		props.remove (PositionProvider.PROP_REFERENCE_POSITION);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: reference.position", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an incorrect formed reference position.
	 */
	public void testCase03 () {		
		props.setProperty (PositionProvider.PROP_REFERENCE_POSITION, "48, 13 440");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Property reference.position should have comma separated values for latitude, longitude and altitude.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an empty reference orientation.
	 */
	public void testCase04 () {		
		props.setProperty (PositionProvider.PROP_REFERENCE_ORIENTATION, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: reference.orientation", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a missing reference orientation.
	 */
	public void testCase041 () {		
		props.remove (PositionProvider.PROP_REFERENCE_ORIENTATION);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: reference.orientation", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an incorrect formed reference orientation.
	 */
	public void testCase05 () {		
		props.setProperty (PositionProvider.PROP_REFERENCE_ORIENTATION, "3,4");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (NumberFormatException e) {
			assertEquals ("For input string: \"3,4\"", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * if the geodetic system is not configured. 
	 */
	public void testCase06 () {		
		props.setProperty (PositionProvider.PROP_GEODETIC_SYSTEM_PREFIX + "className", "no.geodetic.system.Class");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (InstantiationException e) {
			assertEquals ("no.geodetic.system.Class", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an empty identification of tag one.
	 */
	public void testCase07 () {		
		props.setProperty (PositionProvider.PROP_TAG_ONE_ID, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.one.id", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a missing identification of tag one.
	 */
	public void testCase071 () {		
		props.remove (PositionProvider.PROP_TAG_ONE_ID);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.one.id", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an empty identification of tag one.
	 */
	public void testCase08 () {		
		props.setProperty (PositionProvider.PROP_TAG_ONE_TYPE, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.one.type", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a missing identification of tag one.
	 */
	public void testCase081 () {		
		props.remove (PositionProvider.PROP_TAG_ONE_TYPE);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.one.type", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an missing or empty identification of tag two.
	 */
	public void testCase09 () {		
		props.setProperty (PositionProvider.PROP_TAG_TWO_ID, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.two.id", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an missing or empty identification of tag two.
	 */
	public void testCase091 () {		
		props.remove (PositionProvider.PROP_TAG_TWO_ID);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.two.id", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an empty identification of tag two.
	 */
	public void testCase10 () {		
		props.setProperty (PositionProvider.PROP_TAG_TWO_TYPE, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.two.type", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a missing identification of tag two.
	 */
	public void testCase101 () {		
		props.remove (PositionProvider.PROP_TAG_TWO_TYPE);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.two.type", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an missing or empty identification of tag two.
	 */
	public void testCase11 () {		
		String x = props.getProperty(PositionProvider.PROP_TAG_ONE_ID);
		props.setProperty (PositionProvider.PROP_TAG_TWO_ID, x);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Properties tag.one.id and tag.two.id are equal!", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an empty tag distance.
	 */
	public void testCase12 () {		
		props.setProperty (PositionProvider.PROP_TAG_DISTANCE, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.distance", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a missing tag distance.
	 */
	public void testCase121 () {		
		props.remove (PositionProvider.PROP_TAG_DISTANCE);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.distance", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an incorrect formed tag distance.
	 */
	public void testCase13 () {		
		props.setProperty (PositionProvider.PROP_TAG_DISTANCE, "1, 0 0");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Property tag.distance should have comma separated values for x, y and z.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}	
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a too small distance between the two tags.
	 */
	public void testCase14 () {		
		props.setProperty (PositionProvider.PROP_TAG_DISTANCE, "0, 0, 0");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("The distance between the two tags has to be more than 1mm.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an empty tag zero position.
	 */
	public void testCase15 () {		
		props.setProperty (PositionProvider.PROP_TAG_ZERO_POSITION, "");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.zero.position", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing a missing tag zero position.
	 */
	public void testCase151 () {		
		props.remove (PositionProvider.PROP_TAG_ZERO_POSITION);
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Missing or unset property: tag.zero.position", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the construction of a <code>PositionProvider</code>
	 * utilizing an incorrect formed tag zero position.
	 */
	public void testCase16 () {		
		props.setProperty (PositionProvider.PROP_TAG_ZERO_POSITION, "48, 13 440");
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNull (pp);
		} catch (ConfigurationException e) {
			assertEquals ("Property tag.zero.position should have comma separated values for x, y and z.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the <code>getCurrentPosition()</code> returns
	 * <code>null</code> until it receives a location message.
	 */
	public void testCase17 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-24 03:04:45.337753110,0.0231501,1,0.274345,2.89224,0.759078,1,0,0,0*5B\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021099,2008-06-24 03:04:45.347753110,0.0331501,1,0.274345,2.89224,0.759078,1,0,0,0*5C\r\n".getBytes();
		
		PositionProvider pp;
		try {
			pp = new PositionProvider (props);
			assertNotNull (pp);
			assertNull (pp.getCurrentPosition());
			LocationMessage m1 = new LocationMessage (msg1);
			pp.receive(m1);
			assertNull (pp.getCurrentPosition());
			LocationMessage m2 = new LocationMessage (msg2);
			pp.receive(m2);
			assertNotNull (pp.getCurrentPosition());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the south orientation of the tags.  
	 */
	public void testCase18 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-24 03:04:45.337753110,0.0231501,1,1,2,3,1,0,0,0*45\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021099,2008-06-24 03:04:45.347753110,0.0331501,1,1.3,2,3,1,0,0,0*5F\r\n".getBytes();
		PositionProvider pp;

		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);

			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			Double course = pp.getCourseOverGround();
			assertNotNull (course);
			assertEquals (180, course.doubleValue(), 1E-9);
			PolarCoordinate pos = pp.getCurrentPosition(); 
			assertNotNull (pos);
//			System.out.println ("testCase18: pos=" + pos);
			assertEquals (47.999989670086855, pos.latitude, 1E-9);
			assertEquals (13.000026848370378, pos.longitude, 1E-9);
			assertEquals (443, pos.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the east orientation of the tags.  
	 */
	public void testCase19 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-24 03:04:45.337753110,0.0231501,1,1,2,3,1,0,0,0*45\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021099,2008-06-24 03:04:45.347753110,0.0331501,1,1,2.3,3,1,0,0,0*5F\r\n".getBytes();
		PositionProvider pp;

		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);

			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			Double course = pp.getCourseOverGround();
			assertNotNull (course);
			assertEquals (270, course.doubleValue(), 1E-9);
			PolarCoordinate pos = pp.getCurrentPosition(); 
			assertNotNull (pos);
//			System.out.println ("testCase19: pos=" + pos);
			assertEquals (47.99999101746683, pos.latitude, 1E-9);
			assertEquals (13.000028861998157, pos.longitude, 1E-9);
			assertEquals (443, pos.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the north orientation of the tags.  
	 */
	public void testCase20 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-24 03:04:45.337753110,0.0231501,1,1,2,3,1,0,0,0*45\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021099,2008-06-24 03:04:45.347753110,0.0331501,1,0.7,2,3,1,0,0,0*5A\r\n".getBytes();
		PositionProvider pp;

		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);

			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			Double course = pp.getCourseOverGround();
			assertNotNull (course);
			assertEquals (0, course.doubleValue(), 1E-9);
			PolarCoordinate pos = pp.getCurrentPosition(); 
			assertNotNull (pos);
//			System.out.println ("testCase20: pos=" + pos);
			assertEquals (47.999992364846804, pos.latitude, 1E-9);
			assertEquals (13.000026848370378, pos.longitude, 1E-9);
			assertEquals (443, pos.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the west orientation of the tags.  
	 */
	public void testCase21 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-24 03:04:45.337753110,0.0231501,1,1,2,3,1,0,0,0*45\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021099,2008-06-24 03:04:45.347753110,0.0331501,1,1,1.7,3,1,0,0,0*58\r\n".getBytes();
		PositionProvider pp;

		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);

			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			Double course = pp.getCourseOverGround();
			assertNotNull (course);
			assertEquals (90, course.doubleValue(), 1E-9);
			PolarCoordinate pos = pp.getCurrentPosition(); 
			assertNotNull (pos);
//			System.out.println ("testCase21: pos=" + pos);
			assertEquals (47.99999101746683, pos.latitude, 1E-9);
			assertEquals (13.0000248347426, pos.longitude, 1E-9);
			assertEquals (443, pos.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the orientation of the tags having the reference coordinate system rotated by -60 degrees   
	 */
	public void testCase22 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021098,2008-06-24 03:04:45.337753110,0.0231501,1,1,2,3,1,0,0,0*45\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021099,2008-06-24 03:04:45.347753110,0.0331501,1,1,1.7,3,1,0,0,0*58\r\n".getBytes();
		PositionProvider pp;
		
		props.setProperty (PositionProvider.PROP_REFERENCE_ORIENTATION, "-60");

		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);

			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			Double course = pp.getCourseOverGround();
			assertNotNull (course);
			assertEquals (30, course.doubleValue(), 1E-9);
			PolarCoordinate pos = pp.getCurrentPosition(); 
			assertNotNull (pos);
//			System.out.println ("testCase22: pos=" + pos);
			assertEquals (47.99998111739486, pos.latitude, 1E-9);
			assertEquals (13.000000791685903, pos.longitude, 1E-9);
			assertEquals (443, pos.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies the orientation of the tags having the reference coordinate system rotated by 63 degrees   
	 */
	public void testCase23 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021117,2008-07-16 09:04:50.252255674,0.0085539,1,0.281577368981064,1.16560217042381,1.19275831469793,1,0,0,0*58\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021161,2008-07-16 09:04:50.306299362,0.0364162,1,0.528724552342343,1.08485645045045,1.35486550630631,1,0,0,0*52\r\n".getBytes();
		
		PositionProvider pp;
		props.setProperty (PositionProvider.PROP_REFERENCE_POSITION, "47.821933, 13.040875, 440");
		
		props.setProperty (PositionProvider.PROP_REFERENCE_ORIENTATION, "-63.05567257337351");
		props.setProperty (PositionProvider.PROP_TAG_ONE_ID, "000000000000000020000021117");
		props.setProperty (PositionProvider.PROP_TAG_TWO_ID, "000000000000000020000021161");
		props.setProperty (PositionProvider.PROP_TAG_DISTANCE, "-0.29, 0, 0");
		props.setProperty (PositionProvider.PROP_TAG_ZERO_POSITION, "0, 0, 0");
	
		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);
	
			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			Double course = pp.getCourseOverGround();
			assertNotNull (course);
			assertEquals (98.85153943064337, course.doubleValue(), 1E-9);
			PolarCoordinate pos = pp.getCurrentPosition(); 
			assertNotNull (pos);
//			System.out.println ("testCase23: pos=" + pos + ", course=" + course);
			assertEquals (47.82192252041593, pos.latitude, 1E-9);
			assertEquals (13.040878707754016, pos.longitude, 1E-9);
			assertEquals (441.1927583146979, pos.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the receipt of a $LOCPOS message does neither change a orientation nor a position.    
	 */
	public void testCase24 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021117,2008-07-16 09:04:50.252255674,0.0085539,1,0.281577368981064,1.16560217042381,1.19275831469793,1,0,0,0*58\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021161,2008-07-16 09:04:50.306299362,0.0364162,1,0.528724552342343,1.08485645045045,1.35486550630631,1,0,0,0*52\r\n".getBytes();
		byte[] msg3 = "$LOCPOS,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3*6A\r\n".getBytes ();	
		PositionProvider pp;
		props.setProperty (PositionProvider.PROP_REFERENCE_POSITION, "47.821933, 13.040875, 440");
		
		props.setProperty (PositionProvider.PROP_REFERENCE_ORIENTATION, "-63.05567257337351");
		props.setProperty (PositionProvider.PROP_TAG_ONE_ID, "000000000000000020000021117");
		props.setProperty (PositionProvider.PROP_TAG_TWO_ID, "000000000000000020000021161");
		props.setProperty (PositionProvider.PROP_TAG_DISTANCE, "-0.29, 0, 0");
		props.setProperty (PositionProvider.PROP_TAG_ZERO_POSITION, "0, 0, 0");
		
		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);
			LocationMessage m3 = new LocationMessage (msg3);
	
			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			
			Double course1 = pp.getCourseOverGround();
			assertNotNull (course1);
			PolarCoordinate pos1 = pp.getCurrentPosition();
			assertNotNull (pos1);
			
			pp.receive(m3);
			Double course2 = pp.getCourseOverGround();
			assertNotNull (course2);
			PolarCoordinate pos2 = pp.getCurrentPosition();
			assertNotNull (pos2);
			
			assertEquals (course1.doubleValue(), course2.doubleValue(), 1E-9);
			assertEquals (pos1.latitude, pos2.latitude, 1E-9);
			assertEquals (pos1.longitude, pos2.longitude, 1E-9);
			assertEquals (pos1.altitude, pos2.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the receipt of a $LOCPNQ message with an unknown tag does neither change a orientation nor a position.    
	 */
	public void testCase25 () {
		byte[] msg1 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021117,2008-07-16 09:04:50.252255674,0.0085539,1,0.281577368981064,1.16560217042381,1.19275831469793,1,0,0,0*58\r\n".getBytes();
		byte[] msg2 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021161,2008-07-16 09:04:50.306299362,0.0364162,1,0.528724552342343,1.08485645045045,1.35486550630631,1,0,0,0*52\r\n".getBytes();
		byte[] msg3 = "$LOCPNQ,ULocationIntegration::Tag,000000000000000020000021162,2008-07-16 09:04:50.306299362,0.0364162,1,0.528724552342343,1.08485645045045,1.35486550630631,1,0,0,0*51\r\n".getBytes();
		PositionProvider pp;
		props.setProperty (PositionProvider.PROP_REFERENCE_POSITION, "47.821933, 13.040875, 440");
		
		props.setProperty (PositionProvider.PROP_REFERENCE_ORIENTATION, "-63.05567257337351");
		props.setProperty (PositionProvider.PROP_TAG_ONE_ID, "000000000000000020000021117");
		props.setProperty (PositionProvider.PROP_TAG_TWO_ID, "000000000000000020000021161");
		props.setProperty (PositionProvider.PROP_TAG_DISTANCE, "-0.29, 0, 0");
		props.setProperty (PositionProvider.PROP_TAG_ZERO_POSITION, "0, 0, 0");
		
		try {
			LocationMessage m1 = new LocationMessage (msg1);
			LocationMessage m2 = new LocationMessage (msg2);
			LocationMessage m3 = new LocationMessage (msg3);
	
			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.receive(m1);
			pp.receive(m2);
			
			Double course1 = pp.getCourseOverGround();
			assertNotNull (course1);
			PolarCoordinate pos1 = pp.getCurrentPosition();
			assertNotNull (pos1);
			
			pp.receive(m3);
			Double course2 = pp.getCourseOverGround();
			assertNotNull (course2);
			PolarCoordinate pos2 = pp.getCurrentPosition();
			assertNotNull (pos2);
			
			assertEquals (course1.doubleValue(), course2.doubleValue(), 1E-9);
			assertEquals (pos1.latitude, pos2.latitude, 1E-9);
			assertEquals (pos1.longitude, pos2.longitude, 1E-9);
			assertEquals (pos1.altitude, pos2.altitude, 1E-9);
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}	
	
	/**
	 * This test verifies that the <code>PositionProvider.getSpeedOverGround()</code> throws a <code>NotImplementedException</code>.
	 */
	public void testCase26 () {
		PositionProvider pp;
		
		try {
			pp = new PositionProvider (props);
			assertNotNull (pp);
			pp.getSpeedOverGround();
		} catch (NotImplementedException e) {
			assertNull (e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}	
}
