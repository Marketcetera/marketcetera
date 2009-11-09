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
     * unofficial dividend
     */
    UNOFFICIAL,
    /**
     * official dividend
     */
    OFFICIAL,
    /**
     * unknown status
     */
    UNKNOWN
}