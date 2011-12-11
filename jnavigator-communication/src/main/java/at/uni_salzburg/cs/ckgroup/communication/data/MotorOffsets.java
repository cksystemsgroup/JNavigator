/*
 * @(#) MotorOffsets.java
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

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;

public class MotorOffsets implements IDataTransferObject {
	
    /**
     * The roll angle offset [PWM]
     */
    private short rollOffset;

    /**
     * The pitch angle offset [PWM]
     */
    private short pitchOffset;

    /**
     * The yaw angle offset [PWM] 
     */
    private short yawOffset;
	
    /**
     * The Z offset [PWM]
     */
    private short zOffset;
    
    /**
     * The length of the payload in bytes.
     */
    static final int payloadLength = 8;
	
    /**
     * Construct an <code>MotorOffsets</code> object.
     */
    public MotorOffsets () {
    	rollOffset = pitchOffset = yawOffset = zOffset = 0;
    }
    
    /**
	 * Construct an <code>MotorOffsets</code> object.
	 * 
	 * @param rollOffset the roll angle offset [PWM]
	 * @param pitchOffset the pitch angle offset [PWM]
	 * @param yawOffset the yaw angle offset [PWM]
     * @param zOffset the Z offset [PWM]
     */
    public MotorOffsets (short rollOffset, short pitchOffset, short yawOffset, short zOffset) {
    	this.rollOffset = rollOffset;
    	this.pitchOffset = pitchOffset;
    	this.yawOffset = yawOffset;
    	this.zOffset = zOffset;
    }
	
    /**
     * Construct an <code>MotorOffsets</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public MotorOffsets (byte[] data) {
    	this (data, 0);
    }
    
    /**
     * Construct an <code>MotorOffsets</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     * @param offset the offset in the byte array where the packet starts.
     */
    public MotorOffsets (byte[] data, int offset) {
    	int k = offset;
    	rollOffset =  (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	pitchOffset = (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	yawOffset  =  (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	zOffset  =    (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    }
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray () {
		byte[] data = new byte[payloadLength];
		
		int k=0;
		data[k++] = (byte) ((rollOffset >> 8) & 0xFF);
		data[k++] = (byte) (rollOffset & 0xFF);
		data[k++] = (byte) ((pitchOffset >> 8) & 0xFF);
		data[k++] = (byte) (pitchOffset & 0xFF);
		data[k++] = (byte) ((yawOffset >> 8) & 0xFF);
		data[k++] = (byte) (yawOffset & 0xFF);
		data[k++] = (byte) ((zOffset >> 8) & 0xFF);
		data[k++] = (byte) (zOffset & 0xFF);
		
		return data;
	}

	public short getRollOffset() {
		return rollOffset;
	}

	public void setRollOffset(short rollOffset) {
		this.rollOffset = rollOffset;
	}

	public short getPitchOffset() {
		return pitchOffset;
	}

	public void setPitchOffset(short pitchOffset) {
		this.pitchOffset = pitchOffset;
	}

	public short getYawOffset() {
		return yawOffset;
	}

	public void setYawOffset(short yawOffset) {
		this.yawOffset = yawOffset;
	}

	public short getzOffset() {
		return zOffset;
	}

	public void setzOffset(short zOffset) {
		this.zOffset = zOffset;
	}

}
