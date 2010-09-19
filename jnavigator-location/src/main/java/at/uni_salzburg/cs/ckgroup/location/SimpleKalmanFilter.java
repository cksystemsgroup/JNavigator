/*
 * @(#) SimpleKalmanFilter.java
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
package at.uni_salzburg.cs.ckgroup.location;


public class SimpleKalmanFilter {
	
//	private static final int MAX_STATE = 2;
//
//	private static final int MAX_P = 4;
//	private double Te, Q, R;
//	private double[] x, p;
//	
//	public SimpleKalmanFilter (Properties properties)
//	{
//		x = new double[MAX_STATE];
//		p = new double[MAX_P];
//		Te = 0.02;
//		Q = 0;
//		R = 10;
//	}
//	
//	
//	public double apply(int z, int F)
//	{
//		double x1, x2, p11, p12, p21, p22, k1, k2;
//		
//		//update local variables
//		x1 = x[0];
//		x2 = x[1];
//		p11 = p[0];
//		p12 = p[1];
//		p21 = p[2];
//		p22 = p[3];
//		
//		if (z>0 || F>0){ 
//			//TIME UPDATE
//			//Project the state ahead
//			x1 = (x1 + Te*x2);
//			x2 = (x2 + Te*F);
//		    
//			//Project the error covarience ahead
//			p11 = p11 + (Te*(p21 + p12)) + (Te*Te*p22) + Q;
//			p12 = p12 + (Te*p22) + Q;
//			p21 = p21 + (Te*p22) + Q;
//			p22 = p22 + Q;
//		    
//			//MEASURE UPDATE
//			//Compute the Kalman gain
//			k1 = (p11)/(p11 + R);
//			k2 = (p21)/(p11 + R);
//
//			//Update estimates with measurement zk
//			x2 = x2 + (k2*(z/1000.0-x1));
//			x1 = x1 + (k1*(z/1000.0-x1));
//
//			//Update the error covariance
//			p22 = -(k2*p12) + p22;
//			p21 = -(k2*p11) + p21;
//			p12 = ((1 - k1)*p12);
//			p11 = ((1 - k1)*p11);
//				
//			//save local variables
//			x[0] = x1;
//			x[1] = x2;
//			p[0] = p11;
//			p[1] = p12;
//			p[2] = p21;
//			p[3] = p22;	
//		}
//		else{
//			//save local variables
//			x[0] = 0;
//			x[1] = 0;
//			p[0] = 0;
//			p[1] = 0;
//			p[2] = 0;
//			p[3] = 0;	
//		}
//		return (x2*1000);
//	}

}
