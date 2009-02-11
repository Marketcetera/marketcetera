package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Thrown when an attempt is made to save an entity that already exists
 *
 * @see javax.persistence.EntityExistsException
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class EntityExistsException extends PersistenceException {
    private static final long serialVersionUID = 7851476156500095400L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public EntityExistsException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new instance
     *
     * @param cause the cause
     * @param message the error message
     */
    public EntityExistsException(Throwable cause, I18NBoundMessage message) {
        super(cause, message);
    }
}
