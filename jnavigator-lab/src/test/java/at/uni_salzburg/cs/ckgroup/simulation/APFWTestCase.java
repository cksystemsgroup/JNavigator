
package at.uni_salzburg.cs.ckgroup.simulation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import junit.framework.TestCase;

public class APFWTestCase extends TestCase {

    public static final int COMMAND_NOP             = 0;
    public static final int COMMAND_OPEN            = 1;
    public static final int COMMAND_ACK             = 2;
    public static final int COMMAND_CLOSE           = 3;

    public static final int AHRS_STATE              = 40;
	
    private char[] hexConverter = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    private String toHex (byte b) {
    	StringBuffer bld = new StringBuffer ();
    	int n = 0xFF & b;
    	bld.append(hexConverter[n>>4]);
    	bld.append(hexConverter[n&0xF]);
    	return bld.toString();
    }
    
    public void testCase00 () {
    	assertEquals ("00",toHex((byte)0x00));
    	assertEquals ("0F",toHex((byte)0x0F));
    	assertEquals ("F0",toHex((byte)0xF0));
    	assertEquals ("FF",toHex((byte)0xFF));
    	assertEquals ("10",toHex((byte)0x10));
    	assertEquals ("01",toHex((byte)0x01));
    }
	
	public void NOtestCase01 () {
		String hostname = "localhost";
		int port = 2002;
		
		InetAddress address;
		try {
			address = InetAddress.getByName(hostname);
			DatagramSocket clientSocket = new DatagramSocket();
		
			udp_send (clientSocket, address, port, COMMAND_OPEN, null, 0);
			
			byte[] data = new byte[256];
			DatagramPacket packet = new DatagramPacket (data, 256);
			boolean ok = true;
			APFWState state = new APFWState ();
			
			while (ok) {
				clientSocket.receive(packet);
				
				int seconds = bytes2int (reverse (partition (data, 0, 4)));
				int microSeconds = bytes2int (reverse (partition (data, 4, 4)));
				int type = bytes2int (reverse (partition (data, 8, 4)));
				long time = seconds * 1000 + microSeconds / 1000;
				
				if (type != AHRS_STATE)
					System.out.println ("testCase01, received: time=" + time + ", type=" + type);
				
				switch (type) {
				case AHRS_STATE: handle_ahrs_state (time, partition (data, 12, 146), state); break;
				
				case COMMAND_NOP:
				case COMMAND_OPEN:
				case COMMAND_ACK:
				case COMMAND_CLOSE: break;
				default:
					System.out.println ("testCase01, received: time=" + time + ", unknown type=" + type);
				}
			}
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private void handle_ahrs_state (long time, byte[] data, APFWState state) throws IOException {
		/* Body frame linear accelerations */
		state.ax    = bytes2double (reverse (partition (data,   0, 8)));
		state.ay    = bytes2double (reverse (partition (data,   8, 8)));
		state.az    = bytes2double (reverse (partition (data,  16, 8)));
		/* Body frame rotational rates */
		state.p     = bytes2double (reverse (partition (data,  24, 8)));
		state.r     = bytes2double (reverse (partition (data,  32, 8)));
		state.q     = bytes2double (reverse (partition (data,  40, 8)));
		/* Position relative to the ground */
		state.x     = bytes2double (reverse (partition (data,  48, 8)));
		state.y     = bytes2double (reverse (partition (data,  56, 8)));
		state.z     = bytes2double (reverse (partition (data,  64, 8)));
		/* Euler angles relative to the ground */
		state.phi   = bytes2double (reverse (partition (data,  72, 8)));
		state.theta = bytes2double (reverse (partition (data,  80, 8)));
		state.psi   = bytes2double (reverse (partition (data,  88, 8)));
		/* Velocity over the ground */
		state.vx    = bytes2double (reverse (partition (data,  96, 8)));
		state.vy    = bytes2double (reverse (partition (data, 104, 8)));
		state.vz    = bytes2double (reverse (partition (data, 112, 8)));
		/* Moments on the rotor mass */
		state.mx    = bytes2double (reverse (partition (data, 120, 8)));
		state.my    = bytes2double (reverse (partition (data, 128, 8)));
		
		System.out.println (
//				"ax=" + state.ax + ", ax=" + state.ay + ", az=" + state.az + ", " +
				"x=" + state.x + ", y=" + state.y + ", z=" + state.z + ", " +
				"phi=" + state.phi + ", theta=" + state.theta + ", psi=" + state.psi
				);
	}



	public void udp_send (DatagramSocket socket, InetAddress address, int port, int type, byte[] msg, int msgLength) throws IOException {
		System.currentTimeMillis();
		udp_send_raw (socket, address, port, type, System.currentTimeMillis(), msg, msgLength);
	}
	
	public void udp_send_raw (DatagramSocket socket, InetAddress address, int port, int type, long time, byte[] msg, int msgLength) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream ();
		int seconds = (int) (time / 1000);
		int microSeconds = (int) (1000*(1000*seconds - time));

		bOut.write (reverse (int2bytes (seconds)));
		bOut.write (reverse (int2bytes (microSeconds)));
		bOut.write (reverse (int2bytes (type)));
		if (msg != null && msgLength > 0)
			bOut.write (msg, 0, msgLength);
		
		byte[] buf = bOut.toByteArray();
		DatagramPacket packet = new DatagramPacket (buf, buf.length, address, port);
		socket.send (packet);
	}
	
	public byte[] reverse (byte[] a) {
		byte[] b = new byte[a.length];
		for (int k=0; k < a.length; k++)
			b[k] = a[a.length-1-k];
		return b;
	}
	
	public byte[] partition (byte[] a, int from, int length) {
		byte[] b = new byte[length];
		for (int k=0; k < length; k++)
			b[k] = a[from + k];
		return b;
	}
	
	public byte[] int2bytes (int i) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream ();
		DataOutputStream dOut = new DataOutputStream (bOut);
		dOut.writeInt(i);
		return bOut.toByteArray();
	}
	
	public byte[] double2bytes (double d) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream ();
		DataOutputStream dOut = new DataOutputStream (bOut);
		dOut.writeDouble(d);
		return bOut.toByteArray();
	}
	
	public int bytes2int (byte[] a) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream (a);
		DataInputStream din = new DataInputStream(bin);
		return din.readInt();
	}
	
	public double bytes2double (byte[] a) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream (a);
		DataInputStream din = new DataInputStream(bin);
		return din.readDouble();
	}
	
	public class APFWState {
        /* Body frame linear accelerations */
        double          ax;
        double          ay;
        double          az;

        /* Body frame rotational rates */
        double          p;
        double          r;
        double          q;

        /* Position relative to the ground */
        double          x;
        double          y;
        double          z;

        /* Euler angles relative to the ground */
        double          phi;
        double          theta;
        double          psi;

        /* Velocity over the ground */
        double          vx;
        double          vy;
        double          vz;

        /* Moments on the rotor mass */
        double          mx;
        double          my;
	}
	
	public void NOtestCase02 () {
		// 0c 56 77 ba 8f 90 b9 3f <-> 0.099862
		byte[] a = { (byte)0x3f, (byte)0xb9, (byte)0x90, (byte)0x8f, (byte)0xba, (byte)0x77, (byte)0x56, (byte)0x0c };   

		System.out.print("lala: ");
		for (int k=0; k < a.length; k++) {
//			System.out.printf ("%02x ", new Object[] {new Integer (0xFF & a[k])});
			System.out.print(toHex(a[k]));
		}
		System.out.println ();
		
		ByteArrayInputStream bin = new ByteArrayInputStream (a);
		DataInputStream din = new DataInputStream(bin);
		try {
			double d = din.readDouble();
			System.out.println ("Double is: " + d);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double d2 = 0.09986208250423817; // 0.099862;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream ();
		DataOutputStream dOut = new DataOutputStream (bOut);
		try {
			dOut.writeDouble(d2);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] ba = bOut.toByteArray();
		System.out.print ("double d2=" + d2 + " -> ");
		for (int k=0; k < ba.length; k++) {
//			System.out.printf ("%02x ", new Object[] {new Integer (0xFF & ba[k])});
			System.out.print(toHex(ba[k]));
		};
		System.out.println ();
	}

	public void NOtestCase03 () {
		// 16 00 00 00 <-> 22
//		byte[] a = { 0x16, 0x0, 0x0, 0x0 };
		byte[] a = { 0x0, 0x0, 0x0, 0x16 };
		
		ByteArrayInputStream bin = new ByteArrayInputStream (a);
		DataInputStream din = new DataInputStream(bin);
		try {
			long l = din.readInt();
			System.out.println ("testCase03.Long is: " + l);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	

}
