/*
 * @(#) OpenStreetMapTileCache.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

public class OpenStreetMapTileCache {
	
	private final static Logger LOG = Logger.getLogger(OpenStreetMapTileCache.class);
	
	private final static String PROP_TILE_CACHE_DIR = "osm.tile.cache.dir";
	
	private final static String PROP_TILE_DOWNLOAD_URL = "osm.tile.server.url";
	
	private final static String FORMAT_TILE_CACHE_DIR = "%1$s/%2$d/%3$d";
	private final static String FORMAT_TILE_CACHE_FILE = "%1$s/%2$d/%3$d/%4$d.png";
//	private final static String FORMAT_TILE_DOWNLOAD_URL = "http://tile.opencyclemap.org/cycle/%1$d/%2$d/%3$d.png";
	
	private String tileCacheBaseDir;

	private String tileServerUrl;
	
	
	public OpenStreetMapTileCache (Properties props) {
		this.tileCacheBaseDir = props.getProperty(PROP_TILE_CACHE_DIR, "/tmp");
		this.tileServerUrl = props.getProperty(PROP_TILE_DOWNLOAD_URL, "http://tile.opencyclemap.org/cycle/%1$d/%2$d/%3$d.png");
	}
	
	public File getTile (int zoom, int x, int y) throws IOException {
		
		String tileCacheFileName = String.format(Locale.US, FORMAT_TILE_CACHE_FILE, tileCacheBaseDir, zoom, x, y);
		
		File tileCacheFile = new File(tileCacheFileName);
		
		if (tileCacheFile.exists()) {
			LOG.debug(String.format("Cached tile found for zoom=%d, x=%d, y=%d", zoom, x, y));
			return tileCacheFile;
		}
		
		String tileCacheDirName = String.format(Locale.US, FORMAT_TILE_CACHE_DIR, tileCacheBaseDir, zoom, x, y);
		File tileCacheDir = new File(tileCacheDirName);
		if (!tileCacheDir.exists()) {
			tileCacheDir.mkdirs();
		}
		
		String tileDownloadUrl = String.format(Locale.US, tileServerUrl, zoom, x, y);
		
		LOG.debug(String.format("Downloading tile for zoom=%d, x=%d, y=%d, url=%s", zoom, x, y, tileDownloadUrl));
		
		downloadFile(tileDownloadUrl,tileCacheFile);
		
		return tileCacheFile;
	}
	
	public static void downloadFile(String url, File file) throws IOException {

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response;

		response = httpclient.execute(httpget);
		FileOutputStream outStream = new FileOutputStream(file);

		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream inStream = entity.getContent();
			int l;
			byte[] tmp = new byte[8096];
			while ((l = inStream.read(tmp)) != -1) {
				outStream.write(tmp, 0, l);
			}
		}
	}

}
