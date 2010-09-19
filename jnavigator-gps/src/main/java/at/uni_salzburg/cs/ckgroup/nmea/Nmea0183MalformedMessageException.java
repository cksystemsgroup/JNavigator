/*
 * @(#) Nmea0183MalformedMessageException.java
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

import at.uni_salzburg.cs.ckgroup.NavigationException;

/**
 * This exception indicates the receipt of a malformed NMEA message.
 * 
 * @author Clemens Krainer
 */
public class Nmea0183MalformedMessageException extends NavigationException
{
	private static final long serialVersionUID = 8741845683967707301L;

	/**
	 * Construct a Nmea0183MalformedMessageException containing a text message.
	 * 
	 * @param msg the text message
	 */
	public Nmea0183MalformedMessageException (String msg) {
		super (msg);
	}
}
