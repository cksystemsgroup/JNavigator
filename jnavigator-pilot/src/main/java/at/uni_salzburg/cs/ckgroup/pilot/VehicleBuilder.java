/*
 * @(#) VehicleBuilder.java
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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.pilot.config.Configuration;

public class VehicleBuilder implements IVehicleBuilder {
	
	Logger LOG = Logger.getLogger(VehicleBuilder.class);
	
	/**
	 * The current vehicle configuration.
	 */
	private Configuration conf = null;
	
	/**
	 * Load a vehicle configuration from an <code>InputStream</code> and build it.
	 * 
	 * @param inStream the configuration's <code>InputStream</code>
	 * @throws IOException thrown in case of errors.
	 */
	public void setConfig (Configuration configuration) throws IOException {
		destroy();
		this.conf = configuration;
		
		if (configuration.isConfigOk())
			buildVehicle();
	}

	/**
	 * Build the vehicle according to the loaded configuration.
	 */
	private void buildVehicle() {
		// TODO Auto-generated method stub
		LOG.error("Vehicle construction not yet implemented.");
	}
	
	/**
	 * Destroy all dependent objects and unload the configuration.
	 */
	public void destroy() {
		// TODO Auto-generated method stub
		LOG.error("Vehicle destruction not yet implemented.");
	}
	
	public List<String> getConfiguration() {
		List<String> list = new ArrayList<String>();
//		String[] keys = conf.keySet().toArray(new String[0]);
//		Arrays.sort(keys);
//		for (String k : keys)
//			list.add(k + " = " + conf.getProperty(k) + "\n");
		return list;
	}
}
