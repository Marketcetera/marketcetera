package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Graham Miller
 * @version $Id$
 */
public class AccessViolatorTest extends TestCase {


    public AccessViolatorTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new MarketceteraTestSuite(AccessViolatorTest.class);
    }

    public void testReturnValues() throws Exception {
        AccessViolator violator = new AccessViolator(ViolatedClass.class);
        ViolatedClass violated = new ViolatedClass();
        assertEquals(7, ((Integer)violator.getField("YOU_CANT_READ_ME", violated)).intValue());
        assertTrue(((String)violator.invokeMethod("youCantCallMe", violated, "2+2 is 4")).endsWith("2+2 is 4"));
    }

    public void testException() throws Exception {
        final AccessViolator violator = new AccessViolator(ViolatedClass.class);
        final ViolatedClass violated = new ViolatedClass();
        new ExpectedTestFailure(Exception.class) {
            protected void execute() throws Throwable {
                violator.invokeMethod("youCantGetMyException", violated);
            }
        }.run();
    }
}
