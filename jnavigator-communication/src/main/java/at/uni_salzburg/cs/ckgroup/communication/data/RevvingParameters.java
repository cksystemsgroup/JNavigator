/*
 * @(#) RevvingParameters.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * CopyrevvingUpStep (c) 2009  Clemens Krainer
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
 * This class implements a container for revving parameter values.
 * 
 * @author Clemens Krainer
 */
public class RevvingParameters implements IDataTransferObject {

	/**
	 * The idle speed limit in ...
	 */
	private short idleSpeedLimit;

	/**
	 * The revving-up step in ...
	 */
	private short revvingUpStep;

	/**
	 * The revving-down step in ...
	 */
	private short revvingDownStep;

	/**
	 * The integral-down step in ...
	 */
	private short integralDownStep;

	/**
	 * The length of the payload in bytes.
	 */
	private static final int payloadLength = 10;

	public RevvingParameters(double idleSpeedLimit, double revvingUpStep,
			double revvingDownStep, double integralDownStep) {
		this.idleSpeedLimit = (short) idleSpeedLimit;
		this.revvingUpStep = (short) revvingUpStep;
		this.revvingDownStep = (short) revvingDownStep;
		this.integralDownStep = (short) integralDownStep;
	}

	/**
	 * Construct an <code>MotorSignals</code> object from a byte array.
	 * 
	 * @param data
	 *            the byte array that contains the data.
	 */
	public RevvingParameters(byte[] data) {
		int k = 0;
		idleSpeedLimit = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
		revvingUpStep = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
		revvingDownStep = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
		integralDownStep = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray() {
		byte[] data = new byte[payloadLength];

		int k = 0;
		data[k++] = (byte) (idleSpeedLimit >> 8);
		data[k++] = (byte) (idleSpeedLimit & 0xFF);
		data[k++] = (byte) (revvingUpStep >> 8);
		data[k++] = (byte) (revvingUpStep & 0xFF);
		data[k++] = (byte) (revvingDownStep >> 8);
		data[k++] = (byte) (revvingDownStep & 0xFF);
		data[k++] = (byte) (integralDownStep >> 8);
		data[k++] = (byte) (integralDownStep & 0xFF);

		return data;
	}

}
