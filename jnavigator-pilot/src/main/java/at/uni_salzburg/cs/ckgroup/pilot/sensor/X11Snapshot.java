/*
 * @(#) X11Snapshot.java
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
package at.uni_salzburg.cs.ckgroup.pilot.sensor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.log4j.Logger;

import at.uni_salzburg.cs.ckgroup.pilot.config.ConfigurationException;

public class X11Snapshot extends AbstractSensor {
	
	Logger LOG = Logger.getLogger(X11Snapshot.class);
	
	private static final String ERR_INVALID_SCHEME = "Invalid scheme '%s', handling 'x11' displays only.";
	private static final String ERR_WORKDIR_NOT_EXISTENT = "Work directory '%s' does not exist!";
	
	private static final String ERR_IMAGE_PATH = "images/X11SnapshotError.png";
	
	private String display;

	private File workDir;

	public X11Snapshot(Properties props) throws URISyntaxException, ConfigurationException {
		super(props);
		String scheme = uri.getScheme();
		if (!"x11".equals(scheme))
			throw new ConfigurationException(String.format(ERR_INVALID_SCHEME, scheme));
		
		String host = uri.getHost();
		String path = uri.getPath();
		display = path.replaceFirst("/", host == null ? "" : host);
		
		String workDirString = props.getProperty("work.dir");
		if (workDirString == null)
			throw new ConfigurationException("Work directory not configured!");
		
		workDir = new File(workDirString);
		if (!workDir.exists())
			throw new ConfigurationException(String.format(ERR_WORKDIR_NOT_EXISTENT, workDir.getAbsolutePath()));
	}

	@Override
	public String getValue() {
		return null;
	}

	@Override
	public String getMimeType() {
		return "image/png";
	}
	
	@Override
	public byte[] getByteArray() {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		File xwdFile = null;
		File imgFile = null;
		try {
			xwdFile = File.createTempFile("pilot.x11snap", ".xwd", workDir);
			imgFile = File.createTempFile("pilot.x11snap", ".png", workDir);
			
			Process process = Runtime.getRuntime().exec(
					new String[] {"xwd","-display",display,"-root","-out",xwdFile.getAbsolutePath()}
					);
			
			int xwdRc = process.waitFor();
			if (xwdRc != 0)
				LOG.error("xwd: rc=" + xwdRc);
			
			process = Runtime.getRuntime().exec(
					new String[] {"convert",xwdFile.getAbsolutePath(),imgFile.getAbsolutePath()}
					);
			
			int convertRc = process.waitFor();
			if (convertRc != 0)
				LOG.error("convert: rc=" + convertRc);
			
			InputStream in;
			if (imgFile.exists() && xwdRc == 0 && convertRc == 0)
				in = new FileInputStream(imgFile);
			else
				in = Thread.currentThread().getContextClassLoader().getResourceAsStream(ERR_IMAGE_PATH);
			
			int c;
			while ( (c = in.read()) >= 0 ) {
				os.write(c);
			}
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Can not capture image of display " + display);
		} finally {
			if (xwdFile != null)
				xwdFile.delete();
			if (imgFile != null)
				imgFile.delete();
		}
		
		return os.toByteArray();
	}
}
