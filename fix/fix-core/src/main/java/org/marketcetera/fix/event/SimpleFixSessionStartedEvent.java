package org.marketcetera.fix.event;

import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Indicates that a FIX session has been started.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionStartedEvent
        extends AbstractFixSessionStatusEvent
        implements FixSessionStartedEvent
{
    /**
     * Create a new SimpleFixSessionStartedEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     */
    public SimpleFixSessionStartedEvent(quickfix.SessionID inSessionId,
                                        BrokerID inBrokerId)
    {
        super(inSessionId,
              inBrokerId,
              FixSessionStatus.NOT_CONNECTED); // TODO this might be NOT_CONNECTED or DISCONNECTED - does that matter?
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixSessionStartedEvent [getSessionId()=").append(getSessionId())
                .append(", getBrokerId()=").append(getBrokerId()).append(", getFixSessionStatus()=")
                .append(getFixSessionStatus()).append("]");
        return builder.toString();
    }
}
