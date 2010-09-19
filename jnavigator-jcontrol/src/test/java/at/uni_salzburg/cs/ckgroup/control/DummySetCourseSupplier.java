/*
 * @(#) DummySetCourseSupplier.java
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

import java.io.IOException;
import java.io.InputStream;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.course.VehicleStatus;
import at.uni_salzburg.cs.ckgroup.course.WGS84;

public class DummySetCourseSupplier implements ISetCourseSupplier {
	
	public IGeodeticSystem geodeticSystem = new WGS84();
	public VehicleStatus[] setCourseData = null;
	public VehicleStatus setCoursePosition = null;
	public long[] timeTable = null;
	public InputStream courseData = null;
	public boolean fakeIOException = false;
	public boolean fakeConfigurationException = false;

	public IGeodeticSystem getGeodeticSystem() {
		return geodeticSystem;
	}

	public VehicleStatus[] getSetCourseData() {
		return setCourseData;
	}

	public VehicleStatus getSetCoursePosition(long time) {
		return setCoursePosition;
	}

	public long[] getTimeTable() {
		return timeTable;
	}

	public void loadSetCourse(InputStream courseData)
			throws ConfigurationException, IOException {
		if (fakeIOException)
			throw new IOException ("Intentionally thrown Exception.");
		if (fakeConfigurationException)
			throw new ConfigurationException ("Intentionally thrown Exception.");
		this.courseData = courseData;
	}

}
