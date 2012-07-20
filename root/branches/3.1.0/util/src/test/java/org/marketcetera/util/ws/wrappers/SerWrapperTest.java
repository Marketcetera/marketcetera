package org.marketcetera.util.ws.wrappers;

import org.junit.Test;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id: SerWrapperTest.java 82384 2012-07-20 19:09:59Z colin $
 */

/* $License$ */

public class SerWrapperTest
    extends WrapperTestBase
{
    private static final TestInteger TEST_INTEGER=
        new TestInteger(1);


    @Test
    public void all()
        throws Exception
    {
        serialization(new SerWrapper<TestInteger>(TEST_INTEGER),
                      new SerWrapper<TestInteger>(TEST_INTEGER),
                      new SerWrapper<TestInteger>(),
                      new SerWrapper<TestInteger>(null),
                      "I am 1",TEST_INTEGER,
                      new TestUnserializableInteger(1),
                      SerWrapper.class.getName());
    }
}
