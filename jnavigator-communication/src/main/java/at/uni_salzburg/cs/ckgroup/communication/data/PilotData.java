/*
 * @(#) PilotData.java
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
package at.uni_salzburg.cs.ckgroup.communication.data;

import at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject;
import at.uni_salzburg.cs.ckgroup.util.StringUtils;


/**
 * This data transfer object conveys the commands for the auto pilot.
 * 
 * @author Clemens Krainer
 */
public class PilotData implements IDataTransferObject {
	
	public static final String CMD_STRING_PREFIX = "CMD";
	public static final String CMD_STRING_START = "AUTOPILOT START";
	public static final String CMD_STRING_STOP = "AUTOPILOT STOP";
	public static final String CMD_STRING_SEND_SET_COURSE_FILE_NAMES = "AUTOPILOT SEND SET COURSE FILE NAMES";
	public static final String CMD_STRING_RESPONSE = "AUTOPILOT RESPONSE";
	public static final String CMD_STRING_FILE_NAME = "AUTOPILOT FILE NAME";
	
	public static final int CMD_UNKNOWN = 0;
	public static final int CMD_START = 1;
	public static final int CMD_STOP = 2;
	public static final int CMD_SEND_SET_COURSE_FILE_NAMES = 3;
	public static final int CMD_RESPONSE = 4;
	public static final int CMD_FILE_NAME = 5;
	
	/**
	 * The data as a byte array. 
	 */
	private byte[] data;
	
	/**
	 * The current command as an integer.
	 * @see CMD_UNKNOWN
	 * @see CMD_START
	 * @see CMD_STOP
	 */
	private int command;
	
	/**
	 * The current parameters as a String or null.
	 */
	private String parameters;
    
    /**
     * Construct an <code>OneByteData</code> object from a byte array.
     * 
     * @param data the byte array that contains the data.
     */
    public PilotData (byte[] data) {
    	this.data = data;
    	
//    	String[] commandString = (new String (data)).split (",",3);
    	String[] commandString = StringUtils.splitOnCharAndTrim(',',new String (data));
    	
    	command = CMD_UNKNOWN;
    	parameters = null;
    	
    	if (CMD_STRING_PREFIX.equals (commandString[0])) {
	    	if (CMD_STRING_START.equals (commandString[1]) && commandString.length == 3) {
	    		command = CMD_START;
	    		parameters = commandString[2];
	    	} else if (CMD_STRING_STOP.equals(commandString[1])) {
	    		command = CMD_STOP;
	    	} else if (CMD_STRING_SEND_SET_COURSE_FILE_NAMES.equals(commandString[1])) {
	    		command = CMD_SEND_SET_COURSE_FILE_NAMES;
	    	} else if (CMD_STRING_RESPONSE.equals(commandString[1])) {
	    		command = CMD_RESPONSE;
	    		parameters = commandString[2];
	    	} else if (CMD_STRING_FILE_NAME.equals(commandString[1])) {
	    		command = CMD_FILE_NAME;
	    		parameters = commandString[2];
	    	}
    	}
    }
    
    /**
     * @return the current command as an integer.
     */
    public int getCommand () {
    	return command;
    }
    
    /**
     * @return the current parameters as a String.
     */
    public String getParameters () {
    	return parameters;
    }
    
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.communication.IDataTransferObject#toByteArray()
	 */
	public byte[] toByteArray() {
		return data;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer buf = new StringBuffer ();
		buf.append("PilotData: '").append(new String(data)).append("'");
		return buf.toString();
	}
}
