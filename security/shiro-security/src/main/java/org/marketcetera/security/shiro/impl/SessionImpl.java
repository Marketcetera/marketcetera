package org.marketcetera.security.shiro.impl;

import java.io.Serializable;

import org.marketcetera.api.security.Session;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
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
        session.stop();
        session = null;
    }
}
