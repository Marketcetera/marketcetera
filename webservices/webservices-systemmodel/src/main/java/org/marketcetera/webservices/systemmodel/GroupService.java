package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web-services access to the group service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Path("/v1")
public interface GroupService
{
    /**
     * Adds a group with the given name.
     *
     * @param inGroup a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("/groups/{group}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addGroup(@PathParam("group")String inGroup);
    /**
     * Gets the group with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesGroup</code> value
     */
    @GET
    @Path("/groups/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesGroup getGroup(@PathParam("id")long inId);
    /**
     * Gets all groups.
     *
     * @return a <code>List&lt;WebServicesGroup&gt;</code> value
     */
    @GET
    @Path("/groups")
    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesGroup> getGroups();
    /**
     * Deletes the group with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("/groups/{id}")
    public Response deleteGroup(@PathParam("id")long inId);
}
