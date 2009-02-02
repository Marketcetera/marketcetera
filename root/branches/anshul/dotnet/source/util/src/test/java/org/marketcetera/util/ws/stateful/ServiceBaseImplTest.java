package org.marketcetera.util.ws.stateful;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
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
