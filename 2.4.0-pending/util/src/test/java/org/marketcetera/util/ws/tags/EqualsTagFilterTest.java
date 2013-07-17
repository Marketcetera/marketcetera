package org.marketcetera.util.ws.tags;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage2P;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class EqualsTagFilterTest
    extends TagFilterTestBase
{
    @Test
    public void all()
        throws Exception
    {
        EqualsTagFilter filter=new EqualsTagFilter
            (TEST_TAG,TestMessages.MESSAGE);
        assertEquals(TEST_TAG,filter.getTarget());
        assertEquals(TestMessages.MESSAGE,filter.getMessage());

        single(filter,TEST_TAG,TEST_TAG_D,
               new I18NBoundMessage2P
               (TestMessages.MESSAGE,TEST_TAG,TEST_TAG_D));
        single(filter,TEST_TAG,null,
               new I18NBoundMessage2P
               (TestMessages.MESSAGE,TEST_TAG,null));
        single(new EqualsTagFilter(null,TestMessages.MESSAGE),
               null,TEST_TAG,
               new I18NBoundMessage2P
               (TestMessages.MESSAGE,null,TEST_TAG));
    }
}
