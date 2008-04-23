package org.marketcetera.util.file;

import java.io.File;
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


    @Before
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
	public void copyBytesFiles()
        throws Exception
    {
        CopyUtils.copyBytes(VALUE_BYTES,TEST_INPUT_FILE);
        CopyUtils.copyBytes(TEST_INPUT_FILE,TEST_OUTPUT_FILE);
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
	public void copyCharsFiles()
        throws Exception
    {
        CopyUtils.copyChars(VALUE_CHARS,TEST_INPUT_FILE);
        CopyUtils.copyChars(TEST_INPUT_FILE,TEST_OUTPUT_FILE);
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
