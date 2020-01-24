/*
 * @(#) ClockTestCase.java
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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.swing.SwingUtilities;

import junit.framework.TestCase;
import swingunit.extensions.ExtendedRobotEventFactory;
import swingunit.framework.EventPlayer;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;

/**
 * This test verifies the implementation of the <code>Clock</code> class.
 * 
 * @author Clemens Krainer
 */
public class ClockTestCase extends TestCase {

	GregorianCalendar calendar;
	ClockTestApp application;
	Scenario scenario;
	Robot robot;
	
	public static final String clockTestFileName = "at/uni_salzburg/cs/ckgroup/ui/ClockTest/ClockTestApp.xml";
	public static final String referenceImageFolder = "at/uni_salzburg/cs/ckgroup/ui/ClockTest/";
	public static final String capturedImageFolder = "target/";

	protected void setUp() throws Exception {
		super.setUp();
		
		calendar = new GregorianCalendar ();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

		URL url = Thread.currentThread ().getContextClassLoader ().getResource (clockTestFileName);
		assertNotNull ("Can not find file " + clockTestFileName, url);
		System.out.println ("ClockTestCase: url=" + url.getFile());

		Runnable r = new Runnable() {
			public void run() {
				application = new ClockTestApp();
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
		application.getClockFrame().clock.repaint();
		application.getClockFrame().clock.setDateTime (null);
		Thread.sleep(600);
		player.run(robot, "EMPTY_DISPLAY");
		
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-empty.png",          capturedImageFolder+"clock-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-19850325011327.png", capturedImageFolder+"clock-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20000101000000.png", capturedImageFolder+"clock-empty.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20091231235959.png", capturedImageFolder+"clock-empty.png", 4, 26, 180, 180));
	}
	
	public void testCaseClock19850325011327 () throws Exception {
		EventPlayer player = new EventPlayer(scenario);
		calendar.set (1985, 3, 25, 1, 13, 27);
		calendar.set(Calendar.MILLISECOND, 0);
		application.getClockFrame().clock.setDateTime (calendar.getTime ());
		Thread.sleep(600);
		player.run(robot, "CLOCK_19850325011327");

		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-empty.png",          capturedImageFolder+"clock-19850325011327.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-19850325011327.png", capturedImageFolder+"clock-19850325011327.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20000101000000.png", capturedImageFolder+"clock-19850325011327.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20091231235959.png", capturedImageFolder+"clock-19850325011327.png", 4, 26, 180, 180));
	}
	
	public void testCaseClock20000101000000 () throws Exception {
		EventPlayer player = new EventPlayer(scenario);
		calendar.set (2000, 0, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 123);
		application.getClockFrame().clock.setDateTime (calendar.getTime ());
		Thread.sleep(600);
		player.run(robot, "CLOCK_20000101000000");
		
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-empty.png",          capturedImageFolder+"clock-20000101000000.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-19850325011327.png", capturedImageFolder+"clock-20000101000000.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20000101000000.png", capturedImageFolder+"clock-20000101000000.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20091231235959.png", capturedImageFolder+"clock-20000101000000.png", 4, 26, 180, 180));
	}
	
	public void testCaseClock20091231235959 () throws Exception {
		EventPlayer player = new EventPlayer(scenario);
		calendar.set (2009, 11, 31, 23, 59, 59);
		calendar.set(Calendar.MILLISECOND, 962);
		application.getClockFrame().clock.setDateTime (calendar.getTime ());
		Thread.sleep(600);
		player.run(robot, "CLOCK_20091231235959");

		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-empty.png",          capturedImageFolder+"clock-20091231235959.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-19850325011327.png", capturedImageFolder+"clock-20091231235959.png", 4, 26, 180, 180));
		assertFalse (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20000101000000.png", capturedImageFolder+"clock-20091231235959.png", 4, 26, 180, 180));
		assertTrue  (ImageUtils.imagesAreEqual (referenceImageFolder+"clock-20091231235959.png", capturedImageFolder+"clock-20091231235959.png", 4, 26, 180, 180));
	}
	
}
