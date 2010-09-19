package javiator.util;

public class ThrustComponents implements ControllerDataValues
{
    public double uRoll;
    public double uPitch;
    public double uYaw;
    public double uZ;
    
    public ThrustComponents(double uRoll, double uPitch, double uYaw, double uZ) {
        this.uRoll = uRoll;
        this.uPitch = uPitch;
        this.uYaw = uYaw;
        this.uZ = uZ;
    }
    
    public ThrustComponents( ) {}
}
