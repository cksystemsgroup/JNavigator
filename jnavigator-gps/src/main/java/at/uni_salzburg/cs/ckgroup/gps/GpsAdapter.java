/*
 * @(#) GpsAdapter.java
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
package at.uni_salzburg.cs.ckgroup.gps;

import java.io.IOException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

/**
 * This class constructs a GPS position provider from given properties by
 * employing the <code>GpsDaemonBuilder</code>.
 * 
 * @author Clemens Krainer
 */
public class GpsAdapter extends GpsPositionProvider {
	
	private GpsDaemon gpsDaemon;

	/**
	 * Construct a <code>GpsAdapter</code>.
	 * 
	 * @param props the <code>Properties</code> to be used.
	 * @throws ConfigurationException thrown in case of configuration errors.
	 * @throws IOException thrown in case of I/O errors.
	 */
	public GpsAdapter (Properties props) throws ConfigurationException, IOException {
		init (props);
	}
	
	/**
	 * Initialize the <code>GpsAdapter</code>.
	 * 
	 * @param props the <code>Properties</code> to be used.
	 * @throws ConfigurationException thrown in case of configuration errors.
	 * @throws IOException thrown in case of I/O errors.
	 */
	private void init (Properties props) throws ConfigurationException, IOException {
		GpsDaemonBuilder gpsDaemonBuilder = new GpsDaemonBuilder (props);
		gpsDaemon = gpsDaemonBuilder.getGpsDaemon ();
		gpsDaemon.addNmea0183MessageListener (this);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.gps.GpsPositionProvider#close()
	 */
	@Override
	public void close() {
		super.close();
		gpsDaemon.terminate();
	}
}
