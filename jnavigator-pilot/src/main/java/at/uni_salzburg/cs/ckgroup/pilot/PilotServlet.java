/*
 * @(#) PilotServlet.java
 *
 * This code is part of the JNavigator project.
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
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.pilot.config.Configuration;


@SuppressWarnings("serial")
public class PilotServlet extends HttpServlet implements IServletConfig {
	
	Logger LOG = Logger.getLogger(PilotServlet.class);
	
	private static final String PROP_PATH_NAME = "jnavigator-pilot.properties";

	private ServletConfig servletConfig;
	private Properties props = new Properties ();
	private Aviator aviator = new Aviator();
	private VehicleBuilder vehicleBuilder = new VehicleBuilder();
	private Configuration configuration = new Configuration();
	
	private ServiceEntry[] services = {
		new ServiceEntry("/snoop.*", new SnoopService(this)),
		new ServiceEntry("/admin/.*", new AdminService(this)),
		new ServiceEntry("/sensor/.*", new SensorService(this)),
		new ServiceEntry(".*", new DefaultService(this))
	};

	public void init (ServletConfig servletConfig) throws ServletException {
		this.servletConfig = servletConfig;
		super.init();
		myInit();
	}

	private void myInit () throws ServletException {

		InputStream propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(PROP_PATH_NAME);
		
		if (propStream == null)
			throw new ServletException ("Can not find file " + PROP_PATH_NAME + " in class path.");
		
		try {
			props.load(propStream);
					
			servletConfig.getServletContext().setAttribute("aviator", aviator);
			servletConfig.getServletContext().setAttribute("vehicleBuilder", vehicleBuilder);
			servletConfig.getServletContext().setAttribute("configuration", configuration);
			
			File contexTempDir = (File)servletConfig.getServletContext().getAttribute(AdminService.CONTEXT_TEMP_DIR);
			File confFile = new File (contexTempDir, props.getProperty(AdminService.PROP_CONFIG_FILE));
			if (confFile.exists()) {
				configuration.setWorkDir (contexTempDir);
				configuration.loadConfig(new FileInputStream(confFile));
				LOG.info("Loading existing configuration from " + confFile);
			}
			vehicleBuilder.setConfig(configuration);
		
			File courseFile = new File (contexTempDir, props.getProperty(AdminService.PROP_COURSE_FILE));
			if (courseFile.exists()) {
				aviator.loadVclScript(new FileInputStream(courseFile));
				LOG.info("Loading existing course from " + courseFile);
			}
		
		} catch (IOException e) {
			throw new ServletException (e);
		}

		
		
//		String catalinaBase = System.getProperty(BackGroundTimerTask.PROP_CATALINA_BASE);
//		if (catalinaBase == null)
//			throw new ServletException ("Property " + BackGroundTimerTask.PROP_CATALINA_BASE + " is not set!");
//		props.setProperty (BackGroundTimerTask.PROP_CATALINA_BASE, catalinaBase);
//		
//		try {
//			long cycleTime = Long.parseLong(props.getProperty(PROP_CYCLE_TIME, "60000"));
//			backGroundTimerTask = new BackGroundTimerTask(this);
//			backGroundTimer = new Timer();
//			backGroundTimer.schedule(backGroundTimerTask, 60000-(System.currentTimeMillis() % 60000), cycleTime);
//		} catch (Exception e) {
//			throw new ServletException (e);
//		}
	}

	protected void service (HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String servicePath = request.getRequestURI();

		if (request.getRequestURI().startsWith(request.getContextPath())) {
			servicePath = request.getRequestURI().substring(request.getContextPath().length());
		}

		for (int k = 0; k < services.length; k++) {
			if (servicePath.matches(services[k].pattern)) {
				services[k].service.service(servletConfig, request, response);
				return;
			}
		}
		
		return;
	}
	
    public void destroy () {
//    	backGroundTimer.cancel();
//    	backGroundTimerTask.finish();
    	aviator.destroy();
    }

	public Properties getProperties() {
		return props;
	}

//	public void loadConfig(InputStream inStream) throws IOException {
//		// TODO Auto-generated method stub
//		
//	}

	public IAviator getAviator() {
		return aviator;
	}

	public IVehicleBuilder getVehicleBuilder() {
		return vehicleBuilder;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	
//	public String getProperty(String key) {
//		return props.getProperty(key);
//	}
//
//	public String getProperty(String key, String deault) {
//		return props.getProperty(key, deault);
//	}
	
	
}
