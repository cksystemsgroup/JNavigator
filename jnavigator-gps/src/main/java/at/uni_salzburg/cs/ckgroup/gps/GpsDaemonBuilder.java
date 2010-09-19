/*
 * @(#) GpsDaemonBuilder.java
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
package at.uni_salzburg.cs.ckgroup.gps;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.io.IConnection;
import at.uni_salzburg.cs.ckgroup.nmea.Nmea0183MessageListener;
import at.uni_salzburg.cs.ckgroup.rtcm.RtcmSc104Scanner;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

/**
 * This class assembles a GPS daemon configured in the properties file.
 * 
 * @author Clemens Krainer
 */
public class GpsDaemonBuilder
{
	/**
	 * Constants for property keys.
	 */
	public static final String PROP_RTCM_MESSAGE_PROVIDER = "rtcm.message.provider";
	public static final String PROP_GPS_RECEIVER = "gps.receiver";
	
	/**
	 * The RTCM SC-104 message provider
	 */
	private InputStream rtcmMessageProvider;
	
	/**
	 * The connection to the GPS receiver. 
	 */
	private IConnection gpsReceiver;
	
	/**
	 * The GPS daemon providing the NMEA 0183 messages
	 */
	private GpsDaemon gpsDaemon;
	
	/**
	 * The scanner for the RTCM SC-104 messages from the correction data
	 * provider.
	 */
	private RtcmSc104Scanner rtcmSc104Scanner;
	
	/**
	 * Construct the builder.
	 * 
	 * @param propertyPath the (relative) path of the properties file.
	 * @throws IOException thrown in case of a missing or unaccessible
	 *         properties file
	 * @throws ConfigurationException throw in case of an incorrect
	 *         configuration
	 */
	public GpsDaemonBuilder (String propertyPath) throws ConfigurationException, IOException
	{
		Properties props = PropertyUtils.loadProperties (propertyPath);
		init (props);
	}
	
	/**
	 * Construct the builder from already loaded properties.
	 * 
	 * @param props the available properties
	 * @throws IOException thrown in case of a missing or unaccessible
	 *             properties file
	 * @throws ConfigurationException throw in case of an incorrect
	 *             configuration
	 */
	public GpsDaemonBuilder (Properties props) throws ConfigurationException, IOException
	{
		init (props);
	}
	
	/**
	 * Initialize the GPS receiver and daemon, as well as the RTCM message
	 * provider and scanner
	 * 
	 * @param props the properties to be used
	 * @throws ConfigurationException thrown in case of configuration errors
	 * @throws IOException thrown in case of IO errors
	 */
	protected void init (Properties props) throws ConfigurationException, IOException
	{
		gpsReceiver = openGpsReceiver (props);		
		gpsDaemon = new GpsDaemon (gpsReceiver);
		gpsDaemon.start ();

		rtcmMessageProvider = openRtcmMessageProvider (props);
		if (rtcmMessageProvider == null)
			return;
		
		if (rtcmMessageProvider instanceof Nmea0183MessageListener)
			gpsDaemon.addNmea0183MessageListener ((Nmea0183MessageListener)rtcmMessageProvider);
		
		rtcmSc104Scanner = new RtcmSc104Scanner (rtcmMessageProvider);
		rtcmSc104Scanner.addMessageListener (gpsDaemon);
		rtcmSc104Scanner.start ();
	}

	/**
	 * Open the RTCM message provider.
	 * 
	 * @param p the properties  to be used
	 * @return the RTCM message provider as an InputStream
	 * @throws ConfigurationException thrown in case of configuration errors
	 * @throws IOException thrown in case of IO errors
	 */
	private InputStream openRtcmMessageProvider (Properties props) throws ConfigurationException, IOException
	{
		String provider = props.getProperty (PROP_RTCM_MESSAGE_PROVIDER);
		if (provider == null || provider.equals (""))
			return null;
//			throw new ConfigurationException ("Missing property " + PROP_RTCM_MESSAGE_PROVIDER);
		
		String providerType = props.getProperty (provider + ".className");
		if (providerType == null || providerType.equals (""))
			throw new ConfigurationException ("Missing property " + provider + ".className");
		
		return ObjectFactory.getInstance ().instantiateInputStream (provider+".", props);
	}

	/**
	 * Open the GPS receiver.
	 * 
	 * @param p the properties to be used
	 * @return the GPS receiver as an IConnection interface
	 * @throws ConfigurationException thrown in case of configuration errors
	 * @throws IOException thrown in case of IO errors
	 */
	protected IConnection openGpsReceiver (Properties props) throws ConfigurationException, IOException
	{	
		String gpsReceiver = props.getProperty (PROP_GPS_RECEIVER);
		if (gpsReceiver == null || gpsReceiver.equals (""))
			throw new ConfigurationException ("Missing property " + PROP_GPS_RECEIVER);
		
		String gpsReceiverType = props.getProperty (gpsReceiver + ".className");
		if (gpsReceiverType == null || gpsReceiverType.equals (""))
			throw new ConfigurationException ("Missing property " + gpsReceiver + ".className");
		
		return ObjectFactory.getInstance ().instantiateIConnection (gpsReceiver+".", props);
	}

	/**
	 * Return the current instance of the GPS daemon.
	 * @return  the current instance of the GPS daemon.
	 * @uml.property  name="gpsDaemon"
	 */
	public GpsDaemon getGpsDaemon ()
	{
		return gpsDaemon;
	}

}
