/*
 * @(#) YawControllerParameters.java
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

/**
 * This class contains all control parameters for the yaw controller of the
 * <code>JControl</code> control algorithm.
 * 
 * @author Clemens Krainer
 */
public class YawControllerParameters extends ControllerParameters {

	/**
	 * @param kProportional
	 * @param kIntegral
	 * @param kDerivative
	 * @param kSecondDerivative
	 */
	public YawControllerParameters(double kProportional, double kIntegral,
			double kDerivative, double kSecondDerivative) {
		super(kProportional, kIntegral, kDerivative, kSecondDerivative);
	}

	/**
	 * @param data
	 * @throws CommunicationException
	 */
	public YawControllerParameters(byte[] data) throws CommunicationException {
		super(data);
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.data.ControllerParameters#toString()
	 */
	public String toString() {
		return "YawControllerParameters: " + super.toString();
	}
}
