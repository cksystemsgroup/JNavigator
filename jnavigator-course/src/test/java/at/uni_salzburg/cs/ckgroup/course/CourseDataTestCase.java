/*
 * @(#) CourseDataTestCase.java
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
package at.uni_salzburg.cs.ckgroup.course;

import junit.framework.TestCase;

/**
 * This tests verify the implementation of the <code>CourseData</code> class.
 * 
 * @author Clemens Krainer
 */
public class CourseDataTestCase extends TestCase
{
	public void testCase01 ()
	{
		CourseData d = new CourseData (1,2,3,4,false);
		
		assertEquals (1, d.distance,1E-8);
		assertEquals (2, d.speed, 1E-8);
		assertEquals (3, d.elevation, 1E-8);
		assertEquals (4, d.course, 1E-8);
		assertFalse (d.courseIsValid);
		assertEquals ("distance=1.0, speed=2.0, elevation=3.0, course=4.0, courseIsValid=false", d.toString());
	}

	public void testCase02 ()
	{
		CourseData d = new CourseData (-1,2,-3,4,true);
		
		assertEquals (-1, d.distance,1E-8);
		assertEquals (2, d.speed, 1E-8);
		assertEquals (-3, d.elevation, 1E-8);
		assertEquals (4, d.course, 1E-8);
		assertTrue (d.courseIsValid);
		assertEquals ("distance=-1.0, speed=2.0, elevation=-3.0, course=4.0, courseIsValid=true", d.toString());
	}

	public void testCase03 ()
	{
		CourseData d = new CourseData (1,-2,3,-4,false);
		
		assertEquals (1, d.distance,1E-8);
		assertEquals (-2, d.speed, 1E-8);
		assertEquals (3, d.elevation, 1E-8);
		assertEquals (-4, d.course, 1E-8);
		assertFalse (d.courseIsValid);
		assertEquals ("distance=1.0, speed=-2.0, elevation=3.0, course=-4.0, courseIsValid=false", d.toString());
	}

	public void testCase04 ()
	{
		CourseData d = new CourseData (-1,2,3,-4,true);
		
		assertEquals (-1, d.distance,1E-8);
		assertEquals (2, d.speed, 1E-8);
		assertEquals (3, d.elevation, 1E-8);
		assertEquals (-4, d.course, 1E-8);
		assertTrue (d.courseIsValid);
		assertEquals ("distance=-1.0, speed=2.0, elevation=3.0, course=-4.0, courseIsValid=true", d.toString());
	}
	
}
