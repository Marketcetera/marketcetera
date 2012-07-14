package org.marketcetera.util.file;

import java.io.File;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.unicode.SignatureCharset;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

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
            OutputStreamWrapper out=new OutputStreamWrapper
                (new File(TEST_FILE));
            r.register(out);
            out.getStream().write(VALUE_BYTES);
        } finally {
            r.close();
        }
        r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper
                (new File(TEST_FILE));
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
            WriterWrapper out=new WriterWrapper(new File(TEST_FILE));
            r.register(out);
            assertFalse(out.getSkipClose());
            assertNotNull(out.getWriter());
            out.getWriter().write(VALUE);
        } finally {
            r.close();
        }
        
        r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper(new File(TEST_FILE));
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
            WriterWrapper out=new WriterWrapper
                (new File(TEST_FILE),SignatureCharset.UTF8_UTF8);
            r.register(out);
            out.getWriter().write(COMBO);
        } finally {
            r.close();
        }
        r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper
                (new File(TEST_FILE),DecodingStrategy.SIG_REQ);
            r.register(in);
            assertEquals(COMBO,IOUtils.toString(in.getReader()));
        } finally {
            r.close();
        }

        r=new CloseableRegistry();
        try {
            WriterWrapper out=new WriterWrapper
                (SpecialNames.PREFIX_APPEND+TEST_FILE,
                 SignatureCharset.UTF8_UTF8);
            r.register(out);
            out.getWriter().write(COMBO);
        } finally {
            r.close();
        }
        r=new CloseableRegistry();
        try {
            ReaderWrapper in=new ReaderWrapper
                (TEST_FILE,DecodingStrategy.SIG_REQ);
            r.register(in);
            assertEquals(COMBO+COMBO,IOUtils.toString(in.getReader()));
        } finally {
            r.close();
        }
    }
}
