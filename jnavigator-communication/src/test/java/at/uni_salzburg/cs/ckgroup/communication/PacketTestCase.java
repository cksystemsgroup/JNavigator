/*
 * @(#) PacketTestCase.java
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

/**
 * This test case verifies the implementation of the <code>Packet</code> class.
 * 
 * @author Clemens Krainer
 */
public class PacketTestCase extends TestCase {

	/**
	 * This test verifies the correct behavior of the byte array constructor.
	 * @throws IOException 
	 */
	public void testCase01 () throws IOException {
		int[] expected = {255,255,13,3,1,2,3,0,22};
		byte type = 13;
		byte[] payload = new byte[] {1,2,3};
		Packet p = new Packet (type, payload);
		
		byte[] x = p.toByteArray();
		for (int k=0; k < expected.length; k++)
			assertEquals ("Byte ["+k+"]",expected[k], (x[k] + 256)&0xFF);
			
		String s = p.toString();
		assertEquals ("String", "[255,255,13,3,1,2,3,0,22]", s);
	}
	
	/**
	 * This test verifies the correct behavior of the byte array constructor.
	 * @throws IOException 
	 */
	public void testCase02 () throws IOException {
		int[] expected = {255,255,13,5,200,30,129,255,13,2,133};
		byte type = 13;
		byte[] payload = new byte[] {(byte)200,30,(byte)129,(byte)255,13};
		Packet p = new Packet (type, payload);
		
		byte[] x = p.toByteArray();
		for (int k=0; k < expected.length; k++)
			assertEquals ("Byte ["+k+"]",expected[k], (x[k] + 256)&0xFF);
			
		String s = p.toString();
		assertEquals ("String", "[255,255,13,5,200,30,129,255,13,2,133]", s);
	}
	
	/**
	 * This test verifies the correct behavior of the byte array constructor.
	 * @throws IOException 
	 */
	public void testCase03 () throws IOException {
		int[] expected = {255,255,13,0,0,13};
		byte type = 13;
		Packet p = new Packet (type, null);
		
		byte[] x = p.toByteArray();
		for (int k=0; k < expected.length; k++)
			assertEquals ("Byte ["+k+"]",expected[k], (x[k] + 256)&0xFF);
			
		String s = p.toString();
		assertEquals ("String", "[255,255,13,0,0,13]", s);
	}
	
	/**
	 * This test verifies the correct behavior of the <code>InputStream</code>
	 * constructor.
	 */
	public void testCase11 () {
		byte[] expected = {(byte)255,(byte)255,13,3,1,2,3,0,22};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNotNull (p);
			
			byte[] x = p.toByteArray();
			for (int k=0; k < expected.length; k++)
				assertEquals ("Byte ["+k+"]",expected[k], x[k]);
				
			String s = p.toString();
			assertEquals ("String", "[255,255,13,3,1,2,3,0,22]", s);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the correct behavior of the <code>InputStream</code>
	 * constructor.
	 */
	public void testCase12 () {
		byte[] expected = {(byte)255,(byte)255,13,5,(byte)200,30,(byte)129,(byte)255,13,2,(byte)133};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNotNull (p);
			
			byte[] x = p.toByteArray();
			for (int k=0; k < expected.length; k++)
				assertEquals ("Byte ["+k+"]",expected[k], x[k]);
				
			String s = p.toString();
			assertEquals ("String", "[255,255,13,5,200,30,129,255,13,2,133]", s);
		} catch (IOException e) {
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to synchronize.
	 */
	public void testCase13 () {
		byte[] expected = {(byte)255,13,5,(byte)200,30,(byte)129,(byte)255,13,2,(byte)133};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Premature end of InputStream at reading the packet header.", e.getMessage());
		}
	}
	
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to read the type.
	 */
	public void testCase14 () {
		byte[] expected = {(byte)255,(byte)255};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Premature end of InputStream at reading the type.", e.getMessage());
		}
	}
		
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to read the size.
	 */
	public void testCase15 () {
		byte[] expected = {(byte)255,(byte)255,13};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Premature end of InputStream at reading the payload size.", e.getMessage());
		}
	}
	
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to read the payload.
	 */
	public void testCase16 () {
		byte[] expected = {(byte)255,(byte)255,13,5};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Premature end of InputStream at reading the payload.", e.getMessage());
		}
	}
	
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to read the payload.
	 */
	public void testCase17 () {
		byte[] expected = {(byte)255,(byte)255,13,5,(byte)200};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Premature end of InputStream at reading the payload.", e.getMessage());
		}
	}
	
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to read the payload.
	 */
	public void testCase18 () {
		byte[] expected = {(byte)255,(byte)255,13,5,(byte)200,30,(byte)129,(byte)255};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Premature end of InputStream at reading the payload.", e.getMessage());
		}
	}
	
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to read the checksum.
	 */
	public void testCase19 () {
		byte[] expected = {(byte)255,(byte)255,13,5,(byte)200,30,(byte)129,(byte)255,13,2};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Premature end of InputStream at reading the checksum.", e.getMessage());
		}
	}
	
	/**
	 * This test verifies the behavior of the <code>InputStream</code>
	 * constructor if the <code>InputStream</code> fails to read the checksum.
	 */
	public void testCase20 () {
		byte[] expected = {(byte)255,(byte)255,13,5,(byte)200,30,(byte)129,(byte)255,13,2,17};
		
		InputStream is = new ByteArrayInputStream (expected);
		
		try {
			Packet p = new Packet (is);
			assertNull (p);
		} catch (IOException e) {
			assertEquals ("Checksum Error.", e.getMessage());
		}
	}
	
	/**
	 * This test verifies the correct behavior of the <code>getPayload()</code> method.
	 * @throws IOException 
	 */
	public void testCase21 () throws IOException {
		byte type = 13;
		byte[] payload = new byte[] {1,2,3};
		Packet p = new Packet (type, payload);
		byte[] np = p.getPayload ();
		
		assertEquals ("Payload length", payload.length, np.length);
		for (int k=0; k < payload.length; k++)
			assertEquals ("payload at index "+k, payload[k], np[k]);
	}
	
	/**
	 * This test verifies the correct throwing of an <code>IOException</code> if
	 * a <code>Packet</code> would be constructed with too much payload.
	 */
	public void testCase22 () {
		byte type = 13;
		byte[] payload = new byte[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
				13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28,
				29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44,
				45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60,
				61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76,
				77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92,
				93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106,
				107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118,
				119, 120, 121, 122, 123, 124, 125, 126, 127, -128
		};
		try {
			Packet p = new Packet (type, payload);
			fail ();
		} catch (IOException e) {
			assertEquals ("Message too long. Only lengths up to 127 bytes are supported.", e.getMessage());
//			e.printStackTrace();
		}
		
		
	}
}
