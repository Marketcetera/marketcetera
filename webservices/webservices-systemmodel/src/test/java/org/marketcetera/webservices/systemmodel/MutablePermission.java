package org.marketcetera.webservices.systemmodel;

/* $License$ */

import org.marketcetera.api.dao.Permission;

/**
 * Provides a mutable view of an <code>Permission</code> object.
 *
 * @version $Id$
 * @since $Release$
 */
public interface MutablePermission
        extends Permission
{
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName);
}
