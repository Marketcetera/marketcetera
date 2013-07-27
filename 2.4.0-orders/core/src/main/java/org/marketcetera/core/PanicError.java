package org.marketcetera.core;

/**
 * Our top-level error that is thrown when things really break
 * @author Toli Kuznets
 * @version $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class PanicError extends Error
{
    public PanicError(String message)
    {
        super(message);
    }

    public PanicError(Throwable cause)
    {
        super(cause);
    }

    public PanicError(String message, Throwable cause)
    {
        super(message, cause);
    }
}
