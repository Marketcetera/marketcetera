package org.marketcetera.core.util.unicode;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.marketcetera.core.util.file.CloseableRegistry;
import org.marketcetera.core.util.file.CopyBytesUtils;
import org.marketcetera.core.util.file.ReaderWrapper;

import static org.junit.Assert.*;

/**
 * @since 0.6.0
 * @version $Id: FileDecoderTestBase.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@Ignore
public abstract class FileDecoderTestBase
    extends DecoderTestBase
{
    protected static interface ReaderCreator
    {
        UnicodeFileReader create()
            throws Exception;
    }


    protected String decode
        (ReaderCreator creator,
         DecodingStrategy strategy,
         SignatureCharset requestedSignatureCharset,
         SignatureCharset signatureCharset,
         byte[] bytes)
        throws Exception
    {
        CopyBytesUtils.copy(bytes,TEST_FILE);
        CloseableRegistry r=new CloseableRegistry();
        try {
            UnicodeFileReader reader=creator.create();
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
}

