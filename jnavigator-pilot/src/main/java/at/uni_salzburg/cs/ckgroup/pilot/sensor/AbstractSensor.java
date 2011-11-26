/*
 * @(#) AbstractSensor.java
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
package at.uni_salzburg.cs.ckgroup.pilot.sensor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.pilot.config.ConfigurationException;

/**
 * This abstract class defines the minimum set of methods of sensors.
 */
public abstract class AbstractSensor {

	public static final String PROP_NAME = "name";
	public static final String PROP_URI = "uri";
	public static final String PROP_PATH = "path";
	public static final String PROP_PRECISION = "precision";
	
	protected String name;
	protected URI uri;
	protected String path;
	
	/**
	 * Construct an abstract sensor.
	 * 
	 * @param props the properties for constructing the sensor
	 * @throws URISyntaxException thrown in case of an invalid sensor <code>URI</code>
	 * @throws ConfigurationException thrown in case of other configuration errors.
	 */
	public AbstractSensor (Properties props) throws URISyntaxException, ConfigurationException {
		name = props.getProperty(PROP_NAME);
		if (name == null)
			throw new ConfigurationException("Name not configured.");
		
		uri = new URI(props.getProperty(PROP_URI,"rand:///0/100"));
		
		path = props.getProperty(PROP_PATH);
		if (path == null || "".equals(path))
			throw new ConfigurationException("Path not configured.");
		
	}
	
	/**
	 * @return the sensor's name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sensor's URI
	 */
	public URI getUri() {
		return uri;
	}

	/**
	 * @return the registered sensor path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the current sensor value as a <code>String</code>
	 */
	public abstract String getValue ();
	
	/**
	 * @return the Mime Type of the sensor data.
	 */
	public String getMimeType () {
		return "text/plain";
	}
	
	/**
	 * @return the current sensor value as an array of bytes.
	 */
	public byte[] getByteArray () {
		return getValue().getBytes();
	}
}
