/*
 * @(#) IAviator.java
 *
 * This code is part of the JNavigator project.
 * Copyright (c) 2011  Clemens Krainer
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
package at.uni_salzburg.cs.ckgroup.pilot;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import at.uni_salzburg.cs.ckgroup.pilot.vcl.ICommand;


public interface IAviator {
	
	/**
	 * Load a VCL script from an <code>InputStream>/code>. If an autonomous
	 * flight is currently executed, the flight continues with the new
	 * commands immediately. 
	 * 
	 * @param inStream the <code>InputStream>/code> providing the VCL script.
	 * @throws IOException thrown in case of errors.
	 */
	public void loadVclScript (InputStream inStream) throws IOException;
	
	/**
	 * Get rid of everything.
	 */
	public void destroy();
	
	/**
	 * Start an autonomous flight.
	 */
	public void start();
	
	/**
	 * Stop an autonomous flight by landing the vehicle at the current position.
	 */
	public void stop();
	
	/**
	 * @return if the loaded VCL script is currently executed.
	 */
	public boolean isVclExecutionActive();
	
	/**
	 * @return the currently executed VCL command line. 
	 */
	public int getCurrentVclCommandLine ();
	
	/**
	 * @return the current VCL script as a list of <code>ICommand</code> objects.
	 */
	public List<ICommand> getVclSctipt ();
}
