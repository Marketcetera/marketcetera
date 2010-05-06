package org.marketcetera.photon.test;

/* $License$ */

/**
 * Base class for reusable, {@link IllegalArgumentException} expected failures.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id: ExpectedIllegalArgumentException.java 10713 2009-08-30
 *          09:08:28Z tlerios $
 * @since 2.0.0
 */
public abstract class ExpectedIllegalArgumentException extends
        ExpectedFailure<IllegalArgumentException> {

    /**
     * Constructor.
     * 
     * @param message
     *            the message that should be on the IllegalArgumentException
     * 
     * @throws Exception
     *             if there was an unexpected failure
     */
    protected ExpectedIllegalArgumentException(String message) throws Exception {
        super(message);
    }
}