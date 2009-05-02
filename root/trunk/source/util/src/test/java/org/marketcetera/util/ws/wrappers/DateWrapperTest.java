package org.marketcetera.util.ws.wrappers;

import java.util.Date;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.ComparableAssert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 1.5.0
 * @version $Id$
 */

/* $License$ */

public class DateWrapperTest
    extends WrapperTestBase
{
    private static final Date TEST_DATE=
        new Date(1);
    private static final Date TEST_DATE_D=
        new Date(2);


    @Test
    public void all()
    {
        DateWrapper empty=new DateWrapper();
        dual(new DateWrapper(TEST_DATE),
             new DateWrapper(TEST_DATE),
             empty,
             new DateWrapper(null),
             TEST_DATE.toString(),
             TEST_DATE,TEST_DATE.getTime());

        assertComparable(TEST_DATE,
                         new Date(1),
                         TEST_DATE_D);

        DateWrapper w=new DateWrapper(TEST_DATE);
        assertComparable(w,
                         new DateWrapper(TEST_DATE),
                         new DateWrapper(TEST_DATE_D),
                         "Argument is null");

        try {
            empty.compareTo(w);
            fail();
        } catch (NullPointerException ex) {
            assertEquals("Receiver wraps a null value",ex.getMessage());
        }

        try {
            w.compareTo(empty);
            fail();
        } catch (NullPointerException ex) {
            assertEquals("Argument wraps a null value",ex.getMessage());
        }
    }
}
