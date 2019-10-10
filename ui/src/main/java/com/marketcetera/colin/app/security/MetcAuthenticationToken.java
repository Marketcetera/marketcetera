package com.marketcetera.colin.app.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MetcAuthenticationToken
        extends UsernamePasswordAuthenticationToken
{
    /**
     * Create a new MetcAuthenticationToken instance.
     *
     * @param inPrincipal
     * @param inCredentials
     */
    MetcAuthenticationToken(Object inPrincipal,
                            Object inCredentials)
    {
        super(inPrincipal,
              inCredentials);
    }
    /**
     * Create a new MetcAuthenticationToken instance.
     *
     * @param inPrincipal
     * @param inCredentials
     * @param inAuthorities
     */
    MetcAuthenticationToken(Object inPrincipal,
                            Object inCredentials,
                            Collection<? extends GrantedAuthority> inAuthorities)
    {
        super(inPrincipal,
              inCredentials,
              inAuthorities);
    }
    /* (non-Javadoc)
     * @see org.springframework.security.authentication.UsernamePasswordAuthenticationToken#eraseCredentials()
     */
    @Override
    public void eraseCredentials()
    {
        // intentionally do nothing
    }
    private static final long serialVersionUID = -3755015331275189728L;
}
