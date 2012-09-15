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
     * @see org.marketcetera.webservices.systemmodel.UserService#addUser(java.lang.String)
     */
    @Override
    public WebServicesUser addUserJSON(WebServicesUser inUser)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService addUserJSON invoked with user {}", //$NON-NLS-1$
                               inUser);
        return doAddUser(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#getUser(long)
     */
    @Override
    public WebServicesUser getUserJSON(long inId)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService getUserJSON invoked with id {}", //$NON-NLS-1$
                               inId);
        return new WebServicesUser(doGetUser(inId));
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#getUsers()
     */
    @Override
    public List<WebServicesUser> getUsersJSON()
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class, "UserService getUsersJSON invoked"); //$NON-NLS-1$
        return doGetUsers();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#deleteUser(long)
     */
    @Override
    public Response deleteUser(final long inId)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService deleteUser invoked with id {}", //$NON-NLS-1$
                               inId);
        Response response;
        try {
            User user = userDao.getById(inId);
            userDao.delete(user);
            response = Response.ok().build();
        } catch (RuntimeException e) {
            SLF4JLoggerProxy.error(this,
                                   e);
            response = Response.serverError()
                               .entity(e.getMessage()).build();
        }
        return response;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#addUserXML(org.marketcetera.api.dao.User)
     */
    @Override
    public WebServicesUser addUserXML(WebServicesUser inUser)
    {
        return doAddUser(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#getUserXML(long)
     */
    @Override
    public WebServicesUser getUserXML(long inId)
    {
        SLF4JLoggerProxy.debug(UserServiceImpl.class,
                               "UserService getUserXML invoked with id {}", //$NON-NLS-1$
                               inId);
        return doGetUser(inId);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#getUsersXML()
     */
    @Override
    public List<WebServicesUser> getUsersXML()
    {
        return doGetUsers();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#updateUserJSON(org.marketcetera.webservices.systemmodel.WebServicesUser)
     */
    @Override
    public Response updateUserJSON(WebServicesUser inUser)
    {
        return updateUser(inUser);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.webservices.systemmodel.UserService#updateUserXML(org.marketcetera.webservices.systemmodel.WebServicesUser)
     */
    @Override
    public Response updateUserXML(WebServicesUser inUser)
    {
        return updateUser(inUser);
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
     * Updates the given <code>User</code>.
     *
     * @param inUser a <code>WebServicesUser</code> value
     * @return a <code>Response</code> value
     */
    private Response updateUser(WebServicesUser inUser)
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
    /**
     * Executes the retrieval of the the <code>User</code> object associated with the given id.
     *
     * @param inId a <code>long</code> value
     * @return a <code>WebServicesUser</code> value
     */
    private WebServicesUser doGetUser(long inId)
    {
        User user = userDao.getById(inId);
        if(user == null) {
            return null;
        }
        return new WebServicesUser(user);
    }
    /**
     * Executes the retrieval of all existing <code>User</code> objects.
     *
     * @return a <code>List&lt;WebServicesUser&gt;</code> value
     */
    private List<WebServicesUser> doGetUsers()
    {
        List<WebServicesUser> decoratedUsers = new ArrayList<WebServicesUser>();
        for(User user : userDao.getAll()) {
            decoratedUsers.add(new WebServicesUser(user));
        }
        return decoratedUsers;
    }
    /**
     * Executes the addition of the given <code>User</code> object.
     *
     * @param inUser a <code>WebServicesUser</code> value
     * @return a <code>WebServicesUser</code> value
     */
    private WebServicesUser doAddUser(WebServicesUser inUser)
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
    /**
     * allows datastore access for user objects
     */
    private UserDao userDao;
    /**
     * constructs user objects
     */
    private UserFactory userFactory;
}
