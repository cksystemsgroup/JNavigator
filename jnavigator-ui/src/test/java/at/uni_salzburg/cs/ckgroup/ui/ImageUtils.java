/*
 * @(#) ImageUtils.java
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

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class implements functionality needed for the GUI tests.
 * 
 * @author Clemens Krainer
 */
public class ImageUtils {

	public static boolean imagesAreEqual (String referenceImagePath, String capturedImagePath, int x, int y, int w, int h) {
		BufferedImage referenceImage = null;
		BufferedImage capturedImage;
		try {
			referenceImage = ImageIO.read (Thread.currentThread ().getContextClassLoader ().getResourceAsStream (referenceImagePath));
			capturedImage = ImageIO.read (new File (capturedImagePath));
		} catch (IOException e) {
			e.printStackTrace ();
			return false;
		}

		Raster referenceImageRaster = referenceImage.getData();
		Raster capturedImageRaster = capturedImage.getData();
//		System.out.println ("ImageUtils.imagesAreEqual: reference image width=" + referenceImage.getWidth() + ", height=" + referenceImage.getHeight());
//		System.out.println ("ImageUtils.imagesAreEqual: captured image width=" + capturedImage.getWidth() + ", height=" + capturedImage.getHeight());
		
		int[] referencePixels = referenceImageRaster.getPixels (x, y, w, h, (int[])null);
		int[] capturedPixels = capturedImageRaster.getPixels (x, y, w, h, (int[])null);
		
		if (referencePixels.length != capturedPixels.length)
			return false;
		
		int failures = 0;
		for (int k=0; k < referencePixels.length; k++)
			if (referencePixels[k] != capturedPixels[k])
				++failures;
		
		if (failures > 0) {
//			System.out.println ("ImageUtils.imagesAreEqual: failures=" + failures + "/" + referencePixels.length + ", name=" + capturedImagePath);
			return false;
		}
		
		return true;
	}
}
