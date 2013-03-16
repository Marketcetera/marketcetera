package org.marketcetera.webservices.systemmodel;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.*;

import org.marketcetera.api.systemmodel.MutableRole;
import org.marketcetera.api.systemmodel.Permission;
import org.marketcetera.api.systemmodel.Role;
import org.marketcetera.core.security.User;

/* $License$ */

/**
 * Provides a web-services appropriate <code>Role</code> implementation.
 *
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="role")
@XmlAccessorType(XmlAccessType.NONE)
public class WebServicesRole
        extends WebServicesNamedObject
        implements MutableRole
{
    /**
     * Create a new WebServicesRole instance.
     */
    public WebServicesRole() {}
    /**
     * Create a new WebServicesRole instance.
     *
     * @param inRole a <code>Role</code> value
     */
    public WebServicesRole(Role inRole)
    {
        copyAttributes(inRole);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServicesRole [users=").append(users).append(", permissions=").append(permissions)
                .append("]");
        return builder.toString();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableRole#setUsers(java.util.Set)
     */
    @Override
    public void setUsers(Set<User> inUsers)
    {
        users = inUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableRole#setPermissions(java.util.Set)
     */
    @Override
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Role#getUsers()
     */
    @Override
    public Set<User> getUsers()
    {
        return users;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Role#getPermissions()
     */
    @Override
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /**
     * Copies the attributes from the given <code>Role</code> object to this object.
     *
     * @param inRole a <code>Role</code> value
     */
    private void copyAttributes(Role inRole)
    {
        if(inRole == null) {
            return;
        }
        super.copyAttributes(inRole);
        // perform deep copy on both collections
        users = new HashSet<User>();
        if(inRole.getUsers() != null) {
            for(User user : inRole.getUsers()) {
                if(user instanceof WebServicesUser) {
                    users.add(user);
                } else {
                    users.add(new WebServicesUser(user));
                }
            }
        }
        permissions = new HashSet<Permission>();
        if(inRole.getPermissions() != null) {
            for(Permission permission : inRole.getPermissions()) {
                if(permission instanceof WebServicesPermission) {
                    permissions.add(permission);
                } else {
                    permissions.add(new WebServicesPermission(permission));
                }
            }
        }
    }
    /**
     * users value
     */
    @XmlElementWrapper(name="users")
    @XmlElement(name="user",type=WebServicesUser.class)
    private Set<User> users = new HashSet<User>();
    /**
     * permissions value
     */
    @XmlElementWrapper(name="permissions")
    @XmlElement(name="permission",type=WebServicesPermission.class)
    private Set<Permission> permissions = new HashSet<Permission>();
    private static final long serialVersionUID = 1L;
}
