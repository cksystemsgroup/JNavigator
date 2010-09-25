/*
 * @(#) PilotAdapter.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2007  Clemens Krainer
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
package javiator.util;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.pilot.FlightControlData;
import at.uni_salzburg.cs.ckgroup.pilot.HardWareSensorData;
import at.uni_salzburg.cs.ckgroup.pilot.IPilot;

/**
 * This class implements the adapter between the JNavigation pilot and the <code>JControl</code> main loop. It forwards <code>SensorData</code> from the JAviator inertial sensors to the pilot and routes new flight control data to the <code>JControl</code> main loop.
 * @author  Clemens Krainer
 */
public class PilotAdapter implements ISensorDataListener, IPacketListener
{
	/**
	 * Conversion factor to make degrees from milli radiants.
	 */
	private static final double TO_DEGREES = 0.18 / Math.PI;
	
	/**
	 * Conversion factor to make milli radiants from degrees.
	 */
	private static final double TO_MILLI_RADIANTS = Math.PI / 0.18;

	/**
	 * The associated pilot implementation.
	 * @uml.property  name="pilot"
	 * @uml.associationEnd  
	 */
	private IPilot pilot;
	
	/**
	 * The listener for new navigation data.
	 * @uml.property  name="navigationDataListener"
	 * @uml.associationEnd  
	 */
	private ICommandDataListener commandDataListener;
	
	/**
	 * Number of periods to wait before reporting the <code>SensorData</code>
	 * to the auto pilot.
	 */
	private int reportRate;
	
	/**
	 * Counter to handle the report rate.
	 */
	private int counter = 0;
	
	/**
	 * Construct a pilot adapter.
	 * 
	 * @param pilot the associated pilot.
	 */
	public PilotAdapter (IPilot pilot, ICommandDataListener commandDataListener, int reportRate) {
		this.pilot = pilot;
		this.commandDataListener = commandDataListener;
		this.reportRate = reportRate;
	}
	
	/* (non-Javadoc)
	 * @see javiator.util.ISensorDataListener#receive(javiator.util.SensorData)
	 */
	public void receive (SensorData sensorData)
	{
		if (counter-- > 0)
			return;
		counter = reportRate;
//		System.out.println ("PilotAdapter#receive: sensorData: " + sensorData.toString());
		HardWareSensorData hwSensorData = new HardWareSensorData (sensorData.yaw*TO_DEGREES, sensorData.roll*TO_DEGREES, sensorData.pitch*TO_DEGREES, sensorData.z/1000.0);
		FlightControlData flightControlData = pilot.processSensorData (hwSensorData);
		CommandData navigationData = new CommandData ();
		navigationData.yaw = (short)(flightControlData.yaw*TO_MILLI_RADIANTS);
		navigationData.pitch = (short)(flightControlData.pitch*TO_MILLI_RADIANTS);
		navigationData.roll = (short)(flightControlData.roll*TO_MILLI_RADIANTS);
		navigationData.z = (short)(flightControlData.heightAboveGround*1000.0);
//		System.out.println ("PilotAdapter#receive: navigationData: " + navigationData.toString() + ", flying set course: " + pilot.isFlyingSetCourse());
		commandDataListener.receive (navigationData);
	}

	/* (non-Javadoc)
	 * @see javiator.util.IPacketListener#receive(javiator.util.Packet)
	 */
	public boolean receive(Packet packet)
	{
		System.out.println ("IPacketListener#receive: packet " + packet.type + " " + new String (packet.payload));
		if (packet.type != PacketType.COMM_PILOT_DATA)
			return false;
			
		String payload = new String (packet.payload);
		String[] msg = payload.split (",");
		if (msg[0].equals("CMD")) {
			if (msg[1].equals("AUTOPILOT START"))
			{
				try {
					pilot.startFlyingSetCourse();
				} catch (ConfigurationException e) {
					System.out.println ("javiator.util.IPacketListener#receive: can not execute command " + msg[1]);
					e.printStackTrace ();
				}
				commandDataListener.setProcessData (true);
			}
			else if (msg[1].equals("AUTOPILOT STOP"))
			{
				pilot.stopFlyingSetCourse();
				commandDataListener.setProcessData (false);
			}
			else
				System.out.println ("javiator.util.IPacketListener#receive: unknown command '" + payload + "'");
		}
		else
			System.out.println ("javiator.util.IPacketListener#receive: unknown payload '" + payload + "'");
		return false;
	}

}
