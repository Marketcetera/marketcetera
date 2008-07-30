package org.marketcetera.util.unicode;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.file.OutputStreamWrapper;
import org.marketcetera.util.file.WriterWrapper;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class MemoryEncoderTest
    extends EncoderTestBase
{
    private static interface WriterCreator
    {
        UnicodeOutputStreamWriter create
            (OutputStream os)
            throws Exception;
    }


    private byte[] encode
        (WriterCreator creator,
         SignatureCharset requestedSignatureCharset,
         SignatureCharset signatureCharset,
         String string)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        ByteArrayOutputStream os=new ByteArrayOutputStream();
        try {
            r.register(new OutputStreamWrapper(os));
            UnicodeOutputStreamWriter writer=creator.create(os);
            r.register(new WriterWrapper(writer));
            assertEquals(requestedSignatureCharset,
                         writer.getRequestedSignatureCharset());
            assertEquals(signatureCharset,
                         writer.getSignatureCharset());
            writer.write(string);
        } finally {
            r.close();
        }
        return os.toByteArray();
    }


    @Override
    protected byte[] encode
        (String string)
        throws Exception
    {
        return encode(new WriterCreator()
            {
                @Override
                public UnicodeOutputStreamWriter create
                    (OutputStream os)
                {
                    return new UnicodeOutputStreamWriter(os);
                }
            },null,null,string);
    }

    @Override
    protected byte[] encode
        (final SignatureCharset sc,
         String string)
        throws Exception
    {
        return encode(new WriterCreator()
            {
                @Override
                public UnicodeOutputStreamWriter create
                    (OutputStream os)
                {
                    return new UnicodeOutputStreamWriter(os,sc);
                }
            },sc,sc,string);
    }

    @Override
    protected byte[] encode
        (final Reader reader,
         SignatureCharset sc,
         String string)
        throws Exception
    {
        return encode(new WriterCreator()
            {
                @Override
                public UnicodeOutputStreamWriter create
                    (OutputStream os)
                    throws Exception
                {
                    return new UnicodeOutputStreamWriter(os,reader);
                }
            },sc,sc,string);
    }
}
