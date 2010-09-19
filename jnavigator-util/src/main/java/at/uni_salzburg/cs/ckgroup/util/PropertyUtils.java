/*
 * @(#) PropertyUtils.java
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
package at.uni_salzburg.cs.ckgroup.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * This class implements the functionality needed to manipulate
 * <code>Properties</code> in JNavigator.
 * 
 * @author Clemens Krainer
 */
public class PropertyUtils
{
	/**
	 * Load the properties found in the class path and merge them to the system
	 * properties in a way that the system properties supersede the properties
	 * found in the class path.
	 * 
	 * @param propertyPath
	 *            the (relative) path of the properties file.
	 * @return the merged properties
	 * @throws IOException
	 *             thrown in case of a missing or unaccessible properties file
	 */
	public static Properties loadProperties (String propertyPath) throws IOException
	{
		Properties p = new Properties ();
		InputStream propsFileStream = Thread.currentThread ().getContextClassLoader ().getResourceAsStream(propertyPath);
		if (propsFileStream == null)
			throw new FileNotFoundException (propertyPath);
		p.load (propsFileStream);
		
		Enumeration e = System.getProperties ().keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = System.getProperty (key);
			p.setProperty(key, value);
		}
		return p;
	}

    /**
     * This method extracts a subset of a set of Properties by means of a prefix String.
     * 
     * @param prefix the prefix as a String
     * @param properties the set of properties
     * @return the extract
     */
    public static Properties extract (String prefix, Properties properties) {

        Properties p = new Properties ();

        Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if (key.startsWith(prefix)) {
                        String value = properties.getProperty(key);
                        p.setProperty(key, value);
                }
        }

        return p;
    }
    
    /**
     * This method replaces the prefixes of property keys by a replacement string.
     *  
     * @param prefix the prefix as a String
     * @param replacement the replacement for the first part of the key the prefix matches
     * @param properties the set of properties the replacement is applied to
     * @return the modified set of properties
     */
    public static Properties replaceFirst (String prefix, String replacement, Properties properties) {
    	
        Properties p = new Properties ();

        Enumeration e = properties.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = properties.getProperty(key);
            if (key.startsWith(prefix))
            	key = replacement + key.substring(prefix.length());
            p.setProperty(key, value);
        }

        return p;
    }

}
