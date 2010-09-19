/*
 * @(#) ControlParams.java
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
 * This abstract class contains all control parameters for one controller of the
 * <code>JControl</code> control algorithm.
 * 
 * @author Clemens Krainer
 */
public abstract class ControllerParameters implements IDataTransferObject {

	private short kProportional;
	private short kIntegral;
	private short kDerivative;
	private short kSecondDerivative;

	private static final int payloadLength = 8;

	/**
	 * @param kProportional
	 * @param kIntegral
	 * @param kDerivative
	 * @param kSecondDerivative
	 */
	public ControllerParameters(double kProportional, double kIntegral,
			double kDerivative, double kSecondDerivative) {

		this.kProportional = (short) kProportional;
		this.kIntegral = (short) kIntegral;
		this.kDerivative = (short) kDerivative;
		this.kSecondDerivative = (short) kSecondDerivative;
	}

	/**
	 * @param data
	 * @throws CommunicationException
	 */
	public ControllerParameters(byte[] data) throws CommunicationException {

		if (payloadLength != data.length)
			throw new CommunicationException("Input data length of "
					+ data.length + " is not equal to the expected length of "
					+ payloadLength + " bytes");

		int k = 0;
		kProportional = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
		kIntegral = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
		kDerivative = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
		kSecondDerivative = (short) ((data[k++] & 0xFF) << 8 | data[k++] & 0xFF);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray() {
		byte[] data = new byte[payloadLength];
		int k = 0;
		data[k++] = (byte) (kProportional >> 8);
		data[k++] = (byte) (kProportional & 0xFF);
		data[k++] = (byte) (kIntegral >> 8);
		data[k++] = (byte) (kIntegral & 0xFF);
		data[k++] = (byte) (kDerivative >> 8);
		data[k++] = (byte) (kDerivative & 0xFF);
		data[k++] = (byte) (kSecondDerivative >> 8);
		data[k++] = (byte) (kSecondDerivative & 0xFF);
		return data;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("kProportional=").append(kProportional);
		buf.append(", kIntegral=").append(kIntegral);
		buf.append(", kDerivative=").append(kDerivative);
		buf.append(", kSecondDerivative=").append(kSecondDerivative);
		return buf.toString();
	}

	/**
	 * @return
	 */
	public double getKProportional() {
		return kProportional;
	}

	/**
	 * @return
	 */
	public double getKIntegral() {
		return kIntegral;
	}

	/**
	 * @return
	 */
	public double getKDerivative() {
		return kDerivative;
	}

	/**
	 * @return
	 */
	public double getKSecondDerivative() {
		return kSecondDerivative;
	}

}
