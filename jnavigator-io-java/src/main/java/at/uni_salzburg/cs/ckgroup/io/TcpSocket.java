/*
 * @(#) TcpSocket.java
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
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author Clemens Krainer
 */
public class TcpSocket extends Socket implements IConnection
{
	/**
	 * Constants for property keys. 
	 */
	public static final String PROP_HOST = "host";
	public static final String PROP_PORT = "port";
	
	/**
	 * Construct a TCP/IP client connection.
	 * 
	 * @param host the server host name or IP address to connect to.
	 * @param port the server port number to connect to.
	 * @throws UnknownHostException thrown if the server host name or IP address is not valid.
	 * @throws IOException thrown in case of errors.
	 */
	public TcpSocket (String host, int port) throws UnknownHostException, IOException {
		
		super (host, port);
	}

	/**
	 * Construct a TCP/IP client connection.
	 * 
	 * @param props the properties to construct the TcpSocket object
	 * @throws UnknownHostException thrown if the server host name or IP address is not valid.
	 * @throws IOException thrown in case of errors.
	 */
	public TcpSocket (Properties props) throws UnknownHostException, IOException {
		
		super (props.getProperty (PROP_HOST), Integer.parseInt (props.getProperty (PROP_PORT)));
	}


}
