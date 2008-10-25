package org.marketcetera.util.l10n;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class PropertyMessageInfoTest
    extends MessageInfoTestBase
{
    @Test
    public void all()
    {
        retention(TEST_PROPERTY_INFO);
        assertEquals(TEST_TEXT,TEST_PROPERTY_INFO.getMessageText());

        assertTrue(TEST_PROPERTY_INFO!=TEST_PROPERTY_INFO_SAME);
        assertTrue(TEST_PROPERTY_INFO.equals(TEST_PROPERTY_INFO_SAME));
        assertEquals(TEST_PROPERTY_INFO.hashCode(),
                     TEST_PROPERTY_INFO_SAME.hashCode());

        assertFalse(TEST_PROPERTY_INFO.equals(TEST_PROPERTY_INFO_KD));
        assertFalse(TEST_PROPERTY_INFO.equals(TEST_PROPERTY_INFO_PCD));
        assertFalse(TEST_PROPERTY_INFO.equals(TEST_PROPERTY_INFO_TD));
    }
}
