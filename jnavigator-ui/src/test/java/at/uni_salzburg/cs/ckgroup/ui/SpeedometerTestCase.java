/*
 * @(#) SpeedometerTestCase.java
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
import swingunit.framework.ExecuteException;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;

/**
 * This test verifies the implementation of the <code>Speedometer</code> class.
 * 
 * @author Clemens Krainer
 */
public class SpeedometerTestCase extends TestCase {

	SpeedometerTestApp application;
	Scenario scenario;
	Robot robot;
	
	public static final String speedometerTestFileName = "at/uni_salzburg/cs/ckgroup/ui/SpeedometerTest/SpeedometerTestApp.xml";
	public static final String referenceImageFolder = "at/uni_salzburg/cs/ckgroup/ui/SpeedometerTest/";
	public static final String capturedImageFolder = "target/";

	protected void setUp() throws Exception {
		super.setUp();

		URL url = Thread.currentThread ().getContextClassLoader ().getResource (speedometerTestFileName);
		assertNotNull ("Can not find file " + speedometerTestFileName, url);
		System.out.println ("SpeedometerTestCase: url=" + url.getFile());

		Runnable r = new Runnable() {
			public void run() {
				application = new SpeedometerTestApp();
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
	
	public void testCaseEmptyDisplay () throws ExecuteException {
		EventPlayer player = new EventPlayer(scenario);
		player.run(robot, "EMPTY_DISPLAY");

		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-empty.png", capturedImageFolder+"speedometer-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-zero.png",  capturedImageFolder+"speedometer-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-12.png",    capturedImageFolder+"speedometer-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-89.png",    capturedImageFolder+"speedometer-empty.png", 4, 26, 180, 180));
	}
	
	public void testCaseSpeedZero () throws ExecuteException {
		EventPlayer player = new EventPlayer(scenario);
		application.getSpeedometerFrame().speedometer.setSpeed (null, 0);
		player.run(robot, "SPEED_ZERO");

		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-empty.png", capturedImageFolder+"speedometer-zero.png", 4, 26, 180, 180));
		assertTrue (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-zero.png",  capturedImageFolder+"speedometer-zero.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-12.png",    capturedImageFolder+"speedometer-zero.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-89.png",    capturedImageFolder+"speedometer-zero.png", 4, 26, 180, 180));
	}
	
	public void testCaseSpeed12 () throws ExecuteException {
		EventPlayer player = new EventPlayer(scenario);
		application.getSpeedometerFrame().speedometer.setSpeed (null, 12);
		player.run(robot, "SPEED_12");
		
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-empty.png", capturedImageFolder+"speedometer-12.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-zero.png",  capturedImageFolder+"speedometer-12.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-12.png",    capturedImageFolder+"speedometer-12.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-89.png",    capturedImageFolder+"speedometer-12.png", 4, 26, 180, 180));
	}
	
	public void testCaseSpeed89 () throws ExecuteException {
		EventPlayer player = new EventPlayer(scenario);
		application.getSpeedometerFrame().speedometer.setSpeed (null, 89);
		player.run(robot, "SPEED_89");
		
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-empty.png", capturedImageFolder+"speedometer-89.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-zero.png",  capturedImageFolder+"speedometer-89.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-12.png",    capturedImageFolder+"speedometer-89.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"speedometer-89.png",    capturedImageFolder+"speedometer-89.png", 4, 26, 180, 180));
	}
	
}
