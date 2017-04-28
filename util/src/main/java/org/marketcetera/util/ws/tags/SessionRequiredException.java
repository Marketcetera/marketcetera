package org.marketcetera.util.ws.tags;

import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 * Indicates that a session is required.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionRequiredException
        extends I18NException
{
    /**
     * Create a new SessionRequiredException instance.
     */
    public SessionRequiredException()
    {
        super();
    }
    /**
     * Create a new SessionRequiredException instance.
     *
     * @param inCause a <code>Throwable</code> value
     */
    public SessionRequiredException(Throwable inCause)
    {
        super(inCause);
    }
    /**
     * Create a new SessionRequiredException instance.
     *
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public SessionRequiredException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new SessionRequiredException instance.
     *
     * @param inCause a <code>Throwable</code> value
     * @param inMessage an <code>I18NBoundMessage</code> value
     */
    public SessionRequiredException(Throwable inCause,
                                   I18NBoundMessage inMessage)
    {
        super(inCause,
              inMessage);
    }
    private static final long serialVersionUID = 5457267019359887545L;
}
