package org.marketcetera.trade.event;

import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Provides common behavior for {@link IncomingFixMessageEvent} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractIncomingFixMessageEvent
        implements IncomingFixMessageEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.fix.HasSessionId#getSessionId()
     */
    @Override
    public quickfix.SessionID getSessionId()
    {
        return sessionId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasBrokerID#getBrokerID()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.event.HasFIXMessage#getMessage()
     */
    @Override
    public quickfix.Message getMessage()
    {
        return message;
    }
    /**
     * Create a new AbstractIncomingFixMessageEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    protected AbstractIncomingFixMessageEvent(quickfix.SessionID inSessionId,
                                              BrokerID inBrokerId,
                                              quickfix.Message inMessage)
    {
        sessionId = inSessionId;
        brokerId = inBrokerId;
        message = inMessage;
    }
    /**
     * session id value
     */
    private final quickfix.SessionID sessionId;
    /**
     * broker id value
     */
    private final BrokerID brokerId;
    /**
     * message value
     */
    private final quickfix.Message message;
}
