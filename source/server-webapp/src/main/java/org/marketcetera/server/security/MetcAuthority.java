package org.marketcetera.server.security;

import org.springframework.security.core.GrantedAuthority;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum MetcAuthority
        implements GrantedAuthority
{
    /**
     * the right to make changes to the users themselves
     */
    ROLE_ADMIN("ROLE_ADMIN"),
    /**
     * the right to execute modules
     */
    ROLE_STRATEGY("ROLE_STRATEGY"),
    /**
     * the right to issue trades
     */
    ROLE_TRADER("ROLE_TRADER"),
    /**
     * the right to view market data, order status, and position information
     */
    ROLE_USER("ROLE_USER");
    /* (non-Javadoc)
     * @see org.springframework.security.core.GrantedAuthority#getAuthority()
     */
    @Override
    public String getAuthority()
    {
        return name;
    }
    /**
     * Create a new MetcAuthority instance.
     *
     * @param inName
     */
    private MetcAuthority(String inName)
    {
        name = inName;
    }
    /**
     * the name of the authority
     */
    private final String name;
}
