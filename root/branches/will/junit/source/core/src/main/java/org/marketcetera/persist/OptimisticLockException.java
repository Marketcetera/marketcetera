package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Thrown when an attempt is made to write a dirty entity to the database.
 * This may happen if someone else has updated the entity after the entity
 * that is being saved was retrieved but before an attempt was made to save
 * it. The remedy is re-retrieve the entity from the database, make the
 * changes again and retry saving it.
 * <p>
 * The determination of whether a write is a dirty write is made by
 * comparing the value of the entity's
 * {@link org.marketcetera.persist.EntityBase#getUpdateCount() update count}
 * with its value in the database. 
 *
 * @see javax.persistence.OptimisticLockException
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class OptimisticLockException extends PersistenceException {
    private static final long serialVersionUID = 3938425027620460968L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public OptimisticLockException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new instance given the supplied message and
     * underlying cause.
     *
     * @param cause the cause
     * @param message the message.         
     */
    public OptimisticLockException(Throwable cause,
                                   I18NBoundMessage message) {
        super(cause, message);
    }
}
