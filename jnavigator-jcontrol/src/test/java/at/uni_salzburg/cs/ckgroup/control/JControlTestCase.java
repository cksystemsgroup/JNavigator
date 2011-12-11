/*
 * @(#) JControlTestCase.java
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
import java.util.Locale;
import java.util.Properties;

import junit.framework.TestCase;
import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.communication.CommunicationException;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals;
import at.uni_salzburg.cs.ckgroup.communication.data.PilotData;
import at.uni_salzburg.cs.ckgroup.communication.data.SensorData;
import at.uni_salzburg.cs.ckgroup.communication.data.ShutdownEvent;
import at.uni_salzburg.cs.ckgroup.communication.data.TrimValues;

public class JControlTestCase extends TestCase {

	public void testCase01 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "-1");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			JControl control = new JControl (props);
			assertNotNull (control);
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase02 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.NoDummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "1");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");

		try {
			new JControl (props);
			fail ();
		} catch (ConfigurationException e2) {
			assertEquals ("at.uni_salzburg.cs.ckgroup.control.NoDummyAlgorithm", e2.getMessage());
		}
	}
	
	public void testCase03 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "1");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, "noSuchDirectory");

		try {
			new JControl (props);
			fail ();
		} catch (ConfigurationException e) {
			assertEquals ("Can not chdir() set course folder noSuchDirectory", e.getMessage());
		}
	}
	
	public void testCase04 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "-1");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setSetCourseSupplier(setCourseSupplier);
			control.setClock(clock);
			
			dtoProvider.dtos.clear();
			control.run();
			assertNotNull(dtoProvider.dtos.firstElement());
			assertTrue(dtoProvider.dtos.firstElement() instanceof MotorSignals);
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase05 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setSetCourseSupplier(setCourseSupplier);
			control.setClock(clock);
			
			control.run();
			control.run();
			control.run();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase06 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);

			dtoProvider.simulateIOException = true;
			control.run();
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	
	public void testCase07 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			SensorData dto = null;
			
			try {
				MotorSignals ad = new MotorSignals((short)1, (short)2, (short)3, (short)4, (short)5);
				control.receive(ad);
				fail ();
			} catch (IOException e) {
				assertEquals("Can not handle IDataTransferObject object of class at.uni_salzburg.cs.ckgroup.communication.data.MotorSignals", e.getMessage());
			}
			
			try {
				dto = new SensorData(new byte [] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
			} catch (CommunicationException e) {
				e.printStackTrace();
				fail ();
			}
			
			try {
				control.receive(dto);
				assertNull (DummyAlgorithm.instance.sensorData);

				control.setDtoProvider(dtoProvider);
				control.receive(dto);
				assertNull (DummyAlgorithm.instance.sensorData);
				
				control.setPositionProvider(positionProvider);
				control.receive(dto);
				assertNotNull (DummyAlgorithm.instance.sensorData);
				assertEquals(dto, DummyAlgorithm.instance.sensorData);
				
				control.setSetCourseSupplier(setCourseSupplier);
				control.receive(dto);
				
				control.setClock(clock);
				control.receive(dto);
				
				CommandData nd = new CommandData(1, 2, 3, 4);
				control.receive(nd);
				
				control.receive(dto);
				
				
				TrimValues tv = new TrimValues(7, 8, 9);
				control.receive(tv);
				assertEquals(tv.getPitch(), DummyAlgorithm.instance.trimValues.getPitch(), 1E-9);
				assertEquals(tv.getRoll(),  DummyAlgorithm.instance.trimValues.getRoll(),  1E-9);
				assertEquals(tv.getYaw(),   DummyAlgorithm.instance.trimValues.getYaw(),   1E-9);
				
			} catch (IOException e) {
				e.printStackTrace();
				fail ();
			}
			
			
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase08 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			JControl control = new JControl (props);
			assertNotNull (control);
			
			ShutdownEvent dto = new ShutdownEvent (null);
			
			try {
				control.receive(dto);
				fail ();
			} catch (IOException e) {
				assertEquals ("Can not handle ShutdownEvent yet.", e.getMessage());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase09 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			Locale.setDefault(Locale.US);
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setClock(clock);
			
			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_START+ ",setcourse.dat").getBytes());
//			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_START).getBytes());
			control.receive(dto);
			assertFalse (control.getAutoPilotFlight());
			
			control.setSetCourseSupplier(setCourseSupplier);
			control.receive(dto);
			assertFalse (control.getAutoPilotFlight());
			
			String result1 = "PilotData: '4ERROR LOADING setcourse.dat\r\njava.io.FileNotFoundException: ./setcourse.dat (Datei oder Verzeichnis nicht gefunden)\n\tat java.'";
			String result2 = "PilotData: '4ERROR LOADING setcourse.dat\r\njava.io.FileNotFoundException: ./setcourse.dat (No such file or directory)\n\tat java.io.FileInput'";
//			System.err.println ("result1=#" + result1 + "#");
//			System.err.println ("result2=#" + result2 + "#");
//			System.err.println ("dtoRslt=#" + dtoProvider.dtos.firstElement().toString() + "#");
			assertTrue (result1.equals(dtoProvider.dtos.firstElement().toString()) || result2.equals(dtoProvider.dtos.firstElement().toString()));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase10 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, "src/test/resources/setcourses");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			DummyClock clock = new DummyClock();
			clock.currentTime = 12345678;
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setClock(clock);

			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_START+ ",setcourse1.dat").getBytes());
			control.receive(dto);
			assertFalse (control.getAutoPilotFlight());
			
			control.setSetCourseSupplier(setCourseSupplier);
			control.receive(dto);
			
			assertEquals ("PilotData: '4OK LOADED setcourse1.dat'", dtoProvider.dtos.firstElement().toString());
			assertTrue (control.getAutoPilotFlight());
			assertEquals (12345678, control.getAutoPilotStartTime());
			
			SensorData sensorData = new SensorData(new byte [] {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
			control.receive(sensorData);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase11 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, "src/test/resources/setcourses");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			setCourseSupplier.fakeIOException = true;
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setSetCourseSupplier(setCourseSupplier);
			control.setClock(clock);
			
			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_START+ ",setcourse2.dat").getBytes());
			control.receive(dto);
			
			assertEquals ("PilotData: '4ERROR LOADING setcourse2.dat\r\njava.io.IOException: Intentionally thrown Exception.\n\tat at.uni_salzburg.cs.ckgroup.control.Dum'", dtoProvider.dtos.firstElement().toString());
			assertFalse (control.getAutoPilotFlight());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase12 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, "src/test/resources/setcourses");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			setCourseSupplier.fakeConfigurationException = true;
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setSetCourseSupplier(setCourseSupplier);
			control.setClock(clock);
			
			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_START+ ",setcourse2.dat").getBytes());
			control.receive(dto);
			
			assertEquals ("PilotData: '4ERROR LOADING setcourse2.dat\r\nat.uni_salzburg.cs.ckgroup.ConfigurationException: Intentionally thrown Exception.\n\tat at.uni_s'", dtoProvider.dtos.firstElement().toString());
			assertFalse (control.getAutoPilotFlight());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase13 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, "src/test/resources/setcourses");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			setCourseSupplier.fakeConfigurationException = true;
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setSetCourseSupplier(setCourseSupplier);
			control.setClock(clock);
			
			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_STOP).getBytes());
			control.receive(dto);
			
			assertFalse (control.getAutoPilotFlight());
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase14 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, "src/test/resources/setcourses");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			setCourseSupplier.fakeConfigurationException = true;
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setSetCourseSupplier(setCourseSupplier);
			control.setClock(clock);
			
			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_SEND_SET_COURSE_FILE_NAMES).getBytes());
			control.receive(dto);
			
			assertFalse (control.getAutoPilotFlight());
			
			int counter = 0;
			while (++counter <= 10) {
				Thread.sleep(200);
				if (dtoProvider.dtos.size() >= 2)
					break;
			}
			
			assertTrue (dtoProvider.dtos.size() == 2);
			IDataTransferObject dto1 = (IDataTransferObject) dtoProvider.dtos.get(0);
			IDataTransferObject dto2 = (IDataTransferObject) dtoProvider.dtos.get(1);
			assertEquals ("PilotData: 'AUTOPILOT FILE NAME r setcourse1.dat'", dto1.toString());
			assertEquals ("PilotData: 'AUTOPILOT FILE NAME k setcourse2.dat'", dto2.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase15 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, ".");
		
		try {
			Locale.setDefault(Locale.US);
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setClock(clock);
			
			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_START).getBytes());
			control.receive(dto);
			assertFalse (control.getAutoPilotFlight());
			
			control.setSetCourseSupplier(setCourseSupplier);
			control.receive(dto);
			assertFalse (control.getAutoPilotFlight());
			
			assertEquals(0, dtoProvider.dtos.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	public void testCase16 () {
		Properties props = new Properties ();
		props.setProperty(JControl.PROP_ALGORITHM_PREFIX+"className", "at.uni_salzburg.cs.ckgroup.control.DummyAlgorithm");
		props.setProperty(JControl.PROP_FORCED_GC_CYCLE, "2");
		props.setProperty(JControl.PROP_SET_COURSE_FOLDER, "src/test/resources/setcourses");
		
		try {
			DummyPositionProvider positionProvider = new DummyPositionProvider();
			DummyDTOProvider dtoProvider = new DummyDTOProvider();
			dtoProvider.simulateIOException = true;
			DummySetCourseSupplier setCourseSupplier = new DummySetCourseSupplier();
			setCourseSupplier.fakeConfigurationException = true;
			DummyClock clock = new DummyClock();
			
			JControl control = new JControl (props);
			assertNotNull (control);
			
			control.setDtoProvider(dtoProvider);
			control.setPositionProvider(positionProvider);
			control.setSetCourseSupplier(setCourseSupplier);
			control.setClock(clock);
			
			PilotData dto = new PilotData((PilotData.CMD_STRING_PREFIX+','+PilotData.CMD_STRING_SEND_SET_COURSE_FILE_NAMES).getBytes());
			control.receive(dto);
			
			assertFalse (control.getAutoPilotFlight());
			Thread.sleep(1000);
			
			assertTrue (dtoProvider.dtos.size() == 0);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail ();
		}
	}
}
