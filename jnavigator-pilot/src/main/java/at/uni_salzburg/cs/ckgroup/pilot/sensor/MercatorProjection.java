/*
 * @(#) MercatorProjection.java
 *
 * This code is part of the JNavigator project.
 * Copyright (c) 2012  Clemens Krainer
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
package at.uni_salzburg.cs.ckgroup.pilot.sensor;

public class MercatorProjection {
	
	private int xTile;
	private int yTile;
	private int xPixel;
	private int yPixel;
	
	public MercatorProjection (double zoomLevel, double latitude, double longitude) {
		double lat_rad = Math.toRadians(latitude);
		long n = (long) Math.pow(2, zoomLevel);
		double xTileD = ((longitude + 180.0) / 360.0) * n;
		double yTileD = (1.0 - (Math.log(Math.tan(lat_rad) + 1.0/Math.cos(lat_rad)) / Math.PI)) / 2.0 * n;
		xTile = (int)xTileD;
		yTile = (int)yTileD;
		xPixel = (int)(256.0 * (xTileD - xTile));
		yPixel = (int)(256.0 * (yTileD - yTile));
	}
	
	public int getxTile() {
		return xTile;
	}
	
	public int getyTile() {
		return yTile;
	}
	
	public int getxPixel() {
		return xPixel;
	}

	public int getyPixel() {
		return yPixel;
	}
	
	public boolean equalsTile (MercatorProjection other) {
		return xTile == other.xTile && yTile == other.yTile;
	}
}
