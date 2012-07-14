package org.marketcetera.util.ws.tags;

import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;
import static org.marketcetera.util.test.SerializableAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

public class TagTestBase
    extends TestCaseBase
{
    protected static final String TEST_VALUE=
        "testValue";
    protected static final String TEST_VALUE_D=
        "testValueD";


    protected static void single
        (Tag tag,
         Tag copy,
         Tag empty)
    {
        assertEquality(tag,copy,empty);
        assertSerializable(tag);

        assertEquals(TEST_VALUE,tag.getValue());
        assertEquals(TEST_VALUE,tag.toString());

        assertNull(empty.getValue());

        tag.setValue(TEST_VALUE_D);
        assertEquals(TEST_VALUE_D,tag.getValue());

        tag.setValue(null);
        assertEquals(null,tag.getValue());

        assertEquals(empty,tag);
    }
}
