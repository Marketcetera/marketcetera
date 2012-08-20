package org.marketcetera.core;

import org.marketcetera.core.util.except.I18NException;
import org.marketcetera.core.util.log.I18NBoundMessage;
import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */

/**
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id: CoreException.java 16063 2012-01-31 18:21:55Z colin $
 */
@ClassVersion("$Id: CoreException.java 16063 2012-01-31 18:21:55Z colin $")
public class CoreException
    extends I18NException
{
    private static final long serialVersionUID = 3L;
    /**
     * Create a new CoreException instance.
     */
    public CoreException()
    {
        super();
    }
    /**
     * Constructs a new throwable without a message, but with the 
     * given underlying cause.
     *
     * @param nested  The cause.
     */
    public CoreException(Throwable nested)
    {
        super(nested);
    }

    /**
     * Constructs a new throwable with the given message, but without
     * an underlying cause.
     *
     * @param message The message.
     */
    public CoreException(I18NBoundMessage message)
    {
        super(message);
    }

    /**
     * Constructs a new throwable with the given message and
     * underlying cause.
     *
     * @param nested  The cause.
     * @param message The message.
     */
    public CoreException(Throwable nested, I18NBoundMessage message)
    {
        super(nested, message);
    }
}
