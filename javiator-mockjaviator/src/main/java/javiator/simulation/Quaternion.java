package javiator.simulation;

//import java.util.Random;

public class Quaternion {
	public double s;
	public Vector3D v;
	
	public Quaternion(){
		s=1.0;
		v = new Vector3D();
	}
	
	public Quaternion(double scalar, Vector3D vector) {
		s = scalar;
		v = vector;
	}
	
	public Quaternion( double scalar) {
		s = scalar;
		v = new Vector3D();
	}
	
	public void copy(Quaternion b) {
		b.s = s;
		b.v = v.copyInto(b.v);
	}
	public Tensor3D quaternionToRotationTensor3D() {
		//Agrees with David Baraff (Carnegie Mellon) 
		//"An Introduction to Physically Based Modelling..."
		//Transformation from body to space coordinates
		Tensor3D R = new Tensor3D();
		double vv = v.dotProduct(v);
		for (int i = 0; i < R.t.length; i++) {
			R.t[i][i]=1-2.0*vv+2.0*v.v[i]*v.v[i];
			for (int j = 0; j < R.t.length; j++){
				if (i != j) {
					R.t[i][j]= 2.0*v.v[i]*v.v[j];
				}
			}
		}
		R.t[0][1]+= -2.0*s*v.v[2];
		R.t[1][0]+=  2.0*s*v.v[2];
		R.t[0][2]+=  2.0*s*v.v[1];
		R.t[2][0]+= -2.0*s*v.v[1];
		R.t[1][2]+= -2.0*s*v.v[0];
		R.t[2][1]+=  2.0*s*v.v[0];
		return R;
	}
	
	public Quaternion normalize(){
		double sum=s*s+v.dotProduct(v);
		sum = Math.sqrt(sum);
		s=s/sum;
		v = v.multiply(1.0/sum);
		return this;
	}
	
	public Quaternion add(Quaternion b) {
		Quaternion q = new Quaternion();
		q.s=s+b.s;
		q.v=v.add(b.v);
		return q;
	}
	
	public Quaternion multiply(double b) {
		Quaternion q = new Quaternion();
		q.s = b*s;
		q.v = v.multiply(b);
		return q;
	}
	
	public Quaternion multiply(Quaternion qin) {
		Quaternion q = new Quaternion();
		q.s = this.s*qin.s-(this.v).dotProduct(qin.v);
		q.v = (((this.v).multiply(qin.s)).add((qin.v).multiply(this.s))).add((this.v).crossProduct(qin.v));
		return q;
	}
	
	public Quaternion premultiplybyomega(Vector3D w) {
		Quaternion q = new Quaternion();
		q.s = -v.dotProduct(w);
		q.v = (w.multiply(s)).add(w.crossProduct(v));
		return q;
	}
	
	public Vector3D transform(Vector3D X) {
		// transforms the vector from body to space coordinates
		Vector3D result = X.multiply(2.0*s*s-1);
		result = result.add(v.multiply(2.0*X.dotProduct(v)));
		result = result.add((X.crossProduct(v)).multiply(-2.0*s));
		return result;
	}
	
	public Vector3D inversetransform(Vector3D X) {
		// transforms the vector from space to body coordinates
		Vector3D result = X.multiply(2.0*s*s-1);
		result = result.add(v.multiply(2.0*X.dotProduct(v)));
		result = result.add((X.crossProduct(v)).multiply(2.0*s));
		return result;
	}
	//from http://wiki.beyondunreal.com/wiki/Quaternion
	// Changed to follow my convention
	public double yaw(){
		double y = 0.0;
		double c = 2.0*(s*v.v[1]-v.v[0]*v.v[2]);
		if (c < 1.0) {
			if ( -1.0 < c) {
				y = Math.atan2(2.0*(v.v[0]*v.v[1]+s*v.v[2]), 1.0-2.0*(v.v[1]*v.v[1]+v.v[2]*v.v[2]));
			} else {
				y = 0.0;
			}
		} else {
			y = 0.0;		
		}
		return y;
	}
	public double pitch(){
		double p = 0.0;
		//double c = 2.0*(s*v.v[1]-v.v[0]*v.v[2]);
		double c = 2.0*(s*v.v[1]+v.v[0]*v.v[2]);
		if (c < 1.0) {
			if ( -1.0 < c) {
				p = Math.asin(c);
			} else {
				p = -Math.PI/2.0;
			}
		} else {
			p = Math.PI/2.0;		
		}
		return p;
	}
	public double roll(){
		double r = 0.0;
		//double c = 2.0*(s*v.v[1]-v.v[0]*v.v[2]);
		double c = 2.0*(s*v.v[1]+v.v[0]*v.v[2]);
		if (c < 1.0) {
			if ( -1.0 < c) {
				r = Math.atan(2.0*(v.v[1]*v.v[2]+s*v.v[0])/(1.0-2.0*(v.v[0]*v.v[0]+v.v[2]*v.v[2])));
			} else {
				r = -Math.atan(2.0*(v.v[1]*v.v[2]-s*v.v[0])/(1.0-2.0*(v.v[0]*v.v[0]+v.v[2]*v.v[2])));
			}
		} else {
			r = -Math.atan(2.0*(v.v[1]*v.v[2]-s*v.v[0])/(1.0-2.0*(v.v[0]*v.v[0]+v.v[2]*v.v[2])));		
		}
		return r;
	}
	public String toString() {
		String str = new String();
		str +=s+"  ";
		for (int i = 0; i < v.v.length; i++) {
			str+= " "+v.v[i]+" ";
		}
		str+=" \n";
		return str;
	}
	public static void main(String[] args) {
		//Random random = new Random(3);
		Vector3D g = new Vector3D(0.0, 0.0, 1.0);
		Quaternion deltaq;
		Quaternion qnew;
		Vector3D omega = new Vector3D(0.0, 1.0, 0.0);
	//	for (int i = 0; i < 10; i++) {
			System.out.println("\n  A new quaternion");
		//	Vector3D vector = new Vector3D( random.nextDouble()*0.1, random.nextDouble()*0.1, random.nextDouble());
		//	Quaternion q = new Quaternion( random.nextDouble(),vector);
			
		//	Vector3D vector = new Vector3D(0.5, 0.5, 0.5);
		//	Quaternion q = new Quaternion(1.0, vector);
			double pitchin = 1.0;
			double rollin = 1.0;
			double yawin = 1.0;
			
			Quaternion qyawin = new  Quaternion(Math.cos(yawin/2.0),new Vector3D(0.0,0.0,Math.sin(yawin/2.0)));
			Quaternion qpitchin = new  Quaternion(Math.cos(pitchin/2.0),new Vector3D(0.0, Math.sin(pitchin/2.0),0.0));
			Quaternion qrollin = new  Quaternion(Math.cos(rollin/2.0),new Vector3D(Math.sin(rollin/2.0),0.0,0.0));
			Quaternion q = qrollin.multiply(qpitchin.multiply(qyawin));
			System.out.println("omega:  "+omega.toString());
			q.normalize();
		//	for (int j = 0; j < 5; j++) {
				Tensor3D R = q.quaternionToRotationTensor3D();
				System.out.println("pitch:  "+q.pitch()+"  roll:  "+q.roll()+"  yaw:  "+q.yaw());
				double roll = Math.atan2( R.t[2][1], R.t[1][1] );
				double pitch = Math.atan2( R.t[0][2], R.t[0][0] );
				double yaw = Math.atan2( R.t[1][0], R.t[0][0] );
				System.out.println("pitch:  "+pitch+"  roll:  "+roll+"  yaw:  "+yaw);
				System.out.println("omegabody:   "+q.inversetransform(omega).toString());
				System.out.print("q:  "+q.toString());
				System.out.print("R:  "+R.toString());
				Vector3D result = q.inversetransform(g);
				System.out.println("gtranformed =   "+result.toString());
				result = (R.transpose()).multiply(g);
				System.out.println("gtranformed =   "+result.toString());
				System.out.println("\n");
				deltaq = q.premultiplybyomega( omega.multiply( 0.5*0.050 ) );
				qnew = q.add( deltaq );
				qnew.normalize( );
				qnew.copy(q);
		//	}
			System.out.println();
	//	}
	}
}
