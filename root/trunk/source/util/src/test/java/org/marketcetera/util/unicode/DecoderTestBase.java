package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public abstract class DecoderTestBase
    extends IOTestBase
{
    protected abstract String decode
        (byte[] bytes)
        throws Exception;

    private void testDecode
        (byte[] bytes,
         String string)
        throws Exception
    {
        assertEquals(string,decode(bytes));
    }

    protected abstract String decode
        (SignatureCharset sc,
         byte[] bytes)
        throws Exception;

    private void testDecode
        (SignatureCharset sc,
         byte[] bytes,
         String string)
        throws Exception
    {
        assertEquals(string,decode(sc,bytes));
    }

    protected abstract String decode
        (DecodingStrategy strategy,
         SignatureCharset sc,
         byte[] bytes)
        throws Exception;
        
    private void testDecode
        (DecodingStrategy strategy,
         SignatureCharset sc,
         byte[] bytes,
         String string)
        throws Exception
    {
        assertEquals(string,decode(strategy,sc,bytes));
    }


    @Override
    protected void testNative()
        throws Exception
    {
        testDecode(HELLO_EN_NAT,HELLO_EN);
        testDecode(null,HELLO_EN_NAT,HELLO_EN);
        testDecode(null,null,HELLO_EN_NAT,HELLO_EN);

        testDecode(ArrayUtils.EMPTY_BYTE_ARRAY,StringUtils.EMPTY);
        testDecode(null,ArrayUtils.EMPTY_BYTE_ARRAY,StringUtils.EMPTY);
        testDecode(null,null,ArrayUtils.EMPTY_BYTE_ARRAY,StringUtils.EMPTY);
    }

    @Override
    protected void testSignatureCharset
        (SignatureCharset sc,
         byte[] bytes)
        throws Exception
    {
        testDecode(sc,bytes,COMBO);
        testDecode(sc,ArrayUtils.EMPTY_BYTE_ARRAY,StringUtils.EMPTY);
    }

    @Override
    protected void testStrategy
        (DecodingStrategy strategy,
         SignatureCharset sc,
         String string,
         byte[] bytes)
        throws Exception
    {
        testDecode
            (strategy,sc,bytes,string);
        testDecode
            (strategy,sc,ArrayUtils.EMPTY_BYTE_ARRAY,StringUtils.EMPTY);
        testDecode
            (strategy,SignatureCharset.UTF8_UTF8,
             ArrayUtils.addAll(Signature.UTF8.getMark(),COMBO_UTF8),
             COMBO);
        testDecode
            (strategy,SignatureCharset.UTF16BE_UTF16BE,
             ArrayUtils.addAll(Signature.UTF16BE.getMark(),COMBO_UTF16BE),
             COMBO);
        testDecode
            (strategy,SignatureCharset.UTF16LE_UTF16LE,
             ArrayUtils.addAll(Signature.UTF16LE.getMark(),COMBO_UTF16LE),
             COMBO);
        testDecode
            (strategy,SignatureCharset.UTF32BE_UTF32BE,
             ArrayUtils.addAll(Signature.UTF32BE.getMark(),COMBO_UTF32BE),
             COMBO);
        testDecode
            (strategy,SignatureCharset.UTF32LE_UTF32LE,
             ArrayUtils.addAll(Signature.UTF32LE.getMark(),COMBO_UTF32LE),
             COMBO);
    }
}
