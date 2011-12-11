/*
 * @(#) MotorSignalsTestCase.java
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
 * This test case verifies the implementation of the <code>MotorSignals</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class MotorSignalsTestCase extends TestCase {

	/**
	 * Use arbitrary values for the front, right, rear and left motor to verify
	 * the correct conversion.
	 */
	public void testCase01 () {
	    short front = 29765;
	    short right = 16261;
	    short rear = 17027;
	    short left = 22861;
	    short id = 739;
	    
	    byte[] expected = new byte[] {
    		(byte) (front >> 8), (byte) (front & 0xFF),
    		(byte) (right >> 8), (byte) (right & 0xFF),
    		(byte) (rear  >> 8), (byte) (rear  & 0xFF),
    		(byte) (left  >> 8), (byte) (left  & 0xFF),
    		(byte) (id    >> 8), (byte) (id    & 0xFF)
	    }; 
	    
		MotorSignals ad = new MotorSignals (front, right, rear, left, id);
		byte[] b = ad.toByteArray();
		
		for (int k=0; k < b.length; k++)
			assertEquals ("Array index "+k, expected[k], b[k]);
	}
		
	/**
	 * Use arbitrary values for the front, right, rear and left motor to verify
	 * the <code>toString()</code> method.
	 */
	public void testCase02 () {
		MotorSignals ad = new MotorSignals ((short)1245, (short)31123, (short)546, (short)9767, (short)1111);
		String adString = ad.toString();
		assertEquals ("MotorSignals: front=1245, right=31123, rear=546, left=9767, id=1111", adString);

		ad = new MotorSignals ((short)12345, (short)(-31034), (short)354, (short)9621, (short)27);
		adString = ad.toString();
		assertEquals ("MotorSignals: front=12345, right=-31034, rear=354, left=9621, id=27", adString);
	}
	
	/**
	 * Verify the construction from byte arrays.
	 */
	public void testCase03 () {
		MotorSignals ad = new MotorSignals ((short)12345, (short)(-11034), (short)354, (short)943, (short)109);
		byte[] adData = ad.toByteArray();
		System.out.println ("Expected: " + ad.toString());
		
		MotorSignals ad2 = new MotorSignals (adData);
		byte[] b = ad2.toByteArray();
		System.out.println ("Result:   " + ad2.toString());
		
		assertEquals (adData.length, b.length);
		for (int k=0; k < b.length; k++)
			assertEquals ("result  Array index "+k, adData[k], b[k]);
	}
	
	
	/**
	 * Verify the construction from byte arrays.
	 */
	public void testCase04 () {
		int ofs = 11;
		MotorSignals ad = new MotorSignals ((short)12345, (short)(-11034), (short)354, (short)943, (short)109);
		byte[] adData = new byte[ad.toByteArray().length + ofs]; 
		
		int k=0;
		while (k < ofs)
			adData[k] = (byte)k++;
		
		for (byte b : ad.toByteArray())
			adData[k++] = b;
			
		System.out.println ("Expected: " + ad.toString());
		
		MotorSignals ad2 = new MotorSignals (adData, ofs);
		byte[] b = ad2.toByteArray();
		System.out.println ("Result:   " + ad2.toString());
		
		assertEquals (adData.length-ofs, b.length);
		for (int n=0; n < b.length; n++)
			assertEquals ("result  Array index "+n, adData[n+ofs], b[n]);
	}
}
