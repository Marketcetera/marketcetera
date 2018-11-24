package org.marketcetera.trade.event;

import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Indicates that an incoming FIX message has been received.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleIncomingFixAdminMessageEvent
        extends AbstractIncomingFixMessageEvent
        implements IncomingFixAdminMessageEvent
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("IncomingFixAdminMessageEvent [sessionId=").append(getSessionId()).append(", brokerId=")
                .append(getBrokerId()).append(", message=").append(getMessage()).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleIncomingFixAdminMessageEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    public SimpleIncomingFixAdminMessageEvent(quickfix.SessionID inSessionId,
                                              BrokerID inBrokerId,
                                              quickfix.Message inMessage)
    {
        super(inSessionId,
              inBrokerId,
              inMessage);
    }
}
