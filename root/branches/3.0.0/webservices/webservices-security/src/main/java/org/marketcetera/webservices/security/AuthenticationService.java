package org.marketcetera.webservices.security;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

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
//    @Consumes({MediaType.APPLICATION_JSON})
    public Response authenticate(@FormParam("username")String inUsername,
                                 @FormParam("password")String inPassword);
}
