/*
 * (c) Copyright IBM Corp. 2007  All Rights Reserved
 */

/*
 * $RCSfile: DerivedCoefficients.java,v $
 * $Revision: 1.1.6.3 $
 * $Date: 2009/11/03 00:04:56 $
 */
package javiator.JControl;

import javiator.util.JProperties;
import javiator.util.JProperty;

/**
 * This class represents the set of coefficients that are derived from gravitational acceleration, 
 *   the thrust to overcome gravity, and the effective x, y, and z lengths
 */
public class DerivedCoefficients implements Cloneable
{
	public double coefficientZ, coefficientRoll, coefficientPitch, coefficientYaw;

	private double gravity, gravitational_acceleration, effective_x_length, effective_y_length, effective_z_length;
	public DerivedCoefficients(JProperties properties)
	{
		gravity = properties.getDouble(JProperty.gravity);
		gravitational_acceleration = properties.getDouble(JProperty.gravitationalAcceleration);
		effective_x_length = properties.getDouble(JProperty.effective_x_length);
		effective_y_length = properties.getDouble(JProperty.effective_y_length);
		effective_z_length = properties.getDouble(JProperty.effective_z_length);
		compute();
	} 

	/**
	 * Modify the properties after some of them have been set
	 * @param properties new properties (only those that have changed)
	 */
	public void modifyProperties(JProperties properties)
	{
		gravity = properties.getDouble(JProperty.gravity, gravity);
		gravitational_acceleration = properties.getDouble(JProperty.gravitationalAcceleration, 
			gravitational_acceleration);
		effective_x_length = properties.getDouble(JProperty.effective_x_length, effective_x_length);
		effective_y_length = properties.getDouble(JProperty.effective_y_length, effective_y_length);
		effective_z_length = properties.getDouble(JProperty.effective_z_length, effective_z_length);
		compute();
	}
	
	protected Object clone()
	{
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}
	
	/**
	 * Compute the derived coefficient values 
	 */
	private void compute()
	{
		coefficientZ = gravitational_acceleration*1000/gravity;
    coefficientRoll = coefficientZ/effective_x_length;
    coefficientPitch = coefficientZ/effective_y_length;
    coefficientYaw = coefficientZ/effective_z_length;
	}
}
