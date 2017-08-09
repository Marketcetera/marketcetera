package org.marketcetera.admin;

import org.marketcetera.persist.SummaryEntityBase;

/* $License$ */

/**
 * Represents an attribute associated with a user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserAttribute.java 84561 2015-03-31 18:18:14Z colin $
 * @since 1.2.0
 */
public interface UserAttribute
        extends SummaryEntityBase
{
    /**
     * Gets the user value.
     *
     * @return a <code>User</code> value
     */
    User getUser();
    /**
     * Gets the user attribute type value.
     *
     * @return a <code>UserAttributeType</code> value
     */
    UserAttributeType getAttributeType();
    /**
     * Gets the attribute value.
     *
     * @return a <code>String</code> value
     */
    String getAttribute();
}
