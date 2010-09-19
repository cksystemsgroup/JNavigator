/*
 * @(#) SerialLineImpl.java 
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
 * The abstract class <code>SerialLineImpl</code> is a common superclass
 * of all classes that actually implement serial line communication.
 * <p>
 * A "plain" serial line implements these methods exactly as described.
 *
 * @author Clemens Krainer 
 *
 */
public abstract class SerialLineImpl implements SerialLineOptions
{
	/**
	 * The file descriptor object for this serial line.
	 */
	protected FileDescriptor fd;
	
	/**
	 * The file name of the serial line.
	 */
	protected String name;
	
	/**
	 * The baud rate for this serial line.
	 */
	protected int baudRate;

	/**
	 * The number of data bits for this serial line.
	 */
	protected int dataBits;
	
	/**
	 * The number of stop bits for this serial line.
	 */
	protected int stopBits;
	
	/**
	 * The parity for this serial line.
	 */
	protected int parity;

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
	 * Closes this serial line.
	 *
	 * @exception  IOException  if an I/O error occurs when closing this socket.
	 */
	protected abstract void close () throws IOException;

	/**
	 * Returns the value of this serial line's <code>fd</code> field.
	 *
	 * @return  the value of this serial line's <code>fd</code> field.
	 * @see     at.uni_salzburg.cs.ckgroup.io.SerialLineImpl#fd
	 */
	protected FileDescriptor getFileDescriptor () {
		return fd;
	}

	/**
	 * @param baud the baud rate to be converted to a Bxxx constant
	 * @return the according Bxxx constant value
	 * @throws IllegalArgumentException thrown on invalid baud rates
	 */
	protected int convertBaudRate (int baud) {

		for (int k = 0; k < bauds.length; k++)
			if (bauds[k][0] == baud)
				return bauds[k][1];

		throw new IllegalArgumentException ("invalid baud rate");
	}
		
	/**
	 * @param dataBits the number of data bits to be converted to a CSx constant
	 * @return the according CSx constant value
	 * @throws IllegalArgumentException thrown on an illegal number of data bits
	 */
	protected int convertDataBits (int dataBits) {
		
		switch (dataBits) {
			case 5: return CS5;
			case 6: return CS6;
			case 7: return CS7;
			case 8: return CS8;
			default: throw new IllegalArgumentException ("illegal number of data bits");			
		}
	}
	
	/**
	 * @param stopBits the number of stop bits to be converted
	 * @return the according bit mask
	 * @throws IllegalArgumentException thrown on an invalid number of stop bits
	 */
	protected int convertStopBits (int stopBits) {
		
		switch (stopBits) {
			case 1: return 0;
			case 2: return CSTOPB;
			default: throw new IllegalArgumentException ("illegal number of stop bits");	
		}
	}
	
	/**
	 * @param parity the parity to be converted into a internal representation
	 * @return the internal representation
	 * @throws IllegalArgumentException thrown on an illegal parity value
	 */
	protected int convertParity (String parity) {
		
		if (parity.equalsIgnoreCase ("n"))
			return IGNPAR;
		
		if (parity.equalsIgnoreCase ("e"))
			return 0;
		
		if (parity.equalsIgnoreCase ("o"))
			return PARODD;
		
		throw new IllegalArgumentException ("illegal parity");
	}
}
