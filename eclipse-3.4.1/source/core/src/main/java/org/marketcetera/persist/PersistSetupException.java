package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * This exception is thrown to indicate configuration or
 * setup issues.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class PersistSetupException extends PersistenceException {
    private static final long serialVersionUID = 6106632874814142940L;

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param message The message.
     */
    public PersistSetupException(I18NBoundMessage message) {
        super(message);
    }

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param cause   The cause.
     * @param message The message.
     */
    public PersistSetupException(Throwable cause, I18NBoundMessage message) {
        super(cause, message);
    }
}
