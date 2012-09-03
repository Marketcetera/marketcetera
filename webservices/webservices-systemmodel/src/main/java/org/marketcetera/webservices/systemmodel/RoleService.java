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
public interface RoleService
{
    /**
     * Adds a group with the given name.
     *
     * @param inRole a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("{group}")
//    @Consumes({MediaType.APPLICATION_JSON})
    public Response addRole(@PathParam("group")String inRole);
    /**
     * Gets the group with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesRole</code> value
     */
    @GET
    @Path("{id}")
//    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesRole getRole(@PathParam("id")long inId);
    /**
     * Gets all groups.
     *
     * @return a <code>List&lt;WebServicesRole&gt;</code> value
     */
    @GET
//    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesRole> getRoles();
    /**
     * Deletes the group with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response deleteRole(@PathParam("id")long inId);
}
