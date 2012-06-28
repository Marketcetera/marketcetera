package org.marketcetera.event;

import org.marketcetera.core.attributes.ClassVersion;

/**
 * Indicates the status of a dividend.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: DividendStatus.java 16063 2012-01-31 18:21:55Z colin $
 * @since 2.0.0
 */
@ClassVersion("$Id: DividendStatus.java 16063 2012-01-31 18:21:55Z colin $")
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