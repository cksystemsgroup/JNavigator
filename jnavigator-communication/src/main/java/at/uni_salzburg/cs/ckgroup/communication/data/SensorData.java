/*
 * @(#) SensorData.java
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

import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;

/**
 * This class implements a data transfer object for sensor data from the
 * JAviator hardware.
 * 
 * @author Clemens Krainer
 */
public class SensorData implements IDataTransferObject {

	public static final double ANGLE_FACTOR = 0.18/Math.PI; // (double)6283 / 65536;
	public static final double ANG_RATE_FACTOR = 0.18/Math.PI; // ((double)8500 / 32768) * 2 * Math.PI;
	public static final double ANG_ACCEL_FACTOR = 0.18/Math.PI; // ((double)8500 / 32768) * 2 * Math.PI;
	public static final double LENGTH_FACTOR = 0.001;
	public static final double VELOCITY_FACTOR = 0.01;
	public static final double ACCEL_FACTOR = (double)9810 / 4681;
	public static final double BATTERY_FACTOR = 0.001;

	/**
	 * The current roll value.
	 */
	private short roll;

	/**
	 * The current pitch value.
	 */
	private short pitch;

	/**
	 * The current yaw value.
	 */
	private short yaw;

	/**
	 * The first derivative of the current roll value.
	 */
	private short dRoll;

	/**
	 * The first derivative of the current pitch value.
	 */
	private short dPitch;

	/**
	 * The first derivative of the current yaw value.
	 */
	private short dYaw;

	/**
	 * The second derivative of the current roll value.
	 */
	private short ddRoll;

	/**
	 * The second derivative of the current pitch value.
	 */
	private short ddPitch;

	/**
	 * The second derivative of the current yaw value.
	 */
	private short ddYaw;

	/**
	 * The current X value.
	 */
	private short x;

	/**
	 * The current Y value.
	 */
	private short y;

	/**
	 * The current Z value.
	 */
	private short z;

	/**
	 * The first derivative of the current X value.
	 */
	private short dx;

	/**
	 * The first derivative of the current Y value.
	 */
	private short dy;

	/**
	 * The first derivative of the current Z value.
	 */
	private short dz;

	/**
	 * The second derivative of the current X value.
	 */
	private short ddx;

	/**
	 * The second derivative of the current Y value.
	 */
	private short ddy;

	/**
	 * The second derivative of the current Z value.
	 */
	private short ddz;

	/**
	 * The accumulator battery voltage in mV.
	 */
	private short battery;

	/**
	 * The length of this data transfer object in bytes when converted to a byte array.
	 */
	private static final int payloadLength = 38;

	/**
	 * Construct a <code>SensorData</code> data transfer object from a byte array.
	 * 
	 * @param data the byte array
	 * @throws CommunicationException thrown in case of an incorrect length of the provided data.
	 */
	public SensorData (byte[] data) throws CommunicationException {

		if (payloadLength != data.length)
			throw new CommunicationException ("Input data length of " + data.length +
					" is not equal to the expected length of " + payloadLength + " bytes");

		int k = 0;
		roll =       (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		pitch =      (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		yaw =        (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		dRoll =      (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		dPitch =     (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		dYaw =       (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		ddRoll =     (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		ddPitch =    (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		ddYaw =      (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		x =          (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		y =          (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		z =          (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		dx =         (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		dy =         (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		dz =         (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		ddx =        (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		ddy =        (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		ddz =        (short) ((data[k++] << 8) | (data[k++] & 0xFF));
		battery =    (short) ((data[k++] << 8) | (data[k++] & 0xFF));
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray () {
		byte[] data = new byte[payloadLength];
		int k=0;
		data[k++] = (byte) (roll >> 8);			data[k++] = (byte) (roll & 0xFF);
		data[k++] = (byte) (pitch >> 8);		data[k++] = (byte) (pitch & 0xFF);
		data[k++] = (byte) (yaw >> 8);			data[k++] = (byte) (yaw & 0xFF);
		data[k++] = (byte) (dRoll >> 8);		data[k++] = (byte) (dRoll & 0xFF);
		data[k++] = (byte) (dPitch >> 8);		data[k++] = (byte) (dPitch & 0xFF);
		data[k++] = (byte) (dYaw >> 8);			data[k++] = (byte) (dYaw & 0xFF);
		data[k++] = (byte) (ddRoll >> 8);		data[k++] = (byte) (ddRoll & 0xFF);
		data[k++] = (byte) (ddPitch >> 8);		data[k++] = (byte) (ddPitch & 0xFF);
		data[k++] = (byte) (ddYaw >> 8);		data[k++] = (byte) (ddYaw & 0xFF);
		data[k++] = (byte) (x >> 8);			data[k++] = (byte) (x & 0xFF);
		data[k++] = (byte) (y >> 8);			data[k++] = (byte) (y & 0xFF);
		data[k++] = (byte) (z >> 8);			data[k++] = (byte) (z & 0xFF);
		data[k++] = (byte) (dx >> 8);			data[k++] = (byte) (dx & 0xFF);
		data[k++] = (byte) (dy >> 8);			data[k++] = (byte) (dy & 0xFF);
		data[k++] = (byte) (dz >> 8);			data[k++] = (byte) (dz & 0xFF);
		data[k++] = (byte) (ddx >> 8);			data[k++] = (byte) (ddx & 0xFF);
		data[k++] = (byte) (ddy >> 8);			data[k++] = (byte) (ddy & 0xFF);
		data[k++] = (byte) (ddz >> 8);			data[k++] = (byte) (ddz & 0xFF);
		data[k++] = (byte) (battery >> 8);		data[k++] = (byte) (battery & 0xFF);
		return data;
	}


	/**
	 * @return the current roll value in degrees.
	 */
	public double getRoll () {
		return roll * ANGLE_FACTOR;
	}

	/**
	 * @return the current pitch value in degrees.
	 */
	public double getPitch () {
		return pitch * ANGLE_FACTOR;
	}

	/**
	 * @return the current yaw value in degrees.
	 */
	public double getYaw () {
		return yaw * ANGLE_FACTOR;
	}

	/**
	 * @return the current first derivative of the roll value in degrees per second.
	 */
	public double getDRoll () {
		return dRoll * ANG_RATE_FACTOR;
	}

	/**
	 * @return the current first derivative of the pitch value in degrees per second.
	 */
	public double getDPitch () {
		return dPitch * ANG_RATE_FACTOR;
	}

	/**
	 * @return the current first derivative of the yaw value in degrees per second.
	 */
	public double getDYaw () {
		return dYaw * ANG_RATE_FACTOR;
	}

	/**
	 * @return the current second derivative of the roll value in degrees per second.
	 */
	public double getDDRoll () {
		return ddRoll * ANG_ACCEL_FACTOR;
	}

	/**
	 * @return the current second derivative of the pitch value in degrees per second.
	 */
	public double getDDPitch () {
		return ddPitch * ANG_ACCEL_FACTOR;
	}

	/**
	 * @return the current second derivative of the yaw value in degrees per second.
	 */
	public double getDDYaw () {
		return ddYaw * ANG_ACCEL_FACTOR;
	}

	/**
	 * @return the current X value in meters.
	 */
	public double getX () {
		return x * LENGTH_FACTOR;
	}

	/**
	 * @return the current Y value in meters.
	 */
	public double getY () {
		return y * LENGTH_FACTOR;
	}

	/**
	 * @return the current Z value in meters.
	 */
	public double getZ () {
		return z * LENGTH_FACTOR;
	}

	/**
	 * @return the first derivative of the current X value in meters per second.
	 */
	public double getDx () {
		return dx * VELOCITY_FACTOR;
	}

	/**
	 * @return the first derivative of the current Y value in meters per second.
	 */
	public double getDy () {
		return dy * VELOCITY_FACTOR;
	}

	/**
	 * @return the first derivative of the current Z value in meters per second.
	 */
	public double getDz () {
		return dz * VELOCITY_FACTOR;
	}

	/**
	 * @return the second derivative of the current X value in meters per square second.
	 */
	public double getDDx () {
		return ddx * ACCEL_FACTOR;
	}

	/**
	 * @return the second derivative of the current Y value in meters per square second.
	 */
	public double getDDy () {
		return ddy * ACCEL_FACTOR;
	}

	/**
	 * @return the second derivative of the current Z value in meters per square second.
	 */
	public double getDDz () {
		return ddz * ACCEL_FACTOR;
	}

	/**
	 * @return the accumulator battery condition
	 */
	public double getBattery () {
		return battery * BATTERY_FACTOR;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append("SensorData: roll=").append(roll);
		buf.append(", pitch=").append(pitch);
		buf.append(", yaw=").append(yaw);
		buf.append(", dRoll=").append(dRoll);
		buf.append(", dPitch=").append(dPitch);
		buf.append(", dYaw=").append(dYaw);
		buf.append(", ddRoll=").append(ddRoll);
		buf.append(", ddPitch=").append(ddPitch);
		buf.append(", ddYaw=").append(ddYaw);
		buf.append(", x=").append(x);
		buf.append(", y=").append(y);
		buf.append(", z=").append(z);
		buf.append(", dx=").append(dx);
		buf.append(", dy=").append(dy);
		buf.append(", dz=").append(dz);
		buf.append(", ddx=").append(ddx);
		buf.append(", ddy=").append(ddy);
		buf.append(", ddz=").append(ddz);
		buf.append(", battery=").append(battery);
		return buf.toString();
	}
}
