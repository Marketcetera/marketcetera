package org.marketcetera.photon.commons;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Utilities for working with exceptions.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ExceptionUtils {

    /**
     * Safely casts a Throwable to RuntimeException.
     * 
     * @param throwable
     *            the throwable to launder
     * @return the throwable if it is a runtime exception
     * @throws IllegalArgumentException
     *             if throwable is null
     * @throws Error
     *             if t is an error
     * @throws IllegalStateException
     *             if t is a checked exception
     */
    public static RuntimeException launderThrowable(Throwable throwable) {
        Validate.notNull(throwable, "throwable"); //$NON-NLS-1$
        if (throwable instanceof RuntimeException)
            return (RuntimeException) throwable;
        else if (throwable instanceof Error)
            throw (Error) throwable;
        else
            throw new IllegalStateException("Not unchecked", throwable); //$NON-NLS-1$
    }

    /**
     * Gets the result of a future, laundering any resulting
     * {@link ExecutionException}.
     * 
     * @param future
     *            the future to await
     * @return the result of future.get()
     * @throws CancellationException
     *             if the computation was canceled
     * @throws InterruptedException
     *             if the current thread was interrupted while waiting
     * @throws IllegalStateException
     *             if the future task throws a checked exception
     */
    public static <T> T launderedGet(Future<T> future)
            throws InterruptedException {
        try {
            return future.get();
        } catch (ExecutionException e) {
            throw launderThrowable(e.getCause());
        }
    }

    private ExceptionUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
