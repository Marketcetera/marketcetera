package org.marketcetera.util.file;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class CopyUtilsTest
    extends TestCaseBase
{
    private static final String TEST_ROOT=
        DIR_ROOT+File.separator+"copy_utils"+File.separator;
    private static final String TEST_INPUT_FILE=
        TEST_ROOT+"input_file";
    private static final String TEST_OUTPUT_FILE=
        TEST_ROOT+"output_file";
    protected static final String TEST_NONEXISTENT_FILE=
        TEST_ROOT+"nonexistent"+File.separator+"nonexistent";
    private static final String VALUE=
        "marketcetera";
    private static final char[] VALUE_CHARS=
        VALUE.toCharArray();
    private static final byte[] VALUE_BYTES=
        VALUE.getBytes();


    private static void copyIStream
        (String out)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper(TEST_INPUT_FILE);
            r.register(in);
            assertEquals(VALUE_BYTES.length,
                         CopyUtils.copyBytes(in.getStream(),true,out));
        } finally {
            r.close();
        }
    }

    private static void copyOStream
        (String in)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            OutputStreamWrapper out=new OutputStreamWrapper(TEST_OUTPUT_FILE);
            r.register(out);
            assertEquals(VALUE_BYTES.length,
                         CopyUtils.copyBytes(in,out.getStream(),true));
        } finally {
            r.close();
        }
    }

    private static void copyReader
        (String out)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(TEST_INPUT_FILE);
            r.register(in);
            assertEquals(VALUE_CHARS.length,
                         CopyUtils.copyChars(in.getReader(),true,out));
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
            assertEquals(VALUE_CHARS.length,
                         CopyUtils.copyChars(in,out.getWriter(),true));
        } finally {
            r.close();
        }
    }


    @Before
    @After
    public void setupTearDownCopyUtilsTest()
        throws Exception
    {
        Deleter.apply(TEST_INPUT_FILE);
        Deleter.apply(TEST_OUTPUT_FILE);
    }


    @Test
    public void copyBytesMemory()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        assertArrayEquals(VALUE_BYTES,CopyUtils.copyBytes(TEST_INPUT_FILE));
    }

    @Test
    public void copyBytesInputStream()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        copyIStream(TEST_OUTPUT_FILE);
        assertArrayEquals(VALUE_BYTES,CopyUtils.copyBytes(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyBytesOutputStream()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        copyOStream(TEST_INPUT_FILE);
        assertArrayEquals(VALUE_BYTES,CopyUtils.copyBytes(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyBytesFiles()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        assertEquals(VALUE_BYTES.length,
                     CopyUtils.copyBytes(TEST_INPUT_FILE,TEST_OUTPUT_FILE));
        assertArrayEquals(VALUE_BYTES,CopyUtils.copyBytes(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyCharsMemory()
        throws Exception
    {
        CopyUtils.copyChars(VALUE_CHARS,TEST_INPUT_FILE);
        assertArrayEquals(VALUE_CHARS,CopyUtils.copyChars(TEST_INPUT_FILE));
    }

    @Test
    public void copyCharsReader()
        throws Exception
    {
        CopyUtils.copyChars(VALUE_CHARS,TEST_INPUT_FILE);
        copyReader(TEST_OUTPUT_FILE);
        assertArrayEquals(VALUE_CHARS,CopyUtils.copyChars(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyCharsWriter()
        throws Exception
    {
        CopyUtils.copyChars(VALUE_CHARS,TEST_INPUT_FILE);
        copyWriter(TEST_INPUT_FILE);
        assertArrayEquals(VALUE_CHARS,CopyUtils.copyChars(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyCharsFiles()
        throws Exception
    {
        CopyUtils.copyChars(VALUE_CHARS,TEST_INPUT_FILE);
        assertEquals(VALUE_CHARS.length,
                     CopyUtils.copyChars(TEST_INPUT_FILE,TEST_OUTPUT_FILE));
        assertArrayEquals(VALUE_CHARS,CopyUtils.copyChars(TEST_OUTPUT_FILE));
    }

    @Test
    public void failFileBytesInput()
    {
        try {
            CopyUtils.copyBytes(TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage2P m=(I18NBoundMessage2P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            assertEquals(TEST_OUTPUT_FILE,m.getParam2());
            return;
        }
        fail();
    }

    @Test
    public void failFileBytesOutput()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            CopyUtils.copyBytes(TEST_INPUT_FILE,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage2P m=(I18NBoundMessage2P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,m.getMessage());
            assertEquals(TEST_INPUT_FILE,m.getParam1());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam2());
            return;
        }
        fail();
    }

    @Test
    public void failFileCharsInput()
    {
        try {
            CopyUtils.copyChars(TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage2P m=(I18NBoundMessage2P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            assertEquals(TEST_OUTPUT_FILE,m.getParam2());
            return;
        }
        fail();
    }

    @Test
    public void failFileCharsOutput()
        throws Exception
    {
        CopyUtils.copyChars(VALUE_CHARS,TEST_INPUT_FILE);
        try {
            CopyUtils.copyChars(TEST_INPUT_FILE,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage2P m=(I18NBoundMessage2P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,m.getMessage());
            assertEquals(TEST_INPUT_FILE,m.getParam1());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam2());
            return;
        }
        fail();
    }

    @Test
    public void failIStream()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyIStream(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_ISTREAM,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }

    @Test
    public void failOStream()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyOStream(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_OSTREAM,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }

    @Test
    public void failReader()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyReader(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_READER,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }

    @Test
    public void failWriter()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyWriter(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_WRITER,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }

    @Test
    public void failMemoryDstBytes()
    {
        try {
            CopyUtils.copyBytes(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_MEMORY_DST,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }

    @Test
    public void failMemoryDstChars()
    {
        try {
            CopyUtils.copyChars(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_MEMORY_DST,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }

    @Test
    public void failMemorySrcBytes()
    {
        try {
            CopyUtils.copyBytes(VALUE_BYTES,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_MEMORY_SRC,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }

    @Test
    public void failMemorySrcChars()
    {
        try {
            CopyUtils.copyChars(VALUE_CHARS,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_MEMORY_SRC,m.getMessage());
            assertEquals(TEST_NONEXISTENT_FILE,m.getParam1());
            return;
        }
        fail();
    }
}
