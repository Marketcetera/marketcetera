package org.marketcetera.security.shiro.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.session.UnknownSessionException;
import org.marketcetera.api.security.AuthenticationToken;
import org.marketcetera.api.security.Session;
import org.marketcetera.api.security.Subject;
import org.marketcetera.core.util.log.SLF4JLoggerProxy;

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
        try {
            subject.login(new org.apache.shiro.authc.UsernamePasswordToken(token.getPrincipal(), token.getCredentials()));
        } catch (AuthenticationException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw e;
        } catch (UnknownSessionException e) {
            SLF4JLoggerProxy.warn(this,
                                  e);
            throw new org.marketcetera.core.security.UnknownSessionException(e);
        }
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
