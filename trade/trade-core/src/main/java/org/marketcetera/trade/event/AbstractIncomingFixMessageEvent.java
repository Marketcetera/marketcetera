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
        extends AbstractFixMessageEvent
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
        super(inBrokerId,
              inMessage);
        sessionId = inSessionId;
    }
    /**
     * session id value
     */
    private final quickfix.SessionID sessionId;
}
