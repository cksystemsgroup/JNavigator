/*
 * @(#) Nmea0183MessageProvider.java
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
package at.uni_salzburg.cs.ckgroup.nmea;

/**
 * This interface summarizes the functionality of a provider of
 * <code>Nmea0183Message</code> objects.
 * 
 * @author Clemens Krainer
 */
public interface Nmea0183MessageProvider {

	/**
	 * Add a NMEA 0183 message listener to the list of listeners
	 * 
	 * @param listener the listener to be added
	 */
	public void addNmea0183MessageListener (Nmea0183MessageListener listener);

	/**
	 * Remove a NMEA 0183 message listener from the list of listeners
	 * 
	 * @param listener the listener to be deleted
	 */
	public void removeNmea0183MessageListener (Nmea0183MessageListener listener);
	
}
