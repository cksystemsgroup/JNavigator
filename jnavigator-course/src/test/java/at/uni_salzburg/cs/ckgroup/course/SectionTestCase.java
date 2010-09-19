/*
 * @(#) SectionTestCase.java
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
 * This test verifies the correct implementation of the <code>Section</code> class.
 * 
 * @author Clemens Krainer
 */
public class SectionTestCase extends TestCase {

	/**
	 * Verify the construction and the getter methods.
	 */
	public void testCase01 () {
		
		PolarCoordinate startPosition = new PolarCoordinate (48, 13, 440);
		double startOrientation = 45;
		PolarCoordinate endPosition = new PolarCoordinate (49, 14, 445);
		double endOrientation = 97;
		long travelTime = 10013;
		
		Section s = new Section (startPosition, startOrientation, endPosition, endOrientation, travelTime);

		assertNotNull (s);
		
		PolarCoordinate sp = s.getStartPosition();
		double so = s.getStartOrientation();
		PolarCoordinate ep = s.getEndPosition();
		double eo = s.getEndOrientation();
		long t = s.getTravelTime();
		
		assertEquals (startPosition.latitude, sp.latitude, 1E-9);
		assertEquals (startPosition.longitude, sp.longitude, 1E-9);
		assertEquals (startPosition.altitude, sp.altitude, 1E-9);
		assertEquals (startOrientation, so, 1E-9);
		
		assertEquals (endPosition.latitude, ep.latitude, 1E-9);
		assertEquals (endPosition.longitude, ep.longitude, 1E-9);
		assertEquals (endPosition.altitude, ep.altitude, 1E-9);
		assertEquals (endOrientation, eo, 1E-9);
		
		assertEquals (travelTime, t);
	}
	
}
