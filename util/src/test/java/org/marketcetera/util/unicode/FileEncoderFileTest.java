package org.marketcetera.util.unicode;

import java.io.File;
import java.io.Reader;
import org.apache.commons.lang.ArrayUtils;
import org.marketcetera.util.file.CopyBytesUtils;
import org.marketcetera.util.file.Deleter;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class FileEncoderFileTest
    extends FileEncoderTestBase
{
    @Override
    protected byte[] encode
        (String string)
        throws Exception
    {
        return encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter(new File(TEST_FILE));
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
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter(new File(TEST_FILE),sc);
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
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter(new File(TEST_FILE),reader);
                }
            },sc,sc,string);
    }


    @Override
    protected void testEncode
        (byte[] bytes,
         String string)
        throws Exception
    {
        super.testEncode(bytes,string);

        int halfLength=string.length()/2;
        String firstPart=string.substring(0,halfLength);
        String secondPart=string.substring(halfLength);
        encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),false);
                }
            },null,null,firstPart);
        assertArrayEquals(bytes,encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),true);
                }
            },null,null,secondPart));

        Deleter.apply(TEST_FILE);
        CopyBytesUtils.copy(ArrayUtils.EMPTY_BYTE_ARRAY,TEST_FILE);
        assertArrayEquals(bytes,encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),true);
                }
            },null,null,string));
    }

    @Override
    protected void testEncode
        (final SignatureCharset sc,
         byte[] bytes,
         String string)
        throws Exception
    {
        super.testEncode(sc,bytes,string);

        int halfLength=string.length()/2;
        String firstPart=string.substring(0,halfLength);
        String secondPart=string.substring(halfLength);
        encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),false,sc);
                }
            },sc,sc,firstPart);
        assertArrayEquals(bytes,encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),true,sc);
                }
            },sc,sc,secondPart));

        Deleter.apply(TEST_FILE);
        CopyBytesUtils.copy(ArrayUtils.EMPTY_BYTE_ARRAY,TEST_FILE);
        assertArrayEquals(bytes,encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),true,sc);
                }
            },sc,sc,string));
    }

    @Override
    protected void testEncode
        (final Reader reader,
         SignatureCharset sc,
         byte[] bytes,
         String string)
        throws Exception
    {
        super.testEncode(reader,sc,bytes,string);

        int halfLength=string.length()/2;
        String firstPart=string.substring(0,halfLength);
        String secondPart=string.substring(halfLength);
        encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),false,reader);
                }
            },sc,sc,firstPart);
        assertArrayEquals(bytes,encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),true,reader);
                }
            },sc,sc,secondPart));

        Deleter.apply(TEST_FILE);
        CopyBytesUtils.copy(ArrayUtils.EMPTY_BYTE_ARRAY,TEST_FILE);
        assertArrayEquals(bytes,encode(new WriterCreator()
            {
                @Override
                public UnicodeFileWriter create()
                    throws Exception
                {
                    return new UnicodeFileWriter
                        (new File(TEST_FILE),true,reader);
                }
            },sc,sc,string));
    }
}
