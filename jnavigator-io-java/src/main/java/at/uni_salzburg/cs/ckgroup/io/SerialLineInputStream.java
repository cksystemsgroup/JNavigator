/*
 * @(#) SerialLineInputStream.java 
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
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This stream extends FileInputStream to implement a SerialLineInputStream. Note that this class should <b>NOT</b> be public.
 * @author     Clemens Krainer
 */
class SerialLineInputStream extends FileInputStream
{
	private PlainSerialLineImpl impl;
	private boolean eof;
	private byte[] temp = new byte[1];
	
	public SerialLineInputStream (PlainSerialLineImpl impl)
	{
		super(impl.fd);
		this.impl = impl;
	}
	
    /**
     * Reads into an array of bytes at the specified offset using
     * the received serial line primitive.
     * @param fd the FileDescriptor
     * @param b the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @param timeout the read timeout in ms
     * @return the actual number of bytes read, -1 is
     *          returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    private native int serialLineRead0 (FileDescriptor fd,
                                   byte b[], int off, int len)
        throws IOException;

    /**
     * Reads into a byte array data from the serial line.
     * @param b the buffer into which the data is read
     * @return the actual number of bytes read, -1 is
     *          returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read (byte b[]) throws IOException {
        return read (b, 0, b.length);
    }

    /**
     * Reads into a byte array <i>b</i> at offset <i>off</i>,
     * <i>length</i> bytes of data.
     * @param b the buffer into which the data is read
     * @param off the start offset of the data
     * @param len the maximum number of bytes read
     * @return the actual number of bytes read, -1 is
     *          returned when the end of the stream is reached.
     * @exception IOException If an I/O error has occurred.
     */
    public int read (byte b[], int off, int length) throws IOException {

        // EOF already encountered
        if (eof) {
            return -1;
        }

        // bounds check
        if (length <= 0 || off < 0 || off + length > b.length) {
            if (length == 0) {
                return 0;
            }
            throw new ArrayIndexOutOfBoundsException ();
        }

        int n = serialLineRead0 (impl.fd, b, off, length);
        
        if (n > 0) {
            return n;
        }

        eof = true;
        return -1;        
    }
    
    /**
     * Reads a single byte from the serial line.
     */
    public int read () throws IOException {
    	
        if (eof) {
            return -1;
        }
        
        temp[0] = 17;
        int n = read (temp, 0, 1);
        
        if (n <= 0) {
            return -1;
        }
        
        return temp[0] & 0xff;
    }

    /**
     * Skips n bytes of input.
     * @param n the number of bytes to skip
     * @return  the actual number of bytes skipped.
     * @exception IOException If an I/O error has occurred.
     */
    public long skip (long numbytes) throws IOException {
    	throw new UnsupportedOperationException ("Method not implemented.");
    }
    
    /**
     * Closes the stream.
     */
    private boolean closing = false;
    public void close () throws IOException {

        if (closing)
            return;

        closing = true;

        impl.close();
        closing = false;
    }

    void setEOF(boolean eof) {
        this.eof = eof;
    }

    /**
     * Overrides finalize, the fd is closed by the SerialLine.
     */
    protected void finalize () {}

}
