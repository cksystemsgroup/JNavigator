/*
 * @(#) CoordinateTestCase.java This code is part of the JAviator project:
 * javiator.cs.uni-salzburg.at Copyright (c) 2009 Clemens Krainer This program
 * is free software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package at.uni_salzburg.cs.ckgroup.filter;

import at.uni_salzburg.cs.ckgroup.filter.Coordinate;
import junit.framework.TestCase;

/**
 * Verify the implementation of the Coordinate class.
 * 
 * @author Clemens Krainer
 */
public class CoordinateTestCase extends TestCase
{

	/**
	 * This test case verifies the implementation of Coordinate class with a
	 * positive abscissa and a positive ordinate.
	 */
	public void testCase01 () {
		Coordinate c = new Coordinate (1, 2);
		assertEquals (c.x, 1, 0);
		assertEquals (c.y, 2, 0);
	}

	/**
	 * This test case verifies the implementation of Coordinate class with a
	 * negative abscissa and a positive ordinate.
	 */
	public void testCase02 () {
		Coordinate c = new Coordinate (-1, 2);
		assertEquals (c.x, -1, 0);
		assertEquals (c.y, 2, 0);
	}

	/**
	 * This test case verifies the implementation of Coordinate class with a
	 * positive abscissa and a negative ordinate.
	 */
	public void testCase03 () {
		Coordinate c = new Coordinate (1, -2);
		assertEquals (c.x, 1, 0);
		assertEquals (c.y, -2, 0);
	}

	/**
	 * This test case verifies the implementation of Coordinate class with a
	 * negative abscissa and a negative ordinate.
	 */
	public void testCase04 () {
		Coordinate c = new Coordinate (-1, -2);
		assertEquals (c.x, -1, 0);
		assertEquals (c.y, -2, 0);
	}
}
