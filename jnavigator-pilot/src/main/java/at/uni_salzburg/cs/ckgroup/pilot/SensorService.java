/*
 * @(#) MonitorService.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SensorService extends DefaultService {
	
	
	
	public SensorService (IConfiguration configuraton) {
		super (configuraton);
	}

	public void service(ServletConfig config, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		
	
		
		
		
		
		
		String servicePath = request.getRequestURI();
		
		if (request.getRequestURI().startsWith(request.getContextPath()))
			servicePath = request.getRequestURI().substring(request.getContextPath().length());
		
		if (servicePath.matches ("/.*-INF.*")) {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.println ("Your are not at all wellcome!\nNow bugger off!");
			return;
		}
		
//		Map<String,Object> i = request.getParameterMap();
//		int contentLength = request.getContentLength();
//		String contentType = request.getContentType();
//		String characterEncoding = request.getCharacterEncoding();
		BufferedReader reader = request.getReader();
		String line;
		while ( (line = reader.readLine()) != null) {
			System.out.println(line);
		}
		
		
		String[] p = servicePath.split("/");
		
//		File catalinaBaseDir = (File)config.getServletContext().getAttribute(BackGroundTimerTask.PROP_CATALINA_BASE);
//		File workDir = new File (catalinaBaseDir, BackGroundTimerTask.WORK_DIR);
//		File logsDir = new File (workDir, BackGroundTimerTask.LOGS_DIR);

		switch (p.length) {
//		case 2:
//			response.setContentType("text/html");
//			String prefix = request.getContextPath() + "/" + p[1];
//			emitDirectoryListing (response.getWriter(), prefix, logsDir);
//			break;
			
//		case 3:
//			File logFile = new File (logsDir, p[2]);
//			
//			if (!p[2].startsWith(BackGroundTimerTask.LOGS_PREFIX) || !logFile.canRead()) {
//				emit404 (response, response.getWriter(), servicePath);
//				break;
//			}
//				
//			response.setContentType(getContentType(logFile.getName()));
//			emitFile (response.getOutputStream(), new FileInputStream(logFile));
//			break;
			
		default:
			emit404 (request, response);
			break;
		}
	}
	
	

	


}
