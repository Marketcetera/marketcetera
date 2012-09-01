package org.marketcetera.dao.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import org.marketcetera.api.dao.Authority;
import org.marketcetera.api.security.GrantedAuthority;
import org.marketcetera.api.security.User;

/* $License$ */

/**
 * Persistent implementation of {@link User}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: PersistentUser.java 82353 2012-05-10 21:56:11Z colin $
 * @since $Release$
 */
@ThreadSafe
@NamedQueries({ @NamedQuery(name="findUserByUsername",query="from PersistentUser s where s.username = :username"),
                @NamedQuery(name="findAllUsers",query="from PersistentUser")})
@NamedNativeQueries( { @NamedNativeQuery(name="findAuthoritiesByUserId",query="select distinct authorities.id, authorities.authority, authorities.version from authorities as authorities where authorities.id in (select groups_authorities.authorities_id from groups_authorities as groups_authorities where groups_authorities.groups_id in (select groups.id from groups as groups where groups.id in (select groups_id from groups_users as groups_users, users as users where users.id = groups_users.users_id and users.id=?)))",resultClass=PersistentAuthority.class)})
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
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Transient
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        synchronized(authorities) {
            return Collections.unmodifiableSet(authorities);
        }
    }
    /**
     * Sets the authorities for this user.
     *
     * @param inAuthorities a <code>Collection&lt;Authority&gt;</code> value
     */
    public void setAuthorities(Collection<Authority> inAuthorities)
    {
        synchronized(authorities) {
            authorities.clear();
            if(inAuthorities == null) {
                return;
            }
            authorities.addAll(inAuthorities);
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
                .append(", authorities=").append(authorities);
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
     * authorities for this user
     */
    @GuardedBy("authorities")
    private final Set<Authority> authorities = new HashSet<Authority>();
    private static final long serialVersionUID = 1L;
}
