/*
 * @(#) PositionQuery.java
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
package at.uni_salzburg.cs.ckgroup.pilot.json;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.pilot.IServletConfig;

public class PositionQuery implements IJsonQuery {

	public String execute(IServletConfig config) {

		PolarCoordinate pos = config.getVehicleBuilder().getPositionProvider().getCurrentPosition();
		Double courseOverGround = config.getVehicleBuilder().getPositionProvider().getCourseOverGround();
		Double speedOverGround = config.getVehicleBuilder().getPositionProvider().getSpeedOverGround();
		Double altitudeOverGround = config.getVehicleBuilder().getAutoPilot().getAltitudeOverGround();

		if (pos == null)
			return "";
		
		Map<String, Object> obj=new LinkedHashMap<String, Object>();
		obj.put("latitude", pos.getLatitude());
		obj.put("longitude", pos.getLongitude());
		obj.put("altitude", pos.getAltitude());
		obj.put("courseOverGround", courseOverGround);
		obj.put("speedOverGround", speedOverGround);
		obj.put("altitudeOverGround", altitudeOverGround);
		obj.put("autoPilotFlight", Boolean.valueOf(config.getVehicleBuilder().getAutoPilot().isAutoPilotFlight()));
		
		return JSONValue.toJSONString(obj);
	}

}
