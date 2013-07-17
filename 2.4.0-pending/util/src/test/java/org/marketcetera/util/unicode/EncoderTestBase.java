package org.marketcetera.util.unicode;

import java.io.ByteArrayInputStream;
import java.io.Reader;
import org.junit.Ignore;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.file.InputStreamWrapper;
import org.marketcetera.util.file.ReaderWrapper;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

@Ignore
public abstract class EncoderTestBase
    extends IOTestBase
{
    protected abstract byte[] encode
        (String string)
        throws Exception;

    protected void testEncode
        (byte[] bytes,
         String string)
        throws Exception
    {
        assertArrayEquals(bytes,encode(string));
    }

    protected abstract byte[] encode
        (SignatureCharset sc,
         String string)
        throws Exception;

    protected void testEncode
        (SignatureCharset sc,
         byte[] bytes,
         String string)
        throws Exception
    {
        assertArrayEquals(bytes,encode(sc,string));
    }

    protected abstract byte[] encode
        (Reader reader,
         SignatureCharset sc,
         String string)
        throws Exception;

    protected void testEncode
        (Reader reader,
         SignatureCharset sc,
         byte[] bytes,
         String string)
        throws Exception
    {
        assertArrayEquals(bytes,encode(reader,sc,string));
    }


    @Override
    protected void testNative()
        throws Exception
    {
        testEncode(HELLO_EN_NAT,HELLO_EN);
        testEncode(null,HELLO_EN_NAT,HELLO_EN);
        testEncode(null,null,HELLO_EN_NAT,HELLO_EN);
    }

    @Override
    protected void testSignatureCharset
        (SignatureCharset sc,
         byte[] bytes)
        throws Exception
    {
        testEncode(sc,bytes,COMBO);
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayInputStream is=new ByteArrayInputStream(bytes);
            r.register(new InputStreamWrapper(is));
            UnicodeInputStreamReader reader=
                new UnicodeInputStreamReader(is,sc);
            r.register(new ReaderWrapper(reader));
            testEncode(reader,sc,bytes,COMBO);
        } finally {
            r.close();
        }
    }

    @Override
    protected void testStrategy
        (DecodingStrategy strategy,
         SignatureCharset sc,
         String string,
         byte[] bytes)
        throws Exception {}
}
