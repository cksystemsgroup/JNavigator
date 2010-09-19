/*
 * @(#) BirdsEyeFrame.java
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
 * This class implements a <code>Frame</code> that visualizes the
 * <code>BirdsEyeView</code>.
 * 
 * @author Clemens Krainer
 */
public class BirdsEyeFrame extends Frame
{
	private static final long serialVersionUID = 5481246468209034474L;
	
	/**
	 * The <code>BirdsEyeView</code> to be displayed.
	 */
	BirdsEyeView birdView = new BirdsEyeView ();
	
	/**
	 * Construct a <code>BirdsEyeFrame</code>.
	 * 
	 * @param name the name of this <code>Frame</code>.
	 */
	public BirdsEyeFrame (String name) {
		super (name);
		setLayout (new BoxLayout(this, BoxLayout.Y_AXIS));
		add (birdView);
	}

	/**
	 * Return all <code>INavigatorView</code> objects of this <code>Frame</code>.
	 * 
	 * @return all <code>INavigatorView</code> objects of this <code>Frame</code>.
	 */
	public INavigatorView[] getViews ()
	{
		return new INavigatorView[] {birdView};
	}
}
