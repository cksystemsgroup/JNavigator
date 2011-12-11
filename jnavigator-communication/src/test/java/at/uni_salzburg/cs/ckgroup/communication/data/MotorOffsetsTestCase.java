/*
 * @(#) MotorOffsetsTestCase.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2011  Clemens Krainer
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

public class MotorOffsetsTestCase extends TestCase {
	
	short rollOffset =  29765;
	short pitchOffset = 16261;
	short yawOffset  =  17027;
	short zOffset = 	22861;

    byte[] expected = new byte[] {
		(byte) ((rollOffset >> 8) & 0xFF),	(byte) (rollOffset & 0xFF),
		(byte) ((pitchOffset >> 8) & 0xFF),	(byte) (pitchOffset & 0xFF),
		(byte) ((yawOffset >> 8) & 0xFF),	(byte) (yawOffset & 0xFF),
		(byte) ((zOffset >> 8) & 0xFF),		(byte) (zOffset & 0xFF)
    }; 
	
	
	public void testCase01 () {
	    
		MotorOffsets mo = new MotorOffsets(rollOffset, pitchOffset, yawOffset, zOffset);
		byte[] b = mo.toByteArray();
		
		for (int k=0; k < b.length; k++)
			assertEquals ("Array index "+k, expected[k], b[k]);
		
		assertEquals(rollOffset, mo.getRollOffset());
		assertEquals(pitchOffset, mo.getPitchOffset());
		assertEquals(yawOffset, mo.getYawOffset());
		assertEquals(zOffset, mo.getzOffset());
	}
	
	public void testCase02 () {
		
		MotorOffsets mo = new MotorOffsets((short)0,(short)0,(short)0,(short)0);
		
		mo.setRollOffset(rollOffset);
		mo.setPitchOffset(pitchOffset);
		mo.setYawOffset(yawOffset);
		mo.setzOffset(zOffset);
		
		assertEquals(rollOffset, mo.getRollOffset());
		assertEquals(pitchOffset, mo.getPitchOffset());
		assertEquals(yawOffset, mo.getYawOffset());
		assertEquals(zOffset, mo.getzOffset());
	}

	public void testCase03 () {
		
		MotorOffsets mo = new MotorOffsets(expected);
		
		assertEquals(rollOffset, mo.getRollOffset());
		assertEquals(pitchOffset, mo.getPitchOffset());
		assertEquals(yawOffset, mo.getYawOffset());
		assertEquals(zOffset, mo.getzOffset());
	}
	
	public void testCase04 () {
		int ofs = 11;
		MotorOffsets mo = new MotorOffsets(expected, 0);
		byte[] adData = new byte[mo.toByteArray().length + ofs]; 
		
		int k=0;
		while (k < ofs)
			adData[k] = (byte)k++;
		
		for (byte b : mo.toByteArray())
			adData[k++] = b;
		
		System.out.println ("Expected: " + mo.toString());
		
		MotorOffsets mo2 = new MotorOffsets (adData, ofs);
		byte[] b = mo2.toByteArray();
		System.out.println ("Result:   " + mo2.toString());
		
		assertEquals (adData.length-ofs, b.length);
		for (int n=0; n < b.length; n++)
			assertEquals ("result  Array index "+n, adData[n+ofs], b[n]);
	}
}
