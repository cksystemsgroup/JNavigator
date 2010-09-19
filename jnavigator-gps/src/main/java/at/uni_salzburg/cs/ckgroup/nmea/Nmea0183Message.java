/*
 * @(#) Nmea0183Message.java
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
package at.uni_salzburg.cs.ckgroup.nmea;

import at.uni_salzburg.cs.ckgroup.util.StringUtils;

/**
 * This class implements manipulation functionality for NMEA 0183 messages.
 * 
 * @author Clemens Krainer
 */
public class Nmea0183Message
{
	/**
	 * The NMEA 0183 message as a byte array.
	 */
	private byte[] message;

	/**
	 * The NMEA 0183 message prefix.
	 */
	public final static byte[] MESSAGE_PREFIX = "$GP".getBytes ();
	
	/**
	 * The NMEA 0183 message postfix. 
	 */
	public final static byte[] MESSAGE_POSTFIX = "\r\n".getBytes ();
	
	/**
	 * Construct a NMEA 0183 message.
	 * 
	 * @param message the NMEA 0183 message as a byte array
	 * @throws Nmea0183MalformedMessageException thrown if the message is no
	 *         valid NMEA 0183 message
	 */
	public Nmea0183Message (byte[] message) throws Nmea0183MalformedMessageException {
		this.message = message;
		verify ();
	}
	
	/**
	 * Construct a NMEA 0183 message.
	 * 
	 * @param message the NMEA 0183 message as a byte array
	 * @param ofs the offset in the byte array
	 * @param len the length of the message
	 * @throws Nmea0183MalformedMessageException thrown if the message is no
	 *         valid NMEA 0183 message
	 */
	public Nmea0183Message (byte[] message, int ofs, int len) throws Nmea0183MalformedMessageException {
		byte[] b = new byte[len];
		
		for (int k=0; k < len; k++)
			b[k] = message[ofs+k];
		
		this.message = b;
		verify ();
	}
	
	/**
	 * Verify the NMEA 0184 message.
	 * 
	 * @throws Nmea0183MalformedMessageException thrown if the message is no
	 *         valid NMEA 0183 message
	 */
	protected void verify () throws Nmea0183MalformedMessageException {
		
		if (message.length < 11)
			throw new Nmea0183MalformedMessageException ("Message too short.");
		
		for (int k=0; k < MESSAGE_PREFIX.length; k++)
			if (message[k] != MESSAGE_PREFIX[k])
				throw new Nmea0183MalformedMessageException ("Wrong NMEA 0183 message prefix.");
		
		int x = message.length - MESSAGE_POSTFIX.length;
		for (int k=0; k < MESSAGE_POSTFIX.length; k++)
			if (message[k+x] != MESSAGE_POSTFIX[k])
				throw new Nmea0183MalformedMessageException ("Wrong NMEA 0183 message postfix.");
		
		if (!verifyChecksum ())
			throw new Nmea0183MalformedMessageException ("Checksum error.");
	}
	
	/**
	 * Verify the checksum of the NMEA 0183 message.
	 * 
	 * @return true if the checksum is valid, false otherwise.
	 */
	protected boolean verifyChecksum () {
		int checkSum = 0;
		int k = 0;
		while (++k < message.length && message[k] != '*')
			checkSum ^= message[k];
		
		if (k++ >= message.length)
			return false;
		
		int checkSumHigh = 48 + checkSum/16;
		if (checkSumHigh > 57) checkSumHigh += 7;
		int checkSumLow = 48 + checkSum%16;
		if (checkSumLow > 57) checkSumLow += 7;
		
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
	 * Check if the current message starts with "$GPGGA,".
	 * 
	 * @return true if the message starts with "$GPGGA,", false otherwise.
	 */
	public boolean isAGgaMessage () {
		return startsWith("$GPGGA,".getBytes ());
	}
	
	/**
	 * Check if the current message starts with "$GPGGA," and has valid content.
	 * 
	 * @return true if the message starts with "$GPGGA," and has valid content, false otherwise.
	 */
	public boolean isAValidGgaMessage () {

		if (!isAGgaMessage ())
			return false;
		
//		String[] fields = (new String (message)).split (",");
		String[] fields = StringUtils.splitOnCharAndTrim(',',new String (message));
		
		// LEA-4H:
		// $GPGGA,003514.00,4759.42998,N,01256.21471,E,1,08,1.17,447.1,M,46.6,M,,*52
		// $GPGGA,193912.00,          , ,           , ,0,00,99.99,    , ,    , ,,*67
		// AsteRx1:
		// $GPGGA,         ,          , ,           , ,0,00,     ,    ,M,    ,M,,*66
		
		if (fields[ 2].equals ("")
		 || fields[ 3].equals ("")
		 || fields[ 4].equals ("")
		 || fields[ 5].equals ("")
		 || fields[ 9].equals ("")
		 || fields[11].equals ("")
		 ) return false;
		
		return true;
	}

	/**
	 * Check if the current message starts with "$GPRMC,".
	 * 
	 * @return true if the message starts with "$GPRMC,", false otherwise.
	 */
	public boolean isARmcMessage () {
		return startsWith("$GPRMC,".getBytes ());
	}
	
	/**
	 * Check if the current message starts with "$GPRMC," and has valid content.
	 * 
	 * @return true if the message starts with "$GPRMC," and has valid content, false otherwise.
	 */
	public boolean isAValidRmcMessage () {

		if (!isARmcMessage ())
			return false;
		
//		String[] fields = (new String (message)).split (",");
		String[] fields = StringUtils.splitOnCharAndTrim(',',new String (message));
		
		/* LEA-4H:
		 * $GPRMC,215136.00,A,4759.43142,N,01256.20407,E,0.586,345.60,150407,,,A*6A
		 * $GPRMC,174646.00,V,,,,,,,061007,,,N*7B
		 *
		 * AsteRx1:
		 * $GPRMC,133736.00,A,4759.42641081,N,01256.20282520,E,0.1,15.1,161207,,,D*61
		 * $GPRMC,,V,,,,,,,,,,N*53
		 */
		
		if (fields[ 2].equals ("V")
		 || fields[ 1].equals ("")
		 || fields[ 3].equals ("")
		 ) return false;
		
		return true;
	}
	
	/**
	 * Check if the current message starts with "$GPVTG,".
	 * 
	 * @return true if the message starts with "$GPVTG,", false otherwise.
	 */
	public boolean isAVtgMessage () {
		return startsWith("$GPVTG,".getBytes ());
	}
	
	/**
	 * Check if the current message starts with "$GPVTG," and has valid content.
	 * 
	 * @return true if the message starts with "$GPVTG," and has valid content, false otherwise.
	 */
	public boolean isAValidVtgMessage () {

		if (!isAVtgMessage ())
			return false;
		
//		String[] fields = (new String (message)).split (",");
		String[] fields = StringUtils.splitOnCharAndTrim(',',new String (message));
		
		/* LEA-4H:
		 * $GPVTG,316.66,T,,M,0.797,N,1.478,K,A*3A
		 * $GPVTG,,,,,,,,,N*30
		 *
		 * AsteRx1:
		 * $GPVTG,353.68,T,,M,0.366,N,0.678,K,A*3C
		 * $GPVTG,,,,,,,,,N*30
		 */
		
		if (!fields[ 2].equals ("T")
		 || !fields[ 4].equals ("M")
		 || !fields[ 6].equals ("N")
		 ) return false;
		
		return true;
	}
	
	/**
	 * Return the NMEA 0183 message as a byte array
	 * 
	 * @return the message as a byte array
	 */
	public byte[] getBytes () {
		return message;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		return new String (getBytes());
	}
}
