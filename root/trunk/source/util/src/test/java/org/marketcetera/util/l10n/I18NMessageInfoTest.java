package org.marketcetera.util.l10n;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class I18NMessageInfoTest
    extends MessageInfoTestBase
{
    @Test
    public void all()
    {
        retention(TEST_I18N_INFO);
        assertEquals(TEST_MESSAGE,TEST_I18N_INFO.getMessage());

        assertTrue(TEST_I18N_INFO!=TEST_I18N_INFO_SAME);
        assertTrue(TEST_I18N_INFO.equals(TEST_I18N_INFO_SAME));
        assertEquals(TEST_I18N_INFO.hashCode(),
                     TEST_I18N_INFO_SAME.hashCode());

        assertFalse(TEST_I18N_INFO.equals(TEST_I18N_INFO_KD));
        assertFalse(TEST_I18N_INFO.equals(TEST_I18N_INFO_PCD));
        assertFalse(TEST_I18N_INFO.equals(TEST_I18N_INFO_MD));
    }
}
