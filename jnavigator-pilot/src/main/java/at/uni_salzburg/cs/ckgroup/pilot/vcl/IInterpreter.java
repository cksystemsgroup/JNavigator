/*
 * @(#) IInterpreter.java
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

import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.pilot.IAutoPilot;
import at.uni_salzburg.cs.ckgroup.pilot.config.IConfiguration;

public interface IInterpreter {

	public IPositionProvider getPositionProvider();

	public IConfiguration getConfiguration();
	
	public IAutoPilot getAutoPilot();
	
	public boolean isClearanceForTakeOffGranted();
	
	public void switchToManualMode();
	
//	public void switchToAutomaticMode();
	
	public void setSetCoursePosition(PolarCoordinate coordinate);
	
	public PolarCoordinate getCurrentPosition();
	
	public IGeodeticSystem getGeodeticSystem();
	
	public String getStatusData ();
}
