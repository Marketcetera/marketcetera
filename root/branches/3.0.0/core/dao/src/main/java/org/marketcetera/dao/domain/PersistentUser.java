package org.marketcetera.dao.domain;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.*;

import org.marketcetera.api.dao.MutableUser;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Persistent implementation of {@link User}.
 *
 * @version $Id$
 * @since $Release$
 */
@NotThreadSafe
@NamedQueries({ @NamedQuery(name="PersistentUser.findByName",query="select s from PersistentUser s where s.username = :name"),
                @NamedQuery(name="PersistentUser.findAll",query="select s from PersistentUser s")})
@NamedNativeQueries( { @NamedNativeQuery(name="findPermissionsByUserId",query="select distinct permissions.id, permissions.name, permissions.description, permissions.version from permissions as permissions where permissions.id in (select roles_permissions.permissions_id from roles_permissions as roles_permissions where roles_permissions.persistentrole_id in (select roles.id from roles as roles where roles.id in (select persistentrole_id from roles_users as roles_users, users as users where users.id = roles_users.users_id and users.id=?)))",resultClass=PersistentPermission.class)})
@Entity
@Table(name="users", uniqueConstraints = { @UniqueConstraint(columnNames= { "username" } ) } )
@XmlRootElement(name = "user")
@XmlAccessorType(XmlAccessType.NONE)
@Access(AccessType.FIELD)
public class PersistentUser
        extends PersistentVersionedObject
        implements MutableUser
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Override
    public String getName()
    {
        return getUsername();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.impl.User#getUsername()
     */
    @Override
    @XmlAttribute
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    public void setUsername(String inUsername)
    {
        username = inUsername;
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
    public void setDescription(String inDescription)
    {
        description = inDescription;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPermissions()
     */
    @Override
    @XmlElementWrapper(name="permissions")
    @XmlElement(name="permission",type=PersistentPermission.class)
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /**
     * Sets the permissions for this user.
     *
     * @param inPermissions a <code>Set&lt;Permission&gt;</code> value
     */
    public void setPermissions(Set<Permission> inPermissions)
    {
        permissions = inPermissions;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    @Override
    @XmlAttribute
    public String getPassword()
    {
        return password;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    @Override
    @XmlAttribute
    public boolean isAccountNonExpired()
    {
        return enabled;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsAccountNonExpired(boolean)
     */
    @Override
    public void setIsAccountNonExpired(boolean inIsNonExpired)
    {
        enabled = inIsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsAccountNonLocked(boolean)
     */
    @Override
    public void setIsAccountNonLocked(boolean inIsNonLocked)
    {
        locked = !inIsNonLocked;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsCredentialsNonExpired(boolean)
     */
    @Override
    public void setIsCredentialsNonExpired(boolean inIsNonExpired)
    {
        credentialsExpired = !inIsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.api.dao.MutableUser#setIsEnabled(boolean)
     */
    @Override
    public void setIsEnabled(boolean inIsEnabled)
    {
        enabled = inIsEnabled;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    @Override
    @XmlAttribute
    public boolean isAccountNonLocked()
    {
        return !locked;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Override
    @XmlAttribute
    public boolean isCredentialsNonExpired()
    {
        return !credentialsExpired;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    @Override
    @XmlAttribute
    public boolean isEnabled()
    {
        return enabled;
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
        if (!(obj instanceof PersistentUser)) {
            return false;
        }
        PersistentUser other = (PersistentUser) obj;
        if (getId() != other.getId()) {
            return false;
        }
        return true;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("User ").append(getUsername()).append(" [").append(getId()).append("]");
        return builder.toString();
    }
    /**
     * Create a new PersistentUser instance.
     */
    protected PersistentUser()
    {
    }
    /**
     * Create a new PersistentUser instance.
     *
     * @param inName a <code>String</code> value
     * @param inPassword a <code>String</code> value
     */
    protected PersistentUser(String inName,
                             String inPassword)
    {
        username = inName;
        password = inPassword;
    }
    /**
     * username value
     */
    @Column(nullable=false,unique=true)
    private String username;
    @Column(nullable=true)
    private String description;
    /**
     * password value
     */
    @Column(nullable=true)
    private String password;
    /**
     * indicates if the account is enabled
     */
    @Column(nullable=false)
    private boolean enabled = true;
    /**
     * indicates if the account is locked
     */
    @Column(nullable=false)
    private boolean locked = false;
    /**
     * indicates if the credentials have expired
     */
    @Column(nullable=false)
    private boolean credentialsExpired = false;
    /**
     * permissions for this user
     */
    private Set<Permission> permissions = new HashSet<Permission>();
    private static final long serialVersionUID = 1L;
}
