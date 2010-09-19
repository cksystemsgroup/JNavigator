package javiator.simulation;

public class Vector3D {
	double[] v;
	public Vector3D(){
		v = new double[3];
		for (int i =0; i < v.length; i++)
			v[i]=0.0;
	}
	
	public Vector3D(double x, double y, double z){
		v = new double[3];
		v[0]=x;
		v[1]=y;
		v[2]=z;
		
	}
	
	public Vector3D multiply(double scalar) {
		  Vector3D Vout = new Vector3D();
		  for (int i = 0; i < v.length; i++) Vout.v[i]=v[i]*scalar;
		  return Vout;
	  }
	
	public Vector3D crossProduct(Vector3D B) {
		  Vector3D C = new Vector3D();
		  C.v[0]=v[1]*B.v[2]-v[2]*B.v[1];
		  C.v[1]=v[2]*B.v[0]-v[0]*B.v[2];
		  C.v[2]=v[0]*B.v[1]-v[1]*B.v[0];
		  return C;
	  }
	
	public Vector3D add(Vector3D B) {
		  Vector3D C = new Vector3D();
		  for (int i = 0; i < C.v.length; i++)
			  C.v[i]=v[i]+B.v[i];
		  return C;
	  }
	
	public Vector3D copyInto(Vector3D B) {
		  for (int i = 0; i < v.length; i++)
			  B.v[i]=v[i];
		  return B;
	  }
	
	public double dotProduct(Vector3D B){
		double m = 0.0;
		for (int i = 0; i < v.length; i++) m+= v[i]*B.v[i];
		return m;
	}
}
