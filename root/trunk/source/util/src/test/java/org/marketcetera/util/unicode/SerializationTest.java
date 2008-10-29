package org.marketcetera.util.unicode;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;
import static org.marketcetera.util.unicode.Serialization.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class SerializationTest
    extends TestCaseBase
{
    private static final Serialization[] NO_SERIALIZATIONS=
        new Serialization[0];

    private static void common
        (Serialization serialization,
         byte[] data,
         SignatureCharset sc)
        throws Exception
    {
        data=ArrayUtils.addAll(sc.getSignature().getMark(),data);
        Serialization[] candidates=new Serialization[] {serialization};
        assertEquals(sc,getPrefixMatch(candidates,data));
        assertEquals(COMBO,decode(candidates,data));
    }

    private static void single
        (Serialization serialization,
         int count,
         byte[] data,
         SignatureCharset sc)
        throws Exception
    {
        Serialization[] candidates=new Serialization[] {serialization};

        assertNotNull(serialization.getSignatureCharsets());
        assertEquals(count,serialization.getSignatureCharsets().length);

        assertNull(decode(candidates,null));

        common(serialization,data,sc);
        assertEquals((sc.getSignature()==Signature.NONE)?sc:null,
                     getPrefixMatch(candidates,new byte[] {(byte)0x01}));
    }

    private static void multi
        (Serialization serialization,
         byte[] data1,
         SignatureCharset sc1,
         byte[] data2,
         SignatureCharset sc2,
         byte[] data3,
         SignatureCharset sc3)
        throws Exception
    {
        Serialization[] candidates=new Serialization[] {serialization};

        assertNotNull(serialization.getSignatureCharsets());
        assertEquals(3,serialization.getSignatureCharsets().length);

        assertNull(decode(candidates,null));

        common(serialization,data1,sc1);
        common(serialization,data2,sc2);
        common(serialization,data3,sc3);
        assertEquals(serialization.getSignatureCharsets()[0],
                     getPrefixMatch(candidates,new byte[] {(byte)0x01}));
    }


    @Test
    public void all()
        throws Exception
    {
        single(UTF8,1,COMBO_UTF8,SignatureCharset.UTF8_UTF8);
        single(UTF8N,1,COMBO_UTF8,SignatureCharset.NONE_UTF8);
        single(UTF16BE,1,COMBO_UTF16BE,SignatureCharset.NONE_UTF16BE);
        single(UTF16LE,1,COMBO_UTF16LE,SignatureCharset.NONE_UTF16LE);
        single(UTF32BE,1,COMBO_UTF32BE,SignatureCharset.NONE_UTF32BE);
        single(UTF32LE,1,COMBO_UTF32LE,SignatureCharset.NONE_UTF32LE);

        single(UTF16BE_REQ,1,
               COMBO_UTF16BE,SignatureCharset.UTF16BE_UTF16BE);
        single(UTF16LE_REQ,1,
               COMBO_UTF16LE,SignatureCharset.UTF16LE_UTF16LE);
        single(UTF32BE_REQ,1,
               COMBO_UTF32BE,SignatureCharset.UTF32BE_UTF32BE);
        single(UTF32LE_REQ,1,
               COMBO_UTF32LE,SignatureCharset.UTF32LE_UTF32LE);

        multi(UTF16,
              COMBO_UTF16BE,SignatureCharset.NONE_UTF16BE,
              COMBO_UTF16BE,SignatureCharset.UTF16BE_UTF16BE,
              COMBO_UTF16LE,SignatureCharset.UTF16LE_UTF16LE);
        multi(UTF32,
              COMBO_UTF32BE,SignatureCharset.NONE_UTF32BE,
              COMBO_UTF32BE,SignatureCharset.UTF32BE_UTF32BE,
              COMBO_UTF32LE,SignatureCharset.UTF32LE_UTF32LE);
    }

    @Test
    public void multiMatch()
        throws Exception
    {
        byte[] data=new byte[] {(byte)0x01};

        assertNull(getPrefixMatch(NO_SERIALIZATIONS,data));
        try {
            decode(NO_SERIALIZATIONS,data);
            fail();
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),Messages.NO_SIGNATURE_MATCHES,
                         ex.getI18NBoundMessage());
        }

        assertEquals(SignatureCharset.NONE_UTF16BE,getPrefixMatch
                     (new Serialization[] {UTF16,UTF32},data));
        assertEquals(SignatureCharset.NONE_UTF32BE,getPrefixMatch
                     (new Serialization[] {UTF32,UTF16},data));

        assertEquals(SignatureCharset.UTF32LE_UTF32LE,getPrefixMatch
                     (new Serialization[] {UTF16,UTF32},
                      Signature.UTF32LE.getMark()));
        assertEquals(SignatureCharset.UTF32LE_UTF32LE,getPrefixMatch
                     (new Serialization[] {UTF32,UTF16},
                      Signature.UTF32LE.getMark()));
    }
}
