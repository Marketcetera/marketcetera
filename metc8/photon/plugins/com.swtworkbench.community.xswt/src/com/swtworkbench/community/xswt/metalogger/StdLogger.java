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

/**
 * Class StdLogger.  A stdout/stderr logger.
 * 
 * @author daveo
 */
public class StdLogger extends AbstractLogger {
    
    /* (non-Javadoc)
     * @see com.swtworkbench.swtutils.logger.ILogger#error(java.lang.Throwable, java.lang.String)
     */
    public void error(Throwable t, String message) {
        System.err.println(message);
        t.printStackTrace();
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.swtutils.logger.ILogger#message(java.lang.String)
     */
    public void message(String message) {
        System.out.println(message);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#data(java.lang.String)
     */
    public void data(String data) {
        System.out.print(data);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.swtutils.logger.ILogger#debug(java.lang.Class, java.lang.String)
     */
    public void debug(Class subject, String message) {
        if (isDebug(subject)) System.out.println(message);
    }

}
