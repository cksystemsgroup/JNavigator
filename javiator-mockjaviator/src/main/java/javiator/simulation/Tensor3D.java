package javiator.simulation;

public class Tensor3D {
	double[][] t;
	public Tensor3D() {
		//Creates a unit tensor
		t = new double[3][3];
		for (int i=0; i < t.length; i++){
			for(int j = 0; j < t[0].length; j++)
				t[i][j]=0.0;
			t[i][i]=1.0;
		}
	}
	public Tensor3D(double x, double y, double z){
		//Crates a diagnol tensor
		t = new double[3][3];
		for (int i=0; i < t.length; i++) {
			for(int j = 0; j < t[0].length; j++)
				t[i][j]=0.0;
		}
		t[0][0]=x;
		t[1][1]=y;
		t[2][2]=z;
	}
	
	public Tensor3D(double[][] M){
		t = new double[3][3];
		for (int i=0; i < t.length; i++){
			for(int j = 0; j < t[0].length; j++)
				t[i][j]=M[i][j];
		} 
	}
	
	
	public Tensor3D multiply( Tensor3D B ) {
		Tensor3D C = new Tensor3D();
		for (int i = 0; i < t.length; i++) {
			for (int j = 0; j < C.t[0].length; j++) {
				C.t[i][j]=0.0;
				for (int k = 0; k < t[0].length; k ++)
					C.t[i][j] += t[i][k]*B.t[k][j];
			}
		}
		return C;
	}
	public Tensor3D transpose(){
		Tensor3D B = new Tensor3D();
		for (int i = 0; i < t[0].length; i++){
			for (int j=0; j < t.length; j++)
				B.t[i][j]=t[j][i];
		}
		return B;
	}
	public Vector3D multiply(Vector3D V) {
		  Vector3D out = new Vector3D();
		  for (int i= 0; i < t.length; i++) {
			  out.v[i]=0.0;
			  for (int j = 0; j < V.v.length; j++)
				  out.v[i] += t[i][j]*V.v[j];
		  }
		  return out;
	  }
}
