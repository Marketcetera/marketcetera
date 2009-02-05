package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Exception base class for persistence related exceptions.
 * All {@link javax.persistence.PersistenceException} subclasses are mapped
 * to their equivalent {@link org.marketcetera.persist.PersistenceException}
 * subclasses. However only a subset of them get thrown within the Persistence
 * Infrastructure, given how we use JPA. Each of the mapped subclass
 * includes the mapped JPA exception as a nested exception. 
 *
 * Also note that if JPA adds more subclasses in the future, the
 * corresponding {@link org.marketcetera.persist.PersistenceException}
 * subclass mappings may not be available, in which those exceptions
 * will be wrapped within a PersistenceException instance
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class PersistenceException extends I18NException {
    private static final long serialVersionUID = 6786276875783517667L;

    /**
     * Constructs a new throwable without a message, but with the
     * given underlying cause.
     *
     * @param cause The cause.
     */
    public PersistenceException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param message The message.
     */
    public PersistenceException(I18NBoundMessage message) {
        super(message);
    }

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param cause   The cause.
     * @param message The message.
     */
    public PersistenceException(Throwable cause, I18NBoundMessage message) {
        super(cause, message);
    }
}
