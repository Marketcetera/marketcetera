package org.marketcetera.security.shiro.impl;

import java.util.HashSet;
import java.util.Set;

import org.marketcetera.api.security.AuthenticationToken;
import org.marketcetera.api.security.Session;
import org.marketcetera.api.security.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 7:03 AM
 */

public class SubjectImpl implements Subject {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SubjectImpl.class);
    private Set<AuthenticationToken> authenticationTokens = new HashSet<AuthenticationToken>();
    private Session session = null;


    @Override
    public void login(AuthenticationToken token) {
        authenticationTokens.add(token);
    }

    @Override
    public void logout() {
        authenticationTokens.clear();
    }

    @Override
    public Session getSession() {
        return session;
    }

    @Override
    public boolean isAuthenticated() {
        return !authenticationTokens.isEmpty();
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }

    @Override
    public boolean isPermitted(String permission) {
        return false;
    }
}
