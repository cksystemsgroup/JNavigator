/*
 * @(#) PolarCoordinateTestCase.java
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

import junit.framework.TestCase;

public class PolarCoordinateTestCase extends TestCase {

	/**
	 * Verify if the default constructor sets all variables zero.
	 */
	public void testCase01 () {
		PolarCoordinate p = new PolarCoordinate ();
		assertEquals (0.0, p.latitude, 1E-10);
		assertEquals (0.0, p.longitude, 1E-10);
		assertEquals (0.0, p.altitude, 1E-10);
		assertEquals (0.0, p.getLatitude(), 1E-10);
		assertEquals (0.0, p.getLongitude(), 1E-10);
		assertEquals (0.0, p.getAltitude(), 1E-10);
	}
	
	/**
	 * Verify if the constructor using double values assigns the initialization
	 * values to the attributes correctly.
	 */
	public void testCase02 () {
		PolarCoordinate u = new PolarCoordinate (1,2,3);
		assertEquals (1.0, u.latitude, 1E-10);
		assertEquals (2.0, u.longitude, 1E-10);
		assertEquals (3.0, u.altitude, 1E-10);
		assertEquals (1.0, u.getLatitude(), 1E-10);
		assertEquals (2.0, u.getLongitude(), 1E-10);
		assertEquals (3.0, u.getAltitude(), 1E-10);
	}
	
	/**
	 * Verify if the constructor using another <code>PolarCoordinate</code> as
	 * input absorbs the coordinate values correctly.
	 */
	public void testCase03 () {
		PolarCoordinate v = new PolarCoordinate (1,2,3);
		PolarCoordinate u = new PolarCoordinate (v);
		assertEquals (1.0, u.latitude, 1E-10);
		assertEquals (2.0, u.longitude, 1E-10);
		assertEquals (3.0, u.altitude, 1E-10);
		assertEquals (1.0, u.getLatitude(), 1E-10);
		assertEquals (2.0, u.getLongitude(), 1E-10);
		assertEquals (3.0, u.getAltitude(), 1E-10);
	}
	
	/**
	 * Verify if the setter methods work correctly.
	 */
	public void testCase04 () {
		PolarCoordinate u = new PolarCoordinate ();
		u.setLatitude(1);
		u.setLongitude(2);
		u.setAltitude(3);
		assertEquals (1.0, u.latitude, 1E-10);
		assertEquals (2.0, u.longitude, 1E-10);
		assertEquals (3.0, u.altitude, 1E-10);
		assertEquals (1.0, u.getLatitude(), 1E-10);
		assertEquals (2.0, u.getLongitude(), 1E-10);
		assertEquals (3.0, u.getAltitude(), 1E-10);
	}
	
	/**
	 * Verify if the setter method for setting all coordinate values works
	 * correctly.
	 */
	public void testCase05 () {
		PolarCoordinate v = new PolarCoordinate (1,2,3);
		PolarCoordinate u = new PolarCoordinate ();
		u.set(v);
		assertEquals (1.0, u.latitude, 1E-10);
		assertEquals (2.0, u.longitude, 1E-10);
		assertEquals (3.0, u.altitude, 1E-10);
		assertEquals (1.0, u.getLatitude(), 1E-10);
		assertEquals (2.0, u.getLongitude(), 1E-10);
		assertEquals (3.0, u.getAltitude(), 1E-10);
	}
	
	/**
	 * Verify the <code>toString()</code> method.
	 */
	public void testCase06 () {
		PolarCoordinate v = new PolarCoordinate (1,2,3);
		String cooardinateString = v.toString();
		assertEquals ("(1.0°, 2.0°, 3.0m)", cooardinateString);
	}
}
