package org.marketcetera.fix.event;

import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Indicates that a FIX sessions has become available.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleFixSessionAvailableEvent
        extends AbstractFixSessionStatusEvent
        implements FixSessionAvailableEvent
{
    /**
     * Create a new SimpleFixSessionAvailableEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    public SimpleFixSessionAvailableEvent(quickfix.SessionID inSessionId,
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
        builder.append("FixSessionAvailableEvent [getSessionId()=").append(getSessionId())
                .append(", getBrokerId()=").append(getBrokerId()).append(", getFixSessionStatus()=")
                .append(getFixSessionStatus()).append("]");
        return builder.toString();
    }
}
