/*******************************************************************************
 * Copyright (c) 2000, 2003 db4objects, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     David Orme (db4objects) - Initial implementation
 ******************************************************************************/
package com.swtworkbench.community.xswt.metalogger;


/**
 * TeeLogger. A Tee logger logs to two other loggers
 *
 * @author djo
 */
public class TeeLogger implements ILogger {
    private ILogger first;
    private ILogger second;

    /**
     * Construct a TeeLogger on two other loggers.
     * 
     * @param first A delegate logger
     * @param second A delegate logger
     */
    public TeeLogger(ILogger first, ILogger second) {
        this.first = first;
        this.second = second;
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#error(java.lang.Throwable, java.lang.String)
     */
    public void error(Throwable t, String message) {
        first.error(t, message);
        second.error(t, message);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#message(java.lang.String)
     */
    public void message(String message) {
        first.message(message);
        second.message(message);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#data(java.lang.String)
     */
    public void data(String data) {
        first.data(data);
        second.data(data);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#debug(java.lang.Class, java.lang.String)
     */
    public void debug(Class subject, String message) {
        first.debug(subject, message);
        second.debug(subject, message);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#isDebug()
     */
    public boolean isDebug() {
        return first.isDebug() || second.isDebug();
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#setDebug(boolean)
     */
    public void setDebug(boolean debugMode) {
        first.setDebug(debugMode);
        second.setDebug(debugMode);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#setDebug(java.lang.Class, boolean)
     */
    public void setDebug(Class subject, boolean enabled) {
        first.setDebug(subject, enabled);
        second.setDebug(subject, enabled);
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#isDebug(java.lang.Class)
     */
    public boolean isDebug(Class subject) {
        return first.isDebug(subject) || second.isDebug(subject);
    }

}
