/*
 * @(#) Aviator.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
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

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.pilot.vcl.Parser;

/**
 * This class is responsible for navigating the vehicle.
 */
public class Aviator implements IAviator {
	
	Logger LOG = Logger.getLogger(Aviator.class);
	
	/**
	 * The current VCL parser.
	 */
	private Parser parser = new Parser();
	
	/**
	 * This variable is true if the currently loaded VCL script is executed. 
	 */
	private boolean vclExecutionActive = false;
	
	/**
	 * This variable contains the currently executed VCL script line. 
	 */
	private int currentCommandLine = -1;
	
	/**
	 * Load a VCL script from an <code>InputStream</code> and parse it.
	 * 
	 * @param inStream the configuration's <code>InputStream</code>
	 * @throws IOException thrown in case of errors.
	 */
	public void loadVclScript (InputStream inStream) throws IOException {
		parser.parse(inStream);
	}

	/**
	 * Destroy all dependent objects and unload the configuration.
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		LOG.error("Destruction not yet implemented.");
	}
	
	/**
	 * @return whether the currently loaded VCL script has no syntax errors.
	 */
	public boolean isScriptOk() {
		return parser.isScriptOk();
	}
	
	/**
	 * @return the current VCL script as a list of strings.
	 */
	public List<String> getScript() {
		return parser.getSource();
	}
	
	/**
	 * @return the current VCL script errors as a list.
	 */
	public List<Boolean> getScriptErrors() {
		return parser.getErrors();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#start()
	 */
	public void start() {
		// TODO Auto-generated method stub
		LOG.error("Starting autonomous flights is not yet implemented.");
		vclExecutionActive = true;
		
		// TODO implement
		currentCommandLine = 6;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#stop()
	 */
	public void stop() {
		// TODO Auto-generated method stub
		LOG.error("Stopping autonomous flights is not yet implemented.");
		vclExecutionActive = false;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#isVclExecuted()
	 */
	public boolean isVclExecuted() {
		return vclExecutionActive;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IAviator#getCurrentVclCommandLine()
	 */
	public int getCurrentVclCommandLine() {
		// TODO Auto-generated method stub
		return vclExecutionActive ? currentCommandLine : -1;
	}
	
	
}
