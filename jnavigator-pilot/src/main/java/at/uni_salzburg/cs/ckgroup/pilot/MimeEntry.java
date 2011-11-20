/*
 * @(#) MimeEntry.java
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

import java.util.HashMap;
import java.util.Map;

public class MimeEntry {
	
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String CONTENT_TYPE = "Content-Type";
	
	private Map<String,String> headerMap = new HashMap<String,String>();
	private StringBuilder body = new StringBuilder();
	
	public void addHeader (String header) {
		String x[] = header.split(":\\s*",2);
		headerMap.put(x[0],x[1]);
	}
	
	public void addBody (String line) {
		body.append(line).append("\n");
	}
	
	public String getBody() {
		return body.toString();
	}
	
	public Map<String,String> getHeaders () {
		return headerMap;
	}
}