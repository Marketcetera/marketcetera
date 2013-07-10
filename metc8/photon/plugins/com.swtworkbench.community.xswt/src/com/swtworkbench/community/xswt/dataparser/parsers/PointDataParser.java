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


import org.eclipse.swt.graphics.Point;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.DataParser;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Class PointDataParser.  
 * 
 * @author daveo
 */
public class PointDataParser extends NonDisposableDataParser {

    private DataParser dataParser;

	public PointDataParser(DataParser parser) {
    	this.dataParser = parser;
	}

	/* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
     */
    public Object parse(String source) throws XSWTException {
        int[] intArray;
        try {
            intArray = (int[]) dataParser.parse(source, int[].class); 
        } catch (XSWTException e) {
            throw new XSWTException("Unable to parse int[]", e);
        }
        int length = intArray.length;       
        return new Point(length >= 1 ? intArray[0] : 0, length >= 2 ? intArray[1] : 0);
    }
}
