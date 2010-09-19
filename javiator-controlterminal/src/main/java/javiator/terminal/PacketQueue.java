/*
 * @(#) PacketQueue.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2008  Clemens Krainer
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
package javiator.terminal;

import java.util.NoSuchElementException;
import java.util.Vector;

import javiator.util.IPacketContainer;
import javiator.util.Packet;

public class PacketQueue implements IPacketContainer {

	private Vector<Packet> packetContainer = new Vector<Packet> ();

	/* (non-Javadoc)
	 * @see javiator.util.IPacketContainer#add(javiator.util.Packet)
	 */
	public void add(Packet packet)
	{
		packetContainer.add (packet);
	}

	/* (non-Javadoc)
	 * @see javiator.util.IPacketContainer#add(byte, byte[])
	 */
	public void add(byte packetType, byte[] packetPayload)
	{
		Packet packet = new Packet (packetType, packetPayload);
		packetContainer.add (packet);
	}

	/* (non-Javadoc)
	 * @see java.util.Enumeration#hasMoreElements()
	 */
	public boolean hasMoreElements()
	{
		return packetContainer.size() != 0;
	}

	/* (non-Javadoc)
	 * @see java.util.Enumeration#nextElement()
	 */
	public Packet nextElement()
	{
		if (packetContainer.size() == 0)
			throw new NoSuchElementException ();
		
		Packet packet = packetContainer.firstElement();
		packetContainer.remove(0);
		return packet;
	}	
}
