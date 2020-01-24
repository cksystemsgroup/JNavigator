/*
 * @(#) NavigationDataTestCase.java
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
 * This test verifies the implementation of the <code>NavigationData</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class CommandDataTestCase extends TestCase {

	/**
	 * Use arbitrary values for roll, pitch, yaw and heightAboveGround to verify
	 * the correct conversion.
	 */
	public void testCase01 () {
		double [][] data = new double [][] { {1,2,3,4}, {12345,-12345, 31089, -32098} };
		
		for (int k=0; k < data.length; k++) {
	    	
	    	CommandData navigationData = new CommandData (data[k][0], data[k][1], data[k][2], data[k][3]);

	    	short rollS =  (short) (data[k][0] * CommandData.ANGLE_FACTOR);
	    	short pitchS = (short) (data[k][1] * CommandData.ANGLE_FACTOR);
	    	short yawS =   (short) (data[k][2] * CommandData.ANGLE_FACTOR);
	    	short heightAboveGroundS = (short) (data[k][3] * CommandData.METERS_TO_MILLIMETERS);

		    byte[] expected = new byte[] {
		    		(byte) (rollS >> 8),  (byte) (rollS & 0xFF),
		    		(byte) (pitchS >> 8), (byte) (pitchS & 0xFF),
		    		(byte) (yawS >> 8),   (byte) (yawS & 0xFF),
		    		(byte) (heightAboveGroundS >> 8), (byte) (heightAboveGroundS & 0xFF)
			    }; 
	    	
			byte[] b = navigationData.toByteArray();
			
			for (int m=0; m < b.length; m++)
				assertEquals ("Data index "+ k +" Array index "+m, expected[m], b[m]);
	    	
	    	double roll =  rollS / CommandData.ANGLE_FACTOR;
	    	double pitch = pitchS / CommandData.ANGLE_FACTOR;
	    	double yaw =   yawS / CommandData.ANGLE_FACTOR;
	    	double heightAboveGround = heightAboveGroundS / CommandData.METERS_TO_MILLIMETERS;
			
			assertEquals (roll, navigationData.getRoll (), 1E-9);
			assertEquals (pitch, navigationData.getPitch (), 1E-9);
			assertEquals (yaw, navigationData.getYaw (), 1E-9);
			assertEquals (heightAboveGround, navigationData.getHeightOverGround (), 1E-9);
		}
	}
	
	/**
	 * Use arbitrary values for the pitch, yaw and heightAboveGround to verify
	 * the <code>toString()</code> method.
	 */
	public void testCase02 () {
		CommandData navigationData = new CommandData (12,34,54,29);
		String navString = navigationData.toString();
		System.out.println ("Expected: " + navString);
		assertEquals ("CommandData: roll=11.9748, pitch=33.9764, yaw=53.9726, height above ground=29", navString);

		navigationData = new CommandData (360,-359,180,-27);
		navString = navigationData.toString();
		System.out.println ("Expected: " + navString);
		assertEquals ("CommandData: roll=359.9894, pitch=-358.9581, yaw=179.9660, height above ground=-27", navString);
	}
	
	/**
	 * Verify the construction from byte arrays.
	 */
	public void testCase03 () {
		CommandData navigationData = new CommandData (12,34,54,90);
		byte[] data = navigationData.toByteArray();
		System.out.println ("Expected: " + navigationData.toString());
		
		CommandData navigationData2 = new CommandData (data);
		byte[] b = navigationData2.toByteArray();
		System.out.println ("Result:   " + navigationData2.toString());
		
		assertEquals (data.length, b.length);
		for (int k=0; k < b.length; k++)
			assertEquals ("result  Array index "+k, data[k], b[k]);
	}
}
