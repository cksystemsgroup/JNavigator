/*
 * @(#) PlainBluetoothSocketImpl.java
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
 * Default Bluetoot Socket Implementation. This implementation does not
 * implement any security checks. Note this class should <b>NOT</b> be public.
 * 
 * @author Clemens Krainer
 */
class PlainBluetoothSocketImpl extends BluetoothSocketImpl
{
	private Object inputStreamLock = new Object ();
	private BluetoothSocketInputStream bluetoothSocketInputStream = null;
	
	/**
	 * Load net library into runtime.
	 */
	static {
		System.loadLibrary("JNavigator");
	}

	/**
	 * Constructs an empty instance.
	 */
	PlainBluetoothSocketImpl() { }

	/**
	 * Constructs an instance with the given file descriptor.
	 */
	PlainBluetoothSocketImpl(FileDescriptor fd) {
		this.fd = fd;
	}
	
	/**
	 * Connect to a Bluetooth device.
	 * 
	 * @param bdaddr the Bluetooth hardware address of the destination device.
	 * @param channel the channel to connect to.
	 * @throws IOException thrown in case of errors.
	 */
	void connect (String bdaddr, int channel) throws IOException {
		
		this.fd = new FileDescriptor ();
		this.bdaddr = bdaddr;
		this.channel = channel;
		
		bluetoothSocketConnect();
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.BluetoothSocketImpl#close()
	 */
	protected void close () throws IOException {
		bluetoothSocketClose ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.BluetoothSocketImpl#getInputStream()
	 */
	protected InputStream getInputStream () throws IOException {
		synchronized (inputStreamLock) {
			if (bluetoothSocketInputStream == null)
				bluetoothSocketInputStream = new BluetoothSocketInputStream (this);
		}

		return bluetoothSocketInputStream;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.BluetoothSocketImpl#getOutputStream()
	 */
	protected OutputStream getOutputStream () throws IOException {
		return new BluetoothSocketOutputStream (this);
	}

    /**
     * Cleans up if the user forgets to close it.
     */
    protected void finalize() throws IOException {
        close();
    }
	
	private native void bluetoothSocketConnect () throws IOException;
	
	private native void bluetoothSocketClose () throws IOException;
}
