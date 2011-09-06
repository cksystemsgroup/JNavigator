package javiator.JControl;

//written by V. T. Rajan at IBM T. J. Watson Research Center
//(c) Copyright IBM Corp. 2006  All Rights Reserved

public class StateObserver  implements Cloneable
{
	// Creates state observers xa[0] and xa[1] for position and velocity given by
	// Uses a model Dxa[0] = xa[1] + Kxa*(y - xa[0]), Dxa[1] = coeff*u + Kva*(y-xa[0])
	// Chooses Kx, and Kv such as to make the poles on the left side of the complex
	// plane.  The characteristic equation is s^2+Kxa*s+Kva = 0.
	
	// Takes an output y, an approximation of the x
	// No velocity is available
	// It uses the model below to get xa[0] to converge to y, the measured position.
	
	// Makes the poles to the left of the complex plane so as to make sure that xa[0] converges
	// to y, the measured value. 
	// If the value of y is completely unreliable, we choose Kx=0, Kv=0 and we have
	// an open loop control system.
	// If the value of y is reliable, we choose large Kx, Kv such that 
	// That is, we make the real part of the pole large negative and the KX, Kv will be large
	// xa[0] converges to the y faster than x converges to the target.
	// For default I am going to use the complex pole and make it two times more
	// away from the origin, that is, xa[0] converges to y two time faster (in time) than
	// xa[0] converges to the target value.

	public StateObserver(double coeff, double dtime) {
		this.coeff = coeff;
		this.dtime = dtime;
		this.xa = new double[2];
		this.xa[0] = 0.0;
		this.xa[1] = 0.0;
		setDefaultPoles();
	}

	public StateObserver(double coeff, double dtime, double a, double b) {
		this.coeff = coeff;
		this.dtime = dtime;
		this.xa = new double[2];
		this.xa[0] = 0.0;
		this.xa[1] = 0.0;
		setObserverComplexPoles(a, b);
	}

	public void setInitialValues(double x, double v) {
		xa[0] = x;
		xa[1] = v;
	}
	

	public void setDefaultPoles() {
	   // Example poles taken from "Modern Control Theory" by Katshiko Ogata page 855.
	   // Should get the system to stabilize in 4-5 seconds and maximum overshoot be
	   // about 15%.  Nothing sacred about these values of the roots, but it is a good
	   // starting point.
	   // If a, b, c  are made too small, the system stabilizes slowly.  If a, b, c
	   // are made too large, the errors in measured value of x and v will cause
	   // trouble though larger values of a and b makes the system more stable.

		double a = 5.0;
		double b = 5.0;

		setObserverComplexPoles(a, b);
	}

	public void setObserverComplexPoles(double b, double c) {
		// let -b+ci and -b-ci be the roots of the characteristic equation for
		// the observer
		Kxa = 2.0 * b;
		Kva = b * b + c * c;
	}

	public void setObserverRealPoles(double a, double b) {
		// let -a and -b be the roots of the characteristic equation
		// for the observer
		Kxa = a + b;
		Kva = a * b;
	}

	public double[] updateObserver(double y, double u) {
		//Solve the observer equations Dxa = xa[1] + Kxa*(y - xa[0]), Dxa[1] = coeff*u + Kva*(y-xa[0])
		//Use fourth order Runge-Kutta to solve the dynamic equations
		//See Numerical Recipies in C by William H. Press et. al. (1988) page 570.
		double[] k1 = multiplyscalar(dzcalculate(xa,u, y), dtime*0.5);
		double[] k2 = multiplyscalar(dzcalculate(addvector(xa,k1),u, y),dtime*0.5);
		double[] k3 = multiplyscalar(dzcalculate(addvector(xa,k2),u, y),dtime);
		double[] k4 = multiplyscalar(dzcalculate(addvector(xa,k3),u, y),dtime);
		for (int i = 0; i < xa.length; i++)
			xa[i]= xa[i]+k1[i]/3.0+k2[i]*2.0/3.0+k3[i]/3.0+k4[i]/6.0;
		//System.out.println("y =  "+y+"  xa=  "+xa[0]+"   "+xa[1]);
		return xa;
}

	private double[] addvector(double[] a, double[] b) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++)
			result[i] = a[i] + b[i];
		return result;
	}

	private double[] multiplyscalar(double[] a, double s) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++)
			result[i] = a[i] * s;
		return result;
	}

	private double[] dzcalculate(double[] zin, double u, double y) {
		//derivatives Dxa = xa[1] + Kxa*(y - xa[0]), Dxa[1] = coeff*u + Kva*(y-xa[0])

		double[] dz = new double[zin.length];
		dz[0] = zin[1] + Kxa * (y - zin[0]);
		dz[1] = coeff * u + Kva * (y - zin[0]);
		return dz;
	}

	// @see java.lang.Object#clone()
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	private double coeff;
	private double dtime;
	private double Kxa;
	private double Kva;
	private double[] xa;
}


