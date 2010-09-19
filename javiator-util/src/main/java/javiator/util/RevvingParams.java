/*****************************************************************************/
/*   This code is part of the JAviator project: javiator.cs.uni-salzburg.at  */
/*                                                                           */
/*   Copyright (c) 2006  Harald Roeck, Rainer Trummer                        */
/*                                                                           */
/*   This program is free software; you can redistribute it and/or modify    */
/*   it under the terms of the GNU General Public License as published by    */
/*   the Free Software Foundation; either version 2 of the License, or       */
/*   (at your option) any later version.                                     */
/*                                                                           */
/*   This program is distributed in the hope that it will be useful,         */
/*   but WITHOUT ANY WARRANTY; without even the implied warranty of          */
/*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the           */
/*   GNU General Public License for more details.                            */
/*                                                                           */
/*   You should have received a copy of the GNU General Public License       */
/*   along with this program; if not, write to the Free Software Foundation, */
/*   Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.      */
/*                                                                           */
/*****************************************************************************/

package javiator.util;

/**
 * Represents the actuator data
 * 
 * @author hroeck
 */
public class RevvingParams extends NumeratedSendable
{
	public static final int PACKET_SIZE = 8;

    public RevvingParams( )
    {
        reset( );
	}

    public void reset( )
    {
    	idleLimit = 0;
    	revUpStep = 0;
    	revDnStep = 0;
    	intDnStep = 0;
	}

    public String toString( )
    {
		String result;
		result  = " idle limit: "    + idleLimit;
		result += " rev-up step: "   + revUpStep;
		result += " rev-down step: " + revDnStep;
		result += " Ki down step: "  + intDnStep;

		return( result );
	}

	public void copySignals( RevvingParams data )
	{
		idleLimit = data.idleLimit;
		revUpStep = data.revUpStep;
		revDnStep = data.revDnStep;
		intDnStep = data.intDnStep;
	}

	public synchronized Packet toPacket( )
	{
		Packet packet = new Packet( PACKET_SIZE );
		encode( packet, 0 );

		return( packet );
	}

	public void encode( Packet packet, int offset )
	{
		packet.payload[ offset + 0 ] = (byte)( idleLimit >> 8 );
		packet.payload[ offset + 1 ] = (byte)( idleLimit );
		packet.payload[ offset + 2 ] = (byte)( revUpStep >> 8 );
		packet.payload[ offset + 3 ] = (byte)( revUpStep );
		packet.payload[ offset + 4 ] = (byte)( revDnStep >> 8 );
		packet.payload[ offset + 5 ] = (byte)( revDnStep );
		packet.payload[ offset + 6 ] = (byte)( intDnStep >> 8 );
		packet.payload[ offset + 7 ] = (byte)( intDnStep );
	}

	public synchronized Packet toPacket( byte type )
	{
		Packet packet = toPacket( );
		packet.type   = type;
		packet.calcChecksum( );

		return( packet );
	}

	public synchronized void fromPacket( Packet packet )
	{
		decode( packet, 0 );
	}

	public synchronized void decode( Packet packet, int offset )
	{
		idleLimit = (short)( (packet.payload[ offset + 0 ] << 8) | (packet.payload[ offset + 1 ] & 0xFF) );
		revUpStep = (short)( (packet.payload[ offset + 2 ] << 8) | (packet.payload[ offset + 3 ] & 0xFF) );
		revDnStep = (short)( (packet.payload[ offset + 4 ] << 8) | (packet.payload[ offset + 5 ] & 0xFF) );
		intDnStep = (short)( (packet.payload[ offset + 6 ] << 8) | (packet.payload[ offset + 7 ] & 0xFF) );
	}

	public synchronized Object clone( ) 
	{
		RevvingParams copy = new RevvingParams( );
		copyTo( copy );

		return( copy );
	}

	public RevvingParams deepClone( )
	{
		return (RevvingParams) clone( );
	}

	public void copyTo( Copyable to )
	{
		super.copyTo( to );
		RevvingParams copy = (RevvingParams) to;
		copy.idleLimit = idleLimit;
		copy.revUpStep = revUpStep;
		copy.revDnStep = revDnStep;
		copy.intDnStep = intDnStep;
	}

    public short idleLimit;
    public short revUpStep;
    public short revDnStep;
    public short intDnStep;
}
