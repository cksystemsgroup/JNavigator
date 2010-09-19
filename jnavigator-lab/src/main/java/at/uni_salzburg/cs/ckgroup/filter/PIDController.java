/*
 * @(#) PIDController.java
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
package at.uni_salzburg.cs.ckgroup.filter;

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This class implements a proportional–integral–derivative controller (PID
 * controller). The integral part of this controller has a maximum and a minimum
 * state limit. The initial state of the integral part is configurable. Note
 * that the maximum, minimum and initial state of the integral part are not
 * amplified by the integrator gain.
 * 
 * @author Clemens Krainer
 */
public class PIDController implements IFilter
{
	/**
	 * Property key constant for the integrator maximum.
	 */
	public static final String PROP_INTEGRATOR_MAXIMUM = "integrator.maximum";
	
	/**
	 * Property key constant for the integrator maximum.
	 */
	public static final String PROP_INTEGRATOR_MINIMUM = "integrator.minimum";
	
	/**
	 * Property key constant for the integrator initial state.
	 */
	public static final String PROP_INTEGRATOR_INITIAL_STATE = "integrator.initial.state";
	
	/**
	 * Property key constant for the integrator gain.
	 */
	public static final String PROP_INTEGRATOR_GAIN = "integrator.gain";
	
	/**
	 * Property key constant for the proportional gain.
	 */
	public static final String PROP_PROPORTIONAL_GAIN = "proportional.gain";
	
	/**
	 * Property key constant for the first deriative gain.
	 */
	public static final String PROP_FIRST_DERIATIVE_GAIN = "deriative.gain";
	
	/**
	 * Property key constant for the second deriative gain.
	 */
	public static final String PROP_SECOND_DERIATIVE_GAIN = "second.deritative.gain";
	
	/**
	 * The maximum allowable integrator state. Default is 100.
	 */
	double integratorMaximum;
	
	/**
	 * The minimum allowable integrator state. Default is -100
	 */
	double integratorMinimum;
	
	/**
	 * The gain of the integrator term. Default is 0.
	 */
	double	integratorGain;
	
	/**
	 * The gain of the proportional term. Default is 0.
	 */
	double proportionalGain;
	
	/**
	 * The gain of the first deriative term. Default is 0.
	 */
	double firstDeriativeGain;
	
	/**
	 * The gain of the second deriative term. Default is 0.
	 */
	double secondDeriativeGain;
	
	/**
	 * The integrator state. Default start value is 0.   
	 */
	double integratorState;

	/**
	 * The last error state.
	 */
	double lastErrorState = 0;
		
	/**
	 * The last error state.
	 */
	double lastDerivedErrorState = 0;
	
	/**
	 * Construct an PIDController from properties
	 * 
	 * @param props the PIDController properties
	 * @throws ConfigurationException thrown if all three gain values are zero.
	 */
	public PIDController (Properties props) throws ConfigurationException {
		integratorMaximum = Double.parseDouble(props.getProperty (PROP_INTEGRATOR_MAXIMUM, "100"));
		integratorMinimum = Double.parseDouble(props.getProperty (PROP_INTEGRATOR_MINIMUM, "-100"));
		integratorState = Double.parseDouble(props.getProperty (PROP_INTEGRATOR_INITIAL_STATE, "0"));
		integratorGain = Double.parseDouble(props.getProperty (PROP_INTEGRATOR_GAIN, "0"));
		proportionalGain = Double.parseDouble(props.getProperty (PROP_PROPORTIONAL_GAIN, "0"));
		firstDeriativeGain = Double.parseDouble(props.getProperty (PROP_FIRST_DERIATIVE_GAIN, "0"));
		secondDeriativeGain = Double.parseDouble(props.getProperty (PROP_SECOND_DERIATIVE_GAIN, "0"));
		
		if (integratorGain == 0 && proportionalGain == 0 && firstDeriativeGain == 0 && secondDeriativeGain == 0)
			throw new ConfigurationException ("At least one of the properties '" + PROP_INTEGRATOR_GAIN +
					"', '" + PROP_PROPORTIONAL_GAIN + "', '" + PROP_FIRST_DERIATIVE_GAIN + "', '" + PROP_SECOND_DERIATIVE_GAIN +
					"' should have a non zero value.");
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.filter.IFilter#apply(double)
	 */
	public double apply (double value) {
		
		integratorState += value;
		
		if (integratorState > integratorMaximum)
			integratorState = integratorMaximum;
		else
			if (integratorState < integratorMinimum)
				integratorState = integratorMinimum;

		double derivedErrorState = value - lastErrorState;
		double secondDerivedErrorState = derivedErrorState - lastDerivedErrorState;
		double result = proportionalGain*value + integratorGain*integratorState + firstDeriativeGain*derivedErrorState + secondDeriativeGain*secondDerivedErrorState;
		
		lastDerivedErrorState = derivedErrorState;
		lastErrorState = value;
		return result;
	}

}
