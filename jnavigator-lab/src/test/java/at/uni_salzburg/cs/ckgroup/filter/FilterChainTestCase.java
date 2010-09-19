/*
 * @(#) FilterChainTestCase.java
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
import at.uni_salzburg.cs.ckgroup.filter.Coordinate;
import at.uni_salzburg.cs.ckgroup.filter.FilterChain;
import at.uni_salzburg.cs.ckgroup.filter.IFilter;
import at.uni_salzburg.cs.ckgroup.filter.InvalidParameterException;
import at.uni_salzburg.cs.ckgroup.filter.LinearCurve;
import junit.framework.TestCase;

/**
 * This test verifies the implementation of the Filterchain class.
 * 
 * @author Clemens Krainer
 *
 */
public class FilterChainTestCase extends TestCase
{
	/**
	 * This test uses a series of five filters to verify the implementation of
	 * the FilterChain class constructed with the default constructor.
	 * 
	 * @see testCase02
	 */
	public void testCase01 () {
		double[][] v = { { 0, 0 }, { 1, 0.03125 }, { 2, 0.0625 }, { 3, 0.09375 } };
		
		Coordinate[] c = new Coordinate[2];
		c[0] = new Coordinate (0, 0);
		c[1] = new Coordinate (20, 10);
		
		try
		{
			IFilter filter = new LinearCurve (c);

			FilterChain chain = new FilterChain ();
			chain.addFilter (filter);
			chain.addFilter (filter);
			chain.addFilter (filter);
			chain.addFilter (filter);
			chain.addFilter (filter);
			
			for (int k = 0; k < v.length; k++) {
//				System.out.println ("Filter chain: in="+v[k][0]+", out="+v[k][1]+", filter="+chain.apply (v[k][0]));
				assertEquals ("v=(" + v[k][0] + "," + v[k][1] + ")", v[k][1], chain.apply (v[k][0]), 0);
			}
		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	/**
	 * This test uses a series of five filters to verify the implementation of
	 * the FilterChain class constructed with the properties constructor.
	 * 
	 * @see testCase01
	 */
	public void testCase02 () {
		
		Properties props = new Properties ();
		props.setProperty ("filter.list", "a,b,c,d,e");
		props.setProperty ("filter.a.className", "at.uni_salzburg.cs.ckgroup.filter.LinearCurve");
		props.setProperty ("filter.a.order", "2");
		props.setProperty ("filter.a.coordinate.0", "0 0");
		props.setProperty ("filter.a.coordinate.1", "20 10");
		props.setProperty ("filter.b.className", "at.uni_salzburg.cs.ckgroup.filter.LinearCurve");
		props.setProperty ("filter.b.order", "2");
		props.setProperty ("filter.b.coordinate.0", "0 0");
		props.setProperty ("filter.b.coordinate.1", "20 10");
		props.setProperty ("filter.c.className", "at.uni_salzburg.cs.ckgroup.filter.LinearCurve");
		props.setProperty ("filter.c.order", "2");
		props.setProperty ("filter.c.coordinate.0", "0 0");
		props.setProperty ("filter.c.coordinate.1", "20 10");
		props.setProperty ("filter.d.className", "at.uni_salzburg.cs.ckgroup.filter.LinearCurve");
		props.setProperty ("filter.d.order", "2");
		props.setProperty ("filter.d.coordinate.0", "0 0");
		props.setProperty ("filter.d.coordinate.1", "20 10");
		props.setProperty ("filter.e.className", "at.uni_salzburg.cs.ckgroup.filter.LinearCurve");
		props.setProperty ("filter.e.order", "2");
		props.setProperty ("filter.e.coordinate.0", "0 0");
		props.setProperty ("filter.e.coordinate.1", "20 10");
		
		double[][] v = { { 0, 0 }, { 1, 0.03125 }, { 2, 0.0625 }, { 3, 0.09375 } };
		
		try
		{
			FilterChain chain = new FilterChain (props);
			
			for (int k = 0; k < v.length; k++) {
//				System.out.println ("Filter chain: in="+v[k][0]+", out="+v[k][1]+", filter="+chain.apply (v[k][0]));
				assertEquals ("v=(" + v[k][0] + "," + v[k][1] + ")", v[k][1], chain.apply (v[k][0]), 0);
			}
		} catch (ConfigurationException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the <code>FilterChain</code> throws a
	 * <code>ConfigurationException</code> exception containing a specific
	 * message if the configuration lacks the filter.list property.
	 */
	public void testCase03 () {
		
		Properties props = new Properties ();
		try
		{
			FilterChain chain = new FilterChain (props);
			assertNull (chain);
		} catch (ConfigurationException e)
		{
			assertEquals ("Property filter.list is not defined for this filter chain.", e.getMessage());
		}
		
	}
}
