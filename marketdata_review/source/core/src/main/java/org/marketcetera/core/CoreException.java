package org.marketcetera.core;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * @author klim@marketcetera.com
 * @since 0.6.0
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class CoreException
    extends I18NException
{
    private static final long serialVersionUID = 2L;

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
