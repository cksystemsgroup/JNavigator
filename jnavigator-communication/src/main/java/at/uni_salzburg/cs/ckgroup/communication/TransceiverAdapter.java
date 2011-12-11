/*
 * @(#) TransceiverAdapter.java
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
import java.lang.reflect.Constructor;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import at.uni_salzburg.cs.ckgroup.ConfigurationException;
import at.uni_salzburg.cs.ckgroup.util.ObjectFactory;

/**
 * This class links a <code>ITranceiver</code> to a <code>Dispatcher</code>. It
 * is this class that converts <code>Packet</code> objects to
 * <code>IDataTransferObject</code> objects and vice versa.
 * 
 * @author Clemens Krainer
 */
public class TransceiverAdapter extends Thread implements ISender, IDataTransferObjectListener {
	
	/**
	 * The property key prefix for the transceiver configuration.
	 */
	public static final String PROP_TRANSCEIVER_PREFIX = "transceiver.";

	/**
	 * This property key refers to the comma separated list of mappings. Each
	 * entry of this list refers to a mapping of a type number to a
	 * <code>IDataTransferObject</code> derivative.
	 * 
	 * @see PROP_MAPPING_PREFIX
	 * @see PROP_MAPPING_TYPE_SUFFIX
	 * @see PROP_MAPPING_CLASS_NAME_SUFFIX
	 * @see PROP_MAPPING_REGISTER_SUFFIX
	 */
	public static final String PROP_MAPPING_LIST = "mapping.list";
	
	/**
	 * The prefix for all keys of the mapping configuration.
	 * 
	 * @see PROP_MAPPING_LIST
	 * @see PROP_MAPPING_TYPE_SUFFIX
	 * @see PROP_MAPPING_CLASS_NAME_SUFFIX
	 * @see PROP_MAPPING_REGISTER_SUFFIX
	 */
	public static final String PROP_MAPPING_PREFIX = "mapping.";
	
	/**
	 * The suffix for the type keys of the mapping configuration. This property
	 * refers to the type number in a <code>Packet</code> object.
	 * 
	 * @see PROP_MAPPING_LIST
	 * @see PROP_MAPPING_PREFIX
	 * @see PROP_MAPPING_CLASS_NAME_SUFFIX
	 * @see PROP_MAPPING_REGISTER_SUFFIX
	 */
	public static final String PROP_MAPPING_TYPE_SUFFIX = ".type";
	
	/**
	 * The suffix for the class name keys of the mapping configuration. This
	 * property refers to a class name of a <code>IDataTransferObject</code> derivative.
	 * 
	 * @see PROP_MAPPING_LIST
	 * @see PROP_MAPPING_PREFIX
	 * @see PROP_MAPPING_TYPE_SUFFIX
	 * @see PROP_MAPPING_REGISTER_SUFFIX
	 */
	public static final String PROP_MAPPING_CLASS_NAME_SUFFIX = ".className";

	/**
	 * The suffix for the registration keys of the mapping configuration. This
	 * property indicates whether the mapping should be registered with the
	 * associated dispatcher.
	 * 
	 * @see PROP_MAPPING_LIST
	 * @see PROP_MAPPING_PREFIX
	 * @see PROP_MAPPING_TYPE_SUFFIX
	 * @see PROP_MAPPING_CLASS_NAME_SUFFIX
	 */
	public static final String PROP_MAPPING_REGISTER_SUFFIX = ".register";

	/**
	 * This variable refers to the transceiver in charge.
	 */
	private ITransceiver transceiver;
	
	/**
	 * This variable maps a packet type to a <code>IDataTransferObject</code> class.
	 */
	private Map<Integer,Class<?>> packetTypeToDtoMap = new HashMap<Integer,Class<?>>();
	
	/**
	 * This variable maps a <code>IDataTransferObject</code> class to a packet type.
	 */
	private Map<Class<?>,Integer> dtoToPacketTypeMap = new HashMap<Class<?>,Integer>();
	
	/**
	 * This variable maps a packet type to the <code>IDataTransferObject</code> class
	 * constructor that takes a byte array as parameter.
	 */
	private Map<Integer,Constructor<?>> packetTypeToConstructorMap = new HashMap<Integer,Constructor<?>>();
	
	/**
	 * This variable maps a <code>IDataTransferObject</code> class to a boolean value that
	 * indicates whether the class should be registered with the associated
	 * dispatcher.
	 */
	private Map<Class<?>, Boolean> classToRegisterMap = new HashMap<Class<?>, Boolean>();
	
	/**
	 * The associated dispatcher.
	 */
	private IDataTransferObjectProvider dtoProvider;

	/**
	 * This variable indicates that the thread receiving the data from the
	 * underlying transceiver is running.
	 */
	private boolean running = false;
	
	/**
	 * This variable indicates that the reader thread should not abort on I/O
	 * errors and try to reconnect.
	 */
	private boolean reconnectingAllowed;
	
	/**
	 * Construct a <code>TransceiverAdapter</code> and create the underlying
	 * transceiver from the given <code>Properties</code>.
	 * 
	 * @param props
	 *            the properties to be used for construction.
	 * @throws ConfigurationException
	 *             thrown in case of configuration or initialization errors.
	 */
	public TransceiverAdapter (Properties props) throws ConfigurationException {
	
		transceiver = (ITransceiver) ObjectFactory.getInstance().instantiateObject (
				PROP_TRANSCEIVER_PREFIX, ITransceiver.class, props);
		init (props);
		reconnectingAllowed = true;
	}
	
	/**
	 * Construct a <code>TransceiverAdapter</code> and use the provided
	 * transceiver.
	 * 
	 * @param props
	 *            the properties to be used for construction.
	 * @param transceiver
	 *            the transceiver to be used for construction
	 * @throws ConfigurationException
	 *             thrown in case of configuration or initialization errors.
	 */
	public TransceiverAdapter (Properties props, ITransceiver transceiver) throws ConfigurationException {
		if (transceiver == null)
			throw new ConfigurationException ("The transceiver must not be null.");
		
		this.transceiver = transceiver;
		init (props);
		reconnectingAllowed = false;
	}
	
	/**
	 * Initialize the mapping configuration of the <code>TransceiverAdapter</code>.
	 * 
	 * @param props the properties to be used for construction.
	 * @throws ConfigurationException thrown in case of configuration or initialization errors.
	 */
	public void init (Properties props) throws ConfigurationException {
		String mappingListString = props.getProperty (PROP_MAPPING_LIST);
		if (mappingListString == null || "".equals(mappingListString))
			throw new ConfigurationException ("Property " + PROP_MAPPING_LIST + " not configured.");
		
		String[] mappingList = mappingListString.trim().split ("\\s*,\\s*");
//		String[] mappingList = StringUtils.splitOnCharAndTrim(',', mappingListString);
		for (int k=0; k < mappingList.length; k++) {
			String typePropString = PROP_MAPPING_PREFIX + mappingList[k] + PROP_MAPPING_TYPE_SUFFIX;
			String typeString = props.getProperty (typePropString);
			if (typeString == null || "".equals(typeString))
				throw new ConfigurationException ("Property " + typePropString + " not configured.");

			String regPropString = PROP_MAPPING_PREFIX + mappingList[k] + PROP_MAPPING_REGISTER_SUFFIX;
			String regString = props.getProperty (regPropString, "false");
			Boolean register = Boolean.valueOf (regString);
			
			
			String classPropString = PROP_MAPPING_PREFIX + mappingList[k] + PROP_MAPPING_CLASS_NAME_SUFFIX;
			String className = props.getProperty (classPropString);
			if (className == null || "".equals(className))
				throw new ConfigurationException ("Property " + classPropString + " not configured.");

			try {
				Class<?> classInstance = Class.forName (className);
				Class<?>[] interfaces = classInstance.getInterfaces();
				
				boolean isADTO = false;
				for (int j=0; j < interfaces.length; j++)
					if (interfaces[j] == IDataTransferObject.class)
						isADTO = true;
				
				if (!isADTO) {
					interfaces = classInstance.getSuperclass().getInterfaces();
					for (int j=0; j < interfaces.length; j++)
						if (interfaces[j] == IDataTransferObject.class)
							isADTO = true;
				}
				
				if (!isADTO)
					throw new ConfigurationException ("Class " + className + " at " + classPropString +
							" is no derivative of " + IDataTransferObject.class.getName());
				
	        	Class<?> partypes[] = new Class[1];
	            partypes[0] = byte[].class;

	            Constructor<?> ctor = classInstance.getConstructor (partypes);
				
				Integer typeIndex = Integer.valueOf (typeString); 
				if (typeIndex.intValue() <= 0)
					throw new ConfigurationException ("Property " + typePropString +
							" has not been assigned a number > 0.");
				
				packetTypeToDtoMap.put (typeIndex, classInstance);
				dtoToPacketTypeMap.put (classInstance, typeIndex);
				packetTypeToConstructorMap.put (typeIndex, ctor);
				classToRegisterMap.put(classInstance, register);
			} catch (Exception e) {
				throw new ConfigurationException (e);
			}
		}
	}
	
	/**
	 * Set the associated dispatcher and register the configured
	 * <class>IDataTransferObject</code> derivatives with the dispatcher.
	 * 
	 * @param dtoProvider
	 *            the associated send-able provider.
	 */
	public void setDtoProvider (IDataTransferObjectProvider dtoProvider) {
		this.dtoProvider = dtoProvider;
		
		Iterator<Class<?>> i = dtoToPacketTypeMap.keySet().iterator();
		while (i.hasNext()) {
			Class<?> classInstance = i.next();
			Boolean register = (Boolean) classToRegisterMap.get (classInstance);
			if (register.booleanValue())
				dtoProvider.addDataTransferObjectListener (this, classInstance);
		}
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObjectListener#recieve(at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject)
	 */
	public void receive (IDataTransferObject dto) throws IOException {
		// receive data from dispatcher and forward it to the transceiver.
		Integer typeIndex = (Integer) dtoToPacketTypeMap.get (dto.getClass());
		if (typeIndex == null)
			throw new IOException ("No mapping found for IDataTransferObject: " + dto.getClass().toString());
			
		byte type  = typeIndex.byteValue();
		Packet packet = new Packet (type, dto.toByteArray());
		transceiver.send (packet);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run () {
		// receive data from transceiver.
		running = true;
		Packet packet = null;
		while (running) {
			try {
				packet = transceiver.receive();
				Integer typeIndex = new Integer (packet.getType()&0xFF);
	            Constructor<?> ctor = packetTypeToConstructorMap.get (typeIndex);
	            if (ctor == null)
	            	throw new ConfigurationException ("No mapping found for packet type " + typeIndex + " (" + Thread.currentThread().getName() + ")");
	            Object arglist[] = new Object[1];
	            arglist[0] = packet.getPayload();
	            IDataTransferObject dto = (IDataTransferObject) ctor.newInstance (arglist);
	            if (dtoProvider != null)
	            	dtoProvider.dispatch(this, dto);
			} catch (CommunicationException e) {
				e.printStackTrace();
				Thread.yield();
			} catch (ConfigurationException e) {
				e.printStackTrace();
				Thread.yield();
			} catch (SocketException e) {
				if (!"Socket closed".equals(e.getMessage()))
					e.printStackTrace();
			} catch (Exception e) {
//				System.err.println ("\nCan not forward packet to dispatcher. " + (packet == null ? "(null)" : packet.toString()));
				e.printStackTrace();
				running = reconnectingAllowed;
				Thread.yield();
			}
		}
		if (dtoProvider != null)
			dtoProvider.removeIDataTransferObjectListener (this);
	}
	
	/**
	 * Terminate the transceiver reading thread.
	 */
	public void terminate () {
		running = false;
		reconnectingAllowed = false;
		transceiver.close();
	}
	
	/**
	 * Return the current transceiver. This method is intended for the unit
	 * tests only.
	 * 
	 * @return the current transceiver.
	 */
	ITransceiver getTransceiver () {
		return transceiver;
	}

}
