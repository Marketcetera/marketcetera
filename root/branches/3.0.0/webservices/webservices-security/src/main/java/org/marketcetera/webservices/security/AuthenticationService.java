package org.marketcetera.webservices.security;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web services authentication.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Path("/authenticationservice/")
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
    @Path("/authenticate/{username}/{password}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response authenticate(@PathParam("username")String inUsername,
                                 @PathParam("password")String inPassword);
}
