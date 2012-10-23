package org.marketcetera.api.dao;

import java.util.Set;

import org.marketcetera.api.security.User;
import org.marketcetera.api.systemmodel.NamedObject;

/* $License$ */

/**
 * Represents a grouping of {@link org.marketcetera.api.security.User} and {@link Permission} objects.
 *
 * @version $Id$
 * @since $Release$
 */
public interface Role
        extends NamedObject
{
    /**
     * Gets the users in this group.
     *
     * @return a <code>Set&lt;User&gt;</code> value
     */
    public Set<User> getUsers();
    /**
     * Gets the permissions assigned to this group.
     *
     * @return a <code>Set&lt;Permission&gt;</code> value
     */
    public Set<Permission> getPermissions();
}
