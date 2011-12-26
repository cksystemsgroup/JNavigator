/*
 * @(#) SensorBuilder.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.pilot.IVehicleBuilder;
import at.uni_salzburg.cs.ckgroup.pilot.config.Configuration;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

public class SensorBuilder {
	
	private boolean configOk;
	private List<String> configErrors = new ArrayList<String>();
	private Map<String, AbstractSensor> sensors = new HashMap<String, AbstractSensor>();
	private String workDir;
	private IVehicleBuilder vehicleBuilder;
	
	private static final String FMT_UNKNOWN_URI_TYPE = "No driver available to handle URI '%1$s' of sensor '%2$s' (%3$s).";
	private static final String FMT_SENSOR_CONFIG_ERROR = "Sensor %1$s has configuration errors: %2$s";
	
	public void createSensors (Properties conf) {
		configOk = true;
		sensors.clear();
		configErrors.clear();
		
		String[] list = conf.getProperty(Configuration.PROP_SENSOR_LIST,"").trim().split("\\s*,\\s*");
		for (String s : list) {
			String prefix = Configuration.PROP_SENSOR_PREFIX + s + ".";
			Properties props = PropertyUtils.extract(prefix, conf);
			props = PropertyUtils.replaceFirst(prefix, "", props);
			props.setProperty("work.dir", workDir);
			String uri = props.getProperty("uri","");
			String type = props.getProperty("type","");
			String name = props.getProperty("name","");
			AbstractSensor sensor = null;
			
			try {
				if (uri.startsWith("gps:")) {
					sensor = new GpsSensor(props, vehicleBuilder);
				} else if (uri.startsWith("sonar:")) {
					sensor = new SonarSensor(props, vehicleBuilder);
				} else if (uri.startsWith("rand:")) {
					sensor = new RandomSensor(props);
				} else if (uri.startsWith("x11:") && type.equals("snapshot")) {
					sensor = new X11Snapshot(props);
				} else if (uri.startsWith("file:") && type.equals("video4linux")) {
					sensor = new X11FrameGrabber(props);
				} else {
					configErrors.add(String.format(FMT_UNKNOWN_URI_TYPE, uri, name, s));
					configOk = false;
				}
				
				if (sensor != null)
					sensors.put(sensor.getPath(), sensor);
				
			} catch (Throwable e) {
				e.printStackTrace();
				configErrors.add(String.format(FMT_SENSOR_CONFIG_ERROR, s, e.getMessage()));
				configOk = false;
			}
		}
	}

	public boolean isConfigOk() {
		return configOk;
	}

	public List<String> getConfigErrors() {
		return configErrors;
	}

	public Map<String, AbstractSensor> getSensors() {
		return sensors;
	}
	
	/**
	 * @param workDir the directory to be used for temporary files.
	 */
	public void setWorkDir(String workDir) {
		this.workDir = workDir;
	}

	/**
	 * @param vehicleBuilder the current vehicle builder instance.
	 */
	public void setVehicleBuilder(IVehicleBuilder vehicleBuilder) {
		this.vehicleBuilder = vehicleBuilder;
	}
	
}
