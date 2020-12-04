package org.marketcetera.util.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.unicode.SignatureCharset;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class CopyCharsUnicodeUtilsTest
    extends CopyUtilsTestBase
{
    private static final char[] VALUE=
        COMBO_CHARS;


    private static void copyReader
        (String out)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper
                (TEST_INPUT_FILE,DecodingStrategy.SIG_REQ);
            r.register(in);
            assertEquals(VALUE.length,
                         CopyCharsUnicodeUtils.copy(in.getReader(),true,out));
        } finally {
            r.close();
        }
    }

    private static void copyWriter
        (String in)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            WriterWrapper out=new WriterWrapper
                (TEST_OUTPUT_FILE,SignatureCharset.UTF8_UTF8);
            r.register(out);
            assertEquals(VALUE.length,
                         CopyCharsUnicodeUtils.copy
                         (in,DecodingStrategy.SIG_REQ,out.getWriter(),true));
        } finally {
            r.close();
        }
    }


    @Before
    @After
    public void setupTearDownCopyCharsUnicodeUtilsTest()
        throws Exception
    {
        Deleter.apply(TEST_INPUT_FILE);
        Deleter.apply(TEST_OUTPUT_FILE);
    }


    @Test
    public void copyMemory()
        throws Exception
    {
        CopyCharsUnicodeUtils.copy
            (VALUE,TEST_INPUT_FILE,SignatureCharset.UTF8_UTF8);
        assertArrayEquals(VALUE,CopyCharsUnicodeUtils.copy
                          (TEST_INPUT_FILE,DecodingStrategy.SIG_REQ));
    }

    @Test
    public void copyReader()
        throws Exception
    {
        CopyCharsUnicodeUtils.copy
            (VALUE,TEST_INPUT_FILE,SignatureCharset.UTF8_UTF8);
        copyReader(TEST_OUTPUT_FILE);
        assertArrayEquals(VALUE,CopyCharsUnicodeUtils.copy
                          (TEST_OUTPUT_FILE,DecodingStrategy.SIG_REQ));
    }

    @Test
    public void copyWriter()
        throws Exception
    {
        CopyCharsUnicodeUtils.copy
            (VALUE,TEST_INPUT_FILE,SignatureCharset.UTF8_UTF8);
        copyWriter(TEST_INPUT_FILE);
        assertArrayEquals(VALUE,CopyCharsUnicodeUtils.copy
                          (TEST_OUTPUT_FILE,DecodingStrategy.SIG_REQ));
    }

    @Test
    public void copyFiles()
        throws Exception
    {
        CopyCharsUnicodeUtils.copy
            (VALUE,TEST_INPUT_FILE,SignatureCharset.UTF8_UTF8);
        assertEquals(VALUE.length,
                     CopyCharsUnicodeUtils.copy
                     (TEST_INPUT_FILE,DecodingStrategy.SIG_REQ,
                      TEST_OUTPUT_FILE));
        assertArrayEquals(VALUE,CopyCharsUnicodeUtils.copy
                          (TEST_OUTPUT_FILE,DecodingStrategy.SIG_REQ));
    }


    @Test
    public void failFileInput()
    {
        try {
            CopyCharsUnicodeUtils.copy
                (TEST_NONEXISTENT_FILE,DecodingStrategy.SIG_REQ,
                 TEST_OUTPUT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage2P(Messages.CANNOT_COPY_FILES,
                                        TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failFileOutput()
        throws Exception
    {
        CopyCharsUnicodeUtils.copy
            (VALUE,TEST_INPUT_FILE,SignatureCharset.UTF8_UTF8);
        try {
            CopyCharsUnicodeUtils.copy
                (TEST_INPUT_FILE,DecodingStrategy.SIG_REQ,
                 TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage2P(Messages.CANNOT_COPY_FILES,
                                        TEST_INPUT_FILE,TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failReader()
        throws Exception
    {
        CopyCharsUnicodeUtils.copy
            (VALUE,TEST_INPUT_FILE,SignatureCharset.UTF8_UTF8);
        try {
            copyReader(TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_READER,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failWriter()
        throws Exception
    {
        try {
            copyWriter(TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_WRITER,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failMemoryDst()
    {
        try {
            CopyCharsUnicodeUtils.copy
                (TEST_NONEXISTENT_FILE,DecodingStrategy.SIG_REQ);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_MEMORY_DST,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failMemorySrc()
    {
        try {
            CopyCharsUnicodeUtils.copy
                (VALUE,TEST_NONEXISTENT_FILE,SignatureCharset.UTF8_UTF8);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_MEMORY_SRC,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void closeCalled()
        throws Exception
    {
        CloseSetReader in=new CloseSetReader();
        CopyCharsUnicodeUtils.copy(in,true,TEST_INPUT_FILE);
        assertFalse(in.getClosed());

        in=new CloseSetReader();
        CopyCharsUnicodeUtils.copy(in,false,TEST_INPUT_FILE);
        assertTrue(in.getClosed());

        CloseSetWriter out=new CloseSetWriter();
        CopyCharsUnicodeUtils.copy
            (TEST_INPUT_FILE,DecodingStrategy.SIG_REQ,out,true);
        assertFalse(out.getClosed());

        out=new CloseSetWriter();
        CopyCharsUnicodeUtils.copy
            (TEST_INPUT_FILE,DecodingStrategy.SIG_REQ,out,false);
        assertTrue(out.getClosed());
    }
}
