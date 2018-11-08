package com.marketcetera.ors.brokers.impl;

import quickfix.SessionID;

import com.marketcetera.fix.FixSession;

/* $License$ */

/**
 * Enables one or more FIX sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionEnabler
        extends AbstractFixSessionAgent
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.impl.AbstractFixSessionAgent#doSessionAction(com.marketcetera.fix.FixSession)
     */
    @Override
    protected void doSessionAction(FixSession inFixSession)
            throws Exception
    {
        getBrokerService().enableSession(new SessionID(inFixSession.getSessionId()));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.impl.AbstractFixSessionAgent#getSessionActionDescription()
     */
    @Override
    protected String getSessionActionDescription()
    {
        return "enable FIX session";
    }
}
