package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.ext.search.SearchContext;

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
    @Consumes({MediaType.APPLICATION_JSON})
    public WebServicesUser addUserJSON(WebServicesUser inUser);
    /**
     * Adds the given user.
     *
     * @param inUser a <code>WebServicesUser</code> value
     * @return a <code>WebServicesUser</code> value
     */
    @POST
    @Consumes({MediaType.APPLICATION_XML})
    public WebServicesUser addUserXML(WebServicesUser inUser);
    /**
     * Gets the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesUser</code> value
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesUser getUserJSON(@PathParam("id")long inId);
    /**
     * Gets the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>WebServicesUser</code> value
     */
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML})
    public WebServicesUser getUserXML(@PathParam("id")long inId);
    /**
     * Gets all users.
     *
     * @return a <code>List&lt;WebServicesUser&gt;</code> value
     * @param context
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesUser> getUsersJSON(@Context SearchContext context);
    /**
     * Gets all users.
     *
     * @return a <code>List&lt;WebServicesUser&gt;</code> value
     * @param context
     */
    @GET
    @Produces({MediaType.APPLICATION_XML})
    public List<WebServicesUser> getUsersXML(@Context SearchContext context);
    /**
     * Deletes the user with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id")long inId);
    /**
     * Updates the given user.
     *
     * @param inUser a <code>WebServicesUser</code> value
     * @return a <code>Response</code> value
     */
    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    public Response updateUserJSON(WebServicesUser inUser);
    /**
     * Updates the given user.
     *
     * @param inUser a <code>WebServicesUser</code> value
     * @return a <code>Response</code> value
     */
    @PUT
    @Consumes({MediaType.APPLICATION_XML})
    public Response updateUserXML(WebServicesUser inUser);
}
