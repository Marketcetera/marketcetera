package org.marketcetera.util.except;

import org.junit.Test;
import org.marketcetera.util.log.I18NBoundMessage1P;

import static org.junit.Assert.*;

public class I18NInterruptedExceptionTest
	extends I18NThrowableTestBase
{
    @Test
    public void empty()
    {
        I18NInterruptedException ex=new I18NInterruptedException();
        assertEquals(Messages.THREAD_INTERRUPTED,ex.getI18NBoundMessage());
        assertNull(ex.getCause());
    }

    @Test
    public void causeWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        I18NInterruptedException ex=new I18NInterruptedException(nested);
        assertEquals(Messages.THREAD_INTERRUPTED,ex.getI18NBoundMessage());
        assertEquals(nested,ex.getCause());
    }

    @Test
    public void myMessage()
    {
        myMessage
            (new Exception(TEST_MSG_1),
             new I18NInterruptedException
             (new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
    }

    @Test
    public void myMessageAndCauseWithoutMessage()
    {
        InterruptedException nested=new InterruptedException();
        myMessageAndCauseWithoutMessage
            (nested,new Exception(TEST_MSG_1,nested),
             new I18NInterruptedException
             (nested,new I18NBoundMessage1P
              (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
    }

    @Test
    public void interruptionEmptyNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption();
    }

    @Test
    public void interruptionEmptyThrow()
    {
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedException.checkInterruption();
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NBoundMessage());
            assertNull(ex.getCause());
            return;
        }
        fail();
    }

    @Test
    public void interruptionNestedNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption
            (new InterruptedException());
    }

    @Test
    public void interruptionNestedThrow()
    {
        InterruptedException nested=new InterruptedException();
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedException.checkInterruption(nested);
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(ex.getDetail(),Messages.THREAD_INTERRUPTED,
                         ex.getI18NBoundMessage());
            assertEquals(nested,ex.getCause());
            return;
        }
        fail();
    }

    @Test
    public void interruptionI18NMessageNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption
            (new I18NBoundMessage1P
             (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
    }

    @Test
    public void interruptionI18NMessageThrow()
    {
        Thread.currentThread().interrupt();
        try {
            I18NInterruptedException.checkInterruption
                (new I18NBoundMessage1P
                 (TestMessages.MID_EXCEPTION,MID_MSG_PARAM));
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals(ex.getDetail(),TestMessages.MID_EXCEPTION,
                         m.getMessage());
            assertEquals(MID_MSG_PARAM,m.getParam1());
            assertNull(ex.getCause());
            return;
        }
        fail();
    }

    @Test
    public void interruptionI18NMessageNestedNoThrow()
        throws Exception
    {
        I18NInterruptedException.checkInterruption
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
            I18NInterruptedException.checkInterruption
                (nested,
                 (new I18NBoundMessage1P
                  (TestMessages.MID_EXCEPTION,MID_MSG_PARAM)));
        } catch (I18NInterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            I18NBoundMessage1P m=(I18NBoundMessage1P)ex.getI18NBoundMessage();
            assertEquals(ex.getDetail(),TestMessages.MID_EXCEPTION,
                         m.getMessage());
            assertEquals(MID_MSG_PARAM,m.getParam1());
            assertEquals(nested,ex.getCause());
            return;
        }
        fail();
    }
}
