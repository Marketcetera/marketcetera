package org.marketcetera.webservices.security;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;

/* $License$ */

/**
 * Provides web services authentication.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */

// @todo BRAIN DAMAGE!!
// This CORS annotation is strictly for debugging.  It shouldn't hurt anything because it's restricted to localhost,
// but should not be in production code and will be removed shortly.
@CrossOriginResourceSharing(
        allowOrigins = {
                "http://localhost"
        },
        allowCredentials = true
)
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
