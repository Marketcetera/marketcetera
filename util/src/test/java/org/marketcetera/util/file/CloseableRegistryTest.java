package org.marketcetera.util.file;

import static org.junit.Assert.assertEquals;

import java.io.Closeable;
import java.io.IOException;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class CloseableRegistryTest
    extends TestCaseBase
{

    private static final class OrderedCloseable
        implements Closeable
    {
        private static int sSequence;

        private int mSequence;

        static void resetStaticSequence()
        {
            sSequence=0;
        }

        int getSequence()
        {
            return mSequence;
        }

        @Override
        public void close()
        {
            mSequence=++sSequence;
        }
    }

    private static final class ThrowingCloseable
        implements Closeable
    {
        @Override
        public void close()
            throws IOException
        {
            throw new IOException();
        }
    }


    @Before
    public void setupCloseableRegistryTest()
    {
        OrderedCloseable.resetStaticSequence();
        ActiveLocale.setProcessLocale(Locale.ROOT);
    }


    @Test
    public void orderedClosing()
    {
        CloseableRegistry r=new CloseableRegistry();
        OrderedCloseable t1=new OrderedCloseable();
        r.register(t1);
        OrderedCloseable t2=new OrderedCloseable();
        r.register(t2);
        r.close();
        assertEquals(1,t2.getSequence());
        assertEquals(2,t1.getSequence());
    }

    @Test
    public void exceptionsIgnored()
    {
        CloseableRegistry r=new CloseableRegistry();
        OrderedCloseable t1=new OrderedCloseable();
        r.register(t1);
        r.register(new ThrowingCloseable());
        OrderedCloseable t2=new OrderedCloseable();
        r.register(t2);
        r.register(new ThrowingCloseable());
        r.close();
        assertEquals(1,t2.getSequence());
        assertEquals(2,t1.getSequence());
    }
}
