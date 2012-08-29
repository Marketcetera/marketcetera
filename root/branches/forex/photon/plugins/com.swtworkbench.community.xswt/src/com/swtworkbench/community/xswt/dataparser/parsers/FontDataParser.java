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
 ******************************************************************************/

package com.swtworkbench.community.xswt.dataparser.parsers;

import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import com.swtworkbench.community.xswt.StyleParser;
import com.swtworkbench.community.xswt.dataparser.DisposableDataParser;

/**
 * Class FontDataParser. Allows XSWT to reference literal fonts. Use of this
 * class is discouraged since we really need to find a cross-platform way to
 * return system fonts by name or something like that...
 * <p>
 * 
 * This data parser is not enabled in XSWT by default. Enable it by directly
 * calling DataParser.registerDataParser(...) and by initializing the Display
 * property on this object using setDisplay().
 * 
 * @author daveo
 */
public class FontDataParser extends DisposableDataParser {

	/*
	 * Construct a FontDataParser
	 * 
	 */
	public FontDataParser() {
		display = Display.getDefault();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
	 */
	public Object parse(String source) {
		String name = null;
		int height = 10;
		int style = SWT.NORMAL;

		StringTokenizer stringTokenizer = new StringTokenizer(source, ", \t\r\n");

		if (stringTokenizer.hasMoreTokens())
			name = stringTokenizer.nextToken().trim();

		if (stringTokenizer.hasMoreTokens()) {
			try {
				height = Integer.parseInt(stringTokenizer.nextToken().trim());
			} catch (NumberFormatException eNumberFormat) {
				height = 10;
			}
		}

		if (stringTokenizer.hasMoreTokens()) {
			try {
				style = StyleParser.parse(stringTokenizer.nextToken().trim());
			} catch (Exception e) {
				style = SWT.NORMAL;;
			}
		}
		if (display == null)
			display = Display.getCurrent();
		Font f = null;
		try {
			f = new Font(display, name, height, style);
		} catch (Exception e) {
			f = display.getSystemFont();
		}
		return f;
	}

	private Display display = null;

	/**
	 * Method setDisplay. Set the SWT Display on which we will create Font
	 * objects
	 * 
	 * @param display
	 */
	public void setDisplay(Display display) {
		this.display = display;
	}

}