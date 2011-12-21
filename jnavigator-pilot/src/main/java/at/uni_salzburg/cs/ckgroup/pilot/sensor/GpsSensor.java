/*
 * @(#) GpsSensor.java
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

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.pilot.IVehicleBuilder;
import at.uni_salzburg.cs.ckgroup.pilot.config.ConfigurationException;

public class GpsSensor extends AbstractSensor {
	
	public static final String OUTPUT_FORMAT = "Latitude: %1$.8f\nLongitude: %2$.8f\nAltitude: %3$.8f\nCourseOverGround: %4$.0f\nSpeedOverGround: %5$.2f\n";
	
	private IVehicleBuilder vehicleBuilder;

	public GpsSensor(Properties props, IVehicleBuilder vehicleBuilder) throws URISyntaxException, ConfigurationException {
		super(props);
		this.vehicleBuilder = vehicleBuilder;
	}
	
	@Override
	public String getValue() {
		if (vehicleBuilder == null || vehicleBuilder.getPositionProvider() == null)
			return "GPS position not available.";
		
		Double courseOverGround = vehicleBuilder.getPositionProvider().getCourseOverGround();
		PolarCoordinate currentPosition = vehicleBuilder.getPositionProvider().getCurrentPosition();
		Double speedOverGround = vehicleBuilder.getPositionProvider().getSpeedOverGround();
		
		if (currentPosition == null)
			return "";
		
		return String.format(Locale.US, OUTPUT_FORMAT, 
			currentPosition.getLatitude(),
			currentPosition.getLongitude(),
			currentPosition.getAltitude(),
			courseOverGround,
			speedOverGround
		);
	}

}
