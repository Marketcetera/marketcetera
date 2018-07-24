package com.marketcetera.web;

import java.util.Date;
import java.util.Set;

import org.marketcetera.admin.Permission;

import com.google.common.collect.Sets;
import com.vaadin.server.VaadinSession;

/* $License$ */

/**
 * Indicates the currently validated session user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionUser
{
    /**
     * Get the current user.
     *
     * @return a <code>SessionUser</code> value
     */
    public static SessionUser getCurrentUser()
    {
        return VaadinSession.getCurrent().getAttribute(SessionUser.class);
    }
    /**
     * Create a new SessionUser instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     */
    public SessionUser(String inUsername,
                       String inPassword)
    {
        username = inUsername;
        password = inPassword;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Get the loggedIn value.
     *
     * @return a <code>Date</code> value
     */
    public Date getLoggedIn()
    {
        return loggedIn;
    }
    /**
     * Get the password value.
     *
     * @return a <code>String</code> value
     */
    public String getPassword()
    {
        return password;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return username;
    }
    /**
     * Get the permissions value.
     *
     * @return a <code>Set&lt;Permissions&gt;</code> value
     */
    public Set<Permission> getPermissions()
    {
        return permissions;
    }
    /**
     * holds permissions for this user
     */
    private final Set<Permission> permissions = Sets.newHashSet();
    /**
     * username value
     */
    private final String username;
    /**
     * password value
     */
    private final String password;
    /**
     * indicates when the user was logged in
     */
    private final Date loggedIn = new Date();
}
