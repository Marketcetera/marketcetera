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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;


/**
 * A metalogger plug-in that logs to Eclipse's logging framework
 * @author daveo
 */
public class EclipseLogger extends AbstractLogger {
    
    /**
     * Construct an EclipseLogger.
     * 
     * @param plugin The plugin controlling this logger.
     */
    public EclipseLogger(AbstractUIPlugin plugin) {
        this.plugin = plugin;
    }
    
    private AbstractUIPlugin plugin;

    /* (non-Javadoc)
     * @see com.swtworkbench.swtutils.logger.ILogger#error(java.lang.Throwable, java.lang.String)
     */
    public void error(Throwable t, String message) {
        plugin.getLog().log(new Status(
                IStatus.ERROR,
                plugin.getBundle().getSymbolicName(),
                IStatus.ERROR,
                message,
                t));
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.swtutils.logger.ILogger#message(java.lang.String)
     */
    public void message(String message) {
        plugin.getLog().log(new Status(
                IStatus.WARNING,
                plugin.getBundle().getSymbolicName(),
                IStatus.WARNING,
                message,
                null));
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.community.xswt.metalogger.ILogger#data(java.lang.String)
     */
    public void data(String data) {
        plugin.getLog().log(new Status(
                IStatus.WARNING,
                plugin.getBundle().getSymbolicName(),
                IStatus.WARNING,
                data,
                null));
    }

    /* (non-Javadoc)
     * @see com.swtworkbench.swtutils.logger.ILogger#debug(java.lang.Class, java.lang.String)
     */
    public void debug(Class subject, String message) {
        if (isDebug(subject))
            plugin.getLog().log(new Status(
                    IStatus.INFO,
                    plugin.getBundle().getSymbolicName(),
                    IStatus.INFO,
                    subject.getName() + ": " + message,
                    null));
    }
    

}
