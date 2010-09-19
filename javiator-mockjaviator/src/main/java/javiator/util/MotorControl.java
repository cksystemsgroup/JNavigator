package javiator.util;


/**
 * Provide low-level natives (gumstix only) for communicating motor signals to the PWM ports of the
 *   robostix so as to control the motors.  Note that this does not work with the MockJAviator so some
 *   attention to the difference is needed at a higher level.
 */
public class MotorControl
{
	static {
		try {
			System.loadLibrary("MotorControl");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Open and initialize the motor control pathway.
	 * @return a file descriptor to use on other calls or -errno on failure
	 */
	public static final native int openMotorControl();
	
	/**
	 * Set the motor signals to some value from 0 to 1000 (millipercent)
	 * @param fd the file descriptor representing the motor control pathway
	 * @param front value for the front motor
	 * @param right value for the right motor
	 * @param rear value for the rear motor
	 * @param left value for the left motor
	 */
	public static final native void setMotorSignals(int fd, int front, int right, int rear, int left);
	
	/**
	 * Close down the motor control pathway
	 * @param fd the file descriptor that has been used
	 */
	public static final native void closeMotorControl(int fd);
}
