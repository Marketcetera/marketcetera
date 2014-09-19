package org.marketcetera.util.ws.stateful;

import org.marketcetera.util.test.TestCaseBase;
import org.marketcetera.util.ws.stateless.StatelessClientContext;
import org.marketcetera.util.ws.tags.SessionId;
import org.marketcetera.util.ws.tags.VersionId;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class ServiceImplTestBase<T>
    extends TestCaseBase
{
    protected final SessionManager<T> TEST_MANAGER=
        new SessionManager<T>();
    protected final StatelessClientContext TEST_CONTEXT=
        new StatelessClientContext();


    protected ServiceImplTestBase()
    {
        TEST_CONTEXT.setVersionId(VersionId.SELF);
    }


    protected void single
        (ServiceBaseImpl<T> impl,
         ServiceBaseImpl<T> empty)
    {
        assertEquals(TEST_MANAGER,impl.getSessionManager());
        if (empty!=null) {
            assertNull(empty.getSessionManager());
        }
    }

    protected ClientContext getContext
        (SessionId sessionId)
    {
        ClientContext context=new ClientContext();
        context.setVersionId(VersionId.SELF);
        context.setSessionId(sessionId);
        return context;
    }
}
