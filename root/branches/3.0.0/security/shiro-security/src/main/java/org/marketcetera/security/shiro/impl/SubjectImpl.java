package org.marketcetera.security.shiro.impl;

import java.util.HashSet;
import java.util.Set;

import org.marketcetera.api.security.AuthenticationToken;
import org.marketcetera.api.security.Session;
import org.marketcetera.api.security.Subject;

/**
 * @version $Id$
 * @date 8/19/12 7:03 AM
 */

public class SubjectImpl implements Subject {
    private Set<AuthenticationToken> authenticationTokens = new HashSet<AuthenticationToken>();
    private Session session = null;
    private org.apache.shiro.subject.Subject subject;

    public SubjectImpl(org.apache.shiro.subject.Subject subject) {
        this.subject = subject;
    }


    @Override
    public void login(final AuthenticationToken token) {
        subject.login(new org.apache.shiro.authc.UsernamePasswordToken(token.getPrincipal(), token.getCredentials()));
    }

    @Override
    public void logout() {
        subject.logout();
    }

    @Override
    public Session getSession() {
        if (session == null) {
            session = new SessionImpl(subject.getSession());
        }
        return session;
    }

    @Override
    public boolean isAuthenticated() {
        return subject.isAuthenticated();
    }

    @Override
    public boolean hasRole(String role) {
        return subject.hasRole(role);
    }

    @Override
    public boolean isPermitted(String permission) {
        return subject.isPermitted(permission);
    }
}
