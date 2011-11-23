/*
 * @(#) MimeParser.java
 *
 * This code is part of the JNavigator project.
 * Copyright (c) 2011  Clemens Krainer
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
package at.uni_salzburg.cs.ckgroup.pilot;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MimeParser {
	
	String contentType;
	String separator;
	String terminator;
	
	public MimeParser(String contentType) {
		if (contentType == null)
			throw new NullPointerException("Content type is null.");
			
		String[] x = contentType.split(";\\s*boundary=");
		
		if (x.length == 0 || !"multipart/form-data".equals(x[0]))
			throw new IllegalArgumentException("Content type is not a valid 'multipart/form-data'.");
		
		separator = "--" + x[1];
		terminator = separator + "--";
	}

	public List<MimeEntry> parse (BufferedReader reader) throws IOException {
		
		List<MimeEntry> list = new ArrayList<MimeEntry>();
		MimeEntry currentEntry = null;
		
		boolean head = false;
		
		String line;
		while ( (line = reader.readLine()) != null) {
			if (line.equals(terminator)) {
//				System.out.println("The end: " + line);
				break;
			}
			
			if (line.equals(separator)) {
//				System.out.println("Delimiter: " + line);
				head = true;
				currentEntry = new MimeEntry();
				list.add(currentEntry);
				continue;
			}
			
			if (head && "".equals(line)) {
				head = false;
				continue;
			}
			
			if (head)
				currentEntry.addHeader(line);
			else
				currentEntry.addBody(line);
		}
		
		return list;
	}
	
}
