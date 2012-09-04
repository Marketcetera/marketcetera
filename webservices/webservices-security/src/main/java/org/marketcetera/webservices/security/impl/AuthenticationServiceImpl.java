package org.marketcetera.webservices.security.impl;

import javax.ws.rs.core.Response;
import org.marketcetera.api.security.SecurityService;
import org.marketcetera.api.security.UsernamePasswordToken;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.security.AuthenticationService;

/* $License$ */

/**
 * Provides web services access to the authentication service.
 *
 * @version $Id$
 * @since $Release$
 */
public class AuthenticationServiceImpl implements AuthenticationService {
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.security.AuthenticationService#authenticate(java.lang.String, java.lang.String)
     */
    @Override
    public Response authenticate(String username, String password) {
        SLF4JLoggerProxy.trace(AuthenticationServiceImpl.class, "AuthenticationService authenticate invoked with {}/********", //$NON-NLS-1$
                username);
        Response response;
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            securityService.getSubject().login(token);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(AuthenticationServiceImpl.class, e);
            response = Response.status(Response.Status.UNAUTHORIZED).build();
        }
        return response;
    }

    /**
     * Sets the authenticationManagerService value.
     *
     * @param securityService <code>Subject</code> value
     */
    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    /**
     * authentication manager subject
     */
    private SecurityService securityService;
}
