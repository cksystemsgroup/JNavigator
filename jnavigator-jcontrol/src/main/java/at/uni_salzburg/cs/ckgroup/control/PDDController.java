/*
 * @(#) PDDController.java
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

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This class implements PD² controller. If not available, a second derivative
 * of the control variable is estimated internally. The implementation allows
 * configuring a saturation of the proportional part of the controller.
 * 
 * @author Clemens Krainer
 */
public class PDDController implements IController {
	
	/**
	 * The property key for the proportional coefficient of the
	 * <code>PDDController</code>.
	 * 
	 * @see Kp
	 */
	public static final String PROP_CONTROLLER_KP = "Kp";
	
	/**
	 * The property key for the saturation function of the proportional part of
	 * the coefficient of the <code>PDDController</code>.
	 * 
	 * @see KpEpsilon
	 */
	public static final String PROP_CONTROLLER_KP_EPSILON = "KpEpsilon";
	
	/**
	 * The property key for the first derivative coefficient of the
	 * <code>PDDController</code>.
	 * 
	 * @see Kd
	 */
	public static final String PROP_CONTROLLER_KD = "Kd";
	
	/**
	 * The property key for the saturation function of the first derivative
	 * coefficient of the coefficient of the <code>PDDController</code>.
	 * 
	 * @see KdEpsilon
	 */
	public static final String PROP_CONTROLLER_KD_EPSILON = "KdEpsilon";
	
	/**
	 * The property key for the second derivative coefficient of the
	 * <code>PDDController</code>.
	 * 
	 * @see Kd2
	 */
	public static final String PROP_CONTROLLER_KD2 = "Kd2";
	
	/**
	 * The property key for the first derivative coefficient of the internal
	 * feedback of the <code>PDDController</code>.
	 * 
	 * @see Kdf
	 */
	public static final String PROP_CONTROLLER_KDF = "Kdf";
	
	/**
	 * The property key for the reporting counter of the
	 * <code>PDDController</code>. If greater than zero, this implementation
	 * writes a message to the standard output every <i>report counter</i>
	 * invocations of the <code>apply()</code> method.
	 * @see counter
	 * @see reportCounter
	 */
	public static final String PROP_REPORT_COUNTER = "report-counter";
	
	/**
	 * The proportional coefficient of the <code>PDDController</code>.
	 * 
	 * @see PROP_CONTROLLER_KP
	 */
	private double Kp;
	
	/**
	 * The epsilon value for the saturation function of the proportional part of
	 * the coefficient of the <code>PDDController</code>.
	 * 
	 * @see PROP_CONTROLLER_KP_EPSILON
	 */
	private double KpEpsilon;
	
	/**
	 * The first derivative coefficient of the <code>PDDController</code>.
	 * 
	 * @see PROP_CONTROLLER_KD
	 */
	private double Kd;

	/**
	 * The epsilon value for the saturation function of the first derivative
	 * coefficient of the coefficient of the <code>PDDController</code>.
	 * 
	 * @see PROP_CONTROLLER_KD_EPSILON
	 */
	private double KdEpsilon;
	
	/**
	 * The second derivative coefficient of the <code>PDDController</code>.
	 * 
	 * @see PROP_CONTROLLER_KD2
	 */
	private double Kd2;
	
	/**
	 * The first derivative coefficient of the internal feedback of the
	 * <code>PDDController</code>.
	 * 
	 * @see PROP_CONTROLLER_KDF
	 */
	private double Kdf;
	
	/**
	 * The <code>dX</code> value from a previous <code>apply()</code> method
	 * invocation.
	 */
	private double oldDX = 0;
	
	/**
	 * The old output value from a previous <code>apply()</code> method
	 * invocation.
	 */
	private double oldY = 0;
	
	/**
	 * The first derivative of the old output value from a previous
	 * <code>apply()</code> method invocation.
	 */
	private double oldDY = 0;
	
	/**
	 * The old input value from a previous <code>apply()</code> method
	 * invocation.
	 */
	private double oldError = 0;
	
	/**
	 * The reporting counter of the <code>PDDController</code>.
	 * 
	 * @see PROP_REPORT_COUNTER
	 * @see counter
	 */
	private int reportCounter;
	
	/**
	 * The current value of invocations of the <code>apply()</code> method. The
	 * implementation resets it to zero after <code>reportCounter</code>
	 * invocations.
	 * 
	 * @see PROP_REPORT_COUNTER
	 * @see reportCounter
	 */
	private int counter = 0;
	
	/**
	 * Construct a <code>PDDController</code> object.
	 * 
	 * @param props the properties to be used for construction.
	 * @throws ConfigurationException thrown in case of configuration errrors.
	 */
	public PDDController (Properties props) throws ConfigurationException {
		Kp = Double.parseDouble (props.getProperty (PROP_CONTROLLER_KP, "1"));
		KpEpsilon = Double.parseDouble (props.getProperty (PROP_CONTROLLER_KP_EPSILON, "1"));
		if (KpEpsilon == 0)
			throw new ConfigurationException ("Please set property " + PROP_CONTROLLER_KP_EPSILON + " to a non zero value.");
		Kd = Double.parseDouble (props.getProperty (PROP_CONTROLLER_KD, "0"));
		KdEpsilon = Double.parseDouble (props.getProperty (PROP_CONTROLLER_KD_EPSILON, "1"));
		if (KdEpsilon == 0)
			throw new ConfigurationException ("Please set property " + PROP_CONTROLLER_KD_EPSILON + " to a non zero value.");
		Kd2 = Double.parseDouble (props.getProperty (PROP_CONTROLLER_KD2, "0"));
		Kdf = Double.parseDouble (props.getProperty (PROP_CONTROLLER_KDF, "0"));
		reportCounter = Integer.parseInt(props.getProperty (PROP_REPORT_COUNTER, "0"));
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IController#apply(double, double, double)
	 */
	public double apply (double error, double dX, double ddX) {
		
		double xSaturated = error / KpEpsilon;
		if (xSaturated > 1) xSaturated = 1; else if (xSaturated < -1) xSaturated = -1;
		
		double dXsaturated = dX / KdEpsilon;
		if (dXsaturated > 1) dXsaturated = 1; else if (dXsaturated < -1) dXsaturated = -1;
		
		if (Double.isNaN(oldDY))
			oldDY = 0;

		double result = xSaturated * Kp + dXsaturated * Kd + ddX * Kd2 + oldDY * Kdf;
		
		if (reportCounter != 0 && ++counter > reportCounter) {
			System.out.println ("apply(): error="+ error + ", xSaturated=" + xSaturated + ", dX=" + dX + 
					", dXsaturated=" + dXsaturated + ", ddX=" + ddX + ", oldY=" + oldY +
					", oldDY=" + oldDY +					
					", result=" + result);
			counter = 0;
		}

		oldDY = result - oldY;
		oldY = result;
		return result;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IController#apply(double, double)
	 */
	public double apply (double error, double dX) {
		double result = apply (error, dX, dX - oldDX);
		oldDX = dX;
		return result;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IController#apply(double)
	 */
	public double apply (double error) {
		double result = apply (error, error - oldError);
		oldError = error;
		return result;
	}
}
