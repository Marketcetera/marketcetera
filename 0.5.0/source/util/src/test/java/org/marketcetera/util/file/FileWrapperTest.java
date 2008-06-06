package org.marketcetera.util.file;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class FileWrapperTest
    extends WrapperTestBase
{
    @Test
    public void inputOutputStreams()
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            OutputStreamWrapper out=new OutputStreamWrapper(TEST_FILE);
            r.register(out);
            assertFalse(out.getSkipClose());
            assertNotNull(out.getStream());
            out.getStream().write(VALUE_BYTES);
        } finally {
            r.close();
        }

        r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper(TEST_FILE);
            r.register(in);
            assertFalse(in.getSkipClose());
            assertNotNull(in.getStream());
            assertArrayEquals(VALUE_BYTES,IOUtils.toByteArray(in.getStream()));
        } finally {
            r.close();
        }

        r=new CloseableRegistry();
        try {
            OutputStreamWrapper out=
                new OutputStreamWrapper(SpecialNames.PREFIX_APPEND+TEST_FILE);
            r.register(out);
            out.getStream().write(VALUE_BYTES);
        } finally {
            r.close();
        }
        r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper(TEST_FILE);
            r.register(in);
            assertArrayEquals
                (ArrayUtils.addAll(VALUE_BYTES,VALUE_BYTES),
                 IOUtils.toByteArray(in.getStream()));
        } finally {
            r.close();
        }
        
        r=new CloseableRegistry();
        try {
            OutputStreamWrapper out=new OutputStreamWrapper(TEST_FILE);
            r.register(out);
            out.getStream().write(VALUE_BYTES);
        } finally {
            r.close();
        }
        r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper(TEST_FILE);
            r.register(in);
            assertArrayEquals(VALUE_BYTES,IOUtils.toByteArray(in.getStream()));
        } finally {
            r.close();
        }
    }

    @Test
    public void readerWriter()
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            WriterWrapper out=new WriterWrapper(TEST_FILE);
            r.register(out);
            assertFalse(out.getSkipClose());
            assertNotNull(out.getWriter());
            out.getWriter().write(VALUE);
        } finally {
            r.close();
        }
        
        r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(TEST_FILE);
            r.register(in);
            assertFalse(in.getSkipClose());
            assertNotNull(in.getReader());
            assertEquals(VALUE,IOUtils.toString(in.getReader()));
        } finally {
            r.close();
        }

        r=new CloseableRegistry();
        try {
            WriterWrapper out=
                new WriterWrapper(SpecialNames.PREFIX_APPEND+TEST_FILE);
            r.register(out);
            out.getWriter().write(VALUE);
        } finally {
            r.close();
        }
        r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(TEST_FILE);
            r.register(in);
            assertEquals(VALUE+VALUE,IOUtils.toString(in.getReader()));
        } finally {
            r.close();
        }

        r=new CloseableRegistry();
        try {
            WriterWrapper out=new WriterWrapper(TEST_FILE);
            r.register(out);
            out.getWriter().write(VALUE);
        } finally {
            r.close();
        }
        r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(TEST_FILE);
            r.register(in);
            assertEquals(VALUE,IOUtils.toString(in.getReader()));
        } finally {
            r.close();
        }
    }
}
