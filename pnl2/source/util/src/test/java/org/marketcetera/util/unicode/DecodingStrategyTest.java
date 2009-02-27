package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;
import static org.marketcetera.util.unicode.DecodingStrategy.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class DecodingStrategyTest
    extends TestCaseBase
{
    private static void common
        (DecodingStrategy strategy,
         byte[] data,
         SignatureCharset sc)
        throws Exception
    {
        if (sc==null) {
            assertNull(strategy.getPrefixMatch(data));
            try {
                strategy.decode(data);
                fail();
            } catch (I18NException ex) {
                assertEquals(ex.getDetail(),Messages.NO_SIGNATURE_MATCHES,
                             ex.getI18NBoundMessage());
            }
            return;
        }
        data=ArrayUtils.addAll(sc.getSignature().getMark(),data);
        assertEquals(sc,strategy.getPrefixMatch(data));
        assertEquals(COMBO,strategy.decode(data));
    }

    private static void single
        (DecodingStrategy strategy,
         int length,
         byte[] data,
         SignatureCharset sc)
        throws Exception
    {
        assertNotNull(strategy.getSerializations());
        assertEquals(length,strategy.getSerializations().length);

        assertNull(strategy.decode(null));

        common(strategy,COMBO_UTF8,SignatureCharset.UTF8_UTF8);
        common(strategy,COMBO_UTF16BE,SignatureCharset.UTF16BE_UTF16BE);
        common(strategy,COMBO_UTF16LE,SignatureCharset.UTF16LE_UTF16LE);
        common(strategy,COMBO_UTF32BE,SignatureCharset.UTF32BE_UTF32BE);
        common(strategy,COMBO_UTF32LE,SignatureCharset.UTF32LE_UTF32LE);
        common(strategy,data,sc);
    }


    @Test
    public void all()
        throws Exception
    {
        single(UTF8_DEFAULT,4,
               COMBO_UTF8,SignatureCharset.NONE_UTF8);
        single(UTF16_DEFAULT,3,
               COMBO_UTF16BE,SignatureCharset.NONE_UTF16BE);
        single(UTF32_DEFAULT,3,
               COMBO_UTF32BE,SignatureCharset.NONE_UTF32BE);
        single(SIG_REQ,5,
               COMBO_UTF32BE,null);
    }
}
