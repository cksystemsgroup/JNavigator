/*
 * @(#) MotorSignalsTestCase.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2008  Clemens Krainer
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
package javiator.util;

import junit.framework.TestCase;

public class MotorSignalsTestCase extends TestCase {

	public void testCase01 () {
		byte [] payload = new byte[] {1,2,3,4,5,6,7,8};
		
		Packet packet = new Packet ((byte)212, payload);
		MotorSignals actuatorData = new MotorSignals ();
		actuatorData.fromPacket(packet);
		assertEquals (actuatorData.front, 258);
		assertEquals (actuatorData.right, 772);
		assertEquals (actuatorData.rear, 1286);
		assertEquals (actuatorData.left, 1800);
		
		Packet packet2 = actuatorData.toPacket((byte)212);
		assertEquals(payload.length, packet2.payload.length);
		for (int k=0; k < packet2.payload.length; k++)
			assertEquals(payload[k], packet2.payload[k]);
	}
	
	public void testCase02 () {
		MotorSignals actuatorData = new MotorSignals ();
		actuatorData.front = 900;
		actuatorData.right = -900;
		actuatorData.rear = 12345;
		actuatorData.left = -12345;
		
		Packet packet = actuatorData.toPacket((byte)212);
		MotorSignals actuatorData2 = new MotorSignals ();
		actuatorData2.fromPacket(packet);
		assertEquals (900, actuatorData2.front);
		assertEquals (-900, actuatorData2.right);
		assertEquals (12345,  actuatorData2.rear);
		assertEquals (-12345,  actuatorData2.left);
	}
}
