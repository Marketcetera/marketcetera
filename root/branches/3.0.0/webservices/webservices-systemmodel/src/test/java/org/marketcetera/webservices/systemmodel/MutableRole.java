package org.marketcetera.webservices.systemmodel;

import java.util.Collection;

import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.User;
import org.marketcetera.api.systemmodel.MutableNamedObject;

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
     * @param inUsers a <code>Collection&lt;User&gt;</code> value
     */
    public void setUsers(Collection<User> inUsers);
    /**
     * Set the permissions value.
     *
     * @param inPermissions a <code>Collection&lt;Permission&gt;</code> value
     */
    public void setPermissions(Collection<Permission> inPermissions);
}
