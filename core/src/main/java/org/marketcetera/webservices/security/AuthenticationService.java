package org.marketcetera.webservices.security;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    @POST
    @Path("authenticate")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<WebServicesPermission> authenticate(@FormParam("username")String inUsername,
                                                    @FormParam("password") String inPassword);
}
