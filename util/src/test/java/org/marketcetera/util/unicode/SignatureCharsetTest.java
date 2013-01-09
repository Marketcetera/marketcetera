package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;
import static org.marketcetera.util.unicode.SignatureCharset.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class SignatureCharsetTest
    extends TestCaseBase
{

    private static void single
        (SignatureCharset sc,
         Signature signature,
         UnicodeCharset charset,
         byte[] data)
        throws Exception
    {
        assertTrue(sc.isSupported());
        sc.assertSupported();

        assertEquals(signature,sc.getSignature());
        assertEquals(charset,sc.getCharset());

        assertNull(sc.decode(null));
        assertNull(sc.encode(null));

        assertEquals(COMBO,sc.decode(data));
        assertArrayEquals(data,sc.encode(COMBO));
    }


    @Test
    public void all()
        throws Exception
    {
        single(NONE_UTF8,Signature.NONE,
               UnicodeCharset.UTF8,COMBO_UTF8);
        single(NONE_UTF16BE,Signature.NONE,
               UnicodeCharset.UTF16BE,COMBO_UTF16BE);
        single(NONE_UTF16LE,Signature.NONE,
               UnicodeCharset.UTF16LE,COMBO_UTF16LE);
        single(NONE_UTF32BE,Signature.NONE,
               UnicodeCharset.UTF32BE,COMBO_UTF32BE);
        single(NONE_UTF32LE,Signature.NONE,
               UnicodeCharset.UTF32LE,COMBO_UTF32LE);

        single(UTF8_UTF8,Signature.UTF8,
               UnicodeCharset.UTF8,ArrayUtils.addAll
               (Signature.UTF8.getMark(),COMBO_UTF8));
        single(UTF16BE_UTF16BE,Signature.UTF16BE,
               UnicodeCharset.UTF16BE,ArrayUtils.addAll
               (Signature.UTF16BE.getMark(),COMBO_UTF16BE));
        single(UTF16LE_UTF16LE,Signature.UTF16LE,
               UnicodeCharset.UTF16LE,ArrayUtils.addAll
               (Signature.UTF16LE.getMark(),COMBO_UTF16LE));
        single(UTF32BE_UTF32BE,Signature.UTF32BE,
               UnicodeCharset.UTF32BE,ArrayUtils.addAll
               (Signature.UTF32BE.getMark(),COMBO_UTF32BE));
        single(UTF32LE_UTF32LE,Signature.UTF32LE,
               UnicodeCharset.UTF32LE,ArrayUtils.addAll
               (Signature.UTF32LE.getMark(),COMBO_UTF32LE));
    }
}
