package org.marketcetera.api.systemmodel;

import java.util.Set;

import org.marketcetera.api.security.User;

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
