/*
 * @(#) InfiniteImpulseResponseTestCase.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * Test the IIR filter implementation.
 * 
 * @author Clemens Krainer
 *
 */
public class InfiniteImpulseResponseTestCase extends TestCase
{
	/**
	 * This test case verifies the IIR implementation by applying an impulse
	 * and tracking the pulse response of a first order filter.
	 */
	public void testCase01 () {
		// H = 1/(1 + 0.5*z^(-1));
		double[] a = { 0.5 };
		double[] b = { 1, 0 };
		double[] x = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		double[] y = { 1.0, -0.5, 0.25, -0.125, 0.0625, -0.03125, 0.015625, -0.0078125, 0.00390625, -0.001953125 };

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (a, b);

			for (int k = 0; k < x.length; k++)
			{
				double v = f.apply (x[k]);
				assertEquals (y[k], v, 0);
			}

		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	/**
	 * This test case verifies the IIR implementation by applying an impulse
	 * and tracking the pulse response of a second order filter.
	 */
	public void testCase02 () {
		// H = (0.25 + 0.5*z^(-1) + 0.25*z^(-2))/(1 + 0.25*z^(-1) + 0.25*z^(-2));
		double[] a = { 0.25, 0.25 };
		double[] b = { 0.25, 0.5, 0.25 };
		double[] x = { 1.0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		double[] y = { 0.25, 0.4375, 0.078125, -0.12890625, 0.0126953125,
				0.029052734375, -0.01043701171875, -0.0046539306640625,
				0.003772735595703125, 2.2029876708984375E-4, -9.982585906982422E-4 };

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (a, b);

			for (int k = 0; k < x.length; k++)
			{
				double v = f.apply (x[k]);
//				System.out.println (k + ": " + x[k] + ", " + v +", "+y[k]);
				assertEquals (y[k], v, 0);
			}

		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	/**
	 * Check if a InvalidParameterException is thrown if the number of
	 * coefficients does not fit the needs.
	 */
	public void testCase03 () {
		double[] a = { 0.5, 0 };
		double[] b = { 1, 0 };
		
		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (a, b);
			assertNull (f);
		} catch (InvalidParameterException e)
		{
			assertEquals ("Length of feedback coefficient array plus one must be equal to the length of the forward coefficients", e.getMessage ());
		}
	}
	
	/**
	 * This test case verifies the IIR implementation by applying an impulse and
	 * tracking the pulse response of a first order filter. This test verifies
	 * the same data as testCase01() does but it uses the
	 * <code>Properties</code> constructor.
	 */
	public void testCase04 () {
		String propsFileName = "at/uni_salzburg/cs/ckgroup/filter/InfiniteImpulseResponseFilterTest/iir1.properties";
		URL propsUrl = Thread.currentThread ().getContextClassLoader ().getResource (propsFileName);
		
		File inputDataFile = new File (propsUrl.getFile ());
		Properties properties = new Properties ();
		
		try {
			properties.load (new FileInputStream(inputDataFile));
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}

		double[] x = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		double[] y = { 1.0, -0.5, 0.25, -0.125, 0.0625, -0.03125, 0.015625, -0.0078125, 0.00390625, -0.001953125 };

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);

			for (int k = 0; k < x.length; k++)
			{
				double v = f.apply (x[k]);
				assertEquals (y[k], v, 0);
			}

		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test case verifies the IIR implementation by applying an impulse and
	 * tracking the pulse response of a second order filter. This test verifies
	 * the same data as testCase02() does but it uses the
	 * <code>Properties</code> constructor.
	 */
	public void testCase05 () {
		String propsFileName = "at/uni_salzburg/cs/ckgroup/filter/InfiniteImpulseResponseFilterTest/iir2.properties";
		URL propsUrl = Thread.currentThread ().getContextClassLoader ().getResource (propsFileName);
		
		File inputDataFile = new File (propsUrl.getFile ());
		Properties properties = new Properties ();
		
		try {
			properties.load (new FileInputStream(inputDataFile));
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}

		double[] x = { 1.0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		double[] y = { 0.25, 0.4375, 0.078125, -0.12890625, 0.0126953125,
				0.029052734375, -0.01043701171875, -0.0046539306640625,
				0.003772735595703125, 2.2029876708984375E-4, -9.982585906982422E-4 };

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);

			for (int k = 0; k < x.length; k++)
			{
				double v = f.apply (x[k]);
				assertEquals (v, y[k], 0);
			}

		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the "order" property is set.
	 */
	public void testCase06 () {
		Properties properties = new Properties ();

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);
			assertNull (f);
		}	
		catch (ConfigurationException e)
		{
			assertEquals ("Property order is not defined for this IIR filter.", e.getMessage ());
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the "order" property is set.
	 */
	public void testCase07 () {
		Properties properties = new Properties ();
		properties.setProperty ("order", "");

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);
			assertNull (f);
		}	
		catch (ConfigurationException e)
		{
			assertEquals ("Property order is not defined for this IIR filter.", e.getMessage ());
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the "feedback" property is set.
	 */
	public void testCase08 () {
		Properties properties = new Properties ();
		properties.setProperty ("order", "1");

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);
			assertNull (f);
		}	
		catch (ConfigurationException e)
		{
			assertEquals ("Property feedback.0 is not defined for this IIR filter.", e.getMessage ());
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the "forward" property is set.
	 */
	public void testCase09 () {
		Properties properties = new Properties ();
		properties.setProperty ("order", "1");
		properties.setProperty ("feedback.0", "1");

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);
			assertNull (f);
		}	
		catch (ConfigurationException e)
		{
			assertEquals ("Property forward.0 is not defined for this IIR filter.", e.getMessage ());
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * This test verifies that the "forward" property is set.
	 */
	public void testCase10 () {
		Properties properties = new Properties ();
		properties.setProperty ("order", "1");
		properties.setProperty ("feedback.0", "0.3");
		properties.setProperty ("forward.0", "0.2");

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);
			assertNull (f);
		}	
		catch (ConfigurationException e)
		{
			assertEquals ("Property forward.1 is not defined for this IIR filter.", e.getMessage ());
		}
		catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}
	}

	public void testCase11 () {
		Properties properties = new Properties ();
		properties.setProperty ("order", "2");
		properties.setProperty ("feedback.0","-1.955578");
		properties.setProperty ("feedback.1","0.9565437");
		properties.setProperty ("forward.0","2.4135913E-4");
		properties.setProperty ("forward.1","4.8271826E-4");
		properties.setProperty ("forward.2","2.4135913E-4");

//		properties.setProperty ("feedback.0","0");
//		properties.setProperty ("feedback.1","0");
//		properties.setProperty ("forward.0","0.25");
//		properties.setProperty ("forward.1","0.5");
//		properties.setProperty ("forward.2","0.25");
		
		double[] x = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		double[] y = { 	2.4135913E-4, 0.00119607399472714, 0.00307358195522153,
				0.005831968728438131, 0.009430290806320747, 0.013828572808658613,
				0.018987804016056962, 0.024869934121998942, 0.03143786824203646,
				0.038655461217212056, 0.046487511247883084, 0.054899752893194174,
				0.06385884947052518 };

		try
		{
			IFilter f = new InfiniteImpulseResponseFilter (properties);

			for (int k = 0; k < x.length; k++)
			{
				double v = f.apply (x[k]);
				assertEquals (y[k], v, 0);
//				System.out.println (k + ": x=" + x[k] + ", v=" + v + ", y=" + y[k]);
			}

		} catch (InvalidParameterException e)
		{
			e.printStackTrace ();
			fail ();
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	
}
