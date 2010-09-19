/*
 * @(#) PDDControllerTestCase.java
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

import junit.framework.TestCase;

/**
 * This test verifies the implementation of the <code>PDDController</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class PDDControllerTestCase extends TestCase {

	private double[] X = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 9, 8, 7, 6, 5, -4, -3, -2, -1, 0 };
	private double[] dX = { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	private double[] ddX = { 6, 7, 8, 9, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6 };
	
	/**
	 * Verify the P-controller functionality.
	 */
	public void testCase01 () {
		double[] expected =  { 0, 2, 4, 6, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -8, -6, -4, -2, 0 };
		
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "10");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "5");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "0");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "0");
		
		PDDController controller = null;
		try {
			controller = new PDDController (props);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail();
		}
		
		for (int k=0; k < X.length; k++) {
			double o = controller.apply (X[k], dX[k]);
//			System.out.println ("Index [" + k + "]: " + expected[k] + "  " + o);
			assertEquals ("Index [" + k + "]: ", expected[k], o, 1E-9);
		}
	}
	
	/**
	 * Verify the P-controller functionality.
	 */
	public void testCase02 () {
		double[] expected =  { 0, 2, 4, 6, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, -8, -6, -4, -2, 0 };
		
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "10");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "5");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "0");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "0");
		
		PDDController controller = null;
		try {
			controller = new PDDController (props);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
		
		for (int k=0; k < X.length; k++) {
			double o = controller.apply (X[k], dX[k], ddX[k]);
//			System.out.println ("Index [" + k + "]: " + expected[k] + "  " + o);
			assertEquals ("Index [" + k + "]: ", expected[k], o, 1E-9);
		}
	}
	
	/**
	 * Verify the D-controller functionality.
	 */
	public void testCase03 () {
		double[] expected =  { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10  };
		
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "1");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "10");
		props.setProperty (PDDController.PROP_CONTROLLER_KD_EPSILON, "10");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "0");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "0");
		
		PDDController controller = null;
		try {
			controller = new PDDController (props);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
		
		for (int k=0; k < X.length; k++) {
			double o = controller.apply (X[k], dX[k]);
//			System.out.println ("Index [" + k + "]: " + expected[k] + "  " + o);
			assertEquals ("Index [" + k + "]: ", expected[k], o, 1E-9);
		}
	}
	
	/**
	 * Verify the D-controller functionality.
	 */
	public void testCase04 () {
		double[] expected =  { 10, 10, 10, 10, 10, 10, 8, 6, 4, 2, 0, 2, 4, 6, 8, 10, 10, 10, 10, 10, 10 };
		
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "1");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "10");
		props.setProperty (PDDController.PROP_CONTROLLER_KD_EPSILON, "5");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "0");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "0");
		
		PDDController controller = null;
		try {
			controller = new PDDController (props);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
		
		for (int k=0; k < X.length; k++) {
			double o = controller.apply (X[k], dX[k], ddX[k]);
//			System.out.println ("Index [" + k + "]: " + expected[k] + "  " + o);
			assertEquals ("Index [" + k + "]: ", expected[k], o, 1E-9);
		}
	}
	
	/**
	 * Verify the D-controller functionality.
	 */
	public void testCase041 () {
		double[] expected =  { 0, 10, 10, 10, 7.5, 2.5, -2.5, -7.5, -10, -10, -10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
		
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "1");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "10");
		props.setProperty (PDDController.PROP_CONTROLLER_KD_EPSILON, "2");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "0");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "0");
		
		PDDController controller = null;
		try {
			controller = new PDDController (props);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
		
		for (int k=0; k < X.length; k++) {
			double o = controller.apply (k*0.5*dX[k]);
//			System.out.println ("Index [" + k + "]: " + expected[k] + "  " + o);
			assertEquals ("Index [" + k + "]: ", expected[k], o, 1E-9);
		}
	}
	
	/**
	 * Verify the D²-controller functionality.
	 */
	public void testCase05 () {
		double[] expected =  { 100, -10, -10, -10, -10, -10, -10, -10, -10, -10, -10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10 };
		
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "1");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "10");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "0");
		
		PDDController controller = null;
		try {
			controller = new PDDController (props);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
		
		for (int k=0; k < X.length; k++) {
			double o = controller.apply (X[k], dX[k]);
//			System.out.println ("Index [" + k + "]: " + expected[k] + "  " + o);
			assertEquals ("Index [" + k + "]: ", expected[k], o, 1E-9);
		}
	}
	
	/**
	 * Verify the D²-controller functionality.
	 */
	public void testCase06 () {
		double[] expected =  { 60, 70, 80, 90, 100, 90, 80, 70, 60, 50, 40, 30, 20, 10, 0, 10, 20, 30, 40, 50, 60 };
		
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "1");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "10");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "4");
		
		PDDController controller = null;
		try {
			controller = new PDDController (props);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
		
		for (int k=0; k < X.length; k++) {
			double o = controller.apply (X[k], dX[k], ddX[k]);
//			System.out.println ("Index [" + k + "]: " + expected[k] + "  " + o);
			assertEquals ("Index [" + k + "]: ", expected[k], o, 1E-9);
		}
	}
	
	/**
	 * Verify that an exception is thrown if Kp-Epsilon is zero.
	 */
	public void testCase07 () {
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "10");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "4");
		
		try {
			new PDDController (props);
		} catch (ConfigurationException e) {
			assertEquals ("Please set property KpEpsilon to a non zero value.", e.getMessage());
		}
	}
	
	/**
	 * Verify that an exception is thrown if Kd-Epsilon is zero.
	 */
	public void testCase08 () {
		Properties props = new Properties ();
		props.setProperty (PDDController.PROP_CONTROLLER_KP, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KP_EPSILON, "1");
		props.setProperty (PDDController.PROP_CONTROLLER_KD, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD_EPSILON, "0");
		props.setProperty (PDDController.PROP_CONTROLLER_KD2, "10");
		props.setProperty (PDDController.PROP_REPORT_COUNTER, "4");
		
		try {
			new PDDController (props);
		} catch (ConfigurationException e) {
			assertEquals("Please set property KdEpsilon to a non zero value.", e.getMessage());
		}
	}
}
