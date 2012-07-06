package org.marketcetera.core.util.unicode;

import org.junit.Ignore;
import org.marketcetera.core.util.file.CloseableRegistry;
import org.marketcetera.core.util.file.CopyBytesUtils;
import org.marketcetera.core.util.file.WriterWrapper;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id: FileEncoderTestBase.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

@Ignore
public abstract class FileEncoderTestBase
    extends EncoderTestBase
{
    protected static interface WriterCreator
    {
        UnicodeFileWriter create()
            throws Exception;
    }


    protected byte[] encode
        (WriterCreator creator,
         SignatureCharset requestedSignatureCharset,
         SignatureCharset signatureCharset,
         String string)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            UnicodeFileWriter writer=creator.create();
            r.register(new WriterWrapper(writer));
            assertEquals(requestedSignatureCharset,
                         writer.getRequestedSignatureCharset());
            assertEquals(signatureCharset,
                         writer.getSignatureCharset());
            writer.write(string);
        } finally {
            r.close();
        }
        return CopyBytesUtils.copy(TEST_FILE);
    }
}
