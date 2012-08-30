package org.marketcetera.security.shiro.impl;

import org.marketcetera.api.security.Session;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 7:04 AM
 */

public class SessionImpl implements Session {


    @Override
    public Session findSession(String identifier) {
        return null;
    }

    @Override
    public String identifier() {
        return null;
    }

    @Override
    public void invalidate() {

    }
}
