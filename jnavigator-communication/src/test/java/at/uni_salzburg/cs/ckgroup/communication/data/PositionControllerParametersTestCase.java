/*
 * @(#) PositionControllerParametersTestCase.java
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
import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;

/**
 * This tests verify the implementation of class <code>PositionControllerParameters</code>.
 * 
 * @author Clemens Krainer
 */
public class PositionControllerParametersTestCase extends TestCase {

	/**
	 * Test the construction out of double values against the getter methods.
	 */
	public void testCase01 () {
		PositionControllerParameters params = new PositionControllerParameters (1, -1, 2, -2);
		
		assertEquals (1.0, params.getKProportional(), 1E-9);
		assertEquals (-1.0, params.getKIntegral(), 1E-9);
		assertEquals (2.0, params.getKDerivative(), 1E-9);
		assertEquals (-2.0, params.getKSecondDerivative(), 1E-9);
	}
	
	/**
	 * Test the construction out of a byte array against the provided double and byte values.
	 */
	public void testCase02 () {
		
		PositionControllerParameters base = new PositionControllerParameters (1, -1, 2, -2);
		byte[] expected = base.toByteArray();
		
		PositionControllerParameters params;
		try {
			params = new PositionControllerParameters (expected);
			byte [] actual = params.toByteArray();
			
			assertEquals (expected.length, actual.length);
			for (int k=0; k < expected.length; k++)
				assertEquals ("Index ["+k+"]", expected[k], actual[k]);
		
			assertEquals (1.0, params.getKProportional(), 1E-9);
			assertEquals (-1.0, params.getKIntegral(), 1E-9);
			assertEquals (2.0, params.getKDerivative(), 1E-9);
			assertEquals (-2.0, params.getKSecondDerivative(), 1E-9);
		} catch (CommunicationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the <code>toString()</code> method.
	 */
	public void testCase03 () {
		PositionControllerParameters base = new PositionControllerParameters (1, -1, 2, -2);

		String actual = base.toString();
		assertEquals ("PositionControllerParameters: kProportional=1, kIntegral=-1, kDerivative=2, kSecondDerivative=-2", actual);
	}
	
	/**
	 * Verify that the wrong length of the provided byte array causes the
	 * contstructor to throw a <code>CommunicationException</code>.
	 */
	public void testCase04 () {
		byte[] ba = new byte[] { 0,1,2,3,4,5 };
		try {
			PositionControllerParameters base = new PositionControllerParameters (ba);
			assertNull (base);
		} catch (CommunicationException e) {
			assertEquals ("Input data length of 6 is not equal to the expected length of 8 bytes", e.getMessage());
		}
	}
}