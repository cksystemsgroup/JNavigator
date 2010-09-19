/*
 * @(#) DummyAlgorithm.java
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
package at.uni_salzburg.cs.ckgroup.control;

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.TrimValues;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;

public class DummyAlgorithm implements IControlAlgorithm {
	
	public static DummyAlgorithm instance = null;
	
	public SensorData sensorData = null;
	public CommandData navigationData = null;
	public MotorSignals motorSignals = null;
	public VehicleStatus setCourseData = null;
	public PolarCoordinate currentPosition = null;
	public Double courseOverGround = null;
	public Double speedOverGround = null;
	public TrimValues trimValues = null;

	public DummyAlgorithm (Properties props) {
		instance = this;
		System.out.println ("Creating DummyAlgorithm");
	}
	
	public MotorSignals apply(SensorData sensorData,
			CommandData navigationData) {
		
		this.sensorData = sensorData;
		this.navigationData = navigationData;
		return motorSignals;
	}

	public MotorSignals apply(SensorData sensorData,
			VehicleStatus setCourseData, PolarCoordinate currentPosition,
			Double courseOverGround, Double speedOverGround) {
		
		this.sensorData = sensorData;
		this.setCourseData = setCourseData;
		this.currentPosition = currentPosition;
		this.courseOverGround = courseOverGround;
		this.speedOverGround = speedOverGround;

		return motorSignals;
	}

	public void setTrimValues(TrimValues trimValues) {

		this.trimValues = trimValues;
	}

}
