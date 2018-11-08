package com.marketcetera.ors.brokers.impl;

import org.marketcetera.quickfix.FIXMessageUtil;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

import quickfix.SessionID;

import com.marketcetera.fix.SessionRestoreArbiter;
import com.marketcetera.ors.brokers.BrokerService;

/* $License$ */

/**
 * Turns session restore on or off for all sessions at once.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class AllOrNothingSessionRestoreArbiter
        implements SessionRestoreArbiter
{
    /* (non-Javadoc)
     * @see com.marketcetera.ors.brokers.SessionRestoreArbiter#shouldRestore(quickfix.SessionID)
     */
    @Override
    public boolean shouldRestore(SessionID inSessionId)
    {
        SLF4JLoggerProxy.debug(FIXMessageUtil.FIX_RESTORE_LOGGER_NAME,
                               "Invoking restore arbiter {} on {}, returning {}",
                               getClass().getSimpleName(),
                               brokerService.getSessionName(inSessionId),
                               isRestoreEnabled);
        return isRestoreEnabled;
    }
    /**
     * Get the enableRestore value.
     *
     * @return a <code>boolean</code> value
     */
    public boolean getEnableRestore()
    {
        return isRestoreEnabled;
    }
    /**
     * Sets the enableRestore value.
     *
     * @param inEnableRestore a <code>boolean</code> value
     */
    public void setEnableRestore(boolean inEnableRestore)
    {
        isRestoreEnabled = inEnableRestore;
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * indicates if restore should be enabled or not
     */
    private boolean isRestoreEnabled = true;
}
