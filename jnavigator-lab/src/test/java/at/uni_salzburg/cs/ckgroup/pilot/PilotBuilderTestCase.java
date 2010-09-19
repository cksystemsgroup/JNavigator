/*
 * @(#) PilotBuilderTestCase.java
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

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

/**
 * This class verifies the implementation of the <code>PilotBuilder</code>
 * class.
 * 
 * @author Clemens Krainer
 */
public class PilotBuilderTestCase extends TestCase
{
	/**
	 * This test verifies the correct construction of a pilot configured in a
	 * properties file.
	 */
	public void testCase01 () {
		String propertyPath = "at/uni_salzburg/cs/ckgroup/pilot/PilotBuilderTest/builder1.properties";
		
		try
		{
			PilotBuilder builder = new PilotBuilder (propertyPath);
			assertNotNull (builder);
			IPilot pilot = builder.getPilot ();
			assertNotNull (pilot);
			
			assertTrue (pilot instanceof MockPilot);
			MockPilot mockPilot = (MockPilot) pilot;
			
			assertEquals (mockPilot.name, "Mock Pilot");
			assertNotNull (mockPilot.positionProvider);
			assertNotNull (mockPilot.setCourseSupplier);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}	
	}

	/**
	 * This test verifies that a missing pilot interceptor list does not throw
	 * an exception.
	 */
//	public void testCase02 () {
//		String propertyPath = "at/uni_salzburg/cs/ckgroup/pilot/PilotBuilderTest/builder2.properties";
//		System.clearProperty (PilotBuilder.PROP_PILOT_INTERCEPTOR_LIST);
//		
//		try
//		{
//			PilotBuilder builder = new PilotBuilder (propertyPath);
//			assertNotNull (builder);
//			IPilot pilot = builder.getPilot ();
//			assertNotNull (pilot);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			fail ();
//		}	
//	}

	/**
	 * This test verifies that an empty pilot interceptor list does not throw
	 * an exception.
	 */
//	public void testCase03 () {
//		String propertyPath = "at/uni_salzburg/cs/ckgroup/pilot/PilotBuilderTest/builder3.properties";
//		System.clearProperty (PilotBuilder.PROP_PILOT_INTERCEPTOR_LIST);
//		
//		try
//		{
//			PilotBuilder builder = new PilotBuilder (propertyPath);
//			assertNotNull (builder);
//			IPilot pilot = builder.getPilot ();
//			assertNotNull (pilot);
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			fail ();
//		}	
//	}

	/**
	 * This test verifies that a missing pilot interceptor throws an exception.
	 */
//	public void testCase04 () {
//		String propertyPath = "at/uni_salzburg/cs/ckgroup/PilotBuilderTest/builder4.properties";
//		System.clearProperty (PilotBuilder.PROP_PILOT_INTERCEPTOR_LIST);
//		System.clearProperty (PilotBuilder.PROP_PILOT_INTERCEPTOR_PREFIX+"a.className");
//		System.clearProperty (PilotBuilder.PROP_PILOT_INTERCEPTOR_PREFIX+"b.className");
//		System.clearProperty (PilotBuilder.PROP_PILOT_INTERCEPTOR_PREFIX+"c.className");
//		
//		try
//		{
//			PilotBuilder builder = new PilotBuilder (propertyPath);
//			assertNull (builder);
//		}
//		catch (ConfigurationException e)
//		{
////			e.printStackTrace ();
//			assertEquals ("No property className defined for property set pilot.interceptor.a.", e.getMessage ());
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//			fail ();
//		}	
//	}
	
	/**
	 * This test verifies that the <code>PilotBuilder</code> throws a
	 * if position provider has no class name configured.
	 */
	public void testCase05 () {
		String propertyPath = "at/uni_salzburg/cs/ckgroup/pilot/PilotBuilderTest/builder5.properties";

		try
		{
			Properties props = PropertyUtils.loadProperties (propertyPath);
			String lala = props.getProperty("position.provider.className");
			if (lala != null && !"".equals(lala))
				fail ();

			PilotBuilder builder = new PilotBuilder (propertyPath);
			assertNull (builder);
		}
		catch (InstantiationException e)
		{
			assertEquals ("No property className defined for property set position.provider.", e.getMessage());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail ();
		}	
	}

}
