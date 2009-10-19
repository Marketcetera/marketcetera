package org.marketcetera.event;

import org.marketcetera.core.CoreException;
import org.marketcetera.util.log.I18NBoundMessage;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class EventValidationException
        extends CoreException
{
    static EventValidationException error(I18NBoundMessage inMessage)
    {
        return new EventValidationException(inMessage);
    }
    /**
     * Create a new EventValidationException instance.
     *
     * @param inMessage
     */
    public EventValidationException(I18NBoundMessage inMessage)
    {
        super(inMessage);
    }
    /**
     * Create a new EventValidationException instance.
     *
     * @param inNested
     * @param inMessage
     */
    public EventValidationException(Throwable inNested,
                                    I18NBoundMessage inMessage)
    {
        super(inNested,
              inMessage);
    }
    /**
     * Create a new EventValidationException instance.
     *
     * @param inNested
     */
    public EventValidationException(Throwable inNested)
    {
        super(inNested);
    }
    private static final long serialVersionUID = 1L;
}
