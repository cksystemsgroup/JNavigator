/*
 * @(#) LocationMessageTestcase.java
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
package at.uni_salzburg.cs.ckgroup.location;

import junit.framework.TestCase;

/**
 * This class verifies the implementation of the <code>LocationMessage</code> class.
 * 
 * @author Clemens Krainer
 */
public class LocationMessageTestCase extends TestCase {
	/**
	 * Test if empty message leads to a "Message too short." exception message
	 */
	public void testCase01 () {
		final byte[] msg = "".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertNull (m);
		} catch (MalformedLocationMessageException e)
		{
			assertEquals("Message too short.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test if a malformed message leads to a "Checksum error." exception message. The checksum is completely wrong.
	 */
	public void testCase02 () {
		final byte[] msg = "$LOCPNG*28\r\n".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertNull (m);
		} catch (MalformedLocationMessageException e)
		{
			assertEquals("Checksum error.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test if a malformed message leads to a "Checksum error." exception message. The checksum low-byte is wrong.
	 */
	public void testCase021 () {
		final byte[] msg = "$LOCPNG*18\r\n".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertNull (m);
		} catch (MalformedLocationMessageException e)
		{
			assertEquals("Checksum error.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test if a malformed message leads to a "Checksum error." exception message. The checksum high-byte is wrong.
	 */
	public void testCase022 () {
		final byte[] msg = "$LOCPNG*29\r\n".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertNull (m);
		} catch (MalformedLocationMessageException e)
		{
			assertEquals("Checksum error.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}

	/**
	 * Test if invalid prefix leads to a "Wrong location message prefix." exception message
	 */
	public void testCase03 () {
		final byte[] msg = "$AAVTG,304.33,T,,M,0.635,N,1.176,K,A*3B\r\n".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertNull (m);
		} catch (MalformedLocationMessageException e)
		{
			assertEquals("Wrong location message prefix.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}

	/**
	 * Test if invalid postfix leads to a "Wrong location message postfix." exception message
	 */
	public void testCase04 () {
		final byte[] msg = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:20.665272000,1.2,0,1.1,2.2,3.3,4.4,5.5,6.6,7.7*6E".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertNull (m);
		} catch (MalformedLocationMessageException e)
		{
			assertEquals("Wrong location message postfix.", e.getMessage ());
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Test if missing '*' before checksum leads to a "Checksum error." exception message
	 */
	public void testCase05 () {
		final byte[] msg = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:20.665272000,1.2,0,1.1,2.2,3.3,4.4,5.5,6.6,7.76E\r\n".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertNull (m);
		} catch (MalformedLocationMessageException e)
		{
			assertEquals("Checksum error.", e.getMessage ());
		} catch (Throwable t) {
			t.printStackTrace ();
			fail();
		}
	}

	/**
	 * Test a correct location message using the first constructor.
	 */
	public void testCase06 () {

		final byte[] msg = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*69\r\n".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertTrue (m.isAValidPnqMessage());
		} catch (MalformedLocationMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}

	/**
	 * Test a correct location message using the second constructor.
	 */
	public void testCase07 () {

		final byte[] msg = "  $LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*69\r\n".getBytes ();
		try
		{
			LocationMessage m = new LocationMessage (msg,2,msg.length-2);
			assertTrue (m.isAValidPnqMessage());
		} catch (MalformedLocationMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}

	/**
	 * Create a correct location message and test the method LocationMessage.startsWith().
	 */
	public void testCase08 () {

		final byte[] msg = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*69\r\n".getBytes ();
		final byte[] str1 = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*69\r\n ".getBytes ();
		final byte[] str2 = "$LOCPNQ".getBytes ();
		final byte[] str3 = "$LACPNQ".getBytes ();
		
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertTrue (m.isAValidPnqMessage());
			assertFalse (m.startsWith (str1));
			assertTrue (m.startsWith (str2));
			assertFalse (m.startsWith (str3));
		} catch (MalformedLocationMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}

	/**
	 * Create a correct location message and test the method LocationMessage.isAValidPnqMessage().
	 */
	public void testCase09 () {

		final byte[] msg1 = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*69\r\n".getBytes ();
		final byte[] msg2 = "$LOCNPQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*69\r\n".getBytes ();
		
		try
		{
			LocationMessage m = new LocationMessage (msg1);
			assertTrue (m.isAValidPnqMessage());
			
			m = new LocationMessage (msg2);
			assertFalse (m.isAValidPnqMessage());
			
		} catch (MalformedLocationMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}
	

	/**
	 * Create a correct location message and test the method LocationMessage.getBytes().
	 */
	public void testCase10 () {

		final byte[] msg = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*69\r\n".getBytes ();
		
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertTrue (m.isAValidPnqMessage());
			
			byte[] result = m.getBytes();
			assertEquals (msg.length, result.length);

			for (int k=0; k < msg.length; k++)
				assertEquals (msg[k], result[k]);
			
		} catch (MalformedLocationMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}
		
	/**
	 * Create a correct location message that is not of type $LOCPNQ.
	 */
	public void testCase11 () {

		final byte[] msg = "$LOCPOS,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673500000,1.2,1,1.1,2.2,3.3*6A\r\n".getBytes ();
		
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertFalse (m.isAValidPnqMessage());
						
		} catch (MalformedLocationMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}
	
	/**
	 * Create a correct location message with a high checksum and test the method LocationMessage.getBytes().
	 */
	public void testCase12 () {

		final byte[] msg = "$LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:52:23.673569190,1.3,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*AD\r\n".getBytes ();
		msg[16] = (byte) 160;
		
		try
		{
			LocationMessage m = new LocationMessage (msg);
			assertTrue (m.isAValidPnqMessage());
			
			byte[] result = m.getBytes();
			assertEquals (msg.length, result.length);

			for (int k=0; k < msg.length; k++)
				assertEquals (msg[k], result[k]);
			
		} catch (MalformedLocationMessageException e)
		{
			e.printStackTrace();
			fail();
		} catch (Throwable t) {
			fail();
		}	
	}
}
