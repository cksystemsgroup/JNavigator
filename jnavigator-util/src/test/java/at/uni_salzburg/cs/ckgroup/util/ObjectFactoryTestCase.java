/*
 * @(#) ObjectFactoryTestCase.java
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

import java.io.InputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

import junit.framework.TestCase;

/**
 * Test the implementation of the ObjectFactory.
 * 
 * @author Clemens Krainer
 */
public class ObjectFactoryTestCase extends TestCase
{
	private ObjectFactory factory;
	private Properties inputStreamProps;
	private String inputStreamPrefix = "apos.ntrip.dgps.";
	private Properties iConnectionProps;
	private String iConnectionPrefix = "AsteRx1.lan.";
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		factory = ObjectFactory.getInstance ();
		
		inputStreamProps = new Properties ();
		inputStreamProps.setProperty ("apos.ntrip.dgps.className", "at.uni_salzburg.cs.ckgroup.util.InputStreamMock");
		inputStreamProps.setProperty ("apos.ntrip.dgps.caster", "10.13.30.15");
		inputStreamProps.setProperty ("apos.ntrip.dgps.port", "2101");
		inputStreamProps.setProperty ("apos.ntrip.dgps.user", "xyzrghz");
		inputStreamProps.setProperty ("apos.ntrip.dgps.password", "abcdefg");
		inputStreamProps.setProperty ("apos.ntrip.dgps.mountpoint", "APOS_DGPS");
		
		iConnectionProps = new Properties ();
		iConnectionProps.setProperty ("AsteRx1.lan.className", "at.uni_salzburg.cs.ckgroup.util.IConnectionMock");
		iConnectionProps.setProperty ("AsteRx1.lan.host", "10.10.11.105");
		iConnectionProps.setProperty ("AsteRx1.lan.port", "3333");
	}
	
	/**
	 * Test if the factory can instantiate an InputStreamMock object. 
	 */
	public void testCase01 () {
		
		try
		{
			InputStream i = factory.instantiateInputStream (inputStreamPrefix, inputStreamProps);
			assertNotNull (i);
			i.read ();
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test if the factory fails if creating a non-InputStreamMock object. 
	 */
	public void testCase02 () {
		
		try
		{
			InputStream i = factory.instantiateInputStream (iConnectionPrefix, iConnectionProps);
			assertNotNull (i);
			i.read ();
		} catch (ClassCastException e) {
			e.printStackTrace();
			String msg = e.getMessage();
			if (	!"at.uni_salzburg.cs.ckgroup.util.IConnectionMock cannot be cast to java.io.InputStream".equals(msg) &&
					!"at.uni_salzburg.cs.ckgroup.util.IConnectionMock".equals(msg)
			) fail ();
//			System.err.println ("testCase02.ClassCastException: '" + e.getMessage() + "'");
//			assertEquals ("at.uni_salzburg.cs.ckgroup.util.IConnectionMock", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test if the factory can instantiate an IConnectionMock object. 
	 */
	public void testCase03 () {
		
		try
		{
			IConnection i = factory.instantiateIConnection (iConnectionPrefix, iConnectionProps);
			assertNotNull (i);
			i.getInputStream ();
			i.getOutputStream ();
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test if the factory fails if creating a non-IConnectionMock object. 
	 */
	public void testCase04 () {
		
		try
		{
			IConnection i = factory.instantiateIConnection (inputStreamPrefix, inputStreamProps);
			assertNotNull (i);
			i.getInputStream ();
			i.getOutputStream ();
		} catch (ClassCastException e) {
			e.printStackTrace();
			String msg = e.getMessage ();
			if (	!"at.uni_salzburg.cs.ckgroup.util.InputStreamMock cannot be cast to at.uni_salzburg.cs.ckgroup.io.IConnection".equals(msg) &&
					!"at.uni_salzburg.cs.ckgroup.util.InputStreamMock".equals(msg)
			) fail ();
//			System.err.println ("testCase04.ClassCastException: '" + e.getMessage() + "'");
//			assertEquals ("at.uni_salzburg.cs.ckgroup.util.InputStreamMock", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Verify if the factory fails if the GPS receiver class name is not configured.
	 */
	public void testCase05 () {
		
		try
		{
			inputStreamProps.remove ("apos.ntrip.dgps.className");
			IConnection i = factory.instantiateIConnection (inputStreamPrefix, inputStreamProps);
			assertNull (i);
		} catch (InstantiationException e) {
			assertEquals ("No property className defined for property set apos.ntrip.dgps.", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}

	/**
	 * Verify if the factory fails if the GPS receiver class name is not configured.
	 */
	public void testCase06 () {
		
		try
		{
			inputStreamProps.setProperty ("apos.ntrip.dgps.className", "at.uni_salzburg.cs.ckgroup.util.ObjectFactoryTestCase");
			IConnection i = factory.instantiateIConnection (inputStreamPrefix, inputStreamProps);
			assertNull (i);
		} catch (InstantiationException e) {
			assertEquals ("at.uni_salzburg.cs.ckgroup.util.ObjectFactoryTestCase.<init>(java.util.Properties)", e.getMessage ());
//			assertEquals ("at.uni_salzburg.cs.ckgroup.util.ObjectFactoryTestCase.<init>(java.util.Properties)", e.getCause ().getMessage ());
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Verify if the factory fails if the GPS receiver class name is not configured.
	 */
	public void testCase07 () {
		
		try
		{
			inputStreamProps.setProperty ("apos.ntrip.dgps.className", "");
			IConnection i = factory.instantiateIConnection (inputStreamPrefix, inputStreamProps);
			assertNull (i);
		} catch (InstantiationException e) {
			assertEquals ("No property className defined for property set apos.ntrip.dgps.", e.getMessage ());
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Test if the factory can instantiate an InputStreamMock object using a NULL prefix.
	 */
	public void testCase08 () {
		
		Properties props = new Properties ();
		
		String prefixExpr = inputStreamPrefix; // .replaceAll ("\\.", "\\\\.");
		props =  PropertyUtils.extract (prefixExpr, inputStreamProps);
		props = PropertyUtils.replaceFirst (prefixExpr, "", props);
		
		try
		{
			InputStream i = factory.instantiateInputStream (null, props);
			assertNotNull (i);
			i.read ();
		} catch (Throwable e) {
			e.printStackTrace();
			fail ();
		}
	}
}
