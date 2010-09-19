/*
 * @(#) IDataTransferObjectLogger.java
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
package at.uni_salzburg.cs.ckgroup.communication;

import org.apache.log4j.Logger;


/**
 * This class implements a listener for <code>IDataTransferObject</code> derivatives that
 * logs the received messages to a file and/or to the console.
 * 
 * @author Clemens Krainer
 */
public class DataTransferObjectLogger implements IDataTransferObjectListener {
	
	/**
	 * This variable contains the instance of the logger.
	 */
	private static final Logger LOG = Logger.getLogger (DataTransferObjectLogger.class.getName());
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener#recieve(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void receive (IDataTransferObject dto) {
		LOG.debug (dto.toString());
	}

}
