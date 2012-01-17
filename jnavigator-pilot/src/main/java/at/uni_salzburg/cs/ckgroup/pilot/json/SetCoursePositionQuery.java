/*
 * @(#) SensorQuery.java
 *
 * This code is part of the JNavigator project.
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
package at.uni_salzburg.cs.ckgroup.pilot.json;

import org.json.simple.JSONObject;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.pilot.IAutoPilot;
import at.uni_salzburg.cs.ckgroup.pilot.IServletConfig;

public class SetCoursePositionQuery implements IJsonQuery {

	@SuppressWarnings("unchecked")
	public String execute(IServletConfig config) {
		
		JSONObject obj = new JSONObject();
		
		IAutoPilot autopilot = config.getVehicleBuilder().getAutoPilot();
		if (autopilot != null && autopilot.getSetCourseSupplier() != null) {
			VehicleStatus status = config.getVehicleBuilder().getAutoPilot().getSetCourseSupplier().getSetCoursePosition(0);
			PolarCoordinate setCoursePosition = status.position;
			if (setCoursePosition != null) {
				obj.put("latitude", setCoursePosition.getLatitude());
				obj.put("longitude", setCoursePosition.getLongitude());
				obj.put("altitude", setCoursePosition.getAltitude());
			}
			obj.put("autoPilotFlight", Boolean.valueOf(config.getVehicleBuilder().getAutoPilot().isAutoPilotFlight()));
		}
		
		return obj.toJSONString();
	}

}
