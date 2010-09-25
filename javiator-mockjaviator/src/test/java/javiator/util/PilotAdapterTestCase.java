/*
 * @(#) PilotAdapterTestCase.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2008  Clemens Krainer
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

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.pilot.FlightControlData;
import at.uni_salzburg.cs.ckgroup.pilot.HardWareSensorData;
import at.uni_salzburg.cs.ckgroup.pilot.IPilot;
import at.uni_salzburg.cs.ckgroup.util.IClock;

/**
 * This test verifies the implementation of the <code>PilotAdapter</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class PilotAdapterTestCase extends TestCase {
	/**
	 * Verify if the <code>PilotAdapter</code> forwards a zero
	 * <code>SensorData</code> object correctly.
	 */
	public void testCase01() {

		MyMockPilot pilot = new MyMockPilot();
		MyCommandDataListener listener = new MyCommandDataListener();
		PilotAdapter pilotAdapter = new PilotAdapter(pilot, listener, 0);

		SensorData sensorData = new SensorData();
		sensorData.yaw = 0;
		sensorData.roll = 0;
		sensorData.pitch = 0;
		sensorData.z = 0;

		pilotAdapter.receive(sensorData);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(0, pilot.currentFlightControlData.yaw, 1E-9);
		assertEquals(0, pilot.currentFlightControlData.roll, 1E-9);
		assertEquals(0, pilot.currentFlightControlData.pitch, 1E-9);
		assertEquals(0, pilot.currentFlightControlData.heightAboveGround, 1E-9);

		assertNotNull(listener.currentCommandData);
		assertEquals(0, listener.currentCommandData.yaw, 1E-9);
		assertEquals(0, listener.currentCommandData.roll, 1E-9);
		assertEquals(0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(0, listener.currentCommandData.z, 1E-9);

	}

	/**
	 * Verify if the <code>PilotAdapter</code> forwards an arbitrary
	 * <code>SensorData</code> object correctly. Also verify if it correctly
	 * calculates intermediate results.
	 */
	public void testCase02() {

		MyMockPilot pilot = new MyMockPilot();
		MyCommandDataListener listener = new MyCommandDataListener();
		PilotAdapter pilotAdapter = new PilotAdapter(pilot, listener, 0);

		SensorData sensorData = new SensorData();
		sensorData.yaw = (short) 1570.796327;
		sensorData.roll = (short) 785.3901635;
		sensorData.pitch = (short) 392.6990818;
		sensorData.z = (short) 196.3495409;
		System.out.println("PilotAdapterTestCase.testCase02(): sensorData="
				+ sensorData.toString());

		pilotAdapter.receive(sensorData);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

	}

	/**
	 * Verify if the <code>PilotAdapter</code> if it correctly handles a
	 * report rate of 1.
	 */
	public void testCase03() {

		MyMockPilot pilot = new MyMockPilot();
		MyCommandDataListener listener = new MyCommandDataListener();
		PilotAdapter pilotAdapter = new PilotAdapter(pilot, listener, 1);

		SensorData sensorData1 = new SensorData();
		sensorData1.yaw = (short) 1570.796327;
		sensorData1.roll = (short) 785.3901635;
		sensorData1.pitch = (short) 392.6990818;
		sensorData1.z = (short) 196.3495409;
		System.out.println("PilotAdapterTestCase.testCase02(): sensorData="
				+ sensorData1.toString());

		SensorData sensorData2 = new SensorData();
		sensorData2.yaw = 0;
		sensorData2.roll = -1;
		sensorData2.pitch = -2;
		sensorData2.z = -3;
		System.out.println("PilotAdapterTestCase.testCase02(): sensorData="
				+ sensorData2.toString());

		pilotAdapter.receive(sensorData1);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData2);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData2);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(0.0, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(-0.05729577951308232, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(-0.11459155902616464, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(-0.003, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(0.0, listener.currentCommandData.yaw, 1E-9);
		assertEquals(-1.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(-2.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(-3.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData1);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(0.0, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(-0.05729577951308232, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(-0.11459155902616464, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(-0.003, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(0.0, listener.currentCommandData.yaw, 1E-9);
		assertEquals(-1.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(-2.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(-3.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData1);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

	}

	/**
	 * Verify if the <code>PilotAdapter</code> if it correctly handles a
	 * report rate of 2.
	 */
	public void testCase04() {

		MyMockPilot pilot = new MyMockPilot();
		MyCommandDataListener listener = new MyCommandDataListener();
		PilotAdapter pilotAdapter = new PilotAdapter(pilot, listener, 2);

		SensorData sensorData1 = new SensorData();
		sensorData1.yaw = (short) 1570.796327;
		sensorData1.roll = (short) 785.3901635;
		sensorData1.pitch = (short) 392.6990818;
		sensorData1.z = (short) 196.3495409;
		System.out.println("PilotAdapterTestCase.testCase02(): sensorData="
				+ sensorData1.toString());

		SensorData sensorData2 = new SensorData();
		sensorData2.yaw = 0;
		sensorData2.roll = -1;
		sensorData2.pitch = -2;
		sensorData2.z = -3;
		System.out.println("PilotAdapterTestCase.testCase02(): sensorData="
				+ sensorData2.toString());

		pilotAdapter.receive(sensorData1);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData2);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData2);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData2);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(0.0, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(-0.05729577951308232, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(-0.11459155902616464, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(-0.003, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(0.0, listener.currentCommandData.yaw, 1E-9);
		assertEquals(-1.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(-2.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(-3.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData1);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(0.0, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(-0.05729577951308232, pilot.currentFlightControlData.roll,
				1E-3);
		assertEquals(-0.11459155902616464,
				pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(-0.003, pilot.currentFlightControlData.heightAboveGround,
				1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(0.0, listener.currentCommandData.yaw, 1E-9);
		assertEquals(-1.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(-2.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(-3.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData1);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(0.0, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(-0.05729577951308232, pilot.currentFlightControlData.roll,
				1E-3);
		assertEquals(-0.11459155902616464,
				pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(-0.003, pilot.currentFlightControlData.heightAboveGround,
				1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(0.0, listener.currentCommandData.yaw, 1E-9);
		assertEquals(-1.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(-2.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(-3.0, listener.currentCommandData.z, 1E-9);

		pilotAdapter.receive(sensorData1);

		assertNotNull(pilot.currentFlightControlData);
		assertEquals(89.95437383553924, pilot.currentFlightControlData.yaw, 1E-7);
		assertEquals(44.97718691776962, pilot.currentFlightControlData.roll, 1E-3);
		assertEquals(22.45994556912827, pilot.currentFlightControlData.pitch, 1E-3);
		assertEquals(0.196, pilot.currentFlightControlData.heightAboveGround, 1E-7);

		assertNotNull(listener.currentCommandData);
		assertEquals(1570, listener.currentCommandData.yaw, 1E-9);
		assertEquals(785.0, listener.currentCommandData.roll, 1E-9);
		assertEquals(392.0, listener.currentCommandData.pitch, 1E-9);
		assertEquals(196.0, listener.currentCommandData.z, 1E-9);

	}

	/**
	 * The mock pilot implementation for this <code>PilotAdapter</code> tests.
	 * @author  Clemens Krainer
	 */
	private class MyMockPilot implements IPilot {
		/**
		 * @uml.property  name="currentFlightControlData"
		 * @uml.associationEnd  
		 */
		public FlightControlData currentFlightControlData = null;

		public FlightControlData processSensorData(HardWareSensorData sensorData) {
			currentFlightControlData = new FlightControlData(sensorData.yaw,
					sensorData.roll, sensorData.pitch,
					sensorData.heightAboveGround);
			return currentFlightControlData;
		}

		//		public void addPilotInterceptor (IPilotInterceptor pilotInterceptor)	{ throw new UnsupportedOperationException (); }
		//		public void removePilotInterceptor (IPilotInterceptor pilotInterceptor)	{ throw new UnsupportedOperationException (); }
		public void setCourseSupplier(ISetCourseSupplier setCourseSupplier) {
			throw new UnsupportedOperationException();
		}

		public void setPositionProvider(IPositionProvider positionProvider) {
			throw new UnsupportedOperationException();
		}

		public void startFlyingSetCourse() {
			throw new UnsupportedOperationException();
		}

		public void stopFlyingSetCourse() {
			throw new UnsupportedOperationException();
		}

		public boolean isFlyingSetCourse() {
			throw new UnsupportedOperationException();
		}

		public void setClock(IClock clock) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * This class implements a navigation data listener. It is this implementation that receives the data from the <code>PilotAdapter</code> to be verified in the tests above.
	 * @author  Clemens Krainer
	 */
	private class MyCommandDataListener implements ICommandDataListener {
		/**
		 * @uml.property  name="currentCommandData"
		 * @uml.associationEnd  
		 */
		public CommandData currentCommandData = null;

		public void receive(CommandData navigationData) {
			currentCommandData = navigationData;
		}

		public void setProcessData(boolean processData) {
			// intentionally empty
		}
	}
}
