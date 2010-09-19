/*
 * @(#) NavigationFrame.java
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

import java.awt.Frame;

import javax.swing.BoxLayout;

/**
 * This class implements an AWT <code>Frame</code> that displays a compass, a
 * speedometer, an altimeter and a clock.
 * 
 * @author Clemens Krainer
 */
public class NavigationFrame extends Frame
{
	private static final long serialVersionUID = 5481246468209034474L;
	
	Compass compass = new Compass ();
	Speedometer speedoMeter = new Speedometer ();
	Altimeter altimeter = new Altimeter ();
	Clock clock = new Clock ();
	
	/**
	 * Construct a <code>NavigationFrame</code>.
	 * 
	 * @param name the title of the <code>Frame</code>.
	 */
	public NavigationFrame (String name) {
		super (name);
		setLayout (new BoxLayout(this, BoxLayout.Y_AXIS));
		add (compass);
		add (speedoMeter);
		add (altimeter);
		add (clock);
	}

	/**
	 * Return the views of the <code>Frame</code> as an array of
	 * <code>INavigatorView</code> objects.
	 * 
	 * @return the views of the <code>Frame</code>
	 */
	public INavigatorView[] getViews ()
	{
		return new INavigatorView[] {compass, speedoMeter, altimeter, clock};
	}
}
