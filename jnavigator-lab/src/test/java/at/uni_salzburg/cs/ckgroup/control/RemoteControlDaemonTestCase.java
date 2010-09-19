/*
 * @(#) RemoteControlDaemonTestCase.java
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
import java.io.OutputStream;
import java.util.Properties;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * These tests verify the implementation of the <code>RemoteControlDaemon</code> class.
 * 
 * @author Clemens Krainer
 */
public class RemoteControlDaemonTestCase extends TestCase {

	private Properties props;
	
	public void setUp () {
		props = new Properties ();
		props.setProperty(RemoteControlDaemon.PROP_PORT, "3456");
		props.setProperty("remote.control.className", "at.uni_salzburg.cs.ckgroup.io.TcpSocket");
		props.setProperty("remote.control.host", "127.0.0.1");
		props.setProperty("remote.control.port", "3456");
	}
	
	private void writeToRemoteControl (OutputStream rc, double roll, double pitch, double yaw, double thrust) throws IOException {
		byte[] msg = new byte[6];
		msg[0] = (byte) 0x02;
		msg[1] = (byte) yaw;
		msg[2] = (byte) thrust;
		msg[3] = (byte) pitch;
		msg[4] = (byte) roll;
		msg[5] = (byte) (msg[0] ^ msg[1] ^ msg[2] ^ msg[3] ^ msg[4]);
		rc.write (msg);
	}
	
	/**
	 * Verify the normal operation of the <code>RemoteControlDaemon</code>.
	 */
	public void testCase01 () {
		double[][] data = {
				{0, 0, 0, 0}, {10, 20, 30, 40}, {50, 60, 70, 80}, 
				{90, 100, 110, 120}, {130, 140, 150, 160}, {180, 190, 200, 210},
				{220, 230, 240, 250}
			};
		
		try {
			RemoteControlDaemon rc = new RemoteControlDaemon (props);
			MyRemoteControl remoteControl = new MyRemoteControl ();
			rc.setRemoteControl (remoteControl);
			rc.startRemoteControlThread ();

			IConnection remoteControlConnection = (IConnection) ObjectFactory.getInstance ().instantiateObject ("remote.control.", IConnection.class, props);
			OutputStream remoteControlOutputStream = remoteControlConnection.getOutputStream();
			
			for (int k=0; k < data.length; k++) {
				double roll = data [k][0];
				double pitch = data [k][1];
				double yaw = data [k][2];
				double thrust = data [k][3];
				
				writeToRemoteControl (remoteControlOutputStream, roll, pitch, yaw, thrust);
				try { Thread.sleep(100); } catch (Exception e) {}
				
				double _roll = (roll - 0x80) / 31.75;
				double _pitch = (pitch - 0x80) / 31.75;
				double _yaw = (yaw - 0x80) / 0.706;
				double _thrust = (thrust - 0x80) / 620.0;
				
				assertEquals ("roll:   index "+k, _roll,   remoteControl.roll,   1E-2);
				assertEquals ("pitch:  index "+k, _pitch,  remoteControl.pitch,  1E-2);
				assertEquals ("yaw:    index "+k, _yaw,    remoteControl.yaw,    1E-2);
				assertEquals ("thrust: index "+k, _thrust, remoteControl.thrust, 1E-2);
			}

			remoteControlConnection.close();
			rc.stopRemoteControlThread ();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This test verifies, that the
	 * <code>RemoteControlDaemon.startRemoteControlThread()</code> method throws
	 * an <code>IllegalStateException</code> if the demon already runs.
	 */
	public void testCase02 () {
		RemoteControlDaemon rc = null;
		
		try {
			rc = new RemoteControlDaemon (props);
			MyRemoteControl remoteControl = new MyRemoteControl ();
			rc.setRemoteControl (remoteControl);
			rc.startRemoteControlThread ();
			rc.startRemoteControlThread ();
		} catch (IllegalStateException e) {
			assertEquals ("Remote control service is already running.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
		
		rc.stopRemoteControlThread ();
	}
	
	/**
	 * This test verifies, that the
	 * <code>RemoteControlDaemon.stopRemoteControlThread()</code> method throws
	 * an <code>IllegalStateException</code> if the demon does not run.
	 */
	public void testCase03 () {
		RemoteControlDaemon rc = null;
		
		try {
			rc = new RemoteControlDaemon (props);
			MyRemoteControl remoteControl = new MyRemoteControl ();
			rc.setRemoteControl (remoteControl);
			rc.stopRemoteControlThread ();
		} catch (IllegalStateException e) {
			assertEquals ("Remote control service thread does not run.", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify that the <code>RemoteControlDaemon</code> implementation ignores
	 * control commands until an implementation of the
	 * <code>IRemoteControl</code> interface is disclosed to the daemon.
	 */
	public void testCase04 () {
		double[][] data = { {0, 0, 0, 0} };
		
		try {
			RemoteControlDaemon rc = new RemoteControlDaemon (props);
			rc.startRemoteControlThread ();

			IConnection remoteControlConnection = (IConnection) ObjectFactory.getInstance ().instantiateObject ("remote.control.", IConnection.class, props);
			OutputStream remoteControlOutputStream = remoteControlConnection.getOutputStream();
			
			for (int k=0; k < data.length; k++) {
				double roll = data [k][0];
				double pitch = data [k][1];
				double yaw = data [k][2];
				double thrust = data [k][3];
				
				writeToRemoteControl (remoteControlOutputStream, roll, pitch, yaw, thrust);
				try { Thread.sleep(100); } catch (Exception e) {}

			}

			rc.stopRemoteControlThread ();
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify the normal operation of the <code>RemoteControlDaemon</code>.
	 */
	public void testCase05 () {
		props.setProperty(RemoteControlDaemon.PROP_PORT, "1023");
		
		try {
			RemoteControlDaemon rc = new RemoteControlDaemon (props);
			assertNull (rc);
		} catch (ConfigurationException e) {
			assertEquals ("Property " + RemoteControlDaemon.PROP_PORT + " must be greater than 1023", e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * This dummy class implements the <code>IRemoteControl</code> interface to
	 * allow unit testing the <code>RemoteControlDaemon</code> class.
	 * 
	 * @author Clemens Krainer
	 */
	private class MyRemoteControl implements IRemoteControl {
		
		public double pitch = -1000;
		public double roll = -1000;
		public double yaw = -1000;
		public double thrust = -1000;
		
		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setPitch(double)
		 */
		public void setPitch (double pitch) throws IOException {
			this.pitch = pitch;
		}

		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setRoll(double)
		 */
		public void setRoll (double roll) throws IOException {
			this.roll = roll;
		}

		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setYaw(double)
		 */
		public void setYaw (double yaw) throws IOException {
			this.yaw = yaw;
		}
		
		/* (non-Javadoc)
		 * @see at.uni_salzburg.cs.ckgroup.control.IRemoteControl#setThrust(double)
		 */
		public void setThrust (double thrust) throws IOException {
			this.thrust = thrust;
		}
	}
}
