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
 * Provides web-services access to the permission service.
 *
 * @version $Id: UserService.java 16217 2012-08-27 19:42:33Z colin $
 * @since $Release$
 */
@Path("permissions")
public interface PermissionService
{
    /**
     * Adds an permission with the given permission.
     *
     * @param inPermission a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("{permission}")
//    @Consumes({MediaType.APPLICATION_JSON})
    public Response addPermission(@PathParam("permission")String inPermission);
    /**
     * Gets the permission with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    @GET
    @Path("{id}")
//    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesPermission getPermission(@PathParam("id")long inId);
    /**
     * Gets all permissions.
     *
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    @GET
//    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesPermission> getPermissions();
    /**
     * Deletes the permission with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response deletePermission(@PathParam("id")long inId);
}
