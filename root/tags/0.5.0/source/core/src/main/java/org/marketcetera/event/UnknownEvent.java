package org.marketcetera.event;

import quickfix.Message;

/**
 * Represents an event of an unknown type.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public class UnknownEvent
        extends EventBase
{
    /**
     * Create a new UnknownEvent instance.
     *
     * @param inMessageId a <code>long</code> value uniquely identifying this market event
     * @param inTimestamp a <code>long</code> value expressing the time this event occurred in milliseconds
     *  since <code>EPOCH</code> in GMT
     */
    public UnknownEvent(long inMessageId,
                        long inTimestamp)
    {
        super(inMessageId,
              inTimestamp);
    }

    /**
     * Create a new UnknownEvent instance.
     *
     * @param inMessageId a <code>long</code> value uniquely identifying this market event
     * @param inTimestamp a <code>long</code> value expressing the time this event occurred in milliseconds since
     *   EPOCH in GMT
     * @param inFixMessage a <code>Message</code> value encapsulating the underlying market event
     */
    public UnknownEvent(long inMessageId,
                        long inTimestamp,
                        Message inFixMessage)
    {
        super(inMessageId,
              inTimestamp,
              inFixMessage);
    }
}
