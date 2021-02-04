package org.marketcetera.core.queue;

/**
 * Provides a callback for message packages supplied to {@link FunctionalQueueProcessor}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface MessagePackageCallback
{
    /**
     * Invoked upon successful completion.
     */
    void onComplete();
    /**
     * Invoked if an error occurred instead of {@link #onComplete()}.
     *
     * @param inException an <code>Exception</code> value
     */
    void onError(Exception inException);
}
