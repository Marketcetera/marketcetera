package org.marketcetera.fix.event;

import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Indicates that a FIX session has been stopped.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionStoppedEvent
        extends AbstractFixSessionStatusEvent
        implements FixSessionStoppedEvent
{
    /**
     * Create a new SimpleFixSessionStoppedEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     */
    public SimpleFixSessionStoppedEvent(quickfix.SessionID inSessionId,
                                        BrokerID inBrokerId)
    {
        super(inSessionId,
              inBrokerId,
              FixSessionStatus.STOPPED);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixSessionStoppedEvent [getSessionId()=").append(getSessionId())
                .append(", getBrokerId()=").append(getBrokerId()).append(", getFixSessionStatus()=")
                .append(getFixSessionStatus()).append("]");
        return builder.toString();
    }
}
