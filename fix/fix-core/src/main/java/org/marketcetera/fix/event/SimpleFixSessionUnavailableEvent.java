package org.marketcetera.fix.event;

import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Indicates that a FIX sessions has become unavailable.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionUnavailableEvent
        extends AbstractFixSessionStatusEvent
        implements FixSessionAvailableEvent
{
    /**
     * Create a new SimpleFixSessionUnavailableEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    public SimpleFixSessionUnavailableEvent(quickfix.SessionID inSessionId,
                                            BrokerID inBrokerId,
                                            FixSessionStatus inStatus)
    {
        super(inSessionId,
              inBrokerId,
              inStatus);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FixSessionUnavailableEvent [getSessionId()=").append(getSessionId())
                .append(", getBrokerId()=").append(getBrokerId()).append(", getFixSessionStatus()=")
                .append(getFixSessionStatus()).append("]");
        return builder.toString();
    }
}
