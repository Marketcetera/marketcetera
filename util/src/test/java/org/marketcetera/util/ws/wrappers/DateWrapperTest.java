package org.marketcetera.util.ws.wrappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.marketcetera.util.test.ComparableAssert.assertComparable;

import java.time.LocalDateTime;

import org.junit.Test;
import org.marketcetera.util.time.DateService;

/* $License$ */

/**
 * Tests {@link DateWrapper}.
 *
 * @author tlerios@marketcetera.com
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @since 1.5.0
 * @version $Id$
 */
public class DateWrapperTest
        extends WrapperTestBase
{
    @Test
    public void all()
        throws Exception
    {
        DateWrapper empty = new DateWrapper();
        dual(new DateWrapper(TEST_DATE),
             new DateWrapper(TEST_DATE),
             empty,
             new DateWrapper((LocalDateTime)null),
             TEST_DATE.toString(),
             TEST_DATE,
             DateService.toEpochMillis(TEST_DATE));
        assertComparable(TEST_DATE,
                         DateService.toLocalDateTime(1),
                         TEST_DATE_D);
        DateWrapper w = new DateWrapper(TEST_DATE);
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
    /**
     * test value 1ms past epoch, local time
     */
    private static final LocalDateTime TEST_DATE = DateService.toLocalDateTime(1);
    /**
     * test value 2ms past epoch, local time
     */
    private static final LocalDateTime TEST_DATE_D = DateService.toLocalDateTime(2);
}
