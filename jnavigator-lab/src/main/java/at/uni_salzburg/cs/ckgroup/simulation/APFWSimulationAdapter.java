/*
 * @(#) APFWSimulationAdapter.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2009  Clemens Krainer
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package at.uni_salzburg.cs.ckgroup.simulation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.SimpleTimeZone;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.control.IRemoteControl;
import at.uni_salzburg.cs.ckgroup.course.CartesianCoordinate;
import at.uni_salzburg.cs.ckgroup.course.Matrix3x3;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.util.ByteArrayUtils;

/**
 * This class implements an adapter between the <code>LocationDaemon</code>,
 * the <code>RemoteControlDaemon</code> and the simulation package of the
 * Autopilot available at http://autopilot.sourceforge.net
 * 
 * It runs as a separate thread that receives state updates from the simulator
 * and sends control commands to the simulator via UDP/IP packages.
 * 
 * This class implements the <code>IConnection</code> interface to forward the
 * position updates as location messages to the <code>LocationDaemon</code>.
 * 
 * Additionally, this class implements the <code>IRemoteControl</code>
 * interface to receive control commands from the
 * <code>RemoteControlDaemon</code>
 * 
 * @author Clemens Krainer
 */
public class APFWSimulationAdapter extends Thread implements IConnection, IRemoteControl {

    public static final int COMMAND_NOP    = 0;
    public static final int COMMAND_OPEN   = 1;
    public static final int COMMAND_ACK    = 2;
    public static final int COMMAND_CLOSE  = 3;

    public static final int SERVO_ROLL     = 20;
    public static final int SERVO_PITCH    = 21;
    public static final int SERVO_YAW      = 22;
    public static final int SERVO_COLL     = 23;

    public static final int AHRS_STATE     = 40;
    
	/**
	 * This is the property for the message buffer length.  
	 */
	public static final String PROP_BUFFER_LENGTH = "buffer.length";
	
    public static final String PROP_APFW_SIMULATOR_HOSTNAME = "apfw.server.hostname";
    public static final String PROP_APFW_SIMULATOR_PORT = "apfw.server.port";
	public static final String PROP_TAG_ONE_ID = "tag.one.id";
	public static final String PROP_TAG_ONE_TYPE = "tag.one.type";
	public static final String PROP_TAG_TWO_ID = "tag.two.id";
	public static final String PROP_TAG_TWO_TYPE = "tag.two.type";
	public static final String PROP_TAG_DISTANCE = "tag.distance";
	public static final String PROP_TAG_ZERO_POSITION = "tag.zero.position";
	
    public static final double PI180TH = Math.PI/180;
    	
    private String apfwSimulatorHostName;
    private int apfwSimulatorPort;
    private DatagramSocket clientSocket;
	private InetAddress address;

	/**
	 * A UTC calendar. It is this calendar that performs the time estimations.
	 */
	private Calendar calendar;
	
	/**
	 * This variable contains a <b>en_US</b> schema. The simulator uses this
	 * locale for converting numbers into Strings.
	 */
	private Locale locale;

	/**
	 * The ring buffer for the location messages. The receiver simulator creates
	 * new messages and inserts them to this buffer. Whether or not some
	 * <code>LocationReceiverInputStream</code> instances read messages from
	 * this buffer, the simulator continues to insert messages into this buffer.
	 * If the buffer is full, the simulator overwrites the eldest messages with
	 * new ones.
	 */
	private byte[][] messageBuffer;

	/**
	 * The write index of the messageBuffer. 
	 */
	private int writeIndex = 0;
	
	/**
	 * The type of tag one.
	 */
	private String tagOneType;
	
	/**
	 * The identification string of tag one. 
	 */
	private String tagOneId;
	
	/**
	 * The type of tag two.
	 */
	private String tagTwoType;
	
	/**
	 * The identification string of tag two. 
	 */
	private String tagTwoId;
	
	/**
	 * The distance vector between tag one and tag two in meters. It starts at
	 * tag one and points to tag two. Defining the distance between the tags as
	 * a vector allows the <code>PositionProvider</code> deriving the vehicle
	 * orientation from it. The x coordinate corresponds to South, the y
	 * coordinate corresponds to East and the z coordinates corresponds to the
	 * nadir-zenit axis.
	 */
	private CartesianCoordinate tagDistance;
	
	/**
	 * The zero position vector in meters. It starts at tag one and points to
	 * the zero position, i.e. the position the <code>PositionProvider</code>
	 * will report to the <code>LocationMessageListener</code> objects. See
	 * the <code>tagDistance</code> variable for an explanation of the x, y
	 * and z values.
	 */
	private CartesianCoordinate tagZeroPosition;
	
	/**
	 * This variable indicates whether the service is running or not.
	 */
	boolean running = false;
	
	private NumberFormat nf_2d;
	private NumberFormat nf_3d;
	private NumberFormat nf_4d;
    private NumberFormat nf_1f4;
    
    /**
     * Construct an <code>APFWSimulationAdapter</code>.
     * 
     * @param props the properties to be used.
     * @throws ConfigurationException thrown in case of configuration errors.
     */
    public APFWSimulationAdapter (Properties props) throws ConfigurationException {
		int bufferLength = Integer.parseInt (props.getProperty (PROP_BUFFER_LENGTH,"100"));
		messageBuffer = new byte [bufferLength][];
		
		apfwSimulatorHostName = props.getProperty(PROP_APFW_SIMULATOR_HOSTNAME, "localhost");
		apfwSimulatorPort = Integer.parseInt(props.getProperty(PROP_APFW_SIMULATOR_PORT, "2002"));
		calendar = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
		locale = new Locale ("en","US");
		
		tagOneId = props.getProperty (PROP_TAG_ONE_ID);
		if (tagOneId == null || tagOneId.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_ONE_ID);

		tagOneType = props.getProperty (PROP_TAG_ONE_TYPE);
		if (tagOneType == null || tagOneType.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_ONE_TYPE);

		
		tagTwoId = props.getProperty (PROP_TAG_TWO_ID);
		if (tagTwoId == null || tagTwoId.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_TWO_ID);
			
		tagTwoType = props.getProperty (PROP_TAG_TWO_TYPE);
		if (tagTwoType == null || tagTwoType.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_TWO_TYPE);

		if (tagOneId.equals(tagTwoId))
			throw new ConfigurationException ("Properties " + PROP_TAG_ONE_ID + " and " + PROP_TAG_TWO_ID + " are equal!");
		
		
		String tagDistanceString = props.getProperty (PROP_TAG_DISTANCE);
		if (tagDistanceString == null || tagDistanceString.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_DISTANCE);
		
		String[] dist = tagDistanceString.trim ().split ("\\s*,\\s*");
		if (dist.length != 3)
			throw new ConfigurationException ("Property " + PROP_TAG_DISTANCE + " should have comma separated values for x, y and z.");
		
		double x1 = Double.parseDouble (dist[0]);
		double y1 = Double.parseDouble (dist[1]);
		double z1 = Double.parseDouble (dist[2]);
		
		tagDistance = new CartesianCoordinate (x1, y1, z1);
		
		if (tagDistance.norm() < 1E-3)
			throw new ConfigurationException ("The distance between the two tags has to be more than 1mm.");
		
		String tagZeroPositionString = props.getProperty (PROP_TAG_ZERO_POSITION);
		if (tagZeroPositionString == null || tagZeroPositionString.equals(""))
			throw new ConfigurationException ("Missing or unset property: " + PROP_TAG_ZERO_POSITION);
		
		String[] zero = tagZeroPositionString.trim ().split ("\\s*,\\s*");
		if (zero.length != 3)
			throw new ConfigurationException ("Property " + PROP_TAG_ZERO_POSITION + " should have comma separated values for x, y and z.");
		
		double x2 = Double.parseDouble (zero[0]);
		double y2 = Double.parseDouble (zero[1]);
		double z2 = Double.parseDouble (zero[2]);
		
		tagZeroPosition = new CartesianCoordinate (x2, y2, z2);
		
        nf_2d = NumberFormat.getInstance(locale);
        nf_2d.setMaximumFractionDigits(0);
        nf_2d.setMinimumFractionDigits(0);
        nf_2d.setMinimumIntegerDigits(4);
        nf_2d.setGroupingUsed(false);
        
        nf_3d = NumberFormat.getInstance(locale);
        nf_3d.setMaximumFractionDigits(0);
        nf_3d.setMinimumFractionDigits(0);
        nf_3d.setMinimumIntegerDigits(3);
        nf_3d.setGroupingUsed(false);
        
        nf_4d = NumberFormat.getInstance(locale);
        nf_4d.setMaximumFractionDigits(0);
        nf_4d.setMinimumFractionDigits(0);
        nf_4d.setMinimumIntegerDigits(4);
        nf_4d.setGroupingUsed(false);
        
        nf_1f4 = NumberFormat.getInstance(locale);
        nf_1f4.setMaximumFractionDigits(4);
        nf_1f4.setMinimumFractionDigits(4);
        nf_1f4.setMinimumIntegerDigits(1);
        nf_1f4.setGroupingUsed(false);
    }
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run () {
		
		try {
			address = InetAddress.getByName(apfwSimulatorHostName);
			clientSocket = new DatagramSocket();
		
			udp_send (clientSocket, address, apfwSimulatorPort, COMMAND_OPEN, null, 0);
			
			byte[] data = new byte[256];
			DatagramPacket packet = new DatagramPacket (data, 256);
			boolean running = true;
			APFWState state = new APFWState ();
			
			while (running) {
				clientSocket.receive(packet);
				
				long seconds = ByteArrayUtils.bytes2int (ByteArrayUtils.reverse (ByteArrayUtils.partition (data, 0, 4)));
				long microSeconds = ByteArrayUtils.bytes2int (ByteArrayUtils.reverse (ByteArrayUtils.partition (data, 4, 4)));
				int type = ByteArrayUtils.bytes2int (ByteArrayUtils.reverse (ByteArrayUtils.partition (data, 8, 4)));
				long time = seconds * 1000 + microSeconds / 1000;
				
				switch (type) {
				case AHRS_STATE: handle_ahrs_state (time, ByteArrayUtils.partition (data, 12, 146), state); break;
				case COMMAND_NOP:
				case COMMAND_OPEN:
				case COMMAND_ACK:
				case COMMAND_CLOSE: break;
				default:
					System.out.println ("testCase01, received: time=" + time + ", unknown type=" + type);
				}
			}
			
			udp_send (clientSocket, address, apfwSimulatorPort, COMMAND_CLOSE, null, 0);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void terminate () {
		running = false;
	}
	
	/**
	 * Handle the state update from the autopilot simulator.
	 * 
	 * @param time the time of the message.
	 * @param data the message data.
	 * @param state the state object to be updated.
	 * @throws IOException thrown in case of data conversion errors.
	 */
	protected void handle_ahrs_state (long time, byte[] data, APFWState state) throws IOException {
		/* Body frame linear accelerations */
		state.ax    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,   0, 8)));
		state.ay    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,   8, 8)));
		state.az    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  16, 8)));
		/* Body frame rotational rates */
		state.p     = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  24, 8)));
		state.r     = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  32, 8)));
		state.q     = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  40, 8)));
		/* Position relative to the ground */
		state.x     = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  48, 8)));
		state.y     = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  56, 8)));
		state.z     = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  64, 8)));
		/* Euler angles relative to the ground */
		state.phi   = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  72, 8)));
		state.theta = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  80, 8)));
		state.psi   = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  88, 8)));
		/* Velocity over the ground */
		state.vx    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data,  96, 8)));
		state.vy    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data, 104, 8)));
		state.vz    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data, 112, 8)));
		/* Moments on the rotor mass */
		state.mx    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data, 120, 8)));
		state.my    = ByteArrayUtils.bytes2double (ByteArrayUtils.reverse (ByteArrayUtils.partition (data, 128, 8)));
		
//		System.out.println (
//				"time=" + time +
////				"ax=" + state.ax + ", ax=" + state.ay + ", az=" + state.az + ", " +
//				"x=" + state.x + ", y=" + state.y + ", z=" + state.z + ", " +
////				"phi=" + state.phi + ", theta=" + state.theta + ", " +
//				"psi=" + state.psi
//				);
		
		Matrix3x3 r = new Matrix3x3 (0, 0, state.psi/PI180TH);
		CartesianCoordinate z = r.multiply (tagZeroPosition);
		CartesianCoordinate d = r.multiply (tagDistance);
		CartesianCoordinate pos = new CartesianCoordinate (state.x, state.y, -state.z);
		CartesianCoordinate tagOnePos = pos.subtract (z);
		CartesianCoordinate tagTwoPos = tagOnePos.add (d);
		
//		System.out.println ("x=" + state.x + ", y=" + state.y + ", z=" + state.z + ", psi=" + state.psi +
//				", pos=" + pos + ", tagOnePos=" + tagOnePos + ", tagTwoPos=" + tagTwoPos
//				);
				
		messageBuffer [writeIndex] = createLocPnqMessage (time, tagOneType, tagOneId, tagOnePos);
		incrementWriteIndex ();
		messageBuffer [writeIndex] = createLocPnqMessage (time, tagTwoType, tagTwoId, tagTwoPos);
		incrementWriteIndex ();
	}

	/**
	 * Increment the writeIndex of the messageBuffer.
	 */
	private void incrementWriteIndex () {
		
		if (writeIndex+1 >= messageBuffer.length)
			writeIndex = 0;
		else
			++writeIndex;		
	}
	
	/**
	 * Calculate the NMEA 0183 checksum of a given message and append the
	 * checksum to the message.
	 * 
	 * @param msg the message as a StringBuffer.
	 * @return the message with the appended checksum as a byte array.
	 */
	private byte[] calculateAndAppendChecksum (StringBuffer msg) {
		
		char[] cs = calculateChecksum (msg.toString ().getBytes ());
		msg.append (cs);
		msg.append ("\r\n");
		return msg.toString ().getBytes ();
	}
	
	/**
	 * Create a location message for one tag.
	 * e.g. $LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:24:49.334077000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*43
	 * 
	 * @return the message as an array of bytes.
	 */
	public byte[] createLocPnqMessage (long time, String tagType, String tagId, CartesianCoordinate pos) {
		
		calendar.setTimeInMillis(time);
		
//		Object[] args = new Object [7];
//		args[0] = new Integer (calendar.get (Calendar.YEAR));
//		args[1] = new Integer (calendar.get (Calendar.MONTH) + 1);
//		args[2] = new Integer (calendar.get (Calendar.SECOND));
//		args[3] = new Integer (calendar.get (Calendar.HOUR_OF_DAY));
//		args[4] = new Integer (calendar.get (Calendar.MINUTE));
//		args[5] = new Integer (calendar.get (Calendar.SECOND));
//		args[6] = new Integer (calendar.get (Calendar.MILLISECOND));		
//		String timeString = String.format ("%04d-%02d-%02d %02d:%02d:%02d.%03d", args);
		
		String timeString =
			nf_4d.format(calendar.get (Calendar.YEAR)) + "-" +
			nf_2d.format(calendar.get (Calendar.MONTH) + 1) + "-" +
			nf_2d.format(calendar.get (Calendar.SECOND)) + "@" +
			nf_2d.format(calendar.get (Calendar.HOUR_OF_DAY)) + ":" +
			nf_2d.format(calendar.get (Calendar.MINUTE)) + ":" +
			nf_2d.format(calendar.get (Calendar.SECOND)) + "." +
			nf_3d.format(calendar.get (Calendar.MILLISECOND));		
		timeString = timeString.replace(' ', '0').replace('@', ' ');
		
		String stdErr = "1.2";
		String valid = "1";
//		String xString = String.format (locale, "%1.4f", new Object[] {new Double (pos.x)});
//		String yString = String.format (locale, "%1.4f", new Object[] {new Double (pos.y)});
//		String zString = String.format (locale, "%1.4f", new Object[] {new Double (pos.z)});
//		String aString = String.format (locale, "%1.4f", new Object[] {new Double (0)});
		String xString = nf_1f4.format(pos.x).replace(' ', '0');
		String yString = nf_1f4.format(pos.y).replace(' ', '0');
		String zString = nf_1f4.format(pos.z).replace(' ', '0');
		String aString = nf_1f4.format(0).replace(' ', '0');
		String bString = aString;
		String cString = aString;
		String dString = aString;
		
		
		StringBuffer msg = new StringBuffer ("$LOCPNQ,");
		msg.append (tagType).append (",");
		msg.append (tagId).append (",");
		msg.append (timeString).append (",");
		msg.append (stdErr).append (",");
		msg.append (valid).append (",");
		msg.append (xString).append (",");
		msg.append (yString).append (",");
		msg.append (zString).append (",");
		msg.append (aString).append (",");
		msg.append (bString).append (",");
		msg.append (cString).append (",");
		msg.append (dString);

		return calculateAndAppendChecksum (msg);
	}
	
	/**
	 * This method calculates the checksum to a given location message.
	 * 
	 * @param message the message.
	 * @return the checksum of the message.
	 */
	public static char[] calculateChecksum (byte[] message) {
		
		int checkSum = 0;
		int k = 0;
		
		while (++k < message.length)
			checkSum ^= message[k];

		int checkSumHigh = 48 + checkSum/16;
		if (checkSumHigh > 57) checkSumHigh += 7;
		
		int checkSumLow = 48 + checkSum%16;
		if (checkSumLow > 57) checkSumLow += 7;
		
		char[] cs = new char[3];
		cs[0] = '*';
		cs[1] = (char)checkSumHigh;
		cs[2] = (char)checkSumLow;
		return cs;
	}
	
	/**
	 * Send a message via a given UDP/IP socket to a distinct server on a
	 * distinct port. This method determines the current system time and invokes
	 * the <code>udp_send_raw</code>
	 * 
	 * @param socket
	 *            the <code>DatagramSocket</code> to be used for sending.
	 * @param address
	 *            the <code>InetAddress</code> of the destination host.
	 * @param port
	 *            the port number of the destination service.
	 * @param type
	 *            the message type.
	 * @param msg
	 *            the message as a byte array.
	 * @param msgLength
	 *            the number of bytes to be taken from the byte array.
	 * @throws IOException
	 *             thrown in case of transmission or conversion errors.
	 * @see at.uni_salzburg.cs.ckgroup.simulation.APFWSimulationAdapter#udp_send_raw(DatagramSocket, InetAddress, int, int, long, byte[], int)
	 */
	public void udp_send (DatagramSocket socket, InetAddress address, int port, int type, byte[] msg, int msgLength) throws IOException {
		udp_send_raw (socket, address, port, type, System.currentTimeMillis(), msg, msgLength);
	}
	
	/**
	 * Send a message via a given UDP/IP socket to a distinct server on a
	 * distinct port.
	 * 
	 * @param socket
	 *            the <code>DatagramSocket</code> to be used for sending.
	 * @param address
	 *            the <code>InetAddress</code> of the destination host.
	 * @param port
	 *            the port number of the destination service.
	 * @param type
	 *            the message type.
	 * @param time
	 *            the time of this message in milliseconds
	 * @param msg
	 *            the message as a byte array.
	 * @param msgLength
	 *            the number of bytes to be taken from the byte array.
	 * @throws IOException
	 *             thrown in case of transmission or conversion errors.
	 * @see at.uni_salzburg.cs.ckgroup.simulation.APFWSimulationAdapter#udp_send(DatagramSocket, InetAddress, int, int, byte[], int)
	 */
	public void udp_send_raw (DatagramSocket socket, InetAddress address, int port, int type, long time, byte[] msg, int msgLength) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream ();
		int seconds = (int) (time / 1000);
		int microSeconds = (int) (1000*(time - 1000*seconds));

		bOut.write (ByteArrayUtils.reverse (ByteArrayUtils.int2bytes (seconds)));
		bOut.write (ByteArrayUtils.reverse (ByteArrayUtils.int2bytes (microSeconds)));
		bOut.write (ByteArrayUtils.reverse (ByteArrayUtils.int2bytes (type)));
		if (msg != null && msgLength > 0)
			bOut.write (msg, 0, msgLength);
		
		byte[] buf = bOut.toByteArray();
		DatagramPacket packet = new DatagramPacket (buf, buf.length, address, port);
		socket.send (packet);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setPitch(double)
	 */
	public void setPitch(double pitch) throws IOException {
		byte[] data = ByteArrayUtils.reverse (ByteArrayUtils.double2bytes (pitch*PI180TH));
		udp_send (clientSocket, address, apfwSimulatorPort, SERVO_PITCH, data, data.length);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setRoll(double)
	 */
	public void setRoll(double roll) throws IOException {
		byte[] data = ByteArrayUtils.reverse (ByteArrayUtils.double2bytes (roll*PI180TH));
		udp_send (clientSocket, address, apfwSimulatorPort, SERVO_ROLL, data, data.length);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setYaw(double)
	 */
	public void setYaw(double yaw) throws IOException {
		byte[] data = ByteArrayUtils.reverse (ByteArrayUtils.double2bytes (yaw*PI180TH));
		udp_send (clientSocket, address, apfwSimulatorPort, SERVO_YAW, data, data.length);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setThrust(double)
	 */
	public void setThrust(double thrust) throws IOException {
		byte[] data = ByteArrayUtils.reverse (ByteArrayUtils.double2bytes (thrust));
		udp_send (clientSocket, address, apfwSimulatorPort, SERVO_COLL, data, data.length);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close() throws IOException {
		// Intentionally empty
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new LocationReceiverInputStream ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return new LocationReceiverOutputStream ();
	}

	/**
	 * The state data of the autopilot simulator.
	 * 
	 * @author Clemens Krainer
	 */
	private class APFWState {
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

	/**
	 * This subclass implements the OutputStream of the location receiver
	 * simulator. Actually it is a dummy that does nothing.
	 * 
	 * @author Clemens Krainer
	 */
	private class LocationReceiverOutputStream extends OutputStream {

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write (int b) throws IOException {
			// Intentionally empty.			
		}		
	}
	
	/**
	 * This subclass implements the InputStream of the location reciever
	 * simulator.
	 * 
	 * @author Clemens Krainer
	 */
	private class LocationReceiverInputStream extends InputStream {
		
		/**
		 * This variable indicates the index of the next message to get from the
		 * messageBuffer.
		 */
		private int readIndex = 0;
		
		/**
		 * This variable is an index in the currentLine to the next byte to be
		 * read.
		 */
		private int lineIndex = 0;
		
		/**
		 * The current line taken from the messageBuffer.
		 */
		private byte [] currentLine = null;
		
		/**
		 * This method gets new messages from the global messageBuffer and
		 * returns it byte wise to the caller.
		 * 
		 * @see java.io.InputStream#read()
		 */
		public int read () throws IOException {
			
			if (currentLine != null && lineIndex >= currentLine.length) {
				lineIndex = 0;
				currentLine = null;
			}

			if (currentLine == null) {

				int counter = 0;
				while (readIndex == writeIndex) {
					Thread.yield ();
					if (counter++ > 3)
						try { Thread.sleep (100); } catch (Exception e) {}
				}
				
				currentLine = messageBuffer[readIndex];

				if (++readIndex >= messageBuffer.length)
					readIndex = 0;
			}
			
			return currentLine[lineIndex++];
		}
	}



}
