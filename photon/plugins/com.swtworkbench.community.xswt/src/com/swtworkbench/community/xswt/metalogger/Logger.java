package com.swtworkbench.community.xswt.metalogger;
/*
 * Copyright (c) 2003 Advanced Systems Concepts, Inc.  All rights reserved.
 * This file is made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 */

/**
 * Generic application message logging.  Sometimes you want to log to 
 * stdout/stderr, other times to Eclipse's logger, other times to log4j, 
 * etc.  Since all messages go through here, it's easy to redirect the 
 * messages somewhere other than the default location (such as log4j, 
 * for example) by changing the ILogger implementation used here.
 * 
 * @author DaveO
 */
public class Logger {

    private static ILogger logger = null;
    
    /**
     * Method log.  Returns the logger singleton.  If none has been
     * defined, defaults to StdLogger, the Stdout/Stderr logger.
     * 
     * @return The current IMsgLogger.
     */
    public static ILogger log() {
        if (logger == null) {
            logger = new StdLogger();
        }
        return logger;
    }
    
    /**
     * Method setLogger.  Sets the current logger.
     * @param logger
     */
    public static void setLogger(ILogger logger) {
        Logger.logger = logger;
    }

}

