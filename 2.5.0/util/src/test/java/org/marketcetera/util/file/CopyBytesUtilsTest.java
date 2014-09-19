package org.marketcetera.util.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage1P;
import org.marketcetera.util.log.I18NBoundMessage2P;

import static org.junit.Assert.*;
import static org.marketcetera.util.test.UnicodeData.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */

/* $License$ */

public class CopyBytesUtilsTest
    extends CopyUtilsTestBase
{
    private static final byte[] VALUE=
        HELLO_EN_NAT;


    private static void copyIStream
        (String out)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper(TEST_INPUT_FILE);
            r.register(in);
            assertEquals(VALUE.length,
                         CopyBytesUtils.copy(in.getStream(),true,out));
        } finally {
            r.close();
        }
    }

    private static void copyOStream
        (String in)
        throws Exception
    {
        CloseableRegistry r=new CloseableRegistry();
        try {
            OutputStreamWrapper out=new OutputStreamWrapper(TEST_OUTPUT_FILE);
            r.register(out);
            assertEquals(VALUE.length,
                         CopyBytesUtils.copy(in,out.getStream(),true));
        } finally {
            r.close();
        }
    }


    @Before
    @After
    public void setupTearDownCopyBytesUtilsTest()
        throws Exception
    {
        Deleter.apply(TEST_INPUT_FILE);
        Deleter.apply(TEST_OUTPUT_FILE);
    }


    @Test
    public void copyStreams()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        CloseableRegistry r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper(TEST_INPUT_FILE);
            r.register(in);
            OutputStreamWrapper out=new OutputStreamWrapper(TEST_OUTPUT_FILE);
            r.register(out);
            assertEquals(VALUE.length,
                         CopyBytesUtils.copy(in.getStream(),true,
                                             out.getStream(),true));
        } finally {
            r.close();
        }
        assertArrayEquals(VALUE,CopyBytesUtils.copy(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyMemory()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        assertArrayEquals(VALUE,CopyBytesUtils.copy(TEST_INPUT_FILE));
    }

    @Test
    public void copyInputStream()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        copyIStream(TEST_OUTPUT_FILE);
        assertArrayEquals(VALUE,CopyBytesUtils.copy(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyOutputStream()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        copyOStream(TEST_INPUT_FILE);
        assertArrayEquals(VALUE,CopyBytesUtils.copy(TEST_OUTPUT_FILE));
    }

    @Test
    public void copyFiles()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        assertEquals(VALUE.length,
                     CopyBytesUtils.copy(TEST_INPUT_FILE,TEST_OUTPUT_FILE));
        assertArrayEquals(VALUE,CopyBytesUtils.copy(TEST_OUTPUT_FILE));
    }


    @Test
    public void failStreams()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        CloseableRegistry r=new CloseableRegistry();
        try {
            InputStreamWrapper in=new InputStreamWrapper(TEST_INPUT_FILE);
            r.register(in);
            OutputStreamWrapper out=new OutputStreamWrapper(TEST_OUTPUT_FILE);
            r.register(out);
            out.getStream().close();
            CopyBytesUtils.copy(in.getStream(),true,out.getStream(),true);
            fail();
        } catch (I18NException ex) {
            assertEquals(ex.getDetail(),Messages.CANNOT_COPY_STREAMS,
                         ex.getI18NBoundMessage());
        } finally {
            r.close();
        }
    }

    @Test
    public void failFileInput()
    {
        try {
            CopyBytesUtils.copy(TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage2P(Messages.CANNOT_COPY_FILES,
                                        TEST_NONEXISTENT_FILE,TEST_OUTPUT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failFileOutput()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        try {
            CopyBytesUtils.copy(TEST_INPUT_FILE,TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage2P(Messages.CANNOT_COPY_FILES,
                                        TEST_INPUT_FILE,TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failIStream()
        throws Exception
    {
        CopyBytesUtils.copy(VALUE,TEST_INPUT_FILE);
        try {
            copyIStream(TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_ISTREAM,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failOStream()
        throws Exception
    {
        try {
            copyOStream(TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_OSTREAM,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failMemoryDst()
    {
        try {
            CopyBytesUtils.copy(TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_MEMORY_DST,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void failMemorySrc()
    {
        try {
            CopyBytesUtils.copy(VALUE,TEST_NONEXISTENT_FILE);
            fail();
        } catch (I18NException ex) {
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P(Messages.CANNOT_COPY_MEMORY_SRC,
                                        TEST_NONEXISTENT_FILE),
                 ex.getI18NBoundMessage());
        }
    }

    @Test
    public void closeCalled()
        throws Exception
    {
        CloseSetInputStream in=new CloseSetInputStream();
        CloseSetOutputStream out=new CloseSetOutputStream();
        CopyBytesUtils.copy(in,true,out,true);
        assertFalse(in.getClosed());
        assertFalse(out.getClosed());

        in=new CloseSetInputStream();
        out=new CloseSetOutputStream();
        CopyBytesUtils.copy(in,true,out,false);
        assertFalse(in.getClosed());
        assertTrue(out.getClosed());

        in=new CloseSetInputStream();
        out=new CloseSetOutputStream();
        CopyBytesUtils.copy(in,false,out,true);
        assertTrue(in.getClosed());
        assertFalse(out.getClosed());

        in=new CloseSetInputStream();
        out=new CloseSetOutputStream();
        CopyBytesUtils.copy(in,false,out,false);
        assertTrue(in.getClosed());
        assertTrue(out.getClosed());

        in=new CloseSetInputStream();
        CopyBytesUtils.copy(in,true,TEST_INPUT_FILE);
        assertFalse(in.getClosed());

        in=new CloseSetInputStream();
        CopyBytesUtils.copy(in,false,TEST_INPUT_FILE);
        assertTrue(in.getClosed());

        out=new CloseSetOutputStream();
        CopyBytesUtils.copy(TEST_INPUT_FILE,out,true);
        assertFalse(out.getClosed());

        out=new CloseSetOutputStream();
        CopyBytesUtils.copy(TEST_INPUT_FILE,out,false);
        assertTrue(out.getClosed());
    }
}
