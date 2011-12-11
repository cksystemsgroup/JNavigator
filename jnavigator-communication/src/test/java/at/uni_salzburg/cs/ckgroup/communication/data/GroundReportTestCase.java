/*
 * @(#) GroundReportTestCase.java
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

import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;
import junit.framework.TestCase;

public class GroundReportTestCase extends TestCase {
	
	public void testCase01 () throws CommunicationException {
		
		int len = SensorData.payloadLength + MotorSignals.payloadLength + MotorOffsets.payloadLength + 2; 
		byte[] data = new byte[len];
		for (int k=0; k < len; k++)
			data[k] = (byte)k;
		
		GroundReport gr = new GroundReport(data);
		byte[] res = gr.toByteArray();
		
		for (int k = 0; k < len; k++)
			assertEquals("Array index " + k, data[k], res[k]);
	}
	
	
	public void testCase02 () throws CommunicationException {
		
		int len = SensorData.payloadLength + MotorSignals.payloadLength + MotorOffsets.payloadLength + 2; 
		byte[] data = new byte[len];
		for (int k=0; k < len; k++)
			data[k] = (byte)(100-k);
		data[len-2] = 1;
		data[len-1] = 2;
		
		GroundReport gr = new GroundReport(data);

		SensorData sd = gr.getSensorData();
		assertEquals (   15.419, sd.getBattery (), 1E-3);
		assertEquals ( 1266.294, sd.getDDPitch (), 1E-3);
		assertEquals ( 1295.744, sd.getDDRoll (),  1E-3);
		assertEquals (   17.989, sd.getDdx(),      1E-3);
		assertEquals (37699.656, sd.getDDx (),     1E-3);
		assertEquals (   17.475, sd.getDdy(),      1E-3);
		assertEquals (36622.463, sd.getDDy (),     1E-3);
		assertEquals ( 1236.843, sd.getDDYaw (),   1E-3);
		assertEquals (   16.961, sd.getDdz(),      1E-3);
		assertEquals (35545.270, sd.getDDz (),     1E-3);
		assertEquals ( 1354.644, sd.getDPitch (),  1E-3);
		assertEquals ( 1384.094, sd.getDRoll (),   1E-3);
		assertEquals (  195.310, sd.getDx (),      1E-3);
		assertEquals (  190.170, sd.getDy (),      1E-3);
		assertEquals ( 1325.194, sd.getDYaw (),    1E-3);
		assertEquals (  185.030, sd.getDz (),      1E-3);
		assertEquals ( 1442.994, sd.getPitch (),   1E-3);
		assertEquals ( 1472.444, sd.getRoll (),    1E-3);
		assertEquals (   21.073, sd.getX (),       1E-3);
		assertEquals (   20.559, sd.getY (),       1E-3);
		assertEquals ( 1413.544, sd.getYaw (),     1E-3);
		assertEquals (   20.045, sd.getZ (),       1E-3);
		
		MotorSignals ms = gr.getMotorSignals();
		assertEquals (    14905, ms.getFront(), 1E-9);
		assertEquals (    13363, ms.getLeft(),  1E-9);
		assertEquals (    13877, ms.getRear(),  1E-9);
		assertEquals (    14391, ms.getRight(), 1E-9);
		assertEquals (    12849, ms.getId());
		
		MotorOffsets mo = gr.getMotorOffsets();
		assertEquals (    11821, mo.getPitchOffset());
		assertEquals (    12335, mo.getRollOffset());
		assertEquals (    11307, mo.getYawOffset());
		assertEquals (    10793, mo.getzOffset());
		
		FlyingState state = gr.getState();
		assertEquals (        1, state.ordinal());
		
		FlyingMode mode = gr.getMode();
		assertEquals (        2, mode.ordinal());

	}
	
}
