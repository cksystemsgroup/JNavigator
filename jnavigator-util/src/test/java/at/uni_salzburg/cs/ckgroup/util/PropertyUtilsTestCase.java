/*
 * @(#) PropertyUtilsTestCase.java
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * Test the <code>PropertyUtils</code> implementation.
 * 
 * @author Clemens Krainer
 */
public class PropertyUtilsTestCase extends TestCase
{
	/**
	 * The properties for all tests.
	 */
	private Properties props;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		props = new Properties ();
		props.setProperty ("a.b.c1", "string1");
		props.setProperty ("a.b.c2", "string2");
		props.setProperty ("a.b.c3", "string3");
		props.setProperty ("b.a.c1", "string4");
		props.setProperty ("b.a.c2", "string5");
		props.setProperty ("b.a.c3", "string6");
		props.setProperty ("b.b.c4", "string7");
		props.setProperty ("b.b.c5", "string8");
		props.setProperty ("b.b.c6", "string9");
		
		PropertyUtils u = new PropertyUtils ();
		assertNotNull (u);
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	public void tearDown () {
		props = null;
	}
	
	/**
	 * Test the extraction of a subset of properties.
	 */
	public void testCase01 () {
		Properties extr = PropertyUtils.extract ("a.b.", props);
		System.out.println ("result: " + extr);
		
		assertEquals ("string1", extr.getProperty ("a.b.c1"));
		assertEquals ("string2", extr.getProperty ("a.b.c2"));
		assertEquals ("string3", extr.getProperty ("a.b.c3"));
		assertEquals (3, extr.size ());
	}
	
	/**
	 * Test the extraction of a subset of properties.
	 */
	public void testCase02 () {
		Properties extr = PropertyUtils.extract ("b.a.", props);
		System.out.println ("result: " + extr);
		
		assertEquals ("string4", extr.getProperty ("b.a.c1"));
		assertEquals ("string5", extr.getProperty ("b.a.c2"));
		assertEquals ("string6", extr.getProperty ("b.a.c3"));
		assertEquals (3, extr.size ());
	}

	/**
	 * Test the modification of keys of a subset of properties.
	 */
	public void testCase03 () {
		Properties repl = PropertyUtils.replaceFirst ("b.a.", "x.y.", props);
		System.out.println ("result: " + repl);
		
		assertEquals ("string1", repl.getProperty ("a.b.c1"));
		assertEquals ("string2", repl.getProperty ("a.b.c2"));
		assertEquals ("string3", repl.getProperty ("a.b.c3"));
		assertEquals ("string4", repl.getProperty ("x.y.c1"));
		assertEquals ("string5", repl.getProperty ("x.y.c2"));
		assertEquals ("string6", repl.getProperty ("x.y.c3"));
		assertEquals ("string7", repl.getProperty ("b.b.c4"));
		assertEquals ("string8", repl.getProperty ("b.b.c5"));
		assertEquals ("string9", repl.getProperty ("b.b.c6"));
		assertEquals (9, repl.size ());
	}
	
//	/**
//	 * Test the modification of keys of a subset of properties.
//	 */
//	public void testCase04 () {
//		Properties repl = PropertyUtils.replaceFirst ("b.a.", "x.y.", props);
//		System.out.println ("result: " + repl);
//		
//		assertEquals ("string1", repl.getProperty ("a.b.c1"));
//		assertEquals ("string2", repl.getProperty ("a.b.c2"));
//		assertEquals ("string3", repl.getProperty ("a.b.c3"));
//		assertEquals ("string4", repl.getProperty ("x.y.c1"));
//		assertEquals ("string5", repl.getProperty ("x.y.c2"));
//		assertEquals ("string6", repl.getProperty ("x.y.c3"));
//		assertEquals ("string7", repl.getProperty ("x.y.c4"));
//		assertEquals ("string8", repl.getProperty ("x.y.c5"));
//		assertEquals ("string9", repl.getProperty ("x.y.c6"));
//		assertEquals (9, repl.size ());
//	}
	
	/**
	 * Test the <code>PropertyUtils.loadProperties()</code> method and verify it
	 * throws a <code>NullPointerException</code> when the property file name is
	 * null.
	 */
	public void testCase05 () {
		try {
			Properties props = PropertyUtils.loadProperties (null);
			assertNull (props);
		} catch (NullPointerException e) {
			e.printStackTrace();
			assertNull (e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test the <code>PropertyUtils.loadProperties()</code> method and verify it
	 * throws a <code>FileNotFoundException</code> if it can not find the
	 * property file.
	 */
	public void testCase06 () {
		String name = "at/uni_salzburg/cs/ckgroup/util/PropertyUtilsTest/no.properties";
		try {
			Properties props = PropertyUtils.loadProperties (name);
			assertNull (props);
		} catch (FileNotFoundException e) {
			assertEquals (name, e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test the <code>PropertyUtils.loadProperties()</code> method and verify it
	 * reads the properties of an existing file as well as the System properties.
	 */
	public void testCase07 () {
		String name = "at/uni_salzburg/cs/ckgroup/util/PropertyUtilsTest/utils.properties";
		try {
			Properties props = PropertyUtils.loadProperties (name);
			assertNotNull (props);
			assertEquals ("string_1", props.getProperty ("a.b.c1"));
			assertEquals ("string_2", props.getProperty ("a.b.c2"));
			assertEquals ("string_3", props.getProperty ("a.b.c3"));
			assertEquals ("string_4", props.getProperty ("b.a.c1"));
			assertEquals ("string_5", props.getProperty ("b.a.c2"));
			assertEquals ("string_6", props.getProperty ("b.a.c3"));
			assertEquals ("string_7", props.getProperty ("b.b.c4"));
			assertEquals ("string_8", props.getProperty ("b.b.c5"));
			assertEquals ("string_9", props.getProperty ("b.b.c6"));
			assertNotNull ("os.name", props.getProperty ("os.name"));
			assertNotNull ("user.dir", props.getProperty ("user.dir"));
			assertNotNull ("user.home", props.getProperty ("user.home"));
			assertNotNull ("java.io.tmpdir", props.getProperty ("java.io.tmpdir"));
			System.out.println (props);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
}
