package org.marketcetera.util.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class CopyCharsUtilsTest
    extends CopyUtilsTestBase
{
    private static final char[] VALUE=
        HELLO_EN_CHARS;


    private static void copyReader
        (String out)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(TEST_INPUT_FILE);
            r.register(in);
            assertEquals(VALUE.length,
                         CopyCharsUtils.copy(in.getReader(),true,out));
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
            WriterWrapper out=new WriterWrapper(TEST_OUTPUT_FILE);
            r.register(out);
            assertEquals(VALUE.length,
                         CopyCharsUtils.copy(in,out.getWriter(),true));
        } finally {
            r.close();
        }
    }


    @Before
    @After
    public void setupTearDownCopyCharsUtilsTest()
        throws Exception
    {
        Deleter.apply(TEST_INPUT_FILE);
        Deleter.apply(TEST_OUTPUT_FILE);
    }


    @Test
    public void copyReaderWriter()
        throws Exception
    {
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
        CloseableRegistry r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(TEST_INPUT_FILE);
            r.register(in);
            WriterWrapper out=new WriterWrapper(TEST_OUTPUT_FILE);
            r.register(out);
            assertEquals(VALUE.length,
                         CopyCharsUtils.copy(in.getReader(),true,
                                             out.getWriter(),true));
        } finally {
            r.close();
        }
        assertArrayEquals(VALUE,CopyCharsUtils.copy(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyMemory()
        throws Exception
    {
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
        assertArrayEquals(VALUE,CopyCharsUtils.copy(TEST_INPUT_FILE));
    }

    @Test
    public void copyReader()
        throws Exception
    {
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
        copyReader(TEST_OUTPUT_FILE);
        assertArrayEquals(VALUE,CopyCharsUtils.copy(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyWriter()
        throws Exception
    {
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
        copyWriter(TEST_INPUT_FILE);
        assertArrayEquals(VALUE,CopyCharsUtils.copy(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyFiles()
        throws Exception
    {
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
        assertEquals(VALUE.length,
                     CopyCharsUtils.copy(TEST_INPUT_FILE,TEST_OUTPUT_FILE));
        assertArrayEquals(VALUE,CopyCharsUtils.copy(TEST_OUTPUT_FILE));
    }


    @Test
    public void failReaderWriter()
        throws Exception
    {
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
        CloseableRegistry r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(TEST_INPUT_FILE);
            r.register(in);
            WriterWrapper out=new WriterWrapper(TEST_OUTPUT_FILE);
            r.register(out);
            out.getWriter().close();
            CopyCharsUtils.copy(in.getReader(),true,out.getWriter(),true);
            fail();
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),Messages.CANNOT_COPY_CSTREAMS,
                         ex.getI18NBoundMessage());
        } finally {
            r.close();
        }
    }

    @Test
    public void failFileInput()
    {
        try {
            CopyCharsUtils.copy(TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE);
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
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
        try {
            CopyCharsUtils.copy(TEST_INPUT_FILE,TEST_NONEXISTENT_FILE);
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
        CopyCharsUtils.copy(VALUE,TEST_INPUT_FILE);
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
            CopyCharsUtils.copy(TEST_NONEXISTENT_FILE);
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
            CopyCharsUtils.copy(VALUE,TEST_NONEXISTENT_FILE);
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
        CloseSetWriter out=new CloseSetWriter();
        CopyCharsUtils.copy(in,true,out,true);
        assertFalse(in.getClosed());
        assertFalse(out.getClosed());

        in=new CloseSetReader();
        out=new CloseSetWriter();
        CopyCharsUtils.copy(in,true,out,false);
        assertFalse(in.getClosed());
        assertTrue(out.getClosed());

        in=new CloseSetReader();
        out=new CloseSetWriter();
        CopyCharsUtils.copy(in,false,out,true);
        assertTrue(in.getClosed());
        assertFalse(out.getClosed());

        in=new CloseSetReader();
        out=new CloseSetWriter();
        CopyCharsUtils.copy(in,false,out,false);
        assertTrue(in.getClosed());
        assertTrue(out.getClosed());

        in=new CloseSetReader();
        CopyCharsUtils.copy(in,true,TEST_INPUT_FILE);
        assertFalse(in.getClosed());

        in=new CloseSetReader();
        CopyCharsUtils.copy(in,false,TEST_INPUT_FILE);
        assertTrue(in.getClosed());

        out=new CloseSetWriter();
        CopyCharsUtils.copy(TEST_INPUT_FILE,out,true);
        assertFalse(out.getClosed());

        out=new CloseSetWriter();
        CopyCharsUtils.copy(TEST_INPUT_FILE,out,false);
        assertTrue(out.getClosed());
    }
}
