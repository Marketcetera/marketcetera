package org.marketcetera.server.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetcAuthentication
        implements Authentication, CredentialsContainer
{
    /**
     * Create a new MetcAuthentication instance.
     *
     * @param inUsername
     * @param inPassword
     */
    public MetcAuthentication(String inUsername,
                              char[] inPassword)
    {
        username = inUsername;
        password = inPassword;
    }
    /* (non-Javadoc)
     * @see java.security.Principal#getName()
     */
    @Override
    public String getName()
    {
        return username;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getAuthorities()
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities()
    {
        System.out.println("MetcAuthentication:getAuthorities invoked");
        return new HashSet<GrantedAuthority>();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials()
    {
        return new String(password);
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getDetails()
     */
    @Override
    public Object getDetails()
    {
        System.out.println("MetcAuthentication:getDetails invoked");
        return null;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal()
    {
        System.out.println("MetcAuthentication:getPrincipal invoked");
        return null;
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#isAuthenticated()
     */
    @Override
    public boolean isAuthenticated()
    {
        return authenticated.get();
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.Authentication#setAuthenticated(boolean)
     */
    @Override
    public void setAuthenticated(boolean inAuthenticated)
            throws IllegalArgumentException
    {
        authenticated.set(inAuthenticated);
    }
    /* (non-Javadoc)
     * @see org.springframework.security.core.CredentialsContainer#eraseCredentials()
     */
    @Override
    public void eraseCredentials()
    {
        password = new char[0];
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return String.format("MetcAuthentication [username=%s, authenticated=%s]",
                             username,
                             authenticated);
    }
    /**
     * 
     */
    private final String username;
    /**
     * 
     */
    private volatile char[] password;
    /**
     * 
     */
    private final AtomicBoolean authenticated = new AtomicBoolean(false);
    private static final long serialVersionUID = 1L;
}
