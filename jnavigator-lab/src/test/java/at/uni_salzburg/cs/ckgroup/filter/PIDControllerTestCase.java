/*
 * @(#) PIDControllerTestCase.java
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

import junit.framework.TestCase;

public class PIDControllerTestCase extends TestCase {

	/**
	 * This test verifies that the <code>PIDController</code> constructor
	 * throws an exception if all three gain values are zero.
	 */
	public void testCase01 () {
		Properties props = new Properties ();
		try {
			PIDController controller = new PIDController (props);
			assertNull (controller);
		} catch (ConfigurationException e) {
			assertEquals ("At least one of the properties 'integrator.gain', 'proportional.gain', 'deriative.gain', 'second.deritative.gain' should have a non zero value.", e.getMessage());
		} 
	}
	
	/**
	 * This test verifies the porportional part of the
	 * <code>PIDController</code> for positive gains.
	 */
	public void testCase02 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "100");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-100");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "10");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "0");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3 };
		double[] y = { 0, 10, -10, 20, -20, 30, -30 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the porportional part of the
	 * <code>PIDController</code> for negative gains.
	 */
	public void testCase03 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "100");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-100");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "-10");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "0");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3 };
		double[] y = { 0, -10, 10, -20, 20, -30, 30 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the integral part of the
	 * <code>PIDController</code> for positive gains.
	 */
	public void testCase04 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "100");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-100");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "10");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 0, 10, 0, 20, 0, 30, 0, 10, 20, 30, 40, 50 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the integral part of the
	 * <code>PIDController</code> for negative gains.
	 */
	public void testCase05 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "100");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-100");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "-10");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 0, -10, 0, -20, 0, -30, 0, -10, -20, -30, -40, -50 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the integral part of the
	 * <code>PIDController</code> for positive gains and a non-zero initial state.
	 */
	public void testCase06 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "100");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-100");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "2");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "10");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 20, 30, 20, 40, 20, 50, 20, 30, 40, 50, 60, 70 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the integral part of the
	 * <code>PIDController</code> for the maximum limit.
	 */
	public void testCase07 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "10");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-10");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "8");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "10");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 80, 90, 80, 100, 80, 100, 70, 80, 90, 100, 100, 100 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the integral part of the
	 * <code>PIDController</code> for the minimum limit.
	 */
	public void testCase08 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "10");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-10");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "-18");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "-10");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 100, 90, 100, 80, 100, 70, 100, 90, 80, 70, 60, 50 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the first deriative part of the
	 * <code>PIDController</code> for positive gains.
	 */
	public void testCase09 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "10");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-10");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "0");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "2");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 0, 2, -4, 6, -8, 10, -12, 8, 0, 0, 0, 0 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the first deriative part of the
	 * <code>PIDController</code> for negative gains.
	 */
	public void testCase10 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "10");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-10");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "0");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "-2");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 0, -2, 4, -6, 8, -10, 12, -8, 0, 0, 0, 0 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++)
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			
		} catch (ConfigurationException e) {
			fail ();
		} 
	}
	
	/**
	 * This test verifies the second deriative part of the
	 * <code>PIDController</code> for positive gains.
	 */
	public void testCase11 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "10");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-10");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "0");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		props.setProperty (PIDController.PROP_SECOND_DERIATIVE_GAIN, "1");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 0, -1, 2, -3, 4, -5, 6, -4, 0, 0, 0, 0 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++) {
				System.out.println ("x["+k+"]="+x[k]+", y["+k+"]="+y[k]+", d2=" + controller.apply(x[k]));
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			}
			
		} catch (ConfigurationException e) {
			e.printStackTrace ();
			fail ();
		} 
	}
	
	/**
	 * This test verifies the second deriative part of the
	 * <code>PIDController</code> for positive gains.
	 */
	public void testCase12 () {
		Properties props = new Properties ();
		props.setProperty (PIDController.PROP_INTEGRATOR_MAXIMUM, "10");
		props.setProperty (PIDController.PROP_INTEGRATOR_MINIMUM, "-10");
		props.setProperty (PIDController.PROP_INTEGRATOR_INITIAL_STATE, "0");
		props.setProperty (PIDController.PROP_PROPORTIONAL_GAIN, "0");
		props.setProperty (PIDController.PROP_INTEGRATOR_GAIN, "0");
		props.setProperty (PIDController.PROP_FIRST_DERIATIVE_GAIN, "0");
		props.setProperty (PIDController.PROP_SECOND_DERIATIVE_GAIN, "-1");
		
		double[] x = { 0, 1, -1, 2, -2, 3, -3, 1, 1, 1, 1, 1 };
		double[] y = { 0, 1, -2, 3, -4, 5, -6, 4, 0, 0, 0, 0 };
		
		try {
			PIDController controller = new PIDController (props);
			assertNotNull (controller);
			for (int k=0; k < x.length; k++) {
				System.out.println ("x["+k+"]="+x[k]+", y["+k+"]="+y[k]+", d2=" + controller.apply(x[k]));
				assertEquals ("x["+k+"]="+x[k]+", y["+k+"]="+y[k], y[k], controller.apply(x[k]), 0);
			}
			
		} catch (ConfigurationException e) {
			e.printStackTrace ();
			fail ();
		} 
	}
}
