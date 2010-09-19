/*
 * @(#) ByteArrayUtilsTestCase.java
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
 * This class verifies the implementation of the <code>ByteArrayUtils</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class ByteArrayUtilsTestCase extends TestCase {

	/**
	 * Compare two byte arrays. Length and content have to be identical.
	 * 
	 * @param a the first byte array
	 * @param b the second byte array
	 */
	private void compareByteArray (byte[] a, byte[] b) {
		assertEquals ("ByteArray length", a.length, b.length);
		for (int k=0; k < a.length; k++)
			assertEquals ("ByteArray content index "+k, a[k], b[k]);
	}
	
	/**
	 * Verify the <code>reverse()</code> method.
	 */
	public void testCase01 () {
		byte[] a = {0, 1, 2, 3, 4};
		byte[] e = {4, 3, 2, 1, 0};
		
		byte[] b = ByteArrayUtils.reverse(a);
		compareByteArray(e, b);
	}
	
	/**
	 * Verify the <code>partition</code> method.
	 */
	public void testCase02 () {
		byte[] a = {0, 1, 2, 3, 4, 5};
		byte[] e = {2, 3, 4};
		
		byte[] b = ByteArrayUtils.partition (a, 2, 3);
		compareByteArray(e, b);
		
		try {
			b = ByteArrayUtils.partition (a, 4, 3);
			compareByteArray(e, b);
		} catch (ArrayIndexOutOfBoundsException ex) {
			assertEquals ("6", ex.getMessage());
		}
	}
	
	/**
	 * Verify the <code>int2bytes()</code> method.
	 */
	public void testCase03 () {
		int[] a = {0, 1, -2, 3, -4};
		byte[][] e = { {0, 0, 0, 0}, {0, 0, 0, 1}, {-1, -1, -1, -2}, {0, 0, 0, 3}, {-1, -1, -1, -4} };
		
		byte[] b;
		
		try {
			for (int k=0; k < a.length; k++) {
				b = ByteArrayUtils.int2bytes (a[k]);
				compareByteArray (e[k], b);
			}
		} catch (Exception ex) {
			fail ();
		}
	}
	
	/**
	 * Verify the <code>bytes2int()</code> method.
	 */
	public void testCase04 () {
		byte[][] a = { {0, 0, 0, 0}, {0, 0, 0, 1}, {-1, -1, -1, -2}, {0, 0, 0, 3}, {-1, -1, -1, -4} };
		int[] e = {0, 1, -2, 3, -4};
		
		int b;
		
		try {
			for (int k=0; k < a.length; k++) {
				b = ByteArrayUtils.bytes2int (a[k]);
				assertEquals (e[k], b);
			}
		} catch (Exception ex) {
			fail ();
		}
	}
	
	/**
	 * Verify the <code>double2bytes()</code> method.
	 */
	public void testCase05 () {
		double[] a = {0, 1, -2, 3, -4, Math.PI, -Math.PI};
		byte[][] e = { {0, 0, 0, 0, 0, 0, 0, 0},
				{63, -16, 0, 0, 0, 0, 0, 0}, {-64, 0, 0, 0, 0, 0, 0, 0},
				{64, 8, 0, 0, 0, 0, 0, 0}, {-64, 16, 0, 0, 0, 0, 0, 0},
				{64, 9, 33, -5, 84, 68, 45, 24}, {-64, 9, 33, -5, 84, 68, 45, 24}
			};
		
		byte[] b;
		
		try {
			for (int k=0; k < a.length; k++) {
				b = ByteArrayUtils.double2bytes (a[k]);
				compareByteArray (e[k], b);
			}
		} catch (Exception ex) {
			fail ();
		}
	}
	
	/**
	 * Verify the <code>bytes2double()</code> method.
	 */
	public void testCase06 () {
		byte[][] a = { {0, 0, 0, 0, 0, 0, 0, 0},
				{63, -16, 0, 0, 0, 0, 0, 0}, {-64, 0, 0, 0, 0, 0, 0, 0},
				{64, 8, 0, 0, 0, 0, 0, 0}, {-64, 16, 0, 0, 0, 0, 0, 0},
				{64, 9, 33, -5, 84, 68, 45, 24}, {-64, 9, 33, -5, 84, 68, 45, 24}
			};
		double[] e = {0, 1, -2, 3, -4, Math.PI, -Math.PI};
		
		double b;
		
		try {
			for (int k=0; k < a.length; k++) {
				b = ByteArrayUtils.bytes2double (a[k]);
				assertEquals (e[k], b, 1E-9);
			}
		} catch (Exception ex) {
			fail ();
		}
	}
	
	/**
	 * Instantiate a <code>ByteArrayUtils</code> object to make Cobertura happy.
	 */
	public void testCase07 () {
		ByteArrayUtils u = new ByteArrayUtils ();
		assertNotNull (u);
	}
}
