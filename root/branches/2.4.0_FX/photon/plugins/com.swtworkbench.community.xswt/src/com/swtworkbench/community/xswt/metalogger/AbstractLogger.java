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
package com.swtworkbench.community.xswt.metalogger;

import java.util.HashMap;

/**
 * Class AbstractLogger.  An abstract ILogger implementation that provides
 * a default implementation for all debug mode handling. 
 * 
 * @author djo
 */
public abstract class AbstractLogger implements ILogger {
    private boolean debug = false;
    
	/* (non-Javadoc)
	 * @see com.swtworkbench.swtutils.logger.ILogger#isDebug()
	 */
	public boolean isDebug() {
		return debug;
	}
    
    /* (non-Javadoc)
	 * @see com.swtworkbench.swtutils.logger.ILogger#setDebug(boolean)
	 */
	public void setDebug(boolean debugMode) {
        this.debug = debugMode;
	}
    
    private HashMap subjects = new HashMap();
    
    /* (non-Javadoc)
	 * @see com.swtworkbench.swtutils.logger.ILogger#isDebug(java.lang.Class)
	 */
	public boolean isDebug(Class subject) {
        if (subject == null) 
            throw new IllegalArgumentException("MetaLogger: Subject of debug message can't be null!");
        
        if (debug) return true;
		return subjects.containsKey(subject);
	}
    
    /* (non-Javadoc)
	 * @see com.swtworkbench.swtutils.logger.ILogger#setDebug(java.lang.Class, boolean)
	 */
	public void setDebug(Class subject, boolean enabled) {
        if (subject == null) 
            throw new IllegalArgumentException("MetaLogger: Subject of debug message can't be null!");
        
		Object result = subjects.get(subject);
        if (enabled) {
            if (result == null) {
                subjects.put(subject, Boolean.TRUE);
            }
        } else {
            if (result != null) {
                subjects.remove(subject);
            }
        }
	}
}
