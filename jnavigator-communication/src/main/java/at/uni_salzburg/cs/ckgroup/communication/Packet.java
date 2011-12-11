/*
 * @(#) Packet.java
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
package at.uni_salzburg.cs.ckgroup.communication;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a packet in our system that is employed as the data transfer
 * vehicle between the JAviator hardware, the java controller and the ground
 * station. Each packet in our system looks as follows:
 * 
 * <pre>
 *          +------+------+------+------+---...---+------+------+ 
 *          | 0xFF | 0xFF | type | size | payload |  checksum   | 
 *          +------+------+------+------+---...---+------+------+ 
 *    byte -2     -1      0      1      2     (size+2)      (size+4)
 *</pre>
 * 
 * @author Clemens Krainer
 */
public class Packet {
	
	/**
	 * The <code>Packet</code> header.
	 */
	static final byte[] HEADER = { (byte) 0xFF, (byte) 0xFF };
	
	/**
	 * The length of the packet checksum.
	 */
	static final int CHECKSUM_LENGTH = 2;

	/**
	 * The type of the packet. 
	 */
	private byte type;
	
	/**
	 * The payload of the package.
	 */
	private byte[] payload;

	/**
	 * generates a packet of type type using payload. the size is set to
	 * payload.length
	 * 
	 * @param type
	 *            the type of the new packet
	 * @param payload
	 *            the byte array used as payload
	 * @throws IOException 
	 */
	public Packet (byte type, byte[] payload) throws IOException {
		this.type = type;
		this.payload = payload;
		if (payload != null && payload.length > 255)
			throw new IOException("Message too long. Only lengths up to 127 bytes are supported. Requested lenght is " + payload.length);
	}
	
	/**
	 * Construct a <code>Packet</code> from an <code>InputStream</code>.
	 * 
	 * @param in the <code>InputStream</code>
	 * @throws IOException thrown in case of IO errors, a premature end of the <code>InputStream</code> or checksum errors.
	 */
	public Packet (InputStream in) throws IOException {
		
		synchronize (in);

		int ch;
		ch = in.read();
		if (ch < 0)
			throw new IOException ("Premature end of InputStream at reading the type.");
		
		type = (byte) ch;
		
		int size = in.read();
		if (size < 0)
			throw new IOException ("Premature end of InputStream at reading the payload size.");

		payload = new byte[size];
		for (int k=0; k < size; k++) {
			ch = in.read();
			if (ch < 0)
				throw new IOException ("Premature end of InputStream at reading the payload. type=" + type + ", size=" + size + ", read=" + k);
			payload[k] = (byte) ch;
		}
		
		byte[] sentCheckSum = new byte[CHECKSUM_LENGTH];
		for (int k=0; k < CHECKSUM_LENGTH; k++) {
			ch = in.read();
			if (ch < 0)
				throw new IOException ("Premature end of InputStream at reading the checksum.");
			sentCheckSum[k] = (byte) ch;
		}
	
		byte[] checkSum = calculateCheckSum (type, payload);
		for (int k=0; k < checkSum.length; k++)
			if (sentCheckSum[k] != checkSum[k])
				throw new IOException ("Checksum Error.");
	}
	
	/**
	 * Synchronize to the <code>InputStream</code>.
	 * 
	 * @param in the given <code>InputStream</code>
	 * @throws IOException thrown in case of a premature end of the <code>InputStream</code>.
	 */
	private void synchronize (InputStream in) throws IOException {

		int ch;
		int k = 0;
		
		while (k < HEADER.length) {
			ch = in.read();
			if (ch < 0)
				throw new IOException ("Premature end of InputStream at reading the packet header.");
			if (HEADER[k] == (byte)ch)
				k++;
			else {
				k=0;
				System.out.println ("Packet.synchronize(): k=" + HEADER[k] + ", char=" + ch);
			}
		}
	}

	/**
	 * Calculate the checksum of a given packet.
	 * 
	 * @param type the packet type
	 * @param data the packet's payload
	 * @return the calculated checksum
	 */
	public static byte[] calculateCheckSum (byte type, byte[] data) {
		int sum = type;
		
		if (data != null) {
			sum += data.length;
			for (int i = 0; i < data.length; ++i)
				sum += ((int) data[i]) & 0xFF;
		}

		return new byte[] { (byte) ((sum >> 8) & 0xFF), (byte)(sum & 0xFF)};
	}

	/**
	 * Convert the packet to an array of bytes
	 * 
	 * @return the packet as an array of bytes.
	 */
	public byte[] toByteArray() {
		byte[] packet = new byte[HEADER.length + 2 + (payload!=null?payload.length:0) + CHECKSUM_LENGTH];
		
		int i=0;
		for (int k=0; k < HEADER.length; k++)
			packet[i++] = HEADER[k];
		
		packet[i++] = type;
		packet[i++] = (byte) (payload!=null?payload.length:0);
			
		if (payload != null)
			for (int k=0; k < payload.length; k++)
				packet[i++] = payload[k];
		
		byte[] checkSum = calculateCheckSum (type, payload);
		for (int k=0; k < checkSum.length; k++)
			packet[i++] = checkSum[k];
		
		return packet;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		byte[] p = toByteArray();
		StringBuffer b = new StringBuffer ();
		b.append("[");
		for (int k=0; k < p.length; k++) {
			if (k != 0)
				b.append(",");
			b.append((p[k]+256)&0xFF);
		}
		b.append("]");
		return b.toString();
	}
	
	/**
	 * @return the type of the packet as a byte.
	 */
	public byte getType () {
		return type;
	}
	
	/**
	 * @return the payload as an array of bytes.
	 */
	public byte[] getPayload () {
		return payload;
	}
}
