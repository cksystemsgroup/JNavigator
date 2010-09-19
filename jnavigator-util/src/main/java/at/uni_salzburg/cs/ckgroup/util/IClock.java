/*
 * @(#) IClock.java
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
package at.uni_salzburg.cs.ckgroup.util;

/**
 * This interface provides the functionality of a simple clock. E.g the
 * <code>IPilot</code> implementations use implementations of this interface
 * rather than the <code>System</code> methods to allow unit tests to provide a
 * clock independent of execution time.
 * 
 * @author Clemens Krainer
 */
public interface IClock
{
	/**
	 * Returns the current time in milliseconds as
	 * <code>System.currentTimeMillis()</code> would do it.
	 * 
	 * @return the current time in milliseconds.
	 */
	public long currentTimeMillis();
}
