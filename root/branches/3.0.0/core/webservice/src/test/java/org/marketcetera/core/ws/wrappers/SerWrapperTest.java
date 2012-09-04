package org.marketcetera.core.ws.wrappers;

import org.junit.Test;

/**
 * @since 1.0.0
 * @version $Id: SerWrapperTest.java 82324 2012-04-09 20:56:08Z colin $
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
