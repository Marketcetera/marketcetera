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
    @Consumes({MediaType.APPLICATION_JSON})
    public WebServicesPermission addPermissionJSON(WebServicesPermission inPermission);
    /**
     * Adds the given permission.
     *
     * @param inPermission a <code>WebServicesPermission</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML})
    public WebServicesPermission addPermissionXML(WebServicesPermission inPermission);
    /**
     * Gets the permission with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesPermission getPermissionJSON(@PathParam("id")long inId);
    /**
     * Gets the permission with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesPermission</code> value
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML})
    public WebServicesPermission getPermissionXML(@PathParam("id")long inId);
    /**
     * Gets all permissions.
     *
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesPermission> getPermissionsJSON();
    /**
     * Gets all permissions.
     *
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public List<WebServicesPermission> getPermissionsXML();
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
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updatePermissionJSON(WebServicesPermission inPermission);
    /**
     * Updates the given permission.
     *
     * @param inPermission a <code>WebServicesPermission</code> value
     * @return a <code>Response</code> value
     */
    @PUT
    @Consumes({MediaType.APPLICATION_XML})
    public Response updatePermissionXML(WebServicesPermission inPermission);
    /**
     * Gets the permissions assigned to the <code>User</code> with the given id. 
     *
     * @param inId a <code>long</code> value
     * @return a <code>List&lt;WebServicesPermission&gt;</code> value
     */
    @Path("user/{id}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML })
    public List<WebServicesPermission> getAllByUserId(@PathParam("id")long inId);
}
