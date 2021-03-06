/*
 * @(#) MotorSignals.java
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
 * This class implements a container for all actuator values.
 * 
 * @author Clemens Krainer
 */
public class MotorSignals implements IDataTransferObject {
	
    /**
     * The front motor speed in RPM
     */
    private short front;

    /**
     * The right motor speed in RPM
     */
    private short right;

    /**
     * The rear motor speed in RPM
     */
    private short rear;

    /**
     * The left motor speed in RPM
     */
    private short left;

    /**
     * The JAviator plant identification
     */
    private short id;
    
    /**
     * The length of the payload in bytes.
     */
    static final int payloadLength = 10;
    
    /**
     * Construct an <code>MotorSignals</code> object, having all parameters set to zero.
     */
    public MotorSignals () {
    	front = right = rear = left = id = 0;
    }
    
    /**
	 * Construct an <code>MotorSignals</code> object.
	 * 
	 * @param front the front motor speed in RPM
	 * @param right the right motor speed in RPM
	 * @param rear the rear motor speed in RPM
	 * @param left the left motor speed in RPM
	 */
    public MotorSignals (short front, short right, short rear, short left, int id) {
    	this.front = front;
    	this.right = right;
    	this.rear = rear;
    	this.left = left;
    	this.id = (short)id;
    }
    
    /**
     * Construct an <code>MotorSignals</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public MotorSignals (byte[] data) {
    	this (data, 0);
    }
    
    /**
     * Construct an <code>MotorSignals</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     * @param offset the offset in the byte array where the packet starts.
     */
    public MotorSignals (byte[] data, int offset) {
    	int k = offset;
    	front = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
    	right = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
    	rear  = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
    	left  = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
        id    = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
    }
    
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray () {
		byte[] data = new byte[payloadLength];
		
		int k=0;
		data[k++] = (byte) ((front >> 8) & 0xFF);
		data[k++] = (byte) (front & 0xFF);
		data[k++] = (byte) ((right >> 8) & 0xFF);
		data[k++] = (byte) (right & 0xFF);
		data[k++] = (byte) ((rear >> 8) & 0xFF);
		data[k++] = (byte) (rear & 0xFF);
		data[k++] = (byte) ((left >> 8) & 0xFF);
		data[k++] = (byte) (left & 0xFF);
		data[k++] = (byte) ((id >> 8) & 0xFF);
		data[k++] = (byte) (id & 0xFF);
			
		return data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append("MotorSignals: front=").append(front)
			.append(", right=").append(right)
			.append(", rear=").append(rear)
			.append(", left=").append(left)
			.append(", id=").append(id);
		return buf.toString();
	}

	public short getFront() {
		return front;
	}

	public void setFront(short front) {
		this.front = front;
	}

	public short getRight() {
		return right;
	}

	public void setRight(short right) {
		this.right = right;
	}

	public short getRear() {
		return rear;
	}

	public void setRear(short rear) {
		this.rear = rear;
	}

	public short getLeft() {
		return left;
	}

	public void setLeft(short left) {
		this.left = left;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}
}
