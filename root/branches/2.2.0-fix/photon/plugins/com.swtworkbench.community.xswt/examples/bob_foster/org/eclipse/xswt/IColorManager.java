/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.xswt;

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
