package com.marketcetera.colin.app.security;

import org.marketcetera.admin.User;

/* $License$ */

/**
 * Gets the current user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@FunctionalInterface
public interface CurrentMetcUser
{
    /**
     * Get the current user.
     *
     * @return a <code>User</code> value
     */
    User getUser();
}
