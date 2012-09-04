package org.marketcetera.strategy;

import org.marketcetera.core.util.misc.ClassVersion;

/**
 * Describes the types of data that a strategy can emit.
 *
 * @version $Id: OutputType.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
public enum OutputType
{
    /**
     * orders created by this strategy
     */
    ORDERS,
    /**
     * trade suggestions created by this strategy
     */
    SUGGESTIONS,
    /**
     * events created by this strategy
     */
    EVENTS,
    /**
     * notifications created by this strategy
     */
    NOTIFICATIONS,
    /**
     * log output created by this strategy
     */
    LOG,
    /**
     * all objects, regardless of type (includes all the above)
     */
    ALL
}