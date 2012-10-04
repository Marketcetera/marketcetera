package org.marketcetera.webservices.security;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.marketcetera.webservices.security.impl.AuthenticationServiceImpl;
import org.marketcetera.webservices.systemmodel.WebServicesPermission;

/* $License$ */

/**
 * Provides web services authentication.
 *
 * @version $Id$
 * @since $Release$
 */
public interface AuthenticationService
{
    /**
     * Authenticate using the given credentials.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("authenticate")
//    @Produces(MediaType.APPLICATION_JSON)
    public AuthenticationServiceImpl.JaxbList<WebServicesPermission> authenticate(@FormParam("username") String inUsername, @FormParam("password") String inPassword);
}
