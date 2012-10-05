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
     * @see org.marketcetera.webservices.systemmodel.UserService#addUser(org.marketcetera.webservices.systemmodel.WebServicesUser)
     */
    @Override
    public WebServicesUser addUser(WebServicesUser inUser)
    {
        try {
            User persistableUser = userFactory.create(inUser);
            userDao.add(persistableUser);
            return new WebServicesUser(persistableUser);
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.warn(UserServiceImpl.class,
                                  e);
            throw e;
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#getUser(long)
     */
    @Override
    public WebServicesUser getUser(long inId)
    {
        User user = userDao.getById(inId);
        if(user == null) {
            return null;
        }
        return new WebServicesUser(user);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#getUsers()
     */
    @Override
    public List<WebServicesUser> getUsers()
    {
        List<WebServicesUser> decoratedUsers = new ArrayList<WebServicesUser>();
        for(User user : userDao.getAll()) {
            decoratedUsers.add(new WebServicesUser(user));
        }
        return decoratedUsers;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#updateUser(org.marketcetera.webservices.systemmodel.WebServicesUser)
     */
    @Override
    public Response updateUser(WebServicesUser inUser)
    {
        Response response;
        try {
            User persistableUser = userFactory.create(inUser);
            userDao.save(persistableUser);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError().build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#deleteUser(long)
     */
    @Override
    public Response deleteUser(long inId)
    {
        Response response;
        try {
            User user = userDao.getById(inId);
            userDao.delete(user);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError().build();
        }
        return response;
    }
    /**
     * Sets the userFactory value.
     *
     * @param inUserFactory an <code>UserFactory</code> value
     */
    public void setUserFactory(UserFactory inUserFactory)
    {
        userFactory = inUserFactory;
    }
    /**
     * Sets the user DAO value.
     *
     * @param userDao a <code>UserDao</code> value
     */
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    /**
     * allows datastore access for user objects
     */
    private UserDao userDao;
    /**
     * constructs user objects
     */
    private UserFactory userFactory;
}
