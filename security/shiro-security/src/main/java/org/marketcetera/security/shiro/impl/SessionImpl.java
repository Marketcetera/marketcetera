package org.marketcetera.security.shiro.impl;

import org.marketcetera.api.security.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:topping@codehaus.org">Brian Topping</a>
 * @version $Id$
 * @date 8/19/12 7:04 AM
 */

public class SessionImpl implements Session {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SessionImpl.class);


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
