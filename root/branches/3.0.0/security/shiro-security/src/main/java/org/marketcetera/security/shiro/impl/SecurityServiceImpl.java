package org.marketcetera.security.shiro.impl;

import java.util.Map;
import java.util.WeakHashMap;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.marketcetera.api.security.SecurityService;
import org.marketcetera.api.security.Subject;

/**
 * @version $Id$
 * @date 8/19/12 5:03 AM
 */

public class SecurityServiceImpl implements SecurityService {

    private Map<org.apache.shiro.subject.Subject, Subject> map = new WeakHashMap<org.apache.shiro.subject.Subject, Subject>();

    public void init() {
        Realm realm = new ShiroRealm();

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
