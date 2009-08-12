package org.marketcetera.ors.info;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since $Release$
 * @version $Id$
 */

/* $License$ */

public class NestedInfoImplTest
    extends InfoTestBase
{
    @Test
    public void all()
        throws Exception
    {
        NestedInfoImpl<SystemInfo> info=
            new NestedInfoImpl<SystemInfo>(TEST_NAME,SYSTEM_INFO);
        readWrite(info,TEST_NAME,
                  SYSTEM_INFO_NAME+NestedInfoImpl.NAME_SEPARATOR+
                  TEST_NAME);
        assertSame(SYSTEM_INFO,info.getParentInfo());
    }
}
