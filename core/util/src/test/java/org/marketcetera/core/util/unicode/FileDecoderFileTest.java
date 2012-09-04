package org.marketcetera.core.util.unicode;

import java.io.File;

/**
 * @since 0.6.0
 * @version $Id: FileDecoderFileTest.java 16063 2012-01-31 18:21:55Z colin $
 */

/* $License$ */

public class FileDecoderFileTest
    extends FileDecoderTestBase
{
    @Override
    protected String decode
        (byte[] bytes)
        throws Exception
    {
        return decode(new ReaderCreator()
            {
                @Override
                public UnicodeFileReader create()
                    throws Exception
                {
                    return new UnicodeFileReader(new File(TEST_FILE));
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
                public UnicodeFileReader create()
                    throws Exception
                {
                    return new UnicodeFileReader(new File(TEST_FILE),sc);
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
                public UnicodeFileReader create()
                    throws Exception
                {
                    return new UnicodeFileReader(new File(TEST_FILE),strategy);
                }
            },strategy,null,sc,bytes);
    }
}
