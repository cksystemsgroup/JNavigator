/*
 * @(#) MockJaviatorMain.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2010  Clemens Krainer
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
package javiator.simulation;

import java.io.IOException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

public class MockJAviatorMain {
	
	public static final String PROP_SIMULATE_GPS_RECEIVER = "simulate.gps";
	public static final String PROP_SIMULATE_UBISENSE_RECEIVER = "simulate.ubisense";
	
	/**
	 * Main program
	 * 
	 * @throws IOException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws IOException, InstantiationException {
		
		Properties props = PropertyUtils.loadProperties("MockJAviator.properties");
		MockJAviator m = new MockJAviator(props);
		
		if (props.getProperty(PROP_SIMULATE_GPS_RECEIVER, "true").equals("true")) {
			GpsReceiverSimulatorAdapter grsa = new GpsReceiverSimulatorAdapter();
			new Thread(grsa).start();
			m.addDataTransferObjectListener(grsa, SensorData.class);
		}

		if (props.getProperty(PROP_SIMULATE_UBISENSE_RECEIVER, "false").equals("true")) {
			LocationMessageSimulatorAdapter lmsa = new LocationMessageSimulatorAdapter();
			new Thread(lmsa).start();
			m.addDataTransferObjectListener(lmsa, SensorData.class);
		}
		
		m.run();
	}
}
