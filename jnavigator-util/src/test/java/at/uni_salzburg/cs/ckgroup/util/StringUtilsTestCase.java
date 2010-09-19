/*
 * @(#) StringUtilsTestCase.java
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
package at.uni_salzburg.cs.ckgroup.util;

import junit.framework.TestCase;


/**
 * This tests verify the implementation of the <code>StringUtils</code> class.
 *
 * @author Clemens Krainer
 */
public class StringUtilsTestCase extends TestCase {

	public void testCase01 () {
		StringUtils u = new StringUtils ();
		assertNotNull (u);
	}
	
	public void testCase02 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "");
		assertEquals (0, result.length);
	}
	
	public void testCase11 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a");
		assertEquals (1, result.length);
		assertEquals ("a", result[0]);
	}
	
	public void testCase12 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " b");
		assertEquals (1, result.length);
		assertEquals ("b", result[0]);
	}
	
	public void testCase13 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "c ");
		assertEquals (1, result.length);
		assertEquals ("c", result[0]);
	}
	
	public void testCase14 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " d ");
		assertEquals (1, result.length);
		assertEquals ("d", result[0]);
	}
	
	public void testCase21 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a,b");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	public void testCase22 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a,b");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	public void testCase23 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a,b ");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	public void testCase24 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a,b ");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	public void testCase25 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a ,b");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	public void testCase26 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a, b");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	public void testCase27 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a ,b ");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	public void testCase28 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a , b ");
		assertEquals (2, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
	}
	
	
	public void testCase31 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a,b,c");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
		assertEquals ("c", result[2]);
	}
	
	public void testCase32 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a,b,c");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
		assertEquals ("c", result[2]);	}
	
	public void testCase33 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a,b,c ");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
		assertEquals ("c", result[2]);
	}
	
	public void testCase34 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a,b,c ");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
		assertEquals ("c", result[2]);
	}

	public void testCase35 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a ,b ,c ");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
		assertEquals ("c", result[2]);
	}
	
	public void testCase36 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a, b, c");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
		assertEquals ("c", result[2]);
	}
	
	public void testCase37 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a , b , c ");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b", result[1]);
		assertEquals ("c", result[2]);
	}
	
	public void testCase38 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', "a,b c,d");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b c", result[1]);
		assertEquals ("d", result[2]);
	}
	
	public void testCase39 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a, b c , d");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b c", result[1]);
		assertEquals ("d", result[2]);
	}
	
	public void testCase40 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', " a, b  c , d");
		assertEquals (3, result.length);
		assertEquals ("a", result[0]);
		assertEquals ("b  c", result[1]);
		assertEquals ("d", result[2]);
	}
	
	public void testCase41 () {
		String[] result = StringUtils.splitOnCharAndTrim(',', ",,");
		assertEquals (3, result.length);
		assertEquals ("", result[0]);
		assertEquals ("", result[1]);
		assertEquals ("", result[2]);
	}
}
