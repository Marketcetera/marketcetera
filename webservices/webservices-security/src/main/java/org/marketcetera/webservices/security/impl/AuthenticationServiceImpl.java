package org.marketcetera.webservices.security.impl;

import javax.ws.rs.core.Response;

import org.marketcetera.api.security.Subject;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.security.AuthenticationService;

/* $License$ */

/**
 * Provides web services access to the authentication service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AuthenticationServiceImpl
        implements AuthenticationService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.security.AuthenticationService#authenticate(java.lang.String, java.lang.String)
     */
    @Override
    public Response authenticate(String inUsername,
                                 String inPassword)
    {
        SLF4JLoggerProxy.trace(AuthenticationServiceImpl.class,
                               "AuthenticationService authenticate invoked with {}/********", //$NON-NLS-1$
                               inUsername);
        Response response;
        try {
            UsernamePasswordToken token = new UsernamePasswordToken(inUsername,
                                                                    inPassword);
            authenticationManagerService.login(token);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(AuthenticationServiceImpl.class,
                                  e);
            response = Response.notModified().build();
        }
        return response;
    }
    /**
     * Sets the authenticationManagerService value.
     *
     * @param a <code>Subject</code> value
     */
    public void setAuthenticationManagerService(Subject inAuthenticationManagerService)
    {
        authenticationManagerService = inAuthenticationManagerService;
    }
    /**
     * authentication manager subject
     */
    private Subject authenticationManagerService;
}
