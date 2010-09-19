/*
 * @(#) SystemClockTestCase.java
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
package at.uni_salzburg.cs.ckgroup.util;

import junit.framework.TestCase;

/**
 * Verify the implementation of the <code>SystemClock</code> class.
 * 
 * @author Clemens Krainer
 */
public class SystemClockTestCase extends TestCase {

	/**
	 * Compare the <code>SystemClock</code> implementation against the
	 * <code>System.currentTimeMillis()</code> method. The test succeeds if the
	 * <code>SystemClock</code> and the <code>System.currentTimeMillis()</code>
	 * method repeatedly differ no more than 100ms.
	 */
	public void testCase01 ()
	{
		SystemClock clock = new SystemClock (null);
		long t1; 
		long t2;
	
		for (int k=0; k < 100; k++) {
			t1 = clock.currentTimeMillis(); 
			t2 = System.currentTimeMillis();
			assertTrue("clock=" + t1 + ", system=" + t2, Math.abs(t2 - t1) < 100);
		}
	}
}
