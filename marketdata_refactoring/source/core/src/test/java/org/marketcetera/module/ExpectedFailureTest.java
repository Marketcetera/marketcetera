package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.except.I18NInterruptedException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NMessage;
import org.junit.Test;
import static org.junit.Assert.*;

/* $License$ */
/**
 * Tests {@link ExpectedFailure} functionality.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ExpectedFailureTest {
    /**
     * Verifies i18N exception testing
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void i18nExceptions() throws Exception {
        final I18NBoundMessage2P message = new I18NBoundMessage2P(
                TestMessages.EXCEPTION_TEST, true, 1);
        final I18NException expected = new I18NException(message);
        assertSame(expected, check(expected,
                TestMessages.EXCEPTION_TEST, true, 1));
        //Test a failure without message matching
        assertSame(expected, check(expected, (I18NMessage)null));
        //Test a failure matching only the message
        assertSame(expected, check(expected, TestMessages.EXCEPTION_TEST));
        //Test a failure matching only the parameters
        assertSame(expected, check(expected, null, true, 1));
        //Test a failure where message key match fails
        try {
            check(expected, TestMessages.BAD_DATA);
            fail("should fail");
        } catch(AssertionError e) {
        }
        //Test a failure where message parameter match fails
        try {
            check(expected, TestMessages.EXCEPTION_TEST, false, 1);
            fail("should fail");
        } catch(AssertionError e) {
        }
        try {
            check(expected, TestMessages.EXCEPTION_TEST, true, 2);
            fail("should fail");
        } catch(AssertionError e) {
        }
        //Test a failure where exception class matching fails
        try {
            check(new Exception("why"), TestMessages.EXCEPTION_TEST, true, 1);
            fail("should fail");
        } catch(AssertionError e) {
        }
        //Test a failure where no exception gets thrown
        try {
            check(null, TestMessages.EXCEPTION_TEST, true, 1);
            fail("should fail");
        } catch(AssertionError e) {
        }
        //verify subclasses match.
        I18NException another = new I18NInterruptedException(message);
        assertSame(another, check(another, TestMessages.EXCEPTION_TEST,
                true, 1));
    }

    /**
     * Verifies regular exception testing using the exact match flag.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void regularExceptionsExactMatch() throws Exception {
        //Test a regular failure.
        final IllegalArgumentException expected = new IllegalArgumentException("why");
        assertSame(expected, check(expected, "why",true));
        //Test a failure without message matching
        assertSame(expected, check(expected, null, true));
        //Test inexact matching
        assertSame(expected, check(expected, "hy",false));
        assertSame(expected, check(expected, "wh",false));
        assertSame(expected, check(expected, "why",false));
        //Test a failure where string match fails
        try {
            check(expected, "blah", true);
            fail("should fail");
        } catch(AssertionError e) {
        }
        //Test inexact matching failure
        try {
            check(expected, "why?", false);
            fail("should fail");
        } catch(AssertionError e) {
        }
        //Test a failure where exception class matching fails
        try {
            check(new Exception("why"), "why", true);
            fail("should fail");
        } catch(AssertionError e) {
        }
        //test condition when exception is not thrown.
        try {
            check(null, "why", false);
            fail("should fail");
        } catch(AssertionError e) {
        }
    }

    /**
     * Verifies regular exceptions using the default API.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void regularExceptionsDefault() throws Exception {
        //Test a regular failure.
        final IllegalArgumentException expected = new IllegalArgumentException("why");
        assertSame(expected, check(expected, "why"));
        //Test a failure without message matching
        assertSame(expected, check(expected, null));
        //Test a failure where string match fails
        try {
            check(expected, "blah");
            fail("should fail");
        } catch(AssertionError e) {
        }
        //Test a failure where exception class matching fails
        try {
            check(new Exception("why"), "why");
            fail("should fail");
        } catch(AssertionError e) {
        }
        //test condition when exception is not thrown.
        try {
            check(null, "why");
            fail("should fail");
        } catch(AssertionError e) {
        }
    }
    private static I18NException check(final Exception e,
                                       I18NMessage inMessage,
                                       Object... inParameters)
            throws Exception {
        return new ExpectedFailure<I18NException>(inMessage, inParameters){
            protected void run() throws Exception {
                if(e != null) {
                    throw e;
                }
            }
        }.getException();
    }
    private static IllegalArgumentException check(final Exception e,
                                                  String message)
            throws Exception {
        return new ExpectedFailure<IllegalArgumentException>(message){
            protected void run() throws Exception {
                if(e != null) {
                    throw e;
                }
            }
        }.getException();
    }
    private static IllegalArgumentException check(final Exception e,
                                                  String message,
                                                  boolean inExactMatch)
            throws Exception {
        return new ExpectedFailure<IllegalArgumentException>(message,
                inExactMatch){
            protected void run() throws Exception {
                if(e != null) {
                    throw e;
                }
            }
        }.getException();
    }
}
