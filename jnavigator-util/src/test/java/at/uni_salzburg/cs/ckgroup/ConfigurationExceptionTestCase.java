/*
 * @(#) ConfigurationExceptionTestCase.java
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
package at.uni_salzburg.cs.ckgroup;

import junit.framework.TestCase;

public class ConfigurationExceptionTestCase extends TestCase
{
	/**
	 * Test if the ConfigurationException transports the message String
	 */
	public void testCase01 () {
		
		try {
			throw new ConfigurationException ("abc");
		} catch (ConfigurationException e) {
			assertEquals ("abc", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * Test if the ConfigurationException is a Exception
	 */
	public void testCase02 () {
		
		try {
			throw new ConfigurationException ("abc");
		} catch (Exception e) {
			assertEquals ("abc", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace ();
			fail ();
		}
	}
		
	/**
	 * Test if the ConfigurationException transports another Exception.
	 */
	public void testCase03 () {
		
		try {
			throw new ConfigurationException (new Exception ("xyz"));
		} catch (ConfigurationException e) {
			e.printStackTrace ();
			assertEquals ("xyz", e.getMessage ());
		} catch (Throwable e) {
//			e.printStackTrace ();
			fail ();
		}
	}

}
