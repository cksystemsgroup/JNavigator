/*
 * @(#) IPacketContainer.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2007  Clemens Krainer
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

import java.util.Enumeration;

/**
 * This interface represents a packet container. Packets can be added as Packet objects or byte arrays.
 * 
 * @author Clemens Krainer
 */
public interface IPacketContainer extends Enumeration<Packet> {

	/**
	 * Add a packet object to the container.
	 * 
	 * @param packet
	 *            the Packet to be added.
	 */
	public void add (Packet packet);
	
	/**
	 * Add a byte array as a Packet to the container.
	 * 
	 * @param packetType
	 *            the Packet type.
	 * @param packet
	 *            the Packet payload as a byte array.
	 */
	public void add (byte packetType, byte[] packetPayload);
	
}
