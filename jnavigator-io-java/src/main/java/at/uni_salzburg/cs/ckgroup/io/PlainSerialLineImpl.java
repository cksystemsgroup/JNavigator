/*
 * @(#) PlainSerialLineImpl.java 
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
 * Default Serial Line Implementation. This implementation does not implement
 * any security checks. Note this class should <b>NOT</b> be public.
 * 
 * @author Clemens Krainer
 */
class PlainSerialLineImpl extends SerialLineImpl
{
	private Object inputStreamLock = new Object ();
	private SerialLineInputStream serialLineInputStream = null;
	
	/**
	 * Load net library into runtime.
	 */
	static {
		System.loadLibrary("JNavigator");
	}

	/**
	 * Constructs an empty instance.
	 */
	PlainSerialLineImpl() { }

	/**
	 * Constructs an instance with the given file descriptor.
	 */
	PlainSerialLineImpl(FileDescriptor fd) {
		this.fd = fd;
	}
	
	/**
	 * Opens a serial line.
	 * @param name the specified path name of the serial line
	 */
	void open (String name, int baudRate, int dataBits, int stopBits, String parity)
			throws IOException
	{
		this.fd = new FileDescriptor ();
		this.name = name;
		this.baudRate = convertBaudRate (baudRate);
		this.dataBits = convertDataBits (dataBits);
		this.stopBits = convertStopBits (stopBits);
		this.parity = convertParity (parity);
		
//		System.out.println ("PlainSerialLineImpl.open (\"" + name + "\", " + baudRate + ", " + 
//				dataBits + ", " + stopBits + ", " + parity + ")");
//		System.out.println ("PlainSerialLineImpl.open (\"" + name + "\", " + this.baudRate + ", " +
//				this.dataBits + ", "+ this.stopBits + ", " + this.parity + ")");
		serialLineOpen ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.SerialLineImpl#close()
	 */
	protected void close () throws IOException {

		serialLineClose ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.SerialLineImpl#getInputStream()
	 */
	protected InputStream getInputStream () throws IOException {
		
		synchronized (inputStreamLock) {
			if (serialLineInputStream == null)
				serialLineInputStream = new SerialLineInputStream (this);
		}

		return serialLineInputStream;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.SerialLineImpl#getOutputStream()
	 */
	protected OutputStream getOutputStream () throws IOException {

		return new SerialLineOutputStream (this);
	}

    /**
     * Cleans up if the user forgets to close it.
     */
    protected void finalize() throws IOException {
        close();
    }
	
	private native void serialLineOpen () throws IOException;
	
	private native void serialLineClose () throws IOException;
}
