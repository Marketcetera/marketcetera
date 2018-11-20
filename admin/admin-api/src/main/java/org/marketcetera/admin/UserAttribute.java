package org.marketcetera.admin;

import org.marketcetera.persist.SummaryEntityBase;

/* $License$ */

/**
 * Represents an attribute associated with a user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
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
