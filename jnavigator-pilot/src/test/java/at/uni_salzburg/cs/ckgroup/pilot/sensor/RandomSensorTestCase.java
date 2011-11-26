/*
 * @(#) RandomSensorTestCase.java
 *
 * This code is part of the JNavigator project.
 * Copyright (c) 2011  Clemens Krainer
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
package at.uni_salzburg.cs.ckgroup.pilot.sensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URISyntaxException;
import java.util.Properties;

import org.junit.Test;

import at.uni_salzburg.cs.ckgroup.pilot.config.ConfigurationException;

public class RandomSensorTestCase {
	
	@Test
	public void testCase01 () throws URISyntaxException, ConfigurationException {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("uri", "rand:///1080/1100");
		props.setProperty("path", "/rand");
		props.setProperty("precision", "3");
		
		RandomSensor sensor = new RandomSensor(props);
		for (int k=0; k < 1000; ++k) {
			assertTrue (""+sensor.getValue()+" vs. 1080.000", sensor.getValue().compareTo("1080.000") >= 0);
			assertTrue (""+sensor.getValue()+" vs. 1100.000", sensor.getValue().compareTo("1100.000") <= 0);
		}

		assertEquals ("/rand", sensor.getPath());
		assertEquals ("barimetric sensor", sensor.getName());
	}
	
	@Test
	public void testCase02 () {
		Properties props = new Properties();
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Name not configured.", e.getMessage());
		}
	}

	@Test
	public void testCase03 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Path not configured.", e.getMessage());
		}
	}
	
	@Test
	public void testCase031 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Path not configured.", e.getMessage());
		}
	}
	
	@Test
	public void testCase04 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");
		props.setProperty("uri", "int:///1080/1100");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Invalid URI. Use, e.g., rand:///0/100", e.getMessage());
		}
	}
	
	@Test
	public void testCase05 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");
		props.setProperty("uri", "rand://xx/1080/1100");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Invalid URI. Use, e.g., rand:///0/100", e.getMessage());
		}
	}
	
	@Test
	public void testCase06 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");		
		props.setProperty("uri", "rand:////1100");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Invalid URI. Use, e.g., rand:///0/100", e.getMessage());
		}
	}
	
	@Test
	public void testCase07 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");
		props.setProperty("uri", "rand:///1080");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Invalid URI. Use, e.g., rand:///0/100", e.getMessage());
		}
	}
	
	@Test
	public void testCase08 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");
		props.setProperty("uri", "rand:///1080/");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("Invalid URI. Use, e.g., rand:///0/100", e.getMessage());
		}
	}
	
	@Test
	public void testCase09 () throws URISyntaxException, ConfigurationException {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");
		props.setProperty("uri", "rand:///1080/1080");
		props.setProperty("precision", "0");
		
		RandomSensor sensor = new RandomSensor(props);
		assertNotNull(sensor);
		
		for (int k=0; k < 1000; ++k) {
			assertEquals (sensor.getValue(), "1080");
		}
	}
	
	@Test
	public void testCase10 () throws URISyntaxException, ConfigurationException {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");
		props.setProperty("uri", "rand:///1100/1100");
		
		RandomSensor sensor = new RandomSensor(props);
		assertNotNull(sensor);
		
		for (int k=0; k < 1000; ++k) {
			assertEquals (sensor.getValue(), "1100.00000");
		}
	}
	
	@Test
	public void testCase11 () {
		Properties props = new Properties();
		props.setProperty("name", "barimetric sensor");
		props.setProperty("path", "rand");
		props.setProperty("uri", "rand://xx");
		
		try {
			assertNull(new RandomSensor(props));
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail();
		} catch (ConfigurationException e) {
			assertEquals ("URI not configured. Use, e.g., rand:///0/100", e.getMessage());
		}
	}
}
