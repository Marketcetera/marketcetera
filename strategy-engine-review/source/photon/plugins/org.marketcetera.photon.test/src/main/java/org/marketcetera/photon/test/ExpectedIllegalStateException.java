package org.marketcetera.photon.test;

/**
 * Base class for reusable, {@link IllegalStateException} expected failures.
 */
public abstract class ExpectedIllegalStateException {

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
        new ExpectedFailure<IllegalStateException>(message) {
            @Override
            protected void run() throws Exception {
                ExpectedIllegalStateException.this.run();
            }
        };
    }

    /**
     * Subclasses should implement this method to execute code that is expected
     * to fail with an IllegalStateException.
     * 
     * @throws Exception
     *             if there's a failure
     */
    protected abstract void run() throws Exception;
}