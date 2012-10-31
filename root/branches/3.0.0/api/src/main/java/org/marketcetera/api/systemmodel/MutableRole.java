package org.marketcetera.api.systemmodel;

import java.util.Set;

import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Provides a mutable view of a <code>Role</code> object.
 *
 * @version $Id$
 * @since $Release$
 */
public interface MutableRole
        extends Role, MutableNamedObject
{
    /**
     * Set the users value. 
     *
     * @param inUsers a <code>Set&lt;User&gt;</code> value
     */
    public void setUsers(Set<User> inUsers);
    /**
     * Set the permissions value.
     *
     * @param inPermissions a <code>Set&lt;Permission&gt;</code> value
     */
    public void setPermissions(Set<Permission> inPermissions);
}
