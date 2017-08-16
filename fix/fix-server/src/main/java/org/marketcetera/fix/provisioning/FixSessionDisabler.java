package org.marketcetera.fix.provisioning;

import org.marketcetera.fix.FixSession;

import quickfix.SessionID;

/* $License$ */

/**
 * Disables one or more FIX sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionDisabler
        extends AbstractFixSessionAgent
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.impl.AbstractFixSessionAgent#doSessionAction(com.marketcetera.fix.FixSession)
     */
    @Override
    protected void doSessionAction(FixSession inFixSession)
            throws Exception
    {
        getBrokerService().disableSession(new SessionID(inFixSession.getSessionId()));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.impl.AbstractFixSessionAgent#getSessionActionDescription()
     */
    @Override
    protected String getSessionActionDescription()
    {
        return "disable FIX session";
    }
}
