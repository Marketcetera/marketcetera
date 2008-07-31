package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;

/* $License$ */
/**
 * Thrown when a single entity query doesn't retrieve any results
 *
 * @see javax.persistence.NoResultException
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class NoResultException extends PersistenceException {
    private static final long serialVersionUID = -9161488921585071409L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public NoResultException(Throwable cause) {
        super(cause);
    }
}
