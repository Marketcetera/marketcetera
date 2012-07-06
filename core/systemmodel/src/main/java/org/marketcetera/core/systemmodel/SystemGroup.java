package org.marketcetera.core.systemmodel;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */

/**
 * Defines pre-defined system groups.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: SystemGroup.java 82316 2012-03-21 21:13:27Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: SystemGroup.java 82316 2012-03-21 21:13:27Z colin $")
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
