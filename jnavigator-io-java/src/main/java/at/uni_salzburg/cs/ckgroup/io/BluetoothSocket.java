/*
 * @(#) BluetoothSocket.java
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
import java.util.Properties;

/**
 * @author     Clemens Krainer
 */
public class BluetoothSocket implements IConnection
{
	/**
	 * Constants for property keys. 
	 */
	public static final String PROP_BDADDR = "bdaddr";
	public static final String PROP_CHANNEL = "channel";
	
    /**
	 * Various states of this bluetooth socket.
	 * @uml.property  name="closed"
	 */
	private boolean closed;
    private Object closeLock = new Object();
    
    /**
     * The implementation of this BluetoothSocket.
     */
	private BluetoothSocketImpl impl;
    
	/**
	 * The destination bluetooth device address
	 */
	private String bdaddr;
	
	/**
	 * The channel number of the destination device
	 */
	private int channel;
	
	/**
	 * Construct a BluetoothSocket object.
	 * 
	 * @param bdaddr the destination bluetooth device address
	 * @param channel the channel number of the destination device
	 * @throws BluetoothSocketException thrown in case of errors
	 */
	public BluetoothSocket (String bdaddr, int channel) throws BluetoothSocketException {
		
		this.bdaddr = bdaddr;
		this.channel = channel;

		createImpl();
	}
	
	/**
	 * Construct a BluetoothSocket object.
	 * 
	 * @param props the properties to construct the BluetoothSocket object
	 * @throws BluetoothSocketException thrown in case of errors
	 */
	public BluetoothSocket (Properties props) throws BluetoothSocketException {
		
		bdaddr = props.getProperty (PROP_BDADDR);
		String chan = props.getProperty (PROP_CHANNEL);
		
		if (bdaddr == null || bdaddr.equals (""))
			throw new BluetoothSocketException ("Missing property " + PROP_BDADDR);
		
		if (chan == null || chan.equals (""))
			throw new BluetoothSocketException ("Missing property " + PROP_CHANNEL);		
		
		channel = Integer.parseInt (chan);

		createImpl();
	}
	
	/**
     * Creates the serial line implementation.
     *
	 * @throws SerialLineException if creation fails
	 */
	void createImpl () throws BluetoothSocketException {
		
		PlainBluetoothSocketImpl plain = new PlainBluetoothSocketImpl ();
		
		try {
			plain.connect (bdaddr, channel);
	    } catch (IOException e) {
	        throw new BluetoothSocketException (e.getMessage ());
	    }

		closed = false;
		impl = plain;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close () throws IOException {
        synchronized (closeLock) {
            if (isClosed ())
                return;
            impl.close ();
            closed = true;
        }
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream () throws IOException {
        if (isClosed ())
            throw new BluetoothSocketException ("Serial line is closed");

        return impl.getInputStream ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream () throws IOException {
    	if (isClosed ())
            throw new BluetoothSocketException ("Serial line is closed");

        return impl.getOutputStream ();
	}

    /**
     * Converts this bluetooth socket to a <code>String</code>.
     *
     * @return  a string representation of this bluetooth socket.
     */
    public String toString () {

        return "BluetoothSocket[bdaddr=" + bdaddr +
            ",channel=" + channel +
            ",open=" + (isClosed() ? "false" : "true") + "]";
    }

    /**
	 * Returns the closed state of the bluetooth socket.
	 * 
	 * @return  true if the bluetooth socket has been closed
	 * @see  #close
	 * @uml.property  name="closed"
	 */
    public boolean isClosed () {
        synchronized(closeLock) {
            return closed;
        }
    }
}
