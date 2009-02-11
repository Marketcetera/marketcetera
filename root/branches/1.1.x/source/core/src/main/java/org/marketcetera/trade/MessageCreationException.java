package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Indicates an error when creating a message instance. Instances of this
 * exception are typically thrown when a FIX Message cannot be wrapped
 * by the factory into a message that the system is capable of dealing with.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
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
