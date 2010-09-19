/*
 * @(#) IConnectionMock.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

/**
 * This implementation mimics an IConnection.
 * 
 * @author Clemens Krainer
 */
public class IConnectionMock implements IConnection
{
	/**
	 * The Properties of this IConnectionMock object.
	 */
	public Properties properties;
	
	/**
	 * Construct a IConnectionMock object.
	 * 
	 * @param properties the properties to be used
	 */
	public IConnectionMock (Properties properties) {
		this.properties = properties;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close () throws IOException {

	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream () throws IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream () throws IOException {
		return null;
	}

}
