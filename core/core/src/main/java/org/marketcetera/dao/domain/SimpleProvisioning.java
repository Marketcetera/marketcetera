package org.marketcetera.dao.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.marketcetera.api.systemmodel.Permission;
import org.marketcetera.api.systemmodel.Role;
import org.marketcetera.core.security.AssignToRole;
import org.marketcetera.core.security.MutableProvisioning;
import org.marketcetera.core.security.User;

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
        builder.append("Provisioning Command:");
        boolean separatorNeeded = false;
        if(users != null && !users.isEmpty()) {
            builder.append(" users:[");
            for(User user : users) {
                if(separatorNeeded) {
                    builder.append(',');
                }
                builder.append(user.getName());
                separatorNeeded = true;
            }
            builder.append("]");
        }
        separatorNeeded = false;
        if(permissions != null && !permissions.isEmpty()) {
            builder.append(" permissions:[");
            for(Permission permission : permissions) {
                if(separatorNeeded) {
                    builder.append(',');
                }
                builder.append(permission.getName());
                separatorNeeded = true;
            }
            builder.append("]");
        }
        separatorNeeded = false;
        if(roles != null && !roles.isEmpty()) {
            builder.append(" roles:[");
            for(Role role : roles) {
                if(separatorNeeded) {
                    builder.append(',');
                }
                builder.append(role.getName());
                separatorNeeded = true;
            }
            builder.append("]");
        }
        separatorNeeded = false;
        if(assignments != null && !assignments.isEmpty()) {
            builder.append(" assignments:[");
            for(AssignToRole assignment : assignments) {
                if(separatorNeeded) {
                    builder.append(',');
                }
                builder.append(assignment);
                separatorNeeded = true;
            }
            builder.append("]");
        }
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
