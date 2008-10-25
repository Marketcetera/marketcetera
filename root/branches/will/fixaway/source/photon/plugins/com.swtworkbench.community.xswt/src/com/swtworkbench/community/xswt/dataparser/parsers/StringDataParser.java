/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Advanced Systems Concepts - Initial api and implementation
 *******************************************************************************/
package com.swtworkbench.community.xswt.dataparser.parsers;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;


/**
 * Class StringDataParser.  
 * 
 * @author daveo
 */
public class StringDataParser extends NonDisposableDataParser {

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
     */
    public Object parse(String source) throws XSWTException {
    	// FIXME: Need to expand \t for menus
    	// FIXME: This is where internationalization would go.  Or maybe in a separate version
    	//        that could be swapped in so you don't pay for what you don't use.
        return source;
    }
}
