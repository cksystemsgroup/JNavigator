/*
 * @(#) IVehicleBuilder.java
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
package at.uni_salzburg.cs.ckgroup.pilot;

import java.io.File;
import java.io.IOException;

import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.pilot.config.Configuration;

public interface IVehicleBuilder {
	
	/**
	 * @param configuration the current configuration.
	 * @throws IOException thrown in case of I/O errors.
	 */
	public void setConfig (Configuration configuration) throws IOException;
	
	/**
	 * @return the currently used position provider.
	 */
	public IPositionProvider getPositionProvider();
	
	/**
	 * @return the currently used autopilot.
	 */
	public IAutoPilot getAutoPilot();
	
	/**
	 * Destroy the currently configured vehicle.
	 */
	public void destroy();
	
	/**
	 * @param workDir the directory to be used for temporary files.
	 */
	void setWorkDir(File workDir);
	
	/**
	 * @param setCourseSupplier the set-course supplier.
	 */
	void setSetCourseSupplier(ISetCourseSupplier setCourseSupplier);
	
}
