package org.marketcetera.photon.test;

/* $License$ */

/**
 * Base class for reusable, {@link IllegalStateException} expected failures.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: ExpectedIllegalStateException.java 10713 2009-08-30 09:08:28Z
 *          tlerios $
 * @since $Release$
 */
public abstract class ExpectedIllegalStateException extends
        ExpectedFailure<IllegalStateException> {

    /**
     * Constructor.
     * 
     * @param message
     *            the message that should be on the IllegalStateException
     * 
     * @throws Exception
     *             if there was an unexpected failure
     */
    protected ExpectedIllegalStateException(String message) throws Exception {
        super(message);
    }
}