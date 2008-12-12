package org.marketcetera.util.file;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.unicode.DecodingStrategy;
import org.marketcetera.util.unicode.Signature;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class ReaderWrapperTest
    extends WrapperTestBase
{
    private static final class CloseCounterReader
        extends Reader
    {
        private int mClosures=0;

        int getClosures()
        {
            return mClosures;
        }

        @Override
        public int read
            (char[] cbuf,
             int off,
             int len) 
        {
            return 0;
        }

        @Override
        public void close()
        {
            mClosures++;
        }
    }

    @Test
    public void basics()
        throws Exception
    {
        CloseCounterReader counter=new CloseCounterReader();
        ReaderWrapper out=new ReaderWrapper(counter);
        assertEquals(0,counter.getClosures());
        assertFalse(out.getSkipClose());
        assertNotNull(out.getReader());
        out.close();
        assertEquals(1,counter.getClosures());
        out.close();
        assertEquals(1,counter.getClosures());

        counter=new CloseCounterReader();
        out=new ReaderWrapper(counter,true);
        assertEquals(0,counter.getClosures());
        assertTrue(out.getSkipClose());
        assertNotNull(out.getReader());
        out.close();
        assertEquals(0,counter.getClosures());
        out.close();
        assertEquals(0,counter.getClosures());
    }


    @Test(expected=FileNotFoundException.class)
    public void nonexistentFileReader()
        throws Exception
    {
        new ReaderWrapper(TEST_NONEXISTENT_FILE);
    }


    @Override
    protected void testStandardInputStream
        (ByteArrayInputStream in)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            ReaderWrapper wrapper=
                new ReaderWrapper(SpecialNames.STANDARD_INPUT);
            r.register(wrapper);
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getReader());
            assertEquals(VALUE,IOUtils.toString(wrapper.getReader()));
        } finally {
            r.close();
        }
    }

    private void testStandardInputStreamUnicode()
        throws Exception
    {
        InputStream stdInSave=System.in;
        CloseableRegistry r=new CloseableRegistry();
        try {
            ByteArrayInputStream stdIn=new ByteArrayInputStream
                (ArrayUtils.addAll(Signature.UTF8.getMark(),COMBO_UTF8));
            r.register(stdIn);
            System.setIn(stdIn);
            ReaderWrapper wrapper=
                new ReaderWrapper(SpecialNames.STANDARD_INPUT,
                                  DecodingStrategy.SIG_REQ);
            r.register(wrapper);
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getReader());
            assertEquals(COMBO,IOUtils.toString(wrapper.getReader()));
        } finally {
            System.setIn(stdInSave);
            r.close();
        }
    }

    @Test
    public void wrappers()
        throws Exception
    {
        testStandardInputStream();
        testStandardInputStreamUnicode();
    }
}
