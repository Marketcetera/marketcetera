package com.marketcetera.ors.brokers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.marketcetera.fix.FixSessionDay;

/* $License$ */

/**
 * Tests {@link FixSessionDay}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionDayTest
{
    /**
     * Test encoding of fix session day values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testEncoding()
            throws Exception
    {
        assertEquals(2,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Monday)));
        assertEquals(4,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Tuesday)));
        assertEquals(8,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Wednesday)));
        assertEquals(16,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Thursday)));
        assertEquals(32,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Friday)));
        assertEquals(64,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Saturday)));
        assertEquals(128,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Sunday)));
        assertEquals(192,
                     FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Sunday,FixSessionDay.Saturday)));
    }
    /**
     * Test decoding of fix session day values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testDecoding()
            throws Exception
    {
        Set<FixSessionDay> result = FixSessionDay.getValuesFor(192);
        assertEquals(result.toString(),
                     2,
                     result.size());
        assertTrue(result.contains(FixSessionDay.Saturday));
        assertTrue(result.contains(FixSessionDay.Sunday));
    }
    /**
     * Test is active today calculation for fix session day values.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testIsActiveToday()
            throws Exception
    {
        int value = FixSessionDay.getValue(Lists.newArrayList(FixSessionDay.Sunday,FixSessionDay.Saturday));
        assertTrue(FixSessionDay.Sunday.isActiveToday(value));
        assertTrue(FixSessionDay.Saturday.isActiveToday(value));
        assertFalse(FixSessionDay.Monday.isActiveToday(value));
    }
}
