package org.marketcetera.event;

/* $License$ */

/**
 * Represents the cancellation of an order.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since $Release$
 */
public class CancelEvent
        extends EventBase
{
    /**
     * Create a new CancelEvent instance.
     *
     * @param inMessageId
     * @param inTimestamp
     */
    public CancelEvent(long inMessageId,
                       long inTimestamp)
    {
        super(inMessageId,
              inTimestamp);
    }
}
