package org.marketcetera.util.file;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.log.ActiveLocale;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class CloseableRegistryTest
    extends TestCaseBase
{
    private static final String TEST_CATEGORY=
        CloseableRegistry.class.getName();
    private static final String TEST_LOCATION=
        ExceptUtils.class.getName();
    private static final String TEST_MESSAGE=
        "Closing failed";


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
        setLevel(TEST_CATEGORY,Level.WARN);
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
        Iterator<LoggingEvent> events=getAppender().getEvents().iterator();
        assertEvent(events.next(),Level.WARN,TEST_CATEGORY,TEST_MESSAGE,
                    TEST_LOCATION);
        assertEvent(events.next(),Level.WARN,TEST_CATEGORY,TEST_MESSAGE,
                    TEST_LOCATION);
        assertFalse(events.hasNext());
    }
}
