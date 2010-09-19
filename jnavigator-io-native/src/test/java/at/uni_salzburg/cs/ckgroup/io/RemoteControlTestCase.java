/*
 * @(#) RemoteControlTestCase.java
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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.net.URL;

import junit.framework.TestCase;

/**
 * @author Clemens Krainer
 *
 */
public class RemoteControlTestCase extends TestCase
{
	private final static byte[] MSG_AT_CBC_QUERY		= "at+cbc\r\n".getBytes ();

	private final static byte[] MSG_AT_CBC_RESPONSE	= "+CBC: ".getBytes ();
	private final static byte[] MSG_AT_OK_RESPONSE	= "OK\r\n".getBytes ();

	
	public void testCase01 () {
		// intentionally empty.
	}
	
	public void notestCase02 () {
		
		File serialLine = new File ("/dev/ttyUSB1");
		byte[] buf = new byte[30];
		
		try {
			SerialLine p = new SerialLine (serialLine, 115200, 8, 1, "n");
			
			InputStream in = p.getInputStream ();
			OutputStream out = p.getOutputStream ();
			
			out.write (MSG_AT_CBC_QUERY);
		
			
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
			assertTrue (false);
		}
	}
	
	public void notestCase03 () {
		
		File serialLine = new File ("/dev/ttyUSB1");
		URL url = Thread.currentThread ().getContextClassLoader ().getResource ("at/uni_salzburg/cs/ckgroup/remote_control_test_data.dat");
		
		assertNotNull (url);
		
		String fileName = url.getFile ();
		
		System.out.println ("Filename="+fileName);
		File file = new File (fileName);
		
		try
		{
			SerialLine p = new SerialLine (serialLine, 115200, 8, 1, "n");
			
			OutputStream out = p.getOutputStream ();
			
			FileReader fr = new FileReader (file);
			LineNumberReader lnr = new LineNumberReader (fr);

			while (true) {
				
				String line = lnr.readLine ();
				
				if (line == null)
					break;
				
				System.out.print ("Read: " + line + "   ");
				String [] values = line.trim ().split ("\\s*;\\s*");
				int header = 0x02;
				int v0 = Integer.parseInt (values[0]);
				int v1 = Integer.parseInt (values[1]);
				int v2 = Integer.parseInt (values[2]);
				int v3 = Integer.parseInt (values[3]);
				int cs = header ^ v0 ^ v1 ^ v2 ^ v3;

//				out.write (header);
//				out.write (v0);
//				out.write (v1);
//				out.write (v2);
//				out.write (v3);
//				out.write (cs);

				byte [] msg = new byte [6];
				msg[0] = (byte) header;
				msg[1] = (byte) v0;
				msg[2] = (byte) v1;
				msg[3] = (byte) v2;
				msg[4] = (byte) v3;
				msg[5] = (byte) cs;
				
				out.write (msg);
				
				
				System.out.println ("Writing: "+header+" "+v0+" "+v1+" "+v2+" "+v3+" "+cs);
				Thread.sleep (100);
			}
			
			p.close ();
			
		} catch (IOException e)
		{
			e.printStackTrace();
			fail ();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
			fail ();
		}
		
	}
	
	
}
