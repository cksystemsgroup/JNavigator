package javiator.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Describes every JProperty in the system
 */
public abstract class JProperty
{
  /* The table of all JProperty definitions that exist.  These will be either
   *  JDoubleProperty, JIntProperty, JBooleanProperty, or JStringProperty.  All
   *  are public static final.   They should be initialized with aDouble, anInt, aBoolean or aString
   *  to assign both a name and a default value.
   */

  /** Property for setting the useSystemDefaultHeapSizes flag */
  public static final JBooleanProperty useDefaultHeapSizes = aBoolean(false);

  /** Property for two graphs */
  public static final JBooleanProperty twoGraphs = aBoolean(false);

  /** Property for Exotask scheduler tracing */
  public static final JStringProperty exotaskTFTrace = aString(null);

  /** The property for setting the 'a' value for zController */
  public static final JDoubleProperty aZ = aDouble(2.0);

  /** The property for setting the 'b' value for zController */
  public static final JDoubleProperty bZ = aDouble(1.0);

  /** The property for setting the 'c' value for zController */
  public static final JDoubleProperty cZ = aDouble(1.0);

  /** The property for setting the 'a' value for rollController */
  public static final JDoubleProperty aRoll = aDouble(2.0);

  /** The property for setting the 'b' value for rollController */
  public static final JDoubleProperty bRoll = aDouble(1.0);

  /** The property for setting the 'c' value for rollController */
  public static final JDoubleProperty cRoll = aDouble(1.0);

  /** The property for setting the 'a' value for pitchController */
  public static final JDoubleProperty aPitch = aDouble(2.0);

  /** The property for setting the 'b' value for pitchController */
  public static final JDoubleProperty bPitch = aDouble(1.0);

  /** The property for setting the 'c' value for pitchController */
  public static final JDoubleProperty cPitch = aDouble(1.0);

  /** The property for setting the 'a' value for yawController */
  public static final JDoubleProperty aYaw = aDouble(1.0);

  /** The property for setting the 'b' value for yawController */
  public static final JDoubleProperty bYaw = aDouble(0.5);

  /** The property for setting the 'c' value for yawController */
  public static final JDoubleProperty cYaw = aDouble(0.5);

  /** The property for setting the 'kp' value for zController */
  public static final JDoubleProperty kpZ = aDouble(1.29);

  /** The property for setting the 'ki' value for zController */
  public static final JDoubleProperty kiZ = aDouble(0.97);

  /** The property for setting the 'kd' value for zController */
  public static final JDoubleProperty kdZ = aDouble(0.376);

  /** The property for setting the 'kp' value for rollController */
  public static final JDoubleProperty kpRoll = aDouble(0.10);

  /** The property for setting the 'ki' value for rollController */
  public static final JDoubleProperty kiRoll = aDouble(0.103);

  /** The property for setting the 'kd' value for rollController */
  public static final JDoubleProperty kdRoll = aDouble(0.0165);

  /** The property for setting the 'kp' value for pitchController */
  public static final JDoubleProperty kpPitch = aDouble(0.10);

  /** The property for setting the 'ki' value for pitchController */
  public static final JDoubleProperty kiPitch = aDouble(0.103);

  /** The property for setting the 'kd' value for pitchController */
  public static final JDoubleProperty kdPitch = aDouble(0.0165);

  /** The property for setting the 'kp' value for yawController */
  public static final JDoubleProperty kpYaw = aDouble(0.3);

  /** The property for setting the 'ki' value for yawController */
  public static final JDoubleProperty kiYaw = aDouble(0.58);

  /** The property for setting the 'kd' value for yawController */
  public static final JDoubleProperty kdYaw = aDouble(0.07);

  /** The property for setting the controller period.    */
  public static final JDoubleProperty controllerPeriod = aDouble(0.020);

  /** Property indicating that a PD controller should be used in lieu of a PID controller */
  public static final JBooleanProperty usePDController = aBoolean(false);

  /* Constants for the possible integer values of USE_AUTO_ROOTS_PROPERTY */
  public static final int SET_NO_ROOTS = 0;
  public static final int SET_REAL_ROOTS = 1;
  public static final int SET_COMPLEX_ROOTS = 2;

  /** Property for establishing whether setComplexPoles or setRealPoles or neither should be called */
  public static final JIntProperty useSetComplexPoles = anInt(SET_REAL_ROOTS);

  /** The property for setting the gravity (really, total motor thrust to overcome gravity) */
  public static final JDoubleProperty gravity = aDouble(1360.0);
  
  public static final JDoubleProperty angleFactor = aDouble (1.0); // aDouble(SensorData.ANGLE_FACTOR);
  public static final JDoubleProperty angularRateFactor = aDouble (1.0); // aDouble(SensorData.ANG_RATE_FACTOR);
  public static final JDoubleProperty accelFactor = aDouble (1.0); // aDouble(SensorData.ACCEL_FACTOR);

  /** The property for setting the MockJAviator host (if omitted a robostix is assumed) */
  public static final JStringProperty mockJAviatorHost = aString("localhost");

  /** Port that the MockJAviator is listening on */
  public static final JIntProperty mockJAviatorPort = anInt(9879);
  
  /** Flag telling the ground station, MockJAviator and JControl to use UDP instead of TCP.  The EControl
   *   will (probably) use UDP unconditionally once the JaviatorDistributer is fully retired. */
  public static final JBooleanProperty useUDP = aBoolean(false);
  
  /** Port that the MockJAviator should answer to iff UDP is used (ignored otherwise) */
  public static final JIntProperty mockJAviatorAnswerPort = anInt(9880);
  
  /** Host that the JControl/EControl program is running on iff UDP is being used ... used by the 
   * MockJAviator and/or ground station to send to the Control program.  Ignored otherwise
   */
  public static final JStringProperty controlProgramHost = aString("localhost");
  
  /** Port to listen on for connections from the ground station */
  public static final JIntProperty terminalPort = anInt(7000);

  /** Port that the ground station will listen on iff UDP is being used ignored otherwise  */
  public static final JIntProperty terminalSendPort = anInt(7001);
  
  /** Host that the ground station is running on iff UDP is being used ... used to send messages to
   * it.  Ignored otherwise
   */
  public static final JStringProperty terminalHost = aString("localhost");

  /** The name of the JAviator (only meaningful if not using MockJAviator) */
  public static final JStringProperty JAviatorName = aString("J1");
  
  /** Realtime priority at which to run the thread in the JAviatorPort */
  public static final JIntProperty javiatorThreadPriority = anInt(10);
  
  /** Realtime priority at which to run the thread in the TerminalPort */
  public static final JIntProperty terminalThreadPriority = anInt(5);
  
  /** Realtime Priority at which to run the JAviator distributer thread (exotask version) or the JControl
   *  inner loop (JControlMain version).   Note that the exotask scheduler thread is even higher priority
   *  and not currently parameterized. */
  public static final JIntProperty controllerThreadPriority = anInt(15);

  /** Parameter indicating a tight loop simulation */
  public static final JBooleanProperty tightLoopSimulation = aBoolean(false);

  /** Parameter indicating that TF tracing of data is enabled (may be toggled during execution) */
  public static final JBooleanProperty logData = aBoolean(false);

  /** Parameter indicating that TF tracing of data is enabled and the ability to toggle logging is disabled */
  public static final JBooleanProperty alwaysLogData = aBoolean(false);

  /** Parameter to indicate how many periods in between sends to on the terminal link */
  public static final JIntProperty terminalLinkMultiplier = anInt(5);

  /** Parameter to indicate how many steps to run the round-trip measurement during initial JAviator contact */
  public static final JIntProperty roundtripAverageSteps = anInt(5);

  /** Parameter indicating test mode (run motors one at a time) */
  public static final JBooleanProperty testMode = aBoolean(false);

  /** Parameter naming the algorithm to be used for control */
  public static final JStringProperty algorithm = aString("javiator.JControl.StateObserverwithIntegralJControl");

  /** Parameter indicating the shutdown angle */
  public static final JIntProperty shutdownAngle = anInt(500);
  
  /** Parameter indicating the Z tolerance */
  public static final JIntProperty toleranceZ = anInt(10);

  /** Parameter indicating the roll tolerance */
  public static final JIntProperty toleranceRoll = anInt(26);

  /** Parameter indicating the pitch tolerance */
  public static final JIntProperty tolerancePitch = anInt(26);

  /** Parameter indicating the yaw tolerance */
  public static final JIntProperty toleranceYaw = anInt(26);

  /** Property providing a stress-testing allocation rate in bytes per second */
  public static final JIntProperty allocate = anInt(0);

  /** Property to identify the serial port to use */
  public static final JIntProperty serialPort = anInt(0);

  /** 'b' value for Z Observer */
  public final static JDoubleProperty bObserverZ = aDouble(1.0);

  /** 'c' value for Z Observer */
  public final static JDoubleProperty cObserverZ = aDouble(1.0);

  /** 'b' value for roll Observer */
  public final static JDoubleProperty bObserverRoll = aDouble(5.0);

  /** 'c' value for roll Observer */
  public final static JDoubleProperty cObserverRoll = aDouble(5.0);

  /** 'b' value for pitch Observer */
  public final static JDoubleProperty bObserverPitch = aDouble(5.0);

  /** 'c' value for pitch Observer */
  public final static JDoubleProperty cObserverPitch = aDouble(5.0);

  /** 'b' value for yaw Observer */
  public final static JDoubleProperty bObserverYaw = aDouble(5.0);

  /** 'c' value for yaw Observer */
  public final static JDoubleProperty cObserverYaw = aDouble(5.0);

  /** Property indicating that yaw should be controlled */
  public static final JBooleanProperty controlYaw = aBoolean(true);

  /** Property indicating the file name or socket for the TF data trace */
  public static final JStringProperty tfTrace = aString(null);

  /** Property indicating that the TF data trace should use the native library route */
  public static final JBooleanProperty tfNative = aBoolean(false);

  /** The name of the serial device */
  public static final JStringProperty serialDevice = aString("/dev/ttyS");

  /** The baud rate of the serial device */
  public static final JStringProperty serialBaudrate = aString("B57600");

  /** The effective x length property */
  public static final JDoubleProperty effective_x_length = aDouble(0.0320);

  /** The effective y length property */
  public static final JDoubleProperty effective_y_length = aDouble(0.0320);

  /** The effective z length property */
  public static final JDoubleProperty effective_z_length = aDouble(0.906);

  /** Thrust noise flag property */
  public static final JBooleanProperty noise_flag = aBoolean(false);

  /** Thrust noise factor property */
  public static final JDoubleProperty noise_factor = aDouble(1000.0);

  /** Z correction flag property */
  public static final JBooleanProperty z_correction_flag = aBoolean(false);

  /** Z offset property */
  public static final JDoubleProperty z_offset = aDouble(0.09);

  /** Sonar position property */
  public static final JDoubleProperty sonar_position = aDouble(0.13);

  /**Plant Class property name*/
  public static final JStringProperty plant_class = aString("javiator.simulation.JAviatorPlant");

  /** U_BOUND property */
  public final static JDoubleProperty uBound = aDouble(1000.0);

  /** 'a' value for Z obsevers with integral */
  public static final JDoubleProperty aObserverZ = aDouble(2.0);

  /** 'a' value for roll obsevers with integral */
  public static final JDoubleProperty aObserverRoll = aDouble(10.0);

  /** 'a' value for pitch obsevers with integral */
  public static final JDoubleProperty aObserverPitch = aDouble(10.0);

  /** 'a' value for yaw obsevers with integral */
  public static final JDoubleProperty aObserverYaw = aDouble(10.0);

	/** Property to turn on SignalWriter debugging (must be false for exotasks) */
	public static JBooleanProperty useSignalWriters = aBoolean(false);
  
	/** Property for identifying the driver class (JControlMain, ExotaskControl, ExotaskHTLControl, etc) */
	public static final JStringProperty driver = aString(null);
	
	/** Property for turning off communications and just running the exotask graph (for measurement purposes) */
	public static final JBooleanProperty graphOnly = aBoolean(false);
	
	/** Property to run the "custom" exotask scheduler trace instead of a tuningfork trace */
	public static final JIntProperty customTrace = anInt(0);

	/** These are the properties for for the ControlTerminal */
	public static final JBooleanProperty controlAppShow3DWindow = aBoolean(false);
	public static final JBooleanProperty controlAppRecord = aBoolean(false);
	public static final JStringProperty controlAppHostname = aString("");
	public static final JStringProperty controlAppScript = aString("");
	public static final JBooleanProperty controlAppThrust = aBoolean(false);
	public static final JIntProperty controlAppSeconds = anInt(0);
	public static final JIntProperty controlTerminalMotionDelay = anInt(10); // ms
	
	/** Property for identifying a single exotask to be traced.  Ignored if exotaskTFTrace is false.  If
	 *   exotaskTFTrace is true and exotaskTraceOnly is omitted then all exotasks are traced.
	 */ 
	public static JStringProperty exotaskTraceOnly = aString(null);

	/** A fixed factor to be applied to z */
	public static JDoubleProperty z_factor = aDouble(1.0);

	/** A fixed factor to be applied to roll */
	public static JDoubleProperty roll_factor = aDouble(1.0);

	/** A fixed factor to be applied to yaw */
	public static JDoubleProperty yaw_factor = aDouble(1.0);

	/** A fixed factor to be applied to pitch */
	public static JDoubleProperty pitch_factor = aDouble(1.0);

	/** Location of the sonar on the bottom of the JAviator */
	public static JIntProperty sonarDistanceFromCenterInMM = anInt(130);

	/** Parameters governing the butterworth filters on z, roll, pitch, yaw, droll, dpitch, and dyaw */
	public static JBooleanProperty filterRoll = aBoolean(false);
	public static JBooleanProperty filterZ = aBoolean(false);
	public static JBooleanProperty filterPitch = aBoolean(false);
	public static JBooleanProperty filterYaw = aBoolean(false);
	public static JBooleanProperty filterDroll = aBoolean(false);
	public static JBooleanProperty filterDpitch = aBoolean(false);
	public static JBooleanProperty filterDyaw = aBoolean(false);
	
	public static JDoubleProperty zFilterFr = aDouble(3.5);
	public static JDoubleProperty pitchFilterFr = aDouble(3.5);
	public static JDoubleProperty rollFilterFr = aDouble(3.5);
	public static JDoubleProperty yawFilterFr = aDouble(3.5);
	public static JDoubleProperty dpitchFilterFr = aDouble(3.5);
	public static JDoubleProperty drollFilterFr = aDouble(3.5);
	public static JDoubleProperty dyawFilterFr = aDouble(3.5);


	/** Governs whether to apply kalman filter to Z velocity */
	public static JBooleanProperty kalmanFilterDZ = aBoolean(false);

	/* Properties for the ThrustJControl */
	public static JIntProperty motorFrontAdjustment = anInt(0);
	public static JIntProperty motorRearAdjustment = anInt(0);
	public static JIntProperty motorLeftAdjustment = anInt(0);
	public static JIntProperty motorRightAdjustment = anInt(0);
	public static JDoubleProperty thrustControlZFactor = aDouble(0.5);
	public static JDoubleProperty thrustControlRollFactor = aDouble(0.25);
	public static JDoubleProperty thrustControlPitchFactor = aDouble(0.25);
	public static JDoubleProperty thrustControlYawFactor = aDouble(0.25);

	/* Special flag to disable normal Z control and substitute thrust control */
	public static JBooleanProperty thrustZControl = aBoolean(false);

	/** The JAviator mass in grams (currently used by Kalman filter only) */
	public static final JIntProperty massInGrams = anInt(2000);

	/** The kalman filter R parameter */
	public static final JIntProperty kalmanR = anInt(10);

	/** The kalman filter Q parameter */
	public static final JIntProperty kalmanQ = anInt(0);
    
	/** Flag indicating the use of a smoothed PIDController */
	public static JBooleanProperty useSmoothedPidController = aBoolean(true);

	/** The max change value for use with smoothed PIDController */
	public static JDoubleProperty pidControllerMaxChange = aDouble(20.0);

	/** Motor test minimum */
	public static JIntProperty motorTestMin = anInt(150);

	/** Motor test maximum */
	public static JIntProperty motorTestMax = anInt(200);

	/** Motor test step */
	public static JIntProperty motorTestStep = anInt(1);

	/** Motor revving up increment */
	public static JIntProperty motorRevvingUp = anInt(8);

	/** Motor revving down minimum */
	public static JIntProperty motorRevvingDown = anInt(4);

	/** The gravitational acceleration in meters/sec^2 */
	public static JDoubleProperty gravitationalAcceleration = aDouble(9.8);

	/** Indicates that the new compact (single packet) format is to be communicated to the control terminal */
	public static JBooleanProperty useCompactReport = aBoolean(false);

	/** Indicates that it is ok to use Java priorities rather than real time priorities for communications threads
	 *  (both JControlMain and ExotaskControl) and for the control thread (JControlMain ... exotasks use their
	 *  own mechanism). */
	public static JBooleanProperty useJavaPriorities = aBoolean(true);

	/** Number of periods in which there is no new navigation data before entering emergency shutdown.  At the
	 *  default period of 20 ms, the default value of 25 represent 500ms or 1/2 second. */
	public static JIntProperty missedHeartbeatThreshhold = anInt(25);

	/** Number of periods to wait before reporting state in the MockJAviator.  If set to zero, the MockJaviator
	 *  is silent */
	public static JIntProperty mockJAviatorReportRate = anInt(0);

	/**
	 * Number of periods to wait before reporting the <code>SensorData</code> to the auto pilot. BEWARE: Changing this parameter implies changing all the auto pilot filters!
	 * @uml.property  name="pilotSensorDataReportRate"
	 * @uml.associationEnd
	 */
	public static JIntProperty pilotSensorDataReportRate = anInt (4);

  /* End of table ... start of supporting fields and methods */

  /** All the properties as a sorted array */
  public static final JProperty[] properties = initialize();

  /** Every JProperty has a name. */
  public String name;
  
  /** Every JProperty can format its defaultValue as a String */
  public abstract String getDefaultValueAsString();
  
  /** Convenience constructor for JBooleanProperty when building the table */
  public static JBooleanProperty aBoolean(boolean defaultValue)
  {
    JBooleanProperty ans = new JBooleanProperty();
    ans.defaultValue = defaultValue;
    return ans;
  }

  /** Convenience constructor for JDoubleProperty when building the table */
  public static JDoubleProperty aDouble(double defaultValue)
  {
    JDoubleProperty ans = new JDoubleProperty();
    ans.defaultValue = defaultValue;
    return ans;
  }

	/** Convenience constructor for JIntProperty when building the table */
  public static JIntProperty anInt(int defaultValue)
  {
    JIntProperty ans = new JIntProperty();
    ans.defaultValue = defaultValue;
    return ans;
  }

  /** Convenience constructor for JStringProperty when building the table */
  public static JStringProperty aString(String defaultValue)
  {
    JStringProperty ans = new JStringProperty();
    ans.defaultValue = defaultValue;
    return ans;
  }
  
  /**
   * Create a default.properties file in the current directory
   * @param args ignored
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException
  {
  	FileWriter out = new FileWriter("default.properties");
  	PrintWriter wtr = new PrintWriter(out);
  	for (int i = 0; i < properties.length; i++) {
			wtr.println(properties[i].name + "=" + properties[i].getDefaultValueAsString());
		}
  	wtr.close();
  	out.close();
  }

  /**
   * Generate the list of properties and fill in the name fields of all the properties using reflection
   * @return the list of properties as a an array, sorted by name
   */
  private static JProperty[] initialize()
	{
  	SortedMap properties = new TreeMap();
  	Field[] fields = JProperty.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			if (JProperty.class.isAssignableFrom(f.getType()) && (f.getModifiers() & Modifier.STATIC) != 0) {
				try {
					JProperty p = (JProperty) f.get(null);
					p.name = f.getName();
					properties.put(p.name, p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (JProperty[]) properties.values().toArray(new JProperty[0]);
	}
}
