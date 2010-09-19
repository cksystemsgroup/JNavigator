/*
 * @(#) RtcmSc104Message.java
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
 * Parts of this code and parts of RtcmSc104Scanner.java are deduced from the
 * DGPSIP 1.35 BSD/Linux sources by Wolgang Rupprecht.
 */
package at.uni_salzburg.cs.ckgroup.rtcm;

/**
 * This class implements manipulation functionality for RTCM SC-104 messages.
 * The RtcmSc104Scanner uses this class to incrementally construct a RTCM SC-104
 * message.
 * 
 * @author Clemens Krainer
 */
public class RtcmSc104Message
{
	int[] words;
	int messageType;
	int stationId;
	int zCount;
	int sequenceNumber;
	int messageLength;
	int stationHealth;
	
	/**
	 * Give the parity of a byte used in the calculateParity() method.
	 */
	private static final int[] parity_array =
	{
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,	    
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,	    
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,	    
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,	    
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,	    
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,	    
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,	    
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,	    
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0,	    
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,
	    1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 1,	    
	    0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0
	};
	
	/**
	 * A 6 bit inverter. It reverses the bit order, e.g. 110100 becomes 001011.
	 */
	private static final int[] bit_inverter = {
		0, 32, 16, 48,  8, 40, 24, 56,  4, 36, 20, 52, 12, 44, 28, 60,
		2, 34, 18, 50, 10, 42, 26, 58,  6, 38, 22, 54, 14, 46, 30, 62,
		1, 33, 17, 49,  9, 41, 25, 57,  5, 37, 21, 53, 13, 45, 29, 61,
		3, 35, 19, 51, 11, 43, 27, 59,  7, 39, 23, 55, 15, 47, 31, 63
	};
	
	/**
	 * Bit 30 mask, used for inversion of data bits. 
	 */
	private final static int P_30_MASK = 0x40000000;
	
	/**
	 * This mask covers all data bits.
	 */
	private final static int W_DATA_MASK = 0x3fffffc0;
	
	/**
	 * Construct a RTCM SC-104 message header containing the first two words of
	 * a message.
	 * 
	 * @param word1 the first word
	 * @param word2 the second word
	 */
	public RtcmSc104Message (int word1, int word2)
	{
		messageType = (word1 & 0x3f0000) >> 16;
		stationId = (word1 & 0xffc0) >> 6;
		zCount = (word2 & 0x3ffe0000) >> 17;
		sequenceNumber = (word2 & 0x1c000) >> 14;
		messageLength = (word2 & 0x3e00) >> 9;
		stationHealth = (word2 & 0x1c0) >> 6;
		words = new int[messageLength+2];
		words[0] = word1;
		words[1] = word2;
	}
	
	/**
	 * Calculate the parity of a 30 bit word in a RTCM SC-104 message.
	 * 
	 * @param word the word
	 * @return the calculated parity
	 */
	public static int calculateParity (int word)
	{		
		final int PARITY_25 = 0xbb1f3480;
		final int PARITY_26 = 0x5d8f9a40;
		final int PARITY_27 = 0xaec7cd00;
		final int PARITY_28 = 0x5763e680;
		final int PARITY_29 = 0x6bb1f340;
		final int PARITY_30 = 0x8b7a89c0;
	    
		int t;
	    int p;

	    t = word & PARITY_25;
	    p =             parity_array[t & 0xff] ^ parity_array[(t >> 8) & 0xff] ^ parity_array[(t >> 16) & 0xff] ^ parity_array[(t >> 24) & 0xff];
	    t = word & PARITY_26;
	    p = (p << 1) | (parity_array[t & 0xff] ^ parity_array[(t >> 8) & 0xff] ^ parity_array[(t >> 16) & 0xff] ^ parity_array[(t >> 24) & 0xff]);
	    t = word & PARITY_27;
	    p = (p << 1) | (parity_array[t & 0xff] ^ parity_array[(t >> 8) & 0xff] ^ parity_array[(t >> 16) & 0xff] ^ parity_array[(t >> 24) & 0xff]);
	    t = word & PARITY_28;
	    p = (p << 1) | (parity_array[t & 0xff] ^ parity_array[(t >> 8) & 0xff] ^ parity_array[(t >> 16) & 0xff] ^ parity_array[(t >> 24) & 0xff]);
	    t = word & PARITY_29;
	    p = (p << 1) | (parity_array[t & 0xff] ^ parity_array[(t >> 8) & 0xff] ^ parity_array[(t >> 16) & 0xff] ^ parity_array[(t >> 24) & 0xff]);
	    t = word & PARITY_30;
	    p = (p << 1) | (parity_array[t & 0xff] ^ parity_array[(t >> 8) & 0xff] ^ parity_array[(t >> 16) & 0xff] ^ parity_array[(t >> 24) & 0xff]);
		
//	    Object[] o = new Object[3];
//	    o[0] = new Integer (word);
//	    o[1] = new Integer (p);
//	    o[2] = new Integer (word & 0x3f);
//	    System.out.printf ("parity of 0x%08x is 0x%02x, expected 0x%02x\n", o);
	    
	    return (p);
	}
	
	/**
	 * Check if the parity of a word of a given RTCM SC-104 message is correct.
	 * 
	 * @param word the word of a RTCM SC-104 message including the proposed 6
	 *        bit parity
	 * @return true if the proposed parity matches the calculated parity, false
	 *         otherwise.
	 */
	public static boolean isParityOk (int word) {
		return calculateParity (word) == (word & 0x3f);
	}
	
//	private String toString_01 () {
//		double ZCOUNT_SCALE    = 0.6;             /* sec */
//		double RANGE_SMALL     = 0.02;            /* metres */
//		double RANGE_LARGE     = 0.32;            /* metres */
//		double RANGERATE_SMALL = 0.002;           /* metres/sec */
//		double RANGERATE_LARGE = 0.032;           /* metres/sec */
//		StringBuffer buf = new StringBuffer ();
//
//		  int i, w;
//		  int m, n;
//		  int scale, udre, sat, range, rangerate, iod, msg_len;
//
//		  i = 0;
//		  w = 2;
//		  m = 0;
//
//		    msg_len = messageLength;
//		    n = msg_len % 5;
//		    if (n == 1 || n == 3) msg_len--;
//		    if (msg_len < 2)
//		      return "";
//
//		  while (w < msg_len+2) {
//		    if ((i & 0x3) == 0){
//		      m = words[w++] & W_DATA_MASK;
//		      scale = m >> 29 & 0x1;
//		      udre = (m >>27) & 0x3;
//		      sat = (m >>22) & 0x1f;
//		      range = (m >>6) & 0xffff;
//		      if (range > 32767) range -= 65536;
//		      m = words[w++] & W_DATA_MASK;
//		      rangerate = (m >>22) & 0xff;
//		      if (rangerate > 127) rangerate -= 256;
//		      iod = (m >>14) & 0xff;
//		      i++;
//		    }
//		    else if ((i & 0x3) == 1){
//		      scale = m >> 13 & 0x1;
//		      udre = (m >>11) & 0x3;
//		      sat = (m >>6) & 0x1f;
//		      m = words[w++] & W_DATA_MASK;
//		      range = (m >>14) & 0xffff;
//		      if (range > 32767) range -= 65536;
//		      rangerate = (m >>6) & 0xff;
//		      if (rangerate > 127) rangerate -= 256;
//		      m = words[w++] & W_DATA_MASK;
//		      iod = (m >>22) & 0xff;
//		      i++;
//		    }
//		    else {
//		      scale = m >> 21 & 0x1;
//		      udre = (m >>19) & 0x3;
//		      sat = (m >>14) & 0x1f;
//		      range = (m <<2) & 0xff00;
//		      m = words[w++] & W_DATA_MASK;
//		      range |= (m >>22) & 0xff;
//		      if (range > 32767) range -= 65536;
//		      rangerate = (m >>14) & 0xff;
//		      if (rangerate > 127) rangerate -= 256;
//		      iod = (m >>6) & 0xff;
//		      i+= 2;
//		    }
//	
//		    Object[] o = new Object[6];
//			o[0] = new Integer (sat);
//			o[1] = new Integer (udre);
//			o[2] = new Integer (iod);
//			o[3] = new Double (zCount*ZCOUNT_SCALE);
//			o[4] = new Double (range*(scale!=0?RANGE_LARGE:RANGE_SMALL));
//			o[5] = new Double (rangerate*(scale!=0?RANGERATE_LARGE:RANGERATE_SMALL));
//    
//		    buf.append (String.format ("\nType01:\t%d\t%d\t%d\t%.1f\t%.3f\t%.3f", o));
//		  }
//
//		  return buf.toString ();
//	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString () {
		StringBuffer b = new StringBuffer ();
		b.append ("messageType=").append (messageType);
		b.append (", stationId=").append (stationId);
		b.append (", zCount=").append (zCount);
		b.append (", sequenceNumber=").append (sequenceNumber);
		b.append (", messageLength=").append (messageLength);
		b.append (", stationHealth=").append (stationHealth);
		
//		switch (messageType) {
//			case 1:
//				b.append (toString_01());
//				break;
//			case 16: case 47:
//				b.append (", text: '");
//				int x;
//				for (int k=0; k < messageLength; k++) {
//					x = (words[k+2] & 0x3fc00000) >> 22;  b.append ((char)x);
//					x = (words[k+2] & 0x003fc000) >> 14;  if (x > 0)  b.append ((char)x);
//					x = (words[k+2] & 0x00003fc0) >>  6;  if (x > 0)  b.append ((char)x);
//				}
//				b.append ("'");
//				break;
//			default:
//				break;
//		}
		
		return b.toString ();
	}
	
	/**
	 * Return the RTCM SC-104 message as a byte array. The last word of the
	 * previously sent message has to be provided for the calculation of the
	 * parity and the potentially inversion of data bits.
	 * 
	 * @param previouslySentWord the last word of the previously sent message
	 * @return the message as a byte array
	 */
	public byte[] getBytes (int previouslySentWord) {
		
		byte[] b = new byte[(messageLength+2)*5];
		int current_word = previouslySentWord;
		
		for (int k=0; k < words.length; k++) {
			current_word <<= 30;
			current_word |= words[k] & 0x3fffffc0;

//			int x1 = calculateParity (current_word);
			
			current_word |= calculateParity (current_word);			
			
			if ((current_word & P_30_MASK) > 0)
				current_word ^= W_DATA_MASK;

//			int x2 = calculateParity (current_word);
			
			b[5*k]   = (byte)(0x40 + bit_inverter [(current_word>>24) & 0x3f]);
			b[5*k+1] = (byte)(0x40 + bit_inverter [(current_word>>18) & 0x3f]);
			b[5*k+2] = (byte)(0x40 + bit_inverter [(current_word>>12) & 0x3f]);
			b[5*k+3] = (byte)(0x40 + bit_inverter [(current_word>> 6) & 0x3f]);
			b[5*k+4] = (byte)(0x40 + bit_inverter [(current_word    ) & 0x3f]);
			
//			Object[] o = new Object[2];
//			o[0] = new Integer(k);
//			o[1] = new Integer(current_word);
//			System.out.printf ("RtcmSc104Message.getBytes: word[%d]=0x%08x", o);
//			System.out.println (", x1="+x1+", x2="+x2);
		}
		
		return b;
	}
	
	/**
	 * Get the last word of the RTCM SC-104 message.
	 * 
	 * @return the last word of the message
	 */
	public int getLastWord () {
		return words[messageLength+1];
	}
}
