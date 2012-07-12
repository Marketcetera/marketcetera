package org.marketcetera.ors.info;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class ReadWriteInfoImplTest
    extends InfoTestBase
{
    @Test
    public void all()
        throws Exception
    {
        readWrite(new ReadWriteInfoImpl(TEST_NAME),TEST_NAME,
                  TEST_NAME);
    }
}
