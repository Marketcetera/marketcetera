package org.marketcetera.client;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */
/**
 * Represents errors encountered by the client when communicating with the
 * server.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ConnectionException
        extends I18NException
{
    /**
     * Creates an instance.
     *
     * @param message the exception message.
     */
    public ConnectionException(I18NBoundMessage message) {
        super(message);
    }
    /**
     * Create a new ConnectionException instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public ConnectionException(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Creates an instance.
     *
     * @param cause the underlying cause.
     * @param message the exception message.
     */
    public ConnectionException(Throwable cause, I18NBoundMessage message) {
        super(cause, message);
    }
    private static final long serialVersionUID = 1L;
}
