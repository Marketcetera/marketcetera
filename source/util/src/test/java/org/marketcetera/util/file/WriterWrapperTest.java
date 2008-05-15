package org.marketcetera.util.file;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.Writer;
import org.junit.Test;

import static org.junit.Assert.*;

public class WriterWrapperTest
	extends WrapperTestBase
{
    private static final class CloseCounterWriter
        extends Writer
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
            (char[] cbuf,
             int off,
             int len) {}

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
        CloseCounterWriter counter=new CloseCounterWriter();
        WriterWrapper out=new WriterWrapper(counter);
        assertEquals(0,counter.getClosures());
        assertEquals(0,counter.getFlushes());
        assertFalse(out.getSkipClose());
        assertNotNull(out.getWriter());
        out.close();
        assertEquals(1,counter.getClosures());
        assertEquals(1,counter.getFlushes());
        out.close();
        assertEquals(1,counter.getClosures());
        assertEquals(1,counter.getFlushes());

        counter=new CloseCounterWriter();
        out=new WriterWrapper(counter,true);
        assertEquals(0,counter.getClosures());
        assertEquals(0,counter.getFlushes());
        assertTrue(out.getSkipClose());
        assertNotNull(out.getWriter());
        out.close();
        assertEquals(0,counter.getClosures());
        assertEquals(1,counter.getFlushes());
        out.close();
        assertEquals(0,counter.getClosures());
        assertEquals(2,counter.getFlushes());
    }


    @Test(expected=FileNotFoundException.class)
    public void nonexistent()
        throws Exception
    {
        new WriterWrapper(TEST_NONEXISTENT_FILE);
    }


    private static void testStandardStream
        (String name,
         ByteArrayOutputStream out)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            WriterWrapper wrapper=new WriterWrapper(name);
            r.register(wrapper);
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getWriter());
            wrapper.getWriter().write(VALUE);
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
