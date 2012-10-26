package org.marketcetera.core.trade;

import org.marketcetera.core.util.except.I18NException;
import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Indicates an error when creating a message instance. Instances of this
 * exception are typically thrown when a FIX Message cannot be wrapped
 * by the factory into a message that the system is capable of dealing with.
 *
 * @version $Id$
 * @since 1.0.0
 */
public class MessageCreationException extends I18NException {
    /**
     * Creates a new instance.
     *
     * @param message the message.
     */
    public MessageCreationException(I18NBoundMessage message) {
        super(message);
    }

    /**
     * Creates a new instance.
     *
     * @param cause the cause for this exception.
     * @param message the message.
     */
    public MessageCreationException(Throwable cause,
                                    I18NBoundMessage message) {
        super(cause, message);
    }

    private static final long serialVersionUID = 1L;

}
