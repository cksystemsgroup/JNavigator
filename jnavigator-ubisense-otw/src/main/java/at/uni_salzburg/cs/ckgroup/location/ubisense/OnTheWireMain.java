/*
 * @(#) OnTheWireMain.java
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

public class OnTheWireMain {

	/**
	 * Usage: OnTheWireMain [my.properties [--long]]
	 * 
	 * @param args the program arguments
	 */
	public static void main(String[] args) {
		Properties props;
		
		try {
			if (args.length >= 1) {
				InputStream inStream = new FileInputStream (args[0]);
				props = new Properties ();
				props.load(inStream);
			} else {
				props = PropertyUtils.loadProperties ("OnTheWire.properties");
			}
			
			PacketSerializer serializer = 
				args.length >= 2 && "--long".equals(args[1]) ?
						new PacketSerializer (props) : new PacketSerializer4RT (props);
			
			int forwarderPort = Integer.parseInt (props.getProperty ("forwarder.port", "9001"));
			OnTheWireSocketServer server = new OnTheWireSocketServer (forwarderPort, serializer);
			server.start ();

			OnTheWireUdpReceiver receiver = new OnTheWireUdpReceiver (props);
			receiver.setOnTheWireLocationMessageListener(serializer);
			receiver.run ();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
