package org.marketcetera.admin;

import org.marketcetera.trade.UserID;

/* $License$ */

/**
 * Provides a mutable {@link User} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableUser
        extends User
{
    /**
     * Set the name value.
     *
     * @param inName a <code>String</code> value
     */
    void setName(String inName);
    /**
     * Set the description value.
     *
     * @param inDescription a <code>String</code> value
     */
    void setDescription(String inDescription);
    /**
     * Set the user ID value.
     *
     * @param inUserId a <code>UserID</code> value
     */
    void setUserId(UserID inUserId);
    /**
     * Set the active indicator value.
     *
     * @param inIsActive a <code>boolean</code> value
     */
    void setIsActive(boolean inIsActive);
    /**
     * Set the hashed password value.
     *
     * @param inHashedPassword a <code>String</code> value
     */
    void setHashedPassword(String inHashedPassword);
}
