package javiator.JControl;

import java.util.HashMap;
import java.util.Map;

import javiator.simulation.JAviatorPlant;
import javiator.util.ControllerDataValues;
import javiator.util.JDoubleProperty;
import javiator.util.JProperties;
import javiator.util.JProperty;
import javiator.util.SignalWriter;
import javiator.util.ThrustComponents;

/**
 * This is the basic JControl (the latest version prior to the restructure) with
 *   StateObserver support turned off.
 */
public class BasicJControl extends JControlAlgorithm
{
	/* Parameters settable via the properties mechanism (also have defaults) */

	/**Z signal*/
	private static final String SIGNAL_Z = "z";

  private static final String SIGNAL_DZ = "dz";
  
  private static final String SIGNAL_ROLL = "roll";
	
	private static final String SIGNAL_DROLL = "droll";
	private static final String SIGNAL_PITCH = "pitch";
	private static final String SIGNAL_DPITCH = "dpitch";
	private static final String SIGNAL_YAW = "yaw";
	private static final String SIGNAL_DYAW = "dyaw";
	private static final String SIGNAL_T1 = "T1";
	private static final String SIGNAL_T2 = "T2";
	private static final String SIGNAL_T3 = "T3";
	private static final String SIGNAL_T4 = "T4";
	private static final String SIGNAL_Z_TARGET = "zTarget";
	private static final String SIGNAL_ROLL_TARGET = "rollTarget";
	private static final String SIGNAL_PITCH_TARGET = "pitchTarget";
	private static final String SIGNAL_YAW_TARGET = "yawTarget";
	private static final int SET_NO_ROOTS = 0;
	private static final int SET_REAL_ROOTS = 1;
	private static final int SET_COMPLEX_ROOTS = 2;
    
	/** The controller period */
	protected double controllerPeriod;
  
  /** The gravity */
  protected double gravity;
  /**  The roll controller in use */
	protected PIDController rollController;
  /**  The pitch controller in use */
	protected PIDController pitchController;

  /**  The yaw controller in use */
	protected PIDController yawController;
  
  /* Calculated state */

	/**  The Z controller in use */
	protected PIDController zController;

	/** The most recently calculated roll */
	protected double uRoll;

	/** The most recently calculated pitch */
	protected double uPitch;
	
	/** The most recently calculated yaw */
	protected double uYaw;
	
	/** The most recently calculated altitude */
	protected double uZnew;
	
	/** Indicates that a PD controller should be used in lieu of a PID controller */
  private boolean pdcontroller;

	/** Indicates that signalWriters are to be used to document values for modeling */
  private boolean useSignalWriters;

	private int setcontrolroots;

	/** Previous Z value */
	private double uZold;
	
	/** Mode flag indicating whether the JAviator is airborne */
	private boolean airborne;
	    
	/** Flag to indicate direct thrust ZControl */
	private boolean thrustZControl;
	
	private final Map signalWriters = new HashMap();

	/** Flag indicating that smoothed PID controller should be used */
	private boolean useSmoothedPidController; 
	
	/* Tolerances (z tolerance defined in parent) */
	private int toleranceRoll, tolerancePitch, toleranceYaw;
	
	/* Derived coefficients */
	protected DerivedCoefficients derivedCoefficients;

	/* Should yaw be controlled? */
	private boolean controlYaw;

	/**
	 * Inherited constructor
	 * @param properties the properties
	 */
	public BasicJControl(JProperties properties)
	{
		super(properties);
		useSignalWriters = properties.getBoolean(JProperty.useSignalWriters);
		if (useSignalWriters) {
			initWriters();
		}
	}
	
	/* (non-Javadoc)
	 * @see javiator.JControl.JControlAlgorithm#computeActuatorData()
	 */
	public void computeActuatorData()
	{
		if (!thrustZControl) {
			if (airborne) {
				updateControllers();
			}	else {  // Not Airborne
		  		uZnew = uZold + motorRevvingUp;
		  		if( sensorData.z > 0 || uZnew > gravity) {
		  			airborne = true;
		  			zController.resetIntegral( sensorData.z, sensorData.dz, uZnew-gravity );
		  		}
			}
		} else {
			double tempUZnew = newNavigation.z * 2.0;
			if (tempUZnew > gravity) {
				updateControllers();
			}
			uZnew = tempUZnew;
		}
		if (!controlYaw) {
			uYaw = 0.0;
		}
		motorSignals.front = (short) Math.round( 0.25 * uZnew + 0.25 * uYaw + 0.5 * uPitch );
		motorSignals.right = (short) Math.round( 0.25 * uZnew - 0.25 * uYaw - 0.5 * uRoll );
		motorSignals.rear  = (short) Math.round( 0.25 * uZnew + 0.25 * uYaw - 0.5 * uPitch );
		motorSignals.left  = (short) Math.round( 0.25 * uZnew - 0.25 * uYaw + 0.5 * uRoll );

		motorOffsets.roll  = (short)( uRoll );
		motorOffsets.pitch = (short)( uPitch );
		motorOffsets.yaw   = (short)( uYaw );
		motorOffsets.z     = (short)( uZnew - uZold );
		uZold              = uZnew;

		int state = 0;
		
		if( Math.abs( newNavigation.roll - sensorData.roll ) > toleranceRoll )
		{
			state |= ADJUSTING_ROLL;
		}

		if( Math.abs( newNavigation.pitch - sensorData.pitch ) > tolerancePitch )
		{
			state |= ADJUSTING_PITCH;
		}

		if( Math.abs( newNavigation.yaw - sensorData.yaw ) > toleranceYaw )
		{
			state |= ADJUSTING_YAW;
		}

		if( Math.abs( newNavigation.z - sensorData.z ) > toleranceZ )
		{
			state |= ADJUSTING_Z;
		}
		controlState = state;
		
		writeSignals();

	}
	
	/* (non-Javadoc)
	 * @see javiator.JControl.JControlAlgorithm#deepClone()
	 */
	public JControlAlgorithm deepClone()
	{
		BasicJControl clone = (BasicJControl) super.deepClone();
		clone.rollController = (PIDController) rollController.clone();
		clone.pitchController = (PIDController) pitchController.clone();
		clone.yawController = (PIDController) yawController.clone();
		clone.zController = (PIDController) zController.clone();
		clone.derivedCoefficients = (DerivedCoefficients) derivedCoefficients.clone();
		return clone;
	}
	
	/* (non-Javadoc)
	 * @see javiator.JControl.JControlAlgorithm#getControllerDataValues()
	 */
	public ControllerDataValues getControllerDataValues() {
	    return new ThrustComponents(uRoll, uPitch, uYaw, uZnew);
	}
	
	/**
	 * Reset writers.
	 */
	public void terminalDisconnected(){
		//closeWriters();
	}
	
	protected void addWriter(String name){
		signalWriters.put(name, new SignalWriter(name));
	}
	
	/**
	 * Initialize the writers
	 *
	 */
	protected void initWriters(){
		 //initialize the writers
        addWriter(SIGNAL_Z);
        addWriter(SIGNAL_DZ);
        addWriter(SIGNAL_ROLL);
        addWriter(SIGNAL_DROLL);
        addWriter(SIGNAL_PITCH);
        addWriter(SIGNAL_DPITCH);
        addWriter(SIGNAL_YAW);
        addWriter(SIGNAL_DYAW);
        addWriter(SIGNAL_T1);
        addWriter(SIGNAL_T2);
        addWriter(SIGNAL_T3);
        addWriter(SIGNAL_T4);
        addWriter(SIGNAL_Z_TARGET);
        addWriter(SIGNAL_ROLL_TARGET);
        addWriter(SIGNAL_PITCH_TARGET);
        addWriter(SIGNAL_YAW_TARGET);
	}

	/**
	 * Initialize a PD controller via the setComplexPoles method
	 * @param properties the properties to use for initialization
	 * @param coeff the coeff property
	 * @param b the b property
	 * @param c the c property
	 * @return a new PDController initialized from the properties and defaults via the setComplexPoles method
	 */
	protected PIDController makeAutoPDController(JProperties properties, JDoubleProperty coeff, JDoubleProperty b, 
		JDoubleProperty c)
	{
		double coeffVal = properties.getDouble(coeff);
		double bVal = properties.getDouble(b);
		double cVal = properties.getDouble(c);
		return new PDController(coeffVal, controllerPeriod, bVal, cVal);
	}

	/**
	 * Initialize a PID controller via the setComplexPoles or setRealPoles method
	 * @param properties the properties to use for initialization
	 * @param coeffVal the derived coefficient value for this controller
	 * @param a the a property
	 * @param b the b property
	 * @param c the c property
	 * @param setcontrolroots an indicator of the type of poles to be used
	 * @return a new PIDController initialized from the properties and defaults via the indicated method
	 */
	protected PIDController makeAutoPIDController(JProperties properties, double coeffVal, JDoubleProperty a,
		JDoubleProperty b, JDoubleProperty c)
	{
		double aVal = properties.getDouble(a);
		double bVal = properties.getDouble(b);
		double cVal = properties.getDouble(c);
		PIDController pidControl;
		if (useSmoothedPidController) {
			PIDControllerSmoothedBounded p = new PIDControllerSmoothedBounded(coeffVal, controllerPeriod);
			p.setuBound(properties.getDouble(JProperty.uBound));
			p.setMaxChange(properties.getDouble(JProperty.pidControllerMaxChange));
			pidControl = p;
		} else {
			pidControl = new PIDController(coeffVal, controllerPeriod);
		}
		switch(setcontrolroots){
		case SET_COMPLEX_ROOTS:
			pidControl.setComplexPoles(aVal, bVal, cVal);
			break;
		case SET_REAL_ROOTS:
			pidControl.setRealPoles(aVal, bVal, cVal);
			break;
		case SET_NO_ROOTS:
			throw new IllegalStateException();
		}
		
		return pidControl;
	}

	/**
	 * Initialize a PD controller via the manual method
	 * @param properties the properties to use 
	 * @param kp the kp property
	 * @param kd the name of the kd property
	 * @return the initialized PD controller
	 */
	protected PIDController makeManualPDController(JProperties properties, JDoubleProperty kp, JDoubleProperty kd)
	{
		double kpVal = properties.getDouble(kp);
		double kdVal = properties.getDouble(kd);
		return new PDController(controllerPeriod, kpVal, kdVal);
	}

	/**
	 * Initialize a PID controller via the manual method
	 * @param properties the properties to use 
	 * @param kp the kp property
	 * @param ki the ki property
	 * @param kd the kd property
	 * @return a new PID controller initialized from the properties and default via the manual method
	 */
	protected PIDController makeManualPIDController(JProperties properties, JDoubleProperty kp, JDoubleProperty ki,
		JDoubleProperty kd)
	{
		double kpVal = properties.getDouble(kp);
		double kiVal = properties.getDouble(ki);
		double kdVal = properties.getDouble(kd);
		if (useSmoothedPidController) {
			PIDControllerSmoothedBounded p = new PIDControllerSmoothedBounded(controllerPeriod, kpVal, kiVal, kdVal);
			p.setuBound(properties.getDouble(JProperty.uBound));
			p.setMaxChange(properties.getDouble(JProperty.pidControllerMaxChange));
			return p;
		}
		return new PIDController(controllerPeriod, kpVal, kiVal, kdVal);
	}

	/* (non-Javadoc)
	 * @see javiator.JControl.JControlAlgorithm#modifyProperties(javiator.util.JProperties)
	 */
	protected void modifyProperties(JProperties newProperties)
	{
		super.modifyProperties(newProperties);
		DerivedCoefficients oldCoeff = (DerivedCoefficients) derivedCoefficients.clone();
		derivedCoefficients.modifyProperties(newProperties);
		gravity = newProperties.getDouble(JProperty.gravity, gravity);
		controllerPeriod = newProperties.getDouble(JProperty.controllerPeriod, controllerPeriod);
		toleranceRoll = newProperties.getInt(JProperty.toleranceRoll, toleranceRoll);
		toleranceYaw = newProperties.getInt(JProperty.toleranceYaw, toleranceYaw);
		tolerancePitch = newProperties.getInt(JProperty.tolerancePitch, tolerancePitch);
		boolean newPdcontroller = newProperties.getBoolean(JProperty.usePDController, pdcontroller);
		int newSetcontrolroots = newProperties.getInt(JProperty.useSetComplexPoles, setcontrolroots);
		boolean newSmoothedPidController = newProperties.getBoolean(JProperty.useSmoothedPidController, 
			useSmoothedPidController);
		if (newPdcontroller != pdcontroller) {
			System.err.println("Currently may not change usePDController property");
		}
		if (newSetcontrolroots != setcontrolroots) {
			System.err.println("Currently may not change useSetComplexPoles property");
		}
		if (newSmoothedPidController != useSmoothedPidController) {
			System.err.println("Currently may not change useSmoothedPidController property");
		}
		if (!pdcontroller) {
			switch(setcontrolroots)
			{
				case SET_COMPLEX_ROOTS:
				case SET_REAL_ROOTS:
					/* PID controllers initialized with setComplexPoles() or setRealPoles() */
					zController = modifyAutoPIDController(newProperties, derivedCoefficients.coefficientZ, 
						oldCoeff.coefficientZ, JProperty.aZ, JProperty.bZ, JProperty.cZ, zController, sensorData.z, 
						sensorData.dz, uZold);
					rollController = modifyAutoPIDController(newProperties, derivedCoefficients.coefficientRoll, 
						oldCoeff.coefficientRoll, JProperty.aRoll, JProperty.bRoll, JProperty.cRoll, rollController, 
						sensorData.roll, sensorData.droll, uRoll);
					pitchController = modifyAutoPIDController(newProperties, derivedCoefficients.coefficientPitch,
						oldCoeff.coefficientPitch, JProperty.aPitch, JProperty.bPitch, JProperty.cPitch, pitchController, 
						sensorData.pitch, sensorData.dpitch, uPitch);
					yawController = modifyAutoPIDController(newProperties, derivedCoefficients.coefficientYaw, 
						oldCoeff.coefficientYaw, JProperty.aYaw, JProperty.bYaw, JProperty.cYaw, yawController,
						sensorData.yaw, sensorData.dyaw, uYaw);
					break;
				case SET_NO_ROOTS:
					/* PID controllers initialized directly from p, i, and d values */
					zController = modifyManualPIDController(newProperties, JProperty.kpZ, JProperty.kiZ, 
						JProperty.kdZ, zController, sensorData.z, sensorData.dz, uZold);
					rollController = modifyManualPIDController(newProperties, JProperty.kpRoll, 
						JProperty.kiRoll, JProperty.kdRoll, rollController, sensorData.roll, sensorData.droll, uRoll);
					pitchController = modifyManualPIDController(newProperties, JProperty.kpPitch,
						JProperty.kiPitch, JProperty.kdPitch, pitchController, sensorData.pitch, sensorData.dpitch, uPitch);
					yawController = modifyManualPIDController(newProperties, JProperty.kpYaw, JProperty.kiYaw,	
						JProperty.kdYaw, yawController, sensorData.yaw, sensorData.dyaw, uYaw);
					break;
			}
		} else {
			throw new IllegalStateException("No support for pdcontroller=true");
		}
		
	}

	/* (non-Javadoc)
	 * @see javiator.JControl.JControlAlgorithm#resetController()
	 */
	protected void resetController()
	{
    rollController .resetIntegral( );
    pitchController.resetIntegral( );
    yawController  .resetIntegral( );
    zController    .resetIntegral( );
    uZold = uZnew = uPitch = uYaw = uRoll = 0.0;
    airborne = false;
	}

	/* (non-Javadoc)
	 * @see javiator.JControl.JControlAlgorithm#setProperties(java.util.Properties)
	 */
	protected void setProperties(JProperties properties)
	{
		super.setProperties(properties);
		
		/* Initialize derived coefficients */
		derivedCoefficients = new DerivedCoefficients(properties);
		
		/* Set general properties */
		gravity = properties.getDouble(JProperty.gravity);
		controllerPeriod = properties.getDouble(JProperty.controllerPeriod);
		thrustZControl = properties.getBoolean(JProperty.thrustZControl);
		toleranceRoll = properties.getInt(JProperty.toleranceRoll);
		toleranceYaw = properties.getInt(JProperty.toleranceYaw);
		tolerancePitch = properties.getInt(JProperty.tolerancePitch);
		controlYaw = properties.getBoolean(JProperty.controlYaw);

		/* Set the properties that affect the choice of controllers and controller constructors */
		pdcontroller = properties.getBoolean(JProperty.usePDController);
		setcontrolroots = properties.getInt(JProperty.useSetComplexPoles);
		useSmoothedPidController = properties.getBoolean(JProperty.useSmoothedPidController);
		
		/* Set up the controllers according to the pdcontroller and setpidroots properties and associated parameters */
		if (!pdcontroller) {
			switch(setcontrolroots)
			{
				case SET_COMPLEX_ROOTS:
				case SET_REAL_ROOTS:
					/* PID controllers initialized with setComplexPoles() or setRealPoles() */
					zController = makeAutoPIDController(properties, derivedCoefficients.coefficientZ, JProperty.aZ, 
						JProperty.bZ, JProperty.cZ);
					rollController = makeAutoPIDController(properties, derivedCoefficients.coefficientRoll,
						JProperty.aRoll, JProperty.bRoll, JProperty.cRoll);
					pitchController = makeAutoPIDController(properties, derivedCoefficients.coefficientPitch,
						JProperty.aPitch, JProperty.bPitch, JProperty.cPitch);
					yawController = makeAutoPIDController(properties, derivedCoefficients.coefficientYaw, JProperty.aYaw,
						JProperty.bYaw, JProperty.cYaw);
					break;
				case SET_NO_ROOTS:
					/* PID controllers initialized directly from p, i, and d values */
					zController = makeManualPIDController(properties, JProperty.kpZ, JProperty.kiZ, 
						JProperty.kdZ);
					rollController = makeManualPIDController(properties, JProperty.kpRoll, 
						JProperty.kiRoll, JProperty.kdRoll);
					pitchController = makeManualPIDController(properties, JProperty.kpPitch,
						JProperty.kiPitch, JProperty.kdPitch);
					yawController = makeManualPIDController(properties, JProperty.kpYaw, JProperty.kiYaw,	
						JProperty.kdYaw);
					break;
			}
		} else {
			throw new IllegalStateException("PD controller initialization not currently working");
		}
	}

	/**
	 * Overrideable portion of computeActuatorData calculation that allows the basic JControl to be converted
	 *   into a state observer JControl
	 */
	protected void updateControllers()
	{
		uRoll = rollController  .control( sensorData.roll,  sensorData.droll,  newNavigation.roll );
		uPitch = pitchController .control( sensorData.pitch, sensorData.dpitch, newNavigation.pitch );
		uYaw  = yawController .control( sensorData.yaw, sensorData.dyaw, newNavigation.yaw );
		uZnew = (zController   .control( sensorData.z,   sensorData.dz,   newNavigation.z )+gravity)/(Math.cos(sensorData.roll/1000.0)*Math.cos(sensorData.pitch/1000.0));
		//System.out.println("z=  "+sensorData.z+"  dz=  "+sensorData.dz+"  ztarget=  "+newNavigation.z+"  uZnew=  "+uZnew);
		//System.out.println("yaw=  "+sensorData.yaw+"  dyaw=  "+sensorData.dyaw+"  yawtarget=  "+newNavigation.yaw+"  uYaw=  "+uYaw);
		//System.out.println("roll=  "+sensorData.roll+"  droll=  "+sensorData.droll+"  rolltarget=  "+newNavigation.roll+"  uRoll=  "+uRoll);
		//System.out.println("pitch=  "+sensorData.pitch+"  dpitch=  "+sensorData.dpitch+"  pitchtarget=  "+newNavigation.pitch+"  uPitch=  "+uPitch);
	}

	/**
	 * Write a signal value
	 * @param name
	 * @param value
	 */
	protected void writeSignal(String name, double value){
		if (useSignalWriters) {
			((SignalWriter)signalWriters.get(name)).writeSignalValue(value);
		}
	}
    
    /**
		 * Write each signal to a file
		 *
		 */
		protected void writeSignals(){
			writeSignal(SIGNAL_Z,sensorData.z/1000.0);
			writeSignal(SIGNAL_DZ,sensorData.dz/1000.0);
			
			writeSignal(SIGNAL_ROLL,sensorData.roll/1000.0);
			writeSignal(SIGNAL_DROLL,sensorData.droll/1000.0);
			
			writeSignal(SIGNAL_PITCH,sensorData.pitch/1000.0);
			writeSignal(SIGNAL_DPITCH,sensorData.dpitch/1000.0);
			
			writeSignal(SIGNAL_YAW,sensorData.yaw/1000.0);
			writeSignal(SIGNAL_DYAW,sensorData.dyaw/1000.0);
			
			double cz = derivedCoefficients.coefficientZ;
			writeSignal(SIGNAL_T1,JAviatorPlant.thrust2N(motorSignals.front, cz));
			writeSignal(SIGNAL_T2,JAviatorPlant.thrust2N(motorSignals.right, cz));
			writeSignal(SIGNAL_T3,JAviatorPlant.thrust2N(motorSignals.rear, cz));
			writeSignal(SIGNAL_T4,JAviatorPlant.thrust2N(motorSignals.left, cz));
					
			writeSignal(SIGNAL_Z_TARGET, newNavigation.z/1000.0);
			writeSignal(SIGNAL_ROLL_TARGET, newNavigation.roll/1000.0);
			writeSignal(SIGNAL_PITCH_TARGET, newNavigation.pitch/1000.0);
			writeSignal(SIGNAL_YAW_TARGET, newNavigation.yaw/1000.0);
			
		}

	private PIDController modifyAutoPIDController(JProperties newProperties, double coeffVal, double oldCoeffVal, 
		JDoubleProperty a, JDoubleProperty b, JDoubleProperty c, PIDController controller, 
		double x, double v, double u)
	{
		if (coeffVal != oldCoeffVal || newProperties.contains(a) || newProperties.contains(b) || 
			newProperties.contains(c)) {
			PIDController ans = makeAutoPIDController(newProperties, coeffVal, a, b, c);
			ans.resetIntegral(x, v, u);
			return ans;
		}
		return controller;
	}

	private PIDController modifyManualPIDController(JProperties newProperties, JDoubleProperty kp, 
		JDoubleProperty ki, JDoubleProperty kd, PIDController controller, double x, double v, double u)
	{
		if (newProperties.contains(kp) || newProperties.contains(ki) || newProperties.contains(kd)) {
			PIDController ans = makeManualPIDController(newProperties, kp, ki, kd);
			ans.resetIntegral(x, v, u);
			return ans;
		}
		return controller;
	}
}

