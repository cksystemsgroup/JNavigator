/*
 * @(#) LocationMessage.java
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
package at.uni_salzburg.cs.ckgroup.location;

import at.uni_salzburg.cs.ckgroup.util.StringUtils;

/**
 * This class implements manipulation functionality for location messages.
 * 
 * @author Clemens Krainer
 */
public class LocationMessage {
	
	/**
	 * The location message prefix.
	 */
	public final static byte[] MESSAGE_PREFIX = "$LOC".getBytes ();
	
	/**
	 * The location message postfix. 
	 */
	public final static byte[] MESSAGE_POSTFIX = "\r\n".getBytes ();
	
	/**
	 * The location message as a byte array.
	 */
	private byte[] message;
	
	/**
	 * Construct a location message.
	 * 
	 * @param message the location message as a byte array
	 * @throws MalformedLocationMessageException thrown if the message is no
	 *         valid location message
	 */
	public LocationMessage (byte[] message) throws MalformedLocationMessageException {
		this.message = message;
		verify ();
	}
	
	/**
	 * Construct a location message.
	 * 
	 * @param message the location message as a byte array
	 * @param ofs the offset in the byte array
	 * @param len the length of the message
	 * @throws MalformedLocationMessageException thrown if the message is no
	 *         valid location message
	 */
	public LocationMessage (byte[] message, int ofs, int len) throws MalformedLocationMessageException {
		byte[] b = new byte[len];
		
		for (int k=0; k < len; k++)
			b[k] = message[ofs+k];
		
		this.message = b;
		verify ();
	}
	
	/**
	 * Verify the location message.
	 * 
	 * @throws MalformedLocationMessageException thrown if the message is no
	 *         valid location message
	 */
	public void verify () throws MalformedLocationMessageException {
		
		if (message.length < 11)
			throw new MalformedLocationMessageException ("Message too short.");
		
		for (int k=0; k < MESSAGE_PREFIX.length; k++)
			if (message[k] != MESSAGE_PREFIX[k])
				throw new MalformedLocationMessageException ("Wrong location message prefix.");
		
		int x = message.length - MESSAGE_POSTFIX.length;
		for (int k=0; k < MESSAGE_POSTFIX.length; k++)
			if (message[k+x] != MESSAGE_POSTFIX[k])
				throw new MalformedLocationMessageException ("Wrong location message postfix.");
		
		if (!verifyChecksum ())
			throw new MalformedLocationMessageException ("Checksum error.");
	}
	
	/**
	 * Verify the checksum of the location message.
	 * 
	 * @return true if the checksum is valid, false otherwise.
	 */
	protected boolean verifyChecksum () {
		int checkSum = 0;
		int k = 0;
		while (++k < message.length && message[k] != '*')
			checkSum ^= message[k] & 0xFF;
		
		if (k++ >= message.length)
			return false;
		
		int checkSumHigh = 48 + (checkSum & 0xF0)/16;
		if (checkSumHigh > 57)  checkSumHigh += 7;
		int checkSumLow  = 48 + (checkSum & 0x0F);
		if (checkSumLow  > 57)  checkSumLow += 7;
		
//		System.out.println ("Calculated checksum: " + (char)checkSumHigh + " " + (char)checkSumLow);
//		System.out.println ("Provided checksum  : " + (char)message[k] + " " + (char)message[k+1]);
		
		return message[k+1] == checkSumLow && message[k] == checkSumHigh;
	}
	
	/**
	 * Check if the current message starts with a given sequence of bytes.
	 * 
	 * @param m the byte array containing the sequence of bytes to be compared
	 *        with
	 * @return true if m contains a sequence of bytes the current message starts
	 *         with
	 */
	public boolean startsWith (byte[] m) {
		if (message.length < m.length)
			return false;
		
		for (int k=0; k < m.length; k++)
			if (m[k] != message[k])
				return false;
		
		return true;
	}
	
	/**
	 * Check if the current message starts with "$LOCPNQ,".
	 * 
	 * @return true if the message starts with "$LOCPNQ,", false otherwise.
	 */
	public boolean isAPnqMessage () {
		return startsWith("$LOCPNQ,".getBytes ());
	}
	
	/**
	 * Check if the current message starts with "$LOCPNQ," and has valid content.
	 * 
	 * @return true if the message starts with "$LOCPNQ," and has valid content, false otherwise.
	 */
	public boolean isAValidPnqMessage () {

		if (!isAPnqMessage ())
			return false;
		
//		String[] fields = (new String (message)).split (",");
		String[] fields = StringUtils.splitOnCharAndTrim(',',new String (message));
		
		// $LOCPNQ,UBase::Object,000000000000000000000000000,2008-06-20 22:24:49.334077000,1.2,1,1.1,2.2,3.3,4.4,5.5,6.6,7.7*43
		
		return fields[ 5].equals ("1");
	}
	
	/**
	 * Return the location message as a byte array
	 * 
	 * @return the message as a byte array
	 */
	public byte[] getBytes () {
		return message;
	}
}
