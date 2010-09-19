/*
 * @(#) LinearCurveTestCase.java
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
import at.uni_salzburg.cs.ckgroup.filter.IFilter;
import at.uni_salzburg.cs.ckgroup.filter.InvalidParameterException;
import at.uni_salzburg.cs.ckgroup.filter.LinearCurve;
import junit.framework.TestCase;

/**
 * This test verifies the implementation of the <code>LinearCurve</code> class.
 * 
 * @author Clemens Krainer
 *
 */
public class LinearCurveTestCase extends TestCase
{
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class with several coordinates. This test verifies only the interpolation
	 * between coordinates. This test case uses an array of
	 * <code>Coordinate</code> objects for constructing the filter.
	 * 
	 * @see testCase11
	 */
	public void testCase01 () {

		double[][] v = { { 0, 5 }, { 1, 1 }, { 2, 4 }, { 3, 2 }, { 4, 3 },
				{ -1, 5 }, { 0.5, 3 }, { 1.5, 2.5 }, { 2.5, 3 }, { 3.5, 2.5 },
				{ 4.5, 3 }, { 0.1, 4.6 }, { 0.9, 1.4 } };

		Coordinate[] c = new Coordinate[5];
		c[0] = new Coordinate (0, 5);
		c[1] = new Coordinate (1, 1);
		c[2] = new Coordinate (2, 4);
		c[3] = new Coordinate (3, 2);
		c[4] = new Coordinate (4, 3);

		try
		{
			IFilter g = new LinearCurve (c);

			for (int k = 0; k < v.length; k++)
				assertEquals ("v=(" + v[k][0] + "," + v[k][1] + ")", v[k][1], g
						.apply (v[k][0]), 0);
		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if duplicate x-values are provided in the coordinates. This test
	 * case uses an array of <code>Coordinate</code> objects for constructing
	 * the filter.
	 * 
	 * @see testCase12
	 */
	public void testCase02 () {
		
		Coordinate[] c = new Coordinate[2];
		c[0] = new Coordinate (0, 5);
		c[1] = new Coordinate (0, 1);

		try
		{
			IFilter g = new LinearCurve (c);
			assertNull (g);
		} catch (InvalidParameterException e)
		{
			assertEquals ("The order of provided x values must be increasing. Duplicate x values are not allowed also.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if the x-values are not ordered increasingly. This test case uses
	 * an array of <code>Coordinate</code> objects for constructing the
	 * filter.
	 * 
	 * @see testCase13
	 */
	public void testCase03 () {
		
		Coordinate[] c = new Coordinate[2];
		c[0] = new Coordinate (1, 5);
		c[1] = new Coordinate (0, 1);

		try
		{
			IFilter g = new LinearCurve (c);
			assertNull (g);
		} catch (InvalidParameterException e)
		{
			assertEquals ("The order of provided x values must be increasing. Duplicate x values are not allowed also.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if only one coordinate is supplied. This test case uses an array of
	 * <code>Coordinate</code> objects for constructing the filter.
	 * 
	 * @see testCase14
	 */
	public void testCase04 () {
		
		Coordinate[] c = new Coordinate[1];
		c[0] = new Coordinate (0, 5);

		try
		{
			IFilter g = new LinearCurve (c);
			assertNull (g);
		} catch (InvalidParameterException e)
		{
			assertEquals ("More than two coordinates are required to construct a curve.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class with several coordinates. This test verifies only the interpolation
	 * between coordinates. This test case uses <code>Properties</code> for
	 * constructing the filter.
	 * 
	 * @see testCase01
	 */
	public void testCase11 () {
		double[][] v = { { 0, 5 }, { 1, 1 }, { 2, 4 }, { 3, 2 }, { 4, 3 },
				{ -1, 5 }, { 0.5, 3 }, { 1.5, 2.5 }, { 2.5, 3 }, { 3.5, 2.5 },
				{ 4.5, 3 }, { 0.1, 4.6 }, { 0.9, 1.4 } };
		
		Properties props = new Properties ();
		props.setProperty ("order", "5");
		props.setProperty ("coordinate.0", "0.0 5.0");
		props.setProperty ("coordinate.1", "1.0 1  ");
		props.setProperty ("coordinate.2", "2.0 4.0");
		props.setProperty ("coordinate.3", "3.0 2.0");
		props.setProperty ("coordinate.4", "4   3.0");

		try
		{
			IFilter g = new LinearCurve (props);

			for (int k = 0; k < v.length; k++)
				assertEquals ("v=(" + v[k][0] + "," + v[k][1] + ")", v[k][1], g.apply (v[k][0]), 0);
			
		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		} catch (ConfigurationException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if duplicate x-values are provided in the coordinates. This test
	 * case uses <code>Properties</code> for constructing the filter.
	 * 
	 * @see testCase02
	 */
	public void testCase12 () {
		
		Properties props = new Properties ();
		props.setProperty ("order", "2");
		props.setProperty ("coordinate.0", "0   5.0");
		props.setProperty ("coordinate.1", "0.0 1  ");

		try
		{
			IFilter g = new LinearCurve (props);
			assertNull (g);
		} catch (InvalidParameterException e)
		{
			assertEquals ("The order of provided x values must be increasing. Duplicate x values are not allowed also.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if the x-values are not ordered increasingly. This test case uses
	 * <code>Properties</code> for constructing the filter.
	 * 
	 * @see testCase03
	 */
	public void testCase13 () {
		
		Properties props = new Properties ();
		props.setProperty ("order", "2");
		props.setProperty ("coordinate.0", "1   5.0");
		props.setProperty ("coordinate.1", "0.0 1  ");

		try
		{
			IFilter g = new LinearCurve (props);
			assertNull (g);
		} catch (InvalidParameterException e)
		{
			assertEquals ("The order of provided x values must be increasing. Duplicate x values are not allowed also.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if only one coordinate is supplied. This test case uses
	 * <code>Properties</code> for constructing the filter.
	 * 
	 * @see testCase04
	 */
	public void testCase14 () {

		Properties props = new Properties ();
		props.setProperty ("order", "1");
		props.setProperty ("coordinate.0", "0   5.0");

		try
		{
			IFilter g = new LinearCurve (props);
			assertNull (g);
		} catch (InvalidParameterException e)
		{
			assertEquals ("More than two coordinates are required to construct a curve.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
	
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if the property order is missing. This test case uses
	 * <code>Properties</code> for constructing the filter.
	 */
	public void testCase15 () {
		
		Properties props = new Properties ();

		try
		{
			IFilter g = new LinearCurve (props);
			assertNull (g);
		} catch (ConfigurationException e)
		{
			assertEquals ("Property order is not defined for this linear curve filter.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}		
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if filter properties are missing. This test case uses
	 * <code>Properties</code> for constructing the filter.
	 */
	public void testCase16 () {
		
		Properties props = new Properties ();
		props.setProperty ("order", "2");
		props.setProperty ("coordinate.0", "1   5.0");

		try
		{
			IFilter g = new LinearCurve (props);
			assertNull (g);
		} catch (ConfigurationException e)
		{
			assertEquals ("Property coordinate.1 is not defined for this linear curve filter.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
	
	/**
	 * This test verifies the implementation of the <code>LinearCurve</code>
	 * class if filter property parameters are missing. This test case uses
	 * <code>Properties</code> for constructing the filter.
	 */
	public void testCase17 () {
		
		Properties props = new Properties ();
		props.setProperty ("order", "2");
		props.setProperty ("coordinate.0", "1 1");
		props.setProperty ("coordinate.1", "2");

		try
		{
			IFilter g = new LinearCurve (props);
			assertNull (g);
		} catch (ConfigurationException e)
		{
			assertEquals ("Property coordinate.1 should have two values.", e.getMessage ());
		} catch (Throwable t)
		{
			fail ();
		}
	}
}
