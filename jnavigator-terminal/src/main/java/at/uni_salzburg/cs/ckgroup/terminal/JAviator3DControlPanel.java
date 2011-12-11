package at.uni_salzburg.cs.ckgroup.terminal;

import simulation.JAviator3D;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.SimulationData;

/**
 * @author scraciunas
 *
 */
public class JAviator3DControlPanel implements JAviator3DControl {
    
    private JAviator3D javiator3D;

    public JAviator3DControlPanel(){
        javiator3D = new JAviator3D();
        javiator3D.setOldControlTerminal(false);
    }

    /* (non-Javadoc)
     * @see javiator.ui.JAviator3DControl#createModel()
     */
    public void createModel() {
        javiator3D.createModel();
    }

    /* (non-Javadoc)
     * @see javiator.ui.JAviator3DControl#resetModel()
     */
    public void resetModel() {
        javiator3D.resetModel();
    }

    /* (non-Javadoc)
     * @see javiator.ui.JAviator3DControl#sendSensorData(javiator.util.SensorData)
     */
    public void sendSensorData(SensorData sensorData, CommandData actuatorData) {
        javiator3D.sendSensorData((float)sensorData.getRoll(),
        		(float)sensorData.getPitch(), 
        		(float)sensorData.getYaw(),
        		(float)sensorData.getX(),
        		(float)sensorData.getY(),
        		(float)sensorData.getZ(),
        		(float)actuatorData.getRoll(), 
        		(float)actuatorData.getPitch(), 
        		(float)actuatorData.getYaw(), 
        		(float)actuatorData.getHeightOverGround()+1f);
    }

    /* (non-Javadoc)
     * @see javiator.ui.JAviator3DControl#setRotorSpeed(javiator.util.ActuatorData)
     */
    public void setRotorSpeed(Object data) {
        MotorSignals actuatorData = (MotorSignals) data;
        javiator3D.setRotorSpeed(actuatorData.getFront(), actuatorData.getRear(), actuatorData.getRight(), actuatorData.getLeft());
    }

	public void sendSimulationData(SimulationData simulationData, CommandData actuatorData) {
        javiator3D.sendSensorData((float)simulationData.getRoll(),
        		(float)simulationData.getPitch(), 
        		(float)simulationData.getYaw(),
        		(float)simulationData.getX(),
        		(float)simulationData.getY(),
        		(float)simulationData.getZ(),
        		(float)actuatorData.getRoll(), 
        		(float)actuatorData.getPitch(), 
        		(float)actuatorData.getYaw(), 
        		(float)actuatorData.getHeightOverGround()+1f);
	}
}
