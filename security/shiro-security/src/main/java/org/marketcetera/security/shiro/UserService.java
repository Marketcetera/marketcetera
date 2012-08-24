package org.marketcetera.security.shiro;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.marketcetera.dao.impl.PersistentUser;

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
     * Adds the given user.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>Response</code> value
     */
    @POST
    @Path("/users")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response addUser(PersistentUser inUser);
    /**
     * Gets the user with the given id.
     *
     * @param inId a <code>String</code> value
     * @return a <cod>User</code> value
     */
    @GET
    @Path("/users/{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public PersistentUser getUser(@PathParam("id")long inId);
    /**
     * Gets all users.
     *
     * @return a <code>List&lt;User&gt;</code> value
     */
    @GET
    @Path("/users")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<PersistentUser> getUsers();
    /**
     * Updates the given user.
     *
     * @param inUser a <code>User</code> value
     * @return a <code>Response</code> value
     */
    @PUT
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("/users")
    public Response updateUser(PersistentUser inUser);
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
