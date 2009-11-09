package org.marketcetera.saclient;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Instances of this exception are thrown when errors are encountered
 * when initializing a connection to the remote strategy agent.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public class ConnectionException extends I18NException {
    /**
     * Creates a new instance.
     *
     * @param cause the underlying cause.
     * @param message the i18n message.
     */
    public ConnectionException(Throwable cause, I18NBoundMessage message) {
        super(cause, message);
    }

    /**
     * Creates a new instance.
     *
     * @param message the i18n message.
     */
    public ConnectionException(I18NBoundMessage message) {
        super(message);
    }

    private static final long serialVersionUID = 1L;
}
