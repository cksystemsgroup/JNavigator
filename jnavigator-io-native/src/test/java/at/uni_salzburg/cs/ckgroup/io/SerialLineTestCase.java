/*
 * @(#) SerialLineTestCase.java
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
package at.uni_salzburg.cs.ckgroup.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

/**
 * @author Clemens Krainer
 *
 */
public class SerialLineTestCase extends TestCase
{
	private final static byte[] MSG_AT_QUERY			= "at\r\n".getBytes ();
	private final static byte[] MSG_AT_CBC_QUERY		= "at+cbc\r\n".getBytes ();

	private final static byte[] MSG_AT_CBC_RESPONSE	= "+CBC: ".getBytes ();
	private final static byte[] MSG_AT_OK_RESPONSE	= "OK\r\n".getBytes ();

	
	public void testCase01 () {
		// intentionally empty.
	}
	
	public void notestCase02 () {
		
		File serialLine = new File ("/dev/ttyS0");
		
		try {
			SerialLine p = new SerialLine (serialLine, 115200, 8, 1, "n");
			
			p.close ();
			assertTrue (true);
		}
		catch (IOException e) {
			assertTrue (false);
		}
	}

	public void notestCase03 () {
		
		File serialLine = new File ("/dev/ttyUSB9");
		
		try {
			SerialLine p = new SerialLine (serialLine, 115200, 8, 1, "n");
			
			assertTrue (p == null);
		}
		catch (IOException e) {
			assertEquals (e.getMessage (), "Can not find specified path name.");
		}
	}
	
	public void notestCase04 () {
		
		File serialLine = new File ("/dev/ttyUSB0");
		byte[] buf = new byte[30];
		
		try {
			SerialLine p = new SerialLine (serialLine, 115200, 8, 1, "n");
			
			InputStream in = p.getInputStream ();
			OutputStream out = p.getOutputStream ();
			
			while (true) {
				System.out.println ("Check if GSM Modem is ready.");
//				out.write ("ath".getBytes());
				out.write (MSG_AT_QUERY);
				out.flush();
				int cx = IoTestUtils.readLine (in, buf);
				if (cx == MSG_AT_OK_RESPONSE.length && IoTestUtils.byteArrayCompare (buf, MSG_AT_OK_RESPONSE, MSG_AT_OK_RESPONSE.length) )
					break;
				System.out.println ("GSM Modem is not ready yet.");
				try { Thread.sleep (1000); } catch (InterruptedException e) { }
			}
			System.out.println ("GSM Modem is ready.");
				
			out.write (MSG_AT_CBC_QUERY);
			out.flush();
			
			while (true) {
				int cx = IoTestUtils.readLine (in, buf);
				
				System.out.print (cx + " bytes received: '");
				for (int k=0; k < cx; k++)
					IoTestUtils.printOneByte (System.out, buf[k]);
				System.out.println ("'");
				
				if (cx == -1)
					break;
				
				if ( IoTestUtils.byteArrayCompare (buf, MSG_AT_OK_RESPONSE, MSG_AT_OK_RESPONSE.length) )
					break;
				
				if ( IoTestUtils.byteArrayCompare (buf, MSG_AT_CBC_RESPONSE, MSG_AT_CBC_RESPONSE.length) )
					System.out.println ("Mobile Phone Accu Capacity");
			}
			
			System.out.println ();
			p.close ();
			assertTrue (true);
		}
		catch (IOException e) {
			e.printStackTrace ();
			fail ();
		}
	}


	
	
}
