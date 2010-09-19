package javiator.JControl;

public class AngleWithWindingNumber implements Cloneable {
	private int windingNumber;
	private double oldAngle; // last state of the system.
    private double initialAngle; //The yaw offset
	private final static double Pi=1000.0*Math.PI;  //Assumes a scale of 1000 (anlge in milliradians)
	
	//Normalizes the angle to be between (-Pi,Pi] 
	public static double normalize(double inputAngle) {
		double outputAngle = inputAngle;
		while (outputAngle > Pi)
			outputAngle -= 2.0*Pi;
		while (outputAngle <= -Pi)
			outputAngle += 2.0*Pi;
		return outputAngle;
	}
    
	public AngleWithWindingNumber(int initial){
		windingNumber = 0;
		oldAngle = normalize(initial);
		initialAngle = oldAngle;		
	}
    
	public double updateAndGetContinuousAngle(int input){
		double dinput = normalize(input);
		double output = dinput + 2*Pi*windingNumber;
		if (Math.abs(output-oldAngle)  > Pi) {		
		windingNumber -= Math.round(((double)(dinput + 2*Pi*windingNumber-oldAngle))/((double) 2*Pi));
		output = dinput+2*Pi*windingNumber;
		}
		oldAngle = output;
		return (oldAngle - initialAngle);
	}
	
	public double updateAndGetModuloAngle(int input) {
		double dinput = normalize(input);
		double output = dinput + 2*Pi*windingNumber;
		if (Math.abs(output-oldAngle)  > Pi) {		
		windingNumber -= Math.round(((double)(dinput + 2*Pi*windingNumber-oldAngle))/((double) 2*Pi));
		output = dinput+2*Pi*windingNumber;
		}
		oldAngle = output;		
		return normalize(oldAngle - initialAngle);
	}
	
	public double getInitialAngle() {
		return( initialAngle );
	}

	public double getContinuousAngle() {
		return (oldAngle - initialAngle);
	}
	
	public double getModuloAngle() {
		return normalize(oldAngle - initialAngle);
	}
	
	public Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(); // won't occur
		}
	}
    
    /*
     * Test harness
     */
	public static void main(String[] args) {
		int initialangle = 0;
		AngleWithWindingNumber awwn = new AngleWithWindingNumber(initialangle);
		double radianangle = 0.0;
		double cs;
		double ss;
		int modangle;
		System.out.println("\n  Increasing angle");
		for (int i = 0; i < 100; i++) {
			radianangle += 0.1;
			cs = (float) Math.cos(radianangle);
			ss = (float)Math.sin(radianangle);
			modangle= Math.round((float) (1000*Math.atan2(ss, cs)));
			awwn.updateAndGetContinuousAngle(modangle);
			System.out.println("radianangle=  "+Math.round((float) (radianangle*1000))+"  modangle=  "+modangle+"  continuousAngle=  "+awwn.getContinuousAngle()+"  modAngle=  "+awwn.getModuloAngle());	
		}
		System.out.println("\n  Decreasing anlge");
		awwn = new AngleWithWindingNumber(initialangle);
		radianangle = 0.0;
		for (int i = 0; i < 100; i++) {
			radianangle -= 0.1;
			cs = (float) Math.cos(radianangle);
			ss = (float)Math.sin(radianangle);
			modangle= Math.round((float) (1000*Math.atan2(ss, cs)));
			awwn.updateAndGetContinuousAngle(modangle);
			System.out.println("radianangle=  "+Math.round((float) (radianangle*1000))+"  modangle=  "+modangle+"  continuousAngle=  "+awwn.getContinuousAngle()+"  modAngle=  "+awwn.getModuloAngle());	
		}
	}
}
