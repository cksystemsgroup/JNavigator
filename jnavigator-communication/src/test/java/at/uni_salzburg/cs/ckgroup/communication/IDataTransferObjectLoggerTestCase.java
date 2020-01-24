/*
 * @(#) IDataTransferObjectLoggerTestCase.java
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
package at.uni_salzburg.cs.ckgroup.communication;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the <code>IDataTransferObjectLoggerLogger</code> class.
 * 
 * @author Clemens Krainer
 */
public class IDataTransferObjectLoggerTestCase extends TestCase {

    private static Logger logger;

	byte[] s1;
	byte[] s2;
	byte[] s3;

	MockDataTransferObjectOne one;
	MockDataTransferObjectTwo two;
	MockDataTransferObjectThree three;

	{
	    try {
            Field loggerField = DataTransferObjectLogger.class.getDeclaredField("LOG");
            loggerField.setAccessible(true);
    
            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(loggerField, loggerField.getModifiers() & ~Modifier.FINAL);
    
            logger = mock(Logger.class);
            loggerField.set(null, logger);
	    }
	    catch (Exception e) {
	        fail("Can not initialize logger!");
        }
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	public void setUp () {
		s1 = new byte[] { 10, 20, 97 };
		s2 = new byte[] { -30, 120, -97 };
		s3 = new byte[] { 23, 11, -7 };
		one = new MockDataTransferObjectOne (s1);
		two = new MockDataTransferObjectTwo (s2);
		three = new MockDataTransferObjectThree (s3);
	}
	
	
	/**
	 * Create a new <code>IDataTransferObjectLoggerLogger</code> and log messages to it. Verify
	 * that the <code>IDataTransferObjectLoggerLogger</code> logs the message to Log4J correctly.
	 */
	public void testCase01 () throws Exception {
	    
		DataTransferObjectLogger log = new DataTransferObjectLogger ();
		
		log.receive (one);
		verify(logger).debug(one.toString());
		
		log.receive (two);
		verify(logger).debug(two.toString());
		
		log.receive (three);
		verify(logger).debug(three.toString());
		
		verifyNoMoreInteractions(logger);
	}
}
