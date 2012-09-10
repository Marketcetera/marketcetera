package org.marketcetera.webservices.systemmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.marketcetera.api.dao.MutableRole;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Provides a test <code>Group</code> implementation.
 *
 * @version $Id: MockRole.java 16253 2012-09-04 18:35:21Z topping $
 * @since $Release$
 */
public class MockRole
        extends MockVersionedObject
        implements MutableRole
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getDescription()
     */
    @Override
    public String getDescription()
    {
        return description;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Group#getUsers()
     */
    @Override
    public Collection<User> getUsers()
    {
        return users;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Group#getPermissions()
     */
    @Override
    public Collection<Permission> getPermissions()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.MutableNamedObject#setName(java.lang.String)
     */
    @Override
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.MutableNamedObject#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableRole#setUsers(java.util.Set)
     */
    @Override
    public void setUsers(Set<User> inUsers)
    {
        users.clear();
        if(inUsers == null) {
            return;
        }
        users.addAll(inUsers);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableRole#setPermissions(java.util.Set)
     */
    @Override
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions.clear();
        if(inPermissions == null) {
            return;
        }
        permissions.addAll(inPermissions);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("MockRole [name=").append(name).append(", users=").append(users).append(", permissions=")
                .append(permissions).append("]");
        return builder.toString();
    }
    /**
     * name value
     */
    private String name;
    /**
     * description value
     */
    private String description;
    /**
     * users value
     */
    private final Collection<User> users = new ArrayList<User>();
    /**
     * permissions value
     */
    private final Collection<Permission> permissions = new ArrayList<Permission>();
}
