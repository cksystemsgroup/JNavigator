/*
 * @(#) FakeVehicleBuilder.java
 *
 * This code is part of the CPCC project
 * Copyright (c) 2012  Clemens Krainer
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

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;
import at.uni_salzburg.cs.ckgroup.pilot.config.Configuration;

public class FakeVehicleBuilder implements IVehicleBuilder {
	
	private FakePositionProvider positionProvider;

	private IAutoPilot autoPilot;

	private ISetCourseSupplier setCourseSupplier;

	private Configuration configuration;
	
	private IGeodeticSystem geodeticSystem = new WGS84();

	
	public FakeVehicleBuilder(Properties props) {
		// intentionally empty
	}

	@Override
	public void setConfig(Configuration configuration) throws IOException {
		destroy();
		
		if (configuration.isConfigOk()) {
			this.configuration = configuration;
			try {
				buildVehicle();
			} catch (Exception e) {
				e.printStackTrace();
//				throw new IOException(e);
			}
		}
	}

	private void buildVehicle() {
		positionProvider = new FakePositionProvider();
		autoPilot = new MyAutoPilot();
	}

	@Override
	public IPositionProvider getPositionProvider() {
		return positionProvider;
	}

	@Override
	public IAutoPilot getAutoPilot() {
		return autoPilot;
	}

	@Override
	public void destroy() {
		// intentionally empty
	}

	@Override
	public void setWorkDir(File workDir) {
		// intentionally empty
	}

	@Override
	public void setSetCourseSupplier(ISetCourseSupplier setCourseSupplier) {
		this.setCourseSupplier = setCourseSupplier;
	}
	
	
	private class FakePositionProvider implements IPositionProvider {

		private double altitude;
		
		private PolarCoordinate homePosition;
		
		public FakePositionProvider() {
			homePosition = configuration.getPlantHomeLocation();
			altitude = homePosition.getAltitude();
		}
		
		public double getAltitudeOverGround() {
			PolarCoordinate p = getCurrentPosition();
			return p.altitude - altitude > 0 ? p.altitude - altitude : 0;
		}
		
		@Override
		public PolarCoordinate getCurrentPosition() {
			if (setCourseSupplier == null || setCourseSupplier.getSetCoursePosition(0) == null || setCourseSupplier.getSetCoursePosition(0).position == null) {
				return homePosition;
			}
			PolarCoordinate p = new PolarCoordinate(setCourseSupplier.getSetCoursePosition(0).position);
			p.altitude += altitude;
			return p;
		}

		@Override
		public Double getSpeedOverGround() {
			if (setCourseSupplier == null || setCourseSupplier.getSetCoursePosition(0) == null) {
				return Double.valueOf(0.0);
			}
			return setCourseSupplier.getSetCoursePosition(0).totalSpeed;
		}

		@Override
		public Double getCourseOverGround() {
			if (setCourseSupplier == null || setCourseSupplier.getSetCoursePosition(0) == null) {
				return Double.valueOf(0.0);
			}
			return setCourseSupplier.getSetCoursePosition(0).courseOverGround;
		}

		@Override
		public IGeodeticSystem getGeodeticSystem() {
			return geodeticSystem;
		}

		@Override
		public void close() {
			// intentionally empty
		}
		
	}
	
	private class MyAutoPilot implements IAutoPilot {

		private boolean autoPilotFlight = false;

		@Override
		public boolean isAutoPilotFlight() {
			return autoPilotFlight;
		}

		@Override
		public void setAutoPilotFlight(boolean autoPilotFlight) {
			this.autoPilotFlight  = autoPilotFlight;
		}

		@Override
		public void startUpEngines() throws IOException {
			autoPilotFlight = true;
		}

		@Override
		public void shutDownEngines() throws IOException {
			autoPilotFlight = false;
		}

		@Override
		public IPositionProvider getPositionProvider() {
			return positionProvider;
		}

		@Override
		public ISetCourseSupplier getSetCourseSupplier() {
			return setCourseSupplier;
		}

		@Override
		public double getAltitudeOverGround() {
			return positionProvider.getAltitudeOverGround();
		}
		

	}
}
