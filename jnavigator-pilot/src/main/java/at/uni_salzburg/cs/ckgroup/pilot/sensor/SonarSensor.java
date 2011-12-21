/*
 * @(#) SonarSensor.java
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
import java.util.Locale;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.pilot.IVehicleBuilder;
import at.uni_salzburg.cs.ckgroup.pilot.config.ConfigurationException;

public class SonarSensor extends AbstractSensor {
	
	public static final String OUTPUT_FORMAT = "AltitudeOverGround: %1$.4f";
	
	private IVehicleBuilder vehicleBuilder;

	public SonarSensor(Properties props, IVehicleBuilder vehicleBuilder) throws URISyntaxException, ConfigurationException {
		super(props);
		this.vehicleBuilder = vehicleBuilder;
	}

	@Override
	public String getValue() {
		double v = 0;
		if (vehicleBuilder != null && vehicleBuilder.getAutoPilot() != null) {
			v = vehicleBuilder.getAutoPilot().getAltitudeOverGround();
		}
		return String.format(Locale.US, OUTPUT_FORMAT, v);
	}

}
