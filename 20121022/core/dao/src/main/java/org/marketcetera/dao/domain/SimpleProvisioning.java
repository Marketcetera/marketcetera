package org.marketcetera.dao.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.security.AssignToRole;
import org.marketcetera.api.security.MutableProvisioning;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Provides a POJO implementation of a <code>MutableProvisiong</code> value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleProvisioning
        implements MutableProvisioning
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.Provisioning#getUsers()
     */
    @Override
    public Set<User> getUsers()
    {
        return users;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.Provisioning#getRoles()
     */
    @Override
    public Set<Role> getRoles()
    {
        return roles;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.Provisioning#getPermissions()
     */
    @Override
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.Provisioning#getAssignments()
     */
    @Override
    public List<AssignToRole> getAssignments()
    {
        return assignments;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.Provisioning#getMutableView()
     */
    @Override
    public MutableProvisioning getMutableView()
    {
        return this;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableProvisioning#setUsers(java.util.Set)
     */
    @Override
    public void setUsers(Set<User> inUsers)
    {
        users = inUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableProvisioning#setRoles(java.util.Set)
     */
    @Override
    public void setRoles(Set<Role> inRoles)
    {
        roles = inRoles;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableProvisioning#setPermissions(java.util.Set)
     */
    @Override
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.security.MutableProvisioning#setAssignments(java.util.List)
     */
    @Override
    public void setAssignments(List<AssignToRole> inAssignments)
    {
        assignments = inAssignments;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("ProvisioningImpl [users=").append(users).append(", permissions=").append(permissions)
                .append(", roles=").append(roles).append(", assignments=").append(assignments).append("]");
        return builder.toString();
    }
    /**
     * users value
     */
    private Set<User> users = new HashSet<User>();
    /**
     * permissions value
     */
    private Set<Permission> permissions = new HashSet<Permission>();
    /**
     * roles value
     */
    private Set<Role> roles = new HashSet<Role>();
    /**
     * assignments value
     */
    private List<AssignToRole> assignments = new ArrayList<AssignToRole>();
}
