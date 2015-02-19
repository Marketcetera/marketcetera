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

import com.swtworkbench.community.xswt.StyleParser;
import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;


/**
 * Class IntDataParser.  
 * 
 * @author daveo
 */
public class IntDataParser extends NonDisposableDataParser  {

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
     */
    public Object parse(String source) throws XSWTException {
        Integer result = null;
        
        // Assume it's a real int first...
        try {
        	// Changed by Yu You
            //int intermediate = Integer.parseInt(source);
            //result = new Integer(intermediate);
        	result = Integer.decode(source);
        } catch (Exception e) {
        }
        if (result != null) {
        	return result;
        }
        // We couldn't parse it so we'll try to interpret it as an SWT constant
        int intermediate = StyleParser.parse(source);
        return new Integer(intermediate);
    }
}
