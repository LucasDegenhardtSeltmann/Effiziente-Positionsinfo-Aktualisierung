/*
 * This file is part of Locutil2.
 * 
 * Copyright (c) 2007 Thomas King <king@informatik.uni-mannheim.de>,
 * University of Mannheim, Germany
 * 
 * All rights reserved.
 *
 * Loclib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Loclib is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Loclib; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.pi4.locutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Centrally stores all loceva configuration.
 * 
 * @author faerber
 */
public abstract class Configuration {
	private static Properties properties;
	
	static {
		properties = new Properties();
	}

	/**
	 * @see java.util.Properties#getProperty
	 */
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public static void loadPropertyFile(File file) {
		try {
			if (file != null) properties.load(new FileInputStream(file)); 
		} catch (IOException ioe) {
			System.err.println("Property file " + file + " not found.");
		}
	}
}
