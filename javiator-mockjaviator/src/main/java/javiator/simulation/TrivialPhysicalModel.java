package javiator.simulation;

import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;

/**
 * A degenerate and "cheating" physical model that can be used to make sure
 * communication is working, or that two controls are equivalent. The model just
 * echoes the navigation data and so it behaves as a "perfect" helicopter that
 * always jumps immediately to where it's told.
 */
public class TrivialPhysicalModel implements JAviatorPhysicalModel {
	private SensorData sensorData = new SensorData();

	/* (non-Javadoc)
	 * @see javiator.simulation.JAviatorPhysicalModel#initialize(java.lang.Object)
	 */
	public void initialize(Object parameters) {
		// Does nothing
	}

	public void simulate() {
		// Does nothing
	}

	public void reset() {
		// Does nothing
	}

	/* (non-Javadoc)
	 * @see javiator.simulation.JAviatorPhysicalModel#getSensorData()
	 */
	public SensorData getSensorData() {
		return sensorData;
	}

	/* (non-Javadoc)
	 * @see javiator.simulation.JAviatorPhysicalModel#setMotorSignals(at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals)
	 */
	public void setMotorSignals(MotorSignals actuator) {
		// Does nothing
	}

	/* (non-Javadoc)
	 * @see javiator.simulation.JAviatorPhysicalModel#setCommandData(at.uni_salzburg.cs.ckgroup.communication.data.CommandData)
	 */
	public void setCommandData(CommandData navigation) {
		double droll = navigation.getRoll() - sensorData.getRoll();
		double dpitch = navigation.getPitch() - sensorData.getPitch();
		double dyaw = navigation.getYaw() - sensorData.getYaw();
		double dz = navigation.getHeightOverGround() - sensorData.getZ();
		
		sensorData = new SensorData();
		sensorData.setRoll(navigation.getRoll());
		sensorData.setPitch(navigation.getPitch());
		sensorData.setYaw(navigation.getYaw());
		sensorData.setdRoll(droll);
		sensorData.setdPitch(dpitch);
		sensorData.setdYaw(dyaw);
		sensorData.setZ(navigation.getHeightOverGround());
		sensorData.setDz(dz);
	}

}
