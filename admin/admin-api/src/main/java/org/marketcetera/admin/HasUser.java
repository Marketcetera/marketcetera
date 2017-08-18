package org.marketcetera.admin;

/* $License$ */

/**
 * Indicates that the implementer has a {@link User} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasUser
{
    /**
     * Get the user value.
     *
     * @return a <code>User</code> value
     */
    User getUser();
}
