/*
 * @(#) TrimValues.java
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

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;


/**
 * This class implements a container for all navigation values.
 * 
 * @author Clemens Krainer
 */
public class TrimValues implements IDataTransferObject {
	
	/**
	 * The scaling factor of the angle values.
	 */
	public static final double DEGREES_TO_MILLIRADIANTS = Math.PI/0.18;
	
    /**
     * The roll angle in milliradiants
     */
    private short roll;

    /**
     * The pitch angle in milliradiants
     */
    private short pitch;

    /**
     * The yaw angle in milliradiants 
     */
    private short yaw;
    
    /**
     * The length of the payload in bytes.
     */
    private static final int payloadLength = 6;
    
    /**
	 * Construct an <code>ActuatorData</code> object.
	 * 
	 * @param roll the roll angle in degrees
	 * @param pitch the pitch angle in degrees
	 * @param yaw the yaw angle in degrees
	 */
    public TrimValues (double roll, double pitch, double yaw) {
    	this.roll = (short) (roll * DEGREES_TO_MILLIRADIANTS);
    	this.pitch = (short) (pitch * DEGREES_TO_MILLIRADIANTS);
    	this.yaw = (short) (yaw * DEGREES_TO_MILLIRADIANTS);
    }
    
    /**
     * Construct an <code>ActuatorData</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public TrimValues (byte[] data) {
    	int k = 0;
    	roll =  (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	pitch = (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	yaw  =  (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    }
    
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray () {
		byte[] data = new byte[payloadLength];
		
		int k=0;
		data[k++] = (byte) (roll >> 8);
		data[k++] = (byte) (roll & 0xFF);
		data[k++] = (byte) (pitch >> 8);
		data[k++] = (byte) (pitch & 0xFF);
		data[k++] = (byte) (yaw >> 8);
		data[k++] = (byte) (yaw & 0xFF);
			
		return data;
	}
	
	/**
	 * @return the roll value in degrees
	 */
	public double getRoll () {
		return roll / DEGREES_TO_MILLIRADIANTS; 
	}

	/**
	 * @return the pitch value in degrees
	 */
	public double getPitch () {
		return pitch / DEGREES_TO_MILLIRADIANTS; 
	}

	/**
	 * @return the yaw value in degrees
	 */
	public double getYaw () {
		return yaw / DEGREES_TO_MILLIRADIANTS; 
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		return String.format("TrimValues: roll=%.4f, pitch=%.4f, yaw=%.4f", getRoll(), getPitch(), getYaw());
	}
}
