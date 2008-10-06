package org.marketcetera.util.l10n;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class MessageInfoPairTest
    extends MessageInfoTestBase
{
    @Test
    public void all()
    {
        MessageInfoPair info=
            new MessageInfoPair(TEST_I18N_INFO,TEST_PROPERTY_INFO);
        assertEquals(TEST_I18N_INFO,info.getSrcInfo());
        assertEquals(TEST_PROPERTY_INFO,info.getDstInfo());

        assertTrue(info.equals(info));

        assertFalse(info.equals(null));
        assertFalse(info.equals(0));

        MessageInfoPair infoSame=
            new MessageInfoPair(TEST_I18N_INFO_SAME,TEST_PROPERTY_INFO_SAME);
        assertTrue(info!=infoSame);
        assertTrue(info.equals(infoSame));
        assertEquals(info.hashCode(),infoSame.hashCode());

        assertFalse(info.equals(new MessageInfoPair
                                (TEST_I18N_INFO_KD,TEST_PROPERTY_INFO)));
        assertFalse(info.equals(new MessageInfoPair
                                (TEST_I18N_INFO,TEST_PROPERTY_INFO_KD)));
    }
}
