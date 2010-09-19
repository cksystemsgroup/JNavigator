/*
 * @(#) InfiniteImpulseResponseFilter.java
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
 * This class implements an Infinite Impulse Response Filter (IIR).
 * The structure of an IIR filter of order n is:
 * <pre>
 *        +---+                           +---+
 *        |   |                           |   |
 * ------>+ + +-------+-----+--[ b(0) ]-->+ + +------> y
 *        |   |             |             |   |
 *        | + |         +---+---+         | + |
 *        |   |         | z^-1  |         |   |
 *        | + |         +---+---+         | + |
 *        |   |             |             |   |
 *        | + +<--[ a(1) ]--+--[ b(1) ]-->+ + |
 *        |   |             |             |   |
 *        | + |         +---+---+         | + |
 *        |   |         | z^-1  |         |   |
 *        | + |         +---+---+         | + |
 *        |   |             |             |   |
 *        | + +<--[ a(2) ]--+--[ b(2) ]-->+ + |
 *        |   |             |             |   |
 *        | + |         +---+---+         | + |
 *        |   |         | z^-1  |         |   |
 *        | + |         +---+---+         | + |
 *        |   |             |             |   |
 *        | + +<--[ a(2) ]--+--[ b(2) ]-->+ + |
 *        |   |             |             |   |
 *          .               .               .
 *          .               .               .
 *          .               .               .
 *        |   |             |             |   |
 *        | + |         +---+---+         | + |
 *        |   |         | z^-1  |         |   |
 *        | + |         +---+---+         | + |
 *        |   |             |             |   |
 *        | + +<--[ a(n) ]--+--[ b(n) ]-->+ + |
 *        |   |                           |   |
 *        +---+                           +---+
 * </pre>
 * The feedback coefficients a(x) and the forward coefficients b(x) define the
 * characteristic of an IIR. 
 * 
 * @author Clemens Krainer
 *
 */
public class InfiniteImpulseResponseFilter implements IFilter
{
	/**
	 * Property key constant for the order of the IIR filter.
	 */
	public static final String PROP_ORDER = "order";
	
	/**
	 * Property key constant for the prefix of the filter's feedback
	 * coefficients.
	 */
	public static final String PROP_FEEDBACK_PREFIX = "feedback.";
	
	/**
	 * Property key constant for the prefix of the filter's forward
	 * coefficients.
	 */
	public static final String PROP_FORWARD_PREFIX = "forward.";
	
	/**
	 * The filter's feedback coefficients.
	 */
	private double[] a;	// a[0] not used

	/**
	 * The filter's forward coefficients.
	 */
	private double[] b;
	
	/**
	 * The input values history (delay queue).
	 */
	private double[] x;	// x[0] not used
	
	/**
	 * Construct an IIR filter from properties.
	 * 
	 * @param props the IIR filter properties.
	 * @throws ConfigurationException thrown in case of missing property items.
	 * @throws InvalidParameterException thrown if the number of forward and
	 *             feedback coefficients are incorrect.
	 */
	public InfiniteImpulseResponseFilter (Properties props) throws ConfigurationException, InvalidParameterException {
		String o = props.getProperty (PROP_ORDER);
		if (o == null || o.equals (""))
			throw new ConfigurationException ("Property order is not defined for this IIR filter.");
		
		int order = Integer.parseInt (o);
		double[] feedbackCoefficients = new double[order];
		double[] forwardCoefficients = new double[order+1];
		
		for (int k=0; k < order; k++) {
			String property = PROP_FEEDBACK_PREFIX+k;
			String value = props.getProperty (property);
			if (value == null || value.equals (""))
				throw new ConfigurationException ("Property " + property + " is not defined for this IIR filter.");
			feedbackCoefficients[k] = Double.parseDouble (value);
		}
		
		for (int k=0; k <= order; k++) {
			String property = PROP_FORWARD_PREFIX+k;
			String value = props.getProperty (property);
			if (value == null || value.equals (""))
				throw new ConfigurationException ("Property " + property + " is not defined for this IIR filter.");
			forwardCoefficients[k] = Double.parseDouble (value);			
		}
		
		init (feedbackCoefficients, forwardCoefficients);
	}

	/**
	 * Construct an IIR filter.
	 * 
	 * @param feedbackCoefficients n-1 feedback coefficients for the IIR filter
	 * @param forwardCoefficients n forward coefficients for the IIR filter
	 * @throws InvalidParameterException thrown if the number of forward and
	 *         feedback coefficients are incorrect.
	 */
	public InfiniteImpulseResponseFilter(double[] feedbackCoefficients,
			double[] forwardCoefficients) throws InvalidParameterException
	{
		init (feedbackCoefficients, forwardCoefficients);
	}

	/**
	 * Initialise an IIR filter.
	 * 
	 * @param feedbackCoefficients n-1 feedback coefficients for the IIR filter
	 * @param forwardCoefficients n forward coefficients for the IIR filter
	 * @throws InvalidParameterException thrown if the number of forward and
	 *         feedback coefficients are incorrect.
	 */
	private void init (double[] feedbackCoefficients, double[] forwardCoefficients) throws InvalidParameterException
	{
		if (feedbackCoefficients.length + 1 != forwardCoefficients.length)		
			throw new InvalidParameterException ("Length of feedback coefficient array plus one must be equal to the length of the forward coefficients");
		
		a = new double[forwardCoefficients.length];
		b = new double[forwardCoefficients.length];
		x = new double[forwardCoefficients.length];
		
		int k;
		for (k=1; k < forwardCoefficients.length; k++) {
			a[k] = feedbackCoefficients[k-1];
			b[k] = forwardCoefficients[k];
			x[k] = 0;
		}
			
		a[0] = 1;	// unused anyway
		b[0] = forwardCoefficients[0];
		x[0] = 0;	// unused anyway		
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cd.ckgroup.filter.IFilter#apply(double)
	 */
	public double apply (double value) {
		double y = 0;
		int k;

		for (k = a.length-1; k > 0; k--) {
			y += b[k]*x[k];
			value -= a[k]*x[k];
			x[k] = x[k-1];
		}
		y += b[0]*value;
		x[1] = value;

		return y;
	}
}
