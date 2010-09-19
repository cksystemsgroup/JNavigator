/*
 * @(#) DummyPositionProviderTestCase.java
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
package at.uni_salzburg.cs.ckgroup.course;

import java.util.Properties;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the
 * <code>DummyPositionProvider</code> class.
 * 
 * @author Clemens Krainer
 */
public class DummyPositionProviderTestCase extends TestCase
{
	Properties props;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		props = new Properties ();
	}

	/**
	 * Verify the default values (0,0,0),null,null if no Properties are set.
	 */
	public void testCase01 () {
		DummyPositionProvider dummy = new DummyPositionProvider (props);
		PolarCoordinate position = dummy.getCurrentPosition ();
		assertEquals (0.0, position.latitude, 1E-9);
		assertEquals (0.0, position.longitude, 1E-9);
		assertEquals (0.0, position.altitude, 1E-9);
		assertNull (dummy.getCourseOverGround());
		assertNull (dummy.getSpeedOverGround());
	}

	/**
	 * Set the properties to (0,0,0),0,0 and verify if the
	 * <code>getCurrentPosition ()</code> method returns the right values.
	 */
	public void testCase02 () {
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LATITUDE,"0");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LONGITUDE,"0");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_ALTITUDE,"0");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_COURSE_OVER_GROUND,"0");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_SPEED_OVER_GROUND,"0");

		DummyPositionProvider dummy = new DummyPositionProvider (props);
		PolarCoordinate position = dummy.getCurrentPosition ();
		
		assertEquals (0.0, position.latitude, 1E-9);
		assertEquals (0.0, position.longitude, 1E-9);
		assertEquals (0.0, position.altitude, 1E-9);
		assertNotNull (dummy.getCourseOverGround());
		assertEquals (0.0, dummy.getCourseOverGround().doubleValue(), 1E-9);
		assertNotNull (dummy.getSpeedOverGround());
		assertEquals (0.0, dummy.getSpeedOverGround().doubleValue(), 1E-9);
	}
	
	/**
	 * Set the properties to (48,13,440) and verify if the
	 * <code>getCurrentPosition ()</code> method returns the right values.
	 */
	public void testCase03 () {
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LATITUDE,"48");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LONGITUDE,"13");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_ALTITUDE,"440");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_COURSE_OVER_GROUND,"0.123");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_SPEED_OVER_GROUND,"4.321");

		DummyPositionProvider dummy = new DummyPositionProvider (props);
		PolarCoordinate position = dummy.getCurrentPosition ();
		
		assertEquals (48.0, position.latitude, 1E-9);
		assertEquals (13.0, position.longitude, 1E-9);
		assertEquals (440.0, position.altitude, 1E-9);
		assertNotNull (dummy.getCourseOverGround());
		assertEquals (0.123, dummy.getCourseOverGround().doubleValue(), 1E-9);
		assertNotNull (dummy.getSpeedOverGround());
		assertEquals (4.321, dummy.getSpeedOverGround().doubleValue(), 1E-9);
	}

	/**
	 * Set the properties to (48,13,+440) and verify if the
	 * <code>getCurrentPosition ()</code> method returns the right values. Use
	 * the "+" character to indicate a positive value for the altitude.
	 */
	public void testCase04 () {
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LATITUDE,"48.2");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LONGITUDE,"13.5");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_ALTITUDE,"+440.1");

		DummyPositionProvider dummy = new DummyPositionProvider (props);
		PolarCoordinate position = dummy.getCurrentPosition ();
		
		assertEquals (48.2, position.latitude, 1E-9);
		assertEquals (13.5, position.longitude, 1E-9);
		assertEquals (440.1, position.altitude, 1E-9);
	}

	/**
	 * Set the properties to (-48,-13,-440) and verify if the
	 * <code>getCurrentPosition ()</code> method returns the right values. Use
	 * the "-" character to indicate negative values.
	 */
	public void testCase05 () {
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LATITUDE,"-48.2");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LONGITUDE,"-13.5");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_ALTITUDE,"-440.1");

		DummyPositionProvider dummy = new DummyPositionProvider (props);
		PolarCoordinate position = dummy.getCurrentPosition ();
		
		assertEquals (-48.2, position.latitude, 1E-9);
		assertEquals (-13.5, position.longitude, 1E-9);
		assertEquals (-440.1, position.altitude, 1E-9);
	}
	
	/**
	 * Set the Property values to empty Strings and expect the
	 * <code>NumberFormatException</code> to be thrown indicating an <i>empty
	 * String</i>.
	 */
	public void testCase06 () {
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LATITUDE,"");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LONGITUDE,"");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_ALTITUDE,"");

		try {
			DummyPositionProvider dummy = new DummyPositionProvider (props);
			assertNull (dummy);
		} catch (NumberFormatException e) {
			assertEquals ("empty String", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * Set the Property values to empty Strings and expect the
	 * <code>NumberFormatException</code> to be thrown containing the message
	 * <i>For input string: \"48,2\"</i> indicating that the latitude value is
	 * malformed.
	 */
	public void testCase07 () {
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LATITUDE,"48,2");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_LONGITUDE,"-13,5");
		props.setProperty (DummyPositionProvider.PROP_REFERENCE_ALTITUDE,"-440,1");

		try {
			DummyPositionProvider dummy = new DummyPositionProvider (props);
			assertNull (dummy);
		} catch (NumberFormatException e) {
			assertEquals ("For input string: \"48,2\"", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace ();
			fail ();
		}
	}
	
}
