/*
 * @(#) RtcmSc104ReferenceStationMock.java
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * This class emulates a RTCM SC-104 reference station.
 * 
 * @author Clemens Krainer
 */
public class RtcmSc104ReferenceStationMock
{
	private final static String PROP_DATA_FILE = "rtcm.data.file";
	private File inputDataFile;
	
	/**
	 * Construct a RTCM SC-104 reference station emulation.
	 * 
	 * @param fileName the file containing the data to be sent by this reference
	 *        station
	 * @throws FileNotFoundException thrown if the data file does not exist
	 */
	public RtcmSc104ReferenceStationMock (String fileName) throws FileNotFoundException
	{
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (fileName);
	
		if (url == null)
			throw new FileNotFoundException (fileName);
		
		inputDataFile = new File (url.getFile ());
	}

	/**
	 * Construct a RTCM SC-104 reference station emulation.
	 * 
	 * @param props the properties containing the file name of the data to be
	 *            sent by this reference station.
	 * @throws FileNotFoundException thrown if the data file does not exist or
	 *             cannot be read.
	 */
	public RtcmSc104ReferenceStationMock (Properties props) throws FileNotFoundException
	{
		String fileName = props.getProperty (PROP_DATA_FILE);

		if (fileName == null)
			throw new FileNotFoundException ("Property " + PROP_DATA_FILE + " not (properly) set");
		
		URL url = Thread.currentThread ().getContextClassLoader ().getResource (fileName);
		
		if (url == null)
			throw new FileNotFoundException (fileName);
		
		inputDataFile = new File (url.getFile ());
	}

	/**
	 * Return the InputStream of the reference station emulation.
	 * 
	 * @return the InputStream
	 * @throws FileNotFoundException thrown if IO errors occur
	 */
	public InputStream getInputStream () throws FileNotFoundException
	{
		return new FileInputStream (inputDataFile);
	}

}
