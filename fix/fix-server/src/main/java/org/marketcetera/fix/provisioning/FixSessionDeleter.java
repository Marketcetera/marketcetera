package org.marketcetera.fix.provisioning;

import org.marketcetera.fix.FixSession;

import quickfix.SessionID;

/* $License$ */

/**
 * Deletes one or more FIX sessions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionDeleter
        extends AbstractFixSessionAgent
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.impl.AbstractFixSessionAgent#doSessionAction(com.marketcetera.fix.FixSession)
     */
    @Override
    protected void doSessionAction(FixSession inFixSession)
            throws Exception
    {
        getBrokerService().delete(new SessionID(inFixSession.getSessionId()));
    }
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.impl.AbstractFixSessionAgent#getSessionActionDescription()
     */
    @Override
    protected String getSessionActionDescription()
    {
        return "delete FIX session";
    }
}
