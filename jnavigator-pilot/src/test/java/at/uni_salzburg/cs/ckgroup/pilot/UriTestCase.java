/*
 * @(#) UriTestCase.java
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

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

public class UriTestCase {

	@Test
	public void testCase01 () throws URISyntaxException {
		String[] uris = {
			"udp://localhost:7000",
			"udp://user:password@localhost:7000",	
			"udp://localhost:7000",
			"tcp://user:password@localhost:7000",
			"notes://user:password@localhost:7000",
			"notes://user:password@localhost:999999/buggerit",
		};
		
		for (String uri : uris) {
			URI u = new URI(uri);
			printURI(u);
		}
	}
	
	public void printURI (URI u) {
		System.out.println("URI: " + u);
		System.out.println("URI: scheme=" + u.getScheme());
		System.out.println("URI: userInfo=" + u.getUserInfo());
		System.out.println("URI: host=" + u.getHost());
		System.out.println("URI: port=" + u.getPort());
		System.out.println("URI: path=" + u.getPath());
		System.out.println("URI: query=" + u.getQuery());
//		System.out.println("URI: schemeSpecificPart=" + u.getSchemeSpecificPart());
		System.out.println();
	}
	
	
}
