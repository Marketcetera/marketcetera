package org.marketcetera.util.except;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;

import static org.junit.Assert.*;

/**
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

public class I18NInterruptedRuntimeExceptionTest
    extends I18NThrowableTestBase
{
    private final static I18NThrowable[] ALL=new I18NThrowable[] {
        new I18NInterruptedRuntimeException
        (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NInterruptedRuntimeException
        (new InterruptedException(),
         new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        new I18NRuntimeException
        (new I18NBoundMessage1P(TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),
        null
    };


    @Test
    public void empty()
    {
        I18NInterruptedRuntimeException ex=
            new I18NInterruptedRuntimeException();
        assertEquals(Messages.THREAD_INTERRUPTED,ex.getI18NBoundMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void causeWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        I18NInterruptedRuntimeException ex=
            new I18NInterruptedRuntimeException(nested);
        assertEquals(Messages.THREAD_INTERRUPTED,ex.getI18NBoundMessage());
        assertEquals(nested,ex.getCause());
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new Exception(TEST_MSG_1),
             new I18NInterruptedRuntimeException
             (new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,0);
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        myMessageAndCauseWithoutMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NInterruptedRuntimeException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)),ALL,1);
    }

    @Test
    public void interruptionEmptyNoThrow()
        throws Exception
    {
        I18NInterruptedRuntimeException.checkInterruption();
    }

    @Test
    public void interruptionEmptyThrow()
    {
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedRuntimeException.checkInterruption();
            fail();
        } catch (I18NInterruptedRuntimeException ex) {
            assertTrue(Thread.interrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NBoundMessage());
            assertNull(ex.getCause());
        }
    }

    @Test
    public void interruptionNestedNoThrow()
        throws Exception
    {
        I18NInterruptedRuntimeException.checkInterruption
            (new InterruptedException());
    }

    @Test
    public void interruptionNestedThrow()
    {
        InterruptedException nested=new InterruptedException();
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedRuntimeException.checkInterruption(nested);
            fail();
        } catch (I18NInterruptedRuntimeException ex) {
            assertTrue(Thread.interrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NBoundMessage());
            assertEquals(nested,ex.getCause());
        }
    }

    @Test
    public void interruptionI18NMessageNoThrow()
        throws Exception
    {
        I18NInterruptedRuntimeException.checkInterruption
            (new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void interruptionI18NMessageThrow()
    {
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedRuntimeException.checkInterruption
                (new I18NBoundMessage1P
                 (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
            fail();
        } catch (I18NInterruptedRuntimeException ex) {
            assertTrue(Thread.interrupted());
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P
                 (TestMessages.MID_EXCEPTION,MID_MSG_PARAM),
                 ex.getI18NBoundMessage());
            assertNull(ex.getCause());
        }
    }

    @Test
    public void interruptionI18NMessageNestedNoThrow()
        throws Exception
    {
        I18NInterruptedRuntimeException.checkInterruption
            (new InterruptedException(),
             (new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
    }

    @Test
    public void interruptionI18NMessageNestedThrow()
    {
        InterruptedException nested=new InterruptedException();
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedRuntimeException.checkInterruption
                (nested,
                 (new I18NBoundMessage1P
                  (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
            fail();
        } catch (I18NInterruptedRuntimeException ex) {
            assertTrue(Thread.interrupted());
            assertEquals
                (ex.getDetail(),
                 new I18NBoundMessage1P
                 (TestMessages.MID_EXCEPTION,MID_MSG_PARAM),
                 ex.getI18NBoundMessage());
            assertEquals(nested,ex.getCause());
        }
    }
}
