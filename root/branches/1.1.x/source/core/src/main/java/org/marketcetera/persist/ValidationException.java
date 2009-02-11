package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Instances of this class are thrown to indicate failure
 * to validate the entities when saving them. 
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class ValidationException extends PersistenceException {
    private static final long serialVersionUID = 8187793757539265177L;

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param message The message.
     */
    public ValidationException(I18NBoundMessage message) {
        super(message);
    }

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param cause   The cause.
     * @param message The message.
     */
    public ValidationException(Throwable cause, I18NBoundMessage message) {
        super(cause, message);
    }
}
