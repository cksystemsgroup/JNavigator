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
package at.uni_salzburg.cs.ckgroup.location.ubisense;

import java.io.DataInputStream;
import java.io.IOException;

public class OnTheWireLocationMessage {

	/**
	 * Top 32 bits of tag id (currently always zero)
	 */
	private String tag_id_top;

	/**
	 * Bottom 32 bits of tag id
	 */
	private String tag_id_bottom;

	/**
	 * (flags & 1) == 'this is a valid location'
	 */
	private int flags;

	/**
	 * x position
	 */
	private float x;

	/**
	 * y position
	 */
	private float y;

	/**
	 * z position
	 */
	private float z;

	/**
	 * Geometric dilution of precision
	 */
	private float gdop;

	/**
	 * Standard error of location calculation
	 */
	private float error;

	/**
	 * Timeslot number of message
	 */
	private int slot;

	/**
	 * Microseconds between two timeslots (a constant for one instance of the
	 * location system)
	 */
	private int slotInterval;

	/**
	 * Delay between the timeslot in which the UWB was transmitted and the
	 * timeslot of the message (a constant for one instance of the location
	 * system)
	 */
	private int slotDelay;

	/**
	 * Id of the cell that generated the message
	 */
	private int cell;
	
	/**
	 * @param dis
	 * @throws IOException
	 */
	public OnTheWireLocationMessage (DataInputStream dis) throws IOException {
		tag_id_top = tagIdToString (dis.readInt ());
		tag_id_bottom = tagIdToString (dis.readInt ());
		flags = dis.readInt ();
		x = dis.readFloat ();
		y = dis.readFloat ();
		z = dis.readFloat ();
		gdop = dis.readFloat ();
		error = dis.readFloat ();
		slot = dis.readInt ();
		slotInterval = dis.readInt ();
		slotDelay = dis.readInt ();
		cell = dis.readInt ();
	}
	
	public String toString () {
		return "cell=" +  String.format ("%08X", new Object[] {new Integer (cell)}) +
			" tag_id=" + tag_id_top + "-" + tag_id_bottom +
			" flags=" + String.format ("%d", new Object[] {new Integer (flags)}) +
			" (" + x + ", " + y + ", " + z + ") " + gdop + " " + error +
			" slot=" + slot + " slot_interval=" + slotInterval + " slot_delay=" + slotDelay
		;
	}
	
	public static String tagIdToString (int tagId) {
		return String.format ("%03d%03d%03d%03d", new Object[] {
				new Integer ((tagId >> 24) & 0xFF),
				new Integer ((tagId >> 16) & 0xFF),
				new Integer ((tagId >> 8) & 0xFF),
				new Integer (tagId & 0xFF)}
		);
	}

	/**
	 * @return
	 */
	public String getTag_id_top() {
		return tag_id_top;
	}

	/**
	 * @return
	 */
	public String getTag_id_bottom() {
		return tag_id_bottom;
	}

	/**
	 * @return
	 */
	public int getFlags() {
		return flags;
	}

	/**
	 * @return
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return
	 */
	public float getZ() {
		return z;
	}

	/**
	 * @return
	 */
	public float getGdop() {
		return gdop;
	}

	/**
	 * @return
	 */
	public float getError() {
		return error;
	}

	/**
	 * @return
	 */
	public int getSlot() {
		return slot;
	}

	/**
	 * @return
	 */
	public int getSlotInterval() {
		return slotInterval;
	}

	/**
	 * @return
	 */
	public int getSlotDelay() {
		return slotDelay;
	}

	/**
	 * @return
	 */
	public int getCell() {
		return cell;
	}
	
	
}
