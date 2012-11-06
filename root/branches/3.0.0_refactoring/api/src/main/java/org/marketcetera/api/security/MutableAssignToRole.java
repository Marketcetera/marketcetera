package org.marketcetera.api.security;

import java.util.Set;

/* $License$ */

/**
 * Provides a mutable view of an <code>AssignToRole</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MutableAssignToRole
        extends AssignToRole
{
    /**
     * Sets the role to which to assign users and permissions.
     *
     * @param inRoleName a <code>String</code> value
     */
    public void setRole(String inRoleName);
    /**
     * Sets the user names to assign to the role.
     *
     * @param inUsers a <code>Set&lt;String&gt;</code> value
     */
    public void setUsers(Set<String> inUsers);
    /**
     * Sets the permission names to assign to the role.
     *
     * @param inPermissions a <code>Set&lt;String&gt;</code> value
     */
    public void setPermissions(Set<String> inPermissions);
}
