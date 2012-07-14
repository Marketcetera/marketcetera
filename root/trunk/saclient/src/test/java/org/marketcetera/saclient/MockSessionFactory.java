package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * A mock session factory for testing.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */

@ClassVersion("$Id")
class MockSessionFactory implements SessionFactory<Object> {

    /**
     * Creates a new session factory which uses the given JMS manager
     * to create reply topics, and which notifies the given user
     * manager when sessions are added/removed.
     */

    public MockSessionFactory() {
    }

    @Override
    public Object createSession
            (StatelessClientContext context,
             String user,
             SessionId id) {
        return new Object();
    }

    @Override
    public void removedSession(Object session) {
        //do nothing.
    }
}