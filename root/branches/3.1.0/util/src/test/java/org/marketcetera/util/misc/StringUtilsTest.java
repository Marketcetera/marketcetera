package org.marketcetera.util.misc;

import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class StringUtilsTest
    extends TestCaseBase
{
    private static void single
        (String s,
         int[] ucps)
    {
        assertEquals(ucps.length,StringUtils.lengthUCP(s));
        assertEquals(s,StringUtils.fromUCP(ucps));
        assertArrayEquals(ucps,StringUtils.toUCPArray(s));

        String c=StringUtils.fromUCP(ucps[0]);
        assertEquals(1,StringUtils.lengthUCP(c));
        assertEquals(ucps[0],c.codePointAt(0));
    }


    @Test
    public void all()
    {
        assertNull(StringUtils.toUCPArray(null));
        single(SPACE,SPACE_UCPS);
        single(HELLO_EN,HELLO_EN_UCPS);
        single(LANGUAGE_NO,LANGUAGE_NO_UCPS);
        single(HELLO_GR,HELLO_GR_UCPS);
        single(HOUSE_AR,HOUSE_AR_UCPS);
        single(GOODBYE_JA,GOODBYE_JA_UCPS);
        single(GOATS_LNB,GOATS_LNB_UCPS);
        single(G_CLEF_MSC,G_CLEF_MSC_UCPS);
    }

    @Test
    public void toUCPArrayStr()
    { 
        assertNull(StringUtils.toUCPArrayStr(null));
        assertEquals("U+10088 U+20 U+10089",
                     StringUtils.toUCPArrayStr(GOATS_LNB));
        assertEquals("U+1D11E",
                     StringUtils.toUCPArrayStr(G_CLEF_MSC));
        assertEquals("U+3055 U+3088 U+3046 U+306A U+3089",
                     StringUtils.toUCPArrayStr(GOODBYE_JA));
    }

    @Test
    public void isValid()
    {
        assertTrue(StringUtils.isValid(G_CLEF_MSC_UCPS[0]));
        assertFalse(StringUtils.isValid(0x10000000));
        assertFalse(StringUtils.isValid(0xD801));
    }
}
