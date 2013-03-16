package org.marketcetera.core.security;

import org.marketcetera.core.CoreException;
import org.marketcetera.core.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that a session is invalid.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class UnknownSessionException
        extends CoreException
{
    /**
     * Create a new SessionException instance.
     */
    public UnknownSessionException()
    {
        super();
    }
    /**
     * Create a new SessionException instance.
     *
     * @param inNested a <code>Throwable</code> value
     */
    public UnknownSessionException(Throwable inNested)
    {
        super(inNested);
    }
    /**
     * Create a new SessionException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public UnknownSessionException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new SessionException instance.
     *
     * @param inNested a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public UnknownSessionException(Throwable inNested,
                                   I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    private static final long serialVersionUID = 1L;
}
