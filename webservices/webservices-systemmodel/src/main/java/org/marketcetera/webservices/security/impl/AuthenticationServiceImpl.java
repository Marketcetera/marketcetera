package org.marketcetera.webservices.security.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.security.sasl.AuthenticationException;

import org.marketcetera.api.systemmodel.Permission;
import org.marketcetera.core.security.SecurityService;
import org.marketcetera.core.security.User;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;
import org.marketcetera.dao.PermissionDao;
import org.marketcetera.dao.UserDao;
import org.marketcetera.webservices.security.AuthenticationService;
import org.marketcetera.webservices.systemmodel.WebServicesPermission;

/* $License$ */

/**
 * Provides web services access to the authentication service.
 *
 * @version $Id$
 * @since $Release$
 */
public class AuthenticationServiceImpl
        implements AuthenticationService
{
    /**
     * Sets the permission DAO value.
     *
     * @param permissionDao a <code>PermissionDao</code> value
     */
    public void setPermissionDao(PermissionDao permissionDao)
    {
        this.permissionDao = permissionDao;
    }
    /**
     * Sets the authenticationManagerService value.
     *
     * @param securityService <code>Subject</code> value
     */
    public void setSecurityService(SecurityService securityService)
    {
        this.securityService = securityService;
    }
    /**
     * Sets the userDao value.
     *
     * @param a <code>UserDao</code> value
     */
    public void setUserDao(UserDao inUserDao)
    {
        userDao = inUserDao;
    }
    /* (non-Javadoc)
    * @see org.marketcetera.webservices.security.AuthenticationService#authenticate(java.lang.String, java.lang.String)
    */
    @Override
    public List<WebServicesPermission> authenticate(String username,
                                                    String password)
    {
        SLF4JLoggerProxy.trace(AuthenticationServiceImpl.class, "AuthenticationService authenticate invoked with {}/********", //$NON-NLS-1$
                               username);
        try {
            User user = userDao.getByName(username);
            if(user.getPassword() == null) {
                if(password != null) {
                    throw new AuthenticationException("Incorrect username or password");
                }
            } else {
                if(!user.getPassword().equals(password)) {
                    throw new AuthenticationException("Incorrect username or password");
                }
            }
        } catch (AuthenticationException e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Username: {} failed authentication",
                                  username);
            throw new RuntimeException(e);
        } catch (NoResultException e) {
            SLF4JLoggerProxy.warn(this,
                                  e,
                                  "Username: {} failed authentication",
                                  username);
            throw new RuntimeException(new AuthenticationException("Incorrect username or password"));
        }
        // TODO this seems to be highly session-based, which is not the right idea: https://issues.apache.org/jira/browse/SHIRO-266
//        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
//        Subject subject = securityService.getSubject();
//        try {
//            subject.login(token);
//        } catch (UnknownSessionException e) {
//            try {
//                subject.getSession().invalidate();
//            } catch (UnknownSessionException ignored) {}
//            return authenticate(username,
//                                password);
//        } catch (RuntimeException e) {
//            SLF4JLoggerProxy.warn(this,
//                                  e);
//            throw e;
//        }
        List<WebServicesPermission> decoratedPermissions = new ArrayList<WebServicesPermission>();
        for (Permission permission : permissionDao.getAllByUsername(username)) {
            decoratedPermissions.add(new WebServicesPermission(permission));
        }

        return decoratedPermissions;
    }
    /**
     * provides access to user data objects
     */
    private UserDao userDao;
    /**
     * provides access to permission data objects
     */
    private PermissionDao permissionDao;
    /**
     * authentication manager subject
     */
    @SuppressWarnings("unused")
    private SecurityService securityService;
}
