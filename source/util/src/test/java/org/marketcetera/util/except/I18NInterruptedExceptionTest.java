package org.marketcetera.util.except;

import org.junit.Test;

import static org.junit.Assert.*;

public class I18NInterruptedExceptionTest
	extends I18NThrowableTestBase
{
    @Test
    public void empty()
    {
        I18NInterruptedException ex=new I18NInterruptedException();
        assertEquals(Messages.PROVIDER,ex.getI18NProvider());
        assertEquals(Messages.THREAD_INTERRUPTED,ex.getI18NMessage());
        assertEquals(0,ex.getParams().length);
        assertNull(ex.getCause());
    }

    @Test
    public void causeWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        I18NInterruptedException ex=new I18NInterruptedException(nested);
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
             new I18NInterruptedException
             (TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        myMessageAndCauseWithoutMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NInterruptedException
             (nested,
              TestMessages.PROVIDER,TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void interruptionEmptyNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption();
    }

    @Test(expected=I18NInterruptedException.class)
    public void interruptionEmptyThrow()
        throws Exception
    {
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedException.checkInterruption();
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NMessage());
            assertNull(ex.getCause());
            throw ex;
        }
    }

    @Test
    public void interruptionNestedNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption
            (new InterruptedException());
    }

    @Test(expected=I18NInterruptedException.class)
    public void interruptionNestedThrow()
        throws Exception
    {
        InterruptedException nested=new InterruptedException();
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedException.checkInterruption(nested);
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NMessage());
            assertEquals(nested,ex.getCause());
            throw ex;
        }
    }

    @Test
    public void interruptionI18NMessageNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption
            (TestMessages.PROVIDER,
             TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
    }

    @Test(expected=I18NInterruptedException.class)
    public void interruptionI18NMessageThrow()
        throws Exception
    {
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedException.checkInterruption
                (TestMessages.PROVIDER,
                 TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),TestMessages.MID_EXCEPTION,
                         ex.getI18NMessage());
            assertNull(ex.getCause());
            throw ex;
        }
    }

    @Test
    public void interruptionI18NMessageNestedNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption
            (new InterruptedException(),TestMessages.PROVIDER,
             TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
    }

    @Test(expected=I18NInterruptedException.class)
    public void interruptionI18NMessageNestedThrow()
        throws Exception
    {
        InterruptedException nested=new InterruptedException();
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedException.checkInterruption
                (nested,TestMessages.PROVIDER,
                 TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),TestMessages.MID_EXCEPTION,
                         ex.getI18NMessage());
            assertEquals(nested,ex.getCause());
            throw ex;
        }
    }
}
