package org.marketcetera.core.trade;

/* $License$ */
/**
 * Identifies the type of entity that originated the report.
 *
 * @version $Id$
 * @since 1.0.0
 */
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
