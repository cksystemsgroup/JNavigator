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
	
	public static final double ANGLE_FACTOR_NEW = 0.001;

	/**
	 * The current roll value.
	 */
	private short roll = 0;

	/**
	 * The current pitch value.
	 */
	private short pitch = 0;

	/**
	 * The current yaw value.
	 */
	private short yaw = 0;

	/**
	 * The first derivative of the current roll value.
	 */
	private short dRoll = 0;

	/**
	 * The first derivative of the current pitch value.
	 */
	private short dPitch = 0;

	/**
	 * The first derivative of the current yaw value.
	 */
	private short dYaw = 0;

	/**
	 * The second derivative of the current roll value.
	 */
	private short ddRoll = 0;

	/**
	 * The second derivative of the current pitch value.
	 */
	private short ddPitch = 0;

	/**
	 * The second derivative of the current yaw value.
	 */
	private short ddYaw = 0;

	/**
	 * The current X value.
	 */
	private short x = 0;

	/**
	 * The current Y value.
	 */
	private short y = 0;

	/**
	 * The current Z value.
	 */
	private short z = 0;

	/**
	 * The first derivative of the current X value.
	 */
	private short dx = 0;

	/**
	 * The first derivative of the current Y value.
	 */
	private short dy = 0;

	/**
	 * The first derivative of the current Z value.
	 */
	private short dz = 0;

	/**
	 * The second derivative of the current X value.
	 */
	private short ddx = 0;

	/**
	 * The second derivative of the current Y value.
	 */
	private short ddy = 0;

	/**
	 * The second derivative of the current Z value.
	 */
	private short ddz = 0;

	/**
	 * The accumulator battery voltage in mV.
	 */
	private short battery = 0;

	/**
	 * The length of this data transfer object in bytes when converted to a byte array.
	 */
	private static final int payloadLength = 38;
	
	/**
	 * Construct an empty <code>SensorData</code> data transfer object.
	 */
	public SensorData () {
		// Intentionally empty
	}
	
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

	public double getdRoll() {
		return dRoll * ANGLE_FACTOR_NEW;
	}

	public void setdRoll(double dRoll) {
		this.dRoll = (short) (dRoll / ANGLE_FACTOR_NEW);
	}

	public double getdPitch() {
		return dPitch * ANGLE_FACTOR_NEW;
	}

	public void setdPitch(double dPitch) {
		this.dPitch = (short) (dPitch / ANGLE_FACTOR_NEW);
	}

	public double getdYaw() {
		return dYaw * ANGLE_FACTOR_NEW;
	}

	public void setdYaw(double dYaw) {
		this.dYaw = (short) (dYaw / ANGLE_FACTOR_NEW);
	}

	public double getDdRoll() {
		return ddRoll * ANGLE_FACTOR_NEW;
	}

	public void setDdRoll(double ddRoll) {
		this.ddRoll = (short) (ddRoll / ANGLE_FACTOR_NEW);
	}

	public double getDdPitch() {
		return ddPitch * ANGLE_FACTOR_NEW;
	}

	public void setDdPitch(double ddPitch) {
		this.ddPitch = (short) (ddPitch / ANGLE_FACTOR_NEW);
	}

	public double getDdYaw() {
		return ddYaw * ANGLE_FACTOR_NEW;
	}

	public void setDdYaw(double ddYaw) {
		this.ddYaw = (short) (ddYaw / ANGLE_FACTOR_NEW);
	}

	public double getDdx() {
		return ddx * LENGTH_FACTOR;
	}

	public void setDdx(double ddx) {
		this.ddx = (short) (ddx / LENGTH_FACTOR);
	}

	public double getDdy() {
		return ddy * LENGTH_FACTOR;
	}

	public void setDdy(double ddy) {
		this.ddy = (short) (ddy / LENGTH_FACTOR);
	}

	public double getDdz() {
		return ddz * LENGTH_FACTOR;
	}

	public void setDdz(double ddz) {
		this.ddz = (short) (ddz / LENGTH_FACTOR);
	}

	public void setRoll(double roll) {
		this.roll = (short) (roll / ANGLE_FACTOR_NEW);
	}

	public void setPitch(double pitch) {
		this.pitch = (short) (pitch / ANGLE_FACTOR_NEW);
	}

	public void setYaw(double yaw) {
		this.yaw = (short) (yaw / ANGLE_FACTOR_NEW);
	}

	public void setX(double x) {
		this.x = (short) (x / LENGTH_FACTOR);
	}

	public void setY(double y) {
		this.y = (short) (y / LENGTH_FACTOR);
	}

	public void setZ(double z) {
		this.z = (short) (z / LENGTH_FACTOR);
	}

	public void setDx(double dx) {
		this.dx = (short) (dx / LENGTH_FACTOR);
	}

	public void setDy(double dy) {
		this.dy = (short) (dy / LENGTH_FACTOR);
	}

	public void setDz(double dz) {
		this.dz = (short) (dz / LENGTH_FACTOR);
	}

	public void setBattery(short battery) {
		this.battery = battery;
	}
}
