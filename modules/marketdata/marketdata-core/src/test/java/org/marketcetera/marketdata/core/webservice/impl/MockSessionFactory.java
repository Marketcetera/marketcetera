package org.marketcetera.marketdata.core.webservice.impl;

import org.marketcetera.util.ws.stateful.SessionFactory;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;

/* $License$ */

/**
 * Creates {@link MockSession} objects.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockSessionFactory
        implements SessionFactory<MockSession>
{
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.SessionFactory#createSession(org.marketcetera.util.ws.stateless.StatelessClientContext, java.lang.String, org.marketcetera.util.ws.tags.SessionId)
     */
    @Override
    public MockSession createSession(StatelessClientContext inContext,
                                     String inUser,
                                     SessionId inId)
    {
        return new MockSession();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.util.ws.stateful.SessionFactory#removedSession(java.lang.Object)
     */
    @Override
    public void removedSession(MockSession inSession)
    {
    }
}
