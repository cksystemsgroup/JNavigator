/*
 * @(#) SerialLine.java
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author Clemens Krainer
 */
public class SerialLine implements IConnection
{
	/**
	 * Constants for Property keys.
	 */
	public final static String PROP_INTERFACE = "interface";
	public final static String PROP_BAUD_RATE = "baudRate";
	public final static String PROP_DATA_BITS = "dataBits";
	public final static String PROP_STOP_BITS = "stopBits";
	public final static String PROP_PARITY    = "parity";
		
    /**
	 * Various states of this serial line.
	 * @uml.property  name="closed"
	 */
	private boolean closed;
    private Object closeLock = new Object();

    /**
     * The implementation of this SerialLine.
     */
	private SerialLineImpl impl;
	
	/**
	 * The file representation of this SerialLine, e.g. new File("/dev/ttyS0")
	 */
	private File file;
	
	/**
	 * The baud rate of this SerialLine, e.g. 115200 baud 
	 */
	private int baudRate;
	
	/**
	 * The number of data bits for this serial line.
	 */
	protected int dataBits;
	
	/**
	 * The number of stop bits of this SerialLine, e.g. 1 
	 */
	private int stopBits;
	
	/**
	 * The parity of this SerialLine, e.g. "N". 
	 */
	private String parity;	

	/**
	 * Construct a SerialLine object.
	 * 
	 * @param file the File representation of this serial line
	 * @param baudRate the requested baud rate of this serial line
	 * @param dataBits the number of data bits of this serial line
	 * @param stopBits the number of stop bits of this serial line
	 * @param parity the parity of this serial line
	 * @throws SerialLineException if creation fails
	 */
	public SerialLine (File file, int baudRate, int dataBits, int stopBits, String parity) throws SerialLineException {
		
		this.file = file;
		this.baudRate = baudRate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;
		
		createImpl();
	}
	
	/**
	 * Construct a SerialLine object.
	 * 
	 * @param properties the properties to be used to construct the serial line object.
	 * @throws SerialLineException if creation fails
	 */
	public SerialLine (Properties properties) throws SerialLineException {
		
		String serialInterface = properties.getProperty (PROP_INTERFACE,"/dev/ttyS0");
		
		file = new File (serialInterface);
		baudRate = Integer.parseInt (properties.getProperty (PROP_BAUD_RATE, "115200"));
		dataBits = Integer.parseInt (properties.getProperty (PROP_DATA_BITS, "8"));
		stopBits = Integer.parseInt (properties.getProperty (PROP_STOP_BITS, "1"));
		parity = properties.getProperty (PROP_PARITY, "n");
		
		createImpl();
	}
	
	/**
     * Creates the serial line implementation.
     *
	 * @throws SerialLineException if creation fails
	 */
	void createImpl () throws SerialLineException {
		
		PlainSerialLineImpl plain = new PlainSerialLineImpl ();
		
		try {
			plain.open (file.getAbsolutePath (), baudRate, dataBits, stopBits, parity);
	    } catch (IOException e) {
	        throw new SerialLineException (e.getMessage ());
	    }

		closed = false;
		impl = plain;
	}
	
    /**
     * Returns an input stream for this serial line.
     *
     * @return    an input stream for reading bytes from this serial line.
     * @exception IOException  if an I/O error occurs when creating the
     *              input stream, the serial line is closed.
     */
    public InputStream getInputStream () throws IOException {
        if (isClosed ())
            throw new SerialLineException("Serial line is closed");

        return impl.getInputStream ();
    }
    
    /**
     * Returns an output stream for this serial line.
     *
     * @return    an output stream for writing bytes to this serial line.
     * @exception IOException  if an I/O error occurs when creating the
     *               output stream or if the serial line is not connected.
     */
    public OutputStream getOutputStream () throws IOException {

    	if (isClosed ())
            throw new SerialLineException("Serial line is closed");

        return impl.getOutputStream ();
    }
    
    /**
     * Closes this serial line.
     * <p>
     * Once a serial line has been closed, it is not available for further
     * use (i.e. can't be reconnected or rebound). A new serial line needs
     * to be created.
     * 
     * @exception IOException  if an I/O error occurs when closing this
     *                serial line.
     * @see #isClosed
     */
    public void close () throws IOException {
    	
        synchronized (closeLock) {
            if (isClosed ())
                return;
            impl.close ();
            closed = true;
        }
    }

    /**
     * Converts this serial line to a <code>String</code>.
     *
     * @return  a string representation of this serial line.
     */
    public String toString () {

        return "SerialLine[speed=" + baudRate +
            ",parity=" + parity +
            ",stopbits=" + stopBits +
            ",open=" + (isClosed() ? "false" : "true") + "]";
    }

    /**
	 * Returns the closed state of the serial line.
	 * 
	 * @return  true if the serial line has been closed
	 * @see  #close
	 * @uml.property  name="closed"
	 */
    public boolean isClosed () {
        synchronized(closeLock) {
            return closed;
        }
    }

}
