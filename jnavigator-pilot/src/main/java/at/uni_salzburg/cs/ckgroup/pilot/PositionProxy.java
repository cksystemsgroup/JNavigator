/*
 * @(#) PositionProxy.java
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
package at.uni_salzburg.cs.ckgroup.pilot;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import at.uni_salzburg.cs.ckgroup.course.PolarCoordinate;

public class PositionProxy {
	
	Logger LOG = Logger.getLogger(PositionProxy.class);

	private String pilotPositionUrl;
	
	private Double latitude;
	private Double longitude;
	private Double altitude;
//	private Double courseOverGround;
//	private Double speedOverGround;
//	private Double altitudeOverGround;
	private boolean autoPilotFlight;
	
	public PositionProxy (String pilotUrl) {
		if (pilotUrl == null)
			throw new NullPointerException("Pilot URL may not be null!");
//		this.pilotPositionUrl = pilotUrl + "/json/position";
		this.pilotPositionUrl = pilotUrl + "/json/setCoursePosition";
	}
	
	public void fetchCurrentPosition() {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(pilotPositionUrl);
		HttpResponse response;

		String responseString = "";
		try {
			response = httpclient.execute(httpget);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				responseString = EntityUtils.toString(response.getEntity());
			} else {
				LOG.error("Error at accessing " + pilotPositionUrl + " code=" + statusCode + " reason=" + response.getStatusLine().getReasonPhrase());
			}
		} catch (Exception e) {
			LOG.error("Can not access " + pilotPositionUrl ,e);
		}
			
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject)parser.parse(responseString);
			
			latitude = (Double)obj.get("latitude");
			longitude = (Double)obj.get("longitude");
			altitude = (Double)obj.get("altitude");
//			courseOverGround = (Double)obj.get("courseOverGround");
//			speedOverGround = (Double)obj.get("speedOverGround");
//			altitudeOverGround = (Double)obj.get("altitudeOverGround");
			Boolean apf = (Boolean)obj.get("autoPilotFlight");
			autoPilotFlight = apf != null ? apf.booleanValue() : false;
		} catch (ParseException e) {
			LOG.error("Error at fetching current position from " + pilotPositionUrl);
		}
	}

	public PolarCoordinate getCurrentPosition() {
		if (latitude == null || longitude == null || altitude == null) {
			return null;
		}
		return new PolarCoordinate(latitude, longitude, altitude);
	}

//	public Double getCourseOverGround() {
//		return courseOverGround;
//	}
//
//	public Double getSpeedOverGround() {
//		return speedOverGround;
//	}
//
//	public Double getAltitudeOverGround() {
//		return altitudeOverGround;
//	}

	public boolean isAutoPilotFlight() {
		return autoPilotFlight;
	}

}
