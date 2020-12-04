package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.CharEncoding;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;
import static org.marketcetera.util.unicode.UnicodeCharset.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class UnicodeCharsetTest
    extends TestCaseBase
{
    private static void singlePrefix
        (UnicodeCharset charset,
         String name,
         byte[] dataIn,
         byte[] dataOut)
        throws Exception
    {
        assertEquals(name,charset.getName());

        assertTrue(charset.isSupported());
        charset.assertSupported();

        assertNull(charset.decode(null));
        assertNull(charset.decode(null,0,0));
        assertNull(charset.encode(null));

        assertEquals(COMBO,charset.decode(dataIn));

        byte[] inEx=ArrayUtils.addAll
            (new byte[] {(byte)0x00,(byte)0x01},dataIn);
        inEx=ArrayUtils.addAll
            (inEx,new byte[] {(byte)0x00,(byte)0x01});
        assertEquals(COMBO,charset.decode(inEx,2,inEx.length-4));
        assertArrayEquals(dataOut,charset.encode(COMBO));
    }

    private static void single
        (UnicodeCharset charset,
         String name,
         byte[] data)
        throws Exception
    {
        singlePrefix(charset,name,data,data);
    }


    @Test
    public void utf8()
        throws Exception
    {
        single(UTF8,CharEncoding.UTF_8,COMBO_UTF8);
    }

    @Test
    public void utf16()
        throws Exception
    {
        single(UTF16BE,CharEncoding.UTF_16BE,COMBO_UTF16BE);
        single(UTF16LE,CharEncoding.UTF_16LE,COMBO_UTF16LE);

        byte[] dataOut=ArrayUtils.addAll
            (Signature.UTF16BE.getMark(),COMBO_UTF16BE);
        singlePrefix
            (UTF16,CharEncoding.UTF_16,COMBO_UTF16BE,dataOut);
        singlePrefix
            (UTF16,CharEncoding.UTF_16,ArrayUtils.addAll
             (Signature.UTF16BE.getMark(),COMBO_UTF16BE),dataOut);
        singlePrefix
            (UTF16,CharEncoding.UTF_16,ArrayUtils.addAll
             (Signature.UTF16LE.getMark(),COMBO_UTF16LE),dataOut);
    }

    @Test
    public void utf32()
        throws Exception
    {
        single(UTF32BE,"UTF-32BE",COMBO_UTF32BE);
        single(UTF32LE,"UTF-32LE",COMBO_UTF32LE);

        singlePrefix
            (UTF32,"UTF-32",COMBO_UTF32BE,COMBO_UTF32BE);
        singlePrefix
            (UTF32,"UTF-32",ArrayUtils.addAll
             (Signature.UTF32BE.getMark(),COMBO_UTF32BE),
             COMBO_UTF32BE);
        singlePrefix
            (UTF32,"UTF-32",ArrayUtils.addAll
             (Signature.UTF32LE.getMark(),COMBO_UTF32LE),
             COMBO_UTF32BE);
    }
}
