/*
 * @(#) IPacketListener.java
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

/**
 * This interface covers the functionality of a <code>PacketListener</code>
 * 
 * @author Clemens Krainer
 */
public interface IPacketListener {

	/**
	 * Receive new <code>Packet</code> objects.
	 * 
	 * @param packet
	 *            the new <code>Packet</code>
	 * @return true if the listener accepted the <code>Packet</code> object.
	 */
	public boolean receive(Packet packet);
}
