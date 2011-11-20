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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AdminService extends DefaultService {
	
	public final static String CONTEXT_TEMP_DIR = "javax.servlet.context.tempdir";
	
	public final static String ACTION_CONFIG_UPLOAD = "/admin/configUpload";
	public final static String ACTION_COURSE_UPLOAD = "/admin/courseUpload";
	
	public AdminService (IConfiguration configuraton) {
		super (configuraton);
	}

	public void service(ServletConfig config, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		File contexTempDir = (File)config.getServletContext().getAttribute(CONTEXT_TEMP_DIR);
		
		String servicePath = request.getRequestURI();
		if (request.getRequestURI().startsWith(request.getContextPath()))
			servicePath = request.getRequestURI().substring(request.getContextPath().length());
		
//		System.out.println("Headers in this request:");
//        Enumeration<?> enumeration = request.getHeaderNames();
//        while (enumeration.hasMoreElements())
//        {
//                final String key = (String) enumeration.nextElement();
//                final String value = request.getHeader(key);
//                System.out.println("\t" + key + ": " + value);
//        }
		
		MimeParser parser = new MimeParser(request.getContentType());
		List<MimeEntry> list = parser.parse(request.getReader());
		MimeEntry course = null;
		String name = null;
		String fileName = null;
		
		for (MimeEntry e : list) {
//			System.out.println("headers: " + e.getHeaders());
//			System.out.println();
//			System.out.println("-- body:");
//			System.out.print(e.getBody());
//			System.out.println("-- end body.");
			
			Map<String, String> headerMap = e.getHeaders();
			String contentDisposition = headerMap.get(MimeEntry.CONTENT_DISPOSITION);
			
			if (contentDisposition.matches(".*\\sname=\"(\\S*)\".*"))
				name = contentDisposition.replaceFirst(".*\\sname=\"(\\S*)\".*", "$1");
			
			if (contentDisposition.matches(".*\\sfilename=\"(\\S*)\".*"))
				fileName = contentDisposition.replaceFirst(".*\\sfilename=\"(\\S*)\".*", "$1");
			
			System.out.println("cdName=" + name + ", fileName=" + fileName);
			
			if (name != null && fileName != null) {
				course = e;
				break;
			}
		}
		
//		Content-Type: text/html
//		Content-Type=text/plain

		if (ACTION_CONFIG_UPLOAD.equals(servicePath)) {
			System.out.println("OK " + servicePath);
			saveFile (course, new File (contexTempDir, "config.dat"));
		} else if (ACTION_COURSE_UPLOAD.equals(servicePath)) {
			System.out.println("OK " + servicePath);
			saveFile (course, new File (contexTempDir, "course.dat"));
		} else {
			System.out.println("Can not handle: " + servicePath);
			emit404(request, response);
		}
		
//		emit200 (request, response);
		emit301 (request, response, request.getContextPath() + "/index.tpl");
	}

	private void saveFile(MimeEntry course, File file) throws IOException {
		if (file.exists() && file.isFile())
			file.delete();
			
		FileWriter w = new FileWriter(file);
		w.write(course.getBody());
		w.close();
		System.out.println("Written: " + file);
	}


	
	

}
