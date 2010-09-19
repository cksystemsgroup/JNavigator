package javiator.simulation;

import javiator.util.CommandData;
import javiator.util.MotorSignals;
import javiator.util.SensorData;

/**
 * A degenerate and "cheating" physical model that can be used to make sure communication is
 *   working, or that two controls are equivalent.   The model just echoes the navigation data
 *   and so it behaves as a "perfect" helicopter that always jumps immediately to where it's told.
 */
public class TrivialPhysicalModel implements JAviatorPhysicalModel
{
  private SensorData sensorData = new SensorData( );

  // @see javiator.simulation.JAviatorPhysicalModel#initialize(java.lang.Object)
  public void initialize( Object parameters )
  {
    // Does nothing
  }

  public void simulate( )
  {
    // Does nothing
  }

  public void reset() {
    // Does nothing
  }  
  
  // @see javiator.simulation.JAviatorPhysicalModel#getSensorData()
  public SensorData getSensorData( )
  {
    return sensorData;
  }

  // @see javiator.simulation.JAviatorPhysicalModel#setMotors(javiator.JControl.Motor)
  public void setMotorSignals ( MotorSignals actuator )
  {
    // Does nothing
  }

  // @see javiator.simulation.JAviatorPhysicalModel#setNavigation(javiator.JControl.Navigation)
  public void setCommandData ( CommandData navigation )
  {
    short droll  = (short)( navigation.roll  - sensorData.roll );
    short dpitch = (short)( navigation.pitch - sensorData.pitch );
    short dyaw   = (short)( navigation.yaw   - sensorData.yaw );
    short dz     = (short)( navigation.z     - sensorData.z );

    sensorData.roll   = navigation.roll;
    sensorData.pitch  = navigation.pitch;
    sensorData.yaw    = navigation.yaw;
    sensorData.droll  = droll;
    sensorData.dpitch = dpitch;
    sensorData.dyaw   = dyaw;
    sensorData.z      = navigation.z;
    sensorData.dz     = dz;
  }

}
