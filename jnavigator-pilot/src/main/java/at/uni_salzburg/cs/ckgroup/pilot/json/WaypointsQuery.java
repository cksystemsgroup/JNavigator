/*
 * @(#) WaypointsQuery.java
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
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;

import at.uni_salzburg.cs.ckgroup.pilot.IServletConfig;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.CommandFlyToAbs;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.CommandFlyToAbsOld;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.CommandJumpToAbs;
import at.uni_salzburg.cs.ckgroup.pilot.vcl.ICommand;

public class WaypointsQuery implements IJsonQuery {

	public String execute(IServletConfig config) {
		
		
		int line = config.getAviator().getCurrentVclCommandLine();
		List<ICommand> commandList = config.getAviator().getVclSctipt();

		JSONArray list = new JSONArray();

		int counter = 0;
		for (ICommand command : commandList) {
			++counter;
			Map<String,Object> obj = new LinkedHashMap<String,Object>();
			
			if (command instanceof CommandFlyToAbs) {
				CommandFlyToAbs o = (CommandFlyToAbs)command;
				obj.put("longitude", o.getCoordinate().getLongitude());
				obj.put("latitude", o.getCoordinate().getLatitude());
				obj.put("altitude", o.getCoordinate().getAltitude());
				obj.put("precision", o.getPrecision());
				obj.put("velocity", o.getVelocity());
				
			} else if (command instanceof CommandFlyToAbsOld) {
				CommandFlyToAbsOld o = (CommandFlyToAbsOld)command;
				obj.put("longitude", o.getCoordinate().getLongitude());
				obj.put("latitude", o.getCoordinate().getLatitude());
				obj.put("altitude", o.getCoordinate().getAltitude());
				obj.put("precision", o.getPrecision());
				obj.put("velocity", o.getVelocity());
				
			} else if (command instanceof CommandJumpToAbs) {
				CommandJumpToAbs o = (CommandJumpToAbs)command;
				obj.put("longitude", o.getCoordinate().getLongitude());
				obj.put("latitude", o.getCoordinate().getLatitude());
				obj.put("altitude", o.getCoordinate().getAltitude());
				obj.put("precision", o.getPrecision());
				
			} else {
				continue;
			}
			
			obj.put("current", Boolean.valueOf(counter == line));
			list.add(obj);
		}
		
		return list.toJSONString();
	}

}
