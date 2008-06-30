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

import org.eclipse.swt.graphics.RGB;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.DataParser;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;


/**
 * Class RGBDataParser.  
 * 
 * @author daveo
 */
public class RGBDataParser extends NonDisposableDataParser {

    private DataParser dataParser;

	public RGBDataParser(DataParser parser) {
    	this.dataParser = parser;
	}

	private int value(int[] values, int index) {
		if (values.length <= index) {
			return 0;
		}
		int value = values[index];
		return (value < 0 ? 0 : (value > 255 ? 255 : value));
	}
	
	/* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
     */
    public Object parse(String source, IDataParserContext context) throws XSWTException {
        int[] intArray;
        try {
            intArray = (int[])context.parse(source, int[].class); 
        } catch (XSWTException e) {
            throw new XSWTException("Unable to parse int[]", e);
        }
        return new RGB(value(intArray, 0), value(intArray, 1), value(intArray, 2));
    }
}
