/*
 * @(#) ParserTestCase.java
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
package at.uni_salzburg.cs.ckgroup.pilot.vcl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

public class ParserTestCase {

	@Test
	public void testCase01 () throws IOException {
		final String course = "at/uni_salzburg/cs/ckgroup/pilot/vcl/parserTest01.crs";
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(course);
		
		assertNotNull (inStream);
		
		Parser parser = new Parser();
		parser.parse(inStream);
		
		List<Boolean> errors = parser.getErrors();
		List<ICommand> script = parser.getScript();
		List<String> source = parser.getSource();
		
		assertNotNull (errors);
		assertNotNull (script);
		assertNotNull (source);
		
		for (int k=0; k < source.size(); k++) {
			System.out.printf("%3d: %2d: %s\n", k, errors.get(k)?1:0, source.get(k));
		}
		
		assertFalse(parser.isScriptOk());
		for (int k=0; k < parser.getErrors().size(); k++) {
			assertEquals("line "+k, k == 14, parser.getErrors().get(k));
		}
	}
	
	@Test
	public void testCase02 () throws IOException {
		final String course = "at/uni_salzburg/cs/ckgroup/pilot/vcl/parserTest02.crs";
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(course);
		
		assertNotNull (inStream);
		
		Parser parser = new Parser();
		parser.parse(inStream);
		
		List<Boolean> errors = parser.getErrors();
		List<ICommand> script = parser.getScript();
		List<String> source = parser.getSource();
		
		assertNotNull (errors);
		assertNotNull (script);
		assertNotNull (source);
		
		for (int k=0; k < source.size(); k++) {
			System.out.printf("%3d: %2d: %s\n", k, errors.get(k)?1:0, source.get(k));
		}
		
		assertTrue(parser.isScriptOk());
		for (int k=0; k < parser.getErrors().size(); k++) {
			assertEquals("line "+k, false, parser.getErrors().get(k));
		}
	}
	
	@Test
	public void testCase03 () throws IOException {
		final String course = "at/uni_salzburg/cs/ckgroup/pilot/vcl/parserTest03.crs";
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(course);
		
		assertNotNull (inStream);
		
		Parser parser = new Parser();
		parser.parse(inStream);
		
		List<Boolean> errors = parser.getErrors();
		List<ICommand> script = parser.getScript();
		List<String> source = parser.getSource();
		
		assertNotNull (errors);
		assertNotNull (script);
		assertNotNull (source);
		
		for (int k=0; k < source.size(); k++) {
			System.out.printf("%3d: %2d: %s\n", k, errors.get(k)?1:0, source.get(k));
		}
		
		assertFalse(parser.isScriptOk());
		for (int k=0; k < parser.getErrors().size(); k++) {
			assertEquals("line "+k, k >= 10, parser.getErrors().get(k));
		}
	}
	
	@Test
	public void testCase04 () throws IOException {
		final String course = "at/uni_salzburg/cs/ckgroup/pilot/vcl/parserTest04.crs";
		InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(course);
		
		assertNotNull (inStream);
		
		Parser parser = new Parser();
		parser.parse(inStream);
		
		List<Boolean> errors = parser.getErrors();
		List<ICommand> script = parser.getScript();
		List<String> source = parser.getSource();
		
		assertNotNull (errors);
		assertNotNull (script);
		assertNotNull (source);
		
		for (int k=0; k < source.size(); k++) {
			System.out.printf("%3d: %2d: %s\n", k, errors.get(k)?1:0, source.get(k));
		}
		
		assertFalse(parser.isScriptOk());
		for (int k=0; k < parser.getErrors().size(); k++) {
			assertEquals("line "+k, k >= 3, parser.getErrors().get(k));
		}
	}
}
