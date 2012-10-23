package org.marketcetera.core.util.l10n;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

/**
 * @since 0.6.0
 * @version $Id: PropertyMessageInfoTest.java 16063 2012-01-31 18:21:55Z colin $
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

        assertEquality(TEST_PROPERTY_INFO,
                       TEST_PROPERTY_INFO_SAME,
                       TEST_PROPERTY_INFO_KD,
                       TEST_PROPERTY_INFO_PCD,
                       TEST_PROPERTY_INFO_TD);
    }
}
