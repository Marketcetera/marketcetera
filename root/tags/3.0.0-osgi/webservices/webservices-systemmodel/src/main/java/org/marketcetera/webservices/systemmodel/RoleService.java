package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web-services access to the role service.
 *
 * @version $Id$
 * @since $Release$
 */
@Path("roles")
public interface RoleService
{
    /**
     * Adds the given <code>Role</code.
     *
     * @param inRole a <code>WebServicesRole</code> value
     * @return a <code>WebServicesRole</code> value
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public WebServicesRole addRoleJSON(WebServicesRole inRole);
    /**
     * Adds the given <code>Role</code.
     *
     * @param inRole a <code>WebServicesRole</code> value
     * @return a <code>WebServicesRole</code> value
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML})
    public WebServicesRole addRoleXML(WebServicesRole inRole);
    /**
     * Gets the <code>Role</code> with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesRole</code> value
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesRole getRoleJSON(@PathParam("id")long inId);
    /**
     * Gets the <code>Role</code> with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesRole</code> value
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML})
    public WebServicesRole getRoleXML(@PathParam("id")long inId);
    /**
     * Gets all <code>Role</code> values.
     *
     * @return a <code>List&lt;WebServicesRole&gt;</code> value
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesRole> getRolesJSON();
    /**
     * Gets all <code>Role</code> values.
     *
     * @return a <code>List&lt;WebServicesRole&gt;</code> value
     */
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public List<WebServicesRole> getRolesXML();
    /**
     * Deletes the <code>Role</code> with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response deleteRole(@PathParam("id")long inId);
    /**
     * Updates the given <code>Role</code>.
     *
     * @param inRole a <code>WebServicesRole</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateRoleJSON(WebServicesRole inRole);
    /**
     * Updates the given <code>Role</code>.
     *
     * @param inRole a <code>WebServicesRole</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML})
    public Response updateRoleXML(WebServicesRole inRole);
}
