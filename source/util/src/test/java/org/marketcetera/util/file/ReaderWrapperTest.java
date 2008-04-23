package org.marketcetera.util.file;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReaderWrapperTest
	extends WrapperTestBase
{
    private static final class CloseCounterReader
        extends Reader
    {
        private int mClosures=0;

        public int getClosures()
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
        ReaderWrapper wrapper=new ReaderWrapper(SpecialNames.STANDARD_INPUT);
        try {
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getReader());
            assertEquals(VALUE,IOUtils.toString(wrapper.getReader()));
        } finally {
            wrapper.close();
        }
    }

    @Test
	public void wrappers()
        throws Exception
    {
        testStandardInputStream();
    }
}
