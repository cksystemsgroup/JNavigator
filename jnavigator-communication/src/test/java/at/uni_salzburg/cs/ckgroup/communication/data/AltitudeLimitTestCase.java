/*
 * @(#) AltitudeLimitTestCase.java
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

public class AltitudeLimitTestCase extends TestCase {

	/**
	 * Use arbitrary values for the limit to verify the correct conversion.
	 */
	public void testCase01 () {
	    double[] limits = new double[] { 27077, -26261, 17027, 22861, 31700, -31700, 254, 259, 513, 900};

	    for (int k=0; k < limits.length; k++) {
	    	short l = (short) (limits[k] * AltitudeLimit.LIMIT_FACTOR);
	    	byte[] expected = new byte[] { (byte)(l >> 8), (byte)(l & 0xFF) };
	    
		    AltitudeLimit altitudeLimit = new AltitudeLimit (limits[k]);
			byte[] b = altitudeLimit.toByteArray ();	
			for (int i=0; i < b.length; i++)
				assertEquals ("Value "+limits[k]+", Array index "+i, expected[i], b[i]);
			
			double actual = altitudeLimit.getLimit();
			assertEquals(limits[k], actual, 1);
			
			String stringValue = altitudeLimit.toString();
			String expectedStringValue = "AltitudeLimit: limit=" + (int)(limits[k]*AltitudeLimit.LIMIT_FACTOR);
			assertEquals (expectedStringValue, stringValue);
	    }
	}
	
	/**
	 * Use arbitrary values for the limit to verify the construction out of a
	 * byte array.
	 */
	public void testCase02 () {
		double[] limits = new double[] { 27077, -26261, 17027, 22861, 31700, -31700, 254, 259, 513, 900};
		
	    for (int k=0; k < limits.length; k++) {
	    	short l = (short) (limits[k] * AltitudeLimit.LIMIT_FACTOR);
	    	byte[] expected = new byte[] { (byte)(l >> 8), (byte)(l & 0xFF) };
	    	
	    	AltitudeLimit altitudeLimit = new AltitudeLimit (expected);
			byte[] b = altitudeLimit.toByteArray ();	
			for (int i=0; i < b.length; i++)
				assertEquals ("Value "+limits[k]+", Array index "+i, expected[i], b[i]);
			
			
			
	    }
	}
}
