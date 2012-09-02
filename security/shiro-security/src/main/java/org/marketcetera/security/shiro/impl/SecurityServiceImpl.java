package org.marketcetera.security.shiro.impl;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.marketcetera.api.security.SecurityService;
import org.marketcetera.api.security.Subject;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 5:03 AM
 */

public class SecurityServiceImpl implements SecurityService {

    private Map<org.apache.shiro.subject.Subject, Subject> map = new WeakHashMap<org.apache.shiro.subject.Subject, Subject>();

    public void init() {
        Realm realm = new Realm() {
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
            // @todo uupdate with call to database,etc.
            public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
                SimpleAuthenticationInfo result;
                org.apache.shiro.authc.UsernamePasswordToken upToken = (org.apache.shiro.authc.UsernamePasswordToken) token;
                if (upToken.getPrincipal().equals("test") && String.valueOf(upToken.getPassword()).equals("test")) {
                    result = new SimpleAuthenticationInfo(upToken.getPrincipal(), upToken.getCredentials(), name);
                } else {
                    result = null;
                }
                return result;
            }
        };

        org.apache.shiro.mgt.SecurityManager securityManager = new DefaultSecurityManager(realm);

        //Make the SecurityManager instance available to the entire application via static memory:
        SecurityUtils.setSecurityManager(securityManager);
    }

    @Override
    public Subject getSubject() {
        org.apache.shiro.subject.Subject shiroSubject = SecurityUtils.getSubject();

        Subject subject = map.get(shiroSubject);
        if (subject == null) {
            subject = new SubjectImpl(shiroSubject);
            map.put(shiroSubject, subject);
        }
        return subject;
    }
}
