package org.marketcetera.api.dao;

/* $License$ */

import java.util.Set;

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
     * Sets the method value.
     *
     * @param inMethod a <code>Set&lt;PermissionAttribute&gt;</code> value
     */
    public void setMethod(Set<PermissionAttribute> inMethod);
}
