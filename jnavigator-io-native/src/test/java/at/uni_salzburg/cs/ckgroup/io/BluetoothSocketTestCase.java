/*
 * @(#) BluetoothSocketTestCase.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import junit.framework.TestCase;

/**
 * @author Clemens Krainer
 *
 */
public class BluetoothSocketTestCase extends TestCase
{
	private final static byte[] MSG_AT_QUERY			= "at\r\n".getBytes ();
	private final static byte[] MSG_AT_CBC_QUERY		= "at+cbc\r\n".getBytes ();

	private final static byte[] MSG_AT_CBC_RESPONSE	= "+CBC: ".getBytes ();
	private final static byte[] MSG_AT_OK_RESPONSE	= "OK\r\n".getBytes ();

	
	public void testCase01 () {
		// intentionally empty.
	}
	
	public void notestCase02 () {
		
		byte[] buf = new byte[512];
		int lines = 0;
		
		try
		{
			BluetoothSocket bs = new BluetoothSocket ("00:80:37:27:1C:E0",2);
			InputStream in = bs.getInputStream ();
			
			
			while (true) {
				int cx = IoTestUtils.readLine (in, buf);
				
				System.out.print (cx + " bytes received: '");
				for (int k=0; k < cx; k++)
					IoTestUtils.printOneByte (System.out, buf[k]);
				System.out.println ("'");
				
				if (cx == -1)
					break;
				if (++lines > 100)
					break;
			}
			
			System.out.println ();
			bs.close ();
			assertTrue (true);
			
			
		} catch (Exception e)
		{
			e.printStackTrace();
			fail();
		}
		
	}
	
	public void notestCase03 () {

		byte[] buf = new byte[512];
//		int lines = 0;

		try
		{
			// BT @ Rubber Mammy
			BluetoothSocket bs = new BluetoothSocket ("00:60:57:2E:C7:C5", 1);
			InputStream in = bs.getInputStream ();
			OutputStream out = bs.getOutputStream ();
			
			while (true) {
				System.out.println ("Check if GSM Modem is ready.");
//				out.write ("ath".getBytes());
				out.write (MSG_AT_QUERY);
				out.flush();
				int cx = IoTestUtils.readLine (in, buf);
				
				System.out.print (cx + " bytes received: '");
				for (int k=0; k < cx; k++)
					IoTestUtils.printOneByte (System.out, buf[k]);
				System.out.println ("'");

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
				
//				if ( byteArrayCompare (buf, MSG_AT_OK_RESPONSE, MSG_AT_OK_RESPONSE.length) )
//					break;
				
				if ( IoTestUtils.byteArrayCompare (buf, MSG_AT_CBC_RESPONSE, MSG_AT_CBC_RESPONSE.length) )
					System.out.println ("Mobile Phone Accu Capacity");
			}		

			System.out.println ();
			bs.close ();
			assertTrue (true);

		} catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}

	}

	public void notestCase04 () {

		try
		{
			Properties props = new Properties ();
			props.setProperty (BluetoothSocket.PROP_BDADDR, "00:60:57:2E:C7:C5");
			props.setProperty (BluetoothSocket.PROP_CHANNEL, "1");
					
			// BT @ Rubber Mammy
			BluetoothSocket bs;
			InputStream in;
			OutputStream out;
			ModemWorker worker;
			
			while (true) {
				bs = new BluetoothSocket (props);
				in = bs.getInputStream ();
				out = bs.getOutputStream ();
				worker = new ModemWorker (in);
				worker.start ();
				Thread.yield ();
				System.out.println ("Check if GSM Modem is ready.");
				out.write (MSG_AT_QUERY);
				out.flush();

				int waitCounter = 5;
				while (worker.isRunning ()) {
					Thread.yield ();
					if (waitCounter-- <= 0) {
						worker.terminate ();
						bs.close ();
					}
					try { Thread.sleep (100); } catch (InterruptedException e) { }
				}
									
				if (worker.okReveiced ())
					break;

				System.out.println ("GSM Modem is not ready yet.");
			}
			System.out.println ("GSM Modem is ready.");
			
			worker.clearBuffer ();
			out.write (MSG_AT_CBC_QUERY);
			out.flush();
			try { Thread.sleep (1000); } catch (InterruptedException e) { }
			
			int repeatCounter = 0;
			while (true) {
				
				if (!worker.okReveiced ()) {
					if (++repeatCounter > 5)
						break;
					
					System.out.println ("No OK message received yet. Taking a little nap.");
					try { Thread.sleep (1000); } catch (InterruptedException e) { }
					continue;
				}

				byte [] b = worker.getBuffer ();
				
				if (b == null) {
					if (++repeatCounter > 5)
						break;
					
					System.out.println ("Noting received. Taking a little nap.");
					try { Thread.sleep (1000); } catch (InterruptedException e) { }
					break;
				}
				
				System.out.print ("\n" + b.length + " bytes received: '");
				for (int k=0; k < b.length; k++)
					IoTestUtils.printOneByte (System.out, b[k]);
				System.out.println ("'");
				
				if ( IoTestUtils.byteArrayCompare (b, MSG_AT_OK_RESPONSE, MSG_AT_OK_RESPONSE.length) )
					break;
				
				if ( IoTestUtils.byteArrayCompare (b, MSG_AT_CBC_RESPONSE, MSG_AT_CBC_RESPONSE.length) )
					System.out.println ("Mobile Phone Accu Capacity");
			}		

			System.out.println ();
			bs.close ();
			assertTrue (true);

		} catch (Exception e)
		{
			e.printStackTrace ();
			fail ();
		}

	}

	
	public class ModemWorker extends Thread {
		
		private static final int RESULT_INDEX_OK = 0;
		private static final int RESULT_INDEX_ERROR = 1;
		private byte[][] resultStrings = { "OK\r\n".getBytes (), "ERROR\r\n".getBytes () };
		private int[] resultIndexes;
		private boolean[] resultDetected;
		
		private InputStream inputStream;
		private byte[] buf = new byte[1024]; 
		private int index;
		private boolean running;
		
		public ModemWorker (InputStream ins) {
			inputStream = ins;
			resultIndexes = new int[resultStrings.length];
			resultDetected = new boolean[resultStrings.length];
		}
		
		public void run () {
			int ch;
			clearBuffer ();
			index = -1;
			running = true;
			
			try
			{
				while ( (ch = inputStream.read ()) >= 0 && running) {
					buf[++index] = (byte)ch;
					IoTestUtils.printOneByte (System.out, (byte)ch);
					running = checkForResultStrings ((byte)ch);
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			running = false;
			System.out.println ("ModemWorker.run: terminated.");
		}
		
		public void terminate () {
			running = false;
			try {
				inputStream.close ();
			} catch (IOException e) {
				e.printStackTrace ();
			}
		}
				
		public boolean okReveiced () {
			return resultDetected[RESULT_INDEX_OK];
		}
		
		public boolean errorReveiced () {
			return resultDetected[RESULT_INDEX_ERROR];
		}
		
		public boolean isRunning () {
			return running;
		}
		
		private boolean checkForResultStrings (byte ch) {
			boolean noneFound = true;
			
			for (int k=0; k < resultStrings.length; k++) {
				if (!resultDetected[k]) {
					++resultIndexes[k];
					if (resultStrings[k][resultIndexes[k]] == ch) {
						if (resultIndexes[k]+1 == resultStrings[k].length) {
							resultDetected[k] = true;
							noneFound = false;
						}
					} else
						resultIndexes[k] = -1;
				}
			}
			
			return noneFound;
		}

		public byte[] getBuffer () {
			
			if (index < 0)
				return null;

			int len = index;
			byte[] b = new byte[len+1];
			for (int k=0; k <= len; k++)
				b[k] = buf[k];

			return b;
		}
		
		public void clearBuffer () {
			
			for (int k=0; k < resultStrings.length; k++) {
				resultIndexes[k] = -1;
				resultDetected[k] = false;
			}
			
			index = -1;
		}

	}

}
