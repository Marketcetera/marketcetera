/*******************************************************************************
 * Copyright (c) 2000, 2003 Advanced Systems Concepts, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     David Orme (ASC) - Initial implementation
 ******************************************************************************/
package com.swtworkbench.community.xswt.layoutbuilder;

import java.util.HashMap;

/**
 * Class ObjectStub.  Generates object names for SWTSourceBuilder.
 * 
 * @author djo
 */
public class ObjectStub {

    protected static HashMap typeUsageCount = new HashMap();
    
    private String dequalify(String className) {
        int lastPeriod = className.lastIndexOf(".");
        if (lastPeriod >= 0)
            className = className.substring(lastPeriod+1);
        return className;
    }
    
    private String generateName(String className) {
        String sourceName;
        
        Integer nameCount = (Integer) typeUsageCount.get(className);
        if (nameCount == null) {
            nameCount = new Integer(2);
            typeUsageCount.put(className, nameCount);
            sourceName = lowerCaseFirstLetter(className) + "1";
        } else {
            sourceName = lowerCaseFirstLetter(className) + nameCount;
            nameCount = new Integer(nameCount.intValue() + 1);
            typeUsageCount.put(className, nameCount);
        }
        return sourceName;
    }

    public ObjectStub(String className) {
        className = dequalify(className);
        this.sourceName = generateName(className);
        this.className = className;
    }
    
    public ObjectStub(String className, String sourceName) {
        className = dequalify(className);
        this.className = className;
        if (sourceName != null)
            this.sourceName = lowerCaseFirstLetter(sourceName);
        else
            this.sourceName = generateName(className);
    }
        
    public final String className;
    public final String sourceName;

    private String lowerCaseFirstLetter(String source) {
        StringBuffer buf = new StringBuffer(source.substring(0, 1).toLowerCase());
        buf.append(source.substring(1, source.length()));
        return buf.toString();
    }
    
}
