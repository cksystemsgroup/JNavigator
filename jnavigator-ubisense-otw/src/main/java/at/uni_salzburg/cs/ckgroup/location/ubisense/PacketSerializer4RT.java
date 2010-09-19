/*
 * @(#) PacketSerializer4RT.java
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

import java.util.Properties;

/**
 * This class modifies the <code>PacketSerializer</code> to the needs of R.T.
 * 
 * @author Clemens Krainer
 */
public class PacketSerializer4RT extends PacketSerializer {

    public PacketSerializer4RT (Properties props) {
    	super (props);
    }

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.location.ubisense.OnTheWireLocationMessageListener#receive(at.uni_salzburg.cs.ckgroup.location.ubisense.OnTheWireLocationMessage)
	 */
	public void receive(OnTheWireLocationMessage message) {

		StringBuilder sb = new StringBuilder();
		sb.append (message.getTag_id_bottom()).append (',')
			.append ((int)(message.getX()*1000)).append (',')
			.append ((int)(message.getY()*1000));
//		String checkSum = calculateChecksum (sb.toString().getBytes());
//		sb.append (checkSum);
		sb.append("\r\n");
		String msg = sb.toString();
		
		System.out.println ("PacketSerializer4RT: " + msg);
		
		messageBuffer [writeIndex] = msg.getBytes();
		
		if (writeIndex+1 >= messageBuffer.length)
			writeIndex = 0;
		else
			++writeIndex;
	}

}
