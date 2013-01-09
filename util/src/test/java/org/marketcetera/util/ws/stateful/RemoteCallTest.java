package org.marketcetera.util.ws.stateful;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class RemoteCallTest
    extends RemoteCallTestBase
{
    @Test
    public void all()
        throws Exception
    {
        single
            (new RemoteCall<Object>
             (TEST_VERSION_FILTER,TEST_APP_FILTER,TEST_CLIENT_FILTER,
              TEST_MANAGER,TEST_SESSION_FILTER),
             new RemoteCall<Object>(null,null,null,null,null),
             new RemoteCall<Object>(TEST_MANAGER));
        assertEquals(TEST_MANAGER,RemoteCall.getDefaultSessionIdFilter
                     (TEST_MANAGER).getSessionManager());
    }
}
