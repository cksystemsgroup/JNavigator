/*
 * @(#) FlightControlDataTestCase.java
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
package at.uni_salzburg.cs.ckgroup.pilot;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the <code>FlightControlData</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class FlightControlDataTestCase extends TestCase
{
	/**
	 * This test verifies the constructor.
	 */
	public void testCase01 ()
	{
		FlightControlData x = new FlightControlData (1, 2, 3, 4);
		
		assertEquals (1.0, x.yaw, 1e-7);
		assertEquals (2.0, x.roll, 1e-7);
		assertEquals (3.0, x.pitch, 1e-7);
		assertEquals (4.0, x.heightAboveGround, 1e-7);
	}
	
	/**
	 * This test verifies the <code>toString()</code> method. 
	 */
	public void testCase02 ()
	{
		FlightControlData x = new FlightControlData (1, 2, 3, 4);
		String s = x.toString ();
		assertEquals ("FlightControlData: yaw=1.0°, roll=2.0°, pitch=3.0°, heightAboveGround=4.0m", s);
	}
}
