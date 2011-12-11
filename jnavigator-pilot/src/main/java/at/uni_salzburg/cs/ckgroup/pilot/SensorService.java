/*
 * @(#) SensorService.java
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

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.pilot.sensor.AbstractSensor;


public class SensorService extends DefaultService {
	
	Logger LOG = Logger.getLogger(SensorService.class);
	

	public SensorService (IServletConfig servletConfig) {
		super (servletConfig);
	}

	public void service(ServletConfig config, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String servicePath = request.getRequestURI();
		
		if (request.getRequestURI().startsWith(request.getContextPath()))
			servicePath = request.getRequestURI().substring(request.getContextPath().length());
		
		String[] p = servicePath.split("/");
		AbstractSensor s = servletConfig.getConfiguration().getSensorBuilder().getSensors().get(p[2]);
		
		if (s != null) {
			emitByteArray(response, s.getMimeType(), s.getByteArray());
		} else {
			emit404 (request, response);
		}
	}

}
