package org.marketcetera.util.auth;

import org.junit.Ignore;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public class HolderTestBase
    extends TestCaseBase
{
    private static <T> void set
        (Holder<T> holder,
         T value)
    {
        assertFalse(holder.isSet());
        assertNull(holder.getValue());

        holder.setValue(value);
        assertTrue(holder.isSet());
        assertEquals(value,holder.getValue());

        holder.setValue(null);
        assertFalse(holder.isSet());
        assertNull(holder.getValue());
    }

    protected static <T> void simpleNoMessage
        (Holder<T> holder,
         T value)
    {
        assertNull(holder.getMessage());
        set(holder,value);
    }

    protected static <T> void simpleWithMessage
        (Holder<T> holder,
         T value)
    {
        assertEquals(TestMessages.TEST_MESSAGE,holder.getMessage());
        set(holder,value);
    }
}
