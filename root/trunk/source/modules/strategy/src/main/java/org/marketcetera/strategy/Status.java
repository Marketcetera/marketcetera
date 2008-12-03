package org.marketcetera.strategy;

import org.marketcetera.core.ClassVersion;

/* $License$ */

/**
 * Indicates the status of a running strategy.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum Status
{
    /**
     * the strategy is not currently running
     */
    NOT_RUNNING,
    /**
     * the strategy is currently running
     */
    RUNNING,
    /**
     * the strategy is not running because of error
     */
    ERROR
}
