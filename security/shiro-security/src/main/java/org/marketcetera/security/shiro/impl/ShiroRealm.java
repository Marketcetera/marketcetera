package org.marketcetera.security.shiro.impl;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
* @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
* @version $Id$
* @date 9/3/12 12:49 PM
*/
class ShiroRealm extends AuthorizingRealm {
    private final String name = "Basic Realm";

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return (token instanceof org.apache.shiro.authc.UsernamePasswordToken);
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        org.apache.shiro.authc.UsernamePasswordToken upToken = (org.apache.shiro.authc.UsernamePasswordToken) token;

        SimpleAuthenticationInfo result;
        if (upToken.getPrincipal().equals("test") && String.valueOf(upToken.getPassword()).equals("test")) {
            result = new SimpleAuthenticationInfo(upToken.getPrincipal(), upToken.getCredentials(), name);
        } else {
            result = null;
        }
        return result;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return new SimpleAuthorizationInfo();
    }
}
