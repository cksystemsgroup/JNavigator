/*
 * @(#) Matrix3x3.java
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

/**
 * This class implements a simple 3x3 matrix of the data type double.
 * 
 * @author Clemens Krainer
 */
public class Matrix3x3 {
	/**
	 * PI divided by 180. 
	 */
	public static final double PI180TH = Math.PI / 180;
	
	/**
	 * This variable contains the data of the matrix 
	 */
	public double[][] r = new double[3][3];
	
	/**
	 * This constant is the "one" matrix, i.e. the cardinal diagonal contains
	 * the values <b>1</b> and all other fields are zero.
	 */
	public static final Matrix3x3 One = new Matrix3x3 (new double[][] { {1,0,0}, {0,1,0}, {0,0,1} });
	
	/**
	 * This constant is the "zero" matrix, i.e. all matrix fields are zero.
	 */
	public static final Matrix3x3 Zero = new Matrix3x3 (new double[][] { {0,0,0}, {0,0,0}, {0,0,0} });
	

	/**
	 * The default constructor is private to circumvent it's usage.
	 */
	private Matrix3x3 () {
		// intentionally empty
	}
	
	/**
	 * Construct a 3D rotation matrix from given Euler angles. 
	 * 
	 * @param roll the value for roll, i.e. the rotation angle on the X-axis.
	 * @param pitch the value for pitch, i.e. the rotation angle on the Y-axis.
	 * @param yaw the value for yaw, i.e. the rotation angle on the Z-axis.
	 */
	public Matrix3x3 (double roll, double pitch, double yaw) {
		double cosRoll = Math.cos(roll*PI180TH);
		double sinRoll = Math.sin(roll*PI180TH);
		double cosPitch = Math.cos(pitch*PI180TH);
		double sinPitch = Math.sin(pitch*PI180TH);
		double cosYaw = Math.cos(yaw*PI180TH);
		double sinYaw = Math.sin(yaw*PI180TH);
		
		r[0][0] = cosYaw*cosPitch; 
		r[0][1] = -cosYaw*sinPitch*sinRoll - sinYaw*cosRoll; 
		r[0][2] = cosYaw*sinPitch*cosRoll - sinYaw*sinRoll;
		
		r[1][0] = sinYaw*cosPitch;
		r[1][1] = -sinYaw*cosPitch*sinRoll + cosYaw*cosRoll;
		r[1][2] = sinYaw*sinPitch*cosRoll + cosYaw*sinRoll;
		
		r[2][0] = -sinPitch;
		r[2][1] = -cosPitch*sinRoll;
		r[2][2] = cosPitch*cosRoll;
	}
	
	/**
	 * Construct a 3x3 matrix from a 2D double array.
	 * 
	 * @param f the 2D array of double values. 
	 */
	public Matrix3x3 (double[][] f) {
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				r[i][j] = f[i][j];
	}
	
	/**
	 * Multiply the matrix by a <code>CartesianCoordinate</code>. This
	 * operation does not change the fields of the current matrix.
	 * 
	 * @param c the <code>CartesianCoordinate</code>
	 * @return the result as a <code>CartesianCoordinate</code>
	 */
	public CartesianCoordinate multiply (CartesianCoordinate c) {
		CartesianCoordinate result = new CartesianCoordinate ();
		result.x = r[0][0] * c.x + r[0][1] * c.y + r[0][2] * c.z;
		result.y = r[1][0] * c.x + r[1][1] * c.y + r[1][2] * c.z;
		result.z = r[2][0] * c.x + r[2][1] * c.y + r[2][2] * c.z;
		return result;
	}

	/**
	 * Multiply the matrix by another 3x3 matrix. This operation does not change
	 * the fields of the current matrix.
	 * 
	 * @param c the other 3x3 matrix.
	 * @return the result as a 3x3 matrix.
	 */
	public Matrix3x3 multiply (Matrix3x3 c) {
		Matrix3x3 result = new Matrix3x3 ();
		
		for (int i=0; i < 3; i++) {
			for (int j=0; j < 3; j++) {
				result.r[i][j] = 0;
				for (int k=0; k < 3; k++)
					result.r[i][j] += r[i][k] * c.r[k][j];
			}
		}
		
		return result;
	}

	/**
	 * Add another 3x3 matrix to the matrix. This operation does not change
	 * the fields of the current matrix.
	 * 
	 * @param a the other 3x3 matrix.
	 * @return the result as a 3x3 matrix.
	 */
	public Matrix3x3 add (Matrix3x3 a) {
		Matrix3x3 result = new Matrix3x3 ();
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				result.r[i][j] = r[i][j] + a.r[i][j]; 
		
		return result;
	}

	/**
	 * Subtract another 3x3 matrix from the matrix. This operation does not
	 * change the fields of the current matrix.
	 * 
	 * @param a the other 3x3 matrix.
	 * @return the result as a 3x3 matrix.
	 */
	public Matrix3x3 subtract (Matrix3x3 a) {
		Matrix3x3 result = new Matrix3x3 ();
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				result.r[i][j] = r[i][j] - a.r[i][j]; 
		
		return result;
	}
	
	/**
	 * Calculate the determinate of the current matrix. This operation does not
	 * change the fields of the current matrix.
	 * 
	 * @return the determinate of the matrix.
	 */
	public double det () {
		return r[0][0] * r[1][1] * r[2][2] + r[0][1] * r[1][2] * r[2][0] + r[0][2] * r[1][0] * r[2][1]
		       -r[0][0] * r[1][2] * r[2][1] - r[0][1] * r[1][0] * r[2][2] -  r[0][2] * r[1][1] * r[2][0];
	}
	
	/**
	 * Transpose the current matrix and return the result as a new 3x3 matrix.
	 * This operation does not change the fields of the current matrix.
	 * 
	 * @return the transposed matrix.
	 */
	public Matrix3x3 transpose () {
		Matrix3x3 result = new Matrix3x3 ();
		
		for (int i=0; i < 3; i++)
			for (int j=0; j < 3; j++)
				result.r[j][i] = r[i][j]; 
		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		return
			"{ {" + r[0][0] + ", " + r[0][1] + ", " + r[0][2] + "}, " +
			  "{" + r[1][0] + ", " + r[1][1] + ", " + r[1][2] + "}, " +
			  "{" + r[2][0] + ", " + r[2][1] + ", " + r[2][2] + "} }";
	}

}