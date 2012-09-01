package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web-services access to the authority service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserService.java 16217 2012-08-27 19:42:33Z colin $
 * @since $Release$
 */
public interface AuthorityService
{
    /**
     * Adds an authority with the given authority.
     *
     * @param inAuthority a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("/authorities/{authority}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addAuthority(@PathParam("authority")String inAuthority);
    /**
     * Gets the authority with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesAuthority</code> value
     */
    @GET
    @Path("/authorities/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesAuthority getAuthority(@PathParam("id")long inId);
    /**
     * Gets all authorities.
     *
     * @return a <code>List&lt;WebServicesAuthority&gt;</code> value
     */
    @GET
    @Path("/authorities")
    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesAuthority> getAuthorities();
    /**
     * Deletes the authority with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("/authorities/{id}")
    public Response deleteAuthority(@PathParam("id")long inId);
}
