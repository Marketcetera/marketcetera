package org.marketcetera.util.except;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExceptUtilsTest
	extends I18NThrowableTestBase
{
    private static final String TEST_CATEGORY=
        ExceptUtils.class.getName();


    private void swallowException
        (Exception ex,
         boolean interrupted)
    {
        ExceptUtils.swallow
            (ex,TestMessages.LOGGER,
             TEST_CATEGORY,TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        assertEquals(interrupted,Thread.currentThread().interrupted());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,MID_MSG_EN);

        ExceptUtils.swallow(ex);
        assertEquals(interrupted,Thread.currentThread().interrupted());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,
                          "Caught throwable was not propagated");
    }


    @Before
    public void setup()
    {
        setLevel(TEST_CATEGORY,Level.WARN);
    }


    @Test
    public void interruptionEmptyNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption();
    }

    @Test(expected=InterruptedException.class)
    public void interruptionEmptyThrow()
        throws Exception
    {
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption();
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals("Thread execution was interrupted",ex.getMessage());
            assertNull(ex.getCause());
            throw ex;
        }
    }

    @Test
    public void interruptionNestedNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption
            (new CloneNotSupportedException());
    }

    @Test(expected=InterruptedException.class)
    public void interruptionNestedThrow()
        throws Exception
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption(nested);
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals("Thread execution was interrupted",ex.getMessage());
            assertEquals(nested,ex.getCause());
            throw ex;
        }
    }

    @Test
    public void interruptionMessageNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption(TEST_MSG_1);
    }

    @Test(expected=InterruptedException.class)
    public void interruptionMessageThrow()
        throws Exception
    {
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption(TEST_MSG_1);
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(TEST_MSG_1,ex.getMessage());
            assertNull(ex.getCause());
            throw ex;
        }
    }

    @Test
    public void interruptionMessageNestedNoThrow()
        throws Exception
    {
        ExceptUtils.checkInterruption
            (new CloneNotSupportedException(),TEST_MSG_1);
    }

    @Test(expected=InterruptedException.class)
    public void interruptionMessageNestedThrow()
        throws Exception
    {
        CloneNotSupportedException nested=new CloneNotSupportedException();
        Thread.currentThread().interrupt();
        try {
            ExceptUtils.checkInterruption(nested,TEST_MSG_1);
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(TEST_MSG_1,ex.getMessage());
            assertEquals(nested,ex.getCause());
            throw ex;
        }
    }

    @Test
    public void swallow()
    {
        swallowException(new CloneNotSupportedException(),false);
        swallowException(new InterruptedException(),true);
        swallowException(new I18NInterruptedException(),true);
        swallowException(new I18NInterruptedRuntimeException(),true);
    }
}
