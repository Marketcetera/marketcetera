package org.marketcetera.webservices.security.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.marketcetera.api.security.User;
import org.marketcetera.api.security.UserManagerService;
import org.marketcetera.core.systemmodel.UserFactory;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.security.UserService;
import org.marketcetera.webservices.security.WebServicesUser;

/* $License$ */

/**
 * Provides web-services access to the user service.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: UserServiceImpl.java 16218 2012-08-27 23:23:59Z colin $
 * @since $Release$
 */
public class UserServiceImpl
        implements UserService
{
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.security.UserService#addUser(java.lang.String, java.lang.String)
     */
    @Override
    public Response addUser(String inUsername,
                            String inPassword)
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService addUser invoked with user {} and password ********", //$NON-NLS-1$
                               inUsername);
        Response response;
        try {
            User user = userFactory.create(inUsername,
                                           inPassword);
            userManagerService.addUser(user);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(UserServiceImpl.class,
                                  e);
            response = Response.notModified().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#getUser(java.lang.String)
     */
    @Override
    public WebServicesUser getUser(long inId)
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService getUser invoked with id {}", //$NON-NLS-1$
                               inId);
        User user = userManagerService.getUserById(inId);
        if(user == null) {
            return null;
        }
        return new WebServicesUser(user);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#deleteUser(java.lang.String)
     */
    @Override
    public Response deleteUser(long inId)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService deleteUser invoked with user {}", //$NON-NLS-1$
                               inId);
        Response response;
        try {
            User user = userManagerService.getUserById(inId);
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
    public List<WebServicesUser> getUsers()
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class,
                               "UserService getUsers invoked"); //$NON-NLS-1$
        List<WebServicesUser> decoratedUsers = new ArrayList<WebServicesUser>();
        for(User user : userManagerService.getAllUsers()) {
            decoratedUsers.add(new WebServicesUser(user));
        }
        return decoratedUsers;
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
     * Sets the userFactory value.
     *
     * @param a <code>UserFactory</code> value
     */
    public void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * data access object
     */
    private UserManagerService userManagerService;
    /**
     * constructs user objects 
     */
    private UserFactory userFactory;
}
