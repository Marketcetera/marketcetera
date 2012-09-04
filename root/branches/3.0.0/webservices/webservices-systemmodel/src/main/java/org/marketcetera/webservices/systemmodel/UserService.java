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
 * Provides web-services access to the user service.
 *
 * @version $Id$
 * @since $Release$
 */
@Path("users")
public interface UserService
{
    /**
     * Adds a user with the given username and password.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("{username}/{password}")
//    @Consumes({MediaType.APPLICATION_JSON})
    public Response addUser(@PathParam("username")String inUsername,
                            @PathParam("password")String inPassword);
    /**
     * Gets the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <cod>User</code> value
     */
    @GET
    @Path("{id}")
//    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesUser getUser(@PathParam("id")long inId);
    /**
     * Gets all users.
     *
     * @return a <code>List&lt;User&gt;</code> value
     */
    @GET
//    @Path("users")
//    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesUser> getUsers();
    /**
     * Deletes the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id")long inId);
}
