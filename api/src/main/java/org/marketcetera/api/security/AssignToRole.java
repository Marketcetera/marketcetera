package org.marketcetera.api.security;

import java.util.Set;

/* $License$ */

/**
 * Indicates a provisioning instruction.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface AssignToRole
{
    /**
     * Get the role name to which to assign the users and permissions.
     *
     * @return a <code>String</code> value
     */
    public String getRole();
    /**
     * Gets the user names to assign to the role.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getUsers();
    /**
     * Gets the permission names to assign to the role.
     *
     * @return a <code>Set&lt;String&gt;</code> value
     */
    public Set<String> getPermissions();
    /**
     * Gets a mutable view of this object.
     *
     * @return a <code>MutableAssignToRole</code> value
     */
    public MutableAssignToRole getMutableView();
}
