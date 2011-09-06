/** --------------------------------------------------------------------------
 *
 * The code is part of JAviator project (http://javiator.cs.uni-salzburg.at)
 *
 * --------------------------------------------------------------------------
 * Date: 22-Apr-2006
 * --------------------------------------------------------------------------
 *
 * Copyright (c) 2006 The University of Salzburg.
 * All rights reserved. Permission is hereby granted, without written 
 * agreement and without license or royalty fees, to use, copy, modify, and 
 * distribute this software and its documentation for any purpose, provided 
 * that the above copyright notice and the following two paragraphs appear 
 * in all copies of this software.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF SALZBURG BE LIABLE TO ANY PARTY
 * FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES
 * ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
 * THE UNIVERSITY OF SALZBURG HAS BEEN ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.

 * THE UNIVERSITY OF SALZBURG SPECIFICALLY DISCLAIMS ANY WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
 * PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 * SALZBURG HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 * ENHANCEMENTS, OR MODIFICATIONS.
 *
 * --------------------------------------------------------------------------
 * Created by Daniel Iercan (daniel.iercan@aut.upt.ro)
 * --------------------------------------------------------------------------
 * Modified by V. T. Rajan, IBM T.J. Watson Research Center
 * October 13, 2006
 * --------------------------------------------------------------------------
 */

package javiator.simulation;

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;

/**
 * @author Daniel Iercan (daniel.iercan@aut.upt.ro)
 * and V. T. Rajan (vtrajan@us.ibm.com)
 * 
 */
public final class ThrustJAviatorPlant implements JAviatorPhysicalModel
{
	public static final String PROP_CONTROLLER_PERIOD = "controller.period";
	public static final String PROP_SILENT = "silent";
	
    // plant constants
    public static final double g                   = 0.0;//9.8;    // meters per second^2
    public static final double m                   = 1.720;  // kg
    public static final double l                   = 0.34;   // m
    public static final double Ixx                 = 0.0187; // kg-m^2
    public static final double Iyy                 = 0.0187; // kg-m^2
    public static final double Izz                 = 0.0374; // kg-m^2
    public static final double d                   = 0.0240; // meter - converts from Thrust to torque
    public static final double ThrusttoAngMomentum = 0.0;    // account for the gyroscopic effect of rotors
    //variables for simulation
    private Tensor3D           Ibodyinv;

    // commands
    private double             T1;                           // Newton
    private double             T2;                           // Newton
    private double             T3;                           // Newton
    private double             T4;                           // Newton

    // State variables
    private Vector3D           x;
    private Vector3D           deltax;
    private Vector3D           xnew;
    private Quaternion         q;
    private Quaternion         deltaq;
    private Quaternion         qnew;
    private Vector3D           P;
    private Vector3D           deltaP;
    private Vector3D           Pnew;
    private Vector3D           L;
    private Vector3D           deltaL;
    private Vector3D           Lnew;
    // Derived quantities
    private Tensor3D           Iinv;
    private Tensor3D           R;
    private Tensor3D           Rnew;
    private Vector3D           v;
    private Vector3D           vnew;
    private Vector3D           omega;
    private Vector3D           omeganew;
    // computed quantities
    private Vector3D           Force;
    private Vector3D           Forcenew;
    private Vector3D           Torque;
    private Vector3D           Lrotor;

    // signals
    private double             z;
    private double             dz;
    private double             roll;
    private double             yaw;
    private double             pitch;
    private double             droll;
    private double             dyaw;
    private double             dpitch;

    // sample time
    private double             Te;
    private double             epsilon;
    
    // to suppress verbose output
    private boolean            silent;
    
    // controller period
    double controllerPeriod;

    
    public ThrustJAviatorPlant (Properties properties)
    {
    	silent = Boolean.parseBoolean (properties.getProperty (PROP_SILENT, "true"));
        
        String controllerPeriodString = properties.getProperty(PROP_CONTROLLER_PERIOD);
        if (controllerPeriodString == null)
        	throw new IllegalStateException ("Property " + PROP_CONTROLLER_PERIOD + " has not been set.");
        
        controllerPeriod = Double.parseDouble (controllerPeriodString);
        Te = 1.05 * controllerPeriod;
        reset ();
    }
    
    public void reset () {
        // commands
        T1 = 0.0;
        T2 = 0.0;
        T3 = 0.0;
        T4 = 0.0;

        // signals
        z = 0.0;
        roll = 0.0;
        yaw = 0.0;
        pitch = 0.0;
        dz = 0.0;
        droll = 0.0;
        dyaw = 0.0;
        dpitch = 0.0;

        epsilon = 0.001; //Values below this would give (int) 1000*z=0

		Ibodyinv = new Tensor3D(1.0 / Ixx, 1.0 / Iyy, 1.0 / Izz);
		x = new Vector3D();
		deltax = new Vector3D();
		xnew = new Vector3D();
		q = new Quaternion(1.0);
		deltaq = new Quaternion(0.0);
		qnew = new Quaternion(1.0);
		P = new Vector3D();
		deltaP = new Vector3D();
		Pnew = new Vector3D();
		L = new Vector3D();
		deltaL = new Vector3D();
		Lnew = new Vector3D();
		R = q.quaternionToRotationTensor3D();
		Iinv = (R.multiply(Ibodyinv.multiply(R.transpose())));
		v = P.multiply(1.0 / m);
		omega = Iinv.multiply(L);
    }

    public void simulate ()
    {
        // Thrust Force in the body coordinates
        Vector3D Thrustb = new Vector3D( 0.0, 0.0, T1 + T2 + T3 + T4 );
        // Torque in body coordinates - does not include gyroscopic effect of rotors
        Vector3D Torqueb = new Vector3D( ( T2 - T4 ) * l, ( T1 - T3 ) * l, ( T1 - T2 + T3 - T4 ) * d );
        // Angular Momentum of
        Vector3D Lrotorb = new Vector3D( 0.0, 0.0, ThrusttoAngMomentum * ( T1 - T2 + T3 - T4 ) );
        Vector3D gravity = new Vector3D( 0.0, 0.0, -m * g );

        double t = controllerPeriod;

        while( t < Te )
        {
            R = q.quaternionToRotationTensor3D( );
            Iinv = ( R.multiply( Ibodyinv.multiply( R.transpose( ) ) ) );
            omega = Iinv.multiply( L );
            Torque = R.multiply( Torqueb );
            Lrotor = R.multiply( Lrotorb );

            deltaL = Torque.add( ( omega.crossProduct( Lrotor ) ).multiply( -1.0 ) ).multiply( controllerPeriod );
            Lnew = L.add( deltaL );
            omeganew = Iinv.multiply( Lnew );
            deltaq = q.premultiplybyomega( ( omega.add( omeganew ) ).multiply( 0.25 * controllerPeriod ) );
            qnew = q.add( deltaq );
            qnew.normalize( );
            Rnew = qnew.quaternionToRotationTensor3D( );

            Force = R.multiply( Thrustb );
            Forcenew = Rnew.multiply( Thrustb );
            deltaP = ( ( ( Force.add( Forcenew ) ).multiply( 0.5 ) ).add( gravity ) ).multiply( controllerPeriod );
            v = P.multiply( 1.0 / m );
            Pnew = P.add( deltaP );
            vnew = Pnew.multiply( 1.0 / m );
            deltax = ( v.add( vnew ) ).multiply( 0.5 * controllerPeriod );
            xnew = x.add( deltax );

            Pnew.copyInto( P );
            Lnew.copyInto( L );
            xnew.copyInto( x );
            qnew.copy( q );

            if( x.v[2] < epsilon )
            {
                // The Javiator is on the ground
                x.v[2] = 0.0;
                for( int i = 0; i < x.v.length; i++ )
                {
                    P.v[i] = 0.0;
                    L.v[i] = 0.0;
                }
                q = new Quaternion( 1.0 ); // this is strictly not right - the helicoptor may have a finite yaw
            }

            t += controllerPeriod;
        }
        R = q.quaternionToRotationTensor3D( );
        Iinv = ( R.multiply( Ibodyinv.multiply( R.transpose( ) ) ) );
        v = P.multiply( 1.0 / m );
        omega = Iinv.multiply( L );
        z = 0.0;//x.v[2]; // vertical position
        dz = 0.0;//v.v[2]; // vertical velocity
        Vector3D omegabody = ( R.transpose( ) ).multiply( omega );
        //pitch, roll, and yaw from the rotation matrix.  I do not know if I got the definitions right
        //It is consistent with JAviatorPlant for small values of the angles
        //roll is defined as the angle turn around body x-axis to get the wings (y-axis) horizontal
        roll = Math.atan2( R.t[2][1], R.t[1][1] );
        // droll is the component of omega along body x-axis
        droll = omegabody.v[0];
        //	pitch is defined as the angle turn around the body y-axis to get the fuselage (x-axis) horizontal
        pitch = Math.atan2( R.t[0][2], R.t[0][0] );
        // dpitch is the component of omega along body y-axis
        dpitch = omegabody.v[1];
        //	yaw is defined as the angle turn around body z-axis so that the body x-axis is aligned with  space x-z plane
        yaw = Math.atan2( R.t[1][0], R.t[0][0] );
        //	dyaw is the component of omega along body z-axis
        dyaw = omegabody.v[2];

        if (!silent) {
          System.out.println( "roll = " + roll + "  pitch = " + pitch + "  yaw = " + yaw );//+ "  z = " + z + "  dz = " + dz );
        }
    }
    
    public SensorData getSensorData () {
    	SensorData s = new SensorData();
    	s.setRoll(roll);
    	s.setPitch(pitch);
    	s.setYaw(yaw);
    	s.setZ(z);
    	s.setdRoll(droll);
    	s.setdPitch(dpitch);
    	s.setdYaw(dyaw);
    	s.setDz(dz);    	
    	return s;
    }

    private int percent_to_thrust( double percent )
    {
    	// TODO what does 7.0 here ?
        int thrust = (int)( (double) percent * 7.0 );
        return( thrust > 0 ? thrust : 0 );
    }

    public void setMotorSignals (MotorSignals actuator)
    {
        T1 = percent_to_thrust (actuator.getFront());
        T2 = percent_to_thrust (actuator.getLeft ());
        T3 = percent_to_thrust (actuator.getRear ());
        T4 = percent_to_thrust (actuator.getRight());
    }

    // @see javiator.simulation.JAviatorPhysicalModel#initialize(java.lang.Object)
    public void initialize (Object parameters)
    {
//        Te = ( (Double) parameters ).doubleValue( );
    }

    // @see javiator.simulation.JAviatorPhysicalModel#setNavigation(javiator.util.Navigation)
    public void setCommandData (CommandData navigation)
    {
        // As this is a real simulation and not a fake one, we ignore this data
	}    
}
