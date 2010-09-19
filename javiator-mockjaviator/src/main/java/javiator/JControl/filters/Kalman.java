package javiator.JControl.filters;

import javiator.JControl.DerivedCoefficients;
import javiator.util.JProperties;
import javiator.util.JProperty;
import javiator.util.MotorSignals;

public class Kalman implements Cloneable
{
	private static final int MAX_STATE = 2;

	private static final int MAX_P = 4;
	private double Te, Q, R;
	private double[] x, p;
	private double gravity;
	private double coeffZ;
	public Kalman(JProperties properties)
	{
		Te = properties.getDouble(JProperty.controllerPeriod);
		this.Q = properties.getInt(JProperty.kalmanQ)/1000.0;
		this.R = properties.getInt(JProperty.kalmanR)/1000.0;
		gravity = properties.getDouble(JProperty.gravity);
		coeffZ = new DerivedCoefficients(properties).coefficientZ;
		reset();
	}
	
	
	public double apply(double z, MotorSignals motorSignals) 
	{
		return apply((int)z, motorSignals);
	}
	
	public double apply(int z, MotorSignals motorSignals)
	{
		double x1, x2, p11, p12, p21, p22, k1, k2;
		
		//update local variables
		x1 = x[0];
		x2 = x[1];
		p11 = p[0];
		p12 = p[1];
		p21 = p[2];
		p22 = p[3];
		
		double F = (int) ((motorSignals.front + motorSignals.rear + motorSignals.right + motorSignals.left - gravity)
			 * coeffZ );
		
		if (z>0 || F>0){ 
			//TIME UPDATE
			//Project the state ahead
			x1 = (x1 + Te*x2);
			x2 = (x2 + Te*F);
		    
			//Project the error covarience ahead
			p11 = p11 + (Te*(p21 + p12)) + (Te*Te*p22) + Q;
			p12 = p12 + (Te*p22) + Q;
			p21 = p21 + (Te*p22) + Q;
			p22 = p22 + Q;
		    
			//MEASURE UPDATE
			//Compute the Kalman gain
			k1 = (p11)/(p11 + R);
			k2 = (p21)/(p11 + R);

			//Update estimates with measurement zk
			x2 = x2 + (k2*(z/1000.0-x1));
			x1 = x1 + (k1*(z/1000.0-x1));

			//Update the error covariance
			p22 = -(k2*p12) + p22;
			p21 = -(k2*p11) + p21;
			p12 = ((1 - k1)*p12);
			p11 = ((1 - k1)*p11);
				
			//save local variables
			x[0] = x1;
			x[1] = x2;
			p[0] = p11;
			p[1] = p12;
			p[2] = p21;
			p[3] = p22;	
		}
		else{
			//save local variables
			x[0] = 0;
			x[1] = 0;
			p[0] = 0;
			p[1] = 0;
			p[2] = 0;
			p[3] = 0;	
		}
		return (x2*1000);
	}

	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	public void reset() 
	{
		x = new double[MAX_STATE];
		p = new double[MAX_P];
	}
	
	public void main(String[] args) {
		//Testing the Kalman filter
		
		
	}
}
