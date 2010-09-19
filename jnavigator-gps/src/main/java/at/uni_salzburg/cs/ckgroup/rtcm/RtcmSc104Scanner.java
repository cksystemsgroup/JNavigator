/*
 * @(#) RtcmSc104Scanner.java
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
 *
 * Parts of this code and parts of RtcmSc104Message.java are deduced from the
 * DGPSIP 1.35 BSD/Linux sources by Wolgang Rupprecht.
 */
package at.uni_salzburg.cs.ckgroup.rtcm;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * @author Clemens Krainer
 */
public class RtcmSc104Scanner extends Thread implements RtcmSc104MessageProvider
{
	/**
	 * This variable contains all RTCM SC-104 message listeners. 
	 */
	private Vector listeners = new Vector ();
	
	/**
	 * The InputStream the scanner tries to receive RTCM SC-104 messages 
	 */
	private InputStream input;
	
	/**
	 * This variable indicates that the scanner had synchronized to the input
	 * data and the parity check succeeded for every word of a message so far.
	 */
	private boolean sync = false;
	
	/**
	 * This is the current receipt word. 
	 */
	private int current_word;
	
	/**
	 * Indicates if this thread is active. The run() and terminate() methods set and reset this flag.  
	 */
	private boolean running = false;
	
	/**
	 * Bit 30 mask, used for inversion of data bits. 
	 */
	private final static int P_30_MASK = 0x40000000;
	
	/**
	 * This mask covers all data bits.
	 */
	private final static int W_DATA_MASK = 0x3fffffc0;

	/**
	 * A 6 bit inverter. It reverses the bit order, e.g. 110100 becomes 001011.
	 */
	private final static int[] bit_inverter = {
			0, 32, 16, 48,  8, 40, 24, 56,  4, 36, 20, 52, 12, 44, 28, 60,
			2, 34, 18, 50, 10, 42, 26, 58,  6, 38, 22, 54, 14, 46, 30, 62,
			1, 33, 17, 49,  9, 41, 25, 57,  5, 37, 21, 53, 13, 45, 29, 61,
			3, 35, 19, 51, 11, 43, 27, 59,  7, 39, 23, 55, 15, 47, 31, 63
		};

	/**
	 * Construct a RTCM SC-104 message scanner
	 * 
	 * @param input the InputStream delivering the messages
	 */
	public RtcmSc104Scanner(InputStream input) {
		this.input = input;
	}

	/**
	 * Add a listener for RTCM SC-104 messages.
	 * 
	 * @param listener the listener to be added
	 */
	public void addMessageListener (RtcmSc104MessageListener listener) {
		listeners.add (listener);
	}

	/**
	 * Remove a listener of the list.
	 * 
	 * @param listener the listener to be removed
	 */
	public void removeMessageListener (RtcmSc104MessageListener listener) {
		listeners.remove (listener);
	}

	/**
	 * Distribute a newly receipt message to all listeners.
	 * 
	 * @param message the message to be distributed.
	 */
	protected void fireNewMessage (RtcmSc104Message message) {
//		System.out.println ("fireNewMessage: " + message.toString());

		for (int i = 0; i < listeners.size (); i++) {
			Object l = listeners.get (i);
			((RtcmSc104MessageListener)l).receive (message);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run () {
		current_word = 0;
		sync = false;
		running = true;
		
		try {
			
			while (running) {
				int w1 = synchronise ();
				int w2 = readFrame ();
				
//				Object o[] = new Object[2];
//				o[0] = new Integer (w1);
//				o[1] = new Integer (w2);
//				System.out.printf ("Message 0x%08x 0x%08x received.\n", o);
				
				RtcmSc104Message m = new RtcmSc104Message (w1, w2);
				for (int k=0; k < m.messageLength; k++)
					m.words[k+2] = readFrame ();
				
				fireNewMessage (m);
			}
			
		} catch (IOException e) {
			System.out.println ("end.");
		}
		
	}
	
	/**
	 * Terminate this thread. The thread will complete the receipt of a RTCM
	 * SC-104 message and terminate afterwards.
	 */
	public void terminate () {
		running = false;
	}
	
	/**
	 * Print a byte to the screen. Non printable bytes are displayed as '.'.
	 * 
	 * @param b the byte to be printed.
	 */
	private void printByte (int b) {
		if (b >= 32 && b <= 127)
			System.out.write (b);
		else
			System.out.write ('.');		
	}
	
	/**
	 * Print a 32 bit word to the screen.
	 * 
	 * @param w the word to be printed.
	 */
//	private void printWord (int w) {
//		printByte (0x40 + bit_inverter[(w>>24) & 0x3f]);
//		printByte (0x40 + bit_inverter[(w>>18) & 0x3f]);
//		printByte (0x40 + bit_inverter[(w>>12) & 0x3f]);
//		printByte (0x40 + bit_inverter[(w>> 6) & 0x3f]);
//		printByte (0x40 + bit_inverter[(w    ) & 0x3f]);
//	}
		
	/**
	 * Syncronise to the RTCM SC-104 data stream.
	 * 
	 * @return the current received and syncronised word
	 * @throws IOException
	 */
	protected int synchronise () throws IOException {
		
		if (sync)
			return readFrame ();
		
		final int P_30_MASK = 0x40000000;
		final int W_DATA_MASK = 0x3fffffc0;

		int b;
//		boolean locked = false;
		final int PREAMBLE_MASK = 0x3fc00000;
		final int PREAMBLE = 0x19800000;
//		final int INVERTED_PREAMBLE = 0x26400000;
		
		current_word = 0;
		
		while ( !sync ) {
			b = input.read ();
			
			if (b < 0)
				throw new IOException ("End of data.");
			
			int x = bit_inverter[b & 0x3F];
			
			current_word <<= 6;
			current_word |= x;

//			printByte (b);

			int preamble = current_word & PREAMBLE_MASK;
				
//			if (preamble == INVERTED_PREAMBLE) {
//				current_word ^= W_DATA_MASK;
//					
//				if (RtcmSc104Message.isParityOk (current_word))
//					sync = true;
//			}
				
			if (preamble == PREAMBLE && RtcmSc104Message.isParityOk (current_word))
				sync = true;
			
		}
		
		if ((current_word & P_30_MASK) > 0)
			current_word ^= W_DATA_MASK;
		
//		System.out.print ("RtcmSc104Scanner.synchronise '");
//		printWord (current_word);
//		System.out.println ("' parity ok="+RtcmSc104Message.isParityOk (current_word) );
		
		return current_word;
	}

	/**
	 * Read a word from a syncronised RTCM SC-104 data stream. If the parity of
	 * the newly received word is not correct, this method resets the sync flag.
	 * 
	 * @return the received word
	 * @throws IOException thrown in case of IO errors
	 */
	protected int readFrame () throws IOException {

//		System.out.print ("RtcmSc104Scanner.readFrame:  '");
//		System.out.flush ();

		int count = 5;
		int b;
		current_word &= 0x03;
		
		while ( count-- > 0 ) {
			b = input.read ();

			if (b < 0)
				throw new IOException ("End of data.");
			
			int x = bit_inverter[b & 0x3F];
			
			current_word <<= 6;
			current_word |= x;

			printByte (b);
		}

//		System.out.flush ();
//		System.out.print ("'  '");
//		printWord (current_word);
//		System.out.print ("'  '");

		if ((current_word & P_30_MASK) > 0)
				current_word ^= W_DATA_MASK;
		
//		System.out.print ("RtcmSc104Scanner.readFrame   '");
//		printWord (current_word);
//		System.out.println ("' parity ok="+RtcmSc104Message.isParityOk (current_word) );

		if (!RtcmSc104Message.isParityOk (current_word))
			sync = false;
		
		return current_word;
	}

}
