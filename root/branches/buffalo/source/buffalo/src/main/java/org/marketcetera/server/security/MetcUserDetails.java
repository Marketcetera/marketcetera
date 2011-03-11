package org.marketcetera.server.security;

import java.util.Collection;
import java.util.HashSet;

import org.marketcetera.systemmodel.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetcUserDetails
        implements UserDetails
{
    /**
     * Create a new MetcUserDetails instance.
     *
     * @param inUser
     */
    public MetcUserDetails(User inUser)
    {
        user = inUser;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities()
    {
        return new HashSet<GrantedAuthority>();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    @Override
    public String getPassword()
    {
        return user.getHashedPassword();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    @Override
    public String getUsername()
    {
        return user.getName();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
     */
    @Override
    public boolean isAccountNonExpired()
    {
        return user.getActive();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
     */
    @Override
    public boolean isAccountNonLocked()
    {
        return user.getActive();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
     */
    @Override
    public boolean isCredentialsNonExpired()
    {
        return user.getActive();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    @Override
    public boolean isEnabled()
    {
        return user.getActive();
    }
    /**
     * 
     *
     *
     * @return
     */
    public long getId()
    {
        return user.getId();
    }
    /**
     * 
     */
    private final User user;
    private static final long serialVersionUID = 1L;
}
