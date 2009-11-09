package org.marketcetera.options;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Represents the expiration type of an option.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum ExpirationType
{
    /**
     * European expiration options may be exercised only at contract expiration
     */
    EUROPEAN,
    /**
     * American expiration options may be exercised at any point up to and including contract expiration
     */
    AMERICAN
}
