package org.marketcetera.photon.commons;

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
     * Safely casts a Throwable to RuntimeException. Use this when you know a
     * Throwable cannot be a checked exception.
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

    private ExceptionUtils() {
        throw new AssertionError("non-instantiable"); //$NON-NLS-1$
    }
}
