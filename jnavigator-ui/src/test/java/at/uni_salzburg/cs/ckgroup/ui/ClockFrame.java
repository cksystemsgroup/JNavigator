/*
 * @(#) ClockFrame.java
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
package at.uni_salzburg.cs.ckgroup.ui;

import javax.swing.JFrame;

/**
 * @author Clemens Krainer
 */
public class ClockFrame extends JFrame
{
	private static final long serialVersionUID = 8965112134801112471L;
	
	Clock clock = new Clock ();
	
	/**
	 * Construct a <code>ClockFrame</code>.
	 * 
	 * @param name the title of the <code>Frame</code>.
	 */
	public ClockFrame (String name) {
		super (name);
//		setLayout (new BoxLayout(this, BoxLayout.Y_AXIS));
		getContentPane().add (clock);
	}
	
	/**
	 * Retrieve the current instance <class>Clock</code>
	 * 
	 * @return the current instance <class>Clock</code>
	 */
	public Clock getClock () {
		return clock;
	}
	
}
