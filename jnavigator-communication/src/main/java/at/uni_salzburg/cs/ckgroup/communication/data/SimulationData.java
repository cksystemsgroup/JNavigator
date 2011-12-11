package at.uni_salzburg.cs.ckgroup.communication.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;

public class SimulationData implements IDataTransferObject {
	
	/**
	 * The current roll value.
	 */
	private double roll = 0;

	/**
	 * The current pitch value.
	 */
	private double pitch = 0;

	/**
	 * The current yaw value.
	 */
	private double yaw = 0;

	/**
	 * The first derivative of the current roll value.
	 */
	private double dRoll = 0;

	/**
	 * The first derivative of the current pitch value.
	 */
	private double dPitch = 0;

	/**
	 * The first derivative of the current yaw value.
	 */
	private double dYaw = 0;

	/**
	 * The second derivative of the current roll value.
	 */
	private double ddRoll = 0;

	/**
	 * The second derivative of the current pitch value.
	 */
	private double ddPitch = 0;

	/**
	 * The second derivative of the current yaw value.
	 */
	private double ddYaw = 0;

	/**
	 * The current X value.
	 */
	private double x = 0;

	/**
	 * The current Y value.
	 */
	private double y = 0;

	/**
	 * The current Z value.
	 */
	private double z = 0;

	/**
	 * The first derivative of the current X value.
	 */
	private double dx = 0;

	/**
	 * The first derivative of the current Y value.
	 */
	private double dy = 0;

	/**
	 * The first derivative of the current Z value.
	 */
	private double dz = 0;

	/**
	 * The second derivative of the current X value.
	 */
	private double ddx = 0;

	/**
	 * The second derivative of the current Y value.
	 */
	private double ddy = 0;

	/**
	 * The second derivative of the current Z value.
	 */
	private double ddz = 0;
	
	/**
	 * The length of this data transfer object in bytes when converted to a byte array.
	 */
	static final int payloadLength = 144;
	
	/**
	 * Construct an empty <code>SimulationData</code> data transfer object.
	 */
	public SimulationData () {
		// Intentionally empty
	}
	
	/**
	 * Construct a <code>SimulationData</code> data transfer object from a byte array.
	 * 
	 * @param data the byte array
	 * @throws CommunicationException thrown in case of an incorrect length of the provided data.
	 */
	public SimulationData (byte[] data) throws CommunicationException {
		if (payloadLength != data.length)
			throw new CommunicationException ("Input data length of " + data.length +
					" is not equal to the expected length of " + payloadLength + " bytes");
		
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
		try {
			roll = dis.readDouble();
			pitch = dis.readDouble();
			yaw = dis.readDouble();
			dRoll = dis.readDouble();
			dPitch = dis.readDouble();
			dYaw = dis.readDouble();
			ddRoll = dis.readDouble();
			ddPitch = dis.readDouble();
			ddYaw = dis.readDouble();
			x = dis.readDouble();
			y = dis.readDouble();
			z = dis.readDouble();
			dx = dis.readDouble();
			dy = dis.readDouble();
			dz = dis.readDouble();
			ddx = dis.readDouble();
			ddy = dis.readDouble();
			ddz = dis.readDouble();
		} catch (IOException e) {
			throw new CommunicationException ("At reading message", e);
		}
	}
	
	public byte[] toByteArray() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		try {
			dos.writeDouble(roll);
			dos.writeDouble(pitch);
			dos.writeDouble(yaw);
			dos.writeDouble(dRoll);
			dos.writeDouble(dPitch);
			dos.writeDouble(dYaw);
			dos.writeDouble(ddRoll);
			dos.writeDouble(ddPitch);
			dos.writeDouble(ddYaw);
			dos.writeDouble(x);
			dos.writeDouble(y);
			dos.writeDouble(z);
			dos.writeDouble(dx);
			dos.writeDouble(dy);
			dos.writeDouble(dz);
			dos.writeDouble(ddx);
			dos.writeDouble(ddy);
			dos.writeDouble(ddz);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append("SimulationData roll=").append(roll);
		buf.append(", pitch=").append(pitch);
		buf.append(", yaw=").append(yaw);
		buf.append(", dRoll=").append(dRoll);
		buf.append(", dPitch=").append(dPitch);
		buf.append(", dYaw=").append(dYaw);
		buf.append(", ddRoll=").append(ddRoll);
		buf.append(", ddPitch=").append(ddPitch);
		buf.append(", ddYaw=").append(ddYaw);
		buf.append(", x=").append(x);
		buf.append(", y=").append(y);
		buf.append(", z=").append(z);
		buf.append(", dx=").append(dx);
		buf.append(", dy=").append(dy);
		buf.append(", dz=").append(dz);
		buf.append(", ddx=").append(ddx);
		buf.append(", ddy=").append(ddy);
		buf.append(", ddz=").append(ddz);
		return buf.toString();
	}

	public double getRoll() {
		return roll;
	}

	public void setRoll(double roll) {
		this.roll = roll;
	}

	public double getPitch() {
		return pitch;
	}

	public void setPitch(double pitch) {
		this.pitch = pitch;
	}

	public double getYaw() {
		return yaw;
	}

	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	public double getdRoll() {
		return dRoll;
	}

	public void setdRoll(double dRoll) {
		this.dRoll = dRoll;
	}

	public double getdPitch() {
		return dPitch;
	}

	public void setdPitch(double dPitch) {
		this.dPitch = dPitch;
	}

	public double getdYaw() {
		return dYaw;
	}

	public void setdYaw(double dYaw) {
		this.dYaw = dYaw;
	}

	public double getDdRoll() {
		return ddRoll;
	}

	public void setDdRoll(double ddRoll) {
		this.ddRoll = ddRoll;
	}

	public double getDdPitch() {
		return ddPitch;
	}

	public void setDdPitch(double ddPitch) {
		this.ddPitch = ddPitch;
	}

	public double getDdYaw() {
		return ddYaw;
	}

	public void setDdYaw(double ddYaw) {
		this.ddYaw = ddYaw;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double getDx() {
		return dx;
	}

	public void setDx(double dx) {
		this.dx = dx;
	}

	public double getDy() {
		return dy;
	}

	public void setDy(double dy) {
		this.dy = dy;
	}

	public double getDz() {
		return dz;
	}

	public void setDz(double dz) {
		this.dz = dz;
	}

	public double getDdx() {
		return ddx;
	}

	public void setDdx(double ddx) {
		this.ddx = ddx;
	}

	public double getDdy() {
		return ddy;
	}

	public void setDdy(double ddy) {
		this.ddy = ddy;
	}

	public double getDdz() {
		return ddz;
	}

	public void setDdz(double ddz) {
		this.ddz = ddz;
	}
}
