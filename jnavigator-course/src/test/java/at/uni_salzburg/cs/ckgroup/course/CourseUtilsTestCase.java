/*
 * @(#) CourseUtilsTestCase.java
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
 * This test verifies the implementation of the <code>CourseUtilsTestCase</code>
 * class.
 */
public class CourseUtilsTestCase extends TestCase {

	/**
	 * This test verifies the <code>interpolateAngle()</code> of the class <code>CourseUtils</code>.
	 */
	public void testCase01 ()
	{
		assertNotNull (new CourseUtils ());
		
		double r;
		r = CourseUtils.interpolateAngle (0, 360, 0);
		assertEquals (0.0, r, 1E-9);
		
		r = CourseUtils.interpolateAngle (0, 360, 2.1);
		assertEquals (36.0, r, 1E-9);
		
		r = CourseUtils.interpolateAngle (0, 359, 0.1);
		assertEquals (35.9, r, 1E-9);
		
		r = CourseUtils.interpolateAngle (0, 360, 0.2);
		assertEquals (72.0, r, 1E-9);
		
		r = CourseUtils.interpolateAngle (0, 359, 0.9);
		assertEquals (323.1, r, 1E-9);

		r = CourseUtils.interpolateAngle (0, 360, 0.1);
		assertEquals (36.0, r, 1E-9);

		r = CourseUtils.interpolateAngle (0, -200, 0.1);
		assertEquals (340.0, r, 1E-9);

		r = CourseUtils.interpolateAngle (-20, -200, 0.1);
		assertEquals (322.0, r, 1E-9);

		r = CourseUtils.interpolateAngle (-200, -20, 0.1);
		assertEquals (178.0, r, 1E-9);
		
		r = CourseUtils.interpolateAngle (-200, 340, 0.1);
		assertEquals (178.0, r, 1E-9);
		
		r = CourseUtils.interpolateAngle (-200, 340, 1);
		assertEquals (340.0, r, 1E-9);
				
		r = CourseUtils.interpolateAngle (-200, 340, 0.1);
		assertEquals (178.0, r, 1E-9);
		
		r = CourseUtils.interpolateAngle (-200, 340, 0.8);
		assertEquals (304.0, r, 1E-9);
				
		r = CourseUtils.interpolateAngle (200, -340, 0.8);
		assertEquals (56.0, r, 1E-9);
	}
	
}
