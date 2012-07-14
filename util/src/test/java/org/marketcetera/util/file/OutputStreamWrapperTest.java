package org.marketcetera.util.file;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class OutputStreamWrapperTest
    extends WrapperTestBase
{
    private static final class CloseCounterOutputStream
        extends OutputStream
    {
        private int mClosures=0;
        private int mFlushes=0;

        int getClosures()
        {
            return mClosures;
        }

        int getFlushes()
        {
            return mFlushes;
        }

        @Override
        public void write
            (int b) {}

        @Override
        public void flush()
        {
            mFlushes++;
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
        CloseCounterOutputStream counter=new CloseCounterOutputStream();
        OutputStreamWrapper out=new OutputStreamWrapper(counter);
        assertEquals(0,counter.getClosures());
        assertEquals(0,counter.getFlushes());
        assertFalse(out.getSkipClose());
        assertNotNull(out.getStream());
        out.close();
        assertEquals(1,counter.getClosures());
        assertEquals(1,counter.getFlushes());
        out.close();
        assertEquals(1,counter.getClosures());
        assertEquals(1,counter.getFlushes());

        counter=new CloseCounterOutputStream();
        out=new OutputStreamWrapper(counter,true);
        assertEquals(0,counter.getClosures());
        assertEquals(0,counter.getFlushes());
        assertTrue(out.getSkipClose());
        assertNotNull(out.getStream());
        out.close();
        assertEquals(0,counter.getClosures());
        assertEquals(1,counter.getFlushes());
        out.close();
        assertEquals(0,counter.getClosures());
        assertEquals(2,counter.getFlushes());
    }


    @Test(expected=FileNotFoundException.class)
    public void nonexistentFile()
        throws Exception
    {
        new OutputStreamWrapper(TEST_NONEXISTENT_FILE);
    }


    private static void testStandardStream
        (String name,
         ByteArrayOutputStream out)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            OutputStreamWrapper wrapper=new OutputStreamWrapper(name);
            r.register(wrapper);
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getStream());
            wrapper.getStream().write(VALUE_BYTES);
        } finally {
            r.close();
        }
        assertArrayEquals(VALUE_BYTES,out.toByteArray());
    }

    @Override
    protected void testStandardOutputStream
        (ByteArrayOutputStream out)
        throws Exception
    {
        testStandardStream(SpecialNames.STANDARD_OUTPUT,out);
    }

    @Override
    protected void testStandardErrorStream
        (ByteArrayOutputStream err)
        throws Exception
    {
        testStandardStream(SpecialNames.STANDARD_ERROR,err);
    }

    @Test
    public void wrappers()
        throws Exception
    {
        testStandardOutputStream();
        testStandardErrorStream();
    }
}
