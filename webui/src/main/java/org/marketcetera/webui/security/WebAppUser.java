package org.marketcetera.webui.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.collect.Sets;

/* $License$ */

/**
 * Provides a user implementation for the web app.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class WebAppUser
        implements UserDetails
{
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return permissions;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    @Override
    public String getPassword()
    {
        return password;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    @Override
    public String getUsername()
    {
        return username;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    @Override
    public boolean isAccountNonExpired()
    {
        return accountNonExpired;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked()
    {
        return accountNonLocked;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return credentialsNonExpired;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return enabled;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("WebAppUser [username=").append(username).append(", accountNonExpired=")
                .append(accountNonExpired).append(", accountNonLocked=").append(accountNonLocked)
                .append(", credentialsNonExpired=").append(credentialsNonExpired).append(", enabled=").append(enabled)
                .append(", permissions=").append(permissions).append("]");
        return builder.toString();
    }
    /**
     * permissions granted to this user
     */
    private final Collection<? extends GrantedAuthority> permissions = Sets.newHashSet();
    /**
     * user password hashed value
     */
    private String password;
    /**
     * username value
     */
    private String username;
    /**
     * indicates if the account is expired
     */
    private boolean accountNonExpired;
    /**
     * indicates if the account is locked
     */
    private boolean accountNonLocked;
    /**
     * indicates if the credentials are expired
     */
    private boolean credentialsNonExpired;
    /**
     * indicates if the account is enabled
     */
    private boolean enabled;
    private static final long serialVersionUID = -6279001120444578949L;
}
