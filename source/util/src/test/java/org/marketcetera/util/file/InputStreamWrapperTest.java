package org.marketcetera.util.file;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class InputStreamWrapperTest
	extends WrapperTestBase
{
    private static final class CloseCounterInputStream
        extends InputStream
    {
        private int mClosures=0;

        public int getClosures()
        {
            return mClosures;
        }

        @Override
        public int read()
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
        CloseCounterInputStream counter=new CloseCounterInputStream();
        InputStreamWrapper out=new InputStreamWrapper(counter);
        assertEquals(0,counter.getClosures());
        assertFalse(out.getSkipClose());
        assertNotNull(out.getStream());
        out.close();
        assertEquals(1,counter.getClosures());
        out.close();
        assertEquals(1,counter.getClosures());

        counter=new CloseCounterInputStream();
        out=new InputStreamWrapper(counter,true);
        assertEquals(0,counter.getClosures());
        assertTrue(out.getSkipClose());
        assertNotNull(out.getStream());
        out.close();
        assertEquals(0,counter.getClosures());
        out.close();
        assertEquals(0,counter.getClosures());
    }


    @Test(expected=FileNotFoundException.class)
    public void nonexistentFileInputStream()
        throws Exception
    {
        new InputStreamWrapper(TEST_NONEXISTENT_FILE);
    }


    @Override
    protected void testStandardInputStream
        (ByteArrayInputStream in)
        throws Exception
    {
        InputStreamWrapper wrapper=new InputStreamWrapper
            (SpecialNames.STANDARD_INPUT);
        try {
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getStream());
            assertArrayEquals
                (VALUE_BYTES,IOUtils.toByteArray(wrapper.getStream()));
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
