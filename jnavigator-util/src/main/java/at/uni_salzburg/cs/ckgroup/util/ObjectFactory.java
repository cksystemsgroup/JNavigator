/*
 * @(#) ObjectFactory.java
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

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

/**
 * This class implements a object factory for both, IConnection and InputStream
 * objects.
 * 
 * @author Clemens Krainer
 */
public class ObjectFactory
{
	/**
	 * Property constants. 
	 */
	public static final String PROP_CLASS_NAME = "className";
	
	/**
	 * The instance of the global ObjectFactory.
	 */
	private static ObjectFactory instance = null;
	
	/**
	 * Return the instance of the global ObjectFactory.
	 * @return  the instance of the global ObjectFactory.
	 * @uml.property  name="instance"
	 */
	public static ObjectFactory getInstance () {
		
		if (instance == null)
			instance = new ObjectFactory ();
		
		return instance;
	}
	
	/**
	 * Instantiate an object according to provided properties.
	 * 
	 * @param prefix the property prefix. Set to null to skip prefix handling. 
	 * @param classType the type of class to be instantiated, i.e. the newly
	 *        created object must implement an interface or inherit a class of
	 *        type classType
	 * @param properties the properties to be considered for creating the new
	 *        object
	 * @return the newly instantiated object
	 * @throws InstantiationException thrown on errors
	 */
	public Object instantiateObject (String prefix, Class classType, Properties properties) throws InstantiationException {
	
		Properties props = properties;
		
		if (prefix != null) {
//			String prefixExpr = prefix.replaceAll ("\\.", "\\\\.");
			props = PropertyUtils.extract (prefix, properties);
			props = PropertyUtils.replaceFirst (prefix, "", props);
		}
		
		String className = props.getProperty (PROP_CLASS_NAME);
		
		if (className == null || "".equals(className))
			throw new InstantiationException ("No property " + PROP_CLASS_NAME + " defined for property set " + prefix);
				
//		System.out.println ("className=" + className);
		
        Class objectClass;
        Object provider = null;
        
        try
        {
        	objectClass = Class.forName (className);

//			if (!objectClass.isInstance (classType))
//				throw new InstantiationException ("Class " + className + " is not an instance of " + classType.getName ());
        	
        	Class partypes[] = new Class[1];
            partypes[0] = Properties.class;

            Constructor ctor = objectClass.getConstructor (partypes);
            Object arglist[] = new Object[1];
            arglist[0] = props;
            provider = ctor.newInstance (arglist);
        }
        catch (Exception e)
        {
            throw new InstantiationException (e);
        }

        return provider;
	}
	
	/**
	 * This method instantiates a new IConnection object by means of the
	 * instantiateObject method.
	 * 
	 * @param prefix the property prefix
	 * @param properties the properties to be considered for creating the new
	 *        object
	 * @return the newly instantiated object
	 * @throws InstantiationException thrown on errors in instantiateObject()
	 * @see at.uni_salzburg.cs.ckgroup.util.ObjectFactory#instantiateObject()
	 */
	public IConnection instantiateIConnection (String prefix, Properties properties) throws InstantiationException {

		return (IConnection) instantiateObject (prefix, IConnection.class, properties);
	}
	
	/**
	 * This method instantiates a new object InputStream by means of the
	 * instantiateObject method.
	 * 
	 * @param prefix the property prefix
	 * @param properties the properties to be considered for creating the new
	 *        object
	 * @return the newly instantiated object
	 * @throws InstantiationException thrown on errors in instantiateObject()
	 * @see at.uni_salzburg.cs.ckgroup.util.ObjectFactory#instantiateObject()
	 */
	public InputStream instantiateInputStream (String prefix, Properties properties) throws InstantiationException {

		return (InputStream) instantiateObject (prefix, InputStream.class, properties);
	}
}
