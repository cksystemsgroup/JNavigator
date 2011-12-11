package at.uni_salzburg.cs.ckgroup.terminal;

import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;

/**
 * @author scraciunas
 *
 */
public interface JAviator3DControl{
    
    /**
     * interface function to create the scene and the model
     * the implementation should remain the same
     */
    public void createModel();
    
    /**
     * reset the model
     * should remain the same
     */
    public void resetModel();
    
    /**
     * send the sensor data
     * implementation cast to whatever you need
     * eg. in ControlApp the data is received as SensorData 
     * @param data
     */
    public void sendSensorData(SensorData sensorData, CommandData actuatorData);
    
    /**
     * send the motor values
     * implementation cast to whatever you need
     * eg. in ControlApp the data is received as ActuatorData  
     * @param data
     */
    public void setRotorSpeed(Object data);
}
