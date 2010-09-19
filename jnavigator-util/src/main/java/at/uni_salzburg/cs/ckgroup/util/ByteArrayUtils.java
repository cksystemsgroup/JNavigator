/*
 * @(#) ByteArrayUtils.java
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
package at.uni_salzburg.cs.ckgroup.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * This class implements some methods for handling byte arrays.
 * 
 * @author Clemens Krainer
 */
public class ByteArrayUtils {
	
	/**
	 * Reverse the order of a given byte array
	 *  
	 * @param a the byte array to be reversed.
	 * @return the byte array in reversed order.
	 */
	public static byte[] reverse (byte[] a) {
		byte[] b = new byte[a.length];
		for (int k=0; k < a.length; k++)
			b[k] = a[a.length-1-k];
		return b;
	}
	
	/**
	 * Extract a partition of a byte array.
	 * 
	 * @param a the byte array
	 * @param from the index of the first byte in the given byte array
	 * @param length the number of bytes to extract
	 * @return the result as a byte array
	 */
	public static byte[] partition (byte[] a, int from, int length) {
		byte[] b = new byte[length];
		for (int k=0; k < length; k++)
			b[k] = a[from + k];
		return b;
	}
	
	/**
	 * Convert an integer to a byte array.
	 * 
	 * @param i the integer number to be converted.
	 * @return the integer represented as a byte array.
	 * @throws IOException thrown in case of conversion errors.
	 */
	public static byte[] int2bytes (int i) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream ();
		DataOutputStream dOut = new DataOutputStream (bOut);
		dOut.writeInt(i);
		return bOut.toByteArray();
	}
	
	/**
	 * Convert a double to a byte array.
	 * 
	 * @param d the double number to be converted.
	 * @return the double represented as a byte array.
	 * @throws IOException thrown in case of conversion errors.
	 */
	public static byte[] double2bytes (double d) throws IOException {
		ByteArrayOutputStream bOut = new ByteArrayOutputStream ();
		DataOutputStream dOut = new DataOutputStream (bOut);
		dOut.writeDouble(d);
		return bOut.toByteArray();
	}
	
	/**
	 * Convert a byte array back to an integer number.
	 *  
	 * @param a the byte array representing the integer.
	 * @return the converted number as an integer.
	 * @throws IOException thrown in case of conversion errors.
	 */
	public static int bytes2int (byte[] a) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream (a);
		DataInputStream din = new DataInputStream(bin);
		return din.readInt();
	}
	
	/**
	 * convert a byte array back to a double number.
	 * 
	 * @param a the byte array representing the double.
	 * @return the converted number as a double.
	 * @throws IOException thrown in case of conversion errors.
	 */
	public static double bytes2double (byte[] a) throws IOException {
		ByteArrayInputStream bin = new ByteArrayInputStream (a);
		DataInputStream din = new DataInputStream(bin);
		return din.readDouble();
	}
}
