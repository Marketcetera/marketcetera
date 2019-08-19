package org.marketcetera.trade.event;

import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Indicates that an incoming FIX app message has been received.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SimpleIncomingFixAppMessageEvent
        extends AbstractIncomingFixMessageEvent
        implements IncomingFixAppMessageEvent
{
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("IncomingFixAppMessageEvent [sessionId=").append(getSessionId()).append(", brokerId=")
                .append(getBrokerId()).append(", message=").append(getMessage()).append("]");
        return builder.toString();
    }
    /**
     * Create a new SimpleIncomingFixAppMessageEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    public SimpleIncomingFixAppMessageEvent(quickfix.SessionID inSessionId,
                                            BrokerID inBrokerId,
                                            quickfix.Message inMessage)
    {
        super(inSessionId,
              inBrokerId,
              inMessage);
    }
}
