/*
 * @(#) ControllerParameters.java
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

import java.util.Locale;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;

/**
 * This abstract class contains all control parameters for one controller of the
 * <code>JControl</code> control algorithm.
 * 
 * @author Clemens Krainer
 */
public abstract class ControllerParameters implements IDataTransferObject {
	
	public static final String PROP_KP = "Kp";
	public static final String PROP_KI = "Ki";
	public static final String PROP_KD = "Kd";
	public static final String PROP_KDD = "Kdd";

	private short kProportional;
	private short kIntegral;
	private short kDerivative;
	private short kSecondDerivative;

	private static final int payloadLength = 8;
	
	private static final double FACTOR_PARAMETER = 1000.0;
	
	/**
	 * @param props
	 * @param prefix
	 */
	public ControllerParameters(Properties props, String prefix) {
		this (
			Double.parseDouble(props.getProperty(prefix+PROP_KP, "0")),
			Double.parseDouble(props.getProperty(prefix+PROP_KI, "0")),
			Double.parseDouble(props.getProperty(prefix+PROP_KD, "0")),
			Double.parseDouble(props.getProperty(prefix+PROP_KDD, "0"))
		);
	}

	/**
	 * @param kProportional
	 * @param kIntegral
	 * @param kDerivative
	 * @param kSecondDerivative
	 */
	public ControllerParameters(double kProportional, double kIntegral, double kDerivative, double kSecondDerivative) {
		setKProportional(kProportional);
		setKIntegral(kIntegral);
		setKDerivative(kDerivative);
		setKSecondDerivative(kSecondDerivative);
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

	public void saveParameters (Properties props, String prefix) {
		props.setProperty(prefix+PROP_KP,  String.format(Locale.US, "%.3f", getKProportional()));
		props.setProperty(prefix+PROP_KI,  String.format(Locale.US, "%.3f", getKIntegral()));
		props.setProperty(prefix+PROP_KD,  String.format(Locale.US, "%.3f", getKDerivative()));
		props.setProperty(prefix+PROP_KDD, String.format(Locale.US, "%.3f", getKSecondDerivative()));
	}
	
	/**
	 * @return
	 */
	public double getKProportional() {
		return kProportional /  FACTOR_PARAMETER;
	}

	public void setKProportional(double kProportional) {
		this.kProportional = (short) (kProportional * FACTOR_PARAMETER);
	}

	/**
	 * @return
	 */
	public double getKIntegral() {
		return kIntegral /  FACTOR_PARAMETER;
	}

	public void setKIntegral(double kIntegral) {
		this.kIntegral = (short) (kIntegral * FACTOR_PARAMETER);
	}

	/**
	 * @return
	 */
	public double getKDerivative() {
		return kDerivative /  FACTOR_PARAMETER;
	}

	public void setKDerivative(double kDerivative) {
		this.kDerivative = (short) (kDerivative * FACTOR_PARAMETER);
	}

	/**
	 * @return
	 */
	public double getKSecondDerivative() {
		return kSecondDerivative /  FACTOR_PARAMETER;
	}

	public void setKSecondDerivative(double kSecondDerivative) {
		this.kSecondDerivative = (short) (kSecondDerivative * FACTOR_PARAMETER);
	}

	
}
