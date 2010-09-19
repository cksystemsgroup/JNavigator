/*
 * @(#) OnTheWireUdpReceiver.java
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
package at.uni_salzburg.cs.ckgroup.location.ubisense;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Properties;

public class OnTheWireUdpReceiver extends Thread {

	public static final String PROP_PORT = "port";
	public static final String PROP_BUFFER_LENGTH = "buffer.length";

	public static final short MAGIC_0 = (short) 0xE298;
	public static final short CELL_ENTRY_MESSAGE_MAGIC_1 = (short) 0x1B1;
	public static final short CELL_EXIT_MESSAGE_MAGIC_1 = (short) 0x12E;
	public static final short LOCATION_MESSAGE_MAGIC_1 = (short) 0x26A;
	public static final short UPLINK_ONLY_MESSAGE_MAGIC_1 = (short) 0x13D;
	public static final short BUTTON_MESSAGE_MAGIC_1 = (short) 0xBD;

	private DatagramSocket socket;
	private int bufferLength;
	private int port;
	
	private OnTheWireLocationMessageListener locationMessageListener = null;

	/**
	 * @param props
	 * @throws SocketException
	 */
	public OnTheWireUdpReceiver(Properties props) throws SocketException {
		port = Integer.parseInt(props.getProperty(PROP_PORT));
		bufferLength = Integer.parseInt(props.getProperty(PROP_BUFFER_LENGTH,
				"256"));
		socket = new DatagramSocket(port);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		DatagramPacket packet;
		boolean running = true;
		byte[] buf = new byte[bufferLength];

		try {
			while (running) {
				packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				parseMessage(packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		running = false;
	}

	/**
	 * @param packet
	 */
	private void parseMessage(DatagramPacket packet) {
		byte[] buf = packet.getData();
		int offset = packet.getOffset();
		int length = packet.getLength();

		ByteArrayInputStream in = new ByteArrayInputStream(buf, offset, length);
		DataInputStream dis = new DataInputStream(in);

		try {
			short magic0 = dis.readShort();

			if (magic0 != MAGIC_0) {
				// wrong message
				return;
			}

			switch (dis.readShort()) {
			case LOCATION_MESSAGE_MAGIC_1:
				OnTheWireLocationMessage lm = new OnTheWireLocationMessage (dis);
//				System.out.println ("Received: " + lm.toString());
				if (locationMessageListener != null)
					locationMessageListener.receive(lm);
				break;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * @param locationMessageListener
	 */
	public void setOnTheWireLocationMessageListener (OnTheWireLocationMessageListener locationMessageListener) {
		this.locationMessageListener = locationMessageListener;  
	}

}
