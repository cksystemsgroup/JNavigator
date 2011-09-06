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

import junit.framework.TestCase;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This class verifies the implementation of the <code>IDataTransferObjectLoggerLogger</code> class.
 * 
 * @author Clemens Krainer
 */
public class IDataTransferObjectLoggerTestCase extends TestCase {
	
	byte[] s1;
	byte[] s2;
	byte[] s3;
	MockDataTransferObjectOne one;
	MockDataTransferObjectTwo two;
	MockDataTransferObjectThree three;
	MyLogAppender appender;
	
	{
		// perform the static initialisations.
		appender = new MyLogAppender ();
		Logger.getRootLogger().addAppender (appender);
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
	public void testCase01 () {
		DataTransferObjectLogger log = new DataTransferObjectLogger ();
		try { Thread.sleep(200); } catch (Exception e) {;}
		log.receive (one);
		try { Thread.sleep(500); } catch (Exception e) {;}
		Thread.yield();
		assertEquals ("Message one", one.toString(), appender.message);
		log.receive (two);
		try { Thread.sleep(200); } catch (Exception e) {;}
		Thread.yield();
		assertEquals ("Message two", two.toString(), appender.message);
		log.receive (three);
		try { Thread.sleep(200); } catch (Exception e) {;}
		Thread.yield();
		assertEquals ("Message three", three.toString(), appender.message);
	}
	
	/**
	 * This class implements a fake log appender to be used in this unit test.  
	 * 
	 * @author Clemens Krainer
	 */
	private class MyLogAppender implements Appender {
		
		/**
		 * The last logged message.
		 */
		public String message;

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#addFilter(org.apache.log4j.spi.Filter)
		 */
		public void addFilter(Filter newFilter) {
			System.err.println ("addFilter(): " + newFilter.toString());
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#clearFilters()
		 */
		public void clearFilters() {
			System.err.println ("clearFilters()");
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#close()
		 */
		public void close() {
			System.err.println ("close()");
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#doAppend(org.apache.log4j.spi.LoggingEvent)
		 */
		public void doAppend(LoggingEvent event) {
			System.err.println ("doAppend(): " +event.timeStamp+", "+ event.getMessage());
			message = event.getMessage().toString();
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#getErrorHandler()
		 */
		public ErrorHandler getErrorHandler() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#getFilter()
		 */
		public Filter getFilter() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#getLayout()
		 */
		public Layout getLayout() {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#getName()
		 */
		public String getName() {
			return MyLogAppender.class.getName();
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#requiresLayout()
		 */
		public boolean requiresLayout() {
			return false;
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#setErrorHandler(org.apache.log4j.spi.ErrorHandler)
		 */
		public void setErrorHandler(ErrorHandler errorHandler) {
			System.err.println ("setErrorHandler():");
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#setLayout(org.apache.log4j.Layout)
		 */
		public void setLayout(Layout layout) {
			System.err.println ("setLayout():" + layout.toString());
		}

		/* (non-Javadoc)
		 * @see org.apache.log4j.Appender#setName(java.lang.String)
		 */
		public void setName(String name) {
			System.err.println ("setName(): " + name);
		}
		
	}
}
