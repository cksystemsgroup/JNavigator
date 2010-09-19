
package javiator.JControl;


public class PIDController implements Cloneable
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
	public PIDController(){
		
	}

    public PIDController( double coeff, double dtime )
    {
        this.coeff = coeff;
        this.dtime = dtime;
        this.integral = 0.0;
        setDefaultPoles( );
    }

    public PIDController( double coeff, double dtime, double a, double b, double c )
    {
        this.coeff = coeff;
        this.dtime = dtime;
        this.integral = 0.0;
        setComplexPoles( a, b, c );
        //System.out.println("PIDController:  coeff=  "+coeff+"  dtime=  "+dtime+"  Kp=  "+Kp+"  Kd=  "+Kd+"  Ki=  "+Ki);
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
    }

    public void resetIntegral( double x, double v, double u )
    {
        // reset the controller to a non-zero value of x, v, u,
        // where x and v refer to the sampled sensor data and u
        // refers to the old value from the previous iteration

        integral = -( u + Kp * x + Kd * v ) / Ki;
    }

    /**
     * @param x Measured value (eg pitch)
     * @param v Speed (eg derivative of pitch)
     * @param target Target value from joystick etc
     * @return Force component for this quantity
     */
    public double control( double x, double v, double target )
    {
        integral += ( x - target ) * dtime;

        double u = -Kp * x - Kd * v - Ki * integral;

        return( u );
    }

    // @see java.lang.Object#clone()
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
    private double integral;
    private double Kp;
    private double Ki;
    private double Kd;


    public PIDController( double dt, double p, double i, double d )
    {
        dtime    = dt;
        Kp       = p;
        Ki       = i;
        Kd       = d;
        integral = 0.0;
    }

    public void setPID( double p, double i, double d )
    {
        Kp = p;
        Ki = i;
        Kd = d;
    }

   
}
