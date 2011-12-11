/*
 * @(#) IdleLimit.java
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


public class IdleLimit implements IDataTransferObject {

	/**
	 * The scaling factor of the idle limit value.
	 */
	static final double LIMIT_FACTOR = 1.0;
	
	/**
	 * The idle limit. 
	 */
	private short limit;
    
	/**
     * The length of the payload in bytes.
     */
    private static final int payloadLength = 2;
    
    /**
     * Construct an <code>IdleLimit</code> object.
     * 
     * @param limit the idle limit
     */
    public IdleLimit (double limit) {
    	this.limit = (short) (limit * LIMIT_FACTOR);
    }
    
    /**
     * Construct an <code>IdleLimit</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public IdleLimit (byte[] data) {
    	int k = 0;
    	limit = (short) ((0xFF & data[k++]) << 8 | (0xFF & data[k++]));
    }
    
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray() {
		byte[] data = new byte[payloadLength];
		
		int k=0;
		data[k++] = (byte) (limit >> 8);
		data[k++] = (byte) (limit & 0xFF);
		return data;
	}
	
	/**
	 * @return the limit value
	 */
	public double getLimit () {
		return limit / LIMIT_FACTOR;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append("IdleLimit: limit=").append(limit);
		return buf.toString();
	}
}
