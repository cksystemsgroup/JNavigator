/*
 * @(#) VehicleStatusTestCase.java
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

/**
 * This class verifies the implementation of the VehicleStatus class.
 * 
 * @author Clemens Krainer
 */
public class VehicleStatusTestCase extends TestCase
{
	/**
	 * Verify the construction of a VehicleStatus object.
	 */
	public void testCase01 () {
		PolarCoordinate p = new PolarCoordinate (1,2,3);
		VehicleStatus s = new VehicleStatus (p,4,5,6,7);
		
		assertEquals (1.0, s.position.latitude, 1E-5);
		assertEquals (2.0, s.position.longitude, 1E-5);
		assertEquals (3.0, s.position.altitude, 1E-5);
		assertEquals (4.0, s.totalSpeed, 1E-5);
		assertEquals (5.0, s.courseOverGround, 1E-5);
		assertEquals (6.0, s.elevation, 1E-5);
		assertEquals (7.0, s.orientation, 1E-5);
	}
	
	/**
	 * Verify the conversion to a String.
	 */
	public void testCase02 () {
		PolarCoordinate p = new PolarCoordinate (1,2,3);
		VehicleStatus s = new VehicleStatus (p,4,5,6,7);
		String ss = s.toString ();
		assertEquals ("position=(1.0°, 2.0°, 3.0m), totalSpeed=4.0m/s, courseOverGround=5.0°, elevation=6.0°, orientation=7.0°", ss);
	}
}
