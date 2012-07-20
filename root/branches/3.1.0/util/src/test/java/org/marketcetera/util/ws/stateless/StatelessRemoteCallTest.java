package org.marketcetera.util.ws.stateless;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: StatelessRemoteCallTest.java 82384 2012-07-20 19:09:59Z colin $
 */

/* $License$ */

public class StatelessRemoteCallTest
    extends StatelessRemoteCallTestBase
{
    @Test
    public void all()
        throws Exception
    {
        single
            (new StatelessRemoteCall
             (TEST_VERSION_FILTER,TEST_APP_FILTER,TEST_CLIENT_FILTER),
             new StatelessRemoteCall(null,null,null),
             new StatelessRemoteCall());
        assertNotNull(StatelessRemoteCall.DEFAULT_VERSION_FILTER);
    }
}
