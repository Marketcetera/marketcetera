package org.marketcetera.security.shiro.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.marketcetera.api.security.User;
import org.marketcetera.api.security.UserManagerService;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.dao.impl.PersistentUser;
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
    public Response addUser(PersistentUser inUser)
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService addUser invoked with user {}",
                               inUser);
        Response response;
        try {
            userManagerService.addUser(inUser);
            users.put(inUser.getId(),
                      inUser);
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
    public PersistentUser getUser(long inId)
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService getUser invoked with id {}",
                               inId);
//        return userManagerService.getUserById(inId);
        return users.get(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#updateUser(org.marketcetera.api.security.User)
     */
    @Override
    public Response updateUser(PersistentUser inUser)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService updateUser invoked with user {}",
                               inUser);
        Response response;
        try {
            users.put(inUser.getId(),
                      inUser);
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
    public Response deleteUser(long inId)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService deleteUser invoked with user {}",
                               inId);
        Response response;
        try {
            User user = userManagerService.getUserById(inId);
            userManagerService.deleteUser(user);
            users.remove(inId);
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
    public List<PersistentUser> getUsers()
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService getUsers invoked");
//        return userManagerService.getAllUsers();
        return new ArrayList<PersistentUser>(users.values());
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
    private final Map<Long,PersistentUser> users = new HashMap<Long,PersistentUser>();
}
