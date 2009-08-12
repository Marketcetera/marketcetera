package org.marketcetera.photon.test;

/**
 * Base class for reusable, {@link IllegalArgumentException} expected failures.
 */
public abstract class ExpectedIllegalArgumentException {

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
        new ExpectedFailure<IllegalArgumentException>(message) {
            @Override
            protected void run() throws Exception {
                ExpectedIllegalArgumentException.this.run();
            }
        };
    }

    /**
     * Subclasses should implement this method to execute code that is expected
     * to fail with an IllegalArgumentException.
     * 
     * @throws Exception
     *             if there's a failure
     */
    protected abstract void run() throws Exception;
}