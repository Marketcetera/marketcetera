package org.marketcetera.util.unicode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.file.InputStreamWrapper;
import org.marketcetera.util.file.ReaderWrapper;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class MemoryDecoderTest
    extends DecoderTestBase
{
    private static interface ReaderCreator
    {
        UnicodeInputStreamReader create
            (InputStream is);
    }


    private String decode
        (ReaderCreator creator,
         DecodingStrategy strategy,
         SignatureCharset requestedSignatureCharset,
         SignatureCharset signatureCharset,
         byte[] bytes)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayInputStream is=new ByteArrayInputStream(bytes);
            r.register(new InputStreamWrapper(is));
            UnicodeInputStreamReader reader=creator.create(is);
            r.register(new ReaderWrapper(reader));
            assertEquals(strategy,reader.getDecodingStrategy());
            assertEquals(requestedSignatureCharset,
                         reader.getRequestedSignatureCharset());
            assertEquals(signatureCharset,
                         reader.getSignatureCharset());
            return IOUtils.toString(reader);
        } finally {
            r.close();
        }
    }


    @Override
    protected String decode
        (byte[] bytes)
        throws Exception
    {
        return decode(new ReaderCreator()
            {
                @Override
                public UnicodeInputStreamReader create
                    (InputStream is)
                {
                    return new UnicodeInputStreamReader(is);
                }
            },null,null,null,bytes);
    }
    
    @Override
    protected String decode
        (final SignatureCharset sc,
         byte[] bytes)
        throws Exception
    {
        return decode(new ReaderCreator()
            {
                @Override
                public UnicodeInputStreamReader create
                    (InputStream is)
                {
                    return new UnicodeInputStreamReader(is,sc);
                }
            },null,sc,sc,bytes);
    }

    @Override
    protected String decode
        (final DecodingStrategy strategy,
         SignatureCharset sc,
         byte[] bytes)
        throws Exception
    {
        return decode(new ReaderCreator()
            {
                @Override
                public UnicodeInputStreamReader create
                    (InputStream is)
                {
                    return new UnicodeInputStreamReader(is,strategy);
                }
            },strategy,null,sc,bytes);
    }
}
