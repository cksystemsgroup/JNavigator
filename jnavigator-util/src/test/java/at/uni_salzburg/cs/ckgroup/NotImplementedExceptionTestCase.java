/*
 * @(#) NotImplementedExceptionTestCase.java
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
package at.uni_salzburg.cs.ckgroup;

import junit.framework.TestCase;

/**
 * This test verifies the implementation of the
 * <code>NotImplementedException</code> class.
 * 
 * @author Clemens Krainer
 */
public class NotImplementedExceptionTestCase extends TestCase {

	/**
	 * Verify that the <code>NotImplementedException</code> inherits the
	 * <code>RuntimeException</code>.
	 */
	public void testCase01 () {
		NotImplementedException e = new NotImplementedException ();
		assertTrue ("check for RuntimeException", e instanceof RuntimeException);
	}
	
	/**
	 * Verify that the <code>NotImplementedException</code> contains no message.
	 */
	public void testCase02 () {
		try {
			throw new NotImplementedException ();
		} catch (RuntimeException e) {
			assertNull ("NULL exception message", e.getMessage());
		} catch (Throwable t) {
			fail ();
		}
	}
}
