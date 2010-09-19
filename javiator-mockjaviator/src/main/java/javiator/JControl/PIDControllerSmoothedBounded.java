package javiator.JControl;

import javiator.util.JProperties;
import javiator.util.JProperty;
import javiator.util.SignalWriter;

public class PIDControllerSmoothedBounded extends PIDController implements Cloneable
{
    // Creates a default controller, assumes Dx=v, Dv = coeff*u, Dintegral = x-target,
    // where D stands for differntiation, with u = -Kp*x-Kd*v-Ki*integral leads to the
    // linear differential equation with constant coefficients:
    //
    //     DDDx + coeff*Kd*DDx + coeff*Kp*Dx + coeff*Ki*x = coeff*Ki*target,
    //
    // whose characterist equation
    //
    //     s^3 + coeff*Kd*s^2 + coeff*Kp*s + coeff*Ki = 0.
    //
    // We need to choose Kp, Ki, Kd so that this characteristic equation has roots such
    // that its real part is < 0 (poles are in the left half of the complex plane).
	public PIDControllerSmoothedBounded(){
		
	}

    public PIDControllerSmoothedBounded( double coeff, double dtime )
    {
        this.coeff = coeff;
        this.dtime = dtime;
        this.integral = 0.0;
        this.target_smoothed=0.0;
        this.maxchange = 1000000.0;
        setDefaultPoles( );
    }

    public PIDControllerSmoothedBounded( double coeff, double dtime, double a, double b, double c )
    {
        this.coeff = coeff;
        this.dtime = dtime;
        this.integral = 0.0;
        this.target_smoothed=0.0;
        this.maxchange  = 1000000.0;
        setComplexPoles( a, b, c );
        //System.out.println("PIDController:  coeff=  "+coeff+"  dtime=  "+dtime+"  Kp=  "+Kp+"  Kd=  "+Kd+"  Ki=  "+Ki);
    }
    
    public void setuBound(double ub) {
    	ubound = Math.abs(ub);
    }
    
    public void setTargetSmoothing(double tsm) {
    	//targetSmoothingTime = tsm;

    }

    public void setMaxChange(double mc){
        maxchange= mc;
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

        Kd = ( a + 2.0 * b ) / coeff;
        Kp = ( 2.0 * a * b + b * b + c * c ) / coeff;
        Ki = a * ( b * b + c * c ) / coeff;
    }

    public void setRealPoles( double a, double b, double c )
    {
        // Let -a, -b, -c be the real roots of the characteristic equation
        // a > 0 and b > 0.  Then the characteristic equation is
        //
        //     s^3 + (a+b+c)*s^2 + (a*b+b*c+c*a)*s + a*b*c = 0.

        Kd = ( a + b + c ) / coeff;
        Kp = ( a * b + b * c + c * a ) / coeff;
        Ki = a * b * c / coeff;
    }

    public void resetIntegral( )
    {
        // reset the controller back to x = 0, v = 0, u = 0

        integral = 0.0;
        target_smoothed=0.0;
    }

    public void resetIntegral( double x, double v, double target, double u )
    {
        // reset the controller to a non-zero value of x, v, u,
        // where x and v refer to the sampled sensor data and u
        // refers to the old value from the previous iteration

        integral = -( u + Kp * (x-target) + Kd * v ) / Ki;
    }
    
    public void resetUs(double usin) {
    	target_smoothed=usin;
    }
    
    public double sigma(double mc, double value){
     //limits change value
        if (value > mc ) return mc;
        if (value < -mc) return -mc;
        return value;
    }

    public double control( double x, double v, double target )
    {
    	//target_smoothed = (4.0*targetSmoothingTime*target_smoothed+dtime*target)/(4.0*targetSmoothingTime+dtime);
        target_smoothed += sigma(maxchange, target-target_smoothed);
        integral += ( x - target_smoothed ) * dtime;
        
        double u = -Kp * (x-target_smoothed) - Kd * v - Ki * integral;
        if (ubound >= 0.0) {
        	if (u > ubound) {
        		integral = integral+(u-ubound)/Ki;
        		u = ubound;
        	//	System.out.println("Uper bound.");
        	} if ( u < -ubound) {
        		integral = integral+(u+ubound)/Ki;
        		u = -ubound;
        	//	System.out.println("Lower bound.");
        	}
        }

        return( u );
    }

    // @see java.lang.Object#clone()
    public Object clone( )
    {
    	return super.clone( );
    }

    private double coeff;
    private double dtime;
    private double integral;
    private double Kp;
    private double Ki;
    private double Kd;
    private double ubound = -1.0;
    //private double targetSmoothingTime;
    private double target_smoothed;
    private double maxchange;


    public PIDControllerSmoothedBounded( double dt, double p, double i, double d )
    {
        dtime    = dt;
        Kp       = p;
        Ki       = i;
        Kd       = d;
        integral = 0.0;
        target_smoothed=0.0;
    }
    
    public void setPID( double p, double i, double d )
    {
        Kp = p;
        Ki = i;
        Kd = d;
    }

   public static void main(String[] args) {
  	 JProperties properties = new JProperties();
  	 double controllerPeriod = properties.getDouble(JProperty.controllerPeriod);
  	 DerivedCoefficients dc = new DerivedCoefficients(properties);
  	 double cz = dc.coefficientZ;
	   PIDControllerSmoothedBounded pidcd = new PIDControllerSmoothedBounded(cz, controllerPeriod);
	   pidcd.setComplexPoles(JProperty.aZ.defaultValue,JProperty.bZ.defaultValue,JProperty.cZ.defaultValue);
	   pidcd.setTargetSmoothing(1);
	   pidcd.setuBound(500.0);
	   StateObserverwithIntegral observer = new StateObserverwithIntegral(cz, controllerPeriod);
	   StateObserverwithIntegral observern = new StateObserverwithIntegral(cz, controllerPeriod);
	   StateObserver observer2 = new StateObserver(cz, controllerPeriod);
	   observer.setComplexPoles(JProperty.aZ.defaultValue,JProperty.bZ.defaultValue,JProperty.cZ.defaultValue);
	   observern.setComplexPoles(JProperty.aZ.defaultValue,JProperty.bZ.defaultValue,JProperty.cZ.defaultValue);
	   //observer.setComplexPoles(2.0,2.0,2.0);
	   observer2.setObserverComplexPoles(JProperty.bZ.defaultValue,JProperty.cZ.defaultValue);
	   //double target=500;
	   double[] xa = {0.0, 0.0};
	   double[] xa2 = {0.0, 0.0};
	   //double[] xn = { 0.0, 0.0};
	   //double u = 0.0;
	   //SignalWriter uWriter = new SignalWriter("u");
	   SignalWriter xWriter = new SignalWriter("x");
	   SignalWriter vWriter = new SignalWriter("v");
	   SignalWriter x1Writer = new SignalWriter("x1");
	   SignalWriter v1Writer = new SignalWriter("v1");
	   SignalWriter x2Writer = new SignalWriter("x2");
	   SignalWriter v2Writer = new SignalWriter("v2");
	   //SignalWriter uWriter = new SignalWriter("u");
	   
	   double T = 200;
	   double v = 0.0;
	   double z = 0.0;
	   for (int i=0; i < 20; i++ ){
		   T=i*10.0;
	//	   z = 0.5*Constants.COEFFICIENT_Z*controllerPeriod*controllerPeriod*(i+1)*(i+1)*T;
	//	   v = Constants.COEFFICIENT_Z*controllerPeriod*(i+1)*T;
		   z += v*0.5*controllerPeriod;
		   v += cz*controllerPeriod*(i+.5)*10.0;
		   z += v*0.5*controllerPeriod;
		  
		   xa = observer.updateObserver(z, T);
		   xa2 = observer2.updateObserver(z, T);
		   //xn = observern.updateObserverOld(z, T);
//		   u = pidcd.control(xa[0], xa[1], target);
		//   System.out.println("xa=  "+xa[0]+"  "+xa[1]+"  u=  "+u);
		   System.out.println(z+"  "+v+"  "+xa[0]+"  "+xa[1]+"  "+xa2[0]+"  "+xa2[1]);
//		   uWriter.writeSignalValue(u);
		   xWriter.writeSignalValue(z);
		   vWriter.writeSignalValue(v);
		   x1Writer.writeSignalValue(xa[0]);
		   v1Writer.writeSignalValue(xa[1]);
		   x2Writer.writeSignalValue(xa2[0]);
		   v2Writer.writeSignalValue(xa2[1]);
	   }
   }
}

