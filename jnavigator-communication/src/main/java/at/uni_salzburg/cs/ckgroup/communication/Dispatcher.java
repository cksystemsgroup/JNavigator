/*
 * @(#) Dispatcher.java
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
import java.util.HashMap;
import java.util.Vector;


/**
 * This class notifies registered listeners of incoming <code>IDataTransferObject</code>
 * messages.
 * 
 * @author Clemens Krainer
 */
public class Dispatcher implements IDataTransferObjectProvider {
	
	/**
	 * This variable maps a class derivative of an <code>IDataTransferObject</code> to a
	 * vector of listeners.
	 */
	private HashMap<Class<?>,Vector<IDataTransferObjectListener>> dtoTypeMap = new HashMap<> ();
	
	/**
	 * This variable maps a listener to a vector of registered
	 * <code>IDataTransferObject</code> classes.
	 */
	private HashMap<IDataTransferObjectListener, Vector<Class<?>>> listenerMap = new HashMap<> ();
	
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider#addIDataTransferObjectListener(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener, java.lang.Class)
	 */
	public void addDataTransferObjectListener (IDataTransferObjectListener listener, Class<?> dtoType) {
		
		Vector<IDataTransferObjectListener> typeListeners;
		Vector<Class<?>> classes = listenerMap.get (listener);
		
		if (classes != null && classes.contains (IDataTransferObject.class))
			return;
			
		if (classes == null) {
			classes = new Vector<> ();
			listenerMap.put (listener, classes);
		}
		
		if (dtoType == IDataTransferObject.class) {
			for (int k=0; k < classes.size(); k++) {
				typeListeners = dtoTypeMap.get (classes.get (k));
				typeListeners.remove (listener);		
			}
			classes.clear ();
		}

		classes.add (dtoType);

		typeListeners = dtoTypeMap.get (dtoType);

		if (typeListeners == null) {
			typeListeners = new Vector<> ();
			dtoTypeMap.put (dtoType, typeListeners);
		}
		
		if (!typeListeners.contains (listener))
			typeListeners.add (listener);
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider#removeIDataTransferObjectListener(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener, java.lang.Class)
	 */
	public void removeIDataTransferObjectListener (IDataTransferObjectListener listener) {
		
		Vector<Class<?>> classes = listenerMap.get (listener);
		if (classes == null)
			return;
		
		for (int k=0; k < classes.size (); k++) {
			Vector<IDataTransferObjectListener> listeners = dtoTypeMap.get (classes.get (k));
			listeners.remove (listener);
		}
		
		listenerMap.remove (listener);
	}

	/**
	 * Dispatch a message. If a sender reference is provided this method makes
	 * sure that the sender won't get a message.
	 * 
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectProvider#dispatch(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void dispatch (ISender sender, IDataTransferObject dto) throws IOException {
		
		if (dto == null)
			throw new IOException ("Refusing to send a null data transfer object.");
		
		IDataTransferObjectListener s = sender instanceof IDataTransferObjectListener ? (IDataTransferObjectListener) sender : null;

		Vector<IDataTransferObjectListener> listeners = dtoTypeMap.get (dto.getClass());
		if (listeners != null) {
			for (int k=0; k < listeners.size(); k++) {
				IDataTransferObjectListener listener = (IDataTransferObjectListener) listeners.get (k);
				if (listener != s)
					listener.receive (dto);
			}
		}

		listeners = dtoTypeMap.get (IDataTransferObject.class);
		if (listeners != null) {
			for (int k=0; k < listeners.size(); k++) {
				IDataTransferObjectListener listener = (IDataTransferObjectListener) listeners.get (k);
				if (listener != s)
					listener.receive (dto);
			}
		}
	}

}
