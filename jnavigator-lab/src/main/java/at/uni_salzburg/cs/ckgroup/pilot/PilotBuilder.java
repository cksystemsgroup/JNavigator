/*
 * @(#) PilotBuilder.java
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
package at.uni_salzburg.cs.ckgroup.pilot;

import java.io.IOException;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

/**
 * This class assembles a JNavigator auto pilot configured in the properties
 * file. It also constructs the <code>GpsDaemon</code> and the
 * <code>GpsPositionProvider</code> and associates them to the pilot.
 * 
 * @author Clemens Krainer
 */
public class PilotBuilder
{
	/**
	 * The key prefix for the pilot properties. 
	 */
	public static final String PROP_PILOT_PREFIX = "pilot.";
	
	/**
	 * The key prefix of the set course supplier properties.
	 */
	public static final String PROP_SET_COURSE_SUPPLIER_PREFIX = "set.course.supplier.";
	
	/**
	 * The key prefix for the clock properties. 
	 */
	public static final String PROP_CLOCK_PREFIX = "clock.";
	
	/**
	 * The key prefix for the position provider 
	 */
	public static final String PROP_POSITION_PROVIDER_PREFIX = "position.provider.";
	
	/**
	 * The current pilot instance.
	 */
	private IPilot pilot;
	
	/**
	 * Construct the builder.
	 * 
	 * @param propertyPath the (relative) path of the properties file.
	 * @throws IOException thrown in case of a missing or unaccessible
	 *         properties file
	 * @throws ConfigurationException throw in case of an incorrect
	 *         configuration
	 */
	public PilotBuilder (String propertyPath) throws ConfigurationException, IOException
	{
		Properties props = PropertyUtils.loadProperties (propertyPath);
		init (props);
	}
	
	/**
	 * Initialize the JNavigator pilot and engage the pilot interceptors
	 * 
	 * @param props the properties to be used
	 * @throws ConfigurationException thrown in case of configuration errors
	 * @throws IOException thrown in case of IO errors
	 */
	protected void init (Properties props) throws ConfigurationException, IOException
	{
		pilot = (IPilot) ObjectFactory.getInstance ().instantiateObject (PROP_PILOT_PREFIX, IPilot.class, props);
		
		ISetCourseSupplier setCourseSupplier = (ISetCourseSupplier) ObjectFactory.getInstance ().instantiateObject (
				PROP_SET_COURSE_SUPPLIER_PREFIX, ISetCourseSupplier.class, props);
		
		IClock clock = (IClock) ObjectFactory.getInstance ().instantiateObject (
				PROP_CLOCK_PREFIX, IClock.class, props);
		
//		GpsDaemonBuilder gpsDaemonBuilder = new GpsDaemonBuilder (props);
//		
//		GpsDaemon gpsDaemon = gpsDaemonBuilder.getGpsDaemon ();
//		GpsPositionProvider gpsPositionProvider = new GpsPositionProvider();
//		gpsDaemon.addNmea0183MessageListener (gpsPositionProvider);

		IPositionProvider gpsPositionProvider = (IPositionProvider) ObjectFactory.getInstance ().instantiateObject (
				PROP_POSITION_PROVIDER_PREFIX, IPositionProvider.class, props);
		
		pilot.setPositionProvider (gpsPositionProvider);
		pilot.setCourseSupplier (setCourseSupplier);
		pilot.setClock(clock);
	}

	/**
	 * Return the current instance of the pilot.
	 * 
	 * @return the current instance of the pilot.
	 */
	public IPilot getPilot ()
	{
		return pilot;
	}
	
}
