package org.marketcetera.util.exec;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import org.apache.log4j.Level;
import org.junit.Test;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class InputThreadTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        InputThread.class.getName();
    private static final int SLEEP_DURATION=
        1000;
    private static final String TEST_LOCATION=
        TEST_CATEGORY;


    private static final class GenerousInputStream
        extends InputStream
    {
        private int mClosures=0;

        int getClosures()
        {
            return mClosures;
        }

        private static void sleep()
            throws IOException
        {
            try {
                Thread.sleep(SLEEP_DURATION);
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public int read()
            throws IOException
        {
            sleep();
            return 0;
        }

        /*
         * This override is necessary because the default JDK 1.6.0_04
         * implementation of this method silently ignores any
         * IOException thrown by read() above.
         */

        @Override
        public int read
            (byte[] b,
             int off,
             int len)
            throws IOException
        {
            sleep();
            return 0;
        }

        @Override
        public void close()
        {
            mClosures++;
        }
    }

    private static final class ForgetfulOutputStream
        extends OutputStream
    {
        private int mClosures=0;

        int getClosures()
        {
            return mClosures;
        }

        @Override
        public void write
            (int b) {}

        @Override
        public void close()
        {
            mClosures++;
        }
    }

    private void single
        (boolean closeOut)
        throws Exception
    {
        GenerousInputStream in=new GenerousInputStream();
        ForgetfulOutputStream out=new ForgetfulOutputStream();
        InputThread child=new InputThread("command",in,out,closeOut);
        child.start();
        Thread.sleep(SLEEP_DURATION/2);
        child.interrupt();
        Thread.sleep(SLEEP_DURATION/2);
        assertSingleEvent
            (Level.ERROR,TEST_CATEGORY,
             "Cannot copy output of command 'command'",TEST_LOCATION);
        assertEquals(1,in.getClosures());
        assertEquals((closeOut?1:0),out.getClosures());
    }


    @Test
    public void cannotCopy()
        throws Exception
    {
        ActiveLocale.setProcessLocale(Locale.ROOT);
        setLevel(TEST_CATEGORY,Level.ERROR);
        single(true);
        single(false);
    }
}
