package org.marketcetera.util.file;

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
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
        InputStreamWrapper in=new InputStreamWrapper(TEST_INPUT_FILE);
        try {
            assertEquals(VALUE_BYTES.length,
                         CopyUtils.copyBytes(in.getStream(),true,out));
        } finally {
            in.close();
        }
    }

    private static void copyOStream
        (String in)
        throws Exception
    {
        OutputStreamWrapper out=new OutputStreamWrapper(TEST_OUTPUT_FILE);
        try {
            assertEquals(VALUE_BYTES.length,
                         CopyUtils.copyBytes(in,out.getStream(),true));
        } finally {
            out.close();
        }
    }

    private static void copyReader
        (String out)
        throws Exception
    {
        ReaderWrapper in=new ReaderWrapper(TEST_INPUT_FILE);
        try {
            assertEquals(VALUE_CHARS.length,
                         CopyUtils.copyChars(in.getReader(),true,out));
        } finally {
            in.close();
        }
    }

    private static void copyWriter
        (String in)
        throws Exception
    {
        WriterWrapper out=new WriterWrapper(TEST_OUTPUT_FILE);
        try {
            assertEquals(VALUE_CHARS.length,
                         CopyUtils.copyChars(in,out.getWriter(),true));
        } finally {
            out.close();
        }
    }


    @Before
    @After
    public void deleteTestFile()
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
	public void failFileBytes()
        throws Exception
    {
        try {
            CopyUtils.copyBytes(TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,
                 ex.getI18NMessage());
        }
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            CopyUtils.copyBytes(TEST_INPUT_FILE,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,
                 ex.getI18NMessage());
        }
    }

    @Test
	public void failFileChars()
        throws Exception
    {
        try {
            CopyUtils.copyChars(TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,
                 ex.getI18NMessage());
        }
        CopyUtils.copyChars(VALUE_CHARS,TEST_INPUT_FILE);
        try {
            CopyUtils.copyChars(TEST_INPUT_FILE,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_FILES,
                 ex.getI18NMessage());
        }
    }

    @Test
	public void failIStream()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyIStream(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_ISTREAM,
                 ex.getI18NMessage());
        }
    }

    @Test
	public void failOStream()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyOStream(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_OSTREAM,
                 ex.getI18NMessage());
        }
    }

    @Test
	public void failReader()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyReader(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_READER,
                 ex.getI18NMessage());
        }
    }

    @Test
	public void failWriter()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        try {
            copyWriter(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),Messages.CANNOT_COPY_WRITER,
                 ex.getI18NMessage());
        }
    }

    @Test
	public void failMemoryDstBytes()
    {
        try {
            CopyUtils.copyBytes(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),Messages.CANNOT_COPY_MEMORY_DST,
                         ex.getI18NMessage());
        }
    }

    @Test
	public void failMemoryDstChars()
    {
        try {
            CopyUtils.copyChars(TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),Messages.CANNOT_COPY_MEMORY_DST,
                         ex.getI18NMessage());
        }
    }

    @Test
	public void failMemorySrcBytes()
    {
        try {
            CopyUtils.copyBytes(VALUE_BYTES,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),Messages.CANNOT_COPY_MEMORY_SRC,
                         ex.getI18NMessage());
        }
    }

    @Test
	public void failMemorySrcChars()
    {
        try {
            CopyUtils.copyChars(VALUE_CHARS,TEST_NONEXISTENT_FILE);
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),Messages.CANNOT_COPY_MEMORY_SRC,
                         ex.getI18NMessage());
        }
    }
}
