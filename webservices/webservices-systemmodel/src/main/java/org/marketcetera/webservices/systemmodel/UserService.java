package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web-services access to the user service.
 *
 * @version $Id$
 * @since $Release$
 */
@Path("users")
public interface UserService
{
    /**
     * Adds the given user.
     *
     * @param inUser a <code>WebServicesUser</code> value
     * @return a <code>WebServicesUser</code> value
     */
    @POST
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public WebServicesUser add(WebServicesUser inUser);
    /**
     * Gets the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesUser</code> value
     */
    @GET
    @Path("{id}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public WebServicesUser get(@PathParam("id")long inId);
    /**
     * Gets all users.
     *
     * @return a <code>List&lt;WebServicesUser&gt;</code> value
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public List<WebServicesUser> getAll();
    /**
     * Deletes the user with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id")long inId);
    /**
     * Updates the given user.
     *
     * @param inUser a <code>WebServicesUser</code> value
     * @return a <code>Response</code> value
     */
    @PUT
    @Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Response update(WebServicesUser inUser);
}
