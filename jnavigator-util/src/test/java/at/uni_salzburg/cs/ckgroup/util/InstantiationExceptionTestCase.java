/*
 * @(#) InstantiationExceptionTestCase.java
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
package at.uni_salzburg.cs.ckgroup.util;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import junit.framework.TestCase;

public class InstantiationExceptionTestCase extends TestCase
{
	/**
	 * Test if the InstantiationException transports the message String
	 */
	public void testCase01 () {
		
		try {
			throw new InstantiationException ("abc");
		} catch (InstantiationException e) {
			assertEquals ("abc", e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace ();
			fail ();
		}
	}

	/**
	 * Test if the InstantiationException is a ConfigurationException
	 */
	public void testCase02 () {
		
		try {
			throw new InstantiationException ("abc");
		} catch (ConfigurationException e) {
			assertEquals ("abc", e.getMessage ());
		} catch (Exception e) {
			e.printStackTrace ();
			fail ();
		}
	}
	
	/**
	 * Test if the InstantiationException transports another Exception.
	 */
	public void testCase03 () {
		
		try {
			throw new InstantiationException (new Exception ("xyz"));
		} catch (InstantiationException e) {
			assertEquals ("xyz", e.getMessage ());
//			assertEquals ("xyz", e.getCause ().getMessage ());
		} catch (Exception e) {
			e.printStackTrace ();
			fail ();
		}
	}

}
