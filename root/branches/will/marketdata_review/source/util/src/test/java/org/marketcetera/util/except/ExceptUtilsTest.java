package org.marketcetera.util.except;

import java.io.InterruptedIOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.FileLockInterruptionException;
import java.rmi.activation.ActivationException;
import java.rmi.activation.UnknownObjectException;
import javax.naming.InterruptedNamingException;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class ExceptUtilsTest
    extends I18NThrowableTestBase
{
    private static final String TEST_CATEGORY=
        ExceptUtils.class.getName();
    private static final String TEST_LOCATION=
        TEST_CATEGORY;


    private static void interruptHelper
        (Exception ex,
         boolean interrupted)
    {
        assertEquals(interrupted,ExceptUtils.interrupt(ex));
        assertEquals(interrupted,Thread.interrupted());
    }

    private void swallowHelper
        (Exception ex,
         boolean interrupted)
    {
        assertEquals(interrupted,ExceptUtils.swallow
                     (ex,TEST_CATEGORY,new I18NBoundMessage1P
                      (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
        assertEquals(interrupted,Thread.interrupted());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,MID_MSG_EN,TEST_LOCATION);

        assertEquals(interrupted,ExceptUtils.swallow(ex));
        assertEquals(interrupted,Thread.interrupted());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,
                          "Caught throwable was not propagated",TEST_LOCATION);
    }

    private static void wrapHelper
        (Exception ex,
         boolean interruption)
    {
        I18NException out=ExceptUtils.wrap
            (ex,new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));

        assertEquals
            (out.getDetail(),
             new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM),
             out.getI18NBoundMessage());
        assertEquals(ex,out.getCause());
        assertTrue(out instanceof I18NException);
        assertEquals(interruption,out instanceof I18NInterruptedException);
        assertEquals(interruption,Thread.interrupted());

        out=ExceptUtils.wrap(ex);
        assertEquals(ex,out.getCause());
        assertTrue(out instanceof I18NException);
        assertEquals(interruption,out instanceof I18NInterruptedException);
        assertEquals(interruption,Thread.interrupted());

        I18NRuntimeException outR=ExceptUtils.wrapRuntime
            (ex,new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        assertEquals
            (outR.getDetail(),
             new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM),
             outR.getI18NBoundMessage());
        assertEquals(ex,outR.getCause());
        assertTrue(outR instanceof I18NRuntimeException);
        assertEquals(interruption,
                     outR instanceof I18NInterruptedRuntimeException);
        assertEquals(interruption,Thread.interrupted());

        outR=ExceptUtils.wrapRuntime(ex);
        assertEquals(ex,outR.getCause());
        assertTrue(outR instanceof I18NRuntimeException);
        assertEquals(interruption,
                     outR instanceof I18NInterruptedRuntimeException);
        assertEquals(interruption,Thread.interrupted());
    }

    private static void equalityHelper
        (Throwable t1,
         Throwable t2,
         Throwable[] diffs)
    {
        assertTrue(ExceptUtils.areEqual(t1,t1));

        assertTrue(ExceptUtils.areEqual(t1,t2));
        assertTrue(ExceptUtils.areEqual(t2,t1));
        assertNotSame(t1,t2);

        for (Throwable t:diffs) {
            assertFalse(ExceptUtils.areEqual(t1,t));
            assertFalse(ExceptUtils.areEqual(t,t1));
        }

        assertFalse(ExceptUtils.areEqual(t1,null));
        assertFalse(ExceptUtils.areEqual(t1,NumberUtils.INTEGER_ZERO));

        assertEquals(ExceptUtils.getHashCode(t1),ExceptUtils.getHashCode(t2));
    }


    @Before
    public void setupExceptUtilsTest()
    {
        setLevel(TEST_CATEGORY,Level.WARN);
    }


    @Test
    public void interruptionEmptyNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption();
    }

    @Test
    public void interruptionEmptyThrow()
    {
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption();
            fail();
        } catch (InterruptedException ex) {
            assertTrue(Thread.interrupted());
            assertEquals("Thread execution was interrupted",ex.getMessage());
            assertNull(ex.getCause());
        }
    }

    @Test
    public void interruptionNestedNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption
            (new CloneNotSupportedException());
    }

    @Test
    public void interruptionNestedThrow()
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption(nested);
            fail();
        } catch (InterruptedException ex) {
            assertTrue(Thread.interrupted());
            assertEquals("Thread execution was interrupted",ex.getMessage());
            assertEquals(nested,ex.getCause());
        }
    }

    @Test
    public void interruptionMessageNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption(TEST_MSG_1);
    }

    @Test
    public void interruptionMessageThrow()
    {
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption(TEST_MSG_1);
            fail();
        } catch (InterruptedException ex) {
            assertTrue(Thread.interrupted());
            assertEquals(TEST_MSG_1,ex.getMessage());
            assertNull(ex.getCause());
        }
    }

    @Test
    public void interruptionMessageNestedNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption
            (new CloneNotSupportedException(),TEST_MSG_1);
    }

    @Test
    public void interruptionMessageNestedThrow()
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption(nested,TEST_MSG_1);
            fail();
        } catch (InterruptedException ex) {
            assertTrue(Thread.interrupted());
            assertEquals(TEST_MSG_1,ex.getMessage());
            assertEquals(nested,ex.getCause());
        }
    }

    @Test
    public void interruptException()
    {
        assertFalse(ExceptUtils.isInterruptException
                    (new CloneNotSupportedException()));
        assertTrue(ExceptUtils.isInterruptException
                   (new InterruptedException()));
        assertTrue(ExceptUtils.isInterruptException
                   (new InterruptedIOException()));
        assertTrue(ExceptUtils.isInterruptException
                   (new ClosedByInterruptException()));
        assertTrue(ExceptUtils.isInterruptException
                   (new FileLockInterruptionException()));
        assertTrue(ExceptUtils.isInterruptException
                   (new InterruptedNamingException()));
        assertTrue(ExceptUtils.isInterruptException
                   (new I18NInterruptedException()));
        assertTrue(ExceptUtils.isInterruptException
                   (new I18NInterruptedRuntimeException()));
    }

    @Test
    public void interrupt()
    {
        interruptHelper(new CloneNotSupportedException(),false);
        interruptHelper(new InterruptedException(),true);
        interruptHelper(new InterruptedIOException(),true);
        interruptHelper(new ClosedByInterruptException(),true);
        interruptHelper(new FileLockInterruptionException(),true);
        interruptHelper(new InterruptedNamingException(),true);
        interruptHelper(new I18NInterruptedException(),true);
        interruptHelper(new I18NInterruptedRuntimeException(),true);
    }

    @Test
    public void swallow()
    {
        swallowHelper(new CloneNotSupportedException(),false);
        swallowHelper(new InterruptedException(),true);
        swallowHelper(new InterruptedIOException(),true);
        swallowHelper(new ClosedByInterruptException(),true);
        swallowHelper(new FileLockInterruptionException(),true);
        swallowHelper(new InterruptedNamingException(),true);
        swallowHelper(new I18NInterruptedException(),true);
        swallowHelper(new I18NInterruptedRuntimeException(),true);
    }

    @Test
    public void wrap()
    {
        wrapHelper(new CloneNotSupportedException(),false);
        wrapHelper(new InterruptedException(),true);
        wrapHelper(new InterruptedIOException(),true);
        wrapHelper(new ClosedByInterruptException(),true);
        wrapHelper(new FileLockInterruptionException(),true);
        wrapHelper(new InterruptedNamingException(),true);
        wrapHelper(new I18NInterruptedException(),true);
        wrapHelper(new I18NInterruptedRuntimeException(),true);
    }

    @Test
    public void equality()
    {
        assertTrue(ExceptUtils.areEqual(null,null));
        equalityHelper
            (new ActivationException(TEST_MSG_1),
             new ActivationException(TEST_MSG_1),
             new Throwable[] {
                new ActivationException(),
                new ActivationException(TEST_MSG_2),
                new UnknownObjectException(TEST_MSG_1),
                new I18NException(),
                null
            });
        equalityHelper
            (new I18NException
             (new ActivationException(TEST_MSG_1),
              new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
             new I18NException
             (new ActivationException(TEST_MSG_1),
              new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
             new Throwable[] {
                new I18NException
                (new ActivationException(TEST_MSG_1)),
                new I18NException
                (new ActivationException(TEST_MSG_1),
                 TestMessages.BOT_EXCEPTION),
                new I18NException
                (new ActivationException(TEST_MSG_2),
                 new I18NBoundMessage1P
                 (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
                new I18NException
                (new I18NBoundMessage1P
                 (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
                new ActivationException(TEST_MSG_1),
                null
            });
    }
}
