/*
 * @(#) MockPilot.java
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
package at.uni_salzburg.cs.ckgroup.pilot;

import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.util.IClock;

/**
 * This class implements a simple mock pilot. Its main purpose is to help
 * testing the <code>PilotBuilder</code>.
 * 
 * @author Clemens Krainer
 */
public class MockPilot implements IPilot
{
//	public Vector interceptors = new Vector ();
	public ISetCourseSupplier setCourseSupplier;
	public IPositionProvider positionProvider;
	public boolean isFlyingSetCourse = false;
	public String name;
	
	/**
	 * Construct a <code>MockPilot</code>.
	 * 
	 * @param props the <code>Properties</code> for this pilot.
	 */
	public MockPilot (Properties props) {
		name = props.getProperty ("name","Not Named MockPilot");
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#addPilotInterceptor(at.uni_salzburg.cs.ckgroup.pilot.IPilotInterceptor)
	 */
//	public void addPilotInterceptor (IPilotInterceptor pilotInterceptor)
//	{
//		if (pilotInterceptor != null)
//			interceptors.add (pilotInterceptor);
//	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#removePilotInterceptor(at.uni_salzburg.cs.ckgroup.pilot.IPilotInterceptor)
	 */
//	public void removePilotInterceptor (IPilotInterceptor pilotInterceptor)
//	{
//		while (interceptors.remove (pilotInterceptor))
//			continue;
//	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#setCourseSupplier(at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier)
	 */
	public void setCourseSupplier (ISetCourseSupplier setCourseSupplier)
	{
		this.setCourseSupplier = setCourseSupplier;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#setPositionProvider(at.uni_salzburg.cs.ckgroup.course.IPositionProvider)
	 */
	public void setPositionProvider (IPositionProvider positionProvider)
	{
		this.positionProvider = positionProvider;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#startFlyingSetCourse()
	 */
	public void startFlyingSetCourse ()
	{
		isFlyingSetCourse = true;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#stopFlyingSetCourse()
	 */
	public void stopFlyingSetCourse ()
	{
		isFlyingSetCourse = false;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#isFlyingSetCourse()
	 */
	public boolean isFlyingSetCourse()
	{
		return isFlyingSetCourse;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#processSensorData(at.uni_salzburg.cs.ckgroup.pilot.HardWareSensorData)
	 */
	public FlightControlData processSensorData (HardWareSensorData sensorData)
	{
		FlightControlData f = new FlightControlData (sensorData.yaw, sensorData.roll, sensorData.pitch, sensorData.heightAboveGround);
		return f;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IPilot#setClock(at.uni_salzburg.cs.ckgroup.util.IClock)
	 */
	public void setClock(IClock clock) {
		// Intentionally empty.
	}

}
