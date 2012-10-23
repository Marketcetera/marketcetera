package org.marketcetera.core.trade;

/* $License$ */
/**
 * Identifies the type of entity that originated the report.
 *
 * @version $Id: Originator.java 16063 2012-01-31 18:21:55Z colin $
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
