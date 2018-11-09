package com.marketcetera.admin;

import org.marketcetera.persist.SummaryNDEntityBase;

/* $License$ */

/**
 * Represents a user in the system.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface User
        extends SummaryNDEntityBase
{
    /**
     * Indicates if the user is active or not.
     *
     * @return a <code>boolean</code> value
     */
    boolean isActive();
    /**
     * Get the hashed password value.
     *
     * @return a <code>String</code> value
     */
    String getHashedPassword();
}
