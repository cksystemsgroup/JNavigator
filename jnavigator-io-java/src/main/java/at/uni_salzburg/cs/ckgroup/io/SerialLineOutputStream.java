/*
 * @(#) SerialLineOutputStream.java 
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
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This stream extends FileOutputStream to implement a SerialLineOutputStream. Note that this class should <b>NOT</b> be public.
 * @author     Clemens Krainer
 */
class SerialLineOutputStream extends FileOutputStream
{
	private PlainSerialLineImpl impl;
	private byte temp[] = new byte[1];
	
	public SerialLineOutputStream (PlainSerialLineImpl impl)
	{
		super (impl.fd);
		this.impl = impl;
	}

    /**
     * Writes to the serial line.
     * @param fd the FileDescriptor
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private native void serialLineWrite0 (FileDescriptor fd, byte[] b, int off,
                                     int len) throws IOException;

    /**
     * Writes to the serial line with appropriate locking of the
     * FileDescriptor.
     * @param b the data to be written
     * @param off the start offset in the data
     * @param len the number of bytes that are written
     * @exception IOException If an I/O error has occurred.
     */
    private void serialLineWrite (byte b[], int off, int len) throws IOException {

        if (len <= 0 || off < 0 || off + len > b.length) {
            if (len == 0) {
                return;
            }
            throw new ArrayIndexOutOfBoundsException ();
        }

        serialLineWrite0 (impl.fd, b, off, len);
    }

    /**
     * Writes a byte to the serial line.
     * @param b the data to be written
     * @exception IOException If an I/O error has occurred.
     */
    public void write (int b) throws IOException {
        temp[0] = (byte)b;
        serialLineWrite (temp, 0, 1);
    }

    /**
     * Writes the contents of the buffer <i>b</i> to the serial line.
     * @param b the data to be written
     * @exception IOException If an I/O error has occurred.
     */
    public void write (byte b[]) throws IOException {
    	serialLineWrite (b, 0, b.length);
    }

    /**
     * Closes the stream.
     */
    private boolean closing = false;
    public void close () throws IOException {

        if (closing)
            return;

        impl.close();
        
        closing = false;
    }

    /**
     * Overrides finalize, the fd is closed by the SerialLine.
     */
    protected void finalize () {}

}
