/*
 * @(#) IController.java
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
package at.uni_salzburg.cs.ckgroup.control;

/**
 * This interface summarizes the functionality of a controller.
 * 
 * @author Clemens Krainer
 */
public interface IController {
	
	/**
	 * Apply the currently available error value, as well as its first and
	 * second derivative to the control algorithm.
	 * 
	 * @param error the current control deviation.
	 * @param dX the current first derivative
	 * @param ddX the current second derivative
	 * @return the new controller output value
	 */
	public double apply (double error, double dX, double ddX);
	
	/**
	 * Apply the currently available error value, as well as its first
	 * derivative to the control algorithm. The controller may estimate
	 * the second derivative internally.
	 * 
	 * @param error the current control deviation.
	 * @param dX the current first derivative
	 * @return the new controller output value
	 */
	public double apply (double error, double dX);

	/**
	 * Apply the currently available error value to the control algorithm.
	 * The controller may estimate first and the second derivative internally.
	 * 
	 * @param error the current control deviation.
	 * @return the new controller output value
	 */
	public double apply (double error);

}
