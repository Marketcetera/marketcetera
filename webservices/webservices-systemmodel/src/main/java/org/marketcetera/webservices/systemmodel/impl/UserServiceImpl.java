package org.marketcetera.webservices.systemmodel.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.dao.UserFactory;
import org.marketcetera.api.security.User;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.webservices.systemmodel.UserService;
import org.marketcetera.webservices.systemmodel.WebServicesUser;

/* $License$ */

/**
 * Provides web-services access to the user service.
 *
 * @version $Id$
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
            userDao.add(user);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(UserServiceImpl.class,
                                  e);
            response = Response.serverError().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.security.shiro.UserService#getUser(java.lang.String)
     */
    @Override
    public WebServicesUser getUser(long inId)
    {
        SLF4JLoggerProxy.trace(UserServiceImpl.class, "UserService getUser invoked with id {}", //$NON-NLS-1$
                inId);
        User user = userDao.getById(inId);
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
        SLF4JLoggerProxy.debug(UserServiceImpl.class, "UserService deleteUser invoked with user {}", //$NON-NLS-1$
                inId);
        Response response;
        try {
            User user = userDao.getById(inId);
            userDao.delete(user);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            response = Response.serverError().build();
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
        for(User user : userDao.getAll()) {
            decoratedUsers.add(new WebServicesUser(user));
        }
        return decoratedUsers;
    }
    /**
     * Sets the userFactory value.
     *
     * @param inUserFactory a <code>UserFactory</code> value
     */
    public void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * data access object
     */
    private UserDao userDao;

    /**
     * constructs user objects 
     */
    private UserFactory userFactory;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
}
