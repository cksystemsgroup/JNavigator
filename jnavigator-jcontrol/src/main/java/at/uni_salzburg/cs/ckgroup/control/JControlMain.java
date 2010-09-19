/*
 * @(#) JControlMain.java
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
package at.uni_salzburg.cs.ckgroup.control;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Timer;

import at.uni_salzburg.cs.ckgroup.communication.Dispatcher;
import at.uni_salzburg.cs.ckgroup.communication.TcpServer;
import at.uni_salzburg.cs.ckgroup.communication.TransceiverAdapter;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;
import at.uni_salzburg.cs.ckgroup.util.PropertyUtils;

public class JControlMain {
	
	public static final String PROP_JAVIATOR_TRANSCEIVER_ADAPTER_PREFIX = "javiator.adapter.";
	public static final String PROP_JCONTROL_CYCLE_TIME = "jcontrol.cycle.time";
	public static final String PROP_JCONTROL_PREFIX = "jcontrol.";
	public static final String PROP_POSITION_PROVIDER_PREFIX = "position.provider.";
	public static final String PROP_SET_COURSE_SUPPLIER_PREFIX = "set.course.supplier.";
	public static final String PROP_TCP_SERVER_PREFIX = "tcp.server.";
	public static final String PROP_CLOCK_PREFIX = "clock.";
	
	private Dispatcher dispatcher;
	
	private TransceiverAdapter javiator;
	
	private JControl jcontrol;
	
	private IPositionProvider positionProvider;
	
	private ISetCourseSupplier setCourseSupplier;
	
	private TcpServer tcpServer;
	
	private long cycleTime;
	
//	private DataTransferObjectLogger logger;
	
	private IClock clock;
	
	private JControlMain (Properties props) throws InstantiationException, IOException {
		
		cycleTime = Long.parseLong (props.getProperty(PROP_JCONTROL_CYCLE_TIME, "20"));
		
		dispatcher = new Dispatcher ();
		
		clock = (IClock) ObjectFactory.getInstance ().instantiateObject (PROP_CLOCK_PREFIX, IClock.class, props);

		javiator = (TransceiverAdapter) ObjectFactory.getInstance ().instantiateObject (PROP_JAVIATOR_TRANSCEIVER_ADAPTER_PREFIX, TransceiverAdapter.class, props);
		javiator.setDtoProvider (dispatcher);
		javiator.start();
		
		positionProvider = (IPositionProvider) ObjectFactory.getInstance ().instantiateObject (PROP_POSITION_PROVIDER_PREFIX, IPositionProvider.class, props);
		
		setCourseSupplier = (ISetCourseSupplier) ObjectFactory.getInstance ().instantiateObject (PROP_SET_COURSE_SUPPLIER_PREFIX, ISetCourseSupplier.class, props);
		
		jcontrol = (JControl) ObjectFactory.getInstance ().instantiateObject (PROP_JCONTROL_PREFIX, JControl.class, props);
		jcontrol.setDtoProvider (dispatcher);
		jcontrol.setClock (clock);
		jcontrol.setPositionProvider (positionProvider);
		jcontrol.setSetCourseSupplier (setCourseSupplier);

//		logger = new IDataTransferObjectLogger ();
//		dispatcher.addIDataTransferObjectListener(logger, IDataTransferObject.class);
		
		tcpServer = (TcpServer) ObjectFactory.getInstance ().instantiateObject (PROP_TCP_SERVER_PREFIX, TcpServer.class, props);
		tcpServer.setDtoProvider (dispatcher);
		tcpServer.start();
	}
	
	/**
	 * @throws InterruptedException
	 */
	private void run () throws InterruptedException {
		
		try {
			Thread.currentThread().checkAccess();
			int p = Thread.currentThread().getPriority();
			System.out.println ("Running at priority " + p + " of max=" + Thread.MAX_PRIORITY + " and min=" + Thread.MIN_PRIORITY + " and norm=" + Thread.NORM_PRIORITY);
			Thread.currentThread().setPriority (Thread.NORM_PRIORITY+1);
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		Timer timer = new Timer ();
		timer.schedule(jcontrol, 1000, cycleTime);
//		tcpServer.join ();
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Properties props;
		
		try {
			if (args.length >= 1) {
				InputStream inStream = new FileInputStream (args[0]);
				props = new Properties ();
				props.load(inStream);
			} else {
				props = PropertyUtils.loadProperties ("at/uni_salzburg/cs/ckgroup/control/jcontrol.properties");
			}
			JControlMain me = new JControlMain (props);
			me.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
