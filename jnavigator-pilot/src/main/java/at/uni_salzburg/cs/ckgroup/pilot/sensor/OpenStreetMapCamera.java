/*
 * @(#) OpenStreetMapCamera.java
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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import at.uni_salzburg.cs.ckgroup.course.IGeodeticSystem;
import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;
import at.uni_salzburg.cs.ckgroup.course.WGS84;

public class OpenStreetMapCamera {

//	private final static Logger LOG = Logger.getLogger(OpenStreetMapCamera.class);

	private static final String PROP_CAMERA_APERTURE_ANGLE = "osm.camera.aperture.angle";

	private final static String PROP_CAMERA_WIDTH = "osm.camera.width";

	private final static String PROP_CAMERA_HEIGTH = "osm.camera.heigth";

	private final static String PROP_ZOOM_LEVEL = "osm.zoom.level";

	private static final String PROP_TILE_WIDTH = "osm.tile.width";

	private static final String PROP_TILE_HEIGTH = "osm.tile.heigth";
	
	private OpenStreetMapTileCache tileCache;

	private int zoomLevel;

	private int mapWidth;

	private int mapHeight;

	BufferedImage map;

	private double cameraApertureAngle;

	private int cameraWidth;
	
	private int cameraHeigth;
	
	private int tileWidth;

	private int tileHeight;
	
	private MercatorProjection topLeftTile;

	private MercatorProjection bottomRightTile;
	
	private IGeodeticSystem geodeticSystem = new WGS84();
	
	private Graphics[][] tiles;

	public OpenStreetMapCamera(Properties props) {
		tileCache = new OpenStreetMapTileCache(props);
		cameraApertureAngle = Double.parseDouble(props.getProperty(PROP_CAMERA_APERTURE_ANGLE, "1.0"));
		cameraWidth = Integer.parseInt(props.getProperty(PROP_CAMERA_WIDTH, "320"));
		cameraHeigth = Integer.parseInt(props.getProperty(PROP_CAMERA_HEIGTH, "240"));
		zoomLevel = Integer.parseInt(props.getProperty(PROP_ZOOM_LEVEL, "18"));
		tileWidth = Integer.parseInt(props.getProperty(PROP_TILE_WIDTH, "256"));
		tileHeight = Integer.parseInt(props.getProperty(PROP_TILE_HEIGTH, "256"));
		mapWidth = cameraWidth/tileWidth + 2;
		mapHeight = cameraHeigth/tileHeight + 2;
		initMap();
	}

	private void initMap() {
		map = new BufferedImage(mapWidth * tileWidth, mapHeight * tileHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = map.createGraphics();
		tiles = new Graphics[mapWidth][mapHeight];
		
		for (int w=0; w < mapWidth; ++w) {
			for (int h=0; h < mapHeight; ++h) {
				tiles[w][h] = g2d.create(w * tileWidth, h * tileHeight, tileWidth, tileHeight);
			}
		}
	}

	public byte[] getImage(PolarCoordinate pos) throws IOException {
		
		if (pos == null) {
			return null;
		}
		
		if (pos.getAltitude() > 200) {
			pos.setAltitude(100);
		}
		
		double dx = pos.getAltitude() * Math.tan(cameraApertureAngle / 2.0);
		double dy = dx * cameraHeigth / cameraWidth;
		
		PolarCoordinate topLeftPosition = geodeticSystem.walk(pos, -dy, -dx, 0);
		PolarCoordinate bottomRightPosition = geodeticSystem.walk(pos, dy, dx, 0);
		
		MercatorProjection newTopLeftTile = new MercatorProjection(zoomLevel, topLeftPosition.getLatitude(), topLeftPosition.getLongitude());
		MercatorProjection newBottomRightTile = new MercatorProjection(zoomLevel, bottomRightPosition.getLatitude(), bottomRightPosition.getLongitude());
		
		boolean reloadTiles = topLeftTile == null || !topLeftTile.equalsTile(newTopLeftTile);
		topLeftTile = newTopLeftTile;
		bottomRightTile = newBottomRightTile;
		
		int newMapWidth = bottomRightTile.getxTile() - topLeftTile.getxTile() + 1;
		int newMapHeight =  bottomRightTile.getyTile() - topLeftTile.getyTile() + 1;
		
		if (newMapWidth > mapWidth || newMapHeight > mapHeight) {
			mapWidth = newMapWidth;
			mapHeight = newMapHeight;
			initMap();
			reloadTiles = true;
		}
		
		if (reloadTiles) {
			loadTiles();	
		}
		
		return extractImage();
	}

	private void loadTiles() throws IOException {
		
		int xt = topLeftTile.getxTile();
		int yt = topLeftTile.getyTile();
		
		for (int w=0; w < mapWidth; ++w) {
			for (int h=0; h < mapHeight; ++h) {
				File f = tileCache.getTile(zoomLevel, xt+w, yt+h);
				BufferedImage image = ImageIO.read(f);
				tiles[w][h].drawImage(image,0,0,null);
			}
		}
	}

	private byte[] extractImage() throws IOException {
		double dTilesX = bottomRightTile.getxTile() - topLeftTile.getxTile();
		double dTilesY = bottomRightTile.getyTile() - topLeftTile.getyTile();
		
		int height = (int)(dTilesY * tileHeight) + bottomRightTile.getyPixel() - topLeftTile.getyPixel();
		int width = (int)(dTilesX * tileWidth) + bottomRightTile.getxPixel() - topLeftTile.getxPixel();
		
		if (height < 1) {
			height = 1;
		}
		
		if (width < 1) {
			width = 1;
		}
		
		BufferedImage image = map.getSubimage(topLeftTile.getxPixel(), topLeftTile.getyPixel(), width, height);
		Image scaledImage = image.getScaledInstance(cameraWidth, cameraHeigth, 0);
		
		BufferedImage cameraImage = new BufferedImage(cameraWidth, cameraHeigth, BufferedImage.TYPE_INT_RGB);
		cameraImage.createGraphics().drawImage(scaledImage, 0, 0, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(cameraImage, "PNG", bos);
		
		ImageIO.write(map, "PNG", new File("../temp/map.png"));
		ImageIO.write(image, "PNG", new File("../temp/image.png"));
		ImageIO.write(cameraImage, "PNG", new File("../temp/cameraImage.png"));
		
		return bos.toByteArray();
	}
	
}
