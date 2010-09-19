/*
 * @(#) TestModeTestCase.java
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
 * This test case verifies the implementation of the <code>TestMode</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class TestModeTestCase extends TestCase {

	/**
	 * Construct a new <code>TestMode</code> object from a boolean and verify
	 * the result of the <code>getTestMode()</code> method.
	 */
	public void testCase01 () {
		TestMode mode = new TestMode (true);
		assertTrue (mode.getTestMode());
	}
	
	/**
	 * Construct a new <code>TestMode</code> object from a boolean and verify
	 * the result of the <code>getTestMode()</code> method.
	 */
	public void testCase02 () {
		TestMode mode = new TestMode (false);
		assertFalse (mode.getTestMode());
	}
	
	/**
	 * Construct a new <code>TestMode</code> object from a boolean and verify
	 * the result of the <code>toByteArray()</code> method.
	 */
	public void testCase03 () {
		TestMode mode = new TestMode (true);
		byte[] data = mode.toByteArray();
		assertNotNull (data);
		assertEquals (1, data.length);
		assertTrue (data[0] != 0);
	}	
	
	/**
	 * Construct a new <code>TestMode</code> object from a boolean and verify
	 * the result of the <code>toByteArray()</code> method.
	 */
	public void testCase04 () {
		TestMode mode = new TestMode (false);
		byte[] data = mode.toByteArray();
		assertNotNull (data);
		assertEquals (1, data.length);
		assertTrue (data[0] == 0);
	}
	
	/**
	 * Construct a new <code>TestMode</code> object from a byte array and verify
	 * the result of the <code>getTestMode()</code> method.
	 */
	public void testCase05 () {
		TestMode mode = new TestMode (new byte[] {0});
		assertFalse (mode.getTestMode());
	}
	
	/**
	 * Construct a new <code>TestMode</code> object from a byte array and verify
	 * the result of the <code>getTestMode()</code> method.
	 */
	public void testCase06 () {
		TestMode mode = new TestMode (new byte[] {1});
		assertTrue (mode.getTestMode());
		
		mode = new TestMode (new byte[] {-1});
		assertTrue (mode.getTestMode());

		mode = new TestMode (new byte[] {21});
		assertTrue (mode.getTestMode());
	}

	/**
	 * Construct a new <code>TestMode</code> object from a boolean and verify
	 * the result of the <code>toString()</code> method.
	 */
	public void testCase07 () {
		TestMode mode = new TestMode (true);
		assertEquals ("TestMode: mode=true", mode.toString());

		mode = new TestMode (false);
		assertEquals ("TestMode: mode=false", mode.toString());
	}
}
