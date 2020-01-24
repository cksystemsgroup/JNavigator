/*
 * @(#) DummyDTOProvider.java
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
package at.uni_salzburg.cs.ckgroup.control;

import java.io.IOException;
import java.util.Vector;

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener;
import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider;
import at.uni_salzburg.cs.ckgroup.communication.ISender;

public class DummyDTOProvider implements IDataTransferObjectProvider {
	
	public IDataTransferObjectListener listener;
	public ISender sender;
	public Vector<IDataTransferObject> dtos = new Vector<> ();
	public Class<?> dtoType;
	public boolean simulateIOException = false;
	
	public void addDataTransferObjectListener(
			IDataTransferObjectListener listener, Class<?> dtoType) {

		this.listener = listener;
		this.dtoType = dtoType;
	}

	public void dispatch(ISender sender, IDataTransferObject dto)
			throws IOException {

		if (simulateIOException) {
			simulateIOException = false;
			throw new IOException("Faked IOException.");
		}
		
		this.sender = sender;
		dtos.add(dto);
	}

	public void removeIDataTransferObjectListener(
			IDataTransferObjectListener listener) {
		
		if (this.listener == listener)
			this.listener = null;
	}

}
