/*
 * @(#) PacketSender.java
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
package javiator.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class receives <code>Packet</code> objects and writes them byte by byte
 * to a given <code>OutputStream</code>. It employs an internal ring buffer to
 * avoid blocking the sender of <code>Packet</code> objects.
 * 
 * @author Clemens Krainer
 */
public class PacketSender extends Thread implements IPacketListener {

	private OutputStream outputStream;
	private int readIndex = 0;
	private int writeIndex = 0;
	private Packet[] packetRingBuffer = new Packet[10];
	private boolean running = true;
	
	/**
	 * Construct a <code>PacketSender</code>.
	 * 
	 * @param outputStream
	 *            the <code>OutputStream</code> to write the packets to.
	 * @throws IOException
	 *             thrown in case of configuration errors.
	 */
	public PacketSender(OutputStream outputStream) throws IOException {
		
		if (outputStream == null)
			throw new IOException("OutputStream must not be null!");
		
		this.outputStream = outputStream;
	}

	/**
	 * Get <code>Packet</code> objects from the internal ring buffer and write
	 * them to the <code>OutputStream</code>. Terminate the <code>Thread</code>
	 * if errors occur.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		
		while (running) {
			while (readIndex == writeIndex)
				try { Thread.sleep(1000); } catch (InterruptedException e) {;}
			
			Packet packet = packetRingBuffer[readIndex];
			if (readIndex < packetRingBuffer.length-1)
				++readIndex;
			else
				readIndex = 0;
			
			try {
				send (packet);
			} catch (Throwable e) {
				running = false;
				try { outputStream.close(); } catch (IOException e1) {;}
				e.printStackTrace();
			}
		}
	}

	/**
	 * Send one <code>Packet</code> to the <code>OutputStream</code>.
	 * 
	 * @param packet
	 *            the <code>Packet</code> object.
	 * @throws IOException
	 *             thrown in case of I/O errors.
	 */
	private void send(Packet packet) throws IOException {

		outputStream.write(new byte[] { PacketType.COMM_PACKET_MARK,
				PacketType.COMM_PACKET_MARK, packet.type, packet.size });

		if (packet.payload != null && packet.payload.length > 0)
			outputStream.write(packet.payload);

		outputStream.write(new byte[] { (byte) (packet.checksum >> 8),
				(byte) (packet.checksum) });
	}

	/**
	 * Receive a <code>Packet</code> object, put it to the internal ring buffer,
	 * and wake the sender <code>Thread</code>.
	 * 
	 * @param packet
	 *            the <code>Packet</code> object.
	 * @see javiator.util.IPacketListener#receive(javiator.util.Packet)
	 */
	public boolean receive(Packet packet) {
		
		if (!running)
			return false;
		
		packetRingBuffer[writeIndex] = packet;
		
		if (writeIndex < packetRingBuffer.length-1)
			++writeIndex;
		else
			writeIndex = 0;
		
		this.interrupt();
		
		return true;
	}

}