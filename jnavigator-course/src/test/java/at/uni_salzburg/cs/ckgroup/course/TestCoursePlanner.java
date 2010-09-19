/*
 * @(#) TestCoursePlanner.java
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
package at.uni_salzburg.cs.ckgroup.course;

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;

public class TestCoursePlanner implements ICoursePlanner {
	
	public TestCoursePlanner (Properties props) {
		// Intentionally empty.
	}

	public SectionFlightPlan[] planCourse(Section[] sections, IGeodeticSystem geodeticSystem) throws ConfigurationException {
		
		SectionFlightPlan[] fp = new SectionFlightPlan[sections.length];
		for (int k=0; k < sections.length; k++) {
			fp[k] = new SectionFlightPlan (sections[k]);
			CartesianCoordinate startPosition = geodeticSystem.polarToRectangularCoordinates (sections[k].getStartPosition());
			
			double distance = 0;
			
			if (sections[k].getEndPosition() != null) {
				CartesianCoordinate endPosition = geodeticSystem.polarToRectangularCoordinates (sections[k].getEndPosition());
				distance = endPosition.subtract(startPosition).norm();
			}
			
			long durations[] = new long[] { sections[k].getTravelTime() };
			double averageSpeed = distance / durations[0];
			double velocities[] = new double[] { averageSpeed, averageSpeed };
			fp[k].setValues(durations, velocities);
		}
		
		return fp;
	}

}
