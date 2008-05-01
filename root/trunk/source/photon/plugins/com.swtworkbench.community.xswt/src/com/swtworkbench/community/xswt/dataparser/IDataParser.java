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
package com.swtworkbench.community.xswt.dataparser;

import com.swtworkbench.community.xswt.XSWTException;

/**
 * Class IDataParser.  A function object interface for objects that can parse 
 * a source string into a particular type.
 * 
 * @author daveo
 */
public interface IDataParser {
	
	/**
	 * Parse the value for specific class type.
	 * 
	 * @param source The string
	 * @return The cooresponding data object
	 * @throws XSWTException
	 */
    public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException;
    
    /**
     * Return the value to check whether the specific class requires automatical dispose. 
     * 
     * Most primitive types return false. Color and Font often return true.
     * @return boolean the value
     */
    public boolean isResourceDisposeRequired();
}
