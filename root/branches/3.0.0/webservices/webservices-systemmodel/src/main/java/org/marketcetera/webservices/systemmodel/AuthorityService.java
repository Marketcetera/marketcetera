package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web-services access to the authority service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserService.java 16217 2012-08-27 19:42:33Z colin $
 * @since $Release$
 */
@Path("authorities")
public interface AuthorityService
{
    /**
     * Adds an authority with the given authority.
     *
     * @param inAuthority a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("{authority}")
//    @Consumes({MediaType.APPLICATION_JSON})
    public Response addAuthority(@PathParam("authority")String inAuthority);
    /**
     * Gets the authority with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesAuthority</code> value
     */
    @GET
    @Path("{id}")
//    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesAuthority getAuthority(@PathParam("id")long inId);
    /**
     * Gets all authorities.
     *
     * @return a <code>List&lt;WebServicesAuthority&gt;</code> value
     */
    @GET
//    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesAuthority> getAuthorities();
    /**
     * Deletes the authority with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response deleteAuthority(@PathParam("id")long inId);
}
