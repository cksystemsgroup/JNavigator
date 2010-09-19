package javiator.JControl;


//written by V. T. Rajan at IBM T. J. Watson Research Center
//(c) Copyright IBM Corp. 2006  All Rights Reserved






public class StateObserverwithIntegral  implements Cloneable
{
	
	// Creates state observers xa[0] and xa[1] for position and velocity given by
	// Uses a model Dxa[0] = xa[1] - Kd*( xa[0]-y), Dxa[1] = coeff*u - Kp*(xa[0]-y)-Ki*integral
	// Dintegral = (xa[0]-y)
	// Chooses Kp, Kd and Ki such as to make the poles on the left side of the complex
	// plane.  The characteristic equation is  s^3 + coeff*Kd*s^2 + coeff*Kp*s + coeff*Ki = 0.	
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

public StateObserverwithIntegral( double coeff, double dtime )
{
 this.coeff = coeff;
 this.dtime = dtime;
 this.xa = new double[2];
 this.xa[0] = 0.0;
 this.xa[1] = 0.0;
 this.integral = 0.0;
 ir = 0.0;
 xr = 0.0;
 vr = 0.0;
 setDefaultPoles( );
}

public StateObserverwithIntegral( double coeff, double dtime, double a, double b, double c )
{
 this.coeff = coeff;
 this.dtime = dtime;
 this.xa = new double[2];
 this.xa[0] = 0.0;
 this.xa[1] = 0.0;
 this.integral = 0.0;
 ir = 0.0;
 xr = 0.0;
 vr = 0.0;
 setComplexPoles(a,b,c);
}

public void setInitialValues(double x, double v, double integr) {
	xa[0]=x;
	xa[1]=v;
	integral = integr;
	ir = 0.0;
	xr = dtime*v*0.5;
	vr = -Ki*dtime*integr*0.5;
}
public void setDefaultPoles( )
{
    // Example poles taken from "Modern Control Theory" by Katshiko Ogata page 855.
    // Should get the system to stabilize in 4-5 seconds and maximum overshoot be
    // about 15%.  Nothing sacred about these values of the roots, but it is a good
    // starting point.
    // If a, b, c  are made too small, the system stabilizes slowly.  If a, b, c
    // are made too large, the errors in measured value of x and v will cause
    // trouble though larger values of a and b makes the system more stable.

    double a = 5.0;
    double b = 2.0;
    double c = 2.0;

    setComplexPoles( a, b, c );
}
public void setComplexPoles( double a, double b, double c )
{
    // Let -a, -b+ci, -b-ci be the roots of the characteristic equation
    // a > 0 and b > 0.  Then the characteristic equation is
    //
    //     s^3 + (a+2*b)*s^2 + (2*a*b+b^2+c^2)*s + a*(b^2+c^2) = 0.

    Kd = ( a + 2.0 * b ) ;
    Kp = ( 2.0 * a * b + b * b + c * c );
    Ki = a * ( b * b + c * c );
}

public void setRealPoles( double a, double b, double c )
{
    // Let -a, -b, -c be the real roots of the characteristic equation
    // a > 0 and b > 0.  Then the characteristic equation is
    //
    //     s^3 + (a+b+c)*s^2 + (a*b+b*c+c*a)*s + a*b*c = 0.

    Kd = ( a + b + c );
    Kp = ( a * b + b * c + c * a );
    Ki = a * b * c;
}

public double[] updateObserver(double y, double u) {
	//Solve the observer equations Dxa = xa[1] - Kd*(xa[0]-y), Dxa[1] = coeff*u - Kp*(xa[0]-y) - Ki*integral;
	//Dintegral = xa[0]-y;
	//Use simple integral technique
	// error of the order of O(dtime^2)
		int iterations = 3;
		for (int i= 0; i < iterations; i++) {
//			System.out.println("iterations start i=  "+i+"  xa[0]=  "+xa[0]+"  xa[1]=  "+xa[1]);
			xa[1]=vr+coeff*u*dtime-Kp*dtime*(xa[0]-y)*0.5-Ki*dtime*integral*0.5;
			xa[0]=xr+xa[1]*dtime*0.5-Kd*dtime*(xa[0]-y)*0.5;
			integral = ir+dtime*(xa[0]-y)*0.5;			
		}
//		System.out.println("iterations end i=  "+iterations+"  xa[0]=  "+xa[0]+"  xa[1]=  "+xa[1]);
		ir = integral+dtime*(xa[0]-y)*0.5;
		xr = xa[0]+xa[1]*dtime*0.5-Kd*dtime*(xa[0]-y)*0.5;
		vr = xa[1]-Kp*dtime*(xa[0]-y)*0.5-Ki*dtime*integral*0.5;
/*		integral += (xa[0]-y)*dtime;
		xa[1] += (coeff*u-Kp*(xa[0]-y)-Ki*integral)*dtime;
		xa[0] += (xa[1]-Kd*(xa[0]-y))*dtime;
	//	System.out.println("y =  "+y+"  u=  "+u+"  xa=  "+xa[0]+"   "+xa[1]); */
		return xa;
} 

/*public double[] updateObserverOld(double y, double u) {
	//Solve the observer equations Dxa = xa[1] - Kd*(xa[0]-y), Dxa[1] = coeff*u - Kp*(xa[0]-y) - Ki*integral;
	//Dintegral = xa[0]-y;
	//Use simple integral technique
	// error of the order of O(dtime^2)
	integral += (xa[0]-y)*dtime;
	xa[1] += (coeff*u-Kp*(xa[0]-y)-Ki*integral)*dtime;
	xa[0] += (xa[1]-Kd*(xa[0]-y))*dtime;
	//	System.out.println("y =  "+y+"  u=  "+u+"  xa=  "+xa[0]+"   "+xa[1]); 
		return xa;
} */

//@see java.lang.Object#clone()
public Object clone( )
{
 try
 {
     return super.clone( );
 }
 catch( CloneNotSupportedException e )
 {
     throw new RuntimeException( e );
 }
}

private double coeff;
private double dtime;
private double Kp;
private double Kd;
private double Ki;
private double[] xa;
private double integral;
private double ir;
private double xr;
private double vr;
}


