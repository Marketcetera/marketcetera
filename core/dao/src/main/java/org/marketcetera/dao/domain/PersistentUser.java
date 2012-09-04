package org.marketcetera.dao.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import org.marketcetera.api.dao.Permission;
import org.marketcetera.api.security.GrantedPermission;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Persistent implementation of {@link User}.
 *
 * @version $Id: PersistentUser.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@ThreadSafe
@NamedQueries({ @NamedQuery(name="findUserByUsername",query="select s from PersistentUser s where s.username = :username"),
        @NamedQuery(name="findAllUsers",query="select s from PersistentUser s")})
@NamedNativeQueries( { @NamedNativeQuery(name="findPermissionsByUserId",query="select distinct permissions.id, permissions.permission, permissions.version from permissions as permissions where permissions.id in (select groups_permissions.permissions_id from groups_permissions as groups_permissions where groups_permissions.groups_id in (select groups.id from groups as groups where groups.id in (select groups_id from groups_users as groups_users, users as users where users.id = groups_users.users_id and users.id=?)))",resultClass=PersistentPermission.class)})
@Entity
@Table(name="users", uniqueConstraints = { @UniqueConstraint(columnNames= { "username" } ) } )
@XmlRootElement
public class PersistentUser
        extends PersistentVersionedObject
        implements User
{
    /* (non-Javadoc)
     * @see org.marketcetera.api.systemmodel.NamedObject#getName()
     */
    @Transient
    @Override
    public String getName()
    {
        return username;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.systemmodel.impl.User#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }
    /**
     * Sets the username value.
     *
     * @param inUsername a <code>String</code> value
     */
    @Column(nullable=false,unique=true)
    public void setUsername(String inUsername)
    {
        username = inUsername;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPermissions()
     */
    @Transient
    @Override
    public Collection<? extends GrantedPermission> getPermissions()
    {
        synchronized(permissions) {
            return Collections.unmodifiableSet(permissions);
        }
    }
    /**
     * Sets the permissions for this user.
     *
     * @param inPermissions a <code>Collection&lt;Permission&gt;</code> value
     */
    public void setPermissions(Collection<Permission> inPermissions)
    {
        synchronized(permissions) {
            permissions.clear();
            if(inPermissions == null) {
                return;
            }
            permissions.addAll(inPermissions);
        }
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }
    /**
     * Sets the password value.
     *
     * @param inPassword a <code>String</code> value
     */
    @Column(nullable=true)
    public void setPassword(String inPassword)
    {
        password = inPassword;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    @Override
    public boolean isAccountNonExpired()
    {
        return enabled;
    }

    public void setAccountNonExpired(boolean enabled) {
        this.enabled = enabled;
    }

    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked()
    {
        return !locked;
    }
    /**
     * Sets the locked value.
     *
     * @param accountNonLocked a <code>boolean</code> value
     */
    @Column(nullable=false)
    public void setAccountNonLocked(boolean accountNonLocked)
    {
        locked = !accountNonLocked;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return !credentialsExpired;
    }
    /**
     * Sets the credentialsExpired value.
     *
     * @param credentialsNonExpired a <code>boolean</code> value
     */
    @Column(nullable=false)
    public void setCredentialsNonExpired(boolean credentialsNonExpired)
    {
        credentialsExpired = !credentialsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return enabled;
    }
    /**
     * Sets the enabled value.
     *
     * @param inEnabled a <code>boolean</code> value
     */
    @Column(nullable=false)
    public void setEnabled(boolean inEnabled)
    {
        enabled = inEnabled;
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
        builder.append("User ").append(username).append("[").append(getId()).append("] ").append(" enabled=").append(enabled)
                .append(", locked=").append(locked).append(", credentialsExpired=").append(credentialsExpired)
                .append(", permissions=").append(permissions);
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
    private volatile String username;
    /**
     * password value
     */
    private volatile String password;
    /**
     * indicates if the account is enabled
     */
    private volatile boolean enabled = true;
    /**
     * indicates if the account is locked
     */
    private volatile boolean locked = false;
    /**
     * indicates if the credentials have expired
     */
    private volatile boolean credentialsExpired = false;
    /**
     * permissions for this user
     */
    @GuardedBy("permissions")
    private final Set<Permission> permissions = new HashSet<Permission>();
    private static final long serialVersionUID = 1L;
}
