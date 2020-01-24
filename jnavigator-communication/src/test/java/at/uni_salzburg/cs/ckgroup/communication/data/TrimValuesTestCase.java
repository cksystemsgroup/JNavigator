/*
 * @(#) TrimValuesTestCase.java
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
 * This test verifies the implementation of the <code>TrimValues</code> class.
 * 
 * @author Clemens Krainer
 */
public class TrimValuesTestCase extends TestCase {

	/**
	 * Use arbitrary values for roll, pitch, yaw and heightAboveGround to verify
	 * the correct conversion.
	 */
	public void testCase01 () {
		double [][] data = new double [][] { {1,2,3,4}, {12345,-12345, 31089} };
		
		for (int k=0; k < data.length; k++) {
	    	
	    	TrimValues trimValues = new TrimValues (data[k][0], data[k][1], data[k][2]);

	    	short rollS =  (short) (data[k][0] * TrimValues.DEGREES_TO_MILLIRADIANTS);
	    	short pitchS = (short) (data[k][1] * TrimValues.DEGREES_TO_MILLIRADIANTS);
	    	short yawS =   (short) (data[k][2] * TrimValues.DEGREES_TO_MILLIRADIANTS);

		    byte[] expected = new byte[] {
		    		(byte) (rollS >> 8),  (byte) (rollS & 0xFF),
		    		(byte) (pitchS >> 8), (byte) (pitchS & 0xFF),
		    		(byte) (yawS >> 8),   (byte) (yawS & 0xFF),
			    }; 
	    	
			byte[] b = trimValues.toByteArray();
			
			for (int m=0; m < b.length; m++)
				assertEquals ("Data index "+ k +" Array index "+m, expected[m], b[m]);
	    	
	    	double roll =  rollS / TrimValues.DEGREES_TO_MILLIRADIANTS;
	    	double pitch = pitchS / TrimValues.DEGREES_TO_MILLIRADIANTS;
	    	double yaw =   yawS / TrimValues.DEGREES_TO_MILLIRADIANTS;
			
			assertEquals (roll, trimValues.getRoll (), 1E-9);
			assertEquals (pitch, trimValues.getPitch (), 1E-9);
			assertEquals (yaw, trimValues.getYaw (), 1E-9);
		}
	}
	
	/**
	 * Use arbitrary values for the front, right, rear and left motor to verify
	 * the <code>toString()</code> method.
	 */
	public void testCase02 () {
		TrimValues trimValues = new TrimValues (12,34,54);
		String trimString = trimValues.toString();
		assertEquals ("TrimValues: roll=11.9748, pitch=33.9764, yaw=53.9726", trimString);

		trimValues = new TrimValues (360,-359,180);
		trimString = trimValues.toString();
		assertEquals ("TrimValues: roll=359.9894, pitch=-358.9581, yaw=179.9660", trimString);
	}
	
	/**
	 * Verify the construction from byte arrays.
	 */
	public void testCase03 () {
		TrimValues trimValues = new TrimValues (12,34,54);
		byte[] data = trimValues.toByteArray();
		System.out.println ("Expected: " + trimValues.toString());
		
		TrimValues trimValues2 = new TrimValues (data);
		byte[] b = trimValues2.toByteArray();
		System.out.println ("Result:   " + trimValues2.toString());
		
		assertEquals (data.length, b.length);
		for (int k=0; k < b.length; k++)
			assertEquals ("result  Array index "+k, data[k], b[k]);
	}
}
