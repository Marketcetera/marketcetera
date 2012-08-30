package org.marketcetera.webservices.systemmodel;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/* $License$ */

/**
 * Provides web-services access to the user service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Path("/userservice/")
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
    @Path("/users/{username}/{password}")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addUser(@PathParam("username")String inUsername,
                            @PathParam("password")String inPassword);
    /**
     * Gets the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <cod>User</code> value
     */
    @GET
    @Path("/users/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public WebServicesUser getUser(@PathParam("id")long inId);
    /**
     * Gets all users.
     *
     * @return a <code>List&lt;User&gt;</code> value
     */
    @GET
    @Path("/users")
    @Produces({MediaType.APPLICATION_JSON})
    public List<WebServicesUser> getUsers();
    /**
     * Deletes the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <code>Response</code> value
     */
    @DELETE
    @Path("/users/{id}")
    public Response deleteUser(@PathParam("id")long inId);
}
