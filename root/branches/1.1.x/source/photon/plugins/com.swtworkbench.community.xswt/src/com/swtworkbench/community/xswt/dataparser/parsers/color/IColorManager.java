/*******************************************************************************
 * Copyright (c) 2000, 2003 Object Factory, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Bob Foster - The color manager idea; XSWT top-level node idea; some other 
 *                        important stuff
 ******************************************************************************/

/*
 * (c) Copyright 2003 Object Factory Inc. All rights reserved.
 * 
 * This code came from an attachment to bug 38109 from Bugzilla.  According 
 * to the Eclipse.org terms of use, that makes this code fall under the CPL.  
 * I (Dave Orme) just added the appropriate CPL license header to the top.
 */
package com.swtworkbench.community.xswt.dataparser.parsers.color;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public interface IColorManager {

	/**
	 * Returns the color object for the value represented by the given
	 * <code>RGB</code> object.
	 *
	 * @param rgb the rgb color specification
	 * @return the color object for the given rgb value
	 */
	Color getColor(Display display, RGB rgb);	
	
	/**
	 * Tells this object to dispose all its managed colors.
	 */
	void dispose();
}
