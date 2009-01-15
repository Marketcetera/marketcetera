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
        assertSame(expected, check(expected, null, ExpectedFailure.IGNORE, 1));
        //Test a failure matching some of the parameters
        assertSame(expected, check(expected, TestMessages.EXCEPTION_TEST,
                ExpectedFailure.IGNORE, 1));
        assertSame(expected, check(expected, TestMessages.EXCEPTION_TEST,
                ExpectedFailure.IGNORE, ExpectedFailure.IGNORE));
        //Test a failure where message key match fails
        checkFailure(expected, TestMessages.BAD_DATA);
        //Test a failure where message parameter match fails
        checkFailure(expected, TestMessages.EXCEPTION_TEST, false, 1);
        checkFailure(expected, TestMessages.EXCEPTION_TEST, true, 2);
        //Test a failure where exception class matching fails
        checkFailure(new Exception("why"), TestMessages.EXCEPTION_TEST,
                true, 1);
        //Test a failure where no exception gets thrown
        checkFailure(null, TestMessages.EXCEPTION_TEST, true, 1);
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
        final IllegalArgumentException expected =
                new IllegalArgumentException("why");
        assertSame(expected, check(expected, "why",true));
        //Test a failure without message matching
        assertSame(expected, check(expected, null, true));
        //Test inexact matching
        assertSame(expected, check(expected, "hy",false));
        assertSame(expected, check(expected, "wh",false));
        assertSame(expected, check(expected, "why",false));
        //Test a failure where string match fails
        checkFailure(expected, "blah", true);
        //Test inexact matching failure
        checkFailure(expected, "why?", false);
        //Test a failure where exception class matching fails
        checkFailure(new Exception("why"), "why", true);
        //test condition when exception is not thrown.
        checkFailure(null, "why", false);
    }

    /**
     * Verifies regular exceptions using the default API.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void regularExceptionsDefault() throws Exception {
        //Test a regular failure.
        final IllegalArgumentException expected =
                new IllegalArgumentException("why");
        assertSame(expected, check(expected, "why"));
        //Test a failure without message matching
        assertSame(expected, check(expected, null));
        //Test a failure where string match fails
        checkFailure(expected, "blah");
        //Test a failure where exception class matching fails
        checkFailure(new Exception("why"), "why");
        //test condition when exception is not thrown.
        checkFailure(null, "why");
    }
    private static I18NException check(final Exception e,
                                       I18NMessage inMessage,
                                       Object... inParameters)
            throws Exception {
        assertSame(e, ExpectedFailure.assertI18NException(e,
                inMessage, inParameters));
        return new ExpectedFailure<I18NException>(inMessage, inParameters){
            protected void run() throws Exception {
                if(e != null) {
                    throw e;
                }
            }
        }.getException();
    }
    private static void checkFailure(final Exception inException,
                                     I18NMessage inMessage,
                                     Object... inParameters) throws Exception {
        try {
            ExpectedFailure.assertI18NException(inException,
                    inMessage, inParameters);
            fail("should fail");
        } catch(AssertionError e) {
        }
        try {
            new ExpectedFailure<I18NException>(inMessage, inParameters){
                protected void run() throws Exception {
                    if(inException != null) {
                        throw inException;
                    }
                }
            };
            fail("should fail");
        } catch(AssertionError e) {
        }
    }
    private static IllegalArgumentException check(final Exception e,
                                                  String message)
            throws Exception {
        if (message != null) {
            assertSame(e, ExpectedFailure.assertException(e, message, true));
        }
        return new ExpectedFailure<IllegalArgumentException>(message){
            protected void run() throws Exception {
                if(e != null) {
                    throw e;
                }
            }
        }.getException();
    }
    private static void checkFailure(final Exception inException,
                                     String inMessage) throws Exception {
        try {
            ExpectedFailure.assertException(inException, inMessage, true);
            fail("should fail");
        } catch(AssertionError e) {
        }
        try {
            new ExpectedFailure<IllegalArgumentException>(inMessage){
                protected void run() throws Exception {
                    if(inException != null) {
                        throw inException;
                    }
                }
            };
            fail("should fail");
        } catch(AssertionError e) {
        }
    }
    private static IllegalArgumentException check(final Exception e,
                                                  String message,
                                                  boolean inExactMatch)
            throws Exception {
        if (message != null) {
            assertSame(e, ExpectedFailure.assertException(e, message,
                    inExactMatch));
        }
        return new ExpectedFailure<IllegalArgumentException>(message,
                inExactMatch){
            protected void run() throws Exception {
                if(e != null) {
                    throw e;
                }
            }
        }.getException();
    }
    private static void checkFailure(final Exception inException,
                                     String inMessage,
                                     boolean inExactMatch) throws Exception {
        try {
            ExpectedFailure.assertException(inException,
                    inMessage, inExactMatch);
            fail("should fail");
        } catch(AssertionError e) {
        }
        try {
            new ExpectedFailure<IllegalArgumentException>(
                    inMessage, inExactMatch){
                protected void run() throws Exception {
                    if(inException != null) {
                        throw inException;
                    }
                }
            };
            fail("should fail");
        } catch(AssertionError e) {
        }
    }
}
