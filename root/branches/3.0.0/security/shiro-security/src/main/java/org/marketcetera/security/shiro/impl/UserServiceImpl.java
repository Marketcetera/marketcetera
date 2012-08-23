package org.marketcetera.security.shiro.impl;

import javax.ws.rs.core.Response;

import org.marketcetera.api.security.User;
import org.marketcetera.api.security.UserManagerService;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.security.shiro.Container;
import org.marketcetera.security.shiro.UserService;

/* $License$ */

/**
 * Provides web-services access to the user service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UserServiceImpl
        implements UserService
{
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#addUser(org.marketcetera.api.security.User)
     */
    @Override
    public Response addUser(User inUser)
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService addUser invoked with user {}",
                               inUser);
        Response response;
        try {
            userManagerService.addUser(inUser);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#getUser(java.lang.String)
     */
    @Override
    public User getUser(String inId)
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService getUser invoked with id {}",
                               inId);
        long userId = Long.parseLong(inId);
        return userManagerService.getUserById(userId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#updateUser(org.marketcetera.api.security.User)
     */
    @Override
    public Response updateUser(User inUser)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService updateUser invoked with user {}",
                               inUser);
        Response response;
        try {
            userManagerService.saveUser(inUser);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#deleteUser(java.lang.String)
     */
    @Override
    public Response deleteUser(String inId)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService deleteUser invoked with user {}",
                               inId);
        long userId = Long.parseLong(inId);
        Response response;
        try {
            User user = userManagerService.getUserById(userId);
            userManagerService.deleteUser(user);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#getUsers()
     */
    @Override
    public Container<User> getUsers()
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService getUsers invoked");
        Container<User> response = new Container<User>();
        response.list = userManagerService.getAllUsers();
        return response;
    }
    /**
     * Sets the userManagerService value.
     *
     * @param a <code>UserManagerService</code> value
     */
    public void setUserManagerService(UserManagerService inUserManagerService)
    {
        userManagerService = inUserManagerService;
    }
    /**
     * data access object
     */
    private UserManagerService userManagerService;
}
