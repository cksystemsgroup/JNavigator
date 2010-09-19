/*
 * @(#) IConnection.java
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

/**
 * @author Clemens Krainer
 *
 */
public interface IConnection
{
    /**
     * Returns an input stream for this connection.
     *
     * @return    an input stream for reading bytes from this serial line.
     * @exception IOException  if an I/O error occurs when creating the
     *              input stream, the serial line is closed.
     */
    public InputStream getInputStream () throws IOException;
    
    /**
     * Returns an output stream for this connection.
     *
     * @return    an output stream for writing bytes to this serial line.
     * @exception IOException  if an I/O error occurs when creating the
     *               output stream or if the serial line is not connected.
     */
    public OutputStream getOutputStream () throws IOException;
    
    /**
     * Closes this connection.
     * <p>
     * Once a connection has been closed, it is not available for further
     * use (i.e. can't be reconnected or rebound). A new connection needs
     * to be created.
     * 
     * @exception IOException  if an I/O error occurs when closing this
     *                connection.
     */
    public void close () throws IOException;
}
