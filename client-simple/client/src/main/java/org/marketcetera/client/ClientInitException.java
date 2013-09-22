package org.marketcetera.client;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Instances of this class are thrown if the {@link Client} is already
 * initialized or if it's not initialized.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public class ClientInitException
        extends I18NException
{
    /**
     * Creates an instance.
     *
     * @param message the exception message.
     */
    public ClientInitException(I18NBoundMessage message) {
        super(message);
    }
    /**
     * Create a new ClientInitException instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public ClientInitException(Throwable inCause)
    {
        super(inCause);
    }

    private static final long serialVersionUID = 1L;
}