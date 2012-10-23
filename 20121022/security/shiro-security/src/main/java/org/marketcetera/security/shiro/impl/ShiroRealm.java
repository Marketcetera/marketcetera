package org.marketcetera.security.shiro.impl;

import javax.persistence.NoResultException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.marketcetera.api.dao.UserDao;
import org.marketcetera.api.security.User;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;

/**
* @version $Id$
* @date 9/3/12 12:49 PM
*/
class ShiroRealm extends AuthorizingRealm {
// ------------------------------ FIELDS ------------------------------

    private final String name = "Basic Realm";
    private UserDao userDao;

// --------------------------- CONSTRUCTORS ---------------------------

    public ShiroRealm(UserDao userDao) {
        this.userDao = userDao;
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    @Override
    public String getName() {
        return name;
    }

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Realm ---------------------

    @Override
    public boolean supports(AuthenticationToken token) {
        return (token instanceof org.apache.shiro.authc.UsernamePasswordToken);
    }

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        org.apache.shiro.authc.UsernamePasswordToken upToken = (org.apache.shiro.authc.UsernamePasswordToken) token;

        SimpleAuthenticationInfo result;
        try {
            User user = userDao.getByName((String) upToken.getPrincipal());
            // TODO hash given password
            if (String.valueOf(upToken.getPassword()).equals(user.getPassword())) {
                result = new SimpleAuthenticationInfo(upToken.getPrincipal(), upToken.getCredentials(), name);
            } else {
                SLF4JLoggerProxy.warn(this,
                                      "Authentication failed for {}, password does not match",
                                      upToken.getPrincipal());
                throw new AuthenticationException();
            }
            return result;
        } catch (NoResultException e) {
            SLF4JLoggerProxy.warn(this,
                                  "Authentication failed for {}, username not known",
                                  upToken.getPrincipal());
            throw new AuthenticationException();
        }
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }
}
