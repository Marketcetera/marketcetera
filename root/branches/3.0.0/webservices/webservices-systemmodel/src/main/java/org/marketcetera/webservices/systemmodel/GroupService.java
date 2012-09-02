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
 * Provides web-services access to the group service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Path("groups")
public interface GroupService
{
    /**
     * Adds a group with the given name.
     *
     * @param inGroup a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("{group}")
//    @Consumes({MediaType.APPLICATION_JSON})
    public Response addGroup(@PathParam("group")String inGroup);
    /**
     * Gets the group with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesGroup</code> value
     */
    @GET
    @Path("{id}")
//    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesGroup getGroup(@PathParam("id")long inId);
    /**
     * Gets all groups.
     *
     * @return a <code>List&lt;WebServicesGroup&gt;</code> value
     */
    @GET
//    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesGroup> getGroups();
    /**
     * Deletes the group with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response deleteGroup(@PathParam("id")long inId);
}
