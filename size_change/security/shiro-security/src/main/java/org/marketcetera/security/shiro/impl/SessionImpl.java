package org.marketcetera.security.shiro.impl;

import java.io.Serializable;

import org.apache.shiro.session.UnknownSessionException;
import org.marketcetera.api.security.Session;

/**
 * @version $Id$
 * @date 8/19/12 7:04 AM
 */

public class SessionImpl implements Session {


    private org.apache.shiro.session.Session session;

    public SessionImpl(org.apache.shiro.session.Session session) {
        this.session = session;
    }

    @Override
    public Serializable identifier() {
        return session.getId();
    }

    @Override
    public void invalidate() {
        try {
            session.stop();
        } catch (UnknownSessionException e) {
            throw new org.marketcetera.core.security.UnknownSessionException(e);
        } finally {
            session = null;
        }
    }
}
