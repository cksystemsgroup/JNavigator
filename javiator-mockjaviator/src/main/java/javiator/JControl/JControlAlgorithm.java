package javiator.JControl;

import java.lang.reflect.Constructor;

import javiator.JControl.filters.Butterworth;
import javiator.JControl.filters.Kalman;
import javiator.util.CommandData;
import javiator.util.ControllerDataValues;
import javiator.util.JProperties;
import javiator.util.JProperty;
import javiator.util.MotorOffsets;
import javiator.util.MotorSignals;
import javiator.util.ReportToGround;
import javiator.util.SensorData;
import javiator.util.ShutdownMessage;

//import com.ibm.realtime.exotasks.ESystem;

/**
 * This is the abstract class that every JControl algorithm class must extend.  
 * 
 * All non-final methods *may* be overridden, although many have default implementations.  
 * Obviously, the abstract methods must be implemented.
 * 
 * Only change this class when it has a bug or there is a good reason to add function to the base of the
 *   hierarchy.  Do NOT change this class to experiment with new algorithms.  If you do change it, observe
 *   the rules for subclasses.  If you change the public interface, make sure the new one works for both the
 *   non-exotask and exotask versions.
 *   
 * Rules for subclasses:
 *   
 * Every subclass must have a constructor that takes a Properties object.
 * 
 * Do not use mutable static fields.  Feel free to add more instance fields.  Instance fields that you might
 *   think of as parameters can be initialized in setProperties, using a Constants field as a default.
 * 
 * All overrideable properties should be managed in the setProperty method.  Never manage properties
 *   by altering static fields, since that will necessarily make the static field mutable and illegal
 *   to use in an exotask. 
 * 
 * If fields of "reference" (non-primitive) type are added, the deepClone method must be overridden and
 *   extended to clone those fields.
 */
public abstract class JControlAlgorithm implements Cloneable
{
  /* Altitude modes */
  public static final int    ALT_MODE_GROUND    = 0x00;
  public static final int    ALT_MODE_FLYING    = 0x01;
  public static final int    ALT_MODE_SHUTDOWN  = 0x02;

  /* State flags for reporting to control terminal */
  public static final int    ADJUSTING_ROLL     = 0x01;
  public static final int    ADJUSTING_PITCH    = 0x02;
  public static final int    ADJUSTING_YAW      = 0x04;
  public static final int    ADJUSTING_Z        = 0x08;

  /** A place to store properties prior to instantiation when used with exotasks */
	private static JProperties staticProperties;
	
	/** The most recent sensorData */
	protected SensorData sensorData = new SensorData();
	
	/** The CommandData from the current cycle */
	protected CommandData newNavigation = new CommandData();
	
	/** Last computed motor signals sent to the javiator */
  protected MotorSignals motorSignals = new MotorSignals();
  
  /** Last computed "motor offsets"
   */
  protected CommandData motorOffsets = new CommandData();

  /** Describes what is currently being done by the control (for display to the groundstation) */
  protected int controlState;
	
  /** gyro data scaling factors */
  protected  double angleFactor;
  protected  double angleRateFactor;
  protected  double accelFactor;
  
	/** Yaw adjuster */
  protected AngleWithWindingNumber continuousYawFunctionConverter;
    
	/** A fixed multiplier for z */
	protected double z_factor;
	
	/** A fixed multiplier for roll */
	protected double roll_factor;
    
	/** A fixed multiplier for pitch */
  protected double pitch_factor;
    
	/** A fixed multiplier for yaw */
  protected double yaw_factor;
    
  /** Distance of the sonar from the center of the bottom of the JAviator */
  private int sonarDistanceFromCenterInMM;
	
	/** The CommandData from the previous cycle */
	private CommandData oldNavigation = new CommandData();

  /** The current altitude mode */
	private int altitudeMode = ALT_MODE_GROUND;
	
	/** The adjustment value for altitude */
	private short offsetZ;
    
	/** The total of all height measurements before the first liftoff */
    
	private long totalOffsetZ;
    
	/** The total number of height measurements before the first liftoff */
	private long totalOffsetSamples;
  
	/** Flag: has offsetZ been computed (at the first liftoff) yet? */
	private boolean offsetComputed;
  
	/** The shutdown angle "constant" (overridable) */
	private int shutdownAngle;
  
	/** The Z tolerance "constant" */
	protected int toleranceZ;
  
	/** A switch creating a mode for testing the motors */
	private boolean testMode;
	
	/** Butterworth filter for altitude */
	private Butterworth zButter;
	
	/** Kalman filter for altitude velocity */
	private Kalman dzKalman;
	
	/** Butterworth filter for roll */
	private Butterworth rollButter;
	
	/** Butterworth filter for pitch */
	private Butterworth pitchButter;
	
	/** Butterworth filter for yaw */
	private Butterworth yawButter;
	
	/** Butterworth filter for roll */
	private Butterworth drollButter;
	
	/** Butterworth filter for pitch */
	private Butterworth dpitchButter;
	
	/** Butterworth filter for yaw */
	private Butterworth dyawButter;
	
	/** True iff any filter is non-null (for faster test) */
	private boolean filtersActive;
	
	/** Motor test parameters and ground revving parameters */
  private int motorTestMin;
  private int motorTestMax;
  private int motorTestStep;
  protected int motorRevvingUp;
  private int motorRevvingDown;
//  private double trimRoll, trimPitch;
		
//  public void setTrimValues(double roll, double pitch)
//  {
//	  trimRoll = roll;
//	  trimPitch = pitch;
//  }
  
  /**
	 * Create a new JControlAlgorithm with a set of properties.  Every overriding subclass must have a
	 *   constructor of this signature that invokes this one.
	 * @param properties a Properties table in which properties may be looked up
	 */
	protected JControlAlgorithm( JProperties properties )
	{
		setProperties( properties );
	}
	
	/**
   * Compute new actuator data (and information to be sent to the terminal) from the former state,
   *   sensor data, and input from the terminal.  Also update the state itself.  This method is
   *   called when NOT in the ALT_MODE_GROUND or ALT_MODE_SHUTDOWN states
   */
  public abstract void computeActuatorData( );
	
  /**
   * Compute the next altitude mode
   * @param shutdown the value of the shutdown "message" generated by the control terminal explicitly or implicitly
   * @return the new mode (also store it in the state)
   */
	public int computeNewAltitudeMode( ShutdownMessage shutdown )
	{
	  if( shutdown.flag ||
	    Math.abs( sensorData.roll  ) > shutdownAngle ||
	    Math.abs( sensorData.pitch ) > shutdownAngle )
	  {
	  	return altitudeMode = ALT_MODE_SHUTDOWN;
	  }
	
	  switch( altitudeMode )
	  {
	    case ALT_MODE_GROUND:
	        if( newNavigation.z > 0 )
	        {
	            return altitudeMode = ALT_MODE_FLYING;
	        }
	        break;
	
	    case ALT_MODE_FLYING:
            // TODO: needs to be fixed
	        return altitudeMode = ALT_MODE_FLYING;
	
	    case ALT_MODE_SHUTDOWN:
	        if( sensorData.z <= 0 && newNavigation.z <= 0 )
	        {                    
	            return altitudeMode = ALT_MODE_GROUND;
	        }
	        break;
	  }
	
	  return( altitudeMode );
	}
  
  /**
	 * Provide deep cloning support for testing exotask functionality
	 * @return a deep clone of this object
	 */
	public JControlAlgorithm deepClone()
	{
		try {
			JControlAlgorithm clone = (JControlAlgorithm) clone();
			clone.sensorData = (SensorData) sensorData.clone();
			clone.oldNavigation = (CommandData) oldNavigation.clone();
			clone.newNavigation = (CommandData) newNavigation.clone();
			clone.motorSignals = (MotorSignals) motorSignals.clone();
			clone.motorOffsets = (CommandData) motorOffsets.clone();
			if (continuousYawFunctionConverter != null) {
				clone.continuousYawFunctionConverter = (AngleWithWindingNumber) continuousYawFunctionConverter.clone();
			}
			if (zButter != null) {
				clone.zButter = (Butterworth) zButter.clone();
			}
			if (dzKalman != null) {
				clone.dzKalman = (Kalman) dzKalman.clone();
			}
			if (rollButter != null) {
				clone.rollButter = (Butterworth) rollButter.clone();
			}
			if (pitchButter != null) {
				clone.pitchButter = (Butterworth) pitchButter.clone();
			}
			if (yawButter != null) {
				clone.yawButter = (Butterworth) yawButter.clone();
			}
			if (drollButter != null) {
				clone.drollButter = (Butterworth) drollButter.clone();
			}
			if (dpitchButter != null) {
				clone.dpitchButter = (Butterworth) dpitchButter.clone();
			}
			if (dyawButter != null) {
				clone.dyawButter = (Butterworth) dyawButter.clone();
			}
			return clone;
		} catch (CloneNotSupportedException e) {
			/* Should not occur */
			throw new RuntimeException(e);
		}
	}
  
  /**
   * @return the value of the altitudeMode state variable
   */
  public int getAltitudeMode()
	{
		return altitudeMode;
	}

	public abstract ControllerDataValues getControllerDataValues();
  
  /**
   * @return the value of the controlState state variable
   */
  public int getControlState()
	{
		return controlState;
	}

  /**
	 * @return the value of the motorOffsets state variable
	 */
	public CommandData getMotorOffsets()
	{
		return motorOffsets;
	}
	
  /**
   * @return the value of the motorSignals state variable
   */
  public MotorSignals getMotorSignals()
	{
		return motorSignals;
	}
  
  /**
	 * @return the value of the newNavigation state variable
	 */
	public CommandData getNewNavigation()
	{
		return newNavigation;
	}

	/**
   * @return the value of the oldNavigation state variable
   */
  public CommandData getOldNavigation()
	{
		return oldNavigation;
	}
  
  /**
	 * Get the information to be sent to the terminal
	 * @return a ReportToGround structure containing the desired information
	 */
	public ReportToGround getReportToGround()
	{
		return new ReportToGround (
				(SensorData) sensorData.clone(),
				(MotorSignals) motorSignals.clone(),
				(CommandData) motorOffsets.clone(),
				(byte) altitudeMode,
				(byte) controlState
			);
	}

	
	/**
   * @return the value of the sensorData state variable
   */
  public SensorData getSensorData()
	{
		return sensorData;
	}

	/**
   * Perform the actions appropriate when the helicopter is on the ground (ALT_MODE_GROUND)
   * @param state the information structure containing all of the state, including inputs and outputs
   */
  public void performGroundActions( )
  {
    if( testMode )
    {
        testMotors( motorSignals, motorOffsets );
    }
    else
    {
        doRevDown( motorSignals, motorOffsets );
    }
    resetController( );
    controlState = 0;
  }
  
  /**
   * Perform normalizing adjustments to the SensorData in the state
   */
  public void performSensorAdjustments( )
  {
      /* 
       * Adjust altitude measurement
       */
  	if( altitudeMode == ALT_MODE_GROUND )
    {
        if (! offsetComputed) {
            totalOffsetZ += sensorData.z;
            totalOffsetSamples++;
            offsetZ = (short) (totalOffsetZ / totalOffsetSamples); // average of measured height while on ground
        }
    }
    else {
        if (! offsetComputed) {
            offsetComputed = true; // no longer in ground mode; stop changing measured height of ground
//            ESystem.out.println("Computed offset for height of ground = " + offsetZ);
//            ESystem.out.println("This is " + totalOffsetZ + "/" + totalOffsetSamples);
        }
    }
    sensorData.z -= offsetZ;
    
    /*
     * Adjust yaw to eliminate discontinuities at 0/360 degrees
     */
    if (continuousYawFunctionConverter == null) {
        continuousYawFunctionConverter = new AngleWithWindingNumber( (int)sensorData.yaw );
        sensorData.yaw = (short) continuousYawFunctionConverter.getContinuousAngle( );
    }
    else {
        sensorData.yaw = (short) continuousYawFunctionConverter.updateAndGetContinuousAngle( (int)sensorData.yaw );
    }
    
//    sensorData.pitch += trimPitch;
//    sensorData.roll  += trimRoll;
  }
  
  public void performSensorScaling() {
		/* convert the values */
		sensorData.roll *= angleFactor;
		sensorData.pitch *= angleFactor;
		//sensorData.yaw *= angleFactor;

		sensorData.droll *= angleRateFactor;
		sensorData.dpitch *= angleRateFactor;
		//sensorData.dyaw *= angleRateFactor;

		sensorData.ddx *= accelFactor;
		sensorData.ddy *= accelFactor;
		sensorData.ddz *= accelFactor;
	}

	public void reverseSensorScaling(SensorData sensorData) {
		/* convert the values */
		sensorData.roll /= angleFactor;
		sensorData.pitch /= angleFactor;
		sensorData.yaw /= angleFactor;

		sensorData.droll /= angleRateFactor;
		sensorData.dpitch /= angleRateFactor;
		sensorData.dyaw /= angleRateFactor;

		sensorData.ddx /= accelFactor;
		sensorData.ddy /= accelFactor;
		sensorData.ddz /= accelFactor;
	}
	
  /**
   * Perform the emergency shutdown action
   */
  public void performShutdown( )
  {
    motorSignals.reset( );
    motorOffsets.reset( );
    sensorData.reset( );
    resetController( );
    resetAdjustments( );
    resetFilters( );
    controlState = 0;
    oldNavigation.reset();
    newNavigation.reset();
    offsetComputed = false;
    continuousYawFunctionConverter = null;
  }

	public void performSonarCompensation( )
  {
      int correction = (int) (sonarDistanceFromCenterInMM * Math.sin(sensorData.pitch / 1000.0));
      sensorData.z += correction;
  }

	/**
	 * @param altitudeMode the new value of the altitudeMode state variable
	 */
	public void setAltitudeMode(int altitudeMode)
	{
		this.altitudeMode = altitudeMode;
	}

	/**
	 * @param motorSignals new value for motorSignals state variable
	 */
	public void setMotorSignals(MotorSignals motorSignals)
	{
		this.motorSignals = motorSignals;
	}

	/**
	 * @param newNavigation new value for newNavigation state variable
	 */
	public void setNewNavigation(CommandData newNavigation)
	{
		this.newNavigation = newNavigation;
	}

	/**
	 * @param oldNavigation the new value of the oldNavigation state variable
	 */
	public void setOldNavigation(CommandData oldNavigation)
	{
		this.oldNavigation = oldNavigation;
	}

	/**
	 * @param sensorData new value for sensorData state variable
	 */
	public void setSensorData(SensorData sensorData)
	{
		this.sensorData = sensorData; // FIXME: was "applyFilters(sensorData);", but that seems bogus
	}

	/**
   * Reset whatever needs resetting upon disconnection of the terminal. 
   */
  public void terminalDisconnected()
	{
		testMode = false;
		newNavigation.reset();
		oldNavigation.reset();
	}

	/**
	 * Apply any filters that are active against the SensorData
	 * @param sensorData the raw SensorData
	 * @return the filtered SensorData (may return argument if no filters are active)
	 */
	protected SensorData applyFilters(SensorData sensorData)
	{
		if (filtersActive) {
			SensorData ans = sensorData.deepClone();
			if (zButter != null) {
				ans.z = (short) zButter.apply(ans.z);
			}
			if (dzKalman != null) {
				ans.dz = (short) dzKalman.apply(ans.z, motorSignals);
			}
			if (rollButter != null) {
				ans.roll = (short) rollButter.apply(ans.roll);
			}
			if (drollButter != null) {
				ans.droll = (short) drollButter.apply(ans.droll);
			}
			if (pitchButter != null) {
				ans.pitch = (short) pitchButter.apply(ans.pitch);
			}
			if (dpitchButter != null) {
				ans.dpitch = (short) dpitchButter.apply(ans.dpitch);
			}
			if (yawButter != null) {
				ans.yaw = (short) yawButter.apply(ans.yaw);
			}
			if (dyawButter != null) {
				ans.dyaw = (short) dyawButter.apply(ans.dyaw);
			}

			return ans;
		}
		else {
			return sensorData;
		}
	}

	/**
	 * Reset any filters that are active
	 */
	protected void resetFilters()
	{
		if (filtersActive) {
			if (zButter != null) {
				zButter.reset();
			}
			if (dzKalman != null) {
				dzKalman.reset();
			}
			if (rollButter != null) {
				rollButter.reset();
			}
			if (drollButter != null) {
				drollButter.reset();
			}
			if (pitchButter != null) {
				pitchButter.reset();
			}
			if (dpitchButter != null) {
				dpitchButter.reset();
			}
			if (yawButter != null) {
				yawButter.reset();
			}
			if (dyawButter != null) {
				dyawButter.reset();
			}
		}
	}

	/**
   * Subroutine to handle case of normal motor rev-down when on the ground
   * @param motorSignals the MotorSignals to be sent to the JAviator
   * @param motorOffsets information to be sent to the terminal
   */
  protected void doRevDown( MotorSignals motorSignals, CommandData motorOffsets )
  {
      boolean revvingDown = false;

      if( motorSignals.front > motorRevvingDown )
      {
          motorSignals.front -= motorRevvingDown;
          revvingDown = true;
      }

      if( motorSignals.right > motorRevvingDown )
      {
          motorSignals.right -= motorRevvingDown;
          revvingDown = true;
      }

      if( motorSignals.rear > motorRevvingDown )
      {
          motorSignals.rear -= motorRevvingDown;
          revvingDown = true;
      }

      if( motorSignals.left > motorRevvingDown )
      {
          motorSignals.left -= motorRevvingDown;
          revvingDown = true;
      }
      
      if( revvingDown )
      {
          motorOffsets.roll  = 0;
          motorOffsets.pitch = 0;
          motorOffsets.yaw   = 0;
          motorOffsets.z     = (short) -motorRevvingDown;
      }
      else
      {
          motorSignals.reset( );
          motorOffsets.reset( );
      }
  }

	/**
   * Modify some properties.  This is called in response to the arrival of new properties from the
   *   ControlTerminal.  The intended behavior is incremental:  that is, properties should not be modified
   *   unless they are specified; others do NOT revert to defaults and reinitialization is only done to the
   *   extent implied by the change.   Reinitialization must be done safely, meaning that some new properties
   *   might be ignored if their safety implications are too dicey.
   * @param newProperties a JProperties containing only the new values of properties being changed
   */
  protected void modifyProperties( JProperties newProperties)
  {
		shutdownAngle = newProperties.getInt(JProperty.shutdownAngle, shutdownAngle);
		toleranceZ = newProperties.getInt(JProperty.toleranceZ, toleranceZ);
		testMode = newProperties.getBoolean(JProperty.testMode, testMode);
		z_factor = newProperties.getDouble(JProperty.z_factor, z_factor);
		roll_factor = newProperties.getDouble(JProperty.roll_factor, roll_factor);
		yaw_factor = newProperties.getDouble(JProperty.yaw_factor, yaw_factor);
		pitch_factor = newProperties.getDouble(JProperty.pitch_factor, pitch_factor);
		sonarDistanceFromCenterInMM = newProperties.getInt(JProperty.sonarDistanceFromCenterInMM, 
			sonarDistanceFromCenterInMM);
		motorTestMin = newProperties.getInt(JProperty.motorTestMin, motorTestMin);
		motorTestMax = newProperties.getInt(JProperty.motorTestMax, motorTestMax);
		motorTestStep = newProperties.getInt(JProperty.motorTestStep, motorTestStep);
		motorRevvingUp = newProperties.getInt(JProperty.motorRevvingUp, motorRevvingUp);
		motorRevvingDown = newProperties.getInt(JProperty.motorRevvingDown, motorRevvingDown);

  }

	/**
   * Subroutine to reset the controller to a known state (typically performed when reaching the ground)
   */
  abstract protected void resetController();
  
  /**
   * Initially set properties.  This is called once at some point during initialization.
   * @param properties the properties to set
   */
  protected void setProperties( JProperties properties )
  {
		/* Since there is a prohibition against reflective field setting in exotasks, the following code
		 *   can't be made into a table-driven algorithm and must be extended if new property-settable 
		 *   fields are added.
		 */
		shutdownAngle = properties.getInt(JProperty.shutdownAngle);
		toleranceZ = properties.getInt(JProperty.toleranceZ);
		testMode = properties.getBoolean(JProperty.testMode);
		z_factor = properties.getDouble(JProperty.z_factor);
		roll_factor = properties.getDouble(JProperty.roll_factor);
		yaw_factor = properties.getDouble(JProperty.yaw_factor);
		pitch_factor = properties.getDouble(JProperty.pitch_factor);
		sonarDistanceFromCenterInMM = properties.getInt(JProperty.sonarDistanceFromCenterInMM);
		
		motorTestMin = properties.getInt(JProperty.motorTestMin);
		motorTestMax = properties.getInt(JProperty.motorTestMax);
		motorTestStep = properties.getInt(JProperty.motorTestStep);
		motorRevvingUp = properties.getInt(JProperty.motorRevvingUp);
		motorRevvingDown = properties.getInt(JProperty.motorRevvingDown);
		
		angleFactor = properties.getDouble(JProperty.angleFactor);
		accelFactor = properties.getDouble(JProperty.accelFactor);
		angleRateFactor = properties.getDouble(JProperty.angularRateFactor);
		
		if (properties.getBoolean(JProperty.kalmanFilterDZ)) {
			dzKalman = new Kalman(properties);
		}
		
		if (properties.getBoolean(JProperty.filterZ)) {
			zButter = new Butterworth(properties, JProperty.zFilterFr);
		}
		if (properties.getBoolean(JProperty.filterRoll)) {
			rollButter = new Butterworth(properties, JProperty.rollFilterFr);
		}
		if (properties.getBoolean(JProperty.filterPitch)) {
			pitchButter = new Butterworth(properties, JProperty.pitchFilterFr);
		}
		if (properties.getBoolean(JProperty.filterYaw)) {
			yawButter = new Butterworth(properties, JProperty.yawFilterFr);
		}
		if (properties.getBoolean(JProperty.filterDroll)) {
			drollButter = new Butterworth(properties, JProperty.drollFilterFr);
		}
		if (properties.getBoolean(JProperty.filterDpitch)) {
			dpitchButter = new Butterworth(properties, JProperty.dpitchFilterFr);
		}
		if (properties.getBoolean(JProperty.filterDyaw)) {
			dyawButter = new Butterworth(properties, JProperty.dyawFilterFr);
		}
		
		filtersActive = zButter != null || dzKalman != null || rollButter != null || drollButter != null
			|| pitchButter != null || dpitchButter != null || yawButter != null || dyawButter != null;
	}

	/**
   * Perform motor test while on the ground
   * @param motorSignals the MotorSignals to be sent to the JAviator
   * @param motorOffsets the information to be sent to the terminal
   */
  protected void testMotors( MotorSignals motorSignals, CommandData motorOffsets )
  {
      if( motorSignals.front > 0 )
      {
          if( motorSignals.front > motorTestMax )
          {
              motorOffsets.roll = (short) -motorTestStep;
          }
          else if( motorSignals.front < motorTestMin )
          {
              motorSignals.front = 0;   
              motorSignals.right = (short) motorTestMin;
              motorOffsets.roll  = 0;
              motorOffsets.pitch = (short) motorTestStep;
          }

          motorSignals.front += motorOffsets.roll;
      }
      else if( motorSignals.right > 0 )
      {
          if( motorSignals.right > motorTestMax )
          {
              motorOffsets.pitch = (short) -motorTestStep;
          }
          else if( motorSignals.right < motorTestMin )
          {
              motorSignals.right = 0;   
              motorSignals.rear  = (short) motorTestMin;
              motorOffsets.pitch = 0;
              motorOffsets.z     = (short) motorTestStep;
          }

          motorSignals.right += motorOffsets.pitch;
      }
      else if( motorSignals.rear > 0 )
      {
          if( motorSignals.rear > motorTestMax )
          {
              motorOffsets.z = (short) -motorTestStep;
          }
          else if( motorSignals.rear < motorTestMin )
          {
              motorSignals.rear = 0;   
              motorSignals.left = (short) motorTestMin;
              motorOffsets.z    = 0;
              motorOffsets.yaw  = (short) motorTestStep;
          }

          motorSignals.rear += motorOffsets.z;
      }
      else if( motorSignals.left > 0 )
      {
          if( motorSignals.left > motorTestMax )
          {
              motorOffsets.yaw = (short) -motorTestStep;
          }
          else if( motorSignals.left < motorTestMin )
          {
              motorSignals.left  = 0;   
              motorSignals.front = (short) motorTestMin;
              motorOffsets.yaw   = 0;
              motorOffsets.roll  = (short) motorTestStep;
          }

          motorSignals.left += motorOffsets.yaw;
      }
      else
      {
          motorSignals.front = (short) motorTestMin;
          motorOffsets.roll  = (short) motorTestStep;
      }
  }

	private void resetAdjustments() {
      offsetComputed = false;
      totalOffsetSamples = 0;
      totalOffsetZ = 0;
      
      continuousYawFunctionConverter = null; // force re-init by recreation of object
  }
	
	/**
	 * Create an algorithms object or return null and issue a diagnostic.
	 * @param properties the properties object, including at least a property naming the class
	 * @return an algorithms object or null if it could not be created.  
	 */
	public static JControlAlgorithm createAlgorithm(JProperties properties)
	{
		String algorithmClass = properties.getString(JProperty.algorithm);
		if (algorithmClass == null) {
			System.err.println("Algorithm class not specified");
			return null;
		}
		try {
			Class cls = Class.forName(algorithmClass);
			Constructor cons = cls.getConstructor(new Class[] {JProperties.class});
			return (JControlAlgorithm) cons.newInstance(new Object[] {properties});
		} catch (Exception e) {
			System.err.println("Algorithm class " + algorithmClass + " could not be instantiated due to error:");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Save properties for use by the valueOf method when starting up an exotask graph
	 * @param staticProperties the Properties to save
	 */
	public static void setStaticProperties(JProperties staticProperties)
	{
		JControlAlgorithm.staticProperties = staticProperties;
	}

	/**
	 * A 'valueOf' method to allow a JControlAlgorithm to be instantiated as the initial value of the ControlState
	 *   communicator
	 * @param dummy a non-null, non-zero length String which is the initial value declared for the
	 *   ControlState communicator in the exotask graph.  The contents are ignored by this method.
	 * @return a new JControlAlgorithm to use
	 */
	public static JControlAlgorithm valueOf(String dummy)
	{
		JControlAlgorithm ans = createAlgorithm( staticProperties );
		if (ans == null) {
			throw new IllegalStateException();
		}
		return ans;
	}

	/**
	 * Set the test mode directly without going through properties mechanism (used in lean version of exotask
	 *  control)
	 * @param testMode true if test mode is to be turned on, false if it is to be turned off
	 */
	public void setTestMode(boolean testMode)
	{
		this.testMode = testMode;
	}
}