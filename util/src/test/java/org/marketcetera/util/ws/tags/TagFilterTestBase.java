package org.marketcetera.util.ws.tags;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class TagFilterTestBase
    extends TestCaseBase
{
    protected static final Tag TEST_TAG=
        new Tag("tag");
    protected static final Tag TEST_TAG_D=
        new Tag("tagD");


    protected static void singlePass
        (TagFilter filter,
         Tag tag)
        throws Exception
    {
        filter.assertMatch(tag);
    }

    protected static void single
        (TagFilter filter,
         Tag passTag,
         Tag failTag,
         I18NBoundMessage failureMessage)
        throws Exception
    {
        singlePass(filter,passTag);
        try {
            filter.assertMatch(failTag);
            fail();
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),failureMessage,
                         ex.getI18NBoundMessage());
        }
    }
}
