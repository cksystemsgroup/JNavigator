/*
 * @(#) Configuration.java
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
package at.uni_salzburg.cs.ckgroup.pilot.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.pilot.sensor.SensorBuilder;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

public class Configuration implements IConfiguration {
	
	Logger LOG = Logger.getLogger(Configuration.class);
	
	public static final String PLANT_CONFIG_TEMPLATE_FORMAT = "plant/%s-config.vm";
	public static final String CONTROLLER_CONFIG_TEMPLATE_FORMAT = "controller/%s-config.vm";
	public static final String LOCATION_SYS_CONFIG_TEMPLATE_FORMAT = "location/%s-config.vm";
	public static final String PILOT_CONFIG_TEMPLATE_FORMAT = "pilot/%s-config.vm";
	
	/**
	 * The uploaded configuration.
	 */
	private Properties conf = new Properties();
	private SensorBuilder sensors = new SensorBuilder();
	
	/**
	 * The errors in the configuration as a map. Key is the configuration
	 * parameter and value is the according error message. This map contains
	 * only messages of erroneous configuration parameters.
	 */
	private Map<String,String> configErrors = new HashMap<String,String>();
	
	/**
	 * The directory to be used for temporary files. 
	 */
	private File workDir;
	
	/**
	 * The configured sensors.
	 */
//	private List<AbstractSensor> sensors = new ArrayList<AbstractSensor>();
	
	public static final String ERROR_MESSAGE_MISSING_VALUE = "# please provide a value!";
	public static final String ERROR_MESSAGE_INVALID_VALUE = "# invalid value!";
	public static final String ERROR_MESSAGE_UNKNOWN_TYPE = "# unknown type!";
	public static final String ERROR_MESSAGE_UNKNOWN_SENSOR_TYPE = "# unknown sensor type!";

	public static final String PROP_LOCATION_LATITUDE =  "latitude";
	public static final String PROP_LOCATION_LONGITUDE =  "longitude";
	public static final String PROP_LOCATION_ALTITUDE = "altitude";

	public static final String PROP_PLANT_TYPE	= "plant.type";
	public static final String PROP_PLANT_SIMULATED = "plant.simulated";
	public static final String PROP_PLANT_LISTENER = "plant.listener";
	public static final String PROP_PLANT_LOCATION_SYSTEM_TYPE = "plant.location.system.type";
	public static final String PROP_PLANT_LOCATION_SYSTEM_LISTENER = "plant.location.system.listener";
	public static final String PROP_PLANT_LOCATION_SYSTEM_UPDATE_RATE = "plant.location.system.update.rate";
	public static final String PROP_PLANT_HOME_LOCATION = "plant.home.location";
	
	public static final String PROP_CONTROLLER_TYPE = "controller.type";
	public static final String PROP_CONTROLLER_SIMULATED = "controller.simulated";
	
	public static final String PROP_PILOT_TYPE = "pilot.type";
	public static final String PROP_PILOT_CONTROLLER_CONNECTOR = "pilot.controller.connector";
	public static final String PROP_PILOT_NAME = "pilot.name";
	public static final String PROP_PILOT_FGFS_UPDATE = "pilot.fgfs.update";
	public static final String PROP_PILOT_FGFS_CONNECTOR = "pilot.fgfs.connector";
	public static final String PROP_PILOT_FGFS_TYPE = "pilot.fgfs.type";

	/**
	 * The prefix of the sensor properties. 
	 */
	public static final String PROP_SENSOR_PREFIX = "sensor.";
	public static final String PROP_SENSOR_LIST = PROP_SENSOR_PREFIX + "list";
	
	public static final String PROP_PILOT_PREFIX = "pilot.";
	public static final String PROP_PILOT_LIST = PROP_PILOT_PREFIX + "list";
	public static final String PROP_PILOT_ULR_SUFFIX = ".url";
	
	/**
	 * The parameters and their default values. 
	 */
	public String [][] parameters = {
		{ PROP_PLANT_TYPE },
		{ PROP_PLANT_LISTENER },
		{ PROP_PLANT_SIMULATED },
		{ PROP_PLANT_LOCATION_SYSTEM_TYPE, "GPS" },
		{ PROP_PLANT_LOCATION_SYSTEM_LISTENER },
		{ PROP_PLANT_LOCATION_SYSTEM_UPDATE_RATE, "10" },
		{ PROP_PLANT_HOME_LOCATION, "48.0", "13.0", "440.0" },
	
		{ PROP_CONTROLLER_TYPE, "external" },
		{ PROP_CONTROLLER_SIMULATED, "false" },
		        
		{ PROP_PILOT_TYPE },
		{ PROP_PILOT_CONTROLLER_CONNECTOR },
		{ PROP_PILOT_NAME },
		
		{ PROP_PILOT_FGFS_UPDATE, "false" },
		{ PROP_PILOT_FGFS_CONNECTOR, null, PROP_PILOT_FGFS_UPDATE },
		{ PROP_PILOT_FGFS_TYPE, null, PROP_PILOT_FGFS_UPDATE },
	};
	
	/**
	 * This variable is set to true, if and only if the configuration is OK.
	 */
	private boolean configOk = false;
	
	/**
	 * The type of physical plant. 
	 */
	private String plantType;
	
	/**
	 * The connection to the physical plant.
	 */
	private URI plantListener;
	
	/**
	 * True if the physical plant is simulated by the web pilot.
	 */
	private boolean plantSimulated;
	
	/**
	 * The type of location system.
	 */
	private String locationSystemType;
	
	/**
	 * The type of pilot.
	 */
	private String pilotType;
	
	/**
	 * The configured listener of the location system. 
	 */
	private URI locationSystemListener;
	
	/**
	 * The configured update rate of the location system.
	 */
	private int locationSystemUpdateRate;
	
	/**
	 * The configured home location of the plant. 
	 */
	private PolarCoordinate plantHomeLocation;
	
	/**
	 * The type of flight dynamics controller.
	 */
	private String controllerType;
	
	/**
	 * True if the pilot simulates the flight dynamics controller.
	 */
	private boolean controllerSimulated;
	
	/**
	 * The configured listener of the flight dynamics controller.
	 */
	private URI pilotControllerConnector;
	
	/**
	 * The name of the pilot.
	 */
	private String pilotName;
	
	/**
	 * True if the pilot sends update messages to the FlightGear flight simulator.
	 */
	private boolean fsfsUpdate;
	
	/**
	 * The configured listener of the FlightGear flight simulator.
	 */
	private URI fgfsConnector;
	
	/**
	 * The type of flight simulator.
	 */
	private FlightSimulatorType flightSimulatorType;
	
	/**
	 * The pilot URI map for the VCL interpreter.
	 */
	private Map<String, URI> pilotUriMap;
	
	/**
	 * Load a vehicle configuration from an <code>InputStream</code> and build it.
	 * 
	 * @param inStream the configuration's <code>InputStream</code>
	 * @throws IOException thrown in case of errors.
	 */
	public void loadConfig (InputStream inStream) throws IOException {
		conf.clear();
		conf.load(inStream);
		configErrors.clear();
		
		for (String[] entry : parameters) {
			String v = conf.getProperty(entry[0]);
			if (v == null && entry.length >= 2 && entry[1] != null)
				conf.setProperty(entry[0], entry[1]);
		}

		configOk = true;
		for (String[] entry : parameters) {
			String v = conf.getProperty(entry[0]);
			if (v == null && (entry.length != 3 || !"false".equals(conf.getProperty(entry[2],"false"))))
				configOk = false;
		}
		
		plantType = parseTemplate(PROP_PLANT_TYPE, PLANT_CONFIG_TEMPLATE_FORMAT);
		plantListener = parseURI(PROP_PLANT_LISTENER);
		plantSimulated = parseBool(PROP_PLANT_SIMULATED);
		
		locationSystemType = parseTemplate(PROP_PLANT_LOCATION_SYSTEM_TYPE, LOCATION_SYS_CONFIG_TEMPLATE_FORMAT);
		locationSystemListener = parseURI(PROP_PLANT_LOCATION_SYSTEM_LISTENER);   
		locationSystemUpdateRate = parseInt(PROP_PLANT_LOCATION_SYSTEM_UPDATE_RATE);
		plantHomeLocation = parsePolarCoordinate(PROP_PLANT_HOME_LOCATION);
		
		controllerType = parseTemplate(PROP_CONTROLLER_TYPE, CONTROLLER_CONFIG_TEMPLATE_FORMAT);
		controllerSimulated = parseBool(PROP_CONTROLLER_SIMULATED);
		
		pilotType = parseString(PROP_PILOT_TYPE);
		pilotControllerConnector = parseURI(PROP_PILOT_CONTROLLER_CONNECTOR);
		pilotName = parseString(PROP_PILOT_NAME);
		fsfsUpdate = parseBool(PROP_PILOT_FGFS_UPDATE);
		fgfsConnector = fsfsUpdate ? parseURI(PROP_PILOT_FGFS_CONNECTOR) : null;;
		flightSimulatorType = fsfsUpdate ? getFightSim(PROP_PILOT_FGFS_TYPE) : null;
		
		Properties sensorProperties = PropertyUtils.extract(PROP_SENSOR_PREFIX, conf);
		sensors.setWorkDir(workDir.getAbsolutePath());
		sensors.createSensors(sensorProperties);
		
		pilotUriMap = new TreeMap<String, URI>();
		String[] pilots = conf.getProperty(PROP_PILOT_LIST,"").split("\\s*,\\s*");
		for (String p : pilots) {
			if (p.isEmpty()) {
				continue;
			}
			String url = conf.getProperty(PROP_PILOT_PREFIX + p + PROP_PILOT_ULR_SUFFIX, "").trim();
			try {
				pilotUriMap.put(p, new URI(url));
			} catch (URISyntaxException e) {
				LOG.error("Invalid URI to pilot '" + p + "': " + url);
			}
		}
	}

	/**
	 * @param param the property to be parsed. 
	 * @return the parsed property as a <code>String</code> object.
	 */
	private String parseString (String param) {
		String p = conf.getProperty(param);
		if (p == null || "".equals(p.trim())) {
			configErrors.put(param, ERROR_MESSAGE_MISSING_VALUE);
			configOk = false;
		}
		return p;
	}
	
	/**
	 * @param param the property to be parsed.
	 * @param template the requested file name template. 
	 * @return the parsed property as a <code>String</code> object.
	 */
	private String parseTemplate (String param, String template) {
		String p =  parseString (param);
		String res = String.format(template, p);
		URL u = Thread.currentThread().getContextClassLoader().getResource(res);
		if (u == null) {
			configErrors.put(param, ERROR_MESSAGE_UNKNOWN_TYPE);
			configOk = false;
		}
		return p;
	}
	
	/**
	 * @param param the property to be parsed.
	 * @return the parsed property as an <code>URI</code> object.
	 */
	private URI parseURI (String param) {
		URI u = null;
		try {
			u = new URI(conf.getProperty(param));
			if (u.getScheme() == null || u.getHost() == null || u.getPort() <= 0) {
				configErrors.put(param, ERROR_MESSAGE_INVALID_VALUE);
				configOk = false;
			}
		} catch (Throwable e) {
			configErrors.put(param, ERROR_MESSAGE_MISSING_VALUE);
			configOk = false;
		}
		return u;
	}
	
	/**
	 * @param param the property to be parsed.
	 * @return the parsed property as a <code>Boolean</code> object.
	 */
	private Boolean parseBool (String param) {
		Boolean b = null;
		try {
			b = Boolean.parseBoolean(conf.getProperty(param));
		} catch (Throwable e) {
			configErrors.put(param, ERROR_MESSAGE_INVALID_VALUE);
			configOk = false;
		}
		return b;
	}
	
	/**
	 * @param param the property to be parsed.
	 * @return the parsed property as an <code>int</code>.
	 */
	private int parseInt (String param) {
		int i = -1;
		try {
			i = Integer.parseInt(conf.getProperty(param));
		} catch (Throwable e) {
			configErrors.put(param, ERROR_MESSAGE_INVALID_VALUE);
			configOk = false;
		}
		return i;
	}

	/**
	 * @param param the property to be parsed.
	 * @return the parsed property as a <code>PolarCoordinate</code>.
	 */
	private PolarCoordinate parsePolarCoordinate(String param) {
		double latitude = Double.parseDouble(conf.getProperty(param + "." + PROP_LOCATION_LATITUDE));
		double longitude = Double.parseDouble(conf.getProperty(param + "." + PROP_LOCATION_LONGITUDE));
		double altitude = Double.parseDouble(conf.getProperty(param + "." + PROP_LOCATION_ALTITUDE));
		return new PolarCoordinate(latitude, longitude, altitude);
	}
	
	/**
	 * @param param the property to be parsed.
	 * @return the parsed property as a <code>FlightSimulatorType</code> object.
	 */
	private FlightSimulatorType getFightSim(String param) {
		FlightSimulatorType t = FlightSimulatorType.NONE;
		String p = conf.getProperty(param);
		if (p == null || "".equals(p.trim())) {
			configErrors.put(param, ERROR_MESSAGE_MISSING_VALUE);
			configOk = false;
			return null;
		}
		try {
			t = FlightSimulatorType.valueOf(conf.getProperty(param));
		} catch (Throwable e) {
			configErrors.put(param, ERROR_MESSAGE_INVALID_VALUE);
			configOk = false;
		}
		return t;
	}
	
	/**
	 * @return the current configuration as a list of strings.
	 */
	public Properties getConfig () {
		return conf;
	}

	/**
	 * @return the errors of the current configuration as a map.  
	 */
	public Map<String,String> getConfigErrors() {
		return configErrors;
	}
	
	/**
	 * @return the names of all known parameters as a list of strings.
	 */
	public List<String> getParameterNames () {
		List<String> names = new ArrayList<String>();
		for (String[] entry : parameters)
			names.add(entry[0]);
		return names; 
	}
	
	/**
	 * @return returns true if the configuration is consistent.
	 */
	public boolean isConfigOk() {
		return configOk;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getPlantType()
	 */
	public String getPlantType() {
		return plantType;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getPlantListener()
	 */
	public URI getPlantListener() {
		return plantListener;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#isPlantSimulated()
	 */
	public boolean isPlantSimulated() {
		return plantSimulated;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getLocationSystemType()
	 */
	public String getLocationSystemType() {
		return locationSystemType;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getPilotType()
	 */
	public String getPilotType() {
		return pilotType;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getLocationSystemListener()
	 */
	public URI getLocationSystemListener() {
		return locationSystemListener;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getLocationSystemUpdateRate()
	 */
	public int getLocationSystemUpdateRate() {
		return locationSystemUpdateRate;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getPlantHomeLocation()
	 */
	public PolarCoordinate getPlantHomeLocation() {
		return plantHomeLocation;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getControllerType()
	 */
	public String getControllerType() {
		return controllerType;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#isControllerSimulated()
	 */
	public boolean isControllerSimulated() {
		return controllerSimulated;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getPilotControllerConnector()
	 */
	public URI getPilotControllerConnector() {
		return pilotControllerConnector;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getPilotName()
	 */
	public String getPilotName() {
		return pilotName;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#isFsfsUpdate()
	 */
	public boolean isFsfsUpdate() {
		return fsfsUpdate;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getFgfsConnector()
	 */
	public URI getFgfsConnector() {
		return fgfsConnector;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getFlightSimulatorType()
	 */
	public FlightSimulatorType getFlightSimulatorType() {
		return flightSimulatorType;
	}

	/**
	 * @return the currently configured list of sensors.
	 */
	public SensorBuilder getSensorBuilder() {
		return sensors;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration#getPilotUriMap()
	 */
	public Map<String, URI> getPilotUriMap() {
		return pilotUriMap;
	}
	
	/**
	 * @param workDir the directory to be used for temporary files.
	 */
	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}
	
	/**
	 * @param key the system property key of interest.
	 * @return the value of the required property.
	 */
	public String getSystemProperty(String key) {
		return System.getProperty(key);
	}

}
