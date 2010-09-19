/*
 * @(#) PilotDataTestCase.java
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
package at.uni_salzburg.cs.ckgroup.communication.data;

import junit.framework.TestCase;

/**
 * This test case verifies the implementation of the <code>PilotData</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class PilotDataTestCase extends TestCase {

	/**
	 * Verify the 'start' command.
	 */
	public void testCase01 () {
		String path = "lalala/blabla.dat";
		String d = PilotData.CMD_STRING_PREFIX + ',' + PilotData.CMD_STRING_START + ',' + path;
		PilotData p = new PilotData (d.getBytes());
		
		assertEquals (PilotData.CMD_START, p.getCommand());
		assertEquals (path, p.getParameters());
		assertEquals ("PilotData: '" + d + "'", p.toString());
	}
	
	/**
	 * Verify the 'stop' command.
	 */
	public void testCase02 () {
		String path = "lalala/blabla.dat";
		String d = PilotData.CMD_STRING_PREFIX + ',' + PilotData.CMD_STRING_STOP + ',' + path;
		PilotData p = new PilotData (d.getBytes());
		
		assertEquals (PilotData.CMD_STOP, p.getCommand());
		assertNull (p.getParameters());
		assertEquals ("PilotData: '" + d + "'", p.toString());
	}
	
	/**
	 * Verify the 'send set course file names' command.
	 */
	public void testCase03 () {
		String d = PilotData.CMD_STRING_PREFIX + ',' + PilotData.CMD_STRING_SEND_SET_COURSE_FILE_NAMES;
		PilotData p = new PilotData (d.getBytes());
		
		assertEquals (PilotData.CMD_SEND_SET_COURSE_FILE_NAMES, p.getCommand());
		assertEquals (null, p.getParameters());
		assertEquals ("PilotData: '" + d + "'", p.toString());
	}
	
	/**
	 * Verify the 'response' command.
	 */
	public void testCase04 () {
		String response = "my response";
		String d = PilotData.CMD_STRING_PREFIX + ',' + PilotData.CMD_STRING_RESPONSE + ',' + response;
		PilotData p = new PilotData (d.getBytes());
		
		assertEquals (PilotData.CMD_RESPONSE, p.getCommand());
		assertEquals (response, p.getParameters());
		assertEquals ("PilotData: '" + d + "'", p.toString());
	}
	
	/**
	 * Verify the 'file name' command.
	 */
	public void testCase05 () {
		String fileName = "lalala/blabla.dat";
		String d = PilotData.CMD_STRING_PREFIX + ',' + PilotData.CMD_STRING_FILE_NAME + ',' + fileName;
		PilotData p = new PilotData (d.getBytes());
		
		assertEquals (PilotData.CMD_FILE_NAME, p.getCommand());
		assertEquals (fileName, p.getParameters());
		assertEquals ("PilotData: '" + d + "'", p.toString());
	}
	
	/**
	 * Verify a wrong command.
	 */
	public void testCase11 () {
		String path = "lalala/blabla.dat";
		String d = PilotData.CMD_STRING_PREFIX + ",WRONG," + path;
		PilotData p = new PilotData (d.getBytes());
		
		assertEquals (PilotData.CMD_UNKNOWN, p.getCommand());
		assertNull (p.getParameters());
		assertEquals ("PilotData: '" + d + "'", p.toString());
	}
	
	/**
	 * Verify another wrong command.
	 */
	public void testCase12 () {
		String path = "lalala/blabla.dat";
		String d = "VERY,WRONG," + path;
		PilotData p = new PilotData (d.getBytes());
		
		assertEquals (PilotData.CMD_UNKNOWN, p.getCommand());
		assertNull (p.getParameters());
		assertEquals ("PilotData: '" + d + "'", p.toString());
	}

	/**
	 * Verify the <code>toByteArray()</code> method.
	 */
	public void testCase13 () {
		String path = "lalala/blabla.dat";
		String d = PilotData.CMD_STRING_PREFIX + ',' + PilotData.CMD_STRING_START + ',' + path;
		PilotData p = new PilotData (d.getBytes());
		
		byte[] expected = d.getBytes();
		byte[] x = p.toByteArray();
		assertEquals (d.length(), x.length);
		for (int k=0; k < d.length(); k++)
			assertEquals ("Index ["+k+"]: ", expected[k], x[k]);
	}
	
	/**
	 * Verify an invalid start command.
	 */
	public void testCase14 () {
		String d = PilotData.CMD_STRING_PREFIX + ',' + PilotData.CMD_STRING_START;
		PilotData p = new PilotData (d.getBytes());
		assertEquals(PilotData.CMD_UNKNOWN, p.getCommand());
	}
	
}
