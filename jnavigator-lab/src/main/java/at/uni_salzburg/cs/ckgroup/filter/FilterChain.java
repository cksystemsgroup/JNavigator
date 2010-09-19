/*
 * @(#) FilterChain.java
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
package at.uni_salzburg.cs.ckgroup.filter;

import java.util.Properties;
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class enables chaining of filters. 
 * 
 * @author Clemens Krainer
 *
 */
public class FilterChain implements IFilter
{
	private static final long serialVersionUID = 838575198040913651L;

	/**
	 * Property key constant for the list of filters.
	 */
	private static final String PROP_FILTER_LIST = "filter.list";
	
	/**
	 * Key prefix for filter properties. The constructor prepends this before
	 * every filter from the filter list.
	 */
	private static final String PROP_FILTER_PREFIX = "filter.";
	
	/**
	 * This Vector stores the filter chain.
	 */
	private Vector filterChain = new Vector ();
	
	/**
	 * Construct a filter chain.
	 */
	public FilterChain () {
		// intentionally empty
	}
	
	/**
	 * Construct a filter chain from <code>Properties</code>.
	 * 
	 * @param props the <code>Properties</code> containing the filter
	 *            definitions.
	 * @throws ConfigurationException thrown in case of erroneous properties
	 */
	public FilterChain (Properties props) throws ConfigurationException {
		
		String filterList = props.getProperty (PROP_FILTER_LIST);
		if (filterList == null || filterList.equals (""))
			throw new ConfigurationException ("Property "+PROP_FILTER_LIST+" is not defined for this filter chain."); 
	
		String[] list = filterList.split ("\\s*,\\s*");
		
		for (int k=0; k < list.length; k++) {
			String prefix = PROP_FILTER_PREFIX + list[k] + ".";
			IFilter filter = (IFilter) ObjectFactory.getInstance ().instantiateObject (prefix, IFilter.class, props);
			addFilter (filter);
		}
	}
	
	/**
	 * Append a filter to the chain.
	 * 
	 * @param filter the filter to be added.
	 */
	public void addFilter (IFilter filter) {
		filterChain.add (filter);
	}
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cd.ckgroup.filter.IFilter#apply(double)
	 */
	public double apply (double value) {

		for (int k=0; k < filterChain.size (); k++)
			value = ((IFilter)filterChain.elementAt (k)).apply (value);
		
		return value;
	}
	
}
