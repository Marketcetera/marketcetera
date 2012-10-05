package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web-services access to the permission service.
 *
 * @version $Id$
 * @since $Release$
 */
@Path("permissions")
public interface PermissionService
{
    /**
     * Adds the given permission.
     *
     * @param inPermission a <code>WebServicesPermission</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public WebServicesPermission addPermission(WebServicesPermission inPermission);
    /**
     * Gets the permission with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public WebServicesPermission getPermission(@PathParam("id")long inId);
    /**
     * Gets all permissions.
     *
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
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
    /**
     * Updates the given permission.
     *
     * @param inPermission a <code>WebServicesPermission</code> value
     * @return a <code>Response</code> value
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON })
    public Response updatePermission(WebServicesPermission inPermission);
}
