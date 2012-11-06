package org.marketcetera.api.systemmodel;

/* $License$ */

import java.util.Set;


/**
 * Provides a mutable view of an <code>Permission</code> object.
 *
 * @version $Id$
 * @since $Release$
 */
public interface MutablePermission
        extends Permission, MutableNamedObject
{
    /**
     * Sets the method value.
     *
     * @param inMethod a <code>Set&lt;PermissionAttribute&gt;</code> value
     */
    public void setMethod(Set<PermissionAttribute> inMethod);
    /**
     * Sets the permission value.
     *
     * @param inPermission a <code>String</code> value
     */
    public void setPermission(String inPermission);
}
