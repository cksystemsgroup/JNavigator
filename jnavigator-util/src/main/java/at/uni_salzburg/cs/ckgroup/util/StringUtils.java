/*
 * @(#) StringUtils.java
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

import java.util.Vector;

/**
 * This class implements some basic string manipulation functionality, because
 * the IBM WSRT JVM for ARM embedded systems lacks compatibility to more recent
 * Java versions.
 * 
 * @author Clemens Krainer
 */
public class StringUtils {

	/**
	 * Split a given <code>String</code> object on a given character and trim
	 * every string in the result. The equivalent Java 1.4 code is
	 * 
	 * <pre>
	 * String[] otherStrings = someString.trim().split(&quot;\\s*,\\s*&quot;);
	 * </pre>
	 * 
	 * @param line
	 *            the line to be splitted as a <code>String</code> object.
	 * @return
	 */
	public static String[] splitOnCharAndTrim (char splitter, String line) {
		Vector strs = new Vector ();
		
		StringBuffer buf = new StringBuffer ();
		for (int k=0; k < line.length(); ++k) {
			char ch = line.charAt(k);
			if (ch == splitter) {
				strs.add(buf);
				buf = new StringBuffer ();
			} else {
				buf.append(ch);
			}
		}
		if (line.length() != 0)
			strs.add(buf);
		
		String [] sa = new String [strs.size()];
		
		for (int i=0; i < sa.length; ++i) {
			StringBuffer e = (StringBuffer) strs.elementAt(i);
			if (e.length() == 0) {
				sa[i] = "";
				continue;
			}
			int o = 0;
			while (e.charAt(o) == ' ') ++o;
			int l = e.length()-1;
			while (e.charAt(l) == ' ') --l;
			sa[i] = e.substring(o, l+1);
		}
		
		return sa;
	}
}
