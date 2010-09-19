/*
 * @(#) MotorSignals.java
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
	    double front = 29765;
	    double right = 16261;
	    double rear = 17027;
	    double left = 22861;
	    short id = 739;

	    short fr = (short) (front * MotorSignals.MOTOR_SIGNAL_FACTOR);
	    short ri = (short) (right * MotorSignals.MOTOR_SIGNAL_FACTOR);
	    short re = (short) (rear * MotorSignals.MOTOR_SIGNAL_FACTOR);
	    short le = (short) (left * MotorSignals.MOTOR_SIGNAL_FACTOR);
	    
	    byte[] expected = new byte[] {
    		(byte) (fr >> 8), (byte) (fr & 0xFF),
    		(byte) (ri >> 8), (byte) (ri & 0xFF),
    		(byte) (re >> 8), (byte) (re & 0xFF),
    		(byte) (le >> 8), (byte) (le & 0xFF),
    		(byte) (id >> 8), (byte) (id & 0xFF)
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
		MotorSignals ad = new MotorSignals (12, 34, 54, 90, (short)1111);
		String adString = ad.toString();
		assertEquals ("MotorSignals: front=12, right=34, rear=54, left=90, id=1111", adString);

		ad = new MotorSignals (12345,-31034,354,900, (short)27);
		adString = ad.toString();
		assertEquals ("MotorSignals: front=12345, right=-31034, rear=354, left=900, id=27", adString);
	}
	
	/**
	 * Verify the construction from byte arrays.
	 */
	public void testCase03 () {
		MotorSignals ad = new MotorSignals (12345,-11034,354,900, (short)109);
		byte[] adData = ad.toByteArray();
		System.out.println ("Expected: " + ad.toString());
		
		MotorSignals ad2 = new MotorSignals (adData);
		byte[] b = ad2.toByteArray();
		System.out.println ("Result:   " + ad2.toString());
		
		assertEquals (adData.length, b.length);
		for (int k=0; k < b.length; k++)
			assertEquals ("result  Array index "+k, adData[k], b[k]);
	}
}
