/*
 * @(#) SerialLineOptions.java 
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
package at.uni_salzburg.cs.ckgroup.io;

/**
 * Interface of methods to get/set serial line options. This interface is
 * implemented by: <B>SerialLineImpl</B>.
 * Subclasses of these should override the methods of this interface in
 * order to support their own options.
 * <P>
 * The methods and constants which specify options in this interface are
 * for implementation only. If you're not subclassing SerialLineImpl
 * <B>you won't use these directly.</B>
 * <P>
 * @author Clemens Krainer
 *
 */
public interface SerialLineOptions
{
	/**
	 * The following constants for baud rates and stop bits have been cloned
	 * from file /usr/include/bits/termios.h
	 */
	
	/**
	 * Constants for baud rates
	 */
	public final static int B0 = 0000000;
	public final static int B50 = 0000001;
	public final static int B75 = 0000002;
	public final static int B110 = 0000003;
	public final static int B134 = 0000004;
	public final static int B150 = 0000005;
	public final static int B200 = 0000006;
	public final static int B300 = 0000007;
	public final static int B600 = 0000010;
	public final static int B1200 = 0000011;
	public final static int B1800 = 0000012;
	public final static int B2400 = 0000013;
	public final static int B4800 = 0000014;
	public final static int B9600 = 0000015;
	public final static int B19200 = 0000016;
	public final static int B38400 = 0000017;
	public final static int B57600 = 0010001;
	public final static int B115200 = 0010002;
	public final static int B230400 = 0010003;
	public final static int B460800 = 0010004;
	public final static int B500000 = 0010005;
	public final static int B576000 = 0010006;
	public final static int B921600 = 0010007;
	public final static int B1000000 = 0010010;
	public final static int B1152000 = 0010011;
	public final static int B1500000 = 0010012;
	public final static int B2000000 = 0010013;
	public final static int B2500000 = 0010014;
	public final static int B3000000 = 0010015;
	public final static int B3500000 = 0010016;
	public final static int B4000000 = 0010017;
	
	/**
	 * This array is for finding valid baud rates and converting them to the constants
	 * from above. 
	 */
	public final static int[][] bauds = {
		{0,       B0      }, {50,      B50     }, {75,      B75     }, {110,     B110    },
		{134,     B134    }, {150,     B150    }, {200,     B200    }, {300,     B300    },
		{600,     B600    }, {1200,    B1200   }, {1800,    B1800   }, {2400,    B2400   },
		{4800,    B4800   }, {9600,    B9600   }, {19200,   B19200  }, {38400,   B38400  },
		{57600,   B57600  }, {115200,  B115200 }, {230400,  B230400 }, {460800,  B460800 },
		{500000,  B500000 }, {576000,  B576000 }, {921600,  B921600 }, {1000000, B1000000},
		{1152000, B1152000}, {1500000, B1500000}, {2000000, B2000000}, {2500000, B2500000},
		{3000000, B3000000}, {3500000, B3500000}, {4000000, B4000000}
	};
	
	/**
	 * Constants for data bits
	 */
	public final static int CS5 = 0000000;
	public final static int CS6 = 0000020;
	public final static int CS7 = 0000040;
	public final static int CS8 = 0000060;

	/**
	 * Constants for stop bits
	 */
	public final static int CSTOPB = 0000100;
	
	/**
	 * Constants for parity
	 */
	public final static int PARODD = 0001000;
	public final static int IGNPAR = 0000004;
	
}
