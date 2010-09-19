/*
 * @(#) PacketReceiver.java
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
import java.io.InputStream;

/**
 * This class reads serialized packets from an <code>InputStream</code> and
 * forwards them as <code>Packet</code> objects to a given
 * <code>IPacketListener</code>.
 * 
 * @author Clemens Krainer
 */
public class PacketReceiver extends Thread {

	private InputStream inputStream;
	private IPacketListener packetListener;

	/**
	 * Construct a <code>PacketReceiver</code>.
	 * 
	 * @param inputStream
	 *            the <code>InputStream</code> instance to read serialized
	 *            packets from.
	 * @param packetListener
	 *            the listener which receives the packets as <code>Packet</code>
	 *            objects.
	 * @throws IOException
	 *             thrown in case of parameter errors.
	 */
	public PacketReceiver(InputStream inputStream,
			IPacketListener packetListener) throws IOException {

		if (inputStream == null)
			throw new IOException("InputStream must not be null!");

		if (packetListener == null)
			throw new IOException("PacketListener must not be null!");

		this.inputStream = inputStream;
		this.packetListener = packetListener;
	}

	/**
	 * Receive serialized packets from an <code>InputStream</code> and forward
	 * them to the given <code>IPacketListener</code>. Quit running if errors
	 * occur.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		boolean running = true;

		try {
			while (running) {
				Packet packet = receive();
				if (packet != null)
					packetListener.receive(packet);
			}
		} catch (IOException e) {
			running = false;
			try {
				inputStream.close();
			} catch (IOException e1) {
				; // Intentionally empty
			}
			e.printStackTrace();
		}
	}

	/**
	 * Receive one packet from the given <code>InputStream</code>.
	 * 
	 * @return the packet received or null in case of content errors.
	 * @throws IOException
	 *             thrown in case of I/O errors.
	 */
	private Packet receive() throws IOException {

		byte data = ~PacketType.COMM_PACKET_MARK;
		while (data != PacketType.COMM_PACKET_MARK) {
			data = readByte();
			if (data != PacketType.COMM_PACKET_MARK)
				continue;
			data = readByte();
		}

		byte type = readByte();

		/* check for valid type */
		if (type < 1 || type > PacketType.COMM_PACKET_LIMIT)
			return null;

		byte size = (byte) readByte();

		/* check for allowed size */
		if (size < 0)
			return null;

		Packet packet = new Packet(type, size);
		for (int i = 0; i < size; ++i) {
			packet.payload[i] = readByte();
		}

		int checksum = readByte() & 0xFF;
		checksum = (checksum << 8) | (readByte() & 0xFF);

		if (checksum == packet.calcChecksum()) {
			return packet;
		} else {
			System.err.println("PacketReceiver receive Checksum ERROR: "
					+ checksum + " != " + packet.calcChecksum());

			System.err.print(" " + packet.type + " " + packet.size + " ");
			for (int i = 0; i < packet.size; ++i)
				System.err.print(" " + (((int) packet.payload[i]) & 0xFF));
			System.out.println();
		}

		return null;
	}

	/**
	 * Read one byte from the given <code>InputStream</code>.
	 * 
	 * @return the byte read.
	 * @throws IOException
	 *             thrown in case of I/O errors or the end of the
	 *             <code>InputStream</code>.
	 */
	private byte readByte() throws IOException {

		int b = inputStream.read();

		if (b == -1)
			throw new IOException("InputStream ended.");

		return (byte) b;
	}

}
