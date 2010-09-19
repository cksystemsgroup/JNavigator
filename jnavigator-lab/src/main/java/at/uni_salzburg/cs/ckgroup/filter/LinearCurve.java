/*
 * @(#) LinearCurve.java
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
 * This class implements a characteristic curve by means of linear
 * interpolation. To construct a curve, at least two coordinates have to be
 * supplied.
 * 
 * @author Clemens Krainer
 */
public class LinearCurve implements IFilter
{
	/**
	 * Property key constant for the order of the linear courve filter.
	 */
	public static final String PROP_ORDER = "order";
	
	/**
	 * Property key constant for the prefix of the coordinate coefficients. 
	 */
	public static final String PROP_COORDINATE_PREFIX = "coordinate.";
	
	/**
	 * The coordinates used for linear interpolation.
	 * @uml.property  name="cs"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private Coordinate[] cs;
	
	/**
	 * Construct a linear curve from <code>Properties</code>. At least two
	 * coordinates must be supported. The abscissae must be unique and ordered
	 * ascending.
	 * 
	 * @param coords the coordinates
	 * @throws InvalidParameterException thrown in case of errors.
	 */
	public LinearCurve(Properties props) throws ConfigurationException, InvalidParameterException
	{
		String o = props.getProperty (PROP_ORDER);
		if (o == null || o.equals (""))
			throw new ConfigurationException ("Property order is not defined for this linear curve filter.");
		
		int order = Integer.parseInt (o);
		if (order < 2)
			throw new InvalidParameterException ("More than two coordinates are required to construct a curve.");

		cs = new Coordinate[order];		

		for (int k=0; k < order; k++) {
			String property = PROP_COORDINATE_PREFIX+k;
			String value = props.getProperty (property);
			if (value == null || value.equals (""))
				throw new ConfigurationException ("Property " + property + " is not defined for this linear curve filter.");
			
			String values[] = value.split ("\\s+");
			if (values.length != 2)
				throw new ConfigurationException ("Property " + property + " should have two values.");
			
			double x = Double.parseDouble (values[0]);
			double y = Double.parseDouble (values[1]);
			cs[k] = new Coordinate (x, y);
		}
		
		verify ();
	}

	/**
	 * Construct a linear curve. At least two coordinates must be supported.
	 * The abscissae must be unique and ordered ascending.
	 * 
	 * @param coords the coordinates
	 * @throws InvalidParameterException thrown in case of errors.
	 */
	public LinearCurve(Coordinate[] coords) throws InvalidParameterException
	{
		if (coords.length < 2)
			throw new InvalidParameterException ("More than two coordinates are required to construct a curve.");
		
		cs = new Coordinate[coords.length];

		for (int k = 0; k < coords.length; k++)
			cs[k] = new Coordinate (coords[k].x, coords[k].y);
		
		verify ();
	}
	
	/**
	 * Verify the current curve coordinates.
	 * 
	 * @throws InvalidParameterException thrown in case of errors.
	 */
	private void verify () throws InvalidParameterException
	{		
		for (int k = 1; k < cs.length; k++)
			if (cs[k-1].x >= cs[k].x)
				throw new InvalidParameterException ("The order of provided x values must be increasing. Duplicate x values are not allowed also.");
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cd.ckgroup.filter.IFilter#apply(double)
	 */
	public double apply (double x) {
		double y = 0;

		int i = 0;
		
		while (i < cs.length)
		{
			if (cs[i].x >= x)
				break;
			i++;
		}
		if (i < cs.length && cs[i].x == x)
			return cs[i].y; 

		if (i == cs.length)
			return cs[i-1].y;
		
		if (i == 0 && x < cs[0].x)
			return cs[0].y;
			
		if (i > 0 && i < cs.length && cs[i].x >= x) {
			double k = (cs[i].y - cs[i-1].y) / (cs[i].x - cs[i-1].x);
			double d = cs[i].y - k*cs[i].x;
			y = k*x + d;
		}

		return y;
	}
}
