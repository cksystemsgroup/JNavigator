/*
 * @(#) SetCourse.java
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
package at.uni_salzburg.cs.ckgroup.course;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.util.StringUtils;

/**
 * This class loads the set course data from an <code>InputStream</code> and
 * allows access to it by providing several getter methods.
 * 
 * @author Clemens Krainer
 */
public class SetCourse {
	
	/**
	 * Load the course data from a given InputStream. This load procedure
	 * interprets the course data line by line. A line starting with '#' is
	 * considered as a comment and therefore skipped. Empty lines are ignored.
	 * Every other line must contain the values for latitude, longitude,
	 * altitude, duration and orientation. Semicolons (';') separate the fields
	 * from each other.
	 * 
	 * @param courseData the InputStream containing the course data.
	 * @throws ConfigurationException thrown if no course data is available or
	 *         the available data is invalid.
	 * @throws IOException thrown in case of IO errors.
	 */
	public static Section[] loadSetCourse (InputStream courseData) throws ConfigurationException, IOException
	{		
		LineNumberReader reader = new LineNumberReader (new InputStreamReader (courseData));
		String line;
		Vector states = new Vector ();
		Vector orientations = new Vector ();
		Vector timeSpans = new Vector ();
		
		while ((line = reader.readLine()) != null) {
			
//			if (line.matches ("\\s*#.*") || line.matches ("\\s*"))
//				continue;
			line = line.trim();
			if (line.startsWith("#"))
				continue;
			
			if ("".equals(line))
				continue;
			
//			String[] x = line.split (";");
			String[] x = StringUtils.splitOnCharAndTrim(';',line);
			if (x.length < 5)
				throw new ConfigurationException ("Invalid course data in line " + reader.getLineNumber ());
			
			PolarCoordinate position = new PolarCoordinate (Double.parseDouble (x[0]), Double.parseDouble (x[1]), Double.parseDouble (x[2]) );
			
			long duration = Long.parseLong (x[3]);
			if (duration <= 0)
				throw new ConfigurationException ("Negative or zero duration values are not allowed. Error in line " + reader.getLineNumber ());
			
			double orientation = Double.parseDouble (x[4]);
			if (orientation < 0 || orientation > 360)
				throw new ConfigurationException ("Invalid orientation in line " + reader.getLineNumber () + " only values between 0 and 360 degrees are allowed!");

			states.add (position);
			orientations.add (new Double(orientation));
			timeSpans.add (new Long(duration));
		} 
		
		if (states.size () == 0)
			throw new ConfigurationException ("The number of vehicle states is zero.");	

		Section[] sections = new Section [states.size ()];
		for (int k=0; k < states.size (); k++) {

			PolarCoordinate startPosition = (PolarCoordinate)states.get(k);
			double startOrientation = ((Double)orientations.get (k)).doubleValue ();
			
			boolean last = k+1 == states.size ();
			PolarCoordinate endPosition = last ? null : (PolarCoordinate)states.get(k+1);
			double endOrientation = last ? startOrientation : ((Double)orientations.get (k+1)).doubleValue ();
			
			long travelTime = ((Long)timeSpans.get (k)).longValue ();

			sections[k] = new Section (startPosition, startOrientation, endPosition, endOrientation, travelTime);
		}
		
		return sections;
	}

}
