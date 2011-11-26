/*
 * @(#) RandomSensor.java
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

import java.net.URISyntaxException;
import java.util.Formatter;
import java.util.Locale;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.pilot.config.ConfigurationException;


/**
 * This class generates random sensor values in a defined range.
 */
public class RandomSensor extends AbstractSensor {
	
	private float upperLimit;
	private float lowerLimit;
	private int precision;
	
	public RandomSensor (Properties props) throws URISyntaxException, ConfigurationException {
		super(props);
		
		String path = uri.getPath();
		if ("".equals(path))
			throw new ConfigurationException("URI not configured. Use, e.g., rand:///0/100");
		
		String[] ds = path.split("/");
		if (!"rand".equals(uri.getScheme())
				|| uri.getHost() != null
				|| ds.length != 3
				|| "".equals(ds[1].trim())
				|| "".equals(ds[2].trim())
		)	throw new ConfigurationException("Invalid URI. Use, e.g., rand:///0/100");

		lowerLimit = Float.parseFloat(ds[1].trim());
		upperLimit = Float.parseFloat(ds[2].trim());
		precision = Integer.parseInt(props.getProperty(PROP_PRECISION,"5"));
	}
	
	@Override
	public String getValue () {
		float f = (float) (lowerLimit + Math.random() * (upperLimit - lowerLimit));
		Formatter form = new Formatter();
				
		if (precision == 0)
			return form.format(Locale.US, "%d", (int)f).toString();
		
		return form.format(Locale.US, "%."+precision+"f", f).toString();
	}

}
