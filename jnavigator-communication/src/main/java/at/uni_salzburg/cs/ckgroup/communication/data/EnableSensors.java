/*
 * @(#) EnableSensors.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2010  Clemens Krainer
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


public class EnableSensors implements IDataTransferObject {

	/**
	 * The one byte data. 
	 */
	protected byte data;
    
	/**
     * The length of the payload in bytes.
     */
    private static final int payloadLength = 1;
    
	/**
	 * Construct an <code>EnableSensors</code> object.
	 * 
	 * @param mode the enable sensors flag
	 */
	public EnableSensors (boolean mode) {
		this.data = (byte)(mode ? 1 : 0);
	}
    
    /**
     * Construct an <code>OneByteData</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public EnableSensors (byte[] data) {
    	this.data = data[0];
    }
    
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray() {
		byte[] data = new byte[payloadLength];
		data[0] = this.data;
		return data;
	}
	
	/**
	 * @return true in the sensors are enabled, false otherwise. 
	 */
	public boolean getEnableSensors () {
		return data != 0;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append("EnableSensors: mode=").append(data != 0);
		return buf.toString();
	}
}
