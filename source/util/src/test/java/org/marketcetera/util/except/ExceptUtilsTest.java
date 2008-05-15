package org.marketcetera.util.except;

import java.io.InterruptedIOException;
import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ExceptUtilsTest
	extends I18NThrowableTestBase
{
    private static final String TEST_CATEGORY=
        ExceptUtils.class.getName();


    private static void interruptHelper
        (Exception ex,
         boolean interrupted)
    {
        ExceptUtils.interrupt(ex);
        assertEquals(interrupted,Thread.currentThread().interrupted());
    }

    private void swallowHelper
        (Exception ex,
         boolean interrupted)
    {
        ExceptUtils.swallow
            (ex,TEST_CATEGORY,TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        assertEquals(interrupted,Thread.currentThread().interrupted());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,MID_MSG_EN);

        ExceptUtils.swallow(ex);
        assertEquals(interrupted,Thread.currentThread().interrupted());
        assertSingleEvent(Level.WARN,TEST_CATEGORY,
                          "Caught throwable was not propagated");
    }

    private static void wrapHelper
        (Exception ex,
         boolean interruption)
    {
        I18NException out=ExceptUtils.wrap
            (ex,TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        assertEquals(TestMessages.MID_EXCEPTION,out.getI18NMessage());
        assertEquals(new Object[] {MID_MSG_PARAM},out.getParams());
        assertEquals(ex,out.getCause());
        assertTrue(out instanceof I18NException);
        assertEquals(interruption,out instanceof I18NInterruptedException);
        assertEquals(interruption,Thread.currentThread().interrupted());

        out=ExceptUtils.wrap(ex);
        assertEquals(ex,out.getCause());
        assertTrue(out instanceof I18NException);
        assertEquals(interruption,out instanceof I18NInterruptedException);
        assertEquals(interruption,Thread.currentThread().interrupted());

        I18NRuntimeException outR=ExceptUtils.wrapRuntime
            (ex,TestMessages.MID_EXCEPTION,MID_MSG_PARAM);
        assertEquals(TestMessages.MID_EXCEPTION,outR.getI18NMessage());
        assertEquals(new Object[] {MID_MSG_PARAM},outR.getParams());
        assertEquals(ex,outR.getCause());
        assertTrue(outR instanceof I18NRuntimeException);
        assertEquals(interruption,
                     outR instanceof I18NInterruptedRuntimeException);
        assertEquals(interruption,Thread.currentThread().interrupted());

        outR=ExceptUtils.wrapRuntime(ex);
        assertEquals(ex,outR.getCause());
        assertTrue(outR instanceof I18NRuntimeException);
        assertEquals(interruption,
                     outR instanceof I18NInterruptedRuntimeException);
        assertEquals(interruption,Thread.currentThread().interrupted());
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
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals("Thread execution was interrupted",ex.getMessage());
            assertNull(ex.getCause());
            return;
        }
        fail();
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
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals("Thread execution was interrupted",ex.getMessage());
            assertEquals(nested,ex.getCause());
            return;
        }
        fail();
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
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(TEST_MSG_1,ex.getMessage());
            assertNull(ex.getCause());
            return;
        }
        fail();
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
        } catch (InterruptedException ex) {
            assertFalse(Thread.currentThread().isInterrupted());
            assertEquals(TEST_MSG_1,ex.getMessage());
            assertEquals(nested,ex.getCause());
            return;
        }
        fail();
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
        interruptHelper(new I18NInterruptedException(),true);
        interruptHelper(new I18NInterruptedRuntimeException(),true);
    }

    @Test
    public void swallow()
    {
        swallowHelper(new CloneNotSupportedException(),false);
        swallowHelper(new InterruptedException(),true);
        swallowHelper(new InterruptedIOException(),true);
        swallowHelper(new I18NInterruptedException(),true);
        swallowHelper(new I18NInterruptedRuntimeException(),true);
    }

    @Test
    public void wrap()
    {
        wrapHelper(new CloneNotSupportedException(),false);
        wrapHelper(new InterruptedException(),true);
        wrapHelper(new InterruptedIOException(),true);
        wrapHelper(new I18NInterruptedException(),true);
        wrapHelper(new I18NInterruptedRuntimeException(),true);
    }
}
