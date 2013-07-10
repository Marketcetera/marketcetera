package org.marketcetera.ors.info;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 2.0.0
 * @version $Id$
 */

/* $License$ */

public class ReadInfoImplTest
    extends InfoTestBase
{
    private static final class MyInfo
        extends ReadInfoImpl
    {
        MyInfo()
        {
            super(TEST_NAME);
            getMap().put(KEY_INT_1,VALUE_INT_1);
            getMap().put(KEY_NULL_VALUE,NULL_VALUE);
        }
    }


    @Test
    public void all()
        throws Exception
    {
        read(new MyInfo(),TEST_NAME,
             TEST_NAME);
    }
}
