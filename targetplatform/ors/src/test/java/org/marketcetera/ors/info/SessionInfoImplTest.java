package org.marketcetera.ors.info;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class SessionInfoImplTest
    extends InfoTestBase
{
    @Test
    public void all()
        throws Exception
    {
        readWrite(SESSION_INFO,SESSION_INFO_NAME,
                  SYSTEM_INFO_NAME+NestedInfoImpl.NAME_SEPARATOR+
                  SESSION_INFO_NAME);
        assertSame(SYSTEM_INFO,SESSION_INFO.getSystemInfo());

        // Custom name.

        assertEquals(TEST_NAME,
                     (new SessionInfoImpl(TEST_NAME,SYSTEM_INFO)).getName());
    }
}
