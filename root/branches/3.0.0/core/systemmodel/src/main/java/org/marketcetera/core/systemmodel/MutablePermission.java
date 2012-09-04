package org.marketcetera.core.systemmodel;

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
     * Sets the permission value.
     *
     * @param inPermission a <code>String</code> value
     */
    public void setPermission(String inPermission);
}
