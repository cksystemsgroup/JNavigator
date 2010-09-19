/*
 * @(#) FakeConnection.java
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
package at.uni_salzburg.cs.ckgroup.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

/**
 * This class implements a <code>IConnection>/code> used in the unit tests.
 * 
 * @author Clemens Krainer
 */
public class FakeConnection implements IConnection {
	
	/**
	 * The object reference used for synchronisation.
	 */
	private Object lock = new Object[1];
	
	/**
	 * This connection's <code>OutputStream</code>
	 */
	private FakeConnectionOutputStream outputStream = null;
	
	/**
	 * This connection's <code>InputStream</code>
	 */
	private FakeConnectionInputStream inputStream = null;
	
	/**
	 * Throw an <code>IOException</code> instead of performing the normal behavior.
	 */
	private boolean fakeIOException; 
	
	/**
	 * Construct a <code>FakeConnection</code>.
	 * 
	 * @param props the <code>Properties</code> to be used for construction.
	 */
	public FakeConnection (Properties props) {
		System.out.println ("FakeConnection.<init>() properties: " + props);
		outputStream = new FakeConnectionOutputStream ();
		inputStream = new FakeConnectionInputStream ();
		String fakeIOExceptionString = props.getProperty("fake.io.exception", "false"); 
		fakeIOException = fakeIOExceptionString.equalsIgnoreCase("true");
		try { Thread.sleep(200); } catch (InterruptedException e) { }
	}
	
	/**
	 * Set the content of the associated <code>InputStream</code> to be read.
	 * 
	 * @param content the new content as a byte array.
	 */
	public void setReadBuffer (byte[] content) {
		inputStream.setReadBuffer(content);
	}
	
	/**
	 * Get the content of the associated <code>OutputStream</code> written so far.
	 * 
	 * @return
	 */
	public byte[] getWriteBuffer () {
		return outputStream.getWriteBuffer();
	}
	
	/**
	 * Set or reset the fake IOException behavior.
	 * 
	 * @param fakeIt true to cause IOExceptions to be thrown, otherwise false.
	 */
	public void setFakeIOException (boolean fakeIt) {
		fakeIOException = fakeIt;
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close() throws IOException {
		// Intentionally empty
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	/**
	 * The <code>OutputStream</code> implementation of the <code>FakeConnection</code>.
	 * 
	 * @author Clemens Krainer
	 */
	private class FakeConnectionOutputStream extends OutputStream {
		/**
		 * The <code>OutputStream</code> as an array of bytes.
		 */
		private ByteArrayOutputStream s = new ByteArrayOutputStream ();

		/**
		 * @return the content of the <code>OutputStream</code> so far.
		 */
		public byte[] getWriteBuffer () {
			byte[] b;
			synchronized (lock) {
				b = s.toByteArray ();
				s = new ByteArrayOutputStream ();
			}
			System.out.println ("FakeConnectionOutputStream.getWriteBuffer(). length=" + b.length);
			return b;
		}
		
		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int b) throws IOException {
			if (fakeIOException)
				throw new IOException ("write() faked an IOException");
			synchronized (lock) {
				s.write (b);
			}
			Thread.yield ();
		}
	}
	
	/**
	 * The <code>InputStream</code> implementation of the <code>FakeConnection</code>.
	 * 
	 * @author Clemens Krainer
	 */
	private class FakeConnectionInputStream extends InputStream {
		/**
		 * The <code>InputStream</code> as an array of bytes.
		 */
		private ByteArrayInputStream s;

		/**
		 * @param b the new content of the <code>InputStream></code> as an array of bytes. 
		 */
		public void setReadBuffer (byte[] b) {
			s = new ByteArrayInputStream (b);
			System.out.println ("FakeConnectionInputStream.setReadBuffer(). length=" + b.length);
		}
		
		/* (non-Javadoc)
		 * @see java.io.InputStream#read()
		 */
		public int read() throws IOException {
			if (s == null)
				throw new IOException ("FakeConnection is closed.");
			if (fakeIOException)
				throw new IOException ("read() faked an IOException");
			int ch = s.read();
			Thread.yield ();
			return ch;
		}
	}
}
