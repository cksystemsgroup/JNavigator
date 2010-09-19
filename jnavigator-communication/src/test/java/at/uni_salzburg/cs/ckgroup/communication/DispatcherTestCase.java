/*
 * @(#) DispatcherTestCase.java
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

import java.io.IOException;


import junit.framework.TestCase;

/**
 * This class verifies the implementation of the <code>Dispatcher</code> class.
 * 
 * @author Clemens Krainer
 */
public class DispatcherTestCase extends TestCase {
	
	byte[] s1;
	byte[] s2;
	byte[] s3;
	MockDataTransferObjectOne one;
	MockDataTransferObjectTwo two;
	MockDataTransferObjectThree three;
	Dispatcher dispatcher;
	MockListenerOne listenerOne;
	MockListenerTwo listenerTwo;
	MockListenerThree listenerThree;
	
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
		listenerOne = new MockListenerOne ();
		listenerTwo = new MockListenerTwo ();
		listenerThree = new MockListenerThree ();
		dispatcher = new Dispatcher ();		
	}
	
	/**
	 * Compare two byte arrays.
	 * 
	 * @param a the first byte array.
	 * @param b the second byte array.
	 */
	void arrayCompare (byte[] a, byte[] b) {
		assertEquals ("array length", a.length, b.length);
		
		for (int k=0; k < a.length; k++)
			assertEquals ("a["+k+"] != b["+k+"]", a[k], b[k]);
	}
	
	/**
	 * Register two <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> and send a <code>IDataTransferObject</code> for the first
	 * listener. Verify that only the first listener gets the message.
	 */
	public void testCase01 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerTwo, MockDataTransferObjectTwo.class);

		try {
			dispatcher.dispatch (null, one);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNotNull (listenerOne.dto);
		arrayCompare (listenerOne.dto.toByteArray(), one.toByteArray());
		assertNull (listenerTwo.dto);
		assertNull (listenerThree.dto);
		
		assertEquals ("counter one", 1, listenerOne.counter);
		assertEquals ("counter two", 0, listenerTwo.counter);
		assertEquals ("counter three", 0, listenerThree.counter);
	}
	
	/**
	 * Register two <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> and send a <code>IDataTransferObject</code> for the second
	 * listener. Verify that only the second listener gets the message.
	 */
	public void testCase02 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerTwo, MockDataTransferObjectTwo.class);

		try {
			dispatcher.dispatch (null, two);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNull (listenerOne.dto);
		assertNotNull (listenerTwo.dto);
		arrayCompare (listenerTwo.dto.toByteArray(), two.toByteArray());
		assertNull (listenerThree.dto);
		
		assertEquals ("counter one", 0, listenerOne.counter);
		assertEquals ("counter two", 1, listenerTwo.counter);
		assertEquals ("counter three", 0, listenerThree.counter);
	}
	
	/**
	 * Register two <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> and send a <code>IDataTransferObject</code> for none of the
	 * listeners. Verify that no listener gets the message.
	 */
	public void testCase03 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerTwo, MockDataTransferObjectTwo.class);

		try {
			dispatcher.dispatch (null, three);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNull (listenerOne.dto);
		assertNull (listenerTwo.dto);
		assertNull (listenerThree.dto);
		
		assertEquals ("counter one", 0, listenerOne.counter);
		assertEquals ("counter two", 0, listenerTwo.counter);
		assertEquals ("counter three", 0, listenerThree.counter);
	}
	
	/**
	 * Register two <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code>. The second listener is a derivative of the first
	 * one. Send a <code>IDataTransferObject</code> for the first listener and verify that
	 * the only first listener gets the message.
	 */
	public void testCase04 () {
		dispatcher.addDataTransferObjectListener (listenerTwo, MockDataTransferObjectTwo.class);
		dispatcher.addDataTransferObjectListener (listenerThree, MockDataTransferObjectThree.class);

		try {
			dispatcher.dispatch (null, two);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNull (listenerOne.dto);
		assertNotNull (listenerTwo.dto);
		arrayCompare (listenerTwo.dto.toByteArray(), two.toByteArray());
		assertNull (listenerThree.dto);
		
		assertEquals ("counter one", 0, listenerOne.counter);
		assertEquals ("counter two", 1, listenerTwo.counter);
		assertEquals ("counter three", 0, listenerThree.counter);
	}
	
	/**
	 * Register two <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code>. The second listener is a derivative of the first
	 * one. Send a <code>IDataTransferObject</code> for the second listener and verify that
	 * the only second listener gets the message.
	 */
	public void testCase05 () {
		dispatcher.addDataTransferObjectListener (listenerTwo, MockDataTransferObjectTwo.class);
		dispatcher.addDataTransferObjectListener (listenerThree, MockDataTransferObjectThree.class);

		try {
			dispatcher.dispatch (null, three);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNull (listenerOne.dto);
		assertNull (listenerTwo.dto);
		assertNotNull (listenerThree.dto);
		arrayCompare (listenerThree.dto.toByteArray(), three.toByteArray());
		
		assertEquals ("counter one", 0, listenerOne.counter);
		assertEquals ("counter two", 0, listenerTwo.counter);
		assertEquals ("counter three", 1, listenerThree.counter);
	}
	
	/**
	 * Register two <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code>. The first listener registers for all messages.
	 * Send a <code>IDataTransferObject</code> for the first second and verify that both,
	 * first and second listener get the message.
	 */
	public void testCase06 () {
		dispatcher.addDataTransferObjectListener (listenerTwo, IDataTransferObject.class);
		dispatcher.addDataTransferObjectListener (listenerThree, MockDataTransferObjectThree.class);

		try {
			dispatcher.dispatch (null, three);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNull (listenerOne.dto);
		assertNotNull (listenerTwo.dto);
		arrayCompare (listenerTwo.dto.toByteArray(), three.toByteArray());
		assertNotNull (listenerThree.dto);
		arrayCompare (listenerThree.dto.toByteArray(), three.toByteArray());
		
		assertEquals ("counter one", 0, listenerOne.counter);
		assertEquals ("counter two", 1, listenerTwo.counter);
		assertEquals ("counter three", 1, listenerThree.counter);
	}
	
	/**
	 * Register three <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code>. The second listener registers for all messages.
	 * Send a <code>IDataTransferObject</code> for the third listener and verify that
	 * both, second and third listener get the message, but not the first.
	 */
	public void testCase07 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerTwo, IDataTransferObject.class);
		dispatcher.addDataTransferObjectListener (listenerThree, MockDataTransferObjectThree.class);

		try {
			dispatcher.dispatch (null, three);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNull (listenerOne.dto);
		assertNotNull (listenerTwo.dto);
		arrayCompare (listenerTwo.dto.toByteArray(), three.toByteArray());
		assertNotNull (listenerThree.dto);
		arrayCompare (listenerThree.dto.toByteArray(), three.toByteArray());
		
		assertEquals ("counter one", 0, listenerOne.counter);
		assertEquals ("counter two", 1, listenerTwo.counter);
		assertEquals ("counter three", 1, listenerThree.counter);
	}
	
	/**
	 * Register three <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> for the same type. send a <code>IDataTransferObject</code>
	 * for all listeners and verify that all listeners get the message.
	 */
	public void testCase08 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerTwo, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerThree, MockDataTransferObjectOne.class);
		
		try {
			dispatcher.dispatch (null, one);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNotNull (listenerOne.dto);
		arrayCompare (listenerOne.dto.toByteArray(), one.toByteArray());
		assertNotNull (listenerTwo.dto);
		arrayCompare (listenerTwo.dto.toByteArray(), one.toByteArray());
		assertNotNull (listenerThree.dto);
		arrayCompare (listenerThree.dto.toByteArray(), one.toByteArray());
		
		assertEquals ("counter one", 1, listenerOne.counter);
		assertEquals ("counter two", 1, listenerTwo.counter);
		assertEquals ("counter three", 1, listenerThree.counter);
	}	
	
	/**
	 * Register one <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> twice for the same type. Send a
	 * <code>IDataTransferObject</code> for the listener and verify that it gets the
	 * message only once.
	 */
	public void testCase09 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		
		try {
			dispatcher.dispatch (null, one);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNotNull (listenerOne.dto);
		arrayCompare (listenerOne.dto.toByteArray(), one.toByteArray());
		
		assertEquals ("counter one", 1, listenerOne.counter);
	}	
	
	/**
	 * Register one <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> for all messages and for a specific type. Send a
	 * <code>IDataTransferObject</code> for the listener and verify that it gets the
	 * message only once.
	 */
	public void testCase10 () {
		dispatcher.addDataTransferObjectListener (listenerOne, IDataTransferObject.class);
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		
		try {
			dispatcher.dispatch (null, one);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNotNull (listenerOne.dto);
		arrayCompare (listenerOne.dto.toByteArray(), one.toByteArray());
		
		assertEquals ("counter one", 1, listenerOne.counter);
	}	
	
	/**
	 * Register one <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> for all messages and for a specific type. Send a
	 * <code>IDataTransferObject</code> for the listener and verify that it gets the
	 * message only once.
	 */
	public void testCase11 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerOne, IDataTransferObject.class);
		
		try {
			dispatcher.dispatch (null, one);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNotNull (listenerOne.dto);
		arrayCompare (listenerOne.dto.toByteArray(), one.toByteArray());
		
		assertEquals ("counter one", 1, listenerOne.counter);
	}
	
	/**
	 * Register a <code>IDataTransferObjectListener</code> with a <code>Dispatcher</code>
	 * for a message type. Verify that a message of the registered type is
	 * delivered. Unregister the listener and deliver a message of the
	 * previously registered type. Verify that the message was not delivered.
	 */
	public void testCase12 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		
		try {
			dispatcher.dispatch (null, one);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNotNull (listenerOne.dto);
		arrayCompare (listenerOne.dto.toByteArray(), one.toByteArray());
		assertEquals ("counter one", 1, listenerOne.counter);
		listenerOne.dto = null;
		listenerOne.counter = 0;

		dispatcher.removeIDataTransferObjectListener (listenerOne);
		try {
			dispatcher.dispatch (null, one);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
		assertNull (listenerOne.dto);
		assertEquals ("counter one", 0, listenerOne.counter);
	}
	
	/**
	 * Register a <code>IDataTransferObjectListener</code> with a <code>Dispatcher</code>
	 * for two message types. Verify that messages of the registered types are
	 * delivered. Unregister the listener and deliver messages of the previously
	 * registered types. Verify that the messages were not delivered.
	 */
	public void testCase13 () {
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectOne.class);
		dispatcher.addDataTransferObjectListener (listenerOne, MockDataTransferObjectTwo.class);
		
		try {
			dispatcher.dispatch (null, one);

			assertNotNull (listenerOne.dto);
			arrayCompare (listenerOne.dto.toByteArray(), one.toByteArray());
			assertEquals ("counter one", 1, listenerOne.counter);
			listenerOne.dto = null;
			listenerOne.counter = 0;
	
			dispatcher.dispatch (null, two);
			assertNotNull (listenerOne.dto);
			arrayCompare (listenerOne.dto.toByteArray(), two.toByteArray());
			assertEquals ("counter two", 1, listenerOne.counter);
			listenerOne.dto = null;
			listenerOne.counter = 0;
			
			dispatcher.removeIDataTransferObjectListener (listenerOne);
			dispatcher.dispatch (null, one);
			assertNull (listenerOne.dto);
			
			dispatcher.dispatch (null, two);
			assertNull (listenerOne.dto);
			assertEquals ("counter one", 0, listenerOne.counter);
			assertEquals ("counter two", 0, listenerOne.counter);
			
			dispatcher.removeIDataTransferObjectListener (listenerOne);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Register two <code>IDataTransferObjectListener</code> with a
	 * <code>Dispatcher</code> send messages on behalf of the listeners. Verify
	 * that messages of the registered types are not delivered to the sender,
	 * but to the one listener. Verify that the listeners both get the message
	 * if the sender is set to null.
	 */
	public void testCase14 () {
		dispatcher.addDataTransferObjectListener (listenerOne, IDataTransferObject.class);
		dispatcher.addDataTransferObjectListener (listenerTwo, IDataTransferObject.class);
		
		try {
			dispatcher.dispatch (listenerOne, one);

			assertNull (listenerOne.dto);
			assertNotNull (listenerTwo.dto);
			arrayCompare (listenerTwo.dto.toByteArray(), one.toByteArray());
			assertEquals ("counter one", 0, listenerOne.counter);
			assertEquals ("counter two", 1, listenerTwo.counter);
			listenerTwo.dto = null;
			listenerTwo.counter = 0;
	
			dispatcher.dispatch (listenerTwo, two);
			assertNotNull (listenerOne.dto);
			arrayCompare (listenerOne.dto.toByteArray(), two.toByteArray());
			assertNull (listenerTwo.dto);
			assertEquals ("counter one", 1, listenerOne.counter);
			assertEquals ("counter two", 0, listenerTwo.counter);
			listenerOne.dto = null;
			listenerOne.counter = 0;
			
			dispatcher.dispatch (null, three);
			assertNotNull (listenerOne.dto);
			arrayCompare (listenerOne.dto.toByteArray(), three.toByteArray());
			assertNotNull (listenerTwo.dto);
			arrayCompare (listenerTwo.dto.toByteArray(), three.toByteArray());
			assertEquals ("counter one", 1, listenerOne.counter);
			assertEquals ("counter two", 1, listenerTwo.counter);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Veriy that the <code>Dispatcher</code> throws an <code>IOException</code>
	 * at dispatching a null message.
	 */
	public void testCase15 () {
		try {
			dispatcher.dispatch(null, null);
			fail ();
		} catch (IOException e) {
			assertEquals ("Refusing to send a null data transfer object.", e.getMessage());
		}
	}
}
