package org.marketcetera.dao.domain;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.*;

import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.dao.Role;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement(name="objects")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder={ "permissions", "users", "roles", "assignations" })
public class SystemObjectList
{
    public List<Role> getRoles()
    {
        return roles;
    }
    public List<Permission> getPermissions()
    {
        return permissions;
    }
    public List<User> getUsers()
    {
        return users;
    }
    public void addObject(Role inRole)
    {
        addRole(inRole);
    }
    public void addObject(Permission inPermission)
    {
        addPermission(inPermission);
    }
    public void addObject(User inUser)
    {
        addUser(inUser);
    }
    public void addRole(Role inRole)
    {
        roles.add(inRole);
    }
    public void addPermission(Permission inPermission)
    {
        permissions.add(inPermission);
    }
    public void addUser(User inUser)
    {
        users.add(inUser);
    }
    /**
     * Get the assignations value.
     *
     * @return a <code>List<AssignToRole></code> value
     */
    public List<AssignToRole> getAssignToRole()
    {
        return assignations;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SystemObjectList [roles=").append(roles).append(", permissions=").append(permissions)
                .append(", users=").append(users).append("]");
        return builder.toString();
    }
    @XmlElementWrapper(name="roles")
    @XmlElement(name="role",type=PersistentRole.class)
    private final List<Role> roles = new ArrayList<Role>();
    @XmlElementWrapper(name="permissions")
    @XmlElement(name="permission",type=PersistentPermission.class)
    private final List<Permission> permissions = new ArrayList<Permission>();
    @XmlElementWrapper(name="users")
    @XmlElement(name="user",type=PersistentUser.class)
    private final List<User> users = new ArrayList<User>();
    @XmlElementWrapper(name="assignations")
    @XmlElement(name="assignToRole")
    private final List<AssignToRole> assignations = new ArrayList<AssignToRole>();
}
