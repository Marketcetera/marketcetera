package org.marketcetera.core.event;

/**
 * Indicates the status of a dividend.
 *
 * @version $Id$
 * @since 2.0.0
 */
public enum DividendStatus
{
    /**
     * Any next dividend amount and date are projected based on last dividend amount and date using the published dividend frequency
     */
    UNOFFICIAL,
    /**
     * The next dividend amount and date published by the primary exchange
     */
    OFFICIAL,
    /**
     * unknown status
     */
    UNKNOWN
}