/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Bob Foster - The color manager idea; XSWT top-level node idea; some other 
 *                        important stuff
 *     David Orme (ASC) - Rewrote: switched to a reflection-based implementation
 *     Yu You           - Rewrite: switch to DataParser to manage the resource dispose
 ******************************************************************************/

package com.swtworkbench.community.xswt.dataparser.parsers.color;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.swtworkbench.community.xswt.StyleParser;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.DataParser;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Class ColorDataParser. This class converts between string color
 * representations and SWT Color objects.
 * 
 * @author daveo
 */
public class ColorDataParser extends NonDisposableDataParser {
	
	private DataParser dataParser;

	public ColorDataParser(DataParser parser) {
		this.dataParser = parser;
		display = Display.getDefault();
	}

	/*
	 * Map color names to RGB objects
	 * 
	 * can't use TreeMap - RGB is not Comparable
	 */
	private static Map mapColors = new HashMap();
	static {
		Field[] fields = CSSColors.class.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];

			if (RGB.class.equals(field.getType())) {
				int iModifiers = field.getModifiers();

				if (Modifier.isPublic(iModifiers)
						&& Modifier.isStatic(iModifiers)
						&& Modifier.isFinal(iModifiers)) {
					try {
						mapColors.put(field.getName(), field.get(null));
					} catch (IllegalAccessException eIllegalAccess) {
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
	 */
	public Object parse(String source) {
		// Step 1: parse CSS constants
		RGB rgb = (RGB) mapColors.get(source.trim());
		if (rgb != null) {
			isDisposable = true;
			return new Color(display, rgb);
		}
		try {
			// Step 2: parse SWT.COLOR_XXX
			int intermediate = StyleParser.parse(source);
			isDisposable = false;
			return display.getSystemColor(intermediate);
		} catch (XSWTException e1) {
			// We do nothing here but contine parsing attempt.
		}

		// Step 2: parse RGB values (three parameters)
		try {
			isDisposable = true;
			return new Color(display, (RGB) dataParser.parse(
					source, RGB.class));
		} catch (XSWTException e) {
			isDisposable = false;
			return display.getSystemColor(SWT.COLOR_RED);
		}
		//return display.getSystemColor(SWT.COLOR_RED);
	}

	private Display display = null;

	/**
	 * Method setDisplay. Sets the Display on which to operate.
	 * 
	 * @param display
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

}