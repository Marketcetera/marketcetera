package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Identifies the type of entity that originated the report.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum Originator {
    /**
     * Indicates that the report was originated by the system's server.
     */
    Server,
    /**
     * Indicates that the report was originated by the FIX broker. 
     */
    Broker
}
