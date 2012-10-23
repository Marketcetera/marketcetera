package org.marketcetera.dao.domain;

import java.util.HashSet;
import java.util.Set;

import org.marketcetera.api.security.MutableAssignToRole;

/* $License$ */

/**
 * Provides a simple POJO implementation of a <code>MutableAssignToRole</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleAssignToRole
        implements MutableAssignToRole
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getRole()
     */
    @Override
    public String getRole()
    {
        return roleName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getUsers()
     */
    @Override
    public Set<String> getUsers()
    {
        return usernames;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getPermissions()
     */
    @Override
    public Set<String> getPermissions()
    {
        return permissionNames;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.AssignToRole#getMutableView()
     */
    @Override
    public MutableAssignToRole getMutableView()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableAssignToRole#setRole(java.lang.String)
     */
    @Override
    public void setRole(String inRoleName)
    {
        roleName = inRoleName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableAssignToRole#setUsers(java.util.Set)
     */
    @Override
    public void setUsers(Set<String> inUsernames)
    {
        usernames = inUsernames;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableAssignToRole#setPermissions(java.util.Set)
     */
    @Override
    public void setPermissions(Set<String> inPermissionNames)
    {
        permissionNames = inPermissionNames;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SimpleAssignToRole [roleName=").append(roleName).append(", usernames=").append(usernames)
                .append(", permissionNames=").append(permissionNames).append("]");
        return builder.toString();
    }
    /**
     * role name value
     */
    private String roleName;
    /**
     * user name values
     */
    private Set<String> usernames = new HashSet<String>();
    /**
     * permission name values
     */
    private Set<String> permissionNames = new HashSet<String>();
}
