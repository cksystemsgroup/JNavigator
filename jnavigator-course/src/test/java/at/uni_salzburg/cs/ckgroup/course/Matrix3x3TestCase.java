/*
 * @(#) Matrix3x3TestCase.java
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
 * This class verifies the implementation of the <code>Matrix3x3</code> class.
 * 
 * @author Clemens Krainer
 */
public class Matrix3x3TestCase extends TestCase {

	/**
	 * Verify the left multiplication with the 3x3 One matrix.  
	 */
	public void testCase01 () {		
		Matrix3x3 a = Matrix3x3.One;
		Matrix3x3 b = new Matrix3x3 (new double[][] {{1,2,3}, {4,5,6}, {7,8,9}});
		Matrix3x3 c = a.multiply(b);
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				assertEquals (b.r[i][j], c.r[i][j], 1E-9);
	}
	
	/**
	 * Verify the right multiplication with the 3x3 One matrix.  
	 */
	public void testCase02 () {		
		Matrix3x3 a = new Matrix3x3 (new double[][] {{1,2,3}, {4,5,6}, {7,8,9}});
		Matrix3x3 b = Matrix3x3.One;
		Matrix3x3 c = a.multiply(b);
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				assertEquals (a.r[i][j], c.r[i][j], 1E-9);
	}
	
	/**
	 * Verify the 3x3 One matrix constant.
	 */
	public void testCase03 () {
		double[][] one = new double[][] { {1,0,0}, {0,1,0}, {0,0,1} };
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				assertEquals (Matrix3x3.One.r[i][j], one[i][j], 1E-9);
	}

	/**
	 * Verify the 3x3 Zero constant.
	 */
	public void testCase04 () {	
		double[][] zero = new double[][] { {0,0,0}, {0,0,0}, {0,0,0} };
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				assertEquals (Matrix3x3.Zero.r[i][j], zero[i][j], 1E-9);
	}
	
	/**
	 * Verify the <code>toString()</code> method.
	 */
	public void testCase05 () {
		String zero = Matrix3x3.Zero.toString();
		assertEquals ("{ {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0} }", zero);
	}
	
	/**
	 * Verify the <code>toString()</code> method.
	 */	
	public void testCase06 () {
		String one = Matrix3x3.One.toString();
		assertEquals ("{ {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0} }", one);
	}
	
	/**
	 * Verify the <code>toString()</code> method.
	 */
	public void testCase07 () {
		String m = (new Matrix3x3 (new double[][] {{1,2,3}, {4,5,6}, {7,8,9}})).toString();
		assertEquals ("{ {1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}, {7.0, 8.0, 9.0} }", m);
	}
	
	/**
	 * Verify the multiplication of a 3x3 matrix by an <code>CartesianCoordinate</code>
	 */
	public void testCase08 () {
		Matrix3x3 a = new Matrix3x3 (new double[][] {{1,2,3}, {4,5,6}, {7,8,9}});
		CartesianCoordinate b = new CartesianCoordinate (10, 11, 12);
		CartesianCoordinate result = a.multiply(b);
		CartesianCoordinate expected = new CartesianCoordinate (68, 167, 266);

		assertEquals (expected.x, result.x, 1E-9);
		assertEquals (expected.y, result.y, 1E-9);
		assertEquals (expected.z, result.z, 1E-9);
	}

	/**
	 * Verify the multiplication of a 3x3 matrix by an <code>CartesianCoordinate</code>
	 */

	public void testCase09 () {
		Matrix3x3 a = new Matrix3x3 (new double[][] {{1,2,3}, {4,5,6}, {7,8,9}});
		CartesianCoordinate b = new CartesianCoordinate (0, 0, 1);
		CartesianCoordinate result = a.multiply(b);
		CartesianCoordinate expected = new CartesianCoordinate (3, 6, 9);

		assertEquals (expected.x, result.x, 1E-9);
		assertEquals (expected.y, result.y, 1E-9);
		assertEquals (expected.z, result.z, 1E-9);
	}

	/**
	 * Verify the determinant of a 3x3 matrix.
	 */
	public void testCase10 () {
		Matrix3x3 a = new Matrix3x3 (new double[][] {{1,2,3}, {4,5,6}, {7,8,9}});
		assertEquals(0.0, a.det(), 1E-9);
	}

	/**
	 * Verify the determinant of a 3x3 matrix.
	 */
	public void testCase11 () {
		Matrix3x3 a = new Matrix3x3 (new double[][] {{3,0,3}, {4,5,6}, {7,8,9}});
		assertEquals(-18.0, a.det(), 1E-9);
	}
	
	/**
	 * Verify the <code>transpose()</code> method.
	 */
	public void testCase12 () {
		Matrix3x3 a = new Matrix3x3 (new double[][] {{1,2,3}, {4,5,6}, {7,8,9}});
		Matrix3x3 result = a.transpose();
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				assertEquals (result.r[j][i], a.r[i][j], 1E-9);
	}
	
	/**
	 * Verify the rotation 3x3 matrix constructed from Euler angles. 
	 */
	public void testCase13 () {
		CartesianCoordinate c = new CartesianCoordinate (1,0,0);
		Matrix3x3 rot = new Matrix3x3 (90, 0, 0);
		CartesianCoordinate c1 = rot.multiply(c);
		assertEquals ( 1.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 90, 0);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals (-1.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 0, 90);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 1.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);
		
		rot = new Matrix3x3 (-90, 0, 0);
		c1 = rot.multiply(c);
		assertEquals ( 1.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, -90, 0);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 1.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 0, -90);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals (-1.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

	}

	/**
	 * Verify the rotation 3x3 matrix constructed from Euler angles. 
	 */
	public void testCase14 () {
		CartesianCoordinate c = new CartesianCoordinate (0,1,0);
		Matrix3x3 rot = new Matrix3x3 (90, 0, 0);
		CartesianCoordinate c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals (-1.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 90, 0);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 1.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 0, 90);
		c1 = rot.multiply(c);
		assertEquals (-1.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);
		
		rot = new Matrix3x3 (-90, 0, 0);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 1.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, -90, 0);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 1.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 0, -90);
		c1 = rot.multiply(c);
		assertEquals ( 1.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);
	}
	
	/**
	 * Verify the rotation 3x3 matrix constructed from Euler angles. 
	 */
	public void testCase15 () {
		CartesianCoordinate c = new CartesianCoordinate (0,0,1);
		Matrix3x3 rot = new Matrix3x3 (90, 0, 0);
		CartesianCoordinate c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 1.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 90, 0);
		c1 = rot.multiply(c);
		assertEquals ( 1.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 0, 90);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 1.0, c1.z, 1E-9);
		
		rot = new Matrix3x3 (-90, 0, 0);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals (-1.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, -90, 0);
		c1 = rot.multiply(c);
		assertEquals (-1.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 0.0, c1.z, 1E-9);

		rot = new Matrix3x3 (0, 0, -90);
		c1 = rot.multiply(c);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 1.0, c1.z, 1E-9);
	}
	
	/**
	 * Verify the rotation 3x3 matrix constructed from Euler angles. 
	 */
	public void testCase16 () {
		CartesianCoordinate c = new CartesianCoordinate (1,1,1);
		Matrix3x3 rot = new Matrix3x3 (0, 90-35.264389682754654, 45);
		rot = rot.transpose();
		CartesianCoordinate c1 = rot.multiply(c);
		System.out.println ("c="+c+", rot="+rot+", c1="+ c1);
		assertEquals ( 0.0, c1.x, 1E-9);
		assertEquals ( 0.0, c1.y, 1E-9);
		assertEquals ( 1.732050808, c1.z, 1E-9);
	}
	
	/**
	 * Verify the <code>add()</code> method. 
	 */
	public void testCase17 () {
		Matrix3x3 a = new Matrix3x3 (new double[][] { {1,2,3}, {4,5,6}, {7,8,9}});
		Matrix3x3 b = new Matrix3x3 (new double[][] { {11,22,33}, {44,55,66}, {77,88,99}});
		double[][] expected = new double[][] { {12,24,36}, {48,60,72}, {84,96,108} };
		
		Matrix3x3 c = a.add(b);
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				assertEquals (expected[i][j], c.r[i][j], 1E-9);
	}

	/**
	 * Verify the <code>subtract()</code> method. 
	 */
	public void testCase18 () {
		Matrix3x3 a = new Matrix3x3 (new double[][] { {1,2,3}, {4,5,6}, {7,8,9}});
		Matrix3x3 b = new Matrix3x3 (new double[][] { {11,22,33}, {44,55,66}, {77,88,99}});
		double[][] expected = new double[][] { {-10,-20,-30}, {-40,-50,-60}, {-70,-80,-90} };
		
		Matrix3x3 c = a.subtract(b);
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				assertEquals (expected[i][j], c.r[i][j], 1E-9);
	}

}
