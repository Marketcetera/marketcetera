package org.marketcetera.trade.event;

import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Provides common behavior for {@link FixMessageEvent} implementations.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractFixMessageEvent
        implements FixMessageEvent
{
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
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inMessage a <code>quickfix.Message</code> value
     */
    protected AbstractFixMessageEvent(BrokerID inBrokerId,
                                      quickfix.Message inMessage)
    {
        brokerId = inBrokerId;
        message = inMessage;
    }
    /**
     * broker id value
     */
    private final BrokerID brokerId;
    /**
     * message value
     */
    private final quickfix.Message message;
}
