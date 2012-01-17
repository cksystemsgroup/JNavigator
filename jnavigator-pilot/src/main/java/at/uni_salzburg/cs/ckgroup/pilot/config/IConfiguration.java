/*
 * @(#) IConfiguration.java
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

import java.net.URI;
import java.util.Map;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;

public interface IConfiguration {

	/**
	 * @return the plant type as a <code>String</code> object.
	 */
	public String getPlantType();

	/**
	 * @return the plant listener as an <code>URI</code> object.
	 */
	public URI getPlantListener();
	
	/**
	 * @return true if the plant is simulated by the pilot web application, false otherwise.
	 */
	public boolean isPlantSimulated();

	/**
	 * @return the type of the location system as a <code>LocationSystemType</code> object.
	 */
	public String getLocationSystemType();

	/**
	 * @return the type of the location system as a <code>LocationSystemType</code> object.
	 */
	public String getPilotType();

	/**
	 * @return the location system listener as an <code>URI</code> object.
	 */
	public URI getLocationSystemListener();

	/**
	 * @return the location system update rate as an integer value.
	 */
	public int getLocationSystemUpdateRate();
	
	/**
	 * @return the plant's home location.
	 */
	public PolarCoordinate getPlantHomeLocation();
	
	/**
	 * @return the controller type as a <code>String</code> object.
	 */
	public String getControllerType();

	/**
	 * @return true if the controller is simulated by the pilot web application, false otherwise.
	 */
	public boolean isControllerSimulated();

	/**
	 * @return the pilot connection to the controller as an <code>URI</code> object.
	 */
	public URI getPilotControllerConnector();

	/**
	 * @return the pilot name.
	 */
	public String getPilotName();
	
	/**
	 * @return the current pilot URI map.
	 */
	public Map<String, URI> getPilotUriMap(); 

	/**
	 * @return true if the pilot should update the FlightGear flight simulator.
	 */
	public boolean isFsfsUpdate();

	/**
	 * @return the pilot connection to the FlightGear flight simulator as an <code>URI</code> object.
	 */
	public URI getFgfsConnector();

	/**
	 * @return the FlightGear flight simulator type as an <code>FlightSimulatorType</code> object.
	 */
	public FlightSimulatorType getFlightSimulatorType();
}
