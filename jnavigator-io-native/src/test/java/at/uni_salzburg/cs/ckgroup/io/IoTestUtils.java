/*
 * @(#) IoTestUtils.java
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
import java.io.PrintStream;

public class IoTestUtils {

	public static String toOctalString (byte b) {
		String[] c = {"0","1","2","3","4","5","6","7"};
		return c[(b&0xc0)>>6] + c[(b&0x28)>>3] + c[b&0x07]; 
	}
	
	public static void printOneByte (PrintStream out, byte b) {
		switch (b) {
			case '\n':	out.print ("\\n"); break;
			case '\r':	out.print ("\\r"); break;
			case '\t':	out.print ("\\t"); break;
			case '\b':	out.print ("\\b"); break;
			case '\f':	out.print ("\\f"); break;
			default:
				if (b < ' ' || b > '~') {
					out.print (toOctalString(b));
				} else
					out.print ((char)b);
				break;	
		}
	}

	public static boolean byteArrayCompare (byte[] a, byte[] b, int maxLen) {

		int k;
		for (k=0; k < a.length && k < b.length && k < maxLen; k++)
			if (a[k] != b[k])
				return false;
		
		return a.length == b.length || a.length == maxLen || b.length == maxLen;
	}
	
	public static int readLine (InputStream in, byte[] b) throws IOException {
		int count = 0;
		int ch;
		
		while ( b.length >= count && (ch = in.read ()) >= 0) {
			b[count++] = (byte) ch;
			if (ch == '\n')
				break;
//			System.out.println ("readLine: ch=" + (ch < 32 ? ' ' : (char)ch) + " (" + ch + ")");
		}
		
//		System.out.print ("readline: ");
//		for (int k=0; k < count; k++) System.out.print ((char)b[k]);
//		System.out.println ();
		return count;
	}
	
}
