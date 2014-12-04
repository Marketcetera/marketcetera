package org.marketcetera.util.ws.tags;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that a session has expired.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionExpiredException
        extends I18NException
{
    /**
     * Create a new SessionExpiredException instance.
     */
    public SessionExpiredException()
    {
        super();
    }
    /**
     * Create a new SessionExpiredException instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public SessionExpiredException(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Create a new SessionExpiredException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public SessionExpiredException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new SessionExpiredException instance.
     *
     * @param inCause a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public SessionExpiredException(Throwable inCause,
                                   I18NBoundMessage inMessage)
    {
        super(inCause,
              inMessage);
    }
    private static final long serialVersionUID = 7933226364070184570L;
}
