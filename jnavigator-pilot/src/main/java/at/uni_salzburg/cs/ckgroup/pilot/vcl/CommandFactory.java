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
			
		} else {
			
		}
		
		return null;
	}

}
