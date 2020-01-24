/*
 * @(#) IDataTransferObjectProvider.java
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

import java.io.IOException;


/**
 * A provider for <code>IDataTransferObject</code> objects implements this interface.
 * 
 * @author Clemens Krainer
 */
public interface IDataTransferObjectProvider {
	
	/**
	 * Add a <code>IDataTransferObjectListener</code> to the list of receivers.
	 * 
	 * @param listener
	 *            the <code>IDataTransferObjectListener</code> to be added.
	 * @param dtoType
	 *            the send-able type the listener registers for. Type '0' means
	 *            a registration for all send-able types.
	 */
	public void addDataTransferObjectListener (IDataTransferObjectListener listener, Class<?> dtoType);

	/**
	 * Remove a <code>IDataTransferObjectListener</code> from the list of receivers.
	 * 
	 * @param listener
	 *            the <code>IDataTransferObjectListener</code> to be removed.
	 */
	public void removeIDataTransferObjectListener (IDataTransferObjectListener listener);
	
	/**
	 * Dispatch a <code>IDataTransferObject</code> message.
	 * 
	 * @param sender
	 *            the sender of the message.
	 * @param dto
	 *            the <code>IDataTransferObject</code> message to be dispatched.
	 * @throws IOException
	 *             thrown in case of I/O errors.
	 */
	public void dispatch (ISender sender, IDataTransferObject dto) throws IOException;
}
