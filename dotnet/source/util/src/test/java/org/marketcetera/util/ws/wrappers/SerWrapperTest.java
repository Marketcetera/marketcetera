package org.marketcetera.util.ws.wrappers;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class SerWrapperTest
    extends WrapperTestBase
{
    private static final int[] TEST_ARRAY=
        new int[] {1,2};


    @Test
    public void all()
    {
        serialization(new SerWrapper<int[]>(TEST_ARRAY),
                      new SerWrapper<int[]>(TEST_ARRAY),
                      new SerWrapper<int[]>(),
                      new SerWrapper<int[]>(null),
                      "{1,2}",TEST_ARRAY,
                      SerWrapper.class.getName());
    }
}
