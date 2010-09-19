package javiator.JControl.filters;

import javiator.util.JDoubleProperty;
import javiator.util.JProperties;

public class Butterworth implements Cloneable
{
      private final double a2;
      private final double b1;
      private final double b2;
      
      private double raw_old;
      private double raw_old_old;
      private double filtered_old;
      private double filtered_old_old;

      public Butterworth(JProperties properties, JDoubleProperty fr) {
    	  this(properties.getDouble(fr));
      }
      
      public Butterworth(double fr) {
            //Builds Butterworth coefficients given frequency ratio fr
            // N=2, k=0
            // fr should be > 2.0;
            double omegac = Math.tan(Math.PI/fr);
            double ck = 1.0+2.0*omegac*Math.cos(Math.PI/4.0)+omegac*omegac;
            this.a2 = omegac*omegac/ck;
            this.b1=2.0*(omegac*omegac-1.0)/ck;
            this.b2= (1.0-2.0*omegac*Math.cos(Math.PI/4.0)+omegac*omegac)/ck;
      }
      
      public double apply(double raw_signal)
      {
            double filtered = a2*(raw_signal+2*raw_old+raw_old_old)-b1*filtered_old-b2*filtered_old_old;
            raw_old_old = raw_old;
            raw_old = raw_signal;
            filtered_old_old = filtered_old;
            filtered_old = filtered;
            return  filtered;
      }

      public Object clone()
      {
            try {
                  return super.clone();
            } catch (CloneNotSupportedException e) {
                  throw new IllegalStateException(e.getMessage());
            }
      }

      public double getRawData()
      {
            return raw_old;
      }

      public void reset()
      {
            raw_old = 0;
            raw_old_old = raw_old;
            filtered_old = 0;
            filtered_old_old = filtered_old;
      }
}
