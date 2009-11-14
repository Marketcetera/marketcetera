package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Indicates the status of a dividend.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
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