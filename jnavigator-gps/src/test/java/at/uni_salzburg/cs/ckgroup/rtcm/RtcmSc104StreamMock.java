/*
 * @(#) RtcmSc104StreamMock.java
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
package at.uni_salzburg.cs.ckgroup.rtcm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;

/**
 * This class implements a wrapper of the
 * <code>RtcmSc104ReferenceStationMock</code> for loading with the
 * <code>ObjectFactory</code>.
 * 
 * @author Clemens Krainer
 */
public class RtcmSc104StreamMock extends InputStream implements Nmea0183MessageListener
{
	/**
	 * The RTCM 104 reference station mock instance. 
	 */
	private RtcmSc104ReferenceStationMock station;
	
	/**
	 * The <code>InputStream</code> of the reference station.
	 */
	private InputStream stream;
	
	/**
	 * Construct a reference station mock instance.
	 * 
	 * @param props the <code>Properties</code> to be used.
	 * @throws FileNotFoundException thrown in case of a missing configuration
	 *             file.
	 */
	public RtcmSc104StreamMock (Properties props) throws FileNotFoundException
	{
		station = new RtcmSc104ReferenceStationMock (props);
		stream = station.getInputStream ();
	}

	/* (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	public int read () throws IOException
	{
		return stream.read ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener#receive(at.uni_salzburg.cs.ckgroup.nmea.Nmea0183Message)
	 */
	public void receive (Nmea0183Message message)
	{
//		System.out.print ("RtcmSc104StreamMock: received:" + message);
	}

}
