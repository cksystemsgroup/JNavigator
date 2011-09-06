
package javiator.simulation;

import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;

/**
 * Abstracts the physical model so that different ones can be evaluated
 */
public interface JAviatorPhysicalModel
{
	/**
	 * Called once after instantiation to set any parameters
	 * 
	 * @param parameters
	 *            the parameters to set (depends entirely on the model
	 *            implementation)
	 */
	public void initialize(Object parameters);

	/**
	 * Run the simulation
	 */
	public void simulate();

	/**
	 * Get the sonar values
	 * 
	 * @return the sonar values
	 */
	public SensorData getSensorData();

	/**
	 * Specify the motor speeds for the model
	 * 
	 * @param motor
	 *            new motor speeds
	 */
	public void setMotorSignals(MotorSignals actuator);

	/**
	 * Specify the current navigation data. A proper simulation should ignore
	 * this information, since the control should have used it to generate
	 * proper motor values and the physical model should respond only to those.
	 * However, some degenerate models used for testing may want to "cheat" by
	 * knowing the navigation data.
	 * 
	 * @param navigation
	 *            the latest navigation data
	 */
	public void setCommandData(CommandData navigation);

	/**
	 * Reset the physical model.
	 */
	public void reset();
}
