/*
 * @(#) SocketWrapper.java
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
import java.net.Socket;

/**
 * This class wraps an existing <code>Socket</code> to an <code>IConnection</code>.
 *  
 * @author Clemens Krainer
 */
public class SocketWrapper implements IConnection {
	
	/**
	 * The socket so be wrapped.
	 */
	private Socket socket;
	
	/**
	 * Construct a <code>SocketWrapper</code> from an existing <code>Socket</code>.
	 * 
	 * @param socket the existing <code>Socket</code>.
	 */
	public SocketWrapper (Socket socket) {
		this.socket = socket;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close() throws IOException {
		socket.close();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

}
