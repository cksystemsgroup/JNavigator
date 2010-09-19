/*
 * @(#) ICommandDataListener.java
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
 * This interface covers the functionality of a <code>CommandDataListener</code>
 * 
 * @author Clemens Krainer
 */
public interface ICommandDataListener {

	/**
	 * Receive new <code>CommandData</code>
	 * 
	 * @param CommandData
	 *            the new <code>CommandData</code>
	 */
	public void receive(CommandData commandData);
	
	/**
	 * Notify the data listener whether it should process the received data.
	 * 
	 * @param processData
	 *            true if the listener should process the data, false if the
	 *            listener should ignore the data.
	 */
	public void setProcessData (boolean processData);
}
