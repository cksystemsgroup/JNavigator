/*
 * @(#) CommandFactory.java
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
package at.uni_salzburg.cs.ckgroup.pilot.vcl;

import org.json.simple.JSONObject;

public class CommandFactory {
	
	public static ICommand build (Class<?> cmdClass, String[] cmdParams) {
		
		if (cmdClass == CommandGoAuto.class) {
			return new CommandGoAuto();
			
		} else if (cmdClass == CommandGoManual.class) {
			return new CommandGoManual();
			
		} else if (cmdClass == CommandFlyToAbs.class) {
			return new CommandFlyToAbs(
				Double.parseDouble(cmdParams[0]),
				Double.parseDouble(cmdParams[1]),
				Double.parseDouble(cmdParams[2]),
				Double.parseDouble(cmdParams[3]),
				Double.parseDouble(cmdParams[4])
			);
			
		} else if (cmdClass == CommandFlyToAbsOld.class) {
			return new CommandFlyToAbs(
				Double.parseDouble(cmdParams[0]),
				Double.parseDouble(cmdParams[1]),
				Double.parseDouble(cmdParams[2]),
				Double.parseDouble(cmdParams[3]),
				Double.parseDouble(cmdParams[4])
			);
			
		} else if (cmdClass == CommandJumpToAbs.class) {
			return new CommandJumpToAbs(
				Double.parseDouble(cmdParams[0]),
				Double.parseDouble(cmdParams[1]),
				Double.parseDouble(cmdParams[2]),
				Double.parseDouble(cmdParams[3])
			);
			
		} else if (cmdClass == CommandHover.class) {
			return new CommandHover(Long.parseLong(cmdParams[0]));
			
		} else if (cmdClass == CommandLand.class) {
			return new CommandLand();
			
		} else if (cmdClass == CommandTakeOff.class) {
			return new CommandTakeOff(
					Double.parseDouble(cmdParams[0]),
					Long.parseLong(cmdParams[1])
				);
			
		} else if (cmdClass == CommandWaitForGo.class) {
			return new CommandWaitForGo();
			
		} else if (cmdClass == CommandNoop.class) {
			return new CommandNoop();
			
		} else if (cmdClass == CommandFollowDistance.class){
			return new CommandFollowDistance(
				cmdParams[0],
				Double.parseDouble(cmdParams[1]),
				Double.parseDouble(cmdParams[2]),
				Double.parseDouble(cmdParams[3])
			);
		}
		
		return null;
	}

	public static ICommand build(JSONObject obj) {
		
		if ("takeOff".equals(obj.get("cmd"))) {
			Object a = obj.get("altitude");
			Double alt = a != null ? (Double)a : 1.0;
			return new CommandTakeOff(
				alt < 1.0 ? 1.0 : alt,
				(Long)obj.get("time")
			);
		}
			
		if ("flyTo".equals(obj.get("cmd"))) {
			return new CommandFlyToAbs(
				(Double)obj.get("latitude"),
				(Double)obj.get("longitude"),
				(Double)obj.get("altitude"),
				(Double)obj.get("precision"),
				(Double)obj.get("velocity")
			);
		}

		if ("hover".equals(obj.get("cmd"))) {
			return new CommandHover((Long)obj.get("time"));
		}
		
		if ("land".equals(obj.get("cmd"))) {
			return new CommandLand();
		}
		
		return null;
	}
	
}
