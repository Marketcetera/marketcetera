package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ExpectedTestFailureTest extends TestCase
{
    public ExpectedTestFailureTest(String inName)
    {
        super(inName);
    }

    public static Test suite()
    {
        // run the test repeatedly since we can have a race condition
        return new MarketceteraTestSuite(ExpectedTestFailureTest.class);
    }

    public void testClassSpecified()
    {
        final RuntimeException ex = new RuntimeException();
        assertEquals(ex, (new ExpectedTestFailure(RuntimeException.class) {
            protected void execute() throws Throwable
            {
                throw ex;
            }
        }).run());
    }

    public void testMatchSpecified()
    {
        final RuntimeException rex = new RuntimeException("toli was here"); //$NON-NLS-1$
        assertEquals(rex, (new ExpectedTestFailure(RuntimeException.class, "toli") { //$NON-NLS-1$
                protected void execute() throws Throwable
                {
                    throw rex;
                }
            }).run());
        final IllegalArgumentException ex = new IllegalArgumentException("toli was here"); //$NON-NLS-1$
        assertEquals(ex, (new ExpectedTestFailure(IllegalArgumentException.class, "was") { //$NON-NLS-1$
                protected void execute() throws Throwable
                {
                    throw ex;
                }
            }).run());


    }

    /** Check the case when the exception has a message (toString()) but getMessage() returns null */
    public void testExceptinoHasNoMessageButHasString() throws Exception {
        final Exception ex = new Exception() {
            public String toString() {
                return "internal message 32"; //$NON-NLS-1$
            }
        };

        assertEquals(ex, new ExpectedTestFailure(Exception.class, "message 32") { //$NON-NLS-1$
            protected void execute() throws Throwable {
                throw ex;
            }
        }.run());
    }

}
