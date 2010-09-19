/*
 * @(#) CartesianCoordinateTestCase.java
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
package at.uni_salzburg.cs.ckgroup.course;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the Position class.
 * 
 * @author Clemens Krainer
 */
public class CartesianCoordinateTestCase extends TestCase
{
	/**
	 * Verify if the default constructor sets all variables zero.
	 */
	public void testCase01 () {
		CartesianCoordinate p = new CartesianCoordinate ();
		assertEquals (0.0, p.x, 1E-10);
		assertEquals (0.0, p.y, 1E-10);
		assertEquals (0.0, p.z, 1E-10);
		assertEquals (0.0, p.getX (), 1E-10);
		assertEquals (0.0, p.getY (), 1E-10);
		assertEquals (0.0, p.getZ (), 1E-10);
	}
	
	/**
	 * Verify if the constructor using double values assigns the initialization
	 * values to the attributes correctly.
	 */
	public void testCase02 () {
		CartesianCoordinate u = new CartesianCoordinate (1,2,3);
		assertEquals (1.0, u.x, 1E-10);
		assertEquals (2.0, u.y, 1E-10);
		assertEquals (3.0, u.z, 1E-10);
		assertEquals (1.0, u.getX (), 1E-10);
		assertEquals (2.0, u.getY (), 1E-10);
		assertEquals (3.0, u.getZ (), 1E-10);
	}
	
	/**
	 * Verify if the copy constructor works correctly.
	 */
	public void testCase03 () {
		CartesianCoordinate v = new CartesianCoordinate (1,2,3);
		CartesianCoordinate u = new CartesianCoordinate (v);
		assertEquals (1.0, u.x, 1E-10);
		assertEquals (2.0, u.y, 1E-10);
		assertEquals (3.0, u.z, 1E-10);
		assertEquals (1.0, u.getX (), 1E-10);
		assertEquals (2.0, u.getY (), 1E-10);
		assertEquals (3.0, u.getZ (), 1E-10);
		
	}
	
	/**
	 * Verify the <code>add()</code> method.
	 */
	public void testCase04 () {
		CartesianCoordinate u = new CartesianCoordinate (1,2,3);
		CartesianCoordinate v = new CartesianCoordinate (10,20,30);
		CartesianCoordinate w = v.add (u);
		assertEquals ( 1.0, u.x, 1E-10);
		assertEquals ( 2.0, u.y, 1E-10);
		assertEquals ( 3.0, u.z, 1E-10);
		assertEquals (10.0, v.x, 1E-10);
		assertEquals (20.0, v.y, 1E-10);
		assertEquals (30.0, v.z, 1E-10);
		assertEquals (11.0, w.x, 1E-10);
		assertEquals (22.0, w.y, 1E-10);
		assertEquals (33.0, w.z, 1E-10);
	}
	
	/**
	 * Verify the <code>subtract()</code> method.
	 */
	public void testCase05 () {
		CartesianCoordinate u = new CartesianCoordinate (1,2,3);
		CartesianCoordinate v = new CartesianCoordinate (10,20,30);
		CartesianCoordinate w = v.subtract (u);
		assertEquals ( 1.0, u.x, 1E-10);
		assertEquals ( 2.0, u.y, 1E-10);
		assertEquals ( 3.0, u.z, 1E-10);
		assertEquals (10.0, v.x, 1E-10);
		assertEquals (20.0, v.y, 1E-10);
		assertEquals (30.0, v.z, 1E-10);
		assertEquals ( 9.0, w.x, 1E-10);
		assertEquals (18.0, w.y, 1E-10);
		assertEquals (27.0, w.z, 1E-10);
	}
	
	/**
	 * Verify the <code>norm()</code> method.
	 */
	public void testCase06 () {
		CartesianCoordinate u = new CartesianCoordinate (10,20,30);
		double n = u.norm ();		
		assertEquals (10.0, u.x, 1E-10);
		assertEquals (20.0, u.y, 1E-10);
		assertEquals (30.0, u.z, 1E-10);
		assertEquals (37.416573867739416, n, 1E-6);
	}
	
	/**
	 * Verify the <code>set()</code> method.
	 */
	public void testCase07 () {
		CartesianCoordinate p = new CartesianCoordinate ();
		p.set (new CartesianCoordinate (1,2,3));
		assertEquals (1.0, p.x, 1E-10);
		assertEquals (2.0, p.y, 1E-10);
		assertEquals (3.0, p.z, 1E-10);
		assertEquals (1.0, p.getX (), 1E-10);
		assertEquals (2.0, p.getY (), 1E-10);
		assertEquals (3.0, p.getZ (), 1E-10);
	}
	
	/**
	 * Verify the <code>setX()</code>, <code>setY()</code> and <code>setZ()</code> methods.
	 */
	public void testCase08 () {
		CartesianCoordinate p = new CartesianCoordinate ();
		p.setX (1);
		p.setY (2);
		p.setZ (3);
		assertEquals (1.0, p.x, 1E-10);
		assertEquals (2.0, p.y, 1E-10);
		assertEquals (3.0, p.z, 1E-10);
		assertEquals (1.0, p.getX (), 1E-10);
		assertEquals (2.0, p.getY (), 1E-10);
		assertEquals (3.0, p.getZ (), 1E-10);
	}
	
	public void testCase09 () {
		double a = 10;
		double c = 3;

		CartesianCoordinate x = new CartesianCoordinate (5, 5, (c/a)*Math.sqrt (a*a - 5*5 - 5*5));
//		Position y = new Position (5, 6, 2.1213203435596424);
//		Position y = new Position (5, 6, (c/a)*Math.sqrt (a*a - 5*5 - 6*6));
		CartesianCoordinate ref = new CartesianCoordinate (5, 6, (c/a)*Math.sqrt (a*a - 5*5 - 6*6));
		CartesianCoordinate y = new CartesianCoordinate (5, 6, 1.7);
		
		PolarCoordinate X = r2p (x);
		PolarCoordinate Y = r2p (y);
		assertNotNull (X);
		assertNotNull (Y);

		CartesianCoordinate xy = y.subtract (x);
		
		double nz = (a/c)*Math.sqrt (a*a - x.x*x.x - x.y*x.y);
		CartesianCoordinate n = new CartesianCoordinate (x.x, x.y, nz);
		
		double phi = Math.asin ( Math.abs (n.x*xy.x + n.y*xy.y + n.z*xy.z) / (n.norm ()*xy.norm ()) );
		
		phi /= PI180TH;
		if (ref.z > y.z)
			phi = -phi;	
	}
	
	/**
	 * Verify the <code>toString()</code> method.
	 */
	public void testCase10 () {
		CartesianCoordinate p = new CartesianCoordinate (1,2,3);
		String coordinateString = p.toString();
		assertEquals ("(1.0m, 2.0m, 3.0m)", coordinateString);
	}

	/**
	 * Verify the <code>normalize()</code> method.
	 */
	public void testCase11 () {
		CartesianCoordinate p = new CartesianCoordinate (1,2,3);
		CartesianCoordinate n = p.normalize();
		assertEquals (1, n.norm(), 1E-9);
		assertEquals (1/Math.sqrt(14), n.x, 1E-9);
		assertEquals (2/Math.sqrt(14), n.y, 1E-9);
		assertEquals (3/Math.sqrt(14), n.z, 1E-9);
	}

	/**
	 * Verify the <code>normalize()</code> method if the norm of the coordinate is zero.
	 */
	public void testCase12 () {
		CartesianCoordinate p = new CartesianCoordinate (0,0,0);
		assertEquals (0, p.norm(), 1E-9);
		CartesianCoordinate n = p.normalize();
		assertEquals (1, n.norm(), 1E-9);
		assertEquals (1, n.x, 1E-9);
		assertEquals (0, n.y, 1E-9);
		assertEquals (0, n.z, 1E-9);
	}

	/**
	 * Verify the <code>crossProduct()</code> method.
	 */
	public void testCase13 () {
		CartesianCoordinate a = new CartesianCoordinate (1,0,0);
		CartesianCoordinate b = new CartesianCoordinate (0,1,0);
		
		CartesianCoordinate c = a.crossProduct(b);
		assertEquals (0, c.x, 1E-9);
		assertEquals (0, c.y, 1E-9);
		assertEquals (1, c.z, 1E-9);
		
		c = b.crossProduct(a);
		assertEquals (0, c.x, 1E-9);
		assertEquals (0, c.y, 1E-9);
		assertEquals (-1, c.z, 1E-9);
	}

	/**
	 * Verify the <code>crossProduct()</code> method.
	 */
	public void testCase14 () {
		CartesianCoordinate a = new CartesianCoordinate (0,1,0);
		CartesianCoordinate b = new CartesianCoordinate (0,0,1);
		
		CartesianCoordinate c = a.crossProduct(b);
		assertEquals (1, c.x, 1E-9);
		assertEquals (0, c.y, 1E-9);
		assertEquals (0, c.z, 1E-9);
		
		c = b.crossProduct(a);
		assertEquals (-1, c.x, 1E-9);
		assertEquals (0, c.y, 1E-9);
		assertEquals (0, c.z, 1E-9);
	}
	
	/**
	 * Verify the <code>crossProduct()</code> method.
	 */
	public void testCase15 () {
		CartesianCoordinate a = new CartesianCoordinate (1,0,0);
		CartesianCoordinate b = new CartesianCoordinate (0,0,1);
		
		CartesianCoordinate c = a.crossProduct(b);
		assertEquals (0, c.x, 1E-9);
		assertEquals (-1, c.y, 1E-9);
		assertEquals (0, c.z, 1E-9);
		
		c = b.crossProduct(a);
		assertEquals (0, c.x, 1E-9);
		assertEquals (1, c.y, 1E-9);
		assertEquals (0, c.z, 1E-9);
	}
	
	/**
	 * Verify the <code>crossProduct()</code> method.
	 */
	public void testCase16 () {
		CartesianCoordinate a = new CartesianCoordinate (1,2,3);
		CartesianCoordinate b = new CartesianCoordinate (4,5,6);
		
		CartesianCoordinate c = a.crossProduct(b);
		assertEquals (-3, c.x, 1E-9);
		assertEquals ( 6, c.y, 1E-9);
		assertEquals (-3, c.z, 1E-9);
		
		c = b.crossProduct(a);
		assertEquals ( 3, c.x, 1E-9);
		assertEquals (-6, c.y, 1E-9);
		assertEquals ( 3, c.z, 1E-9);
	}
	
	
	public static double PI180TH = Math.PI / 180;
	
	public static CartesianCoordinate p2r (PolarCoordinate p) {
		
		double x = p.altitude * Math.cos (p.latitude*PI180TH) * Math.cos (p.longitude*PI180TH);
		double y = p.altitude * Math.cos (p.latitude*PI180TH) * Math.sin (p.longitude*PI180TH);
		double z = p.altitude * Math.sin (p.latitude*PI180TH);
		
		return new CartesianCoordinate (x, y, z);
	}
	
	public static PolarCoordinate r2p (CartesianCoordinate p) {
		
		double N = Math.sqrt (p.x*p.x + p.y*p.y + p.z*p.z);
		double altitude = N;
		double latitude = Math.asin (p.z/N);
		double longitude = Math.asin (p.y/(N*Math.cos (latitude)));
			
		return new PolarCoordinate (latitude/PI180TH, longitude/PI180TH, altitude);
	}
	

}
