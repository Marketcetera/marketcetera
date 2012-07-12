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
 * Class CharacterDataParser.  
 * 
 * @author daveo
 */
public class CharacterDataParser extends NonDisposableDataParser {

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
     */
    public Object parse(String source) throws XSWTException {
        Character result = null;
        
        try {
            if (source.length() > 1)
                throw new XSWTException("Length of a char cannot exceed 1: " + source);
            char intermediate = source.charAt(0);
            result = new Character(intermediate);
        } catch (Exception e) {
            throw new XSWTException(e);
        }
        return result;
    }

}
