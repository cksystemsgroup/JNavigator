/*
 * @(#) VehicleBuilder.java
 *
 * This code is part of the JAviator project: javiator.cs.uni-salzburg.at
 * Copyright (c) 2011  Clemens Krainer
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Properties;
import java.util.Timer;

import javiator.simulation.GpsReceiverSimulatorAdapter;
import javiator.simulation.LocationMessageSimulatorAdapter;
import javiator.simulation.MockJAviator;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import at.uni_salzburg.cs.ckgroup.communication.Dispatcher;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectForwarder;
import at.uni_salzburg.cs.ckgroup.communication.TransceiverAdapter;
import at.uni_salzburg.cs.ckgroup.communication.data.CommandData;
import at.uni_salzburg.cs.ckgroup.communication.data.SimulationData;
import at.uni_salzburg.cs.ckgroup.control.JControl;
import at.uni_salzburg.cs.ckgroup.course.IPositionProvider;
import at.uni_salzburg.cs.ckgroup.course.ISetCourseSupplier;
import at.uni_salzburg.cs.ckgroup.pilot.config.Configuration;
import at.uni_salzburg.cs.ckgroup.util.IClock;
import at.uni_salzburg.cs.ckgroup.util.InstantiationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

public class VehicleBuilder implements IVehicleBuilder {
	
	Logger LOG = Logger.getLogger(VehicleBuilder.class);
	
	public static final String PROPERTY_FILE_FORMAT = "%s.properties";
	public static final String PROP_SIMULATE_GPS_RECEIVER = "simulate.gps";
	public static final String PROP_SIMULATE_UBISENSE_RECEIVER = "simulate.ubisense";
	
	public static final String PROP_JAVIATOR_TRANSCEIVER_ADAPTER_PREFIX = "javiator.adapter.";
	public static final String PROP_JCONTROL_CYCLE_TIME = "jcontrol.cycle.time";
	public static final String PROP_JCONTROL_PREFIX = "jcontrol.";
	public static final String PROP_POSITION_PROVIDER_PREFIX = "position.provider.";
	public static final String PROP_SET_COURSE_SUPPLIER_PREFIX = "set.course.supplier.";
	public static final String PROP_TCP_SERVER_PREFIX = "tcp.server.";
	public static final String PROP_CLOCK_PREFIX = "clock.";
	public static final String PROP_PILOT_JCONTROL_ADAPTER_PREFIX = "jcontrol.adapter.";

	/**
	 * The current vehicle configuration.
	 */
	private Configuration conf = null;
	
	/**
	 * The directory to be used for temporary files. 
	 */
	private File workDir;
	
	
	private MockJAviator mockJAviator = null;
	private GpsReceiverSimulatorAdapter grsa = null;
	private LocationMessageSimulatorAdapter lmsa = null;
//	private JControlMain jcontrol;
	
//	private Dispatcher dispatcher;
	
	private TransceiverAdapter javiator;
	
	private JControl jcontrol;
	
	private TransceiverAdapter jcontrolAdapter;
	
	private IPositionProvider positionProvider;

	private ISetCourseSupplier setCourseSupplier;
	
	private IDataTransferObjectForwarder tcpServer;
	
	private AutoPilot autoPilot;
	
	private Thread tcpServerThread;
	
	private long cycleTime;
	
	private Timer timer;
	
//	private IClock clock;
	
	/**
	 * Load a vehicle configuration from an <code>InputStream</code> and build it.
	 * 
	 * @param inStream the configuration's <code>InputStream</code>
	 * @throws IOException thrown in case of errors.
	 * @throws  
	 */
	public void setConfig (Configuration configuration) throws IOException {
		destroy();
		this.conf = configuration;
		
		if (configuration.isConfigOk()) {
			try {
				buildVehicle();
			} catch (Exception e) {
				e.printStackTrace();
//				throw new IOException(e);
			}
		}
	}

	/**
	 * Build the vehicle according to the loaded configuration.
	 * @throws InstantiationException 
	 * @throws InterruptedException 
	 */
	private void buildVehicle() throws IOException, InstantiationException, InterruptedException {
        File plantConfig = renderConfigFile(Configuration.PLANT_CONFIG_TEMPLATE_FORMAT, PROPERTY_FILE_FORMAT, conf.getPlantType(), workDir);
        File ctrlConfig = renderConfigFile(Configuration.CONTROLLER_CONFIG_TEMPLATE_FORMAT, PROPERTY_FILE_FORMAT, conf.getControllerType(), workDir);
        File locSysConfig = renderConfigFile(Configuration.LOCATION_SYS_CONFIG_TEMPLATE_FORMAT, PROPERTY_FILE_FORMAT, conf.getLocationSystemType(), workDir);
        File pilotConfig = renderConfigFile(Configuration.PILOT_CONFIG_TEMPLATE_FORMAT, PROPERTY_FILE_FORMAT, conf.getPilotType(), workDir);
        
		Properties props = null;
		Dispatcher dispatcher = null;
		
		if (conf.isPlantSimulated()) {
			props = new Properties();
			LOG.info("Starting MockJAviator using " + plantConfig.getAbsolutePath());
			
			props.load(new FileInputStream(plantConfig));
			mockJAviator = new MockJAviator(props);
			
			if (props.getProperty(PROP_SIMULATE_GPS_RECEIVER, "true").equals("true")) {
				LOG.info("Activating GPS receiver simulator using " + locSysConfig.getAbsolutePath());
				props = new Properties();
				props.load(new FileInputStream(locSysConfig));
				grsa = new GpsReceiverSimulatorAdapter(props);
				grsa.start();
				mockJAviator.addDataTransferObjectListener(grsa, SimulationData.class);
			}
	
			if (props.getProperty(PROP_SIMULATE_UBISENSE_RECEIVER, "false").equals("true")) {
				LOG.info("Activating Ubisense receiver simulator using " + locSysConfig.getAbsolutePath());
				props = new Properties();
				props.load(new FileInputStream(locSysConfig));
				lmsa = new LocationMessageSimulatorAdapter(props);
				lmsa.start();
				mockJAviator.addDataTransferObjectListener(lmsa, SimulationData.class);
			}
			
			mockJAviator.start();
			dispatcher = mockJAviator.getDispatcher();
		} else {
			LOG.info("MockJAviator is not simulated.");
		}
		
		if (conf.isControllerSimulated()) {
			LOG.info("Starting controller using " + ctrlConfig.getAbsolutePath());
			props = new Properties();
			props.load(new FileInputStream(ctrlConfig));
			props.setProperty(PROP_JCONTROL_PREFIX+JControl.PROP_SET_COURSE_FOLDER,workDir.getAbsolutePath());
			
			cycleTime = Long.parseLong (props.getProperty(PROP_JCONTROL_CYCLE_TIME, "20"));
			
			if (dispatcher == null)
				dispatcher = new Dispatcher ();
			
			IClock clock = (IClock) ObjectFactory.getInstance ().instantiateObject (PROP_CLOCK_PREFIX, IClock.class, props);
	
//			javiator = (TransceiverAdapter) ObjectFactory.getInstance ().instantiateObject (PROP_JAVIATOR_TRANSCEIVER_ADAPTER_PREFIX, TransceiverAdapter.class, props);
//			javiator.setDtoProvider (dispatcher);
//			javiator.start();
			
			jcontrol = (JControl) ObjectFactory.getInstance ().instantiateObject (PROP_JCONTROL_PREFIX, JControl.class, props);
			jcontrol.setDtoProvider (dispatcher);
			jcontrol.setClock (clock);
			timer = new Timer ();
			timer.schedule(jcontrol, 1000, cycleTime);

			tcpServer = (IDataTransferObjectForwarder) ObjectFactory.getInstance ().instantiateObject (PROP_TCP_SERVER_PREFIX, IDataTransferObjectForwarder.class, props);
			tcpServer.setDtoProvider (dispatcher);
	        tcpServerThread = new Thread(tcpServer);
	        tcpServerThread.start();
	        
//			setCourseSupplier = (ISetCourseSupplier) ObjectFactory.getInstance ().instantiateObject (PROP_SET_COURSE_SUPPLIER_PREFIX, ISetCourseSupplier.class, props);
//			jcontrol.setPositionProvider (positionProvider);
//			jcontrol.setSetCourseSupplier (setCourseSupplier);
		} else {
			LOG.info("Controller is not simulated.");
		}
		
		
		{
			props = new Properties();
			props.load(new FileInputStream(pilotConfig));
			LOG.info("Starting AutoPilot using " + pilotConfig.getAbsolutePath());
			
			if (dispatcher == null)
				dispatcher = new Dispatcher ();
			
			positionProvider = (IPositionProvider) ObjectFactory.getInstance ().instantiateObject (PROP_POSITION_PROVIDER_PREFIX, IPositionProvider.class, props);
			IClock clock = (IClock) ObjectFactory.getInstance ().instantiateObject (PROP_CLOCK_PREFIX, IClock.class, props);
			autoPilot = new AutoPilot(props);
			autoPilot.setPositionProvider(positionProvider);
			autoPilot.setSetCourseSupplier(setCourseSupplier);
			autoPilot.setClock(clock);
			// TODO fix this
			autoPilot.setDtoProvider(dispatcher);
			
//			jcontrolAdapter = (TransceiverAdapter) ObjectFactory.getInstance ().instantiateObject (PROP_PILOT_JCONTROL_ADAPTER_PREFIX, TransceiverAdapter.class, props);
//			jcontrolAdapter.setDtoProvider (dispatcher);
//			jcontrolAdapter.start();
			
			CommandData cmd = new CommandData(0,0,0,0);
			dispatcher.dispatch(null, cmd);
		}
	}
	
	/**
	 * Destroy all dependent objects and unload the configuration.
	 */
	public void destroy() {
		LOG.error("Terminating MockJAviator");
		
		if (mockJAviator != null)
			mockJAviator.terminate();
		
		if (grsa != null)
			grsa.terminate();
		
		if (lmsa != null)
			lmsa.terminate();
			
		if (timer != null)
			timer.cancel();
		
		if (tcpServer != null)
			tcpServer.terminate();
		
		if (javiator != null)
			javiator.terminate();
		
		if (jcontrolAdapter != null)
			jcontrolAdapter.terminate();
		
		if (positionProvider != null)
			positionProvider.close();
	}

	/**
	 * @param templateFormat the template format to render the template's class path file name.
	 * @param propFormat the template format to render the required properties file.
	 * @param type the configuration type.
	 * @param destinationFolder the base folder to put the configuration files in.
	 * @throws FileNotFoundException thrown in case of I/O errors.
	 */
	private File renderConfigFile (String templateFormat, String propFormat, String type, File destinationFolder) throws FileNotFoundException {
		
		String template = String.format(templateFormat, type);
		InputStream templateStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(template);
		Reader reader = new InputStreamReader(templateStream);
		
		File destination = new File (destinationFolder, String.format(propFormat, type));
		PrintWriter out = new PrintWriter(destination);
		
        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        VelocityEngine ve = new VelocityEngine();
        ve.init(props);
        VelocityContext context = new VelocityContext();
        context.put("configuration", conf);
        
		ve.evaluate(context, out, "VehicleBuilder.renderConfigFile()", reader);
		LOG.info("Creating configuration file " + destination.getName() + " by using template " + template);
		out.close();
		
		return destination;
	}

	/**
	 * @param workDir the directory to be used for temporary files.
	 */
	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	/**
	 * @param setCourseSupplier the set-course supplier.
	 */
	public void setSetCourseSupplier(ISetCourseSupplier setCourseSupplier) {
		this.setCourseSupplier = setCourseSupplier;
//		if (autoPilot != null)
//			autoPilot.setSetCourseSupplier(setCourseSupplier);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IVehicleBuilder#getPositionProvider()
	 */
	public IPositionProvider getPositionProvider() {
		return positionProvider;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.pilot.IVehicleBuilder#getAutoPilot()
	 */
	public IAutoPilot getAutoPilot() {
		return autoPilot;
	}
	
	
}
