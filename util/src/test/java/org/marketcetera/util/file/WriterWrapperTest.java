package org.marketcetera.util.file;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.marketcetera.util.test.UnicodeData.COMBO;
import static org.marketcetera.util.test.UnicodeData.COMBO_UTF8;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.Writer;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;
import org.marketcetera.util.unicode.Signature;
import org.marketcetera.util.unicode.SignatureCharset;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id: WriterWrapperTest.java 16994 2015-03-09 21:18:25Z colin $
 */

/* $License$ */

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
        try(WriterWrapper wrapper = new WriterWrapper(TEST_NONEXISTENT_FILE)) {}
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

    private void testStandardOutputStreamUnicode()
        throws Exception
    {
        PrintStream stdOutSave=System.out;
        CloseableRegistry r=new CloseableRegistry();
        ByteArrayOutputStream stdOutByteArray=new ByteArrayOutputStream();
        try {
            r.register(stdOutByteArray);
            PrintStream stdOut=new PrintStream(stdOutByteArray);
            r.register(stdOut);
            System.setOut(stdOut);
            WriterWrapper wrapper=new WriterWrapper
                (SpecialNames.STANDARD_OUTPUT,SignatureCharset.UTF8_UTF8);
            r.register(wrapper);
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getWriter());
            wrapper.getWriter().write(COMBO);
        } finally {
            System.setOut(stdOutSave);
            r.close();
        }
        assertArrayEquals
            (ArrayUtils.addAll(Signature.UTF8.getMark(),COMBO_UTF8),
             stdOutByteArray.toByteArray());
    }

    private void testStandardErrorStreamUnicode()
        throws Exception
    {
        PrintStream stdErrSave=System.err;
        CloseableRegistry r=new CloseableRegistry();
        ByteArrayOutputStream stdErrByteArray=new ByteArrayOutputStream();
        try {
            r.register(stdErrByteArray);
            PrintStream stdErr=new PrintStream(stdErrByteArray);
            r.register(stdErr);
            System.setErr(stdErr);
            WriterWrapper wrapper=new WriterWrapper
                (SpecialNames.STANDARD_ERROR,SignatureCharset.UTF8_UTF8);
            r.register(wrapper);
            assertTrue(wrapper.getSkipClose());
            assertNotNull(wrapper.getWriter());
            wrapper.getWriter().write(COMBO);
        } finally {
            System.setErr(stdErrSave);
            r.close();
        }
        assertArrayEquals
            (ArrayUtils.addAll(Signature.UTF8.getMark(),COMBO_UTF8),
             stdErrByteArray.toByteArray());
    }

    @Test
    public void wrappers()
        throws Exception
    {
        testStandardOutputStream();
        testStandardOutputStreamUnicode();
        testStandardErrorStream();
        testStandardErrorStreamUnicode();
    }
}
