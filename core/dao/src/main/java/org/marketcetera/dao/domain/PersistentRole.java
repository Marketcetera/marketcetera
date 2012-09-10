package org.marketcetera.dao.domain;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.marketcetera.api.dao.MutableRole;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Persistent implementation of {@link org.marketcetera.api.dao.Role}.
 *
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@Entity
@NamedQueries( { @NamedQuery(name="PersistentRole.findByName",query="select s from PersistentRole s where s.name = :name"),
                 @NamedQuery(name="PersistentRole.findAll",query="select s from PersistentRole s")})
@Table(name="roles", uniqueConstraints = { @UniqueConstraint(columnNames= { "name" } ) } )
@Access(AccessType.FIELD)
@XmlRootElement(name = "role")
@XmlAccessorType(XmlAccessType.NONE)
public class PersistentRole
        extends PersistentVersionedObject
        implements MutableRole
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.Role#getName()
     */
    @Override
    @XmlAttribute
    public String getName()
    {
        return name;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getDescription()
     */
    @Override
    @XmlAttribute
    public String getDescription()
    {
        return description;
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
    @XmlElementWrapper(name="users")
    @XmlElement(name="user",type=PersistentUser.class)
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
    @XmlElementWrapper(name="permissions")
    @XmlElement(name="permission",type=PersistentPermission.class)
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
     * @see org.marketcetera.api.systemmodel.MutableNamedObject#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Role [name=").append(name).append(", users=").append(users).append(", permissions=")
                .append(permissions).append("]");
        return builder.toString();
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
     * permissions granted to this role
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
    private String name;
    @Column(nullable=true)
    private String description;
}
