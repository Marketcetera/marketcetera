package org.marketcetera.util.except;

import org.junit.Test;

import static org.junit.Assert.*;

public class I18NInterruptedRuntimeExceptionTest
	extends I18NThrowableTestBase
{
    @Test
    public void empty()
    {
        I18NInterruptedRuntimeException ex=
            new I18NInterruptedRuntimeException();
        assertEquals(Messages.PROVIDER,ex.getI18NProvider());
        assertEquals(Messages.THREAD_INTERRUPTED,ex.getI18NMessage());
        assertEquals(0,ex.getParams().length);
        assertNull(ex.getCause());
    }

    @Test
    public void causeWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        I18NInterruptedRuntimeException ex=
            new I18NInterruptedRuntimeException(nested);
        assertEquals(Messages.PROVIDER,ex.getI18NProvider());
        assertEquals(Messages.THREAD_INTERRUPTED,ex.getI18NMessage());
        assertEquals(0,ex.getParams().length);
        assertEquals(nested,ex.getCause());
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new Exception(TEST_MSG_1),
             new I18NInterruptedRuntimeException
             (TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        myMessageAndCauseWithoutMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NInterruptedRuntimeException
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
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
        } catch (I18NInterruptedRuntimeException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NMessage());
            assertNull(ex.getCause());
            return;
        }
        fail();
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
        } catch (I18NInterruptedRuntimeException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NMessage());
            assertEquals(nested,ex.getCause());
            return;
        }
        fail();
    }

    @Test
    public void interruptionI18NMessageNoThrow()
        throws Exception
    {
        I18NInterruptedRuntimeException.checkInterruption
            (TestMessages.PROVIDER,
             TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
    }

    @Test
    public void interruptionI18NMessageThrow()
    {
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedRuntimeException.checkInterruption
                (TestMessages.PROVIDER,
                 TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        } catch (I18NInterruptedRuntimeException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),TestMessages.MID_EXCEPTION,
                         ex.getI18NMessage());
            assertNull(ex.getCause());
            return;
        }
        fail();
    }

    @Test
    public void interruptionI18NMessageNestedNoThrow()
        throws Exception
    {
        I18NInterruptedRuntimeException.checkInterruption
            (new InterruptedException(),TestMessages.PROVIDER,
             TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
    }

    @Test
    public void interruptionI18NMessageNestedThrow()
    {
        InterruptedException nested=new InterruptedException();
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedRuntimeException.checkInterruption
                (nested,TestMessages.PROVIDER,
                 TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        } catch (I18NInterruptedRuntimeException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),TestMessages.MID_EXCEPTION,
                         ex.getI18NMessage());
            assertEquals(nested,ex.getCause());
            return;
        }
        fail();
    }
}
