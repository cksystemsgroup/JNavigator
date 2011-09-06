/*--------------------------------------------------------------------------
 *
 * The code is part of JAviator project (http://javiator.cs.uni-salzburg.at)
 *
 *--------------------------------------------------------------------------
 * Date: 22-Apr-2006
 *--------------------------------------------------------------------------
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
 *--------------------------------------------------------------------------
 *Created by Daniel Iercan (daniel.iercan@aut.upt.ro)
 *--------------------------------------------------------------------------
 *Modified by V. T. Rajan, IBM T.J. Watson Research Center
 * October 13, 2006
 */

package javiator.simulation;

import java.util.Properties;
import java.util.Random;

import javiator.JControl.StateObserver;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;

/**
 * @author Daniel Iercan (daniel.iercan@aut.upt.ro)
 * and V. T. Rajan (vtrajan@us.ibm.com)
 */
public final class JAviatorPlant implements JAviatorPhysicalModel
{
	public static final String PROP_REPORT_RATE = "report.rate";
	public static final String PROP_EFFECTIVE_X_LENGTH = "effective_x_length";
	public static final String PROP_EFFECTIVE_Y_LENGTH = "effective_y_length";
	public static final String PROP_EFFECTIVE_Z_LENGTH = "effective_z_length";
	public static final String PROP_NOISE_FLAG = "noise_flag";
	public static final String PROP_NOISE_FACTOR = "noise_factor";
	public static final String PROP_Z_CORRECTION_FLAG = "z_correction_flag";
	public static final String PROP_Z_OFFSET = "z_offset";
	public static final String PROP_SONAR_POSITION = "sonar_position";
	public static final String PROP_CONTROLLER_PERIOD = "controllerPeriod";
	public static final String PROP_GRAVITY_THRUST = "gravity";
	public static final String PROP_GRAVITATIONAL_ACCELERATION = "gravitationalAcceleration";
	
    // plant constants
    public static double g;
    public static final double m                   = 1.720;  // kg
    public static final double l                   = 0.34;   // m
    public static final double d                   = 0.0240; // meter - converts from Thrust to torque
    public static final double ThrusttoAngMomentum = 0.0;    // account for the gyroscopic effect of rotors
    public static double Ixx;
    public static double Iyy;
    public static double Izz;
    //variables for simulation
    private Tensor3D           Ibodyinv;

    // commands
    private double             tFront;                          // Newton
    private double             tLeft;                           // Newton
    private double             tRear;                           // Newton
    private double             tRight;                          // Newton

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
    private Vector3D		   acceleration;
    private Vector3D	       vb;
    private Vector3D           accelerationb;
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
    
    /** to control verbose output */
    private int                reportRate;
    private int                reportCounter;

    StateObserver zObserver;
    StateObserver yawObserver;
    StateObserver pitchObserver;
    StateObserver rollObserver;
    
	private Random noise;

	/** The effective x length value */
	private double effectiveXLength;
	/** The effective y length value */
	private double effectiveYLength;
	/** The effective z length value */
	private double effectiveZLength;
	/** Noise falg value. */
	private boolean noiseFlag;
	/** Noise factor value. */
	private float noiseFactor;
	/** Z correction falg value. */
	private boolean z_correction_flag;
	/** Z offset. */
	private double z_offset;
	/** Sonar position default value. */
	private double sonar_position;
	/** Controller period */
	private double controllerPeriod;
	/** Thrust to overcome gravity */
	private double gravityThrust;

	/** Coefficients derived from gravity and dimensions */
	public double coefficientZ, coefficientRoll, coefficientPitch,
			coefficientYaw;

	public JAviatorPlant(Properties properties) {
		setProperties(properties);
		reset();
	}
    
    public void reset () {
        // commands
        tFront = tLeft = tRear = tRight = 0.0;

        // signals
        z = dz = pitch = dpitch = roll = droll = yaw = dyaw = 0.0;

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
		acceleration = new Vector3D();
		vb = new Vector3D();
		accelerationb = new Vector3D();
		zObserver = new StateObserver(coefficientZ, controllerPeriod, 0.0, 0.0);
		yawObserver = new StateObserver(coefficientYaw, controllerPeriod, 0.0, 0.0);
		pitchObserver = new StateObserver(coefficientPitch, controllerPeriod, 0.0, 0.0);
		rollObserver = new StateObserver(coefficientRoll, controllerPeriod,	0.0, 0.0);
		
		noise = new Random(1);
    }
    
    /**
     * (Re)set some or all properties.  This is called from the constructor.
     * @param properties the properties to set
     */
    protected void setProperties(Properties properties )
    {
  		/* Since there is a prohibition against reflective field setting in exotasks, the following code
  		 *   can't be made into a table-driven algorithm and must be extended if new property-settable 
  		 *   fields are added.
  		 */
    	reportRate = Integer.parseInt(properties.getProperty(PROP_REPORT_RATE, "0"));
    	effectiveXLength = Double.parseDouble(properties.getProperty(PROP_EFFECTIVE_X_LENGTH, "0.0320"));
    	effectiveYLength = Double.parseDouble(properties.getProperty(PROP_EFFECTIVE_Y_LENGTH, "0.0320"));
    	effectiveZLength = Double.parseDouble(properties.getProperty(PROP_EFFECTIVE_Z_LENGTH, "0.906"));
    	noiseFlag = Boolean.parseBoolean(properties.getProperty(PROP_NOISE_FLAG, "false"));
    	noiseFactor = Float.parseFloat(properties.getProperty(PROP_NOISE_FACTOR, "1000.0"));
    	z_correction_flag = Boolean.parseBoolean(properties.getProperty(PROP_Z_CORRECTION_FLAG, "false"));
    	z_offset = Double.parseDouble(properties.getProperty(PROP_Z_OFFSET, "0.09"));
    	sonar_position = Double.parseDouble(properties.getProperty(PROP_SONAR_POSITION, "0.13"));
    	controllerPeriod = Double.parseDouble(properties.getProperty(PROP_CONTROLLER_PERIOD, "0.020"));
    	gravityThrust = Double.parseDouble(properties.getProperty(PROP_GRAVITY_THRUST, "1360.0"));
    	g = Double.parseDouble(properties.getProperty(PROP_GRAVITATIONAL_ACCELERATION, "9.8"));
    	
		coefficientZ = g*1000/gravityThrust;
	    coefficientRoll = coefficientZ/effectiveXLength;
	    coefficientPitch = coefficientZ/effectiveYLength;
	    coefficientYaw = coefficientZ/effectiveZLength;
    	
		Ixx	= effectiveXLength*m*l;//  = 0.0187 Kg-m^2
		Iyy = effectiveYLength*m*l;//  = 0.0187 Kg-m^2
		Izz = effectiveZLength*m*d;//  = 0.0374 kg-m^2
		
		Te = 1.05 * controllerPeriod;
  	}

    public void simulate( )
    {
    	// Change things to the aircraft coordinates
        // Thrust Force in the body coordinates
    	Vector3D Thrustb = new Vector3D( 0.0, 0.0, (tFront + tLeft + tRear + tRight ));
        // Torque in body coordinates - does not include gyroscopic effect of rotors
        Vector3D Torqueb = new Vector3D( ( tLeft - tRight ) * l, ( tFront - tRear ) * l, ( tFront - tLeft + tRear - tRight ) * d );
        // Angular Momentum of
        Vector3D Lrotorb = new Vector3D( 0.0, 0.0, ThrusttoAngMomentum * ( tFront - tLeft + tRear - tRight ) );
        Vector3D gravity = new Vector3D( 0.0, 0.0, -m * g );

        double t = controllerPeriod;
        zObserver.setInitialValues(z*1000.0, dz*1000.0);
        yawObserver.setInitialValues(yaw*1000.0, dyaw*1000.0);
        pitchObserver.setInitialValues(pitch*1000.0, dpitch*1000.0);
        rollObserver.setInitialValues(roll*1000.0, droll*1000.0);
        
		while (t < Te) {

			R = q.quaternionToRotationTensor3D();
			Iinv = (R.multiply(Ibodyinv.multiply(R.transpose())));
			omega = Iinv.multiply(L);
			Torque = R.multiply(Torqueb);
			Lrotor = R.multiply(Lrotorb);

			deltaL = Torque.add((omega.crossProduct(Lrotor)).multiply(-1.0)).multiply(controllerPeriod);
			Lnew = L.add(deltaL);
			omeganew = Iinv.multiply(Lnew);
			deltaq = q.premultiplybyomega((omega.add(omeganew)).multiply(0.25 * controllerPeriod));
			qnew = q.add(deltaq);
			qnew.normalize();
			Rnew = qnew.quaternionToRotationTensor3D();

			Force = R.multiply(Thrustb);
			Forcenew = Rnew.multiply(Thrustb);
			deltaP = (((Force.add(Forcenew)).multiply(0.5)).add(gravity)).multiply(controllerPeriod);
			acceleration = deltaP.multiply(1.0 / (m * controllerPeriod));
			v = P.multiply(1.0 / m);
			Pnew = P.add(deltaP);
			vnew = Pnew.multiply(1.0 / m);
			deltax = (v.add(vnew)).multiply(0.5 * controllerPeriod);
			xnew = x.add(deltax);

			Pnew.copyInto(P);
			Lnew.copyInto(L);
			xnew.copyInto(x);
			qnew.copy(q);

            //Fixed a bug here on 02/05/2007 by vtrajan
            //The Javiator vertical position and velocity does not go to zero just because the Javiator is 
            //near the ground (i.e. less than epsilon above the ground)
            
			if (x.v[2] < 0.0) {
				// The Javiator is on the ground
				x.v[2] = 0.0;
				for (int i = 0; i < x.v.length; i++) {
					P.v[i] = 0.0;
					L.v[i] = 0.0;
				}
				q = new Quaternion(1.0); // this is strictly not right - the helicoptor may have a finite yaw
			}
			t += controllerPeriod;
        }
		
		R = q.quaternionToRotationTensor3D();
		Iinv = (R.multiply(Ibodyinv.multiply(R.transpose())));
		v = P.multiply(1.0 / m);
		omega = Iinv.multiply(L);
		z = Math.round(x.v[2] / epsilon) * epsilon; // vertical position rounded to epsilon for output
		dz = Math.round(v.v[2] / epsilon) * epsilon; // vertical velocity rounded to epsilon for output
		Vector3D omegabody = (R.transpose()).multiply(omega);
		vb = (R.transpose()).multiply(v);
		accelerationb = (R.transpose()).multiply(acceleration);
        //pitch, roll, and yaw from the rotation matrix.  I do not know if I got the definitions right
        //It is consistent with JAviatorPlant for small values of the angles
        //roll is defined as the angle turn around body x-axis to get the wings (y-axis) horizontal
        //roll = Math.atan2( R.t[2][1], R.t[1][1] );
		roll = q.roll();
        // droll is the component of omega along body x-axis
		droll = omegabody.v[0];
        //	pitch is defined as the angle turn around the body y-axis to get the fuselage (x-axis) horizontal
        //pitch = Math.atan2( R.t[0][2], R.t[0][0] );
		pitch = q.pitch();
        // dpitch is the component of omega along body y-axis
		dpitch = omegabody.v[1];
        //	yaw is defined as the angle turn around body z-axis so that the body x-axis is aligned with  space x-z plane
        //yaw = Math.atan2( R.t[1][0], R.t[0][0] );
		yaw = q.yaw();
        //	dyaw is the component of omega along body z-axis
		dyaw = omegabody.v[2];
        
        if (reportRate > 0) {
        	if (reportCounter == reportRate) {
	        	System.out.println( "roll = " + roll + "  pitch = " + pitch + "  yaw = " + yaw + "  z = " + z + "  dz = " + dz );
	        	System.out.println("x =  "+x.v[0]+"  dx=  "+v.v[0]+"  y=  "+x.v[1]+"  dy=  "+v.v[1]);
	        	System.out.println("T1=  "+tFront+"  T2=  "+tLeft+"  T3=  "+tRear+"  T4=  "+tRight);
	        	if (z > 0) {
		        	double[] zObserved = zObserver.updateObserver(z, (tFront+tLeft+tRear+tRight)
		        		*1000.0*Math.cos(pitch/1000.0)*Math.cos(roll/1000.0)/(coefficientZ*m)-gravityThrust);
		        	System.out.println("real observed z: "+1000.0*z+"  "+1000.0*dz+"  "+zObserved[0]+"  "+zObserved[1]);
		        	double[] yawObserved = yawObserver.updateObserver(yaw, (tFront-tLeft+tRear-tRight)*1000.0/(coefficientZ*m));
		        	System.out.println("real observed yaw: "+1000.0*yaw+"  "+1000.0*dyaw+"  "+yawObserved[0]+"  "+yawObserved[1]);
		        	double[] pitchObserved = pitchObserver.updateObserver(pitch, (tRear-tFront)*1000.0/(coefficientZ*m));
		        	System.out.println("real observed pitch: "+1000.0*pitch+"  "+1000.0*dpitch+"  "+pitchObserved[0]+"  "+pitchObserved[1]);
		        	double[] rollObserved = rollObserver.updateObserver(roll, (tLeft-tRight)*1000.0/(coefficientZ*m));
		        	System.out.println("real observed roll: "+1000.0*roll+"  "+1000.0*droll+"  "+rollObserved[0]+"  "+rollObserved[1]);
	        	}
	        	reportCounter = 0;
        	}
        }
    }

    public SensorData getSensorData( )
    {
    	SensorData s = new SensorData();
    	s.setRoll(roll);
    	s.setPitch(pitch);
    	s.setYaw(yaw);

    	s.setdRoll(droll);
    	s.setdPitch(dpitch);
    	s.setdYaw(dyaw);

    	s.setDdRoll(v.v[0]);	// Hack to get this through the SensorData for the GPS simulator
    	s.setDdPitch(v.v[1]);	// Hack to get this through the SensorData for the GPS simulator
    	s.setDdYaw(0);
    	
        s.setX(x.v[0]);
        s.setY(x.v[1]);
//		s.setDx(v.v[0]);
//		s.setDy(v.v[1]);
        s.setDdx(accelerationb.v[0]);
        s.setDdy(accelerationb.v[1]);

      	if(z_correction_flag)
      		s.setZ((z+sonar_position*Math.sin(pitch)+z_offset)/(Math.cos(pitch)*Math.cos(roll)) - z_offset);
      	else
      		s.setZ(z);
      	
    	s.setDz(dz);    	
        s.setDdz(accelerationb.v[2]+g);
    	s.setBattery((short)13780);
        
    	return s;
    }
    

//    public static double thrust2N(short thrust, double coefficientZ){
//    	return percent_to_thrust(thrust, coefficientZ)/1000.0;
//    }
    
    private static int percent_to_thrust( double percent, double coefficientZ)
    {
        int thrust = (int)(percent * coefficientZ * m );
        return thrust > 0 ? thrust : 0 ;
    }

    public void setMotorSignals ( MotorSignals actuator )
    {
    	double cz = coefficientZ;
		tFront = percent_to_thrust(actuator.getFront(), cz);
		tLeft = percent_to_thrust(actuator.getLeft(), cz);
		tRear = percent_to_thrust(actuator.getRear(), cz);
		tRight = percent_to_thrust(actuator.getRight(), cz);
    	
    	if (noiseFlag) {
    		tFront += Math.round (noise.nextFloat() * noiseFactor);
    		tLeft +=  Math.round (noise.nextFloat() * noiseFactor);
    		tRear +=  Math.round (noise.nextFloat() * noiseFactor);
    		tRight += Math.round (noise.nextFloat() * noiseFactor);
		}
    	
		if (reportRate > 0) {
			reportCounter++;
			if (reportCounter == reportRate)
				System.out.println("actuator.front=  " + actuator.getFront()
						+ "  left=  " + actuator.getLeft() + "  rear=  "
						+ actuator.getRear() + "  right=  "
						+ actuator.getRight());
		}
	}

    /* (non-Javadoc)
     * @see javiator.simulation.JAviatorPhysicalModel#initialize(java.lang.Object)
     */
    public void initialize( Object parameters )
    {
//        Te = ( (Double) parameters ).doubleValue( );
    }

    /* (non-Javadoc)
     * @see javiator.simulation.JAviatorPhysicalModel#setCommandData(at.uni_salzburg.cs.ckgroup.communication.data.CommandData)
     */
    public void setCommandData ( CommandData navigation )
    {
        // As this is a real simulation and not a fake one, we ignore this data
    }

}
