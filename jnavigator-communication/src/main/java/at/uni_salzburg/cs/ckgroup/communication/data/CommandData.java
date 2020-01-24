/*
 * @(#) CommandData.java
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
public class CommandData implements IDataTransferObject {
	
	/**
	 * Convert the binary representation of angles to degrees.
	 */
	public static final double ANGLE_FACTOR = Math.PI/0.18; // 6.283 / 65536;
	
	/**
	 * The scaling factor of the angle values.
	 */
	public static final double DEGREES_TO_MILLIRADIANTS = Math.PI/0.18;

	/**
	 * The scaling factor of the altitude values.
	 */
	public static final double METERS_TO_MILLIMETERS = 1000.0;
	
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
     * The height above ground in centimeters
     */
    private short heightAboveGround;
    
    /**
     * The length of the payload in bytes.
     */
    private static final int payloadLength = 8;
    
    /**
	 * Construct an <code>CommandData</code> object.
	 * 
	 * @param roll the roll angle in degrees
	 * @param pitch the pitch angle in degrees
	 * @param yaw the yaw angle in degrees
	 * @param heightAboveGround the height above ground in meters
	 */
    public CommandData (double roll, double pitch, double yaw, double heightAboveGround) {
    	this.roll = (short) (roll * ANGLE_FACTOR);
    	this.pitch = (short) (pitch * ANGLE_FACTOR);
    	this.yaw = (short) (yaw * ANGLE_FACTOR);
    	this.heightAboveGround = (short) (heightAboveGround * METERS_TO_MILLIMETERS);
    }
    
    /**
     * Construct an <code>CommandData</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public CommandData (byte[] data) {
    	int k = 0;
    	roll =  (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	pitch = (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	yaw  =  (short) ((data[k++] << 8) | (data[k++] & 0xFF));
    	heightAboveGround  = (short) ((data[k++] << 8) | (data[k++] & 0xFF));
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
		data[k++] = (byte) (heightAboveGround >> 8);
		data[k++] = (byte) (heightAboveGround & 0xFF);
			
		return data;
	}
	
	/**
	 * @return the roll value in degrees
	 */
	public double getRoll () {
		return roll / ANGLE_FACTOR; 
	}

	/**
	 * @return the pitch value in degrees
	 */
	public double getPitch () {
		return pitch / ANGLE_FACTOR; 
	}

	/**
	 * @return the yaw value in degrees
	 */
	public double getYaw () {
		return yaw / ANGLE_FACTOR; 
	}

	/**
	 * @return the height over ground in meters
	 */
	public double getHeightOverGround () {
		return heightAboveGround / METERS_TO_MILLIMETERS;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
//		DecimalFormat df = new DecimalFormat ("#0.####");
//		DecimalFormatSymbols dfSymbols = df.getDecimalFormatSymbols();
//		dfSymbols.setDecimalSeparator('.');
//		dfSymbols.setGroupingSeparator(',');
//		StringBuffer buf = new StringBuffer ();
//		buf.append("CommandData: roll=").append(df.format(getRoll ()));
//		buf.append(", pitch=").append(df.format(getPitch ()));
//		buf.append(", yaw=").append(df.format(getYaw ()));
//		buf.append(", height above ground=").append(df.format(getHeightOverGround ()));
//		return buf.toString();
	    return String.format("CommandData: roll=%.4f, pitch=%.4f, yaw=%.4f, height above ground=%.0f",
	        getRoll(), getPitch(), getYaw(), getHeightOverGround());
	}
}
