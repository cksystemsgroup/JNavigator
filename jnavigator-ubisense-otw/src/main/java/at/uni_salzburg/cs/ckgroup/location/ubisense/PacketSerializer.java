/*
 * @(#) PacketSerializer.java
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
package at.uni_salzburg.cs.ckgroup.location.ubisense;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Properties;
import java.util.SimpleTimeZone;

import at.uni_salzburg.cs.ckgroup.io.IConnection;

/**
 * @author Clemens Krainer
 */
public class PacketSerializer implements IConnection, OnTheWireLocationMessageListener {
	
	/**
	 * This is the property for the message buffer length.  
	 */
	public static final String PROP_BUFFER_LENGTH = "buffer.length";
	
	/**
	 * A UTC calendar. It is this calendar that performs the time estimations.
	 */
	private Calendar calendar;
	
    /**
     * This variable contains a <b>en_US</b> schema. The simulator uses this
     * locale for converting numbers into Strings.
     */
    private Locale locale;
	
	/**
	 * The ring buffer for the location messages. The packet serializer
	 * creates new messages and inserts them to this buffer. Whether or not some
	 * PacketSerializerInputStream instances read messages from this buffer, the
	 * simulator continues to insert messages into this buffer. If the buffer is
	 * full, the simulator overwrites the eldest messages with new ones.
	 */
	protected byte[][] messageBuffer;

	/**
	 * The write index of the messageBuffer. 
	 */
	protected int writeIndex = 0;
    
    public PacketSerializer (Properties props) {
    	calendar = new GregorianCalendar(new SimpleTimeZone(0,"UTC"));
    	locale = new Locale ("en","US");
		int bufferLength = Integer.parseInt (props.getProperty (PROP_BUFFER_LENGTH,"100"));
		messageBuffer = new byte [bufferLength][];
    }
    
	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#close()
	 */
	public void close() throws IOException {
		// intentionally empty
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return new PacketSerializerInputStream ();
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.io.IConnection#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see at.uni_salzburg.cs.ckgroup.location.ubisense.OnTheWireLocationMessageListener#receive(at.uni_salzburg.cs.ckgroup.location.ubisense.OnTheWireLocationMessage)
	 */
	public void receive(OnTheWireLocationMessage message) {

		calendar.setTime (new Date());
		
		NumberFormat nf = NumberFormat.getInstance(locale);
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		nf.setGroupingUsed(false);

		nf.setMinimumIntegerDigits(4);
		String dateString = nf.format(calendar.get (Calendar.YEAR)) + '-';

		nf.setMinimumIntegerDigits(2);
		dateString += nf.format(calendar.get (Calendar.MONTH)+1) + '-';
		dateString += nf.format(calendar.get (Calendar.DAY_OF_MONTH));

		nf.setMinimumIntegerDigits(2);
		String timeString = nf.format (calendar.get (Calendar.HOUR_OF_DAY)) + ':';
		timeString += nf.format (calendar.get (Calendar.MINUTE)) + ':';
		timeString += nf.format (calendar.get (Calendar.SECOND)) + '.';

		nf.setMinimumIntegerDigits(3);
		timeString += nf.format(calendar.get (Calendar.MILLISECOND));

		nf.setMaximumFractionDigits(6);
		nf.setMinimumFractionDigits(6);
		nf.setMinimumIntegerDigits(1);
		String errorString = nf.format (message.getError());

		StringBuilder sb = new StringBuilder();
		sb.append("$LOCPNQ,ULocationIntegration::Tag,000")
			.append (message.getTag_id_top())
			.append (message.getTag_id_bottom())
			.append (',')
			.append (dateString).append(' ').append (timeString).append(',')
			.append (errorString).append (',')
			.append (message.getFlags()).append (',')
			.append (message.getX()).append (',')
			.append (message.getY()).append (',')
			.append (message.getZ()).append (',')
			.append ("1.0,0,0,0");
		String checkSum = calculateChecksum (sb.toString().getBytes());
		sb.append (checkSum);
		sb.append("\r\n");
		String msg = sb.toString();
		
		System.out.println ("PacketSerializer: " + msg);
		
		messageBuffer [writeIndex] = msg.getBytes();
		
		if (writeIndex+1 >= messageBuffer.length)
			writeIndex = 0;
		else
			++writeIndex;
	}

	
	/**
	 * @param message
	 * @return
	 */
	public static String calculateChecksum (byte[] message) {
		
		int checkSum = 0;
		int k = 1;
		while (k < message.length)
			checkSum ^= message[k++] & 0xFF;
		
		int checkSumHigh = 48 + (checkSum & 0xF0)/16;
		if (checkSumHigh > 57)  checkSumHigh += 7;
		int checkSumLow  = 48 + (checkSum & 0x0F);
		if (checkSumLow  > 57)  checkSumLow += 7;

		return new String (new byte[]{'*', (byte)checkSumHigh, (byte)checkSumLow });
	}
	
	
	/**
	 * @author Clemens Krainer
	 */
	private class PacketSerializerInputStream extends InputStream {
		
		/**
		 * This variable indicates the index of the next message to get from the
		 * messageBuffer.
		 */
		private int readIndex = 0;
		
		/**
		 * This variable is an index in the currentLine to the next byte to be
		 * read.
		 */
		private int lineIndex = 0;
		
		/**
		 * The current line taken from the messageBuffer.
		 */
		private byte [] currentLine = null;
		
		/**
		 * This method gets new messages from the global messageBuffer and
		 * returns it byte wise to the caller.
		 * 
		 * @see java.io.InputStream#read()
		 */
		public int read () throws IOException {
			
			if (currentLine != null && lineIndex >= currentLine.length) {
				lineIndex = 0;
				currentLine = null;
			}

			if (currentLine == null) {

				int counter = 0;
				while (readIndex == writeIndex) {
					Thread.yield ();
					if (counter++ > 3)
						try { Thread.sleep (100); }
						catch (Exception e) {
							System.out.println ("PacketSerializerInputStream.read(): sleep interrupted.");
						}
				}
				
				currentLine = messageBuffer[readIndex];

				if (++readIndex >= messageBuffer.length)
					readIndex = 0;
			}
			
			return currentLine[lineIndex++];
		}
	}
}
