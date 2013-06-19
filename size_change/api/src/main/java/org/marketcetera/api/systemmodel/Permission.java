package org.marketcetera.api.systemmodel;

import java.util.Set;


/* $License$ */

/**
 * Represents a role granted to a {@link org.marketcetera.api.security.User}.
 *
 * @version $Id$
 * @since $Release$
 */
public interface Permission
        extends SystemObject, NamedObject, VersionedObject
{
    /**
     * Get the permissions attributes assigned to this permission.
     *
     * @return a <code>Set&lgt;PermissionAttribute&gt;</code> value
     */
    public Set<PermissionAttribute> getMethod();
    /**
     * Indicates the domain for this permission, uniquely identifying a particular permission topic.
     *
     * @return a <code>String</code> value
     */
    public String getPermission();
}
