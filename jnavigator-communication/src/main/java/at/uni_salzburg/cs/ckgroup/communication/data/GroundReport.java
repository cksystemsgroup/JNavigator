/*
 * @(#) GroundReport.java
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

import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;

public class GroundReport implements IDataTransferObject {

	private SensorData sensorData;
	private MotorSignals motorSignals;
	private MotorOffsets motorOffsets;
	private byte state;
	private byte mode;
	
	
	/**
	 * Construct an empty <code>GroundReport</code> data transfer object.
	 */
	public GroundReport () {
		// Intentionally empty.
	}
	
	/**
	 * Construct a <code>GroundReport</code> data transfer object from a byte array.
	 * 
	 * @param data the byte array
	 * @throws CommunicationException thrown in case of an incorrect length of the provided data.
	 */
	public GroundReport (byte[] data) throws CommunicationException {

		int k = 0;
		sensorData = new SensorData(data);
		k += SensorData.payloadLength;
		motorSignals = new MotorSignals(data, k);
		k += MotorSignals.payloadLength;
		motorOffsets = new MotorOffsets(data, k);
		k += MotorOffsets.payloadLength;
		state = data[k++];
		mode = data[k];
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray() {
		
		byte[] sdChunk = sensorData.toByteArray();
		byte[] msChunk = motorSignals.toByteArray();
		byte[] moChunk = motorOffsets.toByteArray();
		
		byte[] b = new byte[sdChunk.length + msChunk.length + moChunk.length + 2];

		int k=0;
		for (int i=0; i < sdChunk.length; i++)
			b[k++] = sdChunk[i];
		
		for (int i=0; i < msChunk.length; i++)
			b[k++] = msChunk[i];
		
		for (int i=0; i < moChunk.length; i++)
			b[k++] = moChunk[i];
		
		b[k++] = state;
		b[k++] = mode;
		
		return b;
	}

	public SensorData getSensorData() {
		return sensorData;
	}

	public void setSensorData(SensorData sensorData) {
		this.sensorData = sensorData;
	}

	public MotorSignals getMotorSignals() {
		return motorSignals;
	}

	public void setMotorSignals(MotorSignals motorSignals) {
		this.motorSignals = motorSignals;
	}

	public MotorOffsets getMotorOffsets() {
		return motorOffsets;
	}

	public void setMotorOffsets(MotorOffsets motorOffsets) {
		this.motorOffsets = motorOffsets;
	}

	public FlyingState getState() {
		return FlyingState.values()[state];
	}

	public void setState(FlyingState state) {
		this.state = (byte)state.ordinal();
	}

	public FlyingMode getMode() {
		return FlyingMode.values()[mode];
	}

	public void setMode(FlyingMode mode) {
		this.mode = (byte)mode.ordinal();
	}
	
}
