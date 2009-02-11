package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
        assertEquals(7, ((Integer)violator.getField("YOU_CANT_READ_ME", violated)).intValue()); //$NON-NLS-1$
        assertTrue(((String)violator.invokeMethod("youCantCallMe", violated, "2+2 is 4")).endsWith("2+2 is 4")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public void testException() throws Exception {
        final AccessViolator violator = new AccessViolator(ViolatedClass.class);
        final ViolatedClass violated = new ViolatedClass();
        new ExpectedTestFailure(Exception.class) {
            protected void execute() throws Throwable {
                violator.invokeMethod("youCantGetMyException", violated); //$NON-NLS-1$
            }
        }.run();
    }

    public void testSetter() throws Exception {
        AccessViolator violator = new AccessViolator(ViolatedClass.class);
        ViolatedClass violated = new ViolatedClass();
        assertEquals(ViolatedClass.HIDDEN_VALUE, violator.getField("hidden", violated)); //$NON-NLS-1$

        // now set it
        violator.setField("hidden", violated, "violated"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals("violated", violator.getField("hidden", violated)); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testPrimitiveTypeGetter() throws Exception {
        final AccessViolator violator = new AccessViolator(ViolatedClass.class);
        final ViolatedClass violated = new ViolatedClass();

        new ExpectedTestFailure(NoSuchMethodException.class){
			@Override
			protected void execute() throws Throwable {
		        violator.invokeMethod("doSomethingWithTwoBooleans", violated, true, true); //$NON-NLS-1$
			}
        }.run();
        violator.invokeMethod("doSomethingWithTwoBooleans", violated, new Object[] {true, true}, new Class<?>[] {Boolean.TYPE, Boolean.TYPE}); //$NON-NLS-1$
        
    }
}
