/*
 * @(#) NavigationMain.java
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
package at.uni_salzburg.cs.ckgroup.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.gps.GpsDaemon;
import at.uni_salzburg.cs.ckgroup.gps.GpsDaemonBuilder;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;


/**
 * This class implements the <code>main</code> method that instantiates a
 * <code>NavigationFrame</code>, a <code>BirdsEyeFrame</code>, a
 * <code>GpsDaemon</code> and a <code>Nmea0183MessageForwarder</code>. The
 * <code>GpsDaemon</code> forwards the data of the GPS receiver to the views
 * in the <code>NavigationFrame</code> and the <code>BirdsEyeFrame</code> by
 * means of the <code>Nmea0183MessageForwarder</code>.
 * <p>
 * If the connection to the GPS receiver breaks, the <code>main</code> method
 * of this class tries to reconnect up to <code>MAX_RETRIES</code> times.
 * 
 * @author Clemens Krainer
 */
public class NavigationMain
{
	private static final long serialVersionUID = -5797102379894455879L;
	
	/**
	 * The maximum number of connect tries to establish a working connection to
	 * the GPS receiver.
	 */
	public static int MAX_RETRIES = 100;
	
	/**
	 * The name of the default property file.
	 */
	public static final String DEFAULT_PROPERTY_PATH = "jnavigator-ui.properties";

	/**
	 * This variable indicates that this application is running.
	 */
	public static boolean running = true;
	
	/**
	 * The <code>main</code> method.
	 * 
	 * @param args the arguments as an array of <code>String</code> objects.
	 */
	public static void main(String args[])
	{
		NavigationFrame frame = new NavigationFrame ("");
		frame.addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent we){
			running = false;
			System.exit(0);
			}
		});
		frame.setSize(188, 750);
		frame.setVisible(true);
		
		BirdsEyeFrame birdFrame = new BirdsEyeFrame ("");
		birdFrame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent we){
				running = false;
				System.exit(0);
				}
			});
		birdFrame.setSize(408, 430);
		birdFrame.setVisible(true);		
		
		String propertyPath = args.length >= 1 && !args[0].equals ("") ? args[0] : DEFAULT_PROPERTY_PATH;
		
		running = true;
		int retries = MAX_RETRIES;
		
		while (running && retries > 0) {
			try
			{
				Nmea0183MessageForwarder forwarder = new Nmea0183MessageForwarder ();
				forwarder.addViews (frame.getViews ());
				forwarder.addViews (birdFrame.getViews ());
				GpsDaemonBuilder builder = new GpsDaemonBuilder (propertyPath);
				GpsDaemon daemon = builder.getGpsDaemon ();
				daemon.addNmea0183MessageListener (forwarder);
				retries = MAX_RETRIES;
//				daemon.run ();
				Thread.currentThread().join();
			}
			catch (InstantiationException e)
			{
				if (e.getCause () != null
				 && e.getCause () instanceof InvocationTargetException
				 && e.getCause ().getCause () != null
				 && e.getCause ().getCause () instanceof ConnectException
				 && e.getCause ().getCause ().getMessage ().equals ("Connection refused"))
				{
					System.out.println ("Can not connect to the GPS receiver, retrying it " + (retries--) + " more times.");
					try { Thread.sleep (2000); }
					catch (Exception es) {
						System.out.println ("NavigationMain.main(): sleep interrupted.");
					}
					continue;
				}
				e.printStackTrace();
				break;
			}
			catch (ConfigurationException e)
			{
				e.printStackTrace();
				break;				
			}
			catch (IOException e)
			{
				e.printStackTrace();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
		
		if (retries == 0)
			System.out.println ("Could not (re)connect to the GPS receiver. Aborting.");
		
		System.exit(0);
	}
  
}
