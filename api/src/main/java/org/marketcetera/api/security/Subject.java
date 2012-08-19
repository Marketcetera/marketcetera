package org.marketcetera.api.security;

/**
 * Provides facilities for a client to manage authentication and authorization of a user or process in a protected system.

 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 4:00 AM
 */

public interface Subject {
    /**
     * Log the Subject in with the presented credentials.
     *
     * @param token Credentials for this login
     */
    void login(AuthenticationToken token);

    /**
     * Log out the Subject, destroying all credentials and sessions
     */
    void logout();

    /**
     * Returns the Subject's existing session, or creating a new one if none exists
     *
     * @return Session for this user
     */
    Session getSession();

    /**
     * Indicate whether this Subject is authenticated or not
     *
     * @return true if this Subject has successfully logged in
     */
    boolean isAuthenticated();

    /**
     * Indicate whether this Subject has the requested role.  Generally not used, since a protected resource that is a client
     * of this function only cares whether a Subject has a static permission grant.  Roles, on the other hand, are specific
     * to organizational and local structure, with permissions mapped to them.
     *
     * @param role String identifying a role
     * @return true if this Subject has the requested role.
     */
    boolean hasRole(String role);

    /**
     * Indicate whether this Subject has the requested permission.  This is called for every protected resource to determine
     * whether the current Subject has access to it.  In contrast to a role which may aggregate permissions in a site-specific
     * way, a permission is constant across all deployments of a protected resource.
     *
     * @param permission
     * @return
     */
    boolean isPermitted(String permission);
}
