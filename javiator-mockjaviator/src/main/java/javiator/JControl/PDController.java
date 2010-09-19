

package javiator.JControl;


public class PDController extends PIDController implements Cloneable 
{
    // Creates a default controller, assumes Dx=v, Dv = coeff*u,
    // where D stands for differntiation, with u = -Kp*(x-target)-Kd*v leads to the
    // linear differential equation with constant coefficients:
    //
    //     DDx + coeff*Kd*Dx + coeff*Kp*x = Kp,
    //
    // whose characterist equation
    //
    //     s^2 + coeff*Kd*s + coeff*Kp = 0.
    //
    // We need to choose Kp, Kd so that this characteristic equation has roots such
    // that its real part is < 0 (poles are in the left half of the complex plane).

    public PDController( double coeff, double dtime )
    {
        this.coeff = coeff;
      //  this.dtime = dtime;
 //       this.integral = 0.0;
        setDefaultPoles( );
    }

    public PDController( double coeff, double dtime, double b, double c )
    {
        this.coeff = coeff;
    //    this.dtime = dtime;
    //    this.integral = 0.0;
        setComplexPoles(  b, c );
        //System.out.println("PDController:  coeff=  "+coeff+"  dtime=  "+dtime+"  Kp=  "+Kp+"  Kd=  "+Kd);
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

       // double a = 5.0;
        double b = 2.0;
        double c = 2.0;

        setComplexPoles( b, c );
    }

    public void setComplexPoles( double b, double c )
    {
        // Let -b+ci, -b-ci be the roots of the characteristic equation
        //  b > 0.  Then the characteristic equation is
        //
        //     s^2 + (2*b)*s + (b^2+c^2)= 0.

        Kd = ( 2.0 * b ) / coeff;
        Kp = (  b * b + c * c ) / coeff;
       // Ki = a * ( b * b + c * c ) / coeff;
    }

    public void setRealPoles( double a, double b )
    {
        // Let -a, -b, -c be the real roots of the characteristic equation
        // a > 0 and b > 0.  Then the characteristic equation is
        //
        //     s^3 + (a+b+c)*s^2 + (a*b+b*c+c*a)*s + a*b*c = 0.

        Kd = ( a + b ) / coeff;
        Kp = ( a * b  ) / coeff;
       // Ki = a * b * c / coeff;
    }

    public void resetIntegral( )
    {
        // reset the controller back to x = 0, v = 0, u = 0

    //	integral = 0.0;
    	//System.out.println("Should not have called the PDController.resetIntegeral()");
    }

    public void resetIntegral( double x, double v, double u )
    {
        // reset the controller to a non-zero value of x, v, u,
        // where x and v refer to the sampled sensor data and u
        // refers to the old value from the previous iteration
    	//System.out.println("Should not have called the PDController.resetIntegeral(x, v, u)"+x+"  "+v+"  "+u);

      //  integral = -( u + Kp * x + Kd * v ) / Ki;
    
    }

    public double control( double x, double v, double target )
    {
      //  integral += ( x - target ) * dtime;

        double u = -Kp *( x - target) - Kd * v; // - Ki * integral;
        //System.out.println("PDController.control= "+u);

        return( u );
    }

    // @see java.lang.Object#clone()
    public Object clone( )
    {
 //      try
 //       {
            return super.clone( );
  //      }
  //      catch( CloneNotSupportedException e )
  //      {
  //          throw new IllegalStateException( e );
  //      }
    }

    private double coeff;
   // private double dtime;
   // private double integral;
    private double Kp;
  //  private double Ki;
    private double Kd;


    public PDController( double dt, double p, double d )
    {
     //   dtime    = dt;
        Kp       = p;
    //    Ki       = i;
        Kd       = d;
    //   integral = 0.0;
    }

    public void setPD( double p,  double d )
    {
        Kp = p;
     //   Ki = i;
        Kd = d;
    }

   
}

