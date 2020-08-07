package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates the implementor has a {@link UserID} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasUserId
{
    /**
     * Get the user ID value.
     *
     * @return a <code>UserID</code> value
     */
    UserID getUserId();
    /**
     * Set the user ID value.
     *
     * @param inUserId a <code>UserID</code> value
     */
    void setUserId(UserID inUserId);
}
