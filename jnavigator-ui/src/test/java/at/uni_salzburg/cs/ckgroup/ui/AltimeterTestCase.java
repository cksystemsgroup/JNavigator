/*
 * @(#) AltimeterTestCase.java
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
package at.uni_salzburg.cs.ckgroup.ui;

import java.awt.Robot;
import java.net.URL;

import javax.swing.SwingUtilities;

import junit.framework.TestCase;
import swingunit.extensions.ExtendedRobotEventFactory;
import swingunit.framework.EventPlayer;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;

/**
 * This test verifies the implementation of the <code>Altimeter</code> class.
 * 
 * @author Clemens Krainer
 */
public class AltimeterTestCase extends TestCase {

	AltimeterTestApp application;
	Scenario scenario;
	Robot robot;
	
	public static final String altimeterTestFileName = "at/uni_salzburg/cs/ckgroup/ui/AltimeterTest/AltimeterTestApp.xml";
	public static final String referenceImageFolder = "at/uni_salzburg/cs/ckgroup/ui/AltimeterTest/";
	public static final String capturedImageFolder = "target/";

	protected void setUp() throws Exception {
		super.setUp();

		URL url = Thread.currentThread ().getContextClassLoader ().getResource (altimeterTestFileName);
		assertNotNull ("Can not find file " + altimeterTestFileName, url);
		System.out.println ("AltimeterTestCase: url=" + url.getFile());

		Runnable r = new Runnable() {
			public void run() {
				application = new AltimeterTestApp();
				application.setVisible(true);
			}
		};
		SwingUtilities.invokeAndWait(r);
		
		robot = new Robot();
		TestUtility.waitForCalm();
		
		scenario = new Scenario(new ExtendedRobotEventFactory(), new FinderMethodSet());
		scenario.read(url.getFile());
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		Runnable r = new Runnable() {
			public void run() {
				application.dispose();
				application = null;
			}
		};
		
		SwingUtilities.invokeAndWait(r);
		
		scenario = null;
		robot = null;
	}

	public void testCaseEmptyDisplay () throws Exception {
		EventPlayer player = new EventPlayer(scenario);
		assertNotNull(application.getAltimeterFrame());
		application.getAltimeterFrame().altimeter.repaint();
		Thread.sleep(600L);
		player.run(robot, "EMPTY_DISPLAY");

		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-empty.png", capturedImageFolder+"altimeter-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-zero.png",  capturedImageFolder+"altimeter-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-100.png",   capturedImageFolder+"altimeter-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-485.png",   capturedImageFolder+"altimeter-empty.png", 4, 26, 180, 180));
	}
	
	public void testCaseAltitudeZero () throws Exception {
		EventPlayer player = new EventPlayer(scenario);
		application.getAltimeterFrame().altimeter.setAltitude (null, 0);
		Thread.sleep(600L);
		player.run(robot, "ALTITUDE_ZERO");

		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-empty.png", capturedImageFolder+"altimeter-zero.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-zero.png",  capturedImageFolder+"altimeter-zero.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-100.png",   capturedImageFolder+"altimeter-zero.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-485.png",   capturedImageFolder+"altimeter-zero.png", 4, 26, 180, 180));
	}
	
	public void testCaseAltitude100 () throws Exception {
		EventPlayer player = new EventPlayer(scenario);
		application.getAltimeterFrame().altimeter.setAltitude (null, 100);
		Thread.sleep(600L);
		player.run(robot, "ALTITUDE_100");

		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-empty.png", capturedImageFolder+"altimeter-100.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-zero.png",  capturedImageFolder+"altimeter-100.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-100.png",   capturedImageFolder+"altimeter-100.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-485.png",   capturedImageFolder+"altimeter-100.png", 4, 26, 180, 180));
	}
	
	public void testCaseAltitude485 () throws Exception {
		EventPlayer player = new EventPlayer(scenario);
		application.getAltimeterFrame().altimeter.setAltitude (null, 485);
		Thread.sleep(600L);
		player.run(robot, "ALTITUDE_485");

		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-empty.png", capturedImageFolder+"altimeter-485.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-zero.png",  capturedImageFolder+"altimeter-485.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-100.png",   capturedImageFolder+"altimeter-485.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"altimeter-485.png",   capturedImageFolder+"altimeter-485.png", 4, 26, 180, 180));
	}
	
}
