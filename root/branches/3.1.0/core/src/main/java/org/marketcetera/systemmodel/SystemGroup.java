package org.marketcetera.systemmodel;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Defines pre-defined system groups.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemGroup.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: SystemGroup.java 82384 2012-07-20 19:09:59Z colin $")
public enum SystemGroup
{
    /**
     * administrative users
     */
    ADMINISTRATORS,
    /**
     * business users
     */
    USERS;
}
