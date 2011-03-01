package org.marketcetera.server.security;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.marketcetera.systemmodel.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class MetcUserDetails
        implements UserDetails
{
    /* (non-Javadoc)
     * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities()
    {
        // TODO
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(MetcAuthority.ROLE_USER);
        return authorities;
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
     * Create a new MetcUser instance.
     *
     * @param inUser
     */
    public MetcUserDetails(User inUser)
    {
        user = inUser;
        Validate.notNull(user);
    }
    /**
     * underlying user object
     */
    private final User user;
    private static final long serialVersionUID = 1L;
}