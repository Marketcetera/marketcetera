package org.marketcetera.fix.event;

import org.marketcetera.fix.FixSessionStatus;
import org.marketcetera.trade.BrokerID;

/* $License$ */

/**
 * Provides common behavior for {@link FixSessionStatusEvent} implementors.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractFixSessionStatusEvent
        implements FixSessionStatusEvent
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasBrokerID#getBrokerId()
     */
    @Override
    public BrokerID getBrokerId()
    {
        return brokerId;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.HasFixSessionStatus#getFixSessionStatus()
     */
    @Override
    public FixSessionStatus getFixSessionStatus()
    {
        return status;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.fix.HasSessionId#getSessionId()
     */
    @Override
    public quickfix.SessionID getSessionId()
    {
        return sessionId;
    }
    /**
     * Create a new AbstractFixSessionStatusEvent instance.
     *
     * @param inSessionId a <code>quickfix.SessionID</code> value
     * @param inBrokerId a <code>BrokerID</code> value
     * @param inStatus a <code>FixSessionStatus</code> value
     */
    protected AbstractFixSessionStatusEvent(quickfix.SessionID inSessionId,
                                            BrokerID inBrokerId,
                                            FixSessionStatus inStatus)
    {
        sessionId = inSessionId;
        brokerId = inBrokerId;
        status = inStatus;
    }
    /**
     * session id value
     */
    private final quickfix.SessionID sessionId;
    /**
     * status value
     */
    private final FixSessionStatus status;
    /**
     * broker id value
     */
    private final BrokerID brokerId;
}
