/*
 * @(#) ModemWorkerTestCase.java
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
package at.uni_salzburg.cs.ckgroup.apos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

import junit.framework.TestCase;

/**
 * This test verifies the implementation of class ModemWorker.
 * 
 * @author Clemens Krainer
 */
public class ModemWorkerTestCase extends TestCase
{
	private String testCase01_inputStream =
		"at\r\nOK\r\n" +
		"atd013379502\r\n" +
		"CONNECT 9600\r\n" +
		"Trying GpsNet10500 (10.254.98.11, 10500)... Open\r\n" +
		"???\r\n" +
		"???\r\n";
	
	private int [] testCase01_expectedResult = { 63, 63, 63, 13, 10, -1 };


	/**
	 * Connect to a fake modem that behaves like the APOS GSM service.
	 */
	public void testCase01 () {
		Properties props = new Properties ();
		props.setProperty ("className", "at.uni_salzburg.cs.ckgroup.apos.AposGsmModemMock");
		props.setProperty ("input", testCase01_inputStream);
		
		ModemWorker worker = new ModemWorker (props);
		assertNotNull (worker);
		
		try
		{
			worker.connect (3);
			byte[][] resultStrings = new byte[1][];
			resultStrings[0] = "???\r\n".getBytes ();
			
			byte[][] result = worker.executeCommand ("atd013379502\r\n".getBytes (), resultStrings, 1);
			
			if (result[0] != null) {
				System.out.write ("Result[0]: '".getBytes ());
				System.out.write (result[0]);
				System.out.write ("'\nResult[1]: ".getBytes ());
				System.out.write (result[1]);
				System.out.write ("'\n".getBytes ());
				System.out.flush ();
			}
			
			IConnection connection = worker.getConnection ();
			InputStream in = connection.getInputStream ();
			
			System.out.println ("More data:");
			for (int k=0; k < testCase01_expectedResult.length; k++) {
				int ch = in.read ();
				/* This is weird, but never cast -1 to a char and write it when running
				 * in an IBM JVM. It causes the JVM to loop endlessly. 
				 */
				System.out.write ( ch >= 0 ? (char)ch : '\n');
				assertEquals ("[" + k + "]=" + testCase01_expectedResult[k] + " <-> " + ch, testCase01_expectedResult[k], ch);
			}
			System.out.println ();
			
		} catch (IOException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
	
	/**
	 * Connect to a real modem over Bluetooth.
	 */
	public void notestCase02 () {
		Properties props = new Properties ();
		props.setProperty ("className", "at.uni_salzburg.cs.ckgroup.io.BluetoothSocket");
//		props.setProperty (at.uni_salzburg.cs.ckgroup.io.BluetoothSocket.PROP_BDADDR, "00:60:57:2E:C7:C5"); // Rubber Mammy
		props.setProperty (at.uni_salzburg.cs.ckgroup.io.BluetoothSocket.PROP_BDADDR, "00:17:B0:1D:86:8D"); // Rubber Nanny
		props.setProperty (at.uni_salzburg.cs.ckgroup.io.BluetoothSocket.PROP_CHANNEL, "1");
		ModemWorker worker = new ModemWorker (props);
		assertNotNull (worker);
		
		try
		{
			worker.connect (3);
			byte[][] resultStrings = new byte[1][];
			resultStrings[0] = "???\r\n".getBytes ();

			byte[][] result = worker.executeCommand ("atd013379502\r\n".getBytes (), resultStrings, 1);
//			byte[][] result = worker.executeCommand ("atd06647636314\r\n".getBytes (), resultStrings, 1);
			
			if (result[0] != null) {
				System.out.write ("Result[0]: '".getBytes ());    System.out.write (result[0]);
				System.out.write ("'\nResult[1]: ".getBytes ());  System.out.write (result[1]);  System.out.write ("'\n".getBytes ());
				System.out.flush ();
			}
			
			IConnection connection = worker.getConnection ();
			OutputStream out = connection.getOutputStream ();
			out.write ("$GPGGA,223339.00,4759.43570,N,01256.20953,E,1,06,1.42,434.8,M,46.6,M,,*5F\r\n".getBytes ());
			out.flush ();
			
			InputStream in = connection.getInputStream ();
			
			System.out.println ("More data:");
			for (int k=0; k < 400; k++) {
				int ch = in.read ();
				System.out.write ((char)ch);
			}
			System.out.println ();
			
			// Does not work (jet).
//			result = worker.executeCommand ("+++ath\r\n+++at+chup\r\n".getBytes (), null, 1);
//			System.out.write ("Result[0]: '".getBytes ());    System.out.write (result[0]);
//			System.out.write ("'\nResult[1]: ".getBytes ());  System.out.write (result[1]);  System.out.write ("'\n".getBytes ());
//			System.out.flush ();
			
		} catch (IOException e)
		{
			e.printStackTrace();
			fail ();
		}
	}
}
