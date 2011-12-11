/*
 * @(#) JaviatorData.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2011  Clemens Krainer
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
package at.uni_salzburg.cs.ckgroup.communication.data;

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;

public class JaviatorData implements IDataTransferObject {
	
	
	public static final double FACTOR_EULER_ANGLE	= 2.0*Math.PI/65536.0;		/* [units] --> [rad] (2*PI* rad/2^16) */
	public static final double FACTOR_ANGULAR_VEL	= 8.5/32768.0;				/* [units] --> [rad/s] */
	public static final double FACTOR_ANGULAR_ACC	= 8.5/32768.0*76.3;			/* [units] --> [rad/s^2] */
	public static final double FACTOR_LINEAR_ACC	= 9.810/4681.0;				/* [units] --> [m/s^2] (4681=32768000/7000) */
	public static final double FACTOR_BMU_MAPS		= 115000000.0/16777216.0;	/* [0-5V]  --> [115000000mPa] */
	public static final double FACTOR_BMU_TEMP		= 100.0/4096.0;				/* [0-1V]  --> [0-100°C] */
	public static final double FACTOR_BMU_BATT		= 18.0/1024.0;				/* [0-5V]  --> [0-18V] */
	public static final double FACTOR_SONAR			= 3/1024.0;					/* [0-5V]  --> [0-3m] */
//	public static final double FACTOR_PARAMETER		= 0.001;					/* [mrad]  --> [rad] */
	public static final double OFFSET_TEMPERATURE	= 0.7;                      /* [°C] temperature calibration */
	
	public static final short ST_NEW_DATA_IMU		= 0x01;		/* new data from IMU available */
	public static final short ST_NEW_DATA_BMU		= 0x02;		/* new data from BMU available */
	public static final short ST_NEW_DATA_SONAR		= 0x04;		/* new data from SONAR available */
	public static final short ST_NEW_DATA_POS_X		= 0x08;		/* new data from Laser position available */
	public static final short ST_NEW_DATA_POS_Y		= 0x10;		/* new data from Laser position available */

	
	/**
	 * Convert the binary representation of angles to degrees.
	 */
//	public static final double ANGLE_FACTOR = Math.PI/0.18; // 6.283 / 65536;
	
	/**
	 * The scaling factor of the angle values.
	 */
//	public static final double DEGREES_TO_MILLIRADIANTS = Math.PI/0.18;

	/**
	 * The scaling factor of the altitude values.
	 */
//	public static final double METERS_TO_MILLIMETERS =;
	
	long maps;		/* [mV] 24-bit resolution */
	int temp;		/* [mV] 10-bit resolution */
	int batt;		/* [mV] 10-bit resolution */
	int sonar;		/* [mV] 10-bit resolution */
	int state;		/* JAviator data state */
	int id;			/* transmisson ID */
	byte[] x_pos = new byte[8];	/* [mm] range: 0...500000 */
	byte[] y_pos = new byte[8];	/* [mm] range: 0...500000 */
	short roll;		/* | roll | */
	short pitch;	/* [units] --> [mrad] = | pitch | * 2000 * PI / 65536 */
	short yaw;		/* | yaw | */
	short droll;	/* | droll | */
	short dpitch;	/* [units] --> [mrad/s] = | dpitch | * 8500 / 32768 */
	short dyaw;		/* | dyaw | */
	short ddx;		/* | ddx | */
	short ddy;		/* [units] --> [mm/s^2] = | ddy | * 9810 * 7 / 32768 */
	short ddz;		/* | ddz | */
    
	/**
     * The length of the payload in bytes.
     */
    private static final int payloadLength = 48;

	/**
	 * Construct an empty <code>JaviatorData</code> data transfer object.
	 */
	public JaviatorData () {
		// Intentionally empty
	}
	
    /**
     * Construct an <code>JaviatorData</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public JaviatorData (byte[] data) {
    	int k = 0;

    	maps =	(long) (((data[k++] & 0xFF) << 24) | ((data[k++] & 0xFF) << 16) | ((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	temp =	(short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	batt =	(short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	sonar =	(short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	state =	(short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	id =	(short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	
    	x_pos[0] = data[k++];	x_pos[1] = data[k++];
    	x_pos[2] = data[k++];	x_pos[3] = data[k++];
    	x_pos[4] = data[k++];	x_pos[5] = data[k++];
    	x_pos[6] = data[k++];	x_pos[7] = data[k++];
    	
    	y_pos[0] = data[k++];	y_pos[1] = data[k++];
    	y_pos[2] = data[k++];	y_pos[3] = data[k++];
    	y_pos[4] = data[k++];	y_pos[5] = data[k++];
    	y_pos[6] = data[k++];	y_pos[7] = data[k++];
    	
    	roll =  (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	pitch = (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	yaw  =  (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	droll =  (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	dpitch = (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	dyaw  =  (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	ddx =  (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	ddy = (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    	ddz  =  (short) (((data[k++] & 0xFF) << 8) | (data[k++] & 0xFF));
    }
    
	public byte[] toByteArray() {
		
		byte[] data = new byte[payloadLength];
		
		int k=0;
		data[k++] = (byte) ((maps >> 24) & 0xFF);
		data[k++] = (byte) ((maps >> 16) & 0xFF);
		data[k++] = (byte) ((maps >> 8) & 0xFF);
		data[k++] = (byte) (maps & 0xFF);
		data[k++] = (byte) ((temp >> 8) & 0xFF);
		data[k++] = (byte) (temp & 0xFF);
		data[k++] = (byte) ((batt >> 8) & 0xFF);
		data[k++] = (byte) (batt & 0xFF);
		data[k++] = (byte) ((sonar >> 8) & 0xFF);
		data[k++] = (byte) (sonar & 0xFF);
		data[k++] = (byte) ((state >> 8) & 0xFF);
		data[k++] = (byte) (state & 0xFF);
		data[k++] = (byte) ((id >> 8) & 0xFF);
		data[k++] = (byte) (id & 0xFF);
		data[k++] = x_pos[0];	data[k++] = x_pos[1];	data[k++] = x_pos[2];	data[k++] = x_pos[3];
		data[k++] = x_pos[4];	data[k++] = x_pos[5];	data[k++] = x_pos[6];	data[k++] = x_pos[7];
		data[k++] = y_pos[0];	data[k++] = y_pos[1];	data[k++] = y_pos[2];	data[k++] = y_pos[3];
		data[k++] = y_pos[4];	data[k++] = y_pos[5];	data[k++] = y_pos[6];	data[k++] = y_pos[7];		
		data[k++] = (byte) (roll >> 8);
		data[k++] = (byte) (roll & 0xFF);
		data[k++] = (byte) (pitch >> 8);
		data[k++] = (byte) (pitch & 0xFF);
		data[k++] = (byte) (yaw >> 8);
		data[k++] = (byte) (yaw & 0xFF);
		data[k++] = (byte) (droll >> 8);
		data[k++] = (byte) (droll & 0xFF);
		data[k++] = (byte) (dpitch >> 8);
		data[k++] = (byte) (dpitch & 0xFF);
		data[k++] = (byte) (dyaw >> 8);
		data[k++] = (byte) (dyaw & 0xFF);
		data[k++] = (byte) (ddx >> 8);
		data[k++] = (byte) (ddx & 0xFF);
		data[k++] = (byte) (ddy >> 8);
		data[k++] = (byte) (ddy & 0xFF);
		data[k++] = (byte) (ddz >> 8);
		data[k++] = (byte) (ddz & 0xFF);
		return data;
	}

	public double getMaps() {
		return maps * FACTOR_BMU_MAPS;
	}

	public double getTemp() {
		return temp * FACTOR_BMU_TEMP - OFFSET_TEMPERATURE;
	}

	public double getBatt() {
		return batt * FACTOR_BMU_BATT;
	}

	public double getSonar() {
		return sonar * FACTOR_SONAR;
	}

	public int getState() {
		return state;
	}

	public int getId() {
		return id;
	}

	public double getX_pos() {
		double pos = 0;
		for (int k=0; k < x_pos.length; k++) {
			pos *= 10;
			pos += x_pos[k] - '0';
		}
		return pos / 1000.0;
	}

	public double getY_pos() {
		double pos = 0;
		for (int k=0; k < y_pos.length; k++) {
			pos *= 10;
			pos += y_pos[k] - '0';
		}
		return pos / 1000.0;
	}

	public double getRoll() {
		return roll * FACTOR_EULER_ANGLE;
	}

	public double getPitch() {
		return pitch * FACTOR_EULER_ANGLE;
	}

	public double getYaw() {
		return yaw * FACTOR_EULER_ANGLE;
	}

	public double getDroll() {
		return droll * FACTOR_ANGULAR_VEL;
	}

	public double getDpitch() {
		return dpitch * FACTOR_ANGULAR_VEL;
	}

	public double getDyaw() {
		return dyaw * FACTOR_ANGULAR_VEL;
	}

	public double getDdx() {
		return ddx * FACTOR_LINEAR_ACC;
	}

	public double getDdy() {
		return ddy * FACTOR_LINEAR_ACC;
	}

	public double getDdz() {
		return ddz * FACTOR_LINEAR_ACC;
	}

	public void setMaps(double maps) {
		this.maps = (long)(maps / FACTOR_BMU_MAPS);
	}

	public void setTemp(double temp) {
		this.temp = (short)((temp + OFFSET_TEMPERATURE) / FACTOR_BMU_TEMP);
	}
	
	public void setBatt(double batt) {
		this.batt = (short)(batt / FACTOR_BMU_BATT);
	}

	public void setSonar(double sonar) {
		this.sonar = (short)(sonar / FACTOR_SONAR);
	}

	public void setState(int state) {
		this.state = state;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setX_pos(double xPos) {
		long pos = (long)(xPos*1000);
		for (int k=x_pos.length-1; k >= 0; k--) {
			x_pos[k] = (byte)(pos % 10 + '0');
			pos /= 10;
		}
	}

	public void setY_pos(double yPos) {
		long pos = (long)(yPos*1000);
		for (int k=y_pos.length-1; k >= 0; k--) {
			y_pos[k] = (byte)(pos % 10 + '0');
			pos /= 10;
		}
	}

	public void setRoll(double roll) {
		this.roll = (short)(roll / FACTOR_EULER_ANGLE);
	}

	public void setPitch(double pitch) {
		this.pitch = (short)(pitch / FACTOR_EULER_ANGLE);
	}

	public void setYaw(double yaw) {
		this.yaw = (short)(yaw / FACTOR_EULER_ANGLE);
	}

	public void setDroll(double droll) {
		this.droll = (short)(droll / FACTOR_ANGULAR_VEL);
	}

	public void setDpitch(double dpitch) {
		this.dpitch = (short)(dpitch / FACTOR_ANGULAR_VEL);
	}

	public void setDyaw(double dyaw) {
		this.dyaw = (short)(dyaw / FACTOR_ANGULAR_VEL);
	}

	public void setDdx(double ddx) {
		this.ddx = (short)(ddx / FACTOR_LINEAR_ACC);
	}

	public void setDdy(double ddy) {
		this.ddy = (short)(ddy / FACTOR_LINEAR_ACC);
	}

	public void setDdz(double ddz) {
		this.ddz = (short)(ddz / FACTOR_LINEAR_ACC);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append("JaviatorData: maps=").append(maps);
		buf.append(", temp=").append(temp);
		buf.append(", batt=").append(batt);
		buf.append(", sonar=").append(sonar).append(" (").append(getSonar()).append(")");
		buf.append(", state=").append(state);
		buf.append(", id=").append(id);
		buf.append(", x_pos=");
		for (int k=0; k < x_pos.length; k++)
			buf.append((char)x_pos[k]);
		buf.append(", y_pos=");
		for (int k=0; k < y_pos.length; k++)
			buf.append((char)y_pos[k]);
		buf.append(", roll=").append(roll).append(" (").append(getRoll()).append(")");
		buf.append(", pitch=").append(pitch);
		buf.append(", yaw=").append(yaw);
		buf.append(", droll=").append(droll);
		buf.append(", dpitch=").append(dpitch);
		buf.append(", dyaw=").append(dyaw);
		buf.append(", ddx=").append(ddx);
		buf.append(", ddy=").append(ddy);
		buf.append(", ddz=").append(ddz);
		return buf.toString();
	}
}
