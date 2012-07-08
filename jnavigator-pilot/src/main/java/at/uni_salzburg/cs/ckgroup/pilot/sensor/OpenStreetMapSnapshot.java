/*
 * @(#) OpenStreetMapSnapshot.java
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.pilot.IAutoPilot;
import at.uni_salzburg.cs.ckgroup.pilot.IVehicleBuilder;
import at.uni_salzburg.cs.ckgroup.pilot.config.ConfigurationException;

public class OpenStreetMapSnapshot extends AbstractSensor {

	Logger LOG = Logger.getLogger(OpenStreetMapSnapshot.class);
	
	OpenStreetMapCamera camera;

	private IVehicleBuilder vehicleBuilder;
	
	public OpenStreetMapSnapshot(Properties props, IVehicleBuilder vehicleBuilder) throws URISyntaxException, ConfigurationException {
		super(props);
		this.vehicleBuilder = vehicleBuilder;
		camera = new OpenStreetMapCamera(props);
	}

	@Override
	public String getValue() {
		return null;
	}
	@Override
	public String getMimeType() {
		return "image/png";
	}
	
	@Override
	public byte[] getByteArray() {
		PolarCoordinate pos = null;
		
		if (vehicleBuilder != null && vehicleBuilder.getAutoPilot() != null) {
			IAutoPilot autoPilot = vehicleBuilder.getAutoPilot();
			pos = new PolarCoordinate(autoPilot.getPositionProvider().getCurrentPosition());
			pos.setAltitude(autoPilot.getAltitudeOverGround());
		}
		
		try {
			return camera.getImage(pos);
		} catch (IOException e) {
			LOG.info("Can not get image from OpenStreetMapCamera", e);
			return null;
		}
	}
}
