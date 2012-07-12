package org.marketcetera.util.l10n;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.EqualityAssert.*;

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

        assertEquality(TEST_PROPERTY_INFO,
                       TEST_PROPERTY_INFO_SAME,
                       TEST_PROPERTY_INFO_KD,
                       TEST_PROPERTY_INFO_PCD,
                       TEST_PROPERTY_INFO_TD);
    }
}
