package org.marketcetera.core.ws.tags;

import org.junit.Test;
import org.marketcetera.core.util.log.I18NBoundMessage2P;

import static org.junit.Assert.*;

/**
 * @since 1.0.0
 * @version $Id: NonNullTagFilterTest.java 82324 2012-04-09 20:56:08Z colin $
 */

/* $License$ */

public class NonNullTagFilterTest
    extends TagFilterTestBase
{
    private static final I18NBoundMessage2P TEST_MESSAGE=
        new I18NBoundMessage2P(TestMessages.MESSAGE,"a","b");


    @Test
    public void all()
        throws Exception
    {
        NonNullTagFilter filter=new NonNullTagFilter(TEST_MESSAGE);
        assertEquals(TEST_MESSAGE,filter.getMessage());

        single(filter,TEST_TAG,null,TEST_MESSAGE);
    }
}
