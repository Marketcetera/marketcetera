package org.marketcetera.dao.domain;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.*;

import org.marketcetera.api.dao.Role;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Persistent implementation of {@link org.marketcetera.api.dao.Role}.
 *
 * @version $Id: PersistentRole.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@ThreadSafe
@Entity
@NamedQueries( { @NamedQuery(name="findRoleByName",query="select s from PersistentRole s where s.name = :name"),
                 @NamedQuery(name="findAllRoles",query="select s from PersistentRole s")})
@Table(name="roles", uniqueConstraints = { @UniqueConstraint(columnNames= { "name" } ) } )
@Access(AccessType.FIELD)
public class PersistentRole
        extends PersistentVersionedObject
        implements Role
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Role#getName()
     */
    @Override
    public String getName()
    {
        return name;
    }
    /**
     * Sets the name value.
     *
     * @param inName a <code>String</code> value
     */
    public void setName(String inName)
    {
        name = inName;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Role#getUsers()
     */
    @Override
    public Set<User> getUsers()
    {
        return users;
    }
    /**
     * Sets the users value.
     *
     * @param inUsers a <code>Set&lt;User&gt;</code> value
     */
    public void setUsers(Set<User> inUsers)
    {
        users = inUsers;
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
     * Sets the permissions value.
     *
     * @param inPermissions a <code>Set&lt;Permission&gt;</code> value
     */
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (getId() ^ (getId() >>> 32));
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PersistentRole)) {
            return false;
        }
        PersistentRole other = (PersistentRole) obj;
        if (getId() != other.getId()) {
            return false;
        }
        return true;
    }
    /**
     * permissions granted to this group
     */
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentPermission.class)
    private Set<Permission> permissions = new HashSet<Permission>();
    /**
     * users in this group
     */
    @ManyToMany(fetch=FetchType.EAGER,targetEntity=PersistentUser.class)
    private Set<User> users = new HashSet<User>();
    /**
     * name value
     */
    @Column(nullable=false,unique=true)
    private volatile String name;
}
