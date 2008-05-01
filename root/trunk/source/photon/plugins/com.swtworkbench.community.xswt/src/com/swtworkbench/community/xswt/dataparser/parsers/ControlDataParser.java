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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Control;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

/**
 * Class ControlDataParser.  
 * 
 * @author daveo
 */
public class ControlDataParser extends NonDisposableDataParser {

    private Map map = new HashMap();

    /**
     * Method put.  Put an ID into the id to control map
     * @param id the String ID
     * @param obj The object we're storing
     */
    public void put(String id, Object obj) {
        map.put(id, obj);
    }
    
    /**
     * Method getControlMap.  Returns a reference to the control map
     * @return The Map we use to link IDs to objects
     */
    public Map getControlMap() {
        return map;
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.dataparser.IDataParser#parse(java.lang.String)
     */
    public Object parse(String source) throws XSWTException {
        Control result = null;
        result = (Control) map.get(source);
        if (result == null) {
            throw new XSWTException("Undefined ID:" + source);
        }
        return result;
    }

}


