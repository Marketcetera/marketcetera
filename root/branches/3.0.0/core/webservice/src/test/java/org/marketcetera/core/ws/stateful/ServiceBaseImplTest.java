package org.marketcetera.core.ws.stateful;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: ServiceBaseImplTest.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public class ServiceBaseImplTest
    extends ServiceImplTestBase<Object>
{
    @Test
    public void all()
        throws Exception
    {
        single(new ServiceBaseImpl<Object>(TEST_MANAGER),
               new ServiceBaseImpl<Object>(null));
    }
}
