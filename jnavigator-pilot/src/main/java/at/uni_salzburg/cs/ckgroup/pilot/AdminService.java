/*
 * @(#) AdminService.java
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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class AdminService extends DefaultService {
	
	Logger LOG = Logger.getLogger(AdminService.class);
	
	public final static String CONTEXT_TEMP_DIR = "javax.servlet.context.tempdir";
	
	public final static String ACTION_CONFIG_UPLOAD = "configUpload";
	public final static String ACTION_COURSE_UPLOAD = "courseUpload";
	public final static String ACTION_START_COURSE = "courseStart";
	public final static String ACTION_STOP_COURSE = "courseStop";
	
	public final static String PROP_CONFIG_FILE = "admin.config.file";
	public final static String PROP_COURSE_FILE = "admin.course.file";
	
	public AdminService (IConfiguration configuraton) {
		super (configuraton);
	}

	@Override
	public void service(ServletConfig config, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		File contexTempDir = (File)config.getServletContext().getAttribute(CONTEXT_TEMP_DIR);
		
		String servicePath = request.getRequestURI();
		if (request.getRequestURI().startsWith(request.getContextPath()))
			servicePath = request.getRequestURI().substring(request.getContextPath().length());
		
		String[] cmd = servicePath.trim().split("/+");
		boolean textMode = "text".equals(cmd[2]);
		String action = cmd[3];
		
		MimeParser parser = new MimeParser(request.getContentType());
		List<MimeEntry> list = parser.parse(request.getReader());
		MimeEntry uploadedFile = null;
		String name = null;
		String fileName = null;
		
		for (MimeEntry entry : list) {
			Map<String, String> headerMap = entry.getHeaders();
			String contentDisposition = headerMap.get(MimeEntry.CONTENT_DISPOSITION);
			
			if (contentDisposition.matches(".*\\sname=\"(\\S*)\".*"))
				name = contentDisposition.replaceFirst(".*\\sname=\"(\\S*)\".*", "$1");
			
			if (contentDisposition.matches(".*\\sfilename=\"(\\S*)\".*"))
				fileName = contentDisposition.replaceFirst(".*\\sfilename=\"(\\S*)\".*", "$1");
			
//			System.out.println("cdName=" + name + ", fileName=" + fileName);
			LOG.info("cdName=" + name + ", fileName=" + fileName);
			
			if (name != null && fileName != null && !"".equals(fileName)) {
				uploadedFile = entry;
				break;
			}
		}
		
		String nextPage;

		if (ACTION_CONFIG_UPLOAD.equals(action)) {
			File confFile = new File (contexTempDir, configuration.getProperties().getProperty(PROP_CONFIG_FILE));
			if (uploadedFile != null) {
				saveFile (uploadedFile, confFile);
				nextPage = request.getContextPath() + "/config.tpl";
				configuration.getVehicleBuilder().loadConfig(new FileInputStream(confFile));
				LOG.info("Configuration uploaded.");
			} else {
				emit422(request, response);
				return;
			}
		} else if (ACTION_COURSE_UPLOAD.equals(action)) {
			File courseFile = new File (contexTempDir, configuration.getProperties().getProperty(PROP_COURSE_FILE));
			if (uploadedFile != null) {
				saveFile (uploadedFile, courseFile);
				nextPage = request.getContextPath() + "/course.tpl";
				configuration.getAviator().loadVclScript(new FileInputStream(courseFile));
				LOG.info("Course uploaded.");
			} else {
				emit422(request, response);
				return;
			}
		} else if (ACTION_START_COURSE.equals(action)) {
			configuration.getAviator().start();
			nextPage = request.getContextPath() + "/course.tpl";
			LOG.info("Course started.");
		} else if (ACTION_STOP_COURSE.equals(action)) {
			configuration.getAviator().stop();
			nextPage = request.getContextPath() + "/course.tpl";
			LOG.info("Course stopped.");
		} else{
			LOG.error("Can not handle: " + servicePath);
			emit404(request, response);
			return;
		}
		
		if (textMode) {
			emit200 (request, response);
		} else {
			emit301 (request, response, nextPage);	
		}

	}

	private void saveFile(MimeEntry course, File file) throws IOException {
		if (file.exists() && file.isFile())
			file.delete();
			
		FileWriter w = new FileWriter(file);
		w.write(course.getBody());
		w.close();
		LOG.info("Written: " + file);
	}


	
	

}
