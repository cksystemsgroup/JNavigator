/*
 * @(#) SensorDataTestCase.java
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
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;

/**
 * This test case verifies the implementation of the <code>SensorData</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class SensorDataTestCase extends TestCase {

	/**
	 * Verify the correct conversion of an array of byes to a
	 * <code>SensorData</code> object and back into an array of bytes.
	 */
	public void testCase01() {
		byte payload[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
				14, 15, 16, 17, 18,	19, 20, 21, 22, 23, 24, 25, 26,
				27, 28, 29, 30, 31, 32, 33, 34, 35, 36,	37, 38 };

		try {
			SensorData sd = new SensorData(payload);
			byte[] b2 = sd.toByteArray();
			assertEquals("Array length differs", payload.length, b2.length);

			for (int k = 0; k < payload.length; k++)
				assertEquals("Array index " + k, payload[k], b2[k]);

		} catch (CommunicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Verify the correct conversion of an array of byes to a
	 * <code>SensorData</code> object.
	 */
	public void testCase02() {
		byte payload[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
				14, 15, 16, 17, 18,	19, 20, 21, 22, 23, 24, 25, 26,
				27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38 };

		try {
			SensorData sd = new SensorData(payload);
			int k = 0;

			short roll = (short) (sd.getRoll() / SensorData.ANGLE_FACTOR);
			assertEquals("roll HI", (byte) (roll >> 8), payload[k++]);
			assertEquals("roll LO", (byte) (roll & 0xFF), payload[k++]);

			short pitch = (short) (sd.getPitch() / SensorData.ANGLE_FACTOR + 0.0001);
			assertEquals("pitch HI", (byte) (pitch >> 8), payload[k++]);
			assertEquals("pitch LO", (byte) (pitch & 0xFF), payload[k++]);

			short yaw = (short) (sd.getYaw() / SensorData.ANGLE_FACTOR);
			assertEquals("yaw HI", (byte) (yaw >> 8), payload[k++]);
			assertEquals("yaw LO", (byte) (yaw & 0xFF), payload[k++]);

			short dRoll = (short) (sd.getDRoll() / SensorData.ANG_RATE_FACTOR);
			assertEquals("dRoll HI", (byte) (dRoll >> 8), payload[k++]);
			assertEquals("dRoll LO", (byte) (dRoll & 0xFF), payload[k++]);

			short dPitch = (short) (sd.getDPitch() / SensorData.ANG_RATE_FACTOR);
			assertEquals("dPitch HI", (byte) (dPitch >> 8), payload[k++]);
			assertEquals("dPitch LO", (byte) (dPitch & 0xFF), payload[k++]);

			short dYaw = (short) (sd.getDYaw() / SensorData.ANG_RATE_FACTOR);
			assertEquals("dYaw HI", (byte) (dYaw >> 8), payload[k++]);
			assertEquals("dYaw LO", (byte) (dYaw & 0xFF), payload[k++]);

			short ddRoll = (short) (sd.getDDRoll() / SensorData.ANG_RATE_FACTOR);
			assertEquals("ddRoll HI", (byte) (ddRoll >> 8), payload[k++]);
			assertEquals("ddRoll LO", (byte) (ddRoll & 0xFF), payload[k++]);

			short ddPitch = (short) (sd.getDDPitch() / SensorData.ANG_RATE_FACTOR);
			assertEquals("ddPitch HI", (byte) (ddPitch >> 8), payload[k++]);
			assertEquals("ddPitch LO", (byte) (ddPitch & 0xFF), payload[k++]);

			short ddYaw = (short) (sd.getDDYaw() / SensorData.ANG_RATE_FACTOR);
			assertEquals("ddYaw HI", (byte) (ddYaw >> 8), payload[k++]);
			assertEquals("ddYaw LO", (byte) (ddYaw & 0xFF), payload[k++]);

			short x = (short) (sd.getX() / SensorData.LENGTH_FACTOR);
			assertEquals ("x HI", (byte) (x >> 8), payload[k++]);
			assertEquals ("x LO", (byte) (x & 0xFF), payload[k++]);

			short y = (short) (sd.getY() / SensorData.LENGTH_FACTOR);
			assertEquals ("y HI", (byte) (y >> 8), payload[k++]);
			assertEquals ("y LO", (byte) (y & 0xFF), payload[k++]);

			short z = (short) (sd.getZ() / SensorData.LENGTH_FACTOR);
			assertEquals("z HI", (byte) (z >> 8), payload[k++]);
			assertEquals("z LO", (byte) (z & 0xFF), payload[k++]);


			short dx = (short) (sd.getDx() / SensorData.VELOCITY_FACTOR);
			assertEquals("dx HI", (byte) (dx >> 8), payload[k++]);
			assertEquals("dx LO", (byte) (dx & 0xFF), payload[k++]);

			short dy = (short) (sd.getDy() / SensorData.VELOCITY_FACTOR);
			assertEquals("dy HI", (byte) (dy >> 8), payload[k++]);
			assertEquals("dy LO", (byte) (dy & 0xFF), payload[k++]);

			short dz = (short) (sd.getDz() / SensorData.VELOCITY_FACTOR);
			assertEquals("dz HI", (byte) (dz >> 8), payload[k++]);
			assertEquals("dz LO", (byte) (dz & 0xFF), payload[k++]);
			
			
			short ddx = (short) (sd.getDDx() / SensorData.ACCEL_FACTOR);
			assertEquals("ddx HI", (byte) (ddx >> 8), payload[k++]);
			assertEquals("ddx LO", (byte) (ddx & 0xFF), payload[k++]);

			short ddy = (short) (sd.getDDy() / SensorData.ACCEL_FACTOR);
			assertEquals("ddy HI", (byte) (ddy >> 8), payload[k++]);
			assertEquals("ddy LO", (byte) (ddy & 0xFF), payload[k++]);

			short ddz = (short) (sd.getDDz() / SensorData.ACCEL_FACTOR);
			assertEquals("ddz HI", (byte) (ddz >> 8), payload[k++]);
			assertEquals("ddz LO", (byte) (ddz & 0xFF), payload[k++]);
			
			short voltage = (short) (sd.getBattery() / SensorData.BATTERY_FACTOR);
			assertEquals ("voltage HI", (byte) (voltage >> 8), payload[k++]);
			assertEquals ("voltage LO", (byte) (voltage & 0xFF), payload[k++]);

		} catch (CommunicationException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Verify that an <code>CommunicationException</code> is thrown in case of
	 * an failed conversion.
	 */
	public void testCase03() {
		byte payload[] = { 1, 2, 3, 4 };

		try {
			SensorData sd = new SensorData(payload);
			assertNull(sd);
		} catch (CommunicationException e) {
			assertEquals(
					"Input data length of 4 is not equal to the expected length of 38 bytes",
					e.getMessage());
		}
	}
	
	/**
	 * Verify the construction from byte arrays.
	 */
	public void testCase04 () {
		
		byte payload[] = { 1, 2, 3, -4, 5, 6, -7, 8, -9, 10, 11, 12, 13,
				-127, 15,	16,	-17, 18, 19, 20, 21, -22, 23, 127, -25, -26,
				27, -28, -29, -30, 31, -32, 33, 34, -35, 36, -37, 38 };
		
		try {
			SensorData ad = new SensorData (payload);
			byte[] adData = ad.toByteArray();
			System.out.println ("Expected: " + ad.toString());
			
			SensorData ad2 = new SensorData (adData);
			byte[] b = ad2.toByteArray();
			System.out.println ("Result:   " + ad2.toString());
			
			for (int k=0; k < b.length; k++) {
				assertEquals ("payload Array index "+k, payload[k], adData[k]);
				assertEquals ("result  Array index "+k, adData[k], b[k]);
			}
			
		} catch (CommunicationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the construction from byte arrays and the getter methods.
	 */
	public void testCase05 () {
		
		byte payload[] = { 1, 2, 3, -4, 5, 6, -7, 8, -9, 10, 11, 12, 13,
				-127, 15,	16,	-17, 18, 19, 20, 21, -22, 23, 127, -25, -26,
				27, -28, -29, -30, 31, -32, 33, 34, -35, 36, -37, 38 };
		
		try {
			SensorData ad = new SensorData (payload);
			byte[] adData = ad.toByteArray();
			System.out.println ("Expected: " + ad.toString());
			
			SensorData ad2 = new SensorData (adData);
			byte[] b = ad2.toByteArray();
			System.out.println ("Result:   " + ad2.toString());
			
			for (int k=0; k < b.length; k++) {
				assertEquals ("payload Array index "+k, payload[k], adData[k]);
				assertEquals ("result  Array index "+k, adData[k], b[k]);
			}
			
			int k = 0;
			short roll =       (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short pitch =      (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short yaw =        (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short dRoll =      (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short dPitch =     (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short dYaw =       (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short ddRoll =     (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short ddPitch =    (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short ddYaw =      (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short x =          (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short y =          (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short z =          (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short dx =         (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short dy =         (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short dz =         (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short ddx =        (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short ddy =        (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short ddz =        (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));
			short battery =    (short) ((payload[k++] << 8) | (payload[k++] & 0xFF));

			assertEquals (   roll * SensorData.ANGLE_FACTOR,     ad.getRoll (), 1E-9);
			assertEquals (  pitch * SensorData.ANGLE_FACTOR,     ad.getPitch (), 1E-9);
			assertEquals (    yaw * SensorData.ANGLE_FACTOR,     ad.getYaw (), 1E-9);

			assertEquals (  dRoll * SensorData.ANG_RATE_FACTOR,  ad.getDRoll (), 1E-9);
			assertEquals ( dPitch * SensorData.ANG_RATE_FACTOR,  ad.getDPitch (), 1E-9);
			assertEquals (   dYaw * SensorData.ANG_RATE_FACTOR,  ad.getDYaw (), 1E-9);

			assertEquals ( ddRoll * SensorData.ANG_ACCEL_FACTOR, ad.getDDRoll (), 1E-9);
			assertEquals (ddPitch * SensorData.ANG_ACCEL_FACTOR, ad.getDDPitch (), 1E-9);
			assertEquals (  ddYaw * SensorData.ANG_ACCEL_FACTOR, ad.getDDYaw (), 1E-9);

			assertEquals (      x * SensorData.LENGTH_FACTOR,    ad.getX (), 1E-9);
			assertEquals (      y * SensorData.LENGTH_FACTOR,    ad.getY (), 1E-9);
			assertEquals (      z * SensorData.LENGTH_FACTOR,    ad.getZ (), 1E-9);

			assertEquals (     dx * SensorData.VELOCITY_FACTOR,  ad.getDx (), 1E-9);
			assertEquals (     dy * SensorData.VELOCITY_FACTOR,  ad.getDy (), 1E-9);
			assertEquals (     dz * SensorData.VELOCITY_FACTOR,  ad.getDz (), 1E-9);
			
			assertEquals (    ddx * SensorData.ACCEL_FACTOR,     ad.getDDx (), 1E-9);
			assertEquals (    ddy * SensorData.ACCEL_FACTOR,     ad.getDDy (), 1E-9);
			assertEquals (    ddz * SensorData.ACCEL_FACTOR,     ad.getDDz (), 1E-9);

			assertEquals (battery * SensorData.BATTERY_FACTOR,   ad.getBattery (), 1E-9);

			assertEquals ("SensorData: roll=258, pitch=1020, yaw=1286, dRoll=-1784, dPitch=-2294, dYaw=2828, ddRoll=3457, ddPitch=3856, " +
					"ddYaw=-4334, x=4884, y=5610, z=6015, dx=-6170, dy=7140, dz=-7198, ddx=8160, ddy=8482, ddz=-8924, battery=-9434", ad.toString ());
		} catch (CommunicationException e) {
			e.printStackTrace();
			fail ();
		}
	}

}
