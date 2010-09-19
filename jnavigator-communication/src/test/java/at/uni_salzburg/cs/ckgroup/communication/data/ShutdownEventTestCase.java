/*
 * @(#) ShutdownEventTestCase.java
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
package at.uni_salzburg.cs.ckgroup.communication.data;

import junit.framework.TestCase;

/**
 * This test case verifies the implementation of the <code>ShutdownEvent</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class ShutdownEventTestCase extends TestCase {
	
	/**
	 * Verify the correct construction of a <code>ShutdownEvent</code> object.
	 */
	public void testCase01 () {
		ShutdownEvent event = new ShutdownEvent (new byte[] {});
		assertNotNull (event);
		
		byte[] data = event.toByteArray();
		assertNotNull (data);
		assertEquals (0, data.length);
	}
	
	/**
	 * Verify the correct behaviour of the <code>toString()</code> method.
	 */
	public void testCase02 () {
		ShutdownEvent event = new ShutdownEvent (null);
		assertNotNull (event);
		assertEquals ("ShutdownEvent", event.toString());
	}

}
