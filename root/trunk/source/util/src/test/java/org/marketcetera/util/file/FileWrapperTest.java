package org.marketcetera.util.file;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class FileWrapperTest
	extends WrapperTestBase
{
    @Test
    public void inputOutputStreams()
        throws Exception
    {
        OutputStreamWrapper out=new OutputStreamWrapper(TEST_FILE);
        try {
            assertFalse(out.getSkipClose());
            assertNotNull(out.getStream());
            out.getStream().write(VALUE_BYTES);
        } finally {
            out.close();
        }

        InputStreamWrapper in=new InputStreamWrapper(TEST_FILE);
        try {
            assertFalse(in.getSkipClose());
            assertNotNull(in.getStream());
            assertArrayEquals(VALUE_BYTES,IOUtils.toByteArray(in.getStream()));
        } finally {
            in.close();
        }

        out=new OutputStreamWrapper(SpecialNames.PREFIX_APPEND+TEST_FILE);
        try {
            out.getStream().write(VALUE_BYTES);
        } finally {
            out.close();
        }
        in=new InputStreamWrapper(TEST_FILE);
        try {
            assertArrayEquals
                (ArrayUtils.addAll(VALUE_BYTES,VALUE_BYTES),
                 IOUtils.toByteArray(in.getStream()));
        } finally {
            in.close();
        }
        
        out=new OutputStreamWrapper(TEST_FILE);
        try {
            out.getStream().write(VALUE_BYTES);
        } finally {
            out.close();
        }
        in=new InputStreamWrapper(TEST_FILE);
        try {
            assertArrayEquals(VALUE_BYTES,IOUtils.toByteArray(in.getStream()));
        } finally {
            in.close();
        }
    }

    @Test
    public void readerWriter()
        throws Exception
    {
        WriterWrapper out=new WriterWrapper(TEST_FILE);
        try {
            assertFalse(out.getSkipClose());
            assertNotNull(out.getWriter());
            out.getWriter().write(VALUE);
        } finally {
            out.close();
        }
        
        ReaderWrapper in=new ReaderWrapper(TEST_FILE);
        try {
            assertFalse(in.getSkipClose());
            assertNotNull(in.getReader());
            assertEquals(VALUE,IOUtils.toString(in.getReader()));
        } finally {
            in.close();
        }

        out=new WriterWrapper(SpecialNames.PREFIX_APPEND+TEST_FILE);
        try {
            out.getWriter().write(VALUE);
        } finally {
            out.close();
        }
        in=new ReaderWrapper(TEST_FILE);
        try {
            assertEquals(VALUE+VALUE,IOUtils.toString(in.getReader()));
        } finally {
            in.close();
        }

        out=new WriterWrapper(TEST_FILE);
        try {
            out.getWriter().write(VALUE);
        } finally {
            out.close();
        }
        in=new ReaderWrapper(TEST_FILE);
        try {
            assertEquals(VALUE,IOUtils.toString(in.getReader()));
        } finally {
            in.close();
        }
    }
}
