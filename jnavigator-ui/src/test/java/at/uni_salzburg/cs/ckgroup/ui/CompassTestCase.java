/*
 * @(#) CompassTestCase.java
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
import java.io.IOException;
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
 * This test verifies the implementation of the <code>Compass</code> class.
 * 
 * @author Clemens Krainer
 */
public class CompassTestCase extends TestCase {

	CompassTestApp application;
	Scenario scenario;
	Robot robot;
	
	public static final String compassTestFileName = "at/uni_salzburg/cs/ckgroup/ui/CompassTest/CompassTestApp.xml";
	public static final String referenceImageFolder = "at/uni_salzburg/cs/ckgroup/ui/CompassTest/";
	public static final String capturedImageFolder = "target/";

	protected void setUp() throws Exception {
		super.setUp();

		URL url = Thread.currentThread ().getContextClassLoader ().getResource (compassTestFileName);
		assertNotNull ("Can not find file " + compassTestFileName, url);
		System.out.println ("CompassTestCase: url=" + url.getFile());

		Runnable r = new Runnable() {
			public void run() {
				application = new CompassTestApp();
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

	public void testCaseEmptyDisplay () throws ExecuteException, IOException {
		EventPlayer player = new EventPlayer(scenario);
		player.run(robot, "EMPTY_DISPLAY");

		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-empty.png", capturedImageFolder+"compass-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-n.png",     capturedImageFolder+"compass-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-078.png",   capturedImageFolder+"compass-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-355.png",   capturedImageFolder+"compass-empty.png", 4, 26, 180, 180));
	}
	
	public void testCaseSpeedotudeZero () throws ExecuteException, IOException {
		EventPlayer player = new EventPlayer(scenario);
		application.getCompassFrame().compass.setCourse (null, 0);
		player.run(robot, "ORIENTATION_N");

		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-empty.png", capturedImageFolder+"compass-n.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-n.png",     capturedImageFolder+"compass-n.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-078.png",   capturedImageFolder+"compass-n.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-355.png",   capturedImageFolder+"compass-n.png", 4, 26, 180, 180));
	}
	
	public void testCaseCompass078 () throws ExecuteException, IOException {
		EventPlayer player = new EventPlayer(scenario);
		application.getCompassFrame().compass.setCourse (null, 78);
		player.run(robot, "ORIENTATION_078");
		
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-empty.png", capturedImageFolder+"compass-078.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-n.png",     capturedImageFolder+"compass-078.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-078.png",   capturedImageFolder+"compass-078.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-355.png",   capturedImageFolder+"compass-078.png", 4, 26, 180, 180));
	}
	
	public void testCaseCompass355 () throws ExecuteException, IOException {
		EventPlayer player = new EventPlayer(scenario);
		application.getCompassFrame().compass.setCourse (null, 355);
		player.run(robot, "ORIENTATION_355");
		
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-empty.png", capturedImageFolder+"compass-355.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-n.png",     capturedImageFolder+"compass-355.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-078.png",   capturedImageFolder+"compass-355.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"compass-355.png",   capturedImageFolder+"compass-355.png", 4, 26, 180, 180));
	}
	
}
