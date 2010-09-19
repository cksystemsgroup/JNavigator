/*
 * @(#) BluetoothSocketImpl.java
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

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The abstract class <code>BluetoothSocketImpl</code> is a common superclass
 * of all classes that actually implement bluetooth socket communication.
 * <p>
 * A "plain" bluetooth socket implements these methods exactly as described.
 *
 * @author Clemens Krainer 
 *
 */
public abstract class BluetoothSocketImpl implements BluetoothSocketOptions
{
	/**
	 * The file descriptor object for this bluetooth socket.
	 */
	protected FileDescriptor fd;
	
	/**
	 * The destination address 
	 */
	protected String bdaddr;
	
	/**
	 * The channel number
	 */
	protected int channel;
	
	/**
	 * Returns an input stream for this socket.
	 *
	 * @return    a stream for reading from this socket.
	 * @exception IOException  if an I/O error occurs when creating the
	 *              input stream.
	 */
	protected abstract InputStream getInputStream () throws IOException;

	/**
	 * Returns an output stream for this socket.
	 *
	 * @return     an output stream for writing to this socket.
	 * @exception  IOException  if an I/O error occurs when creating the
	 *               output stream.
	 */
	protected abstract OutputStream getOutputStream () throws IOException;

	/**
	 * Closes this bluetooth socket.
	 *
	 * @exception  IOException  if an I/O error occurs when closing this socket.
	 */
	protected abstract void close () throws IOException;
	
	/**
	 * Returns the value of this bluetooth socket's <code>fd</code> field.
	 *
	 * @return  the value of this bluetooth socket's <code>fd</code> field.
	 * @see     at.uni_salzburg.cs.ckgroup.io.BluetoothSocketImpl#fd
	 */
	protected FileDescriptor getFileDescriptor () {
		return fd;
	}
	
}
